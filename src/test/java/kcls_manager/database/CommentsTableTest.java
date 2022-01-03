package kcls_manager.database;

import static kcls_manager.database.DBConstants.AUTHORS_TABLE_NAME;
import static kcls_manager.database.DBConstants.COMMENTS_TABLE_NAME;
import static kcls_manager.database.DBConstants.LISTS_TABLE_NAME;
import static kcls_manager.database.DBConstants.TITLES_TABLE_NAME;
import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.TITLE_TYPE;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kcls_manager.main.Author;
import kcls_manager.main.Comment;
import kcls_manager.main.KCLSException;
import kcls_manager.main.KCLSList;
import kcls_manager.main.Title;
import kcls_manager.main.Utils;
import test_util.AuthorFactory;
import test_util.CommentFactory;
import test_util.TestUtils;
import test_util.TitleFactory;

/**
 * Tester for the CommentsTable class.
 * 
 * <em>Important:</em>
 * To assist testing, collections of Comments are 
 * stored in Sets (rather than Lists).
 * It turns out sets break if you change an object after its been added.
 * Probably need to turn everything back into lists,
 * though that will require sorting prior to comparing.
 * For now: <b>always add a new item to the database
 * prior to adding it to a set to be used for comparison.
 * 
 * @author jstra
 *
 */
class CommentsTableTest
{
    private static final String    defListName     = "author list 1";
    private static final KCLSList  authorList1     = 
        new KCLSList( AUTHOR_TYPE, defListName );
    private static final String    defAuthorName   = "One, Author";
    private static int             defAuthorID;
    private static final Author    defAuthor       = 
        new Author( defAuthorName, defListName );
    
    private DBServer        dbServer;
    private AuthorFactory   authorFactory;
    private TitleFactory    titleFactory;
    private CommentFactory  commentFactory;
    
    @BeforeAll
    public static void beforeAll()
    {
        TestUtils.loggingInit();
        DBServer    dbServer    = TestUtils.getDBServer();
        dbServer.truncateTable( COMMENTS_TABLE_NAME );
        dbServer.truncateTable( AUTHORS_TABLE_NAME );
        dbServer.truncateTable( TITLES_TABLE_NAME );
        dbServer.truncateTable( LISTS_TABLE_NAME );

        dbServer.insertList( authorList1 );
    }
    
    @BeforeEach
    void setUp() throws Exception
    {
        dbServer = TestUtils.getDBServer();
        dbServer.truncateTable( COMMENTS_TABLE_NAME );
        dbServer.truncateTable( AUTHORS_TABLE_NAME );
        dbServer.truncateTable( TITLES_TABLE_NAME );
        
        authorFactory = new AuthorFactory();
        commentFactory = new CommentFactory();
        titleFactory = new TitleFactory();

        dbServer.insertAuthor( defAuthor );
        defAuthorID = defAuthor.getIdent().getAsInt();
    }

    @Test
    void testInsertGetComment()
    {
        Comment     exp     = getUniqueComment( AUTHOR_TYPE, defAuthorID );
        dbServer.insertComment( exp );
        OptionalInt ident   = exp.getIdent();
        assertTrue( ident.isPresent() );
        
        Comment actual  = dbServer.getComment( ident.getAsInt() );
        assertEquals( exp, actual );
    }

    @Test
    void testUpdateComment()
    {
        Comment     comment = getUniqueComment( AUTHOR_TYPE, defAuthorID );
        dbServer.insertComment( comment );
        
        comment.setText( comment.getText() + " ***" );
        dbServer.updateComment( comment );
        
        Comment act = dbServer.getComment( comment.getIdent().getAsInt() );
        assertNotNull( act );
        assertFalse( comment == act );
        assertEquals( comment, act );
    }

