package kcls_manager.database;

import static kcls_manager.database.DBConstants.AUTHORS_TABLE_NAME;
import static kcls_manager.database.DBConstants.COMMENTS_TABLE_NAME;
import static kcls_manager.database.DBConstants.LISTS_TABLE_NAME;
import static kcls_manager.database.DBConstants.TEST_DB_URL;
import static kcls_manager.database.DBConstants.TITLES_TABLE_NAME;
import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.TITLE_TYPE;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kcls_manager.main.Author;
import kcls_manager.main.Comment;
import kcls_manager.main.DataManager;
import kcls_manager.main.KCLSException;
import kcls_manager.main.KCLSList;
import kcls_manager.main.LibraryItem;
import kcls_manager.main.Title;
import kcls_manager.main.Utils;
import util.AuthorFactory;
import util.CommentFactory;
import util.TestUtils;
import util.TitleFactory;

class DBServerTest
{
    private static final String loggerName  = DBServerTest.class.getName();
    private static final Logger logger      = Logger.getLogger( loggerName );
    
    /** Lists associated with Titles */
    private static final String[]   allTitleLists   =
    { "Title List 1", "Title List 2", "Title List 3", "Title List 4" };
    private static final String     defTitleList    = allTitleLists[0];
    
    /** Lists associated with Author */
    private static final String[]   allAuthorLists =
    { "Author List 1", "Author List 2", "Author List 3", "Author List 4" };
    private static final String     defAuthorList   = allAuthorLists[0];
    private static final String     defAuthorName   = "Author, Default";
    
    private static DBServer dbServer;
    
    private TitleFactory    titleFactory;
    private AuthorFactory   authorFactory;
    private CommentFactory  commentFactory;
    
    @BeforeAll
    static void setUpBeforeClass() throws Exception
    {
        TestUtils.loggingInit();
    }

    @BeforeEach
    void setUp() throws Exception
    {
        dbServer  = TestUtils.getDBServer();
        dbServer.truncateTable( AUTHORS_TABLE_NAME );
        dbServer.truncateTable( TITLES_TABLE_NAME );
        dbServer.truncateTable( LISTS_TABLE_NAME );
        dbServer.truncateTable( COMMENTS_TABLE_NAME );
        
        insertLists( allTitleLists, TITLE_TYPE );
        insertLists( allAuthorLists, AUTHOR_TYPE );
        
        titleFactory = new TitleFactory();
        authorFactory = new AuthorFactory();
        commentFactory = new CommentFactory();
        
        Author  author  = getUniqueAuthor( 0 );
        author.setAuthor( defAuthorName );
        dbServer.insertAuthor( author );
    }
    
    private static void insertLists( String[] listNames, int type )
    {
        final String    fmtString   = "Inserted list: %s\"%s\"";
        String  listType    = type == TITLE_TYPE ? "Titles " : "Authors";
        
        for ( String listName : listNames )
        {
            String      msg     = 
                String.format( fmtString, listType, listName );
            KCLSList    list    = new KCLSList( type, listName );
            dbServer.insertList( list );
            logger.info( msg );
        }

    }

    @AfterEach
    void tearDown() throws Exception
    {
        DataManager.closeConnection();
    }

    /**
     * Exercises the default constructor.
     */
    @Test
    void testDBServer()
    {
//        DataManager.closeConnection();
//        dbServer    = DataManager.getDBServer( TEST_DB_URL );
//        Class<KCLSException>    excClass    = KCLSException.class;
//        assertThrows( excClass, () -> new DBServer() );
    }

    /**
     * Exercises the constructor that requires just a URL
     */
    @Test
    void testDBServerString()
    {
        DataManager.closeConnection();
        dbServer    = DataManager.getDBServer( TEST_DB_URL );
    }

    @Test
    void testDBServerStringStringString()
    {
        // TODO find a better way to test constructors
        // for now, just make sure it doesn't crash
        // new  DBServer( TEST_DB_URL, "name", "password" );
    }

