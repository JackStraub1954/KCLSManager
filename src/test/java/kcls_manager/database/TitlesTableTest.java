package kcls_manager.database;

import static kcls_manager.database.DBConstants.AUTHORS_TABLE_NAME;
import static kcls_manager.database.DBConstants.COMMENTS_TABLE_NAME;
import static kcls_manager.database.DBConstants.LISTS_TABLE_NAME;
import static kcls_manager.database.DBConstants.TITLES_TABLE_NAME;
import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.TITLE_TYPE;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kcls_manager.main.Author;
import kcls_manager.main.Comment;
import kcls_manager.main.KCLSException;
import kcls_manager.main.KCLSList;
import kcls_manager.main.Title;
import kcls_manager.main.Utils;
import util.TestUtils;
import util.TitleFactory;

class TitlesTableTest
{
    private static final String     defAuthor      = "Name, Author Anne";
    private static final String     authorName      = defAuthor;
    private static final String     authorListName  = "Author List Name";
    private static final Author     author          = 
        new Author( authorName, authorListName );
    
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
    
    /**
     * Comparator to sort lists of Authors by ident.
     */
    private final Comparator<Title> sortByIdent =
        (t1, t2) -> t1.getIdent().getAsInt() - t2.getIdent().getAsInt();
        
    /**
     *  Database connection
     */
    private DBServer    dbServer;
    
    /**
     * Handy-dandy default Title object for convenience of testing.
     */
    private Title       defTitle;
    
    /**
     * for generating unique Title objects
     */
    private TitleFactory    titleFactory;
    
    /**
     * Prefix for creating unique Titles.
     */
    private int         nextTitlePrefix;
    
    /**
     * Prefix for creating unique Authors.
     */
    private int         nextAuthorPrefix;

    @BeforeAll
    static void setUpBeforeClass() throws Exception
    {
        TestUtils.loggingInit( "kcls_test.log", false );
        DBServer    server  = TestUtils.getDBServer();
        server.truncateTable( AUTHORS_TABLE_NAME );
        server.truncateTable( LISTS_TABLE_NAME );
        for ( int inx = 0 ; inx < allListNames.length ; ++inx )
        {
            String      name    = allListNames[inx];
            KCLSList    list    = new KCLSList( TITLE_TYPE, name );
            server.insertList( list );
            allLists[inx] = list;
        }
        
        KCLSList    authorList  = new KCLSList( AUTHOR_TYPE, authorListName );
        server.insertList( authorList );
        server.insertAuthor( author );
    }

