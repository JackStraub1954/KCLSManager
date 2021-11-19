package kcls_manager.database;

import static kcls_manager.database.DBConstants.COMMENTS_ID_FIELD;
import static kcls_manager.database.DBConstants.COMMENTS_TABLE_NAME;
import static kcls_manager.database.DBConstants.ITEM_ID_FIELD;
import static kcls_manager.database.DBConstants.LIST_TYPE_FIELD;
import static kcls_manager.database.DBConstants.TEXT_FIELD;
import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.TITLE_TYPE;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Set;
import java.util.logging.Logger;

import kcls_manager.main.Author;
import kcls_manager.main.Comment;
import kcls_manager.main.KCLSException;
import kcls_manager.main.LibraryItem;
import kcls_manager.main.Title;

/**
 * This class provides support for maintenance of the 
 * COMMENTS table.
 * 
 * @author jstra
 *
 */
public class CommentsTable
{
    private static final String loggerName  = CommentsTable.class.getName();
    private static final Logger logger      = Logger.getLogger( loggerName );
    
    /** Inserts a single comment into the COMMENTS table */
    private static final String    insertCommentSQL   = 
        "INSERT INTO " + COMMENTS_TABLE_NAME + "("
        + TEXT_FIELD + ", "
        + ITEM_ID_FIELD + ", "
        + LIST_TYPE_FIELD
    + " )"
    + "VALUES ( ?, ?, ? )";
    private final PreparedStatement insertCommentPStatement;
    
    /** Updates an existing comment in the COMMENTS table */
    private static final String    updateCommentSQL   = 
        "UPDATE " + COMMENTS_TABLE_NAME + " SET "
        + TEXT_FIELD + " = ?, "
        + ITEM_ID_FIELD + " = ?, "
        + LIST_TYPE_FIELD + " = ? "
    + "WHERE " + COMMENTS_ID_FIELD + " = ?";
    private final PreparedStatement updateCommentPStatement;
    
    /** Deletes a comment from the COMMENTS table */
    private static final String    deleteCommentSQL   = 
        "DELETE FROM " + COMMENTS_TABLE_NAME
        + " WHERE " + COMMENTS_ID_FIELD + " = ?";
    private final PreparedStatement deleteCommentPStatement;
    
    /** Selects a comment by COMMENTS id. */
    private static final String getCommentSQL   =
        "SELECT *" + " FROM " + COMMENTS_TABLE_NAME
        + " WHERE " + COMMENTS_ID_FIELD + " = " + "?";
    private final PreparedStatement getCommentPStatement;
    
    /** Selects all comments. */
    private static final String getAllCommentsSQL   =
        "SELECT *" + " FROM " + COMMENTS_TABLE_NAME;
    private final PreparedStatement getAllCommentsPStatement;
    
    /** Selects all comments for a given Title object. */
    private static final String getTitleCommentsSQL   =
        "SELECT *" + " FROM " + COMMENTS_TABLE_NAME
        + " WHERE " + LIST_TYPE_FIELD + " = " + TITLE_TYPE 
        + " AND "+ ITEM_ID_FIELD + " = " + "?";
    private final PreparedStatement getTitleCommentsPStatement;
    
    /** Selects all comments for a given item. */
    private static final String getItemCommentsSQL   =
        "SELECT *" + " FROM " + COMMENTS_TABLE_NAME
        + " WHERE " + LIST_TYPE_FIELD + " = ?" 
        + " AND "+ ITEM_ID_FIELD + " = ?";
    private final PreparedStatement getItemCommentsPStatement;
    
    /** Selects all comments for a given author. */
    private static final String getAuthorCommentsSQL   =
        "SELECT *" + " FROM " + COMMENTS_TABLE_NAME
        + " WHERE " + LIST_TYPE_FIELD + " = " + AUTHOR_TYPE 
        + " AND "+ ITEM_ID_FIELD + " = " + "?";
    private final PreparedStatement getAuthorCommentsPStatement;

