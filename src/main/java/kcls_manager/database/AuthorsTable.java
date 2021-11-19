package kcls_manager.database;

import static kcls_manager.database.DBConstants.AUTHORS_ID_FIELD;
import static kcls_manager.database.DBConstants.AUTHORS_TABLE_NAME;
import static kcls_manager.database.DBConstants.AUTHOR_FIELD;
import static kcls_manager.database.DBConstants.CREATION_DATE_FIELD;
import static kcls_manager.database.DBConstants.CURRENT_COUNT_FIELD;
import static kcls_manager.database.DBConstants.LAST_COUNT_FIELD;
import static kcls_manager.database.DBConstants.LISTS_ID_FIELD;
import static kcls_manager.database.DBConstants.MODIFICATION_DATE_FIELD;
import static kcls_manager.database.DBConstants.RANK_FIELD;
import static kcls_manager.database.DBConstants.RATING_FIELD;
import static kcls_manager.database.DBConstants.SOURCE_FIELD;

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

/**
 * Provides database support for accessing the LISTS table. This class
 * should only be used inside the database package.
 * @author jstra
 *
 */
public class AuthorsTable extends Table
{
    private static final String loggerName  = AuthorsTable.class.getName();
    private static final Logger logger      = Logger.getLogger( loggerName );

    /** Inserts a single author into the AUTHORS table */
    private final String    insertAuthorSQL   = 
        "INSERT INTO " + AUTHORS_TABLE_NAME + "("
        + AUTHOR_FIELD + ", "
        + RATING_FIELD + ", "
        + RANK_FIELD + ", "
        + SOURCE_FIELD + ", "
        + LAST_COUNT_FIELD + ", "
        + CURRENT_COUNT_FIELD + ", "
        + LISTS_ID_FIELD + ", "
        + CREATION_DATE_FIELD + ", "
        + MODIFICATION_DATE_FIELD
    + " )"
    + "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";
    private final PreparedStatement insertAuthorPStatement;
    
    /** Update a row in the AUTHORS database */
    private final String updateAuthorSQL    =
      "UPDATE " + AUTHORS_TABLE_NAME + " SET "    
      + AUTHOR_FIELD + " = ?, "
      + RATING_FIELD + " = ?, "
      + RANK_FIELD + " = ?, "
      + SOURCE_FIELD + " = ?, "
      + LAST_COUNT_FIELD + " = ?, "
      + CURRENT_COUNT_FIELD + " = ?, "
      + LISTS_ID_FIELD + " = ?, "
      + CREATION_DATE_FIELD + " = ?, "
      + MODIFICATION_DATE_FIELD  + " = ? "
    + "WHERE " + AUTHORS_ID_FIELD + " = ?";
    private final PreparedStatement updateAuthorPStatement;
    
    /** Deletes an author from the AUTHORS table */
    private static final String    deleteAuthorSQL   = 
        "DELETE FROM " + AUTHORS_TABLE_NAME
        + " WHERE " + AUTHORS_ID_FIELD + " = ?";
    private final PreparedStatement deleteAuthorPStatement;

    /** Gets all authors from the AUTHORS table */
    private final String    getAllAuthorsSQL  =
        "SELECT * FROM " + AUTHORS_TABLE_NAME;
    private final PreparedStatement getAllAuthorsPStatement;
    
    /** Gets all authors for a specific list */
    private final String    getAuthorListSQL  =
        "SELECT * FROM " + AUTHORS_TABLE_NAME
        + " WHERE " + LISTS_ID_FIELD + " = ?";
    private final PreparedStatement getAuthorsListPStatement;
    
    /** Gets author by ID */
    private final String    getAuthorByIDSQL  =
        "SELECT * FROM " + AUTHORS_TABLE_NAME
        + " WHERE " + AUTHORS_ID_FIELD + " = ?";
    private final PreparedStatement getAuthorByIDPStatement;
    
    /** Gets author ID for a given name*/
    private final String    getAuthorByNameSQL  =
        "SELECT " + AUTHORS_ID_FIELD + " FROM " + AUTHORS_TABLE_NAME
        + " WHERE " + AUTHOR_FIELD + " = ?";
    private final PreparedStatement getAuthorByNamePStatement;
    
    /** The DBServer instance that owns this object. */
    private final DBServer  dbServer;

