package kcls_manager.database;

import static kcls_manager.database.DBConstants.AUTHORS_ID_FIELD;
import static kcls_manager.database.DBConstants.CHECK_DATE_FIELD;
import static kcls_manager.database.DBConstants.CHECK_QPOS_FIELD;
import static kcls_manager.database.DBConstants.CREATION_DATE_FIELD;
import static kcls_manager.database.DBConstants.LISTS_ID_FIELD;
import static kcls_manager.database.DBConstants.MODIFICATION_DATE_FIELD;
import static kcls_manager.database.DBConstants.RANK_FIELD;
import static kcls_manager.database.DBConstants.RATING_FIELD;
import static kcls_manager.database.DBConstants.RECKON_DATE_FIELD;
import static kcls_manager.database.DBConstants.RECKON_QPOS_FIELD;
import static kcls_manager.database.DBConstants.SOURCE_FIELD;
import static kcls_manager.database.DBConstants.TITLES_ID_FIELD;
import static kcls_manager.database.DBConstants.TITLES_TABLE_NAME;
import static kcls_manager.database.DBConstants.TITLE_FIELD;
import static kcls_manager.database.DBConstants.MEDIA_TYPE_FIELD;

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

import kcls_manager.main.Author;
import kcls_manager.main.KCLSException;
import kcls_manager.main.KCLSList;
import kcls_manager.main.Title;

/**
 * Provides database support for accessing the LISTS table. This class
 * should only be used inside the database package.
 * @author jstra
 *
 */
public class TitlesTable extends Table
{
    private static final String loggerName  = TitlesTable.class.getName();
    private static final Logger logger      = Logger.getLogger( loggerName );
    
    /** Inserts a single title into the TITLES table */
    private final String    insertTitleSQL   = 
        "INSERT INTO " + TITLES_TABLE_NAME + "("
        + TITLE_FIELD + ", "
        + AUTHORS_ID_FIELD + ", "
        + LISTS_ID_FIELD + ", "
        + MEDIA_TYPE_FIELD + ", "
        + CHECK_QPOS_FIELD + ", "
        + RECKON_QPOS_FIELD + ", "
        + RANK_FIELD + ", "
        + RATING_FIELD + ", "
        + SOURCE_FIELD + ", "
        + CREATION_DATE_FIELD + ", "
        + MODIFICATION_DATE_FIELD + ", "
        + RECKON_DATE_FIELD + ", "
        + CHECK_DATE_FIELD
    + " )"
    + "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
    private final PreparedStatement insertTitlePStatement;
    
    /** Updates an existing title in the TITLES table */
    private final String    updateTitleSQL   = 
      "UPDATE " + TITLES_TABLE_NAME + " SET "
        + TITLE_FIELD + " = ?, "
        + AUTHORS_ID_FIELD + " = ?, "
        + LISTS_ID_FIELD + " = ?, "
        + MEDIA_TYPE_FIELD + " = ?, "
        + CHECK_QPOS_FIELD + " = ?, "
        + RECKON_QPOS_FIELD + " = ?, "
        + RANK_FIELD + " = ?, "
        + RATING_FIELD + " = ?, "
        + SOURCE_FIELD + " = ?, "
        + CREATION_DATE_FIELD + " = ?, "
        + MODIFICATION_DATE_FIELD + " = ?, "
        + RECKON_DATE_FIELD + " = ?, "
        + CHECK_DATE_FIELD + " = ? "
      + "WHERE " + TITLES_ID_FIELD + " = ? ";
    private final PreparedStatement updateTitlePStatement;
    
    /** Deletes a title from the TITLES table */
    private static final String    deleteTitleSQL   = 
        "DELETE FROM " + TITLES_TABLE_NAME
        + " WHERE " + TITLES_ID_FIELD + " = ?";
    private final PreparedStatement deleteTitlePStatement;
    
    /** Gets a title from the TITLES table given a row ID */
    private final String    getTitleSQL  =
        "SELECT * FROM " + TITLES_TABLE_NAME
        + " WHERE " + TITLES_ID_FIELD  + " = ?";
    private final PreparedStatement getTitlePStatement;
    
