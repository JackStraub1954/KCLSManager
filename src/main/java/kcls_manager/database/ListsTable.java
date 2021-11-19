package kcls_manager.database;

import static kcls_manager.database.DBConstants.CREATION_DATE_FIELD;
import static kcls_manager.database.DBConstants.LABEL_FIELD;
import static kcls_manager.database.DBConstants.LISTS_ID_FIELD;
import static kcls_manager.database.DBConstants.LISTS_TABLE_NAME;
import static kcls_manager.database.DBConstants.LIST_TITLE_FIELD;
import static kcls_manager.database.DBConstants.LIST_TYPE_FIELD;
import static kcls_manager.database.DBConstants.MODIFICATION_DATE_FIELD;
import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.TITLE_TYPE;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.logging.Logger;

import kcls_manager.main.KCLSException;
import kcls_manager.main.KCLSList;

/**
 * Provides database support for accessing the LISTS table.
 * This class should only be used inside the database package.
 * Public access to the functionality contained here
 * is provided via the DBServer class.
 * 
 * @author jstra
 * @see DBServer
 */
public class ListsTable extends Table
{
    private static final String loggerName  = ListsTable.class.getName();
    private static final Logger logger      = Logger.getLogger( loggerName );
    
    /** Inserts a single list into the LISTS table */
    private final String    insertListSQL   = 
        "INSERT INTO " + LISTS_TABLE_NAME + "("
        + LIST_TITLE_FIELD + ", "
        + LABEL_FIELD + ", "
        + LIST_TYPE_FIELD + ", "
        + CREATION_DATE_FIELD + ", "
        + MODIFICATION_DATE_FIELD 
    + " )"
    + "VALUES ( ?, ?, ?, ?, ? )";
    private final PreparedStatement insertListPStatement;

    /** Updates an existing list in the LISTS table */
    private static final String    updateListSQL   = 
        "UPDATE " + LISTS_TABLE_NAME + " SET "
        + LIST_TITLE_FIELD + " = ?, "
        + LABEL_FIELD + " = ?, "
        + LIST_TYPE_FIELD + " = ?, "
        + CREATION_DATE_FIELD + " = ?, "
        + MODIFICATION_DATE_FIELD  + " = ? "
    + "WHERE " + LISTS_ID_FIELD + " = ?";
    private final PreparedStatement updateListPStatement;
    
    /** Deletes a list from the LISTS table */
    private static final String    deleteListSQL   = 
        "DELETE FROM " + LISTS_TABLE_NAME
        + " WHERE " + LISTS_ID_FIELD + " = ?";
    private final PreparedStatement deleteListPStatement;

    /** Gets all lists from the list table */
    private final String    getAllListsSQL  =
        "SELECT * FROM " + LISTS_TABLE_NAME;
    private final PreparedStatement getAllListsPStatement;
    
    /** Gets all lists of type TITLE */
    private final String    getTitleListsSQL  =
        "SELECT * FROM " + LISTS_TABLE_NAME
        + " WHERE " + LIST_TYPE_FIELD + " = " + TITLE_TYPE;
    private final PreparedStatement getTitleListsPStatement;
    
    /** Gets all lists of type AUTHOR */
    private final String    getAuthorListsSQL  =
        "SELECT * FROM " + LISTS_TABLE_NAME
        + " WHERE " + LIST_TYPE_FIELD + " = " + AUTHOR_TYPE;
    private final PreparedStatement getAuthorListsPStatement;
    
    /** Gets list ID for list name */
    private final String    getListIDForNameSQL  =
        "SELECT " + LISTS_ID_FIELD + " FROM " + LISTS_TABLE_NAME
        + " WHERE " + LIST_TITLE_FIELD + " = " + "?";
    private final PreparedStatement getListIDForNamePStatement;
    
    /** Gets list given a list ID */
    private final String    getListSQL  =
        "SELECT *" + " FROM " + LISTS_TABLE_NAME
        + " WHERE " + LISTS_ID_FIELD + " = " + "?";
    private final PreparedStatement getListPStatement;