    @Test
    void testGetSurrenderPreparedStatement()
    {
        final int genKeys   = Statement.RETURN_GENERATED_KEYS;
        final int noGenKeys = Statement.NO_GENERATED_KEYS;
        
        String  sql = "SELECT * from " + LISTS_TABLE_NAME;
        PreparedStatement   statement1  =
            dbServer.getPreparedStatement(sql, genKeys);
        PreparedStatement   statement2  =
            dbServer.getPreparedStatement( sql, noGenKeys );
        
        dbServer.surrenderPreparedStatement( statement1 );
        dbServer.surrenderPreparedStatement( statement2 );
    }

    @Test
    void testTruncateTable()
    {
        // Insert record
        Comment comment = getUniqueComment( TITLE_TYPE );
        dbServer.insertComment( comment );
        
        // verify record present
        OptionalInt optIdent    = comment.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        Comment temp            = dbServer.getComment( intIdent );
        assertEquals( comment, temp );
        
        // truncate table
        dbServer.truncateTable( COMMENTS_TABLE_NAME );
        
        // verify record gone
        temp = dbServer.getComment( intIdent );
        assertNull( temp ); 
    }

    @Test
    void testInsertGetComment()
    {
        Comment comment = getUniqueComment( TITLE_TYPE );
        dbServer.insertComment( comment );
        
        OptionalInt optIdent    = comment.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        Comment temp            = dbServer.getComment( intIdent );
        assertEquals( comment, temp );
    }

    @Test
    void testUpdateComment()
    {
        Comment comment = getUniqueComment( TITLE_TYPE );
        dbServer.insertComment( comment );
        
        OptionalInt optIdent    = comment.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        Comment temp            = dbServer.getComment( intIdent );
        assertEquals( comment, temp );
        
        comment.setText( comment.getText() + "###" );
        dbServer.updateComment( comment );
        temp = dbServer.getComment( intIdent );
        assertEquals( comment, temp );
    }

    @Test
    void testDeleteComment()
    {
        Comment comment = getUniqueComment( TITLE_TYPE );
        dbServer.insertComment( comment );
        
        OptionalInt optIdent    = comment.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        Comment temp            = dbServer.getComment( intIdent );
        assertEquals( comment, temp );
        
        dbServer.deleteComment( comment );
        temp = dbServer.getComment( intIdent );
        assertNull( temp );
    }

    @Test
    void testDeleteCommentsFor()
    {
        // populate database
        List<Title>     allTitles   = new ArrayList<>();
        List<Author>    allAuthors  = new ArrayList<>();
        for ( int inx = 0 ; inx < 3 ; ++inx )
        {
            Title   title   = getUniqueTitle( 5, defAuthorName );
            dbServer.insertTitle( title );
            allTitles.add( title );
            
            Author  author  = getUniqueAuthor( 5 );
            dbServer.insertAuthor( author );
            allAuthors.add( author );
        }
        
        // verify database populated
        for ( Title title : allTitles )
        {
            Title   temp    = new Title( title );
            dbServer.getCommentsFor( title );
            assertEquals( title, temp );
        }
        
        for ( Author author : allAuthors )
        {
            Author  temp    = new Author( author );
            dbServer.getCommentsFor( temp );
            assertEquals( author, temp );
        }
        
        int     titleCount  = allTitles.size();
        int     authorCount = allAuthors.size();
        int     count = titleCount > authorCount ? titleCount: authorCount;
        
        for ( int inx = count - 1 ; inx >= 0 ; --inx )
        {
            List<Comment>   expComments = new ArrayList<>();
            if ( inx < allTitles.size() )
            {
                Title   title   = allTitles.remove( inx );
                dbServer.deleteCommentsFor( title );
            }
            if ( inx < allAuthors.size() )
            {
                Author  author  = allAuthors.remove( inx );
                dbServer.deleteCommentsFor( author );
            }
            for ( Title title : allTitles )
                expComments.addAll( title.getComments() );
            for ( Author author : allAuthors )
                expComments.addAll( author.getComments() );
            
            Set<Comment>    actComments = dbServer.getAllComments();
            assertTrue( Utils.equals( expComments, actComments) );
        }
    }