    @Test
    void testDeleteCommentComment()
    {
        Comment     comment = getUniqueComment( AUTHOR_TYPE, defAuthorID );
        dbServer.insertComment( comment );
        
        OptionalInt optIdent    = comment.getIdent();
        assertTrue( optIdent.isPresent() );
        
        int         ident       = optIdent.getAsInt();
        Comment     check       = dbServer.getComment( ident );
        assertEquals( comment, check );
        
        dbServer.deleteComment( comment );
        check = dbServer.getComment( ident );
        assertNull( check );
    }
    
    /**
     * Exercises the deleteCommentIf process.
     * 
     * When processing a list of comments for deletion
     * the code looks for an Ident in the comment;
     * if there is one it attempts to delete it from
     * the Comments table.
     * <ol>
     * <li>Create a few comments and add the to the Comments table;</li>
     * <li>
     * Create a couple of comments and DON'T 
     * add them to the Comments table;
     * <li>
     * </li>
     * Delete a proper subset of the original comments
     * from the database.
     * </li>
     * <li>
     * Stick all the comments in a collection and 
     * pass the collection to the target method.
     * Verify that the database contains the correct
     * collection of comments.
     * </li
     * </ol>
     */
    @Test
    public void testDeleteCommentIf()
    {
        List<Comment>   allComments = new ArrayList<>();
        List<Comment>   persistent  = new ArrayList<>();
        List<Comment>   toDelete    = new ArrayList<>();
        List<Comment>   notInTable  = new ArrayList<>();
        
        // comments that go in the database and stay there.
        for ( int inx = 0 ; inx < 5 ; ++inx )
        {
            Comment comment = getUniqueComment( AUTHOR_TYPE, defAuthorID );
            dbServer.insertComment( comment );
            persistent.add( comment );
            allComments.add( comment );
        }
        
        // comments that go in the database then get deleted
        for ( int inx = 0 ; inx < 5 ; ++inx )
        {
            Comment comment = getUniqueComment( AUTHOR_TYPE, defAuthorID );
            dbServer.insertComment( comment );
            toDelete.add( comment );
            allComments.add( comment );
        }
        
        // verify data so far
        List<Comment>   actComments = dbServer.getAllComments();
        assertTrue( Utils.equals( allComments, actComments ) );
        
        // Comments that don't go in the database at all
        for ( int inx = 0 ; inx < 5 ; ++inx )
        {
            Comment comment = getUniqueComment( AUTHOR_TYPE, defAuthorID );
            notInTable.add( comment );
            allComments.add( comment );
        }
        
        // delete target comments
        for ( Comment comment : toDelete )
        {
            dbServer.deleteComment( comment );
            allComments.remove( comment );
        }
        
        // verify configuration so far
        for ( Comment comment : toDelete )
        {
            OptionalInt ident   = comment.getIdent();
            assertTrue( ident.isPresent() );
            
            assertNull( dbServer.getComment( ident.getAsInt() ) );
        }
        
        for ( Comment comment : persistent )
        {
            OptionalInt ident       = comment.getIdent();
            assertTrue( ident.isPresent() );
            int         intIdent    = ident.getAsInt();
            
            assertEquals( comment, dbServer.getComment( intIdent ) );
        }
        
        // all the comments that should be in the database
        actComments = dbServer.getAllComments();
        assertTrue( Utils.equals( persistent, actComments ) );
    }
    
//
//    @Test
//    void testDeleteCommentInt()
//    {
//        Set<Comment>   titleComments   = new HashSet<>();
//        Set<Comment>   authorComments  = new HashSet<>();
//        compileCommentLists( 10, titleComments, authorComments );
//        
//        List<Comment>   expComments     = new ArrayList<>();
//        expComments.addAll( titleComments );
//        expComments.addAll( authorComments );
//        
//        for ( Comment comment : expComments )
//            dbServer.insertComment( comment );
//        
//        expComments.sort(sortByIdent);
//        while ( !expComments.isEmpty() )
//        {
//            int next    = (int)(Math.random() * expComments.size() );
//            Comment comment = expComments.remove( next );
//            dbServer.deleteComment( comment );
//            Set<Comment>   actComments = dbServer.getAllComments();
//            assertEquals( expComments, actComments );
//        }
//    }
    
