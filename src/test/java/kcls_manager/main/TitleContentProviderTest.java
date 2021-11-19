package kcls_manager.main;

import static kcls_manager.database.DBConstants.AUTHORS_TABLE_NAME;
import static kcls_manager.database.DBConstants.LISTS_TABLE_NAME;
import static kcls_manager.database.DBConstants.TITLES_TABLE_NAME;
import static kcls_manager.main.Constants.AUTHOR_NAME;
import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.TITLE_NAME;
import static kcls_manager.main.Constants.TITLE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kcls_manager.database.DBServer;
import util.TestUtils;
import util.TitleFactory;

class TitleContentProviderTest
{
    /** Lists of titles to test against */
    private static final String[]   titleListNames  =
    { "Title List 1", "Title List 2", "Title List 3" };
    
    /** Default title list name */
    private static final String     defTitleListName    = titleListNames[0];
    
    /** Count of lists of titles */
    private static final int        titleListNamesCount = 
        titleListNames.length;
    
    /** List of authors to test against */
    private static final String     authorListName  = "author list";
    
    /** Author names to test against */
    private static final String[]   authorNames     =
    { 
        "Author, Anne One", 
        "Author, Anne Two", 
        "Author, Anne Three", 
        "Author, Anne Four"
    };
    
    /** Count of author names */
    private static final int        authorNamesCount = authorNames.length;
    
    private static final Map<String,List<Title>>  listsMap    =
        new HashMap<>();
    
    /** For serially selecting list names */
    private int             listInx     = 0;
    
    /** For serially selecting author names */
    private int             authorInx     = 0;
    
    /** For generating titles to test against */
    private TitleFactory    titleFactory;
    
    @BeforeAll
    static void setUpBeforeClass() throws Exception
    {
        TestUtils.loggingInit( "kcls_test.log", false );
        DBServer    dbServer    = TestUtils.getDBServer();
        dbServer.truncateTable( TITLES_TABLE_NAME );
        dbServer.truncateTable( AUTHORS_TABLE_NAME );
        dbServer.truncateTable( LISTS_TABLE_NAME );
        
        for ( String listName : titleListNames )
        {
            KCLSList    list    = new KCLSList( TITLE_TYPE, listName );
            listsMap.put( listName, new ArrayList<>() );
            dbServer.insertList( list );
        }
        KCLSList    list    = new KCLSList( AUTHOR_TYPE, authorListName );
        dbServer.insertList( list );
        listsMap.put( authorListName, new ArrayList<>() );
        
        for ( String name : authorNames )
        {
            Author  author  = new Author( name, authorListName );
            dbServer.insertAuthor( author );
        }
    }