    public ListsTable( DBServer server )
    {
        final int genKeys   = Statement.RETURN_GENERATED_KEYS;
        final int noGenKeys = Statement.NO_GENERATED_KEYS;
        
        insertListPStatement = 
            server.getPreparedStatement( insertListSQL, genKeys );
        updateListPStatement =
            server.getPreparedStatement( updateListSQL, genKeys );
        deleteListPStatement =
            server.getPreparedStatement( deleteListSQL, genKeys );
        getAllListsPStatement = 
            server.getPreparedStatement( getAllListsSQL, noGenKeys );
        getTitleListsPStatement =
            server.getPreparedStatement( getTitleListsSQL, noGenKeys );
        getAuthorListsPStatement =
            server.getPreparedStatement( getAuthorListsSQL, noGenKeys );
        getListIDForNamePStatement =
            server.getPreparedStatement( getListIDForNameSQL, noGenKeys );
        getListPStatement =
            server.getPreparedStatement( getListSQL, noGenKeys );
    }

    /**
     * Add a new record to the List table. 
     *
     * @param list  Object representing list to be added
     * 
     * @throws  SQLException if a SQL error occurs
     */
    public void insertList( KCLSList list ) throws SQLException
    {
        String      dialogTitle         = list.getDialogTitle();
        String      componentLabel      = list.getComponentLabel();
        int         listType            = list.getListType();
        LocalDate   creationDate        = list.getCreationDate();
        LocalDate   modificationDate    = list.getModifyDate();
        
        java.sql.Date   creDate     = java.sql.Date.valueOf( creationDate );
        java.sql.Date   modDate     = 
        java.sql.Date.valueOf( modificationDate );
    
        int inx = 1;
        insertListPStatement.setString( inx++, dialogTitle );
        insertListPStatement.setString( inx++, componentLabel );
        insertListPStatement.setInt( inx++, listType );
        insertListPStatement.setDate( inx++, creDate );
        insertListPStatement.setDate( inx++, modDate );
        
        logger.info( "inserting list: " + dialogTitle );
        insertListPStatement.execute();
        logger.info("list insert complete" );
        
        int ident   = 0;
        try ( ResultSet rSet = insertListPStatement.getGeneratedKeys() )
        {
            if ( !rSet.next() )
            {
                String  message = "Insert list: generated key not returned";
                logger.severe( message );
                throw new KCLSException( message );
            }
            ident   = rSet.getInt( 1 );
        }
        list.setIdent( ident );
        logger.info( "list: " + dialogTitle + " inserted" );
    }
    
    /**
     * Updates an existing list in the LISTS table.
     * The list record must have previously been read from the table.
     * 
     * @param list  the list to update
     */
    public void updateList( KCLSList list ) throws SQLException
    {
        OptionalInt optionalIdent       = list.getIdent();
        if ( optionalIdent.isEmpty() )
        {
            String  message = 
                "Expected ID for list: "
                + list.getDialogTitle()
                + " not found";
            throw new KCLSException( message );
        }
        
        int         listID              = optionalIdent.getAsInt();
        String      dialogTitle         = list.getDialogTitle();
        String      componentLabel      = list.getComponentLabel();
        int         listType            = list.getListType();
        LocalDate   creationDate        = list.getCreationDate();
        LocalDate   modificationDate    = list.getModifyDate();
        
        java.sql.Date   creDate     = java.sql.Date.valueOf( creationDate );
        java.sql.Date   modDate     = 
        java.sql.Date.valueOf( modificationDate );
        
        int inx = 1;
        updateListPStatement.setString( inx++, dialogTitle );
        updateListPStatement.setString( inx++, componentLabel );
        updateListPStatement.setInt( inx++, listType );
        updateListPStatement.setDate( inx++, creDate );
        updateListPStatement.setDate( inx++, modDate );
        updateListPStatement.setInt( inx++, listID );
        
        logger.info( "updating list: " + dialogTitle );
        updateListPStatement.execute();
        logger.info("list update complete" );
    }
    
