package kcls_manager.database;

import static kcls_manager.database.DBConstants.DB_URL;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import kcls_manager.main.Author;
import kcls_manager.main.Comment;
import kcls_manager.main.KCLSException;
import kcls_manager.main.KCLSList;
import kcls_manager.main.LibraryItem;
import kcls_manager.main.Title;

/**
 * This class provides top-level access to the Librarian database.
 * All the methods in this class throw KCLS exceptions when
 * an error occurs. 
 * If a SQL exception is thrown it is caught and re-thrown as a 
 * KCLS exception.
 * 
 * Most of the use of this class comes from GUI components
 * that query or update the database. 
 * This class also handles top-level access to relations
 * between tables;
 * e.g. if querying a title requires access to the AUTHORS table,
 * that access is moderated via this class.
 * 
 * @author jstra
 *
 *  @TODO   add shutdown hook to DataManager... or property-changed
 *          callback to DBServer?
 */
public class DBServer
{
    private static final String loggerName  = DBServer.class.getName();
    private static final Logger logger      = Logger.getLogger( loggerName );
    
    /** URL of the database. */
    private final String        dbURL;
    /** User name required for access to the database; might not be used. */
    private final String        dbUserName;
    /** Password required for database access; might no be used. */
    private final String        dbPassword;
        
    /** Encapsulates the database connection. */
    private final Connection    connection;
    
    /* *******************************
     * TABLE OBJECTS
     */
    /** Object representing the LISTS table */
    private final ListsTable    listsTable;

    /** Object representing the COMMENTS table */
    private final CommentsTable commentsTable;

    /** Object representing the AUTHORS table */
    private final AuthorsTable  authorsTable;

    /** Object representing the TITLES table */
    private final TitlesTable   titlesTable;

    /**
     * List of resources, such as Statements and PreparedStatements,
     * that need to be closed when no longer needed.
     */
    private final List<AutoCloseable> allCloseables   = new ArrayList<>();
    
    /**
     * Default constructor.
     */
    public DBServer()
    {
        this( DB_URL, "", "" );
    }
    
    /**
     * Constructor that provides the URL needed to access the database.
     * User name and password are defaulted.
     * 
     * @param dbURL the URL to connect to the database
     */
    public DBServer( String dbURL )
    {
        this( dbURL, "", "" );
    }
    
    /**
     * Constructor that provides all parameters needed to access the database.
     * 
     * @param dbURL         URL for database access
     * @param dbUserName    user name for database access
     * @param dbPassword    password for database access
     */
    public DBServer( String dbURL, String dbUserName, String dbPassword ) 
    {
        this.dbURL = dbURL;
        this.dbUserName = dbUserName;
        this.dbPassword = dbPassword;
        connection = connectToDatabase();
        
        listsTable = new ListsTable( this );
        commentsTable = new CommentsTable( this );
        authorsTable = new AuthorsTable( this );
        titlesTable = new TitlesTable( this );
    }
    
