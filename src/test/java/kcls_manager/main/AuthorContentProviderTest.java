package kcls_manager.main;

import static kcls_manager.database.DBConstants.AUTHORS_TABLE_NAME;
import static kcls_manager.database.DBConstants.LISTS_TABLE_NAME;
import static kcls_manager.main.Constants.AUTHOR_NAME;
import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.CREATION_DATE_NAME;
import static kcls_manager.main.Constants.CURRENT_COUNT_NAME;
import static kcls_manager.main.Constants.LAST_COUNT_NAME;
import static kcls_manager.main.Constants.LIST_NAME_NAME;
import static kcls_manager.main.Constants.MODIFY_DATE_NAME;
import static kcls_manager.main.Constants.RANK_NAME;
import static kcls_manager.main.Constants.RATING_NAME;
import static kcls_manager.main.Constants.SOURCE_NAME;
import static kcls_manager.main.Constants.TITLE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import util.AuthorFactory;
import util.TestUtils;

class AuthorContentProviderTest
{
    /** All possible column headers. */
    private static final String[]   allColumnHeaders =
    {
        RANK_NAME, 
        RATING_NAME,
        SOURCE_NAME,
//        COMMENTS_NAME,
        CREATION_DATE_NAME, 
        MODIFY_DATE_NAME,
        AUTHOR_NAME, 
        LIST_NAME_NAME,
        LAST_COUNT_NAME,
        CURRENT_COUNT_NAME,
        "hidden" 
    };

    /** Lists of authors to test against */
    private static final String[]   authorListNames  =
    { "Author List 1", "Author List 2", "Author List 3" };
    
    private static final String titleListName   = "Title List 1";

    private static final Map<String,Set<Author>>   listsMap    =
        new HashMap<>();
    
    private static final DBServer  dbServer    = TestUtils.getDBServer();
    
    private static AuthorFactory   authorFactory;

    @BeforeAll
    static void setUpBeforeClass() throws Exception
    {
        TestUtils.loggingInit( "kcls_test.log", false );
        authorFactory = new AuthorFactory();
        dbServer.truncateTable( AUTHORS_TABLE_NAME );
        dbServer.truncateTable( LISTS_TABLE_NAME );
        
        for ( String listName : authorListNames )
        {
            KCLSList    list    = new KCLSList( AUTHOR_TYPE, listName );
            listsMap.put( listName, new HashSet<>() );
            dbServer.insertList( list );
        }
        
        int nameCount   = authorListNames.length;
        int authorCount = 4 * nameCount;
        for ( int inx = 0 ; inx < authorCount ; ++inx )
        {
            String  listName    = authorListNames[inx % nameCount];
            Author  author      = authorFactory.getUniqueAuthor( 0 );
            author.setListName( listName );
            Set<Author> authors = listsMap.get( listName );
            authors.add( author );
            dbServer.insertAuthor( author );
        }
        
        KCLSList    titleList       = 
            new KCLSList( TITLE_TYPE, titleListName );
        dbServer.insertList( titleList );
        
        for ( String listName : authorListNames )
        {
            Set<Author> set = listsMap.get( listName );
            for ( Author author : set )
                System.out.println( author.getIdent() );
            System.out.println( "**********" );
        }
    }