    @Test
    void testGetComment()
    {
        int             commentCount    = 5;
        List<Comment>   expComments     = new ArrayList<>();
        IntStream.range( 0,  commentCount ).forEach( i -> {
            Comment comment = getUniqueComment( TITLE_TYPE );
            expComments.add( comment );
            dbServer.insertComment( comment ); 
            }
        );
        for ( Comment   expComment : expComments )
        {
            OptionalInt optIdent    = expComment.getIdent();
            assertTrue( optIdent.isPresent() );
            int         intIdent    = optIdent.getAsInt();
            Comment     actComment  = dbServer.getComment( intIdent );
            assertEquals( expComment, actComment );
        }
    }

    @Test
    void testGetAllComments()
    {
        int             commentCount    = 5;
        List<Comment>   expComments     = new ArrayList<>();
        IntStream.range( 0,  commentCount ).forEach( i -> {
            Comment comment = getUniqueComment( TITLE_TYPE );
            expComments.add( comment );
            dbServer.insertComment( comment ); 
            }
        );
        
        Set<Comment>    actComments = dbServer.getAllComments();
        assertTrue( Utils.equals( expComments, actComments ) );
    }

    @Test
    void testSynchronizeCommentsForAuthor()
    {
        dbServer.truncateTable( AUTHORS_TABLE_NAME );
        int             authorCount     = 5;
        int             commentCount    = 3;
        List<Author>    expAuthors     = insertAuthors( authorCount, commentCount );
        List<Author>    actAuthors     = dbServer.getAllAuthors();
        assertListsEqual( expAuthors, actAuthors );
        
        for ( Author expAuthor : expAuthors )
        {
            List<Comment>   comments    = new ArrayList<>( expAuthor.getComments() );
            comments.remove( 0 );
            Comment oldComment  = comments.get( 1 );
            String  oldText     = oldComment.getText();
            oldComment.setText( oldText + "###" );
            comments.add( getUniqueComment( AUTHOR_TYPE ) );
            expAuthor.setComments( comments );
            dbServer.synchronizeCommentsFor( expAuthor );
        }
        
        actAuthors       = dbServer.getAllAuthors();
        assertListsEqual( expAuthors, actAuthors );
    }

    @Test
    void testInsertGetCommentsForAuthor()
    {
        int             authorCount     = 3;
        int             commentCount    = 5;
        List<Author>    expAuthors      = new ArrayList<>();
        IntStream.range( 0, authorCount ).forEach( i ->
            expAuthors.add( getUniqueAuthor( commentCount ) )
        );
        
        for ( Author author : expAuthors )
            dbServer.insertAuthor( author );
        
        for ( Author expAuthor : expAuthors )
        {
            Author   actAuthor    = new Author( expAuthor );
            dbServer.getCommentsFor( actAuthor );
            assertEquals( expAuthor, actAuthor );
        }
    }

    @Test
    void testInsertGetCommentsForTitle()
    {
        int         titleCount      = 3;
        int         commentCount    = 5;
        List<Title> expTitles       = new ArrayList<>();
        IntStream.range( 0, titleCount ).forEach( i -> {
            Title   title   = getUniqueTitle( 0, defAuthorName );
            dbServer.insertTitle( title );
            expTitles.add( title  );
            IntStream.range( 0, commentCount ).forEach( j ->
                title.addComment( getUniqueComment( TITLE_TYPE ) ) );
            dbServer.insertCommentsFor( title );
            }
        );
        
        for ( Title expTitle : expTitles )
        {
            Title   actTitle    = new Title( expTitle );
            actTitle.clearComments();
            dbServer.getCommentsFor( actTitle );
            
            Set<Comment>    expComments = expTitle.getComments();
            Set<Comment>    actComments = actTitle.getComments();
            assertCollectionsEqual( expComments, actComments );
        }
    }

    @Test
    void testSynchronizeCommentsForTitle()
    {
        int         titleCount      = 5;
        int         commentCount    = 3;
        List<Title> expTitles       = insertTitles( titleCount, commentCount );
        List<Title> actTitles       = dbServer.getAllTitles();
        assertListsEqual( expTitles, actTitles );
        
        for ( Title expTitle : expTitles )
        {
            List<Comment>   comments    = new ArrayList<>( expTitle.getComments() );
            comments.remove( 0 );
            Comment oldComment  = comments.get( 1 );
            String  oldText     = oldComment.getText();
            oldComment.setText( oldText + "###" );
            comments.add( getUniqueComment( TITLE_TYPE ) );
            expTitle.setComments( comments );
            dbServer.synchronizeCommentsFor( expTitle );
        }
        
        actTitles       = dbServer.getAllTitles();
        assertListsEqual( expTitles, actTitles );
    }