    public AuthorsTable( DBServer server )
    {
        final int genKeys   = Statement.RETURN_GENERATED_KEYS;
        final int noGenKeys = Statement.NO_GENERATED_KEYS;
        
        dbServer = server;
        
        insertAuthorPStatement = 
            server.getPreparedStatement( insertAuthorSQL, genKeys );
        updateAuthorPStatement = 
            server.getPreparedStatement( updateAuthorSQL, noGenKeys );
        deleteAuthorPStatement =
            server.getPreparedStatement( deleteAuthorSQL, noGenKeys );
        getAllAuthorsPStatement = 
            server.getPreparedStatement( getAllAuthorsSQL, noGenKeys );
        getAuthorsListPStatement =
            server.getPreparedStatement( getAuthorListSQL, noGenKeys );
        getAuthorByIDPStatement =
            server.getPreparedStatement( getAuthorByIDSQL, noGenKeys );
        getAuthorByNamePStatement =
            server.getPreparedStatement( getAuthorByNameSQL, noGenKeys );
    }

    /**
     * Add a new record to the AUTHORS table. 
     *
     * @param author Object representing author to be added
     * 
     * @throws  SQLException if a SQL error occurs
     */
    public void insertAuthor( Author author ) throws SQLException
    {
        cvtAuthorToRow( author, insertAuthorPStatement );
        String  name    = author.getAuthor();
        
        // insert most data for author
        logger.info( "inserting author: " + name );
        insertAuthorPStatement.executeUpdate();
        ResultSet   rSet    = insertAuthorPStatement.getGeneratedKeys();
        if ( !rSet.next() )
        {
            String  message = "Insert author: generated key not returned";
            logger.severe( message );
            throw new KCLSException( message );
        }
        int ident   = rSet.getInt( 1 );
        author.setIdent( ident );
        
        // insert author comments
        dbServer.insertCommentsFor( author );
        logger.info( "author: " + name + " inserted" );
    }
    
    /**
     * Updates an existing author in the AUTHORS table.
     * The author record must have previously been read from the table.
     *
     * @param author    the author to update
     * 
     * @throws SQLException if a SQL error occurs
     */
    public void updateAuthor( Author author ) throws SQLException
    {
        String      name        = author.getAuthor();
        OptionalInt optIdent    = author.getIdent();
        if ( optIdent.isEmpty() )
        {
            String  message = 
                "Expected ID not found for author: " + name;
            logger.severe(message);
            throw new KCLSException( message );
        }
        
        int     ident   = optIdent.getAsInt();
        int     count   = cvtAuthorToRow( author, updateAuthorPStatement );
        updateAuthorPStatement.setInt( count, ident );
        
        // update most author data
        logger.info( "updating author: " + name );
        updateAuthorPStatement.executeUpdate();
        
        // update author comments
        dbServer.synchronizeCommentsFor( author );
        logger.info( "author: " + name + " updated" );
    }
    
    /**
     * Delete a row from the AUTHORS table.
     * The row must have been previously read from table.
     * 
     * @param author    the author to delete
     */
    public void deleteAuthor( Author author ) throws SQLException
    {
        String      name        = author.getAuthor();
        OptionalInt optIdent    = author.getIdent();
        if ( optIdent.isEmpty() )
        {
            String  message = 
                "Expected ID not found for author: " + name;
            logger.severe(message);
            throw new KCLSException( message );
        }
        
        int     ident   = optIdent.getAsInt();
        dbServer.deleteCommentsFor( author );
        deleteAuthorPStatement.setInt( 1, ident );
        deleteAuthorPStatement.executeUpdate();
    }
        
    /**
     * Get a list of all rows in the AUTHORS table.
     * 
     * @return  a list of all rows in the AUTHORS table
     * @throws SQLException
     */
    public List<Author> getAllAuthors() throws SQLException
    {
        ResultSet       rSet        = getAllAuthorsPStatement.executeQuery();
        List<Author>    allAuthors  = new ArrayList<>();
        while ( rSet.next() )
        {
            allAuthors.add( cvtRowToAuthor( rSet ) );
        }
        
        return allAuthors;
    }
    
    
    /**
     * Retrieve an author ID using its name.
     * If the author is not found, a value less than 0 is returned.
     * 
     * @param name  the name of the target author
     * 
     * @return  Author ID corresponding to <em>name</em>
     *          or a value less than 0 if not found
     *          
     * @throws SQLException if a SQL error occurs
     */
    public int getAuthorID( String name ) throws SQLException
    {
        getAuthorByNamePStatement.setString( 1, name );
        ResultSet   rSet    = getAuthorByNamePStatement.executeQuery();
        int         ident   = -1;
        if ( rSet.next() )
        {
            ident = rSet.getInt( AUTHORS_ID_FIELD );
        }
        return ident;
    }
    