    /**
     * Create and insert three authors with non-empty comment lists.
     * <ol>
     * <li>Verify all comments in database</li>
     * <li>
     *      Delete one author;
     *      verify author's comments deleted from database.
     * </li>
     * <li>
     *      Delete second author;
     *      verify author's comments deleted from database.
     * </li>
     * <li>
     *      Without updating, add more comments to remaining author.
     *      Delete remaining author;
     *      verify
     *      <ul>
     *          <li>Doesn't crash on new (un-added) comments</li>
     *          <li>Comments table empty</li>
     *      </ul>
     * </li>
     * </ol>
     */
    @Test
    public void testDeleteCommentsForAuthor()
    {
        Author  authorToDelete  = getUniqueAuthor( 5 );
        Author  authorToKeep    = getUniqueAuthor( 5 );
        dbServer.insertAuthor( authorToDelete);
        dbServer.insertAuthor( authorToKeep);
        
        Author  tempToDelete = new Author( authorToDelete );
        Author  tempToKeep   = new Author( authorToKeep );
        tempToDelete.clearComments();
        tempToKeep.clearComments();
        
        dbServer.getCommentsFor( tempToDelete );
        dbServer.getCommentsFor( tempToKeep );
        
        List<Comment>   expComments = authorToDelete.getComments();
        List<Comment>   actComments = tempToDelete.getComments();
        assertTrue( Utils.equals( expComments, actComments ) );
        
        expComments = authorToKeep.getComments();
        actComments = tempToKeep.getComments();
        assertTrue( Utils.equals( expComments, actComments ) );
        
        
        // Catch an odd boundary condition
        tempToDelete.addComment( getUniqueComment( AUTHOR_TYPE, 1 ) );
        dbServer.deleteCommentsFor( tempToDelete );
        dbServer.getCommentsFor( tempToDelete );
        assertTrue( tempToDelete.getComments().isEmpty() );
        
        dbServer.getCommentsFor( tempToKeep );
        expComments = authorToKeep.getComments();
        actComments = tempToKeep.getComments();
        assertTrue( Utils.equals( expComments, actComments ) );
    }

    /**
     * Executes three subtests: getCommentsFor(Title),
     * getCommentsFor(Author), getAllComments.
     * The Title and Author tests must be executed first,
     * because these populate the database for the
     * getAllComments test.
     */
    @Test
    void testGetComments()
    {
//        System.out.println( dbServer.getAllComments() );
        dbServer.truncateTable( COMMENTS_TABLE_NAME );
        List<Comment>   allComments = new ArrayList<>();
        List<Title>     titles      = testGetCommentsForTitle();
        for ( Title title : titles )
            allComments.addAll( title.getComments() );
        
        List<Author>    authorComments  = 
            testGetCommentsForAuthor();
        for ( Author author : authorComments )
            allComments.addAll( author.getComments() );
        
        // Do this again, after author comments have been added
        for ( Title title : titles )
        {
            Title   newTitle    = new Title( title );
            dbServer.getCommentsFor( newTitle );
            List<Comment>   expComments = title.getComments();
            List<Comment>   actComments = newTitle.getComments();
            assertTrue( Utils.equals( expComments, actComments ) );
        }
        
        List<Comment>   actComments     = dbServer.getAllComments();
        assertTrue( Utils.equals( allComments, actComments ) );
    }
    