    @Test
    void testGetCommentsForAuthor()
    {
        int             authorCount     = 3;
        int             commentCount    = 5;
        List<Author>    expAuthors      = new ArrayList<>();
        
        for ( int inx = 0 ; inx < authorCount ; ++inx )
        {
            Author  author  = getUniqueAuthor( commentCount );
            dbServer.insertAuthor( author );
            expAuthors.add( author );
        }
        
        for ( Author expAuthor : expAuthors )
        {
            OptionalInt optIdent    = expAuthor.getIdent();
            assertTrue( optIdent.isPresent() );
            int     intIdent        = optIdent.getAsInt();
            Author  actAuthor       = dbServer.getAuthor( intIdent );
            assertEquals( expAuthor, actAuthor );
        }
    }

    @Test
    void testUpdateList()
    {
        String      listName1   = "Dialog Title #1";
        String      listName2   = "Dialog Title #2";
        int         listType1   = TITLE_TYPE;
        int         listType2   = AUTHOR_TYPE;
        KCLSList    expList = new KCLSList( listType1, listName1 );
        dbServer.insertList( expList );
        
        OptionalInt optIdent    = expList.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        KCLSList    actList = dbServer.getList( intIdent );
        assertEquals( expList, actList );
        
        expList.setDialogTitle( listName2 );
        expList.setListType( listType2 );
        dbServer.updateList( expList );
        actList = dbServer.getList( intIdent );
        assertEquals( expList, actList );
    }

    @Test
    void testInsertList()
    {
        String      listName    = "Dialog Title #1";
        int         listType    = TITLE_TYPE;
        KCLSList    expList = new KCLSList( listType, listName );
        dbServer.insertList( expList );
        
        OptionalInt optIdent    = expList.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        KCLSList    actList = dbServer.getList( intIdent );
        assertEquals( expList, actList );
    }

    @Test
    void testDeleteListKCLSList()
    {
        String      listName1   = "Dialog Title #1";
        int         listType1   = TITLE_TYPE;
        KCLSList    expList = new KCLSList( listType1, listName1 );
        dbServer.insertList( expList );
        
        OptionalInt optIdent    = expList.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        KCLSList    actList = dbServer.getList( intIdent );
        assertEquals( expList, actList );
        
        dbServer.deleteList( expList );
        actList = dbServer.getList( intIdent );
        assertNull( actList );
    }

    @Test
    void testDeleteListInt()
    {
        String      listName1   = "Dialog Title #1";
        int         listType1   = TITLE_TYPE;
        KCLSList    expList = new KCLSList( listType1, listName1 );
        dbServer.insertList( expList );
        
        OptionalInt optIdent    = expList.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        KCLSList    actList = dbServer.getList( intIdent );
        assertEquals( expList, actList );
        
        dbServer.deleteList( intIdent );
        actList = dbServer.getList( intIdent );
        assertNull( actList );
    }

    @Test
    void testGetAllLists()
    {
        List<String>    expListNames    = new ArrayList<>();
        expListNames.addAll( Arrays.asList( allTitleLists ) );
        expListNames.addAll( Arrays.asList( allAuthorLists ) );
        List<KCLSList>  allLists        = dbServer.getAllLists();
        assertEquals( expListNames.size(), allLists.size() );
        for ( KCLSList list : allLists )
        {
            assertTrue( expListNames.contains( list.getDialogTitle() ) );
        }
    }

    @Test
    void testGetTitleLists()
    {
        List<String>    expListNames    = Arrays.asList( allTitleLists );
        List<KCLSList>  titleLists      = dbServer.getTitleLists();
        assertEquals( expListNames.size(), titleLists.size() );
        for ( KCLSList  list : titleLists )
        {
            assertEquals( TITLE_TYPE, list.getListType() );
            assertTrue( expListNames.contains( list.getDialogTitle() ) );
        }
    }