    /**
     * Delete a given list from the LISTS table.
     * The list record must previously have been read for the table.
     * 
     * @param list  the given list
     */
    public void deleteList( KCLSList list ) throws SQLException
    {
        OptionalInt optionalIdent       = list.getIdent();
        if ( optionalIdent.isEmpty() )
        {
            String  message = 
                "Expected ID for list: "
                + list.getDialogTitle()
                + " not found";
            throw new KCLSException( message );
        }
        
        int         listID              = optionalIdent.getAsInt();
        deleteList( listID );
    }
    
    /**
     * Delete a given list from the LISTS table.
     * 
     * @param ident     the ID of the given list
     */
    public void deleteList( int ident ) throws SQLException
    {
        deleteListPStatement.setInt( 1, ident);
        logger.info( "deleting list: " + ident );
        deleteListPStatement.executeUpdate();
        logger.info( "list deleted" );
    }
    
    public KCLSList getList( int ident ) throws SQLException
    {
        getListPStatement.setInt( 1, ident);
        KCLSList    list    = null;
        try ( ResultSet   rSet    = getListPStatement.executeQuery() )
        {
            if ( rSet.next() )
                list = cvtRowToList( rSet );
            }
        return list;
    }
    
    public List<KCLSList> getAllLists() throws SQLException
    {
        List<KCLSList>  allLists    = new ArrayList<>();
        try ( ResultSet rSet = getAllListsPStatement.executeQuery() )
        {
            while ( rSet.next() )
            {
                allLists.add( cvtRowToList( rSet ) );
            }
        }
        
        return allLists;
    }
    
    public List<KCLSList> getTitleLists() throws SQLException
    {
        List<KCLSList>  allLists    = new ArrayList<>();
        try ( ResultSet rSet = getTitleListsPStatement.executeQuery() )
        {
            while ( rSet.next() )
            {
                allLists.add( cvtRowToList( rSet ) );
            }
        }
        
        return allLists;
    }
    
    public List<KCLSList> getAuthorLists() throws SQLException
    {
        List<KCLSList>  allLists    = new ArrayList<>();
        try ( ResultSet rSet = getAuthorListsPStatement.executeQuery() )
        {
            while ( rSet.next() )
            {
                allLists.add( cvtRowToList( rSet ) );
            }
        }
        
        return allLists;
    }
    
    public int getListID( String listName ) throws SQLException
    {
        int         ident   = -1;
        getListIDForNamePStatement.setString( 1, listName );
        try ( ResultSet   rSet    = getListIDForNamePStatement.executeQuery() )
        {
            if ( rSet.next() )
            {
                ident = rSet.getInt( 1 );
            }
        }
        
        return ident;
    }
    
    private KCLSList cvtRowToList( ResultSet rSet ) throws SQLException
    {
        int     listID      = rSet.getInt( LISTS_ID_FIELD );
        String  dialogTitle = rSet.getString( LIST_TITLE_FIELD );
        String  label       = rSet.getString( LABEL_FIELD );
        int     listType    = rSet.getInt( LIST_TYPE_FIELD );
        Date    sqlCreDate  = rSet.getDate( CREATION_DATE_FIELD );
        Date    sqlModDate  = rSet.getDate( MODIFICATION_DATE_FIELD );
        
        LocalDate       creDate         = sqlCreDate.toLocalDate();
        LocalDate       modDate         = sqlModDate.toLocalDate();
        
        KCLSList    list    = new KCLSList( listType, dialogTitle );
        list.setIdent( listID );
        list.setDialogTitle( dialogTitle );
        list.setComponentLabel( label );
        list.setCreationDate( creDate );
        list.setModifyDate( modDate );
        return list;
    }
}