    /**
     * Gets a prepared statement.
     * The prepared statement is tracked, and automatically closed
     * during shutdown. If the statement needs to be closed prior
     * to shutdown use <em>surrenderPreparedStatement</em>.
     * This method should be used only by other database classes.
     * 
     * @param   sql     the SQL command associated with the 
     *                  prepared statement.
     *                  
     * @param   flags   Statement.RETURN_GENERATED_KEYS or
     *                  Statement.NO_GENERATED_KEYS
     * 
     * @return  a prepared statement for connected to the database
     * 
     * @throws KCLSException if the operation fails
     * 
     * @see #surrenderPreparedStatement( PreparedStatement )
     */
    public PreparedStatement 
    getPreparedStatement( String sql, int flags )
        throws KCLSException
    {
        PreparedStatement   statement   = null;
        try
        {
            statement = connection.prepareStatement( sql, flags );
            allCloseables.add( statement );
        }
        catch ( SQLException exc )
        {
            String  message = "failed to acquire prepared statement";
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        
        return statement;
    }
    
    /**
     * Frees the resources associated with a prepared statement
     * obtained via <em>getPreparedStatement.</em>.
     * This method should <em>only</em> be used with prepared
     * statements obtained via <em>getPreparedStatement.</em>. 
     * 
     * @param   statement   the prepared statement to surrender
     * 
     * @throws KCLSException if the operation fails
     * 
     * @see #getPreparedStatement(String, int)
     */
    public void surrenderPreparedStatement( PreparedStatement statement )
    {
        if ( !allCloseables.remove( statement ) )
        {
            String  message = "Invalid attempt to surrender a SQL statement";
            throw new KCLSException( message );
        }
        try
        {
            statement.close();
        }
        catch ( SQLException exc )
        {
            String  message = "failed to close prepared statement";
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Delete all rows from a given table.
     * This is probably only necessary during testing.
     * 
     * @param listName  the given table
     * 
     * @throws KCLSException if an error occurs
     */
    public void truncateTable( String listName ) throws KCLSException
    {
        logger.info( "truncating table: " + listName );
        try ( Statement statement = connection.createStatement() )
        {
            String  sql = "DELETE FROM " +listName + " WHERE 1=1";
            statement.executeUpdate( sql );
            logger.info( "table: " + listName + " truncated" );
        }
        catch ( SQLException exc )
        {
            String  message = "Failure to truncate table: " + listName;
            logger.severe( message );
            throw new KCLSException( message, exc );
        }
        logger.info( "table: " + listName + " truncated" );
    }
    
    /**
     * Add a new record to the COMMENTS table. 
     *
     * @param comment   the comment to add
     * 
     * @throws KCLSException if an error occurs
     */
    public void insertComment( Comment comment ) throws KCLSException
    {
        try
        {
            logger.info( "inserting comment " );
            commentsTable.insertComment( comment );
            logger.info( "comment inserted" );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Insert comment", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Update a given row in the COMMENTS table.
     * The row must previously have been fetched,
     * and the commentsID set.
     * 
     * @param comment   the given row
     * 
     * @throws KCLSException if an error occurs
     */
    public void updateComment( Comment comment ) throws KCLSException
    {
        try
        {
            logger.info( "updating comment " );
            commentsTable.updateComment( comment );
            logger.info( "comment updated" );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Update comment", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Delete a given row in the COMMENTS table.
     * The row must previously have been fetched,
     * and the commentsID set.
     * 
     * @param comment   the given row
     * 
     * @throws KCLSException if an error occurs
     */
    public void deleteComment( Comment comment ) throws KCLSException
    {
        try
        {
            logger.info( "updating comment " );
            commentsTable.deleteComment( comment );
            logger.info( "comment updated" );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Update comment", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Delete all comments for a given LibraryItem
     * (typically Author or Title).
     * Comments that do not have database ids are assumed not to be
     * in the database and are ignored.
     * 
     * @param item  the given LibraryItem
     * 
     * @throws KCLSException if an error occurs
     */
    public void deleteCommentsFor( LibraryItem item ) throws KCLSException
    {
        try
        {
            logger.info( "deleting comments for LibraryItem" );
            commentsTable.deleteCommentsFor( item );
            logger.info( "comments for LibraryItem deleted" );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Update comment", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Get a row from the comments table given the row's
     * commentsID.
     * 
     * @param ident the given commentsID
     * 
     * @return the retrieved comment, or null if not found
     * 
     * @throws KCLSException if an error occurs
     */
    public Comment getComment( int ident ) throws KCLSException
    {
        Comment comment = null;
        try
        {
            logger.info( "getting comment " );
            comment = commentsTable.getComment( ident );
            logger.info( "comment retrieved" );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Get comment", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        return comment;
    }
    
    /**
     * Get all comments from the COMMENTS table.
     * 
     * @return  a (possibly empty) list of all rows in the COMMENTS table
     * 
     * @throws KCLSException if an error occurs
     */
    public Set<Comment> getAllComments() throws KCLSException
    {
        Set<Comment>   comments    = null;
        try
        {
            logger.info( "getting all comments " );
            comments = commentsTable.getAllComments();
            logger.info( "all comments retrieved" );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Get all comments", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        return comments;
    }
    
    /**
     * Get all rows in the COMMENTS table that are associated with
     * a given title. The retrieved comments are set in the given
     * title object.
     * 
     * @param title the given title object
     * 
     * @throws KCLSException if an error occurs
     */
    public void getCommentsFor( Title title ) throws KCLSException
    {
        try
        {
            logger.info( "getting all title comments " );
            commentsTable.getCommentsFor( title );
            logger.info( "all title comments retrieved" );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Get all title comments", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Update the COMMENTS table using a list of Comment objects
     * owned by a given Author.
     * If the Comment object is already represented in the table 
     * (Comment.itemID.isPresent == true) the existing Comment row
     * is updated, otherwise the itemID is set, and the comment
     * is inserted.
     * Comments assigned to the Author already in the database,
     * but not represented in the Author's list of comments,
     * are deleted.
     * 
     * @param author the given author object
     */
    public void synchronizeCommentsFor( Author author ) throws KCLSException
    {
        try
        {
            String  name    = author.getAuthor();
            logger.info( "updating comments for author: " + name );
            commentsTable.synchronizeCommentsFor( author );
            logger.info( "update comments complete for author: " + name );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Update author comments", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Insert all comments associated with an author.
     * This is typically an adjunct operation to insertAuthor.
     * 
     * @param author    the author owning the comments to add
     * 
     * @throws KCLSException    if an error occurs
     */
    public void insertCommentsFor( Author author )
        throws KCLSException
    {
        try
        {
            String  name    = author.getAuthor();
            logger.info( "inserting comments for author: " + name );
            commentsTable.insertCommentsFor( author );
            logger.info( "insert comments complete for author: " + name );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Insert author comments", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Insert all comments associated with a title.
     * This is typically an adjunct operation to insertTitle.
     * 
     * @param title    the title owning the comments to add
     * 
     * @throws KCLSException    if an error occurs
     */
    public void insertCommentsFor( Title title )
        throws KCLSException
    {
        try
        {
            String  name    = title.getTitle();
            logger.info( "inserting comments for title: " + name );
            commentsTable.insertCommentsFor( title );
            logger.info( "insert comments complete for title: " + name );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Insert ti comments", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Update the COMMENTS table using a list of Comment objects
     * owned by a given Title.
     * If the Comment object is already represented in the table 
     * (Comment.itemID.isPresent == true) the existing Comment row
     * is updated, otherwise the itemID is set, and the comment
     * is inserted.
     * Comments assigned to the Title already in the database,
     * but not represented in the Title's list of comments,
     * are deleted.
     * 
     * @param title the given Title object
     */
    public void synchronizeCommentsFor( Title title )
    {
        try
        {
            logger.info( "getting all title comments " );
            commentsTable.synchronizeCommentsFor( title );
            logger.info( "all title comments retrieved" );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Get all title comments", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Get all rows in the COMMENTS table that are associated with
     * a given author. The retrieved comments are set in the given
     * author object.
     * 
     * @param author the given title object
     * 
     * @throws KCLSException if an error occurs
     */
    public void getCommentsFor( Author author ) throws KCLSException
    {
        try
        {
            logger.info( "getting all author comments " );
            commentsTable.getCommentsFor( author );
            logger.info( "all author comments retrieved" );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Get all author comments", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Add a new record to the LISTS table. 
     *
     * @param list  the list to add
     * 
     * @throws KCLSException if an error occurs
     */
    public void insertList( KCLSList list ) throws KCLSException
    {
        try
        {
            String  listName    = list.getDialogTitle();
            logger.info( "inserting list " + listName );
            listsTable.insertList( list );
            logger.info( listName + " inserted" );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Insert list", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Update an existing record in the LISTS table.
     * The record must have been previously read from the table. 
     *
     * @param list  the list to update
     * 
     * @throws KCLSException if an error occurs
     */
    public void updateList( KCLSList list ) throws KCLSException
    {
        try
        {
            String  listName    = list.getDialogTitle();
            logger.info( "updating list " + listName );
            listsTable.updateList( list );
            logger.info( listName + " updated" );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Update list", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Delete an existing record from the LISTS table.
     * The record must have been previously read from the table. 
     *
     * @param list  the list to delete
     * 
     * @throws KCLSException if an error occurs
*/
    public void deleteList( KCLSList list ) throws KCLSException
    {
        try
        {
            String  listName    = list.getDialogTitle();
            logger.info( "deleting list " + listName );
            listsTable.deleteList( list );
            logger.info( listName + " deleted" );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Delete list", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Delete an existing record from the LISTS table.
     *
     * @param ident  the ID of the list to delete
     * 
     * @throws KCLSException if an error occurs
     */
    public void deleteList( int ident ) throws KCLSException
    {
        try
        {
            logger.info( "deleting list " + ident );
            listsTable.deleteList( ident );
            logger.info( "List deleted: " + ident );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Delete list", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }

    /**
     * Returns a list of all rows in the LISTS table.
     * 
     * @return a list of all rows in the LISTS table
     * 
     * @throws KCLSException if an error occurs
     */
    public List<KCLSList> getAllLists() throws KCLSException
    {
        List<KCLSList>  allLists;
        try
        {
            logger.info( "getting all lists" );
            allLists = listsTable.getAllLists();
            logger.info( "\"get all lists\" operation complete"  );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Get all lists", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        return allLists;
    }
    
    /**
     * Returns a list of all lists of type TITLE_TYPE.
     * 
     * @return a list of all lists of type TITLE_TYPE
     * @throws KCLSException if an error occurs.
     */
    public List<KCLSList> getTitleLists() throws KCLSException
    {
        List<KCLSList>  titleLists;
        try
        {
            logger.info( "getting title lists" );
            titleLists = listsTable.getTitleLists();
            logger.info( "\"get title lists\" operation complete"  );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Get title lists", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        return titleLists;
    }
    
    public List<KCLSList> getAuthorLists()
    {
        List<KCLSList>  titleLists;
        try
        {
            logger.info( "getting author lists" );
            titleLists = listsTable.getAuthorLists();
            logger.info( "\"get author lists\" operation complete"  );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Get author lists", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        return titleLists;
    }
    
    public int getListID( String listName )
    {
        int ident;
        try
        {
            logger.info( "getting list id for " + listName );
            ident = listsTable.getListID( listName );
            logger.info( 
                "\"get list ID for " + listName 
                + "\" operation complete"  );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Get list ID for " + listName, exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        return ident;
    }
    
    /**
     * Given a database row ident, get the associated list.
     * Returns null if the associated LISTS row can't be found.
     * 
     * @param ident the given row ident
     * 
     * @return KCLSList object associated with ident,
     *         or null if not resolved
     * 
     * @throws KCLSException if an error occurs 
     */
    public KCLSList getList( int ident )
    {
        KCLSList    list    = null;
        try
        {
            logger.info( "getting list for " + ident );
            list = listsTable.getList( ident );
            if ( list == null )
                logger.severe( "list not found for ident: " + ident );
            logger.info( 
                "\"get list for " + ident 
                + "\" operation complete"  );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Get list for ident", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        return list;
    }
    
    /**
     * Add a new record to the AUTHORS table. 
     *
     * @param author   the author to add
     */
    public void insertAuthor( Author author )
    {
        String  name    = author.getAuthor();
        try
        {
            logger.info( "inserting author: " + name );
            authorsTable.insertAuthor( author );
            logger.info( "inserted author: " + name );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Insert author", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Update a record in the AUTHORS table. 
     * The record must previously have been queried.
     *
     * @param author   the author to update
     */
    public void updateAuthor( Author author )
    {
        String  name    = author.getAuthor();
        try
        {
            logger.info( "updating author: " + name );
            authorsTable.updateAuthor( author );
            logger.info( "updated author: " + name );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Update author", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Delete an author from the AUTHORS table.
     * 
     * @param author    the author to delete
     */
    public void deleteAuthor( Author author )
    {
        String  name    = author.getAuthor();
        try
        {
            logger.info( "deleting author: " + name );
            authorsTable.deleteAuthor( author );
            logger.info( "deleted author: " + name );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "Delete author", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Given an author name find its row ID.
     * 
     * @param name  the name of the author to query
     * 
     * @return row ID for <em>author</em>
     *         or < 0 if not found
     */
    public int getAuthorIDForName( String name )
    {
        int     ident   = -1;
        try
        {
            logger.info( "getting author ID for : " + name );
            ident = authorsTable.getAuthorID( name );
            logger.info( "read complete for author: " + name );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "get author by name", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        return ident;
    }
    
    /**
     * Reads a record from the AUTHORS table. 
     *
     * @param author   the comment to add
     */
    public Author getAuthor( int ident )
    {
        Author  author  = null;
        try
        {
            logger.info( "getting author: " + ident );
            author = authorsTable.getAuthorByID( ident );
            logger.info( "read complete for author: " + ident );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "get author", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        return author;
    }
    
    /**
     * Returns a list of all authors in the AUTHORS table. 
     *
     * return a list of all authors in the AUTHORS table
     * 
     * @throws  KCLSException if an error occurs
     */
    public List<Author> getAllAuthors() throws KCLSException
    {
        List<Author>    authors;
        try
        {
            logger.info( "getting all authors" );
            authors = authorsTable.getAllAuthors();
            logger.info( "read complete for all authors");
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "get all authors", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        return authors;
    }
    
    /**
     * Insert a row into the TITLES table.
     * 
     * @param title     Title representing row to insert
     * 
     * @throws KCLSException if an error occurs
     */
    public void insertTitle( Title title )
        throws KCLSException
    {
        String  name    = title.getTitle();
        try
        {
            logger.info( "inserting title: " + name );
            titlesTable.insertTitle( title );
            logger.info( "inserted title: " + name );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "insert title", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    public void updateTitle( Title title ) throws KCLSException
    {
        String  name    = title.getTitle();
        try
        {
            logger.info( "updating title: " + name );
            titlesTable.updateTitle( title );
            logger.info( "updated title: " + name );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "update title", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Delete a row from the TITLES table.
     * The row must have previously been queried.
     * 
     * @param title     Title representing row to delete.
     * 
     * @throws KCLSException if an error occurs
     */
    public void deleteTitle( Title title ) throws KCLSException
    {
        String  name    = title.getTitle();
        try
        {
            logger.info( "deleting title: " + name );
            titlesTable.deleteTitle( title );
            logger.info( "deleted title: " + name );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "delete title", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
    }
    
    /**
     * Get a title given its row ID.
     * 
     * @param ident the given row ID
     * 
     * @return  Title object representing queried row, or null if not found
     * 
     * @throws KCLSException if an error occurs
     */
    public Title getTitle( int ident ) throws KCLSException
    {
        Title   title   = null;
        try
        {
            logger.info( "getting title: " + ident );
            title = titlesTable.getTitle( ident );
            logger.info( "queried row for title: " + ident );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "insert title", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        
        return title;
    }
    
    /**
     * Gets a list of all rows in the TITLES table.
     * 
     * @return  a list of all rows in the TITLES table
     * @throws KCLSException if an error occurs
     */
    public List<Title> getAllTitles() throws KCLSException
    {
        List<Title> titles  = new ArrayList<>();
        try
        {
            logger.info( "getting all titles" );
            titles.addAll( titlesTable.getAllTitles() );
            logger.info( "completed getting all titles" );
        }
        catch ( SQLException exc )
        {
            String  message = formatSQLError( "getting all titles", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        
        return titles;
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
     * @throws KCLSException if the given author is not in the database
     */
    public List<Title> getTitlesForAuthor( Author author )
        throws KCLSException
    {
        List<Title> titles  = new ArrayList<>();
        String      name    = author.getAuthor();
        try
        {
            logger.info( "getting all titles for author: " + name );
            titles.addAll( titlesTable.getTitlesForAuthor( author ) );
            logger.info( "completed getting all titles for author: " + name );
        }
        catch ( SQLException exc )
        {
            String  message = 
                formatSQLError( "getting all titles for author", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        
        return titles;
    }

    public List<Title> getTitlesForList( String listName )
        throws KCLSException
    {
        List<Title> titles  = new ArrayList<>();
        try
        {
            logger.info( "getting titles for list: " + listName );
            titles.addAll( titlesTable.getTitleList( listName ) );
            logger.info( "queried all titles for list: " + listName );
        }
        catch ( SQLException exc )
        {
            String  message = 
                formatSQLError( "getting all titles for list", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        
        return titles;
    }

    public List<Author> getAuthorsForList( String listName )
        throws KCLSException
    {
        List<Author> authors  = new ArrayList<>();
        try
        {
            logger.info( "getting authors for list: " + listName );
            authors.addAll( authorsTable.getAuthorList( listName ) );
            logger.info( "queried all authors for list: " + listName );
        }
        catch ( SQLException exc )
        {
            String  message = 
                formatSQLError( "getting all authors for list", exc );
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        
        return authors;
    }

    /**
     * Shutdown the Derby embedded driver.
     */
    public void shutdown()
    {
        // Surely these constants are declared somewhere in the
        // Derby distribution, but I can't find them.
        // Constant values are taken from SimpleApp.java, which
        // is part of the Derby download.
        final int       derbySystemShutdownErrorCode    = 50000;
        final String    derbySystemShutdownState        = "XJ015";
        try
        {
            logger.info( "freeing resources" );
            for ( AutoCloseable closeable : allCloseables )
                closeable.close();
            logger.info( "freeing resources complete" );

            logger.info( "closing connection" );
            connection.close();
            
            // DON'T TRY TO ACTUALLY SHUTDOWN THE SERVER
            // WHEN USING THE EMBEDDED DRIVER
//            logger.info( "starting shutdown" );
//            DriverManager.getConnection("jdbc:derby:;shutdown=true");
//            logger.info( "shutdown complete without exception" );
        }
        catch ( IOException exc )
        {
            // Apparently this is not unexpected
        }
        catch ( SQLException exc )
        {
            int     errorCode   = exc.getErrorCode();
            String  state       = exc.getSQLState();
            if ( errorCode == derbySystemShutdownErrorCode
                 && state.equals( derbySystemShutdownState )
               )
            {
                String  message = 
                    "shutdown complete with error code: " + errorCode
                    + " and system state: " + state;
                logger.info( message );
            }
            else
            {
                String  message = 
                    "shutdown incomplete with error code: " + errorCode
                    + " and system state: " + state;
                logger.log( Level.SEVERE, message, exc );
            }
        }
        catch ( Exception exc )
        {
            logger.log( Level.SEVERE, "shutdown incomplete", exc );
        }
    }
    
    /**
     * Dump the contents of a given table.
     * Mainly used for diagnostic purposes.
     * 
     * @param tableName the name of the given table
     * 
     * @return the name of the file the table was dumped to
     */
    public String dumpTable( String tableName )
    {
        String  fileName    = 
            "dump-" + System.currentTimeMillis() + "-" + tableName + ".db";
        logger.info( "dumping " + tableName + " table to " + fileName );
        String  sql = 
        "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)";
        logger.info( sql );
        try ( PreparedStatement statement =
            connection.prepareStatement( sql ) )
        {
            statement.setString(1,null);
            statement.setString(2,tableName);
            statement.setString(3,fileName);
            statement.setString(4,"-");
            statement.setString(5,null);
            statement.setString(6,null);
            statement.execute();
        }
        catch ( SQLException exc )
        {
            int     errorCode   = exc.getErrorCode();
            String  sqlState    = exc.getSQLState();
            String  message = 
                "dump of " + tableName + " failed;"
                + " err code: " + errorCode + " sqlState: " + sqlState;
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        logger.info( "dump of " + tableName + " complete" );
        return fileName;
    }
//    
//    private String formatSQLError( SQLException exc )
//    {
//        return formatSQLError( "SQL Error", exc );
//    }
    
    private String formatSQLError( String prefix, SQLException exc )
    {
        int     errorCode   = exc.getErrorCode();
        String  sqlState    = exc.getSQLState();
        String  message     =
            prefix + "; error code: " + errorCode 
            + ", SQL state: " + sqlState;
        
        return message;
    }
    
    private Connection connectToDatabase()
    {
        Connection  conn    = null;
        try
        {
            logger.info( "opening connection on: " + dbURL );
            conn    = 
                DriverManager.getConnection( dbURL, dbUserName, dbPassword );
            logger.info( "connection opened on: " + dbURL );
        }
        catch ( SQLException exc )
        {
            String  message = 
                "database connection failure for URL: " + dbURL; 
            logger.log( Level.SEVERE, message, exc );
            throw new KCLSException( message, exc );
        }
        return conn;
    }
}