    @BeforeEach
    void setUp() throws Exception
    {
        authorFactory = new AuthorFactory();
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetHeaders()
    {
        String                  listName    = authorListNames[0];
        AuthorContentProvider   provider    =
            new AuthorContentProvider( listName, allColumnHeaders );
        assertEquals( allColumnHeaders, provider.getHeaders() );
    }

    @Test
    void testGetDialogTitle()
    {
        for ( String listName : authorListNames )
        {
            AuthorContentProvider   provider    =
                new AuthorContentProvider( listName, allColumnHeaders );
            assertEquals( listName, provider.getDialogTitle() );
        }
    }

    @Test
    void testAuthorContentProviderString()
    {
        String                  listName    = authorListNames[0];
        AuthorContentProvider   provider    =
            new AuthorContentProvider( listName );
        Object[]                headers     = provider.getHeaders();
        List<Object>            headList    = List.of( headers );
        assertTrue( headList.contains( AUTHOR_NAME ) );
        assertTrue( headList.contains( RATING_NAME ) );
        assertTrue( headList.contains( CURRENT_COUNT_NAME ) );
    }

    @Test
    void testAuthorContentProviderStringStringArray()
    {
        for ( String listName : authorListNames )
        {
            AuthorContentProvider   provider    =
                new AuthorContentProvider( listName, allColumnHeaders );
            Set<Author>             expAuthors  = listsMap.get( listName );
            Set<Author>             actAuthors  = provider.getAuthors();
            Object[]                actHeaders  = provider.getHeaders();
            
            assertTrue( expAuthors.size() > 0 );
            
            assertTrue( Utils.equals( expAuthors, actAuthors ) );
            assertEquals( allColumnHeaders, actHeaders );
        }
    }

    @Test
    void testGetDataColumn()
    {
        AuthorContentProvider   provider    =
            new AuthorContentProvider( authorListNames[0], allColumnHeaders );
        int                     expIndex    = allColumnHeaders.length - 1;
        int                     actIndex    = provider.getDataColumnIndex();
        assertEquals( expIndex, actIndex );
     }

    @Test
    void testGetAllListNames()
    {
        List<String>    expNames    = List.of( authorListNames );
        List<String>    actNames    = 
            AuthorContentProvider.getAllListNames();
        assertTrue( expNames.size() > 0 );
        assertTrue( Utils.equals( expNames, actNames ) );
    }

    @Test
    void testGetAuthorsForList()
    {
        for ( String listName : authorListNames )
        {
            Set<Author> expAuthors  = listsMap.get( listName );
            Set<Author> actAuthors  = 
                AuthorContentProvider.getAuthorsForList( listName );
            assertTrue( expAuthors.size() > 0 );
            assertTrue( Utils.equals( expAuthors, actAuthors ) );
        }
    }

    @Test
    void testGetAuthors()
    {
        for ( String listName : authorListNames )
        {
            AuthorContentProvider   provider    =
                new AuthorContentProvider( listName );
            Set<Author> expAuthors  = listsMap.get( listName );
            Set<Author> actAuthors  = provider.getAuthors();
            assertTrue( expAuthors.size() > 0 );
            assertTrue( Utils.equals( expAuthors, actAuthors ) );
        }
    }

    @Test
    void testSetAuthorsCollectionOfAuthor()
    {
        int namesLen    = authorListNames.length;
        for ( int inx = 0 ; inx < namesLen ; ++inx )
        {
            // Create the provider instance using one list name,
            // then change the Author set using a different name.
            String  testingListName = authorListNames[inx];
            String  workingListName = authorListNames[(inx + 1) % namesLen];

            AuthorContentProvider   provider    =
                new AuthorContentProvider( workingListName );
            Set<Author> expAuthors  = listsMap.get( testingListName );
            assertTrue( expAuthors.size() > 0 );
            provider.setAuthors( expAuthors );
            Set<Author> actAuthors  = provider.getAuthors();
            
            // After setting/getting, the getAuthors() list should be
            // equal to the getAuthors() list, but not identical to it.
            assertFalse( expAuthors == actAuthors );
            assertTrue( Utils.equals( expAuthors, actAuthors ) );
        }
    }

    @Test
    void testSetAuthorsJTable()
    {
        int         numRows     = 5;
        int         numCols     = allColumnHeaders.length;
        Object[][]  data        = new Object[numRows][numCols];
        int         authorInx   = numCols - 1;
        Set<Author> expAuthors  = new HashSet<>();
        for ( int inx = 0 ; inx < numRows ; ++inx )
        {
            Author  author  = authorFactory.getUniqueAuthor( 0 );
            for ( int jnx = 0 ; jnx < authorInx ; ++jnx )
                data[inx][jnx] = new Object();
            data[inx][authorInx] = author;
            expAuthors.add( author );
        }
        
        JTable                  table       = 
            new JTable( data, allColumnHeaders );
        String                  listName    = authorListNames[0];
        AuthorContentProvider   provider    =
            new AuthorContentProvider( listName, allColumnHeaders );
        provider.setAuthors( table );
        Set<Author>             actAuthors  = provider.getAuthors();
        assertTrue( expAuthors.size() > 0 );
        assertCollectionEquals( expAuthors, actAuthors );
        
        // Exception test
        Title                   title       = 
            new Title( titleListName, "Some Title Name" );
        data[numRows - 1][authorInx]  = title;
        JTable                  badTable    = 
            new JTable( data, allColumnHeaders );
        Class<KCLSException>    excClass    = KCLSException.class;
        assertThrows( excClass, () -> provider.setAuthors( badTable ) );
    }
    
    @Test
    public void testGoWrong()
    {
        
    }

    @Test
    void testIterator()
    {
        for ( String listName : authorListNames )
        {
            Set<Author>             actSet  = new HashSet<>();
            AuthorContentProvider   provider    =
                new AuthorContentProvider( listName );
            int                 dataCol = provider.getDataColumnIndex();
            Iterator<Object[]>  iter    = provider.iterator();
            while ( iter.hasNext() )
            {
                Object[]    row = iter.next();
                actSet.add( (Author)row[dataCol] );
            }
            
            Set<Author>  expSet = listsMap.get( listName );
            assertTrue( Utils.equals( expSet, actSet ) );
        }
    }

    /**
     * Workaround for comparing two Sets for equality
     * (which doesn't seem to be working properly; don't know why).
     * 
     * @param col1  First set to compare
     * @param col2  Second set to compare
     */
    private void
    assertCollectionEquals( Collection<?> coll1, Collection<?> coll2 )
    {
        assertEquals( coll1.size(), coll2.size() );
        for ( Object obj : coll1 )
        {
            Author  author  = (Author)obj;
            System.out.print( author + " :: " );
        }
        System.out.println();
        
        for ( Object obj : coll2 )
        {
            Author  author  = (Author)obj;
            System.out.print( author + " :: " );
        }
        System.out.println();
        System.out.println( "**************" );

        
        assertTrue( coll1.containsAll( coll2 ) ); 
        assertTrue( coll2.containsAll( coll1 ) ); 
//        assertEquals( col1.size(), col2.size() );
//        Comparator<Author>    sortByName  =
//            (a1, a2) -> a1.getAuthor().compareTo( a2.getAuthor() );
//            
//        List<Author>    expList = new ArrayList<Author>();
//        expList.addAll( col1 );
//        expList.sort( sortByName );
//        
//        List<Author>    actList = new ArrayList<Author>();
//        actList.addAll( col2 );
//        actList.sort( sortByName );
//        assertEquals( expList, actList );
//        
//        for ( Object obj : col1 )
//            if ( !col2.contains( obj ) )
//            {
//                Comparator<Author>    sortByName  =
//                    (a1, a2) -> a1.getAuthor().compareTo( a2.getAuthor() );
//                    
//                List<Author>    expList = new ArrayList<Author>();
//                expList.addAll( (Collection<Author>)col1 );
//                expList.sort( sortByName );
//                
//                List<Author>    actList = new ArrayList<Author>();
//                actList.addAll( (Collection<Author>)col2 );
//                actList.sort( sortByName );
//                
//                String  message = "EXPECTED: " + expList + " ACTUAL: " + actList;
//                fail( message );
//            }
    }

    /**
     * Workaround for comparing two Sets for equality
     * (which doesn't seem to be working properly; don't know why).
     * 
     * @param col1  First set to compare
     * @param col2  Second set to compare
     */
//    private void
//    assertCollectionEquals( Collection<KCLSList> col1, Collection<KCLSList> col2 )
//    {
//        assertEquals( col1.size(), col2.size() );
//        Comparator<KCLSList>    sortByName  =
//            (a1, a2) -> a1.getDialogTitle().compareTo( a2.getDialogTitle() );
//            
//        List<KCLSList>    expList = new ArrayList<KCLSList>();
//        expList.addAll( col1 );
//        expList.sort( sortByName );
//        
//        List<KCLSList>    actList = new ArrayList<KCLSList>();
//        actList.addAll( col2 );
//        actList.sort( sortByName );
//        assertEquals( expList, actList );
//    }
}