    public CommentsTable( DBServer server )
    {
        final int genKeys   = Statement.RETURN_GENERATED_KEYS;
        final int noGenKeys = Statement.NO_GENERATED_KEYS;
        
        insertCommentPStatement = 
            server.getPreparedStatement( insertCommentSQL, genKeys );
        updateCommentPStatement =
            server.getPreparedStatement( updateCommentSQL, noGenKeys);
        deleteCommentPStatement = 
            server.getPreparedStatement( deleteCommentSQL, noGenKeys );
        getCommentPStatement = 
            server.getPreparedStatement( getCommentSQL, noGenKeys );
        getAllCommentsPStatement = 
            server.getPreparedStatement( getAllCommentsSQL, noGenKeys );
        getTitleCommentsPStatement = 
            server.getPreparedStatement( getTitleCommentsSQL, noGenKeys );
        getAuthorCommentsPStatement = 
            server.getPreparedStatement( getAuthorCommentsSQL, noGenKeys );
        getItemCommentsPStatement = 
            server.getPreparedStatement( getItemCommentsSQL, noGenKeys );
    }

    /**
     * Add a new record to the COMMENTS  table. 
     * The comment's item ID must be set prior to invoking this method.
     *
     * @param comment  Object representing comment to be added
     * 
     * @throws  SQLException if a SQL error occurs
     */
    public void insertComment( Comment comment ) throws SQLException
    {
        String      text                = comment.getText();
        OptionalInt itemID              = comment.getItemID();
        int         type                = comment.getType();
        
        if ( itemID.isEmpty() )
        {
            String  message = 
                "itemID: null; missing required reference"
                + " to AUTHORS or TITLES table.";
            logger.severe( message );
            throw new KCLSException( message );
        }
        
        int inx = 1;
        insertCommentPStatement.setString( inx++, text );
        insertCommentPStatement.setInt( inx++, itemID.getAsInt() );
        insertCommentPStatement.setInt( inx++, type );
        
        logger.info( "inserting comment: " + text );
        insertCommentPStatement.executeUpdate();
        
        int ident   = 0;
        try ( ResultSet rSet = insertCommentPStatement.getGeneratedKeys() )
        {
            if ( !rSet.next() )
            {
                String  message = "Insert comment: generated key not returned";
                logger.severe( message );
                throw new KCLSException( message );
            }
            ident   = rSet.getInt( 1 );
        }
        comment.setIdent( ident );
        logger.info( "comment inserted" );
    }
    
    /**
     * Update a given row in the COMMENTS table.
     * The row must previously have been fetched,
     * and the commentsID set.
     * 
     * @param comment   the given row
     * 
     * @throws SQLException if a SQL exception occurs
     */
    public void updateComment( Comment comment ) throws SQLException
    {
        OptionalInt  ident              = comment.getIdent();
        if ( ident.isEmpty() )
        {
            String message  = "Expected ident for update; not found";
            logger.severe( message );
            throw new KCLSException( message );
        }
        
        int         commentsID          = ident.getAsInt();
        String      text                = comment.getText();
        OptionalInt itemID              = comment.getItemID();
        int         type                = comment.getType();
    
        int inx = 1;
        updateCommentPStatement.setString( inx++, text );
        updateCommentPStatement.setInt( inx++, itemID.getAsInt() );
        updateCommentPStatement.setInt( inx++, type );
        updateCommentPStatement.setInt( inx++, commentsID );
        
        logger.info( "updating comment: " + text );
        updateCommentPStatement.executeUpdate();
        logger.info( "comment updated" );

    }
    
    /**
     * Delete a given row in the COMMENTS table.
     * The row must previously have been fetched,
     * and the commentsID set.
     * 
     * @param comment   the given row
     * 
     * @throws SQLException if a SQL exception occurs
     */
    public void deleteComment( Comment comment ) throws SQLException
    {
        OptionalInt  ident  = comment.getIdent();
        if ( ident.isEmpty() )
        {
            String message  = "Expected ident for delete; not found";
            logger.severe( message );
            throw new KCLSException( message );
        }
        int         commentsID          = ident.getAsInt();
        
        deleteComment( commentsID );
    }
    
    /**
     * Delete a row in the COMMENTS table
     * using a given row ID.
     * 
     * @param commentsID   the given row ID
     * 
     * @throws SQLException if a SQL exception occurs
     */
    public void deleteComment( int commentsID ) throws SQLException
    {
        deleteCommentPStatement.setInt( 1, commentsID);
        logger.info( "deleting comment: " + commentsID );
        deleteCommentPStatement.executeUpdate();
        logger.info( "comment deleted" );
    }
    
    /**
     * Get a row from the comments table given the row's
     * commentsID.
     * 
     * @param ident the given commentsID
     * 
     * @return the retrieved comment, or null if not found
     * 
     * @throws SQLException if a SQL exception occurs
     */
    public Comment getComment( int ident ) throws SQLException
    {
        getCommentPStatement.setInt( 1, ident);
        Comment comment = null;
        try ( ResultSet   rSet    = getCommentPStatement.executeQuery() )
        {
            if ( rSet.next() )
                comment = cvtRowToComment( rSet );
        }
        return comment;
    }
    