    @Test
    void testGetAuthorLists()
    {
        List<String>    expListNames    = Arrays.asList( allAuthorLists );
        List<KCLSList>  authorLists     = dbServer.getAuthorLists();
        assertEquals( expListNames.size(), authorLists.size() );
        for ( KCLSList  list : authorLists )
        {
            assertEquals( AUTHOR_TYPE, list.getListType() );
            assertTrue( expListNames.contains( list.getDialogTitle() ) );
        }
    }

    @Test
    void testGetListID()
    {
        int             listCount   = 5;
        List<KCLSList>  allLists    = new ArrayList<>();
        for ( int inx = 0 ; inx < listCount ; ++inx )
        {
            String      listName    = "list name " + inx;
            KCLSList    list        = new KCLSList( TITLE_TYPE, listName );
            dbServer.insertList( list );
            allLists.add( list );
        }
        
        for ( KCLSList expList : allLists )
        {
            OptionalInt optIdent    = expList.getIdent();
            int         intIdent    = optIdent.getAsInt();
            KCLSList    actList     = dbServer.getList( intIdent );
            assertEquals( expList, actList );
        }
    }

    @Test
    void testGetList()
    {
        String      listName1   = "Dialog Title #1";
        int         listType1   = TITLE_TYPE;
        KCLSList    expList = new KCLSList( listType1, listName1 );
        dbServer.insertList( expList );
        
        OptionalInt optIdent    = expList.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        KCLSList    actList = dbServer.getList( intIdent );
        assertEquals( expList, actList );
    }

    @Test
    void testInsertAuthor()
    {
        Author      expAuthor   = getUniqueAuthor( 5 );
        dbServer.insertAuthor( expAuthor );
        OptionalInt optIdent    = expAuthor.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        Author      actAuthor   = dbServer.getAuthor( intIdent );
        assertEquals( expAuthor, actAuthor );
    }

    @Test
    void testUpdateAuthor()
    {
        String      listName1   = allAuthorLists[0];
        String      listName2   = allAuthorLists[1];
        Author      expAuthor   = getUniqueAuthor( 5 );
        expAuthor.setListName( listName1 );
        
        dbServer.insertAuthor( expAuthor );
        OptionalInt optIdent    = expAuthor.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        Author      actAuthor   = dbServer.getAuthor( intIdent );
        assertEquals( expAuthor, actAuthor );
        
        String  text    = expAuthor.getAuthor();
        String  newText = text + "#####";
        expAuthor.setAuthor( newText );
        expAuthor.setListName( listName2 );
        dbServer.updateAuthor( expAuthor );
        actAuthor = dbServer.getAuthor( intIdent );
        assertEquals( expAuthor, actAuthor );
    }

    @Test
    void testDeleteAuthor()
    {
        Author      expAuthor   = getUniqueAuthor( 5 );
        dbServer.insertAuthor( expAuthor );
        OptionalInt optIdent    = expAuthor.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        Author      actAuthor   = dbServer.getAuthor( intIdent );
        assertEquals( expAuthor, actAuthor );
        
        dbServer.deleteAuthor( expAuthor );
        actAuthor = dbServer.getAuthor( intIdent );
        assertNull( actAuthor );
    }

    @Test
    void testGetAuthorIDForName()
    {
        int             authorCount     = 5;
        int             commentCount    = 5;
        List<Author>    expAuthors      = insertAuthors( authorCount, commentCount );
        for ( Author expAuthor : expAuthors )
        {
            String      name        = expAuthor.getAuthor();
            OptionalInt optIdent    = expAuthor.getIdent();
            assertTrue( optIdent.isPresent() );
            int         expIdent    = optIdent.getAsInt();
            int         actIdent    = dbServer.getAuthorIDForName( name );
            assertEquals( expIdent, actIdent );
        }
    }

    @Test
    void testGetAuthor()
    {
        Author  author  = getUniqueAuthor( 5 );
        dbServer.insertAuthor( author );
        
        OptionalInt optIdent    = author.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        
        Author  temp    = dbServer.getAuthor( intIdent );
        assertEquals( author, temp );
    }