    /** Gets all titles from the TITLES table */
    private final String    getAllTitlesSQL  =
        "SELECT * FROM " + TITLES_TABLE_NAME;
    private final PreparedStatement getAllTitlesPStatement;
    
    /** Gets all titles for a specific list */
    private final String    getTitleListSQL  =
        "SELECT * FROM " + TITLES_TABLE_NAME
        + " WHERE " + LISTS_ID_FIELD + " = ?";
    private final PreparedStatement getTitleListPStatement;
    
    /** Gets all titles for a specific author */
    private final String    getAuthorTitlesSQL  =
        "SELECT * FROM " + TITLES_TABLE_NAME
        + " WHERE " + AUTHORS_ID_FIELD + " = ?";
    private final PreparedStatement getAuthorTitlesPStatement;
    
    private final DBServer  dbServer;

    public TitlesTable( DBServer server )
    {
        final int genKeys   = Statement.RETURN_GENERATED_KEYS;
        final int noGenKeys = Statement.NO_GENERATED_KEYS;
        
        dbServer = server;
        
        insertTitlePStatement = 
            server.getPreparedStatement( insertTitleSQL, genKeys );
        getTitlePStatement = 
            server.getPreparedStatement( getTitleSQL, noGenKeys );
        getAllTitlesPStatement = 
            server.getPreparedStatement( getAllTitlesSQL, noGenKeys );
        getTitleListPStatement =
            server.getPreparedStatement( getTitleListSQL, noGenKeys );
        getAuthorTitlesPStatement =
            server.getPreparedStatement( getAuthorTitlesSQL, noGenKeys );
        updateTitlePStatement =
            server.getPreparedStatement( updateTitleSQL, noGenKeys );
        deleteTitlePStatement =
            server.getPreparedStatement( deleteTitleSQL, noGenKeys );
    }

    /**
     * Add a new record to the TITLES table. 
     *
     * @param title Object representing title to be added
     * 
     * @throws  SQLException if a SQL error occurs
     */
    public void insertTitle( Title title ) throws SQLException
    {
        cvtTitleToRow( title, insertTitlePStatement );

        String  name    = title.getTitle();
        logger.info( "inserting title: " + name );
        insertTitlePStatement.executeUpdate();
        ResultSet   rSet    = insertTitlePStatement.getGeneratedKeys();
        if ( !rSet.next() )
        {
            String  message = "Insert title: generated key not returned";
            logger.severe( message );
            throw new KCLSException( message );
        }
        int     ident   = rSet.getInt( 1 );
        title.setIdent( ident );
        dbServer.insertCommentsFor( title );
        logger.info( "title: " + name + " inserted" );
    }
    
    /**
     * Updates an existing title in the TITLES table.
     * The title must first have been queried.
     * 
     * @param title the title to update
     * @throws SQLException if a SQL error occurs
     * @throws KCLSException if title to update has no row ID
     */
    public void updateTitle( Title title ) 
        throws SQLException, KCLSException
    {
        String  name    = title.getTitle();
        OptionalInt optIdent    = title.getIdent();
        if ( optIdent.isEmpty() )
        {
            String  message = 
                "Expected ID not found for title: " + name;
            logger.severe(message);
            throw new KCLSException( message );
        }
        
        int     ident   = optIdent.getAsInt();
        int     count   = cvtTitleToRow( title, updateTitlePStatement );
        updateTitlePStatement.setInt( count, ident );
        
        // update most title data
        logger.info( "updating title: " + name );
        updateTitlePStatement.executeUpdate();
        
        // update title comments
        dbServer.synchronizeCommentsFor( title );
        logger.info( "title: " + name + " updated" );
    }
    
    /**
     * Delete a row from the TITLES table.
     * The row must have been previously read from table.
     * 
     * @param title    the title to delete
     * @throws SQLException if a SQL error occurs
     * @throws KCLSException if title to delete has no row ID
     */
    public void deleteTitle( Title title ) throws SQLException
    {
        String      name        = title.getTitle();
        OptionalInt optIdent    = title.getIdent();
        if ( optIdent.isEmpty() )
        {
            String  message = 
                "Expected ID not found for title: " + name;
            logger.severe(message);
            throw new KCLSException( message );
        }
        
        int     ident   = optIdent.getAsInt();
        dbServer.deleteCommentsFor( title );
        logger.info( "deleted comments for: " + title );
        deleteTitlePStatement.setInt( 1, ident );
        deleteTitlePStatement.executeUpdate();
        logger.info( "deleted title: " + title );
    }
    