    @BeforeEach
    void setUp() throws Exception
    {
        dbServer = TestUtils.getDBServer();
        dbServer.truncateTable( TITLES_TABLE_NAME );
        dbServer.truncateTable( COMMENTS_TABLE_NAME );
        titleFactory = new TitleFactory();
        
        nextTitlePrefix = 0;
        nextAuthorPrefix = 0;
        defTitle = getUniqueTitle( 0 );
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testInsertTitle()
    {
        titleFactory.addComments( 5, defTitle );
        dbServer.insertTitle( defTitle );
        OptionalInt optIdent = defTitle.getIdent();
        assertTrue( optIdent.isPresent() );
        
        Title   actTitle    = dbServer.getTitle( optIdent.getAsInt() );
        assertEquals( defTitle, actTitle );
    }

    @Test
    void testUpdateTitle()
    {
        int         numItems    = 5;
        List<Title> expTitles   = new ArrayList<>();
        for ( int inx = 0 ; inx < numItems ; ++inx )
        {
            Title       title   = getUniqueTitle( 5 );
            dbServer.insertTitle( title );
            expTitles.add( title );
        }
        
        List<Title> actTitles   = dbServer.getAllTitles();
        assertTrue( Utils.equals( expTitles, actTitles ) );
        
        for ( Title title : expTitles )
        {
            Title   temp    = new Title( title );
            dbServer.getCommentsFor( temp );
            Set<Comment>    actComments = temp.getComments();
            Set<Comment>    expComments = title.getComments();
//            printByComment( expComments, actComments );
            assertTrue( Utils.equals( expComments, actComments ) );
            
            Comment newComment  = new Comment( TITLE_TYPE, "new comment..." );
            title.addComment( newComment );
            dbServer.updateTitle( title );
        }
        
        for ( Title title : expTitles )
        {
            Title   temp    = new Title( title );
            dbServer.getCommentsFor( temp );
            Set<Comment>    actComments = temp.getComments();
            Set<Comment>    expComments = title.getComments();
            printByComment( expComments, actComments );
            assertTrue( Utils.equals( expComments, actComments ) );
        }
    }
    
    private void printByComment(
        Collection<Comment> coll1, 
        Collection<Comment> coll2
    )
    {
        Comparator<Comment> sortByComment   = 
            (c1,c2) -> c1.getText().compareTo( c2.getText() );
        List<Comment>   list1   = new ArrayList<>( coll1 );
        List<Comment>   list2   = new ArrayList<>( coll2 );
        list1.sort( sortByComment );
        list2.sort( sortByComment );
        System.out.println( "***************" );
        System.out.println( list1 );
        System.out.println( list2 );
    }

    /**
     * Attempt invalid Title update.
     */
    @Test
    void testUpdateTitleGoWrong()
    {
        Class<KCLSException>    excClass    = KCLSException.class;
        assertThrows( excClass, () -> dbServer.updateTitle( defTitle ) );
    }

    @Test
    void testDeleteTitle()
    {
        int         numItems    = 5;
        List<Title> expTitles   = new ArrayList<>();
        for ( int inx = 0 ; inx < numItems ; ++inx )
        {
            Title       title   = getUniqueTitle( 5 );
            dbServer.insertTitle( title );
            expTitles.add( title );
        }
        expTitles.sort( sortByIdent );
        
        List<Title> actTitles   = dbServer.getAllTitles();
        actTitles.sort( sortByIdent );
        assertEquals( expTitles, actTitles );
        
        // Delete Titles one at a time;
        //     verify title has been deleted
        //     verify all comments associated with tile have been deleted
        while ( !expTitles.isEmpty() )
        {
            Title   title   = expTitles.remove( expTitles.size() - 1 );
            dbServer.deleteTitle( title );
            
            int     ident   = title.getIdent().getAsInt();
            Title   test    = dbServer.getTitle( ident );
            assertNull( test );
            
            Set<Comment>  comments = title.getComments();
            for ( Comment comment : comments )
            {
                Comment tempComment = 
                    dbServer.getComment( comment.getIdent().getAsInt() );
                assertNull( tempComment );
            }
        }
    }

    /**
     * Attempt invalid Title delete.
     */
    @Test
    void testDeleteTitleGoWrong()
    {
        Class<KCLSException>    excClass    = KCLSException.class;
        assertThrows( excClass, () -> dbServer.deleteTitle( defTitle ) );
    }

    @Test
    void testGetAllTitles()
    {
        List<Title> expTitles   = new ArrayList<>();
        for ( int inx = 0 ; inx < 5 ; ++inx )
        {
            Title       title   = getUniqueTitle( inx );
            dbServer.insertTitle( title );
            expTitles.add( title );
        }
        expTitles.sort( sortByIdent );
        
        List<Title> actTitles   = dbServer.getAllTitles();
        actTitles.sort( sortByIdent );
        assertEquals( expTitles, actTitles );
    }
    
    @Test
    void testGetTitle()
    {
        Title   titleNoAuthorName   = getUniqueTitle( 0 );
        titleNoAuthorName.setAuthor( "" );
        Title   titleWithAuthorName = getUniqueTitle( 0 );
        
        dbServer.insertTitle( titleWithAuthorName );
        dbServer.insertTitle( titleNoAuthorName );
        
        OptionalInt optIdent        = titleNoAuthorName.getIdent();
        int         ident           = optIdent.getAsInt();
        Title       actNoAuthorName = dbServer.getTitle( ident );
        assertEquals( titleNoAuthorName, actNoAuthorName );
        
        optIdent = titleWithAuthorName.getIdent();
        ident = optIdent.getAsInt();
        Title   actWithAuthorName   = dbServer.getTitle( ident );
        assertEquals( titleWithAuthorName, actWithAuthorName );
    }

    @Test
    void testGetTitleList()
    {
        Map<String,List<Title>> allTitleLists   = new HashMap<>();
        for ( String listName : allListNames )
        {
            int         numItems    = 3;
            List<Title> titles      = new ArrayList<>();
            for ( int inx = 0 ; inx < numItems ; ++inx )
            {
                Title       title   = getUniqueTitle( 5 );
                title.setListName( listName );
                titles.add( title );
                dbServer.insertTitle( title );
            }
            titles.sort( sortByIdent );
            allTitleLists.put( listName, titles );
        }

        for ( String listName : allListNames )
        {
            List<Title> actTitles   = dbServer.getTitlesForList( listName );
            actTitles.sort(sortByIdent);
            
            List<Title> expTitles   = allTitleLists.get( listName );
            assertEquals( expTitles, actTitles );
        }
    }
    
    /**
     * Set up three authors. 
     * Add titles for each author.
     * Validate target method.
     * ...
     * Set up author with no titles.
     * Validate target method returns empty list.
     */
    @Test
    public void testGetTitlesForAuthor()
    {
        Map<Author,List<Title>> authorMap   = new HashMap<>();
        int                     authorCount = 3;
        int                     titleCount  = 3;
        for ( int inx = 0 ; inx < authorCount ; ++inx )
        {
            Author      author      = getUniqueAuthor();
            String      authorName  = author.getAuthor();
            dbServer.insertAuthor( author );

            List<Title> titles      = new ArrayList<>();
            for ( int jnx = 0 ; jnx < titleCount ; ++jnx )
            {
                Title   title   = getUniqueTitle( 0 );
                title.setAuthor( authorName );
                titles.add( title );
                dbServer.insertTitle( title );
            }
            titles.sort( sortByIdent );
            authorMap.put( author, titles );
        }
        
        Set<Author> authors = authorMap.keySet();
        for ( Author author : authors )
        {
            List<Title> expTitles   = authorMap.get( author );
            
            // get by Author WITH ident
            List<Title> actTitles   = dbServer.getTitlesForAuthor( author );
            actTitles.sort(sortByIdent);
            assertEquals( expTitles, actTitles );
            
            // get by Author WITHOUT ident
            author.setIdent( OptionalInt.empty() );
            actTitles   = dbServer.getTitlesForAuthor( author );
            actTitles.sort(sortByIdent);
            assertEquals( expTitles, actTitles );
        }
        
        // Test for empty list return
        Author  author  = getUniqueAuthor();
        dbServer.insertAuthor( author );
        List<Title> actTitles   = dbServer.getTitlesForAuthor( author );
        assertTrue( actTitles.isEmpty() );
    }
    
    @Test
    public void testGetTitlesForAuthorGoWrong()
    {
        
    }
    
    @Test
    public void testGetWithoutAuthor()
    {
        defTitle.setAuthor( "" );
        dbServer.insertTitle( defTitle );
        
        int     ident           = defTitle.getIdent().getAsInt();
        Title   committedTitle  = dbServer.getTitle( ident );
        assertEquals( defTitle, committedTitle );
    }

    /**
     * Attempt to get titles for a list that doesn't exist.
     */
    @Test
    void testGetTitleListNonexistent()
    {
        List<Title> list    = dbServer.getTitlesForList( "no such list" );
        assertTrue( list.isEmpty() );
    }

    private Title getUniqueTitle( int numComments )
    {
        final int   numNames    = allListNames.length;
        int         nextList    = nextTitlePrefix % numNames;
        String      listName    = allListNames[nextList];
        Title       title       = titleFactory.getUniqueTitle( numComments );
        title.setAuthor( defAuthor );
        title.setListName( listName );;

        return title;
    }
    
    private Author getUniqueAuthor()
    {
        String      authorName  = 
            String.format( "%03d%s", ++nextAuthorPrefix, defAuthor );
        Author      author      = new Author( authorName, authorListName );
        return author;
    }

    /**
     * Add comments to a Title's list of comments.
     * The difference between this and getComments is that
     * the Title's list of comments is not cleared first.
     * 
     * @param commentCount  The number of comments to generate
     * @param title         The owner of the new comments
     */
//    private void addComments( int commentCount, Title title )
//    {
//        for ( int count = 0 ; count < commentCount ; ++count )
//        {
//            String  nextComment = 
//                String.format( "%04d - %s", ++nextCommentPrefix, defComment );
//            Comment comment     = new Comment( TITLE_TYPE, nextComment );
//            title.addComment( comment );
//        }
//    }
}