    /**
     * Get all comments from the COMMENTS table.
     * 
     * @return  a (possibly empty) list of all rows in the COMMENTS table
     * 
     * @throws SQLException if a SQL exception occurs
     */
    public Set<Comment> getAllComments() throws SQLException
    {
        Set<Comment>   comments    = null;
        try ( ResultSet rSet    = getAllCommentsPStatement.executeQuery() )
        {
            comments = getComments( rSet );
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
     * @throws SQLException if a SQL error occurs
     */
    public void getCommentsFor( Title title ) throws SQLException
    {
        OptionalInt optIdent    = title.getIdent();
        if ( optIdent.isEmpty() )
        {
            String  message = 
                "Cannot get comments for title \"" + title.getTitle() + "\";"
                + "title id not present";
            logger.severe( message );
            throw new KCLSException( message );
        }
        int             ident       = optIdent.getAsInt();
        Set<Comment>   comments    = getCommentsForTitle( ident );
        title.setComments( comments );
    }
    
    /**
     * Get all rows in the COMMENTS table that are associated with
     * a given titlesID.
     * 
     * @param ident the given titlesID
     * 
     * @return  a (possibly empty) list of all comments associated
     *          with a given commentsID.
     * 
     * @throws SQLException if a SQL error occurs
     */
    private Set<Comment> getCommentsForTitle( int ident ) throws SQLException
    {
        getTitleCommentsPStatement.setInt( 1, ident);
        Set<Comment>   comments    = null;
        try ( ResultSet rSet = getTitleCommentsPStatement.executeQuery() )
        {
            comments = getComments( rSet );
        }
        return comments;
    }
    
    /**
     * Get all rows in the A table that are associated with
     * a given author. The retrieved comments are set in the given
     * author object.
     * 
     * @param author the given author object
     * 
     * @throws SQLException if a SQL error occurs
     */
    public void getCommentsFor( Author author ) throws SQLException
    {
        OptionalInt optIdent    = author.getIdent();
        if ( optIdent.isEmpty() )
        {
            String  message = 
                "Cannot get comments for author \""
                + author.getAuthor() + "\"; author id not present";
            logger.severe( message );
            throw new KCLSException( message );
        }
        int ident   = optIdent.getAsInt();
        Set<Comment>   comments    = getCommentsForAuthor( ident );
        author.setComments( comments );
    }
    
    public void synchronizeCommentsFor( LibraryItem item )
        throws SQLException
    {
        OptionalInt     optIdent    = item.getIdent();
        if ( !optIdent.isPresent() )
        {
            String  message = "Expected LibraryItem ID not found";
            logger.severe(message);
            throw new KCLSException( message );
        }
        int             itemID      = optIdent.getAsInt();
        int             itemType    = 
            item instanceof Title ? TITLE_TYPE : AUTHOR_TYPE;
        Set<Comment>   current     = item.getComments();
        Set<Comment>   committed   = getCommentsForItem( item, itemType );
        for ( Comment comment : committed )
            deleteCommentIf( current, comment );
        
        for ( Comment comment : current )
        {
            comment.setItemID( itemID );
            if ( comment.getIdent().isPresent() )
                updateComment( comment );
            else
                insertComment( comment );
        }
    }
    
    /**
     * Deletes a given comment it has been removed from a given list of
     * comments. The given comment is deemed to have been removed if:
     * <ol>
     * <li>It has a row ID; and</li>
     * <li>No comment in the given list has a matching row ID.</li>
     * </ol>
     * 
     * @param comments  the given list
     * @param comment   the given comment
     * 
     * @return  true if the given comment was deleted 
     *          from the COMMENTS table.
     *
     * @throws SQLException if a SQL error occurs
     * @throws KCLSException if the given comment does not have a row ID
     */
    private boolean 
    deleteCommentIf( Set<Comment> comments, Comment comment )
        throws SQLException, KCLSException
    {
        OptionalInt optIdent    = comment.getIdent();
        if ( optIdent.isEmpty() )
        {
            String  message = "Primary key for comment not found";
            logger.severe( message );
            throw new KCLSException( message );
        }
        
        boolean rcode   = false;
        int     ident   = optIdent.getAsInt();
        for ( Comment testComment : comments )
        {
            OptionalInt testOptIdent    = testComment.getIdent();
            if ( testOptIdent.isPresent() )
            {
                int testIdent   = testOptIdent.getAsInt();
                if ( ident == testIdent )
                    rcode = true;
            }
        }
        
        if ( !rcode )
            deleteComment( comment );
        
        return rcode;
    }
    
    /**
     * Get all rows in the COMMENTS table that are associated with
     * a given authorsID.
     * 
     * @param ident the given authorsID
     * 
     * @return  a (possibly empty) list of all comments associated
     *          with a given authorsID.
     * 
     * @throws SQLException if a SQL error occurs
     */
    private Set<Comment> getCommentsForAuthor( int ident )
        throws SQLException
    {
        getAuthorCommentsPStatement.setInt( 1, ident);
        Set<Comment>   comments    = null;
        try ( ResultSet rSet = getAuthorCommentsPStatement.executeQuery() )
        {
            comments = getComments( rSet );
        }
        return comments;
    }
    
    /**
     * Get all comments for a given LibraryItem from a given list type.
     * 
     * @param item      the given LibraryItem
     * @param listType  the given list type
     * 
     * @return a list of comments for the given item/list type
     * 
     * @throws SQLException     if a SQL exception occurs
     * @throws KCLSException    if <em>item</em> does not have a row ID
     */
    private Set<Comment> getCommentsForItem( LibraryItem item, int listType )
        throws SQLException, KCLSException
    {
        OptionalInt optIdent    = item.getIdent();
        if ( optIdent.isEmpty() )
        {
            String  message = "Expected row ID for LibraryItem not found";
            logger.severe( message );
            throw new KCLSException( message );
        }
        
        int ident   = optIdent.getAsInt();
        getItemCommentsPStatement.setInt( 1, listType );
        getItemCommentsPStatement.setInt( 2, ident );
        Set<Comment>   comments    = null;
        try ( ResultSet rSet = getItemCommentsPStatement.executeQuery() )
        {
            comments = getComments( rSet );
        }
        return comments;
    }
        
    public void insertCommentsFor( LibraryItem item ) throws SQLException
    {
        OptionalInt optIdent    = item.getIdent();
        if ( optIdent.isEmpty() )
        {
            String  message = "Expected ident for LibraryItem not found";
            logger.severe( message );
            throw new KCLSException( message );
        }
        int         ident       = optIdent.getAsInt();
        for ( Comment comment : item.getComments() )
        {
            comment.setItemID( ident );
            insertComment( comment );
        }
    }
    
    /**
     * Delete all comments for a given LibraryItem
     * (typically Author or Title).
     * Comments that do not have ids are assumed not to be
     * in the database and are ignored.
     * 
     * @param item  the given LibraryItem
     */
    public void deleteCommentsFor( LibraryItem item ) throws SQLException
    {
        for ( Comment comment : item.getComments() )
        {
            OptionalInt optIdent    = comment.getIdent();
            if ( optIdent.isPresent() )
                deleteComment( optIdent.getAsInt() );
        }
    }
    
    /**
     * Given a result set, compile a list of all comments represented
     * in the result set.
     * 
     * @param rSet  the given result set
     * 
     * @return  a (possibly empty) list of all comments represented
     *          in the result set.
     * @throws SQLException if a SQL error occurs
     */
    private Set<Comment> getComments( ResultSet rSet ) throws SQLException
    {
        Set<Comment>   comments    = new HashSet<>();
        while ( rSet.next() )
            comments.add( cvtRowToComment( rSet ) );
        return comments;
    }
    
    /**
     * Given a result set, compile the single comment associated with
     * the current row in the set. It is assumed that the next row exists,
     * and that the result set's cursor is set to the desired row.
     * 
     * @param rSet  the given result set
     * 
     * @return comment associated with the current row of a result set
     * 
     * @throws SQLException if a SQL error occurs
     */
    private Comment cvtRowToComment( ResultSet rSet ) throws SQLException
    {
        int     commentID   = rSet.getInt( COMMENTS_ID_FIELD );
        String  text        = rSet.getString( TEXT_FIELD );
        Integer itemID      = rSet.getInt( ITEM_ID_FIELD );
        int     listType    = rSet.getInt( LIST_TYPE_FIELD );
        
        Comment comment = new Comment( listType, text );
        comment.setIdent( commentID );
        comment.setText( text );
        comment.setItemID( itemID );
        return comment;
    }
}
