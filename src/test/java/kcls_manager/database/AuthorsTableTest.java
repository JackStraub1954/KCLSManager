package kcls_manager.database;

import static kcls_manager.database.DBConstants.AUTHORS_TABLE_NAME;
import static kcls_manager.database.DBConstants.COMMENTS_TABLE_NAME;
import static kcls_manager.database.DBConstants.LISTS_TABLE_NAME;
import static kcls_manager.database.DBConstants.TITLES_TABLE_NAME;
import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kcls_manager.main.Author;
import kcls_manager.main.Comment;
import kcls_manager.main.KCLSList;
import test_util.TestUtils;

class AuthorsTableTest
{
    private static final    String[]    allListNames    =
    {
        "List A",
        "List B",
        "List C",
        "List D",
        "List E"
    };
    
    private static final    KCLSList[]  allLists        = 
        new KCLSList[allListNames.length];
    
    private static final    String      defName         = "Name, Author";
    private static final    int         defLastCount    = 20;
    private static final    int         defCurrentCount = defLastCount + 10;
    private static final    LocalDate   defCreationDate = 
        LocalDate.now().plusDays( 1 );
    private static final    LocalDate   defModifyDate   = 
        defCreationDate.plusDays( 1 );
    private static final    String      defComment      = "Default comment";
    
    /**
     * Comparator to sort lists of Authors by ident.
     */
    private final Comparator<Author>    sortByIdent =
        (a1, a2) -> a1.getIdent().getAsInt() - a2.getIdent().getAsInt();

    /** Used to generate unique Author objects. */
    private int         nextAuthorPrefix;
    /** Used to generate unique Comment objects. */
    private int         nextCommentPrefix;
    private DBServer    dbServer;
    private Author      defAuthor;
    
    @BeforeAll
    static void beforeAll()
    {
        TestUtils.loggingInit();
        DBServer    server  = TestUtils.getDBServer();
        server.truncateTable( AUTHORS_TABLE_NAME );
        server.truncateTable( TITLES_TABLE_NAME );
        server.truncateTable( COMMENTS_TABLE_NAME );
        server.truncateTable( LISTS_TABLE_NAME );
        for ( int inx = 0 ; inx < allListNames.length ; ++inx )
        {
            String      name    = allListNames[inx];
            KCLSList    list    = new KCLSList( AUTHOR_TYPE, name );
            server.insertList( list );
            allLists[inx] = list;
        }
    }
    