    /**
     * Query the title associated with a given row ID.
     * 
     * @param ident the given row ID
     * 
     * @return  Title object representing queried row, or null if not found
     * 
     * @throws SQLException if a SQL error occurs
     */
    public Title getTitle( int ident ) throws SQLException
    {
        getTitlePStatement.setInt( 1, ident );
        ResultSet   rSet        = getTitlePStatement.executeQuery();
        Title       title       = null;
        if ( rSet.next() )
        {
            title = cvtRowToTitle( rSet );
            dbServer.getCommentsFor(title);
        }
        
        return title;
    }
    
    /**
     * Returns a list of all titles for a given author.
     * If the given author has an AUTHORS table ID, that
     * ID will be used for the query, 
     * otherwise an attempt will be made to find the ID
     * use the author's name.
     * If the AUTHORS table ID cannot be resolved
     * a KCLSException will be thrown.
     * 
     * @param author    the given author
     * 
     * @return a list of all titles for the given author
     * 
     * @throws SQLException if a SQL error occurs
     * @throws KCLSException if the given author is not in the database
     */
    public List<Title> getTitlesForAuthor( Author author )
        throws SQLException, KCLSException
    {
        String      name        = author.getAuthor();
        OptionalInt optIdent    = author.getIdent();
        int         ident       = -1;
        if ( optIdent.isPresent() )
            ident = optIdent.getAsInt();
        else
            ident = dbServer.getAuthorIDForName( name );
        if ( ident < 1 )
        {
            String  message = "Author not found: " + name;
            logger.severe( message );
            throw new KCLSException( message );
        }
        getAuthorTitlesPStatement.setInt( 1, ident );
        List<Title> titles  = new ArrayList<>();
        ResultSet   rSet    = getAuthorTitlesPStatement.executeQuery();
        while ( rSet.next() )
            titles.add( cvtRowToTitle( rSet ) );
        
        return titles;
    }

    /**
     * Get all rows from the TITLES table.
     * 
     * @return a list of all rows from the TITLES table
     * 
     * @throws SQLException if a SQL error occurs
     */
    public List<Title> getAllTitles() throws SQLException
    {
        ResultSet   rSet        = getAllTitlesPStatement.executeQuery();
        List<Title> allTitles   = new ArrayList<>();
        while ( rSet.next() )
        {
            Title   title   = cvtRowToTitle( rSet );
            dbServer.getCommentsFor( title );
            allTitles.add( title );
        }
        
        return allTitles;
    }
    
    /**
     * Get all titles associated with a given list.
     * 
     * @param listName  the name of the given list
     * 
     * @return  a list of all titles associated with the given list
     * 
     * @throws SQLException if a SQL exception occurs
     */
    public List<Title> getTitleList( String listName ) throws SQLException
    {
        List<Title> allTitles   = new ArrayList<>();
        int         listID      = dbServer.getListID( listName ); 
        if ( listID > 0 )
        {
            getTitleListPStatement.setInt( 1, listID );
            ResultSet   rSet        = getTitleListPStatement.executeQuery();
            while ( rSet.next() )
            {
                Title   title   = cvtRowToTitle( rSet );
                dbServer.getCommentsFor( title );
                allTitles.add( title );
            }
        }
        return allTitles;
    }
    