    @Test
    public void testSynchronzeCommentsForAuthor()
    {
        dbServer.truncateTable( COMMENTS_TABLE_NAME );
        defAuthor.clearComments();
        
        List<Comment>   allComments = new ArrayList<>();
        List<Comment>   persistent  = new ArrayList<>();
        List<Comment>   toRemove    = new ArrayList<>();
        
        // Persistent comments
        for ( int inx = 0 ; inx < 5 ; ++inx )
        {
            Comment comment = getUniqueComment( AUTHOR_TYPE, defAuthorID );
            allComments.add( comment );
            persistent.add( comment );
        }
        
        // To remove after inserting
        for ( int inx = 0 ; inx < 5 ; ++inx )
        {
            Comment comment = getUniqueComment( AUTHOR_TYPE, defAuthorID );
            allComments.add( comment );
            toRemove.add( comment );
        }
        
        defAuthor.setComments( allComments );
        dbServer.insertCommentsFor( defAuthor );
        List<Comment>   actComments = dbServer.getAllComments();
        assertTrue( Utils.equals( allComments, actComments ) );
        
        // on synch, these should be deleted from the database
        for ( Comment comment : toRemove )
        {
            assertTrue( comment.getIdent().isPresent() );
            allComments.remove( comment );
            defAuthor.setComments( allComments );
        }
        
        // On synch, these will be updated in the database
        for ( Comment comment : defAuthor.getComments() )
        {
            assertTrue( comment.getIdent().isPresent() );
            String  newText = comment.getText() + "###";
            comment.setText( newText );
        }
        
        // on synch, these should be added to the database
        for ( int inx = 0 ; inx < 5 ; ++inx )
        {
            Comment newComment  = getUniqueComment( AUTHOR_TYPE, defAuthorID );
            assertTrue( newComment.getIdent().isEmpty() );
            defAuthor.addComment( newComment );
            allComments.add( newComment );
        }
        assertTrue( Utils.equals( allComments, defAuthor.getComments() ) );
        
        dbServer.synchronizeCommentsFor( defAuthor );
        actComments = dbServer.getAllComments();
        assertTrue( Utils.equals( allComments, actComments ) );
        assertEquals( allComments.size(), actComments.size() );
    }
    
    @Test
    public void testSynchronzeCommentsFor()
    {
        dbServer.truncateTable( COMMENTS_TABLE_NAME );
        Title   title   = getUniqueTitle( 0 );
        dbServer.insertTitle( title );
        OptionalInt titleID = title.getIdent();
        title.clearComments();
        
        List<Comment>   allComments = new ArrayList<>();
        List<Comment>   persistent  = new ArrayList<>();
        List<Comment>   toRemove    = new ArrayList<>();
        
        // Persistent comments
        assertTrue( titleID.isPresent() );
        int intTitleID  = titleID.getAsInt();
        for ( int inx = 0 ; inx < 5 ; ++inx )
        {
            Comment comment = getUniqueComment( TITLE_TYPE, intTitleID );
            allComments.add( comment );
            persistent.add( comment );
        }
        
        // To remove after inserting
        for ( int inx = 0 ; inx < 5 ; ++inx )
        {
            Comment comment = getUniqueComment( TITLE_TYPE, intTitleID );
            allComments.add( comment );
            toRemove.add( comment );
        }
        
        title.setComments( allComments );
        dbServer.insertCommentsFor( title );
        List<Comment>   actComments = dbServer.getAllComments();
        assertTrue( Utils.equals( allComments, actComments ) );
        
        // on synch, these should be deleted from the database
        for ( Comment comment : toRemove )
        {
            assertTrue( comment.getIdent().isPresent() );
            allComments.remove( comment );
            title.setComments( allComments );
        }
        
        // On synch, these will be updated in the database
        for ( Comment comment : title.getComments() )
        {
            assertTrue( comment.getIdent().isPresent() );
            String  newText = comment.getText() + "###";
            comment.setText( newText );
        }
        
        // on synch, these should be added to the database
        for ( int inx = 0 ; inx < 5 ; ++inx )
        {
            Comment newComment  = getUniqueComment( AUTHOR_TYPE, intTitleID );
            assertTrue( newComment.getIdent().isEmpty() );
            title.addComment( newComment );
            allComments.add( newComment );
        }
        assertTrue( Utils.equals( allComments, title.getComments() ) );
        
        dbServer.synchronizeCommentsFor( title );
        actComments = dbServer.getAllComments();
        assertTrue( Utils.equals( allComments, actComments ) );
        assertEquals( allComments.size(), actComments.size() );
    }
    