    @BeforeEach
    void setUp() throws Exception
    {
        dbServer = TestUtils.getDBServer();
        dbServer.truncateTable( AUTHORS_TABLE_NAME );
        dbServer.truncateTable( TITLES_TABLE_NAME );
        dbServer.truncateTable( COMMENTS_TABLE_NAME );
        
        nextAuthorPrefix = 0;
        nextCommentPrefix = 0;
        defAuthor = new Author( defName );
        defAuthor.setLastCount( defLastCount );
        defAuthor.setCurrentCount( defCurrentCount );
        defAuthor.setCreationDate( defCreationDate );
        defAuthor.setModifyDate( defModifyDate );
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testInsertAuthor()
    {
        getComments( 10, defAuthor );
        defAuthor.setListName( allListNames[0] );
        dbServer.insertAuthor( defAuthor );
        
        OptionalInt optIdent    = defAuthor.getIdent();
        assertTrue( optIdent.isPresent() );
        int         ident       = optIdent.getAsInt();
        Author      newAuthor   = dbServer.getAuthor( ident );
        assertEquals( defAuthor, newAuthor );
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    void testUpdateAuthor()
    {
        getComments( 10, defAuthor );
        defAuthor.setListName( allListNames[0] );
        dbServer.insertAuthor( defAuthor );

        // Change non-comment fields.
        // Remove two comments, add three comments
        // Update author...
        // ... read/validate author
        // ... validate all comments in dbase match those owned by author
        String      name        = defName + " Q.";
        int         lastCount   = defLastCount + 5;
        int         currCount   = defCurrentCount + 5;
        String      list        = allListNames[1];
        LocalDate   creDate     = defCreationDate.plusDays( 10 );
        LocalDate   modDate     = defModifyDate.plusDays( 10 );
        
        List<Comment>   comments    = defAuthor.getComments();
        comments.remove( comments.size() / 2 );
        comments.remove( comments.size() / 2 );
          
        comments.add( new Comment( AUTHOR_TYPE, "new comment 1" ) );
        comments.add( new Comment( AUTHOR_TYPE, "new comment 2" ) );
        comments.add( new Comment( AUTHOR_TYPE, "new comment 3" ) );
        
        defAuthor.setAuthor( name );
        defAuthor.setLastCount( lastCount );
        defAuthor.setCurrentCount( currCount );
        defAuthor.setCreationDate( creDate );
        defAuthor.setModifyDate( modDate );
        defAuthor.setListName( list );
        defAuthor.setComments( comments );
        dbServer.updateAuthor( defAuthor );
        
        List<Comment>   actComments = dbServer.getAllComments();
        List<Comment>   expComments = defAuthor.getComments();
        assertEquals( expComments, actComments );
    }

    @Test
    void testGetAllAuthors()
    {
        List<Author>    allAuthors  = new ArrayList<>();
        for ( int inx = 0 ; inx < 3 ; ++inx )
        {
            String  name    = "Last" + inx + ", first" + inx;
            String  list    = allListNames[inx];
            Author  author  = new Author( name );
            author.setListName( list );
            dbServer.insertAuthor( author );
            allAuthors.add( author );
            testGetAllAuthors( allAuthors );
        }
    }

    @Test
    void testDeleteAuthor()
    {
        List<Author>    allAuthors  = new ArrayList<>();
        for ( int inx = 0 ; inx < 3 ; ++inx )
        {
            String  name    = "Last" + inx + ", first" + inx;
            String  list    = allListNames[inx];
            Author  author  = new Author( name );
            author.setListName( list );
            dbServer.insertAuthor( author );
            allAuthors.add( author );
        }
        
        for ( int inx = allAuthors.size() - 1 ; inx >= 0 ; --inx )
        {
            Author  author  = allAuthors.remove( inx );
            dbServer.deleteAuthor( author );
            testGetAllAuthors( allAuthors );
        }
    }
    
    private void testGetAllAuthors( List<Author> expAuthors )
    {
        List<Author>    actAuthors  = dbServer.getAllAuthors();
        assertEquals( expAuthors.size(), actAuthors.size() );
        
        int size    = expAuthors.size();
        for ( int inx = 0 ; inx < size ; ++inx )
        {
            assertTrue( actAuthors.contains( expAuthors.get( inx ) ) );
            assertTrue( expAuthors.contains( actAuthors.get( inx ) ) );
        }
    }
    
    @Test
    void testGetAuthorInt()
    {
        List<Author>    expAuthors  = getThreeAuthors();
        for ( Author expAuthor : expAuthors )
        {
            int     ident       = expAuthor.getIdent().getAsInt();
            Author  actAuthor   = dbServer.getAuthor( ident );
            assertNotNull( actAuthor );
            assertEquals( expAuthor, actAuthor );
        }
        
        // Negative case
    }

    /**
     * Make <em>n</em> authors; verify getAllAuthors works.
     * Delete authors one at a time;
     * verify getAllAuthors returns the correct list of authors.
     */
    @Test
    void testGetAuthorList()
    {
        List<Author>    expAuthors  = new ArrayList<>();
        for ( int inx = 0 ; inx < 10 ; ++inx )
        {
            // passing inx ensures that each author 
            // gets a different number of comments.
            Author  author  = getUniqueAuthor( inx );
            expAuthors.add( author );
            dbServer.insertAuthor( author );
        }
        
        while ( !expAuthors.isEmpty() )
        {
            List<Author>    actAuthors  = dbServer.getAllAuthors();
            assertEquals( expAuthors, actAuthors );
            
            Author  toDelete    = expAuthors.remove( expAuthors.size() / 2 );
            dbServer.deleteAuthor( toDelete );
        }
        
        List<Author>    actAuthors  = dbServer.getAllAuthors();
        assertNotNull( actAuthors );
        assertTrue( actAuthors.isEmpty() );
    }

    private void getComments( int commentCount, Author author )
    {
        author.setComments( new ArrayList<>() );
        assertEquals( 0, author.getComments().size() );
        addComments( commentCount, author );
    }

    /**
     * Add comments to an authors list of comments.
     * The difference between this and getComments is that
     * the author's list of comments is not cleared first.
     * @param commentCount
     * @param author
     */
    private void addComments( int commentCount, Author author )
    {
        for ( int count = 0 ; count < commentCount ; ++count )
        {
            String  nextComment = 
                String.format( "%04d - %s", ++nextCommentPrefix, defComment );
            Comment comment     = new Comment( AUTHOR_TYPE, nextComment );
            author.addComment( comment );
        }
//        printCollection( author.getComments() );
    }
    
    /**
     * Add three authors to the AUTORS table.
     * Return a list containing the authors, sorted by row ID.
     * Two of the authors will have a non-zero
     * number of comments, and one will have no comments at all.
     * 
     * @return list of three author, added to the database and sorted
     *         as discussed above.
     */
    private List<Author> getThreeAuthors()
    {
        List<Author>    authors = new ArrayList<>();
        int             base    = 5;
        
        for ( int count : new int[] { base, base / 2, 0 } )
        {
            Author  author  = getUniqueAuthor( count );
            dbServer.insertAuthor( author );
            authors.add(author);
        }
        authors.sort( sortByIdent );
        
        return authors;
    }
    
    /**
     * Generate an Author object with a unique value.
     * The generated author is guaranteed do be unique
     * only among those authors generated by this method.
     * 
     * @param   numComments the number of comments to generate
     *          for the target author
     * 
     * @return a unique Author object.
     */
    private Author getUniqueAuthor( int numComments )
    {
        String      prefix      = String.format( "%4d", ++nextAuthorPrefix );
        final int   numNames    = allListNames.length;
        int         nextList    = nextAuthorPrefix % numNames;
        
        String      name        = prefix + defName;
        int         lastCount   = defLastCount + nextAuthorPrefix;
        int         currCount   = defCurrentCount + nextAuthorPrefix;
        LocalDate   creDate     = defCreationDate.plusDays( nextAuthorPrefix );
        LocalDate   modDate     = defModifyDate.plusDays( nextAuthorPrefix );
        String      list        = allListNames[nextList];

        Author      author      = new Author( name );
        author.setLastCount( lastCount );
        author.setCurrentCount( currCount );
        author.setCreationDate( creDate );
        author.setModifyDate( modDate );
        author.setListName( list );
        getComments( numComments, author );

        return author;
    }
    
//    private void printCollection( Collection<?> coll )
//    {
//        coll.stream().forEach( System.out::println );
//    }
}