    @Test
    void testGetAllAuthors()
    {
        dbServer.truncateTable( AUTHORS_TABLE_NAME );
        int             authorCount     = 3;
        int             commentCount    = 5;
        List<Author>    expAuthors      = new ArrayList<>();
        IntStream.range( 0, authorCount ).forEach( i ->
            expAuthors.add( getUniqueAuthor( commentCount) )
        );
        
        for ( Author author : expAuthors )
            dbServer.insertAuthor( author );
        
        List<Author>    actAuthors      = dbServer.getAllAuthors();
        assertListsEqual( expAuthors, actAuthors );
    }

    @Test
    void testInsertTitle()
    {
        Title   title   = getUniqueTitle( 5, defAuthorName );
        dbServer.insertTitle( title );
        
        OptionalInt optIdent    = title.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        Title       temp        = dbServer.getTitle( intIdent );
        assertEquals( title, temp );
    }

    @Test
    void testUpdateTitle()
    {
        Title   title   = getUniqueTitle( 5, defAuthorName );
        dbServer.insertTitle( title );
        
        Set<Comment>    setComments     = title.getComments();
        List<Comment>   listComments    = new ArrayList<>( setComments );
        listComments.remove( 0 );
        listComments.add( getUniqueComment( TITLE_TYPE) );
        
        String          text            = title.getTitle();
        title.setTitle( text + "###" );
        dbServer.updateTitle( title );
        
        OptionalInt optIdent    = title.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        Title       temp        = dbServer.getTitle( intIdent );
        assertEquals( title, temp );
    }

    @Test
    void testDeleteTitle()
    {
        Title   title   = getUniqueTitle( 5, defAuthorName );
        dbServer.insertTitle( title );
        
        OptionalInt optIdent    = title.getIdent();
        assertTrue( optIdent.isPresent() );
        int         intIdent    = optIdent.getAsInt();
        Title       temp        = dbServer.getTitle( intIdent );
        assertEquals( title, temp );
        
        dbServer.deleteTitle( title );
        temp = dbServer.getTitle( intIdent );
        assertNull( temp );
    }

    @Test
    void testGetTitle()
    {
        int         titleCount      = 3;
        int         commentCount    = 5;
        List<Title> expTitles       = insertTitles( titleCount, commentCount );
        for ( Title expTitle : expTitles )
        {
            OptionalInt optIdent    = expTitle.getIdent();
            int         intIdent    = optIdent.getAsInt();
            Title       actTitle    = dbServer.getTitle( intIdent );
            assertEquals( expTitle, actTitle );
        }
    }

    @Test
    void testGetAllTitles()
    {
        int         titleCount      = 3;
        int         commentCount    = 5;
        List<Title> expTitles       = insertTitles( titleCount, commentCount );
        List<Title> actTitles       = dbServer.getAllTitles();
        assertListsEqual( expTitles, actTitles );
//        assertTrue( Utils.equals( expTitles, actTitles ) );
    }

    @Test
    void testGetTitlesForAuthor()
    {
        int                     authorCount     = 3;
        int                     commentCount    = 5;
        int                     titleCount      = 5;
        List<Author>            allAuthors      = 
            insertAuthors( authorCount, commentCount );
        Map<String,List<Title>> authorMap       = new HashMap<>();
        for ( Author author : allAuthors )
        {
            String      authorName  = author.getAuthor();
            List<Title> titles      = new ArrayList<>();
            
            for ( int inx = 0 ; inx < titleCount ; ++inx )
            {
                Title   title   = getUniqueTitle( 0 );
                title.setAuthor( authorName );
                dbServer.insertTitle( title );
                titles.add( title );
            }
            authorMap.put( authorName, titles );
        }
        
        for ( Author author : allAuthors )
        {
            String      authorName  = author.getAuthor();
            List<Title> expTitles   = authorMap.get( authorName );
            List<Title> actTitles   = dbServer.getTitlesForAuthor( author );
            assertListsEqual( expTitles, actTitles );
        }
    }

    @Test
    void testGetTitlesForList()
    {
        int                     titleCount      = 3;
        int                     commentCount    = 5;
        Map<String,List<Title>> listTitleMap    = new HashMap<>();
        for ( String listName : allTitleLists )
        {
            List<Title> expTitles       = new ArrayList<>();
            for ( int inx = 0 ; inx < titleCount ; ++inx )
            {
                Title   title   = getUniqueTitle( commentCount );
                title.setListName( listName );
                dbServer.insertTitle( title );
                expTitles.add( title );
            }
            listTitleMap.put( listName, expTitles );
        }
        Set<String> allListNames    = listTitleMap.keySet();
        for ( String listName : allListNames )
        {
            List<Title> expTitles   = listTitleMap.get( listName );
            List<Title> actTitles   = dbServer.getTitlesForList( listName );
            assertEquals( expTitles, actTitles );
        }
    }