    private Title cvtRowToTitle( ResultSet rSet ) throws SQLException
    {
        int         titleID     = rSet.getInt( TITLES_ID_FIELD );
        String      text        = rSet.getString( TITLE_FIELD);
        int         authorID    = rSet.getInt( AUTHORS_ID_FIELD );
        int         listID      = rSet.getInt( LISTS_ID_FIELD );
        String      mediaType   = rSet.getString( MEDIA_TYPE_FIELD );
        int         checkQPoS   = rSet.getInt( CHECK_QPOS_FIELD );
        int         reckonQPos  = rSet.getInt( RECKON_QPOS_FIELD );
        int         rank        = rSet.getInt( RANK_FIELD );
        int         rating      = rSet.getInt( RATING_FIELD );
        String      source      = rSet.getString( SOURCE_FIELD );
        Date        sqlCreDate  = rSet.getDate( CREATION_DATE_FIELD );
        Date        sqlModDate  = rSet.getDate( MODIFICATION_DATE_FIELD );
        Date        sqlRecDate  = rSet.getDate( RECKON_DATE_FIELD );
        Date        sqlCheDate  = rSet.getDate( CHECK_DATE_FIELD );
        
        LocalDate   creDate     = sqlCreDate.toLocalDate();
        LocalDate   modDate     = sqlModDate.toLocalDate();
        LocalDate   cheDate     = sqlCheDate.toLocalDate();
        LocalDate   recDate     = sqlRecDate.toLocalDate();
        
        Author      author      = dbServer.getAuthor( authorID );
        KCLSList    list        = dbServer.getList( listID );
        if ( list == null )
        {
            String  message = "No list reference found for title: " + text;
            logger.severe( message );
            throw new KCLSException( message );
        }
        String      authorName  = "";
        if ( author != null )
            authorName = author.getAuthor();
        String      listName    = list.getDialogTitle();
        
        Title   title   = new Title( creDate, text, authorName );
        title.setIdent( titleID );
        // title.setTitle
        // title.setAuthor
        title.setListName( listName );
        title.setMediaType( mediaType );
        title.setCheckQPos( checkQPoS );
        title.setReckonQPos( reckonQPos);
        title.setRank( rank );
        title.setRating( rating );
        title.setSource( source );
        // title.setCreationDate
        title.setModifyDate( modDate );
        title.setReckonDate( recDate );
        title.setCheckDate( cheDate );
        return title;
    }
    
    /**
     * Uses populates a prepared statement with data from a given title.
     * The total number of fields set in the statement is returned.
     * 
     * @param title     the given title
     * @param statement the prepared statement to populate
     * @return the total number of fields set in the statement
     *
     * @throws SQLException if a SQL error occurs
     * @throws KCLSException if the author has not be assigned to a list
     */
    private int cvtTitleToRow( Title title, PreparedStatement statement )
        throws SQLException, KCLSException
    {
        String      name                = title.getTitle();
        String      author              = title.getAuthor();
        String      list                = title.getListName();
        String      mediaType           = title.getMediaType();
        int         checkQPos           = title.getCheckQPos();
        int         reckonQPos          = title.getReckonQPos();
        int         rank                = title.getRank();
        int         rating              = title.getRating();
        String      source              = title.getSource();
        LocalDate   creationDate        = title.getCreationDate();
        LocalDate   modificationDate    = title.getModifyDate();
        LocalDate   reckonDate          = title.getReckonDate();
        LocalDate   checkDate           = title.getCheckDate();
        
        int         authorID            = dbServer.getAuthorIDForName( author );
        int         listID              = dbServer.getListID( list );
        if ( listID < 1 )
        {
            String   message    = "List \"" + list + "\" not found "
                + "for title: " + name;
            logger.severe( message );;
            throw new KCLSException( message );
        }
        
        java.sql.Date   creDate     = java.sql.Date.valueOf( creationDate );
        java.sql.Date   modDate     = 
            java.sql.Date.valueOf( modificationDate );
        java.sql.Date   recDate     = java.sql.Date.valueOf( reckonDate );
        java.sql.Date   cheDate     = java.sql.Date.valueOf( checkDate );

        int inx = 1;
        statement.setString( inx++, name );
        statement.setInt( inx++, authorID );
        statement.setInt( inx++, listID );
        statement.setString( inx++, mediaType );
        statement.setInt( inx++, checkQPos );
        statement.setInt( inx++, reckonQPos );
        statement.setInt( inx++, rank );
        statement.setInt( inx++, rating );
        statement.setString( inx++, source );
        statement.setDate( inx++, creDate );
        statement.setDate( inx++, modDate );
        statement.setDate( inx++, recDate );
        statement.setDate( inx++, cheDate );
        
        return inx;
    }
}