    @BeforeEach
    void setUp() throws Exception
    {
        DBServer    dbServer    = TestUtils.getDBServer();
        dbServer.truncateTable( TITLES_TABLE_NAME );
        titleFactory = new TitleFactory();
        Set<String>  listKeys    = listsMap.keySet();
        for ( String listName : listKeys )
            listsMap.get( listName ).clear();

        for ( int inx = 0 ; inx < 4 * titleListNames.length ; ++inx )
        {
            Title   title   = getUniqueTitle( inx );
            dbServer.insertTitle( title );
            listsMap.get( title.getListName() ).add( title );
        }
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    /**
     * Headings live in a hard-coded array inside of TitleContentProvider;
     * they are not set by the application.
     * Get the array and make sure it contains a few obvious headings.
     */
    @Test
    void testGetHeadings()
    {
        String      listName    = titleListNames[0];

        /* test with default column names */;
        TitleContentProvider    provider    =
            new TitleContentProvider( listName );
        Object[]    headingsArr = provider.getHeaders();
        
        // Just check for a few obvious characteristics...
        
        // there has to be at least one column for the title,
        // and a second, hidden column.
        assertTrue( headingsArr.length > 1 );
        assertTrue( Arrays.asList( headingsArr ).contains( TITLE_NAME ) );
        
        /* Test with explicit column names. */
        String[]    colNames    = { TITLE_NAME, AUTHOR_NAME, "hidden" };
        provider = new TitleContentProvider( listName, colNames );
        headingsArr = provider.getHeaders();
        assertEquals( colNames, headingsArr );
    }

    @Test
    void testGetDialogTitle()
    {
        String                  listName    = titleListNames[0];
        TitleContentProvider    provider    =
            new TitleContentProvider( listName );
        assertEquals( listName, provider.getDialogTitle() );
    }

//    @Test
//    void testTitleContentProvider()
//    {
//        fail("Not yet implemented");
//    }

    @Test
    void testGetDataColumn()
    {  
        String                  listName    = titleListNames[0];
        TitleContentProvider    provider    =
            new TitleContentProvider( listName );
        int                     dataColumn  = provider.getDataColumn();
        int                     lastInx     = 
            provider.getHeaders().length - 1;
        assertEquals( lastInx, dataColumn );
    }

    @Test
    void testGetAllListNames()
    {
        List<String>    actNames    = 
            TitleContentProvider.getAllListNames();
        for ( String name : actNames )
        {
            TitleContentProvider    provider    =
                new TitleContentProvider( name );
            System.out.println( provider.getDialogTitle() );
        }
        List<String>    expNames    = Arrays.asList( titleListNames );
        assertCollectionEquals( expNames, actNames );
    }

    @Test
    void testGetTitlesForList()
    {
        for ( String listName : titleListNames )
        {
            List<Title> actTitles   = 
                TitleContentProvider.getTitlesForList( listName );
            List<Title> expTitles   = listsMap.get( listName );
            TestUtils.assertListsEqual( expTitles, actTitles );
        }
    }

    @Test
    void testGetTitles()
    {
        for ( String listName : titleListNames )
        {
            TitleContentProvider    provider    =
                new TitleContentProvider( listName );
            List<Title>  actTitles  = provider.getTitles();
            List<Title>  expTitles  = listsMap.get( listName );
            TestUtils.assertListsEqual( expTitles, actTitles );
        }
    }

    @Test
    void testSetTitlesCollectionOfTitle()
    {
        String      listName    = titleListNames[0];
        List<Title> expList     = listsMap.get( listName );
        
        TitleContentProvider    provider    =
            new TitleContentProvider( listName );
        provider.setTitles( expList );
        
        List<Title> actList     = provider.getTitles();
        TestUtils.assertListsEqual( expList, actList );
        
    }

    @Test
    void testSetTitlesJTable()
    {
        TitleContentProvider    provider    = 
            new TitleContentProvider( defTitleListName );
        String[]        headings        = provider.getHeaders();
        int             headingCount    = headings.length;
        int             hiddenCol       = headingCount - 1;
        int             titlesCount     = 10;
        Object[][]      data            = new Object[titlesCount][headingCount];
        List<Title>     expTitles       = new ArrayList<>();
        System.out.println( Arrays.toString( headings ) );
        for ( int inx = 0 ; inx < titlesCount ; ++inx )
        {
            Title   title   = titleFactory.getUniqueTitle( 0 );
            for ( int jnx = 0 ; jnx < hiddenCol ; ++jnx )
                data[inx][jnx] = (char)('a' + jnx) + "";
            data[inx][hiddenCol] = title;
            expTitles.add( title );
            System.out.println( Arrays.toString( data[inx] ) );
        }
        
        JTable                  jTable      = new JTable( data, headings );
        provider.setTitles( jTable );
        List<Title>             actTitles   = provider.getTitles();
        TestUtils.assertListsEqual( expTitles, actTitles );
        
        ///////////////////////////////////
        // Test go-wrong path
        data[0][hiddenCol]  = new Object();
        Class<KCLSException>    clazz   = KCLSException.class;
        assertThrows( clazz, () -> provider.setTitles( jTable ) );
    }

    @Test
    void testIterator()
    {
        for ( String listName : titleListNames )
        {
            List<Title>             actTitles   = new ArrayList<>();
            TitleContentProvider    provider    =
                new TitleContentProvider( listName );
            int                     dataCol     = provider.getDataColumn();
            Iterator<Object[]>      iter        = provider.iterator();
            while ( iter.hasNext() )
            {
                Object[]    row = iter.next();
                actTitles.add( (Title)row[dataCol] );
            }
            
            List<Title> expTitles   = listsMap.get( listName );
            TestUtils.assertListsEqual( expTitles, actTitles );
        }
    }
    
    private void 
    assertCollectionEquals( Collection<?> coll1, Collection<?> coll2 )
    {
        assertEquals( coll1.size(), coll2.size() );
        assertTrue( coll1.containsAll( coll2 ) ); 
        assertTrue( coll2.containsAll( coll1 ) ); 
    }

    private Title getUniqueTitle( int commentCount )
    {
        Title   title   = titleFactory.getUniqueTitle( commentCount );
        title.setListName( titleListNames[listInx++ % titleListNamesCount] );
        title.setAuthor( authorNames[authorInx++ % authorNamesCount] );
        return title;
    }
}