    @Test
    void testGetAuthorsForList()
    {
        dbServer.truncateTable( AUTHORS_TABLE_NAME );
        int                     authorCount     = 3;
        int                     commentCount    = 5;
        Map<String,List<Author>> listAuthorMap  = new HashMap<>();
        for ( String listName : allAuthorLists )
        {
            List<Author> expAuthors       = new ArrayList<>();
            for ( int inx = 0 ; inx < authorCount ; ++inx )
            {
                Author   author   = getUniqueAuthor( commentCount );
                author.setListName( listName );
                dbServer.insertAuthor( author );
                expAuthors.add( author );
            }
            listAuthorMap.put( listName, expAuthors );
        }
        Set<String> allListNames    = listAuthorMap.keySet();
        for ( String listName : allListNames )
        {
            List<Author> expAuthors   = listAuthorMap.get( listName );
            List<Author> actAuthors   = dbServer.getAuthorsForList( listName );
            assertListsEqual( expAuthors, actAuthors );
        }
    }

    @Test
    void testCloseConnection()
    {
        DataManager.closeConnection();
    }

    @Test
    void testDumpTable()
    {
        List<KCLSList>  actLists    = dbServer.getAllLists();
        String          path        = dbServer.dumpTable( LISTS_TABLE_NAME );
        File            file        = new File( path );
        List<String>    lines       = null;
        try ( FileReader fileReader = new FileReader( file );
              BufferedReader reader = new BufferedReader( fileReader );
            )
        {
            lines = reader.lines().collect( Collectors.toList() );
        }
        catch ( IOException exc )
        {
            fail( "unable to read: " + path, exc );
        }
        
        assertEquals( actLists.size(), lines.size() );
        for ( KCLSList list : actLists )
            assertContainsListName( list, lines );
    }
    
    /**
     * Exercise paths that throw exceptions
     */
    @Test
    public void testGoWrong()
    {
      Class<KCLSException>    excClass    = KCLSException.class;
      
      // Invalid PreparedStatement
      assertThrows( excClass, () -> 
          dbServer.getPreparedStatement( "invalid sql", 0 ) );
      
      // Attempt to remove non-existent PreparedStatement
      String            sql         = "SELECT * from " + LISTS_TABLE_NAME;
      PreparedStatement statement   = 
          dbServer.getPreparedStatement( sql, 0 );
      dbServer.surrenderPreparedStatement( statement );
      assertThrows( excClass, () -> 
          dbServer.surrenderPreparedStatement( statement ) );
      
      // Attempt to truncate non-existent table
      assertThrows( excClass, () -> 
          dbServer.truncateTable( "not a table name"  ) );
      
      // Attempt to update/delete a non-existent:
      // ... comment
      // ... author
      // ... title
      // ... list
      Comment   comment = getUniqueComment( AUTHOR_TYPE );
      assertThrows( excClass, () -> 
          dbServer.updateComment( comment ) );
      assertThrows( excClass, () -> 
          dbServer.deleteComment( comment ) );
      
      Author    author  = getUniqueAuthor( 5 );
      assertThrows( excClass, () -> 
          dbServer.updateAuthor( author ) );
      assertThrows( excClass, () -> 
          dbServer.deleteAuthor( author ) );
      assertThrows( excClass, () -> 
          dbServer.getCommentsFor( author ) );
      assertThrows( excClass, () -> 
          dbServer.synchronizeCommentsFor( author ) );
      assertThrows( excClass, () -> 
          dbServer.insertCommentsFor( author ) );
      
      Title     title   = getUniqueTitle( 5 );
      assertThrows( excClass, () -> 
          dbServer.updateTitle( title ) );
      assertThrows( excClass, () -> 
          dbServer.deleteTitle( title ) );
      assertThrows( excClass, () -> 
          dbServer.getCommentsFor( title ) );
      assertThrows( excClass, () -> 
          dbServer.synchronizeCommentsFor( title ) );
      assertThrows( excClass, () -> 
          dbServer.insertCommentsFor( title ) );
      
      KCLSList  list    = new KCLSList( AUTHOR_TYPE, "some name" );
      assertThrows( excClass, () -> 
          dbServer.updateList( list ) );
      assertThrows( excClass, () -> 
          dbServer.deleteList( list ) );
}
    