    /**
     * Test some of the error paths.
     */
    @Test
    public void testGoWrong()
    {
        Title       title       = new Title( "A Title" );
        Author      author      = new Author( "Anne Author" );
        Comment comment = getUniqueComment( AUTHOR_TYPE, -1 );
        Class<KCLSException>    excClass    = KCLSException.class;
        
        assertThrows( excClass, () -> dbServer.updateComment( comment ) );
        assertThrows( excClass, () -> dbServer.deleteComment( comment ) );
        assertThrows( excClass, () -> dbServer.getCommentsFor( title ) );
        assertThrows( excClass, () -> dbServer.getCommentsFor( author ) );
        
        comment.setItemID( OptionalInt.empty() );
        assertThrows( excClass, () -> dbServer.insertComment( comment ) );
        assertThrows( excClass, () -> 
            dbServer.synchronizeCommentsFor( title ) );
        assertThrows( excClass, () -> 
            dbServer.insertCommentsFor( title ) );
    }
    
    private List<Title> testGetCommentsForTitle()
    {
        List<Title> titles  = new ArrayList<>();
        for ( int inx = 0 ; inx < 5 ; ++inx )
        {
            Title   title1  = getUniqueTitle( inx );
            dbServer.insertTitle( title1 );
            titles.add( title1 );
            
            List<Comment>   expComments = title1.getComments();
            OptionalInt     titleIdent  = title1.getIdent();
            assertEquals( expComments.size(), inx );
            assertTrue( titleIdent.isPresent() );
            for ( Comment comment : expComments )
            {
                assertEquals( titleIdent, comment.getItemID() );
                assertEquals( TITLE_TYPE, comment.getType() );
            }
            
            Title   title2  = new Title( title1 );
            dbServer.getCommentsFor( title2 );
            List<Comment>   actComments = title2.getComments();
            assertEquals( expComments, actComments );
        }
        
        return titles;
    }
    
    private List<Author> testGetCommentsForAuthor()
    {
        List<Author>    authors = new ArrayList<>();
        for ( int inx = 0 ; inx < 5 ; ++inx )
        {
            Author  author1  = getUniqueAuthor( inx );
            dbServer.insertAuthor( author1 );
            authors.add( author1 );
            
            List<Comment>   expComments = author1.getComments();
            OptionalInt     authorIdent = author1.getIdent();
            assertEquals( expComments.size(), inx );
            assertTrue( authorIdent.isPresent() );
            for ( Comment comment : expComments )
            {
                assertEquals( authorIdent, comment.getItemID() );
                assertEquals( AUTHOR_TYPE, comment.getType() );
            }
            
            Author  author2  = new Author( author1 );
            dbServer.getCommentsFor( author2 );
            List<Comment>   actComments = author2.getComments();
            assertEquals( expComments, actComments );
        }
        
        return authors;
    }
    
    private Title getUniqueTitle( int numComments )
    {
        Title   title   = titleFactory.getUniqueTitle( numComments );
        title.setListName( defListName );
        return title;
    }
    
    private Author getUniqueAuthor( int numComments )
    {
        Author  author  = authorFactory.getUniqueAuthor( numComments );
        author.setListName( defListName );
        return author;
    }

    private Comment getUniqueComment( int type, int itemID )
    {
        Comment     comment = commentFactory.getUniqueComment( itemID );
        comment.setIdent( OptionalInt.empty() );
        comment.setType( type );
        
        return comment;
    }
}