    /**
     * Retrieve an author using its ID.
     * 
     * @param ident the ID of the target author
     * 
     * @return  Author object corresponding to <em>ident</em>
     *          or null if not found
     * @throws SQLException if a SQL error occurs
     */
    public Author getAuthorByID( int ident ) throws SQLException
    {
        getAuthorByIDPStatement.setInt( 1, ident );
        ResultSet       rSet        = getAuthorByIDPStatement.executeQuery();
        Author          author      = null;
        if ( rSet.next() )
        {
            author =  cvtRowToAuthor( rSet );
        }
        
        return author;
    }
    
    public List<Author> getAuthorList( String listName ) throws SQLException
    {
        List<Author>    allAuthors  = new ArrayList<>();
        int             listID      = dbServer.getListID( listName ); 
        if ( listID > 0 )
        {
            getAuthorsListPStatement.setInt( 1, listID );
            ResultSet   rSet        = getAuthorsListPStatement.executeQuery();
            while ( rSet.next() )
            {
                allAuthors.add( cvtRowToAuthor( rSet ) );
            }
        }
        return allAuthors;
    }
    
    private Author cvtRowToAuthor( ResultSet rSet ) throws SQLException
    {
        int     authorID        = rSet.getInt( AUTHORS_ID_FIELD );
        String  text            = rSet.getString( AUTHOR_FIELD);
        int     rating          = rSet.getInt( RATING_FIELD );
        int     rank            = rSet.getInt( RANK_FIELD );
        String  source          = rSet.getString( SOURCE_FIELD );
        int     lastCount       = rSet.getInt( LAST_COUNT_FIELD );
        int     currentCount    = rSet.getInt( CURRENT_COUNT_FIELD );
        int     listID          = rSet.getInt( LISTS_ID_FIELD );
        Date    sqlCreDate      = rSet.getDate( CREATION_DATE_FIELD );
        Date    sqlModDate      = rSet.getDate( MODIFICATION_DATE_FIELD );
        
        LocalDate   creDate     = sqlCreDate.toLocalDate();
        LocalDate   modDate     = sqlModDate.toLocalDate();
        
        KCLSList    list        = dbServer.getList( listID );
        String      listName    = list.getDialogTitle();
        
        Author  author  = new Author( creDate, text, listName );
        author.setIdent( authorID );
        // author.setAuthor
        author.setRating( rating );
        author.setRank( rank );
        author.setSource( source );
        author.setLastCount( lastCount );
        author.setCurrentCount( currentCount );
//        author.setList( listName );
        // title.setCreationDate
        author.setModifyDate( modDate );
        
        dbServer.getCommentsFor( author );
        
        return author;
    }
    
    /**
     * Uses populates a prepared statement with data from a given author.
     * The total number of fields set in the statement is returned.
     * 
     * @param author    the given author
     * @param statement the prepared statement to populate
     * @return the total number of fields set in the statement
     *
     * @throws SQLException if a SQL error occurs
     * @throws KCLSException if the author has not be assigned to a list
     */
    private int cvtAuthorToRow( Author author, PreparedStatement statement )
        throws SQLException, KCLSException
    {
        String      name                = author.getAuthor();
        int         rating              = author.getRating();
        int         rank                = author.getRank();
        String      source              = author.getSource();
        int         lastCount           = author.getLastCount();
        int         currentCount        = author.getCurrentCount();
        String      list                = author.getListName();
        LocalDate   creationDate        = author.getCreationDate();
        LocalDate   modificationDate    = author.getModifyDate();
        
        java.sql.Date   creDate     = java.sql.Date.valueOf( creationDate );
        java.sql.Date   modDate     = 
            java.sql.Date.valueOf( modificationDate );
        
        int         listID              = dbServer.getListID( list );
        if ( listID < 1 )
        {
            String message  = 
                "No list designation found for author: " + name;
            logger.severe( message );
            throw new KCLSException( message );
        }
        
        int inx = 1;
        statement.setString( inx++, name );
        statement.setInt( inx++, rating );
        statement.setInt( inx++, rank );
        statement.setString( inx++, source );
        statement.setInt( inx++, lastCount );
        statement.setInt( inx++, currentCount );
        statement.setInt( inx++, listID );
        statement.setDate( inx++, creDate );
        statement.setDate( inx++, modDate );
        
        return inx;
    }
}