    private void 
    assertContainsListName( KCLSList list, List<String> listLines )
    {
        String  name    = list.getDialogTitle();
        for ( String listLine : listLines )
            if ( listLine.contains( name ) )
                return;
        StringBuilder   bldr    = new StringBuilder();
        bldr.append( "List name \"" ).append( name )
            .append( "\" not found in: " ).append( listLines );
        fail( bldr.toString() );
    }
    
    /**
     * Assert that two lists of LibraryItems are equal,
     * as determined by Utils.equals(List,List).
     * 
     * @param <T>       the type of the lists
     * @param expList   the expected list of values
     * @param actList   the actual list of values
     * 
     * @see kcls_manager.main.Utils#equals(List, List)
     */
    private <T extends LibraryItem> void 
    assertListsEqual( List<T> expList, List<T> actList )
    {
        if ( !Utils.equals( expList, actList ) )
        {
            StringBuilder   bldr    = new StringBuilder();
            bldr.append( "expected: " ).append( expList )
                .append( " actual: " ).append( actList );
            fail( bldr.toString() );
        }
    }
    
    /**
     * Assert that two collections of Comments are equal,
     * as determined by Utils.equals(Collection,Collection).
     * 
     * @param expColl   the expected collection of comments
     * @param actColl   the actual collection of comments
     * 
     * @see kcls_manager.main.Utils#equals(Collection,Collection)
     */
    private void assertCollectionsEqual( 
        Collection<Comment> expColl,
        Collection<Comment> actColl 
    )
    {
        if ( !Utils.equals( expColl, actColl ) )
        {
            StringBuilder   bldr    = new StringBuilder();
            bldr.append( "expected: " ).append( expColl )
                .append( " actual: " ).append( actColl );
            fail( bldr.toString() );
        }
    }
    
    private Author getUniqueAuthor( int numComments )
    {
        Author  author  = authorFactory.getUniqueAuthor( numComments );
        author.setListName( defAuthorList );
        return author;
    }
    
    private Comment getUniqueComment( int type )
    {
        Comment comment = commentFactory.getUniqueComment( -1 );
        comment.setType( type );
        return comment;
    }
    
    private Title getUniqueTitle( int numComments, String authorName )
    {
        Title   title   = getUniqueTitle( numComments );
        title.setAuthor( authorName );
        return title;
    }

    private Title getUniqueTitle( int numComments )
    {
        Title   title   = titleFactory.getUniqueTitle( numComments );
        title.setListName( defTitleList );
        title.setAuthor( defAuthorName );
        return title;
    }
    
    /**
     * Create a list of titles, adding each title to the Titles table,
     * and return the list.
     * 
     * @param titleCount    the number of titles to create and insert
     * @param commentCount  the number of comments generated for each title
     * @return  a list of titles added to the Titles table
     */
    private List<Title> insertTitles( int titleCount, int commentCount )
    {
        List<Title> titles  = new ArrayList<>();
        for ( int inx = 0 ; inx < titleCount ; ++inx )
        {
            Title   title   = getUniqueTitle( commentCount );
            dbServer.insertTitle( title );
            titles.add( title );
        }
        return titles;
    }
    
    /**
     * Create a list of authors, adding each author to the Authors table,
     * and return the list.
     * 
     * @param authorCount    the number of authors to create and insert
     * @param commentCount  the number of comments generated for each author
     * @return  a list of authors added to the Authors table
     */
    private List<Author> insertAuthors( int authorCount, int commentCount )
    {
        List<Author> authors  = new ArrayList<>();
        for ( int inx = 0 ; inx < authorCount ; ++inx )
        {
            Author   author   = getUniqueAuthor( commentCount );
            dbServer.insertAuthor( author );
            authors.add( author );
        }
        return authors;
    }
}
