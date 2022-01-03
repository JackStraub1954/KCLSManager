package kcls_manager.database;
import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.TITLE_TYPE;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kcls_manager.main.KCLSException;
import kcls_manager.main.KCLSList;
import test_util.TestUtils;

class ListsTableTest
{
    private static final int        defType1        = TITLE_TYPE;
    private static final int        defType2        = AUTHOR_TYPE;
    private static final String     defLabel        = "Default Label";
    private static final String     defDialogTitle  = "Default dialog title";
    private static final LocalDate  defCreationDate =
        LocalDate.now().plusDays( 1 );
    private static final LocalDate  defModifyDate   = 
        defCreationDate.plusDays( 1 );
    
    /**
     * Comparator to sort lists of KCLSLists by ident.
     */
    private final Comparator<KCLSList>  sortByIdent =
        (l1, l2) -> l1.getIdent().getAsInt() - l2.getIdent().getAsInt();
    
    private KCLSList  defList;
    
    private DBServer    dbServer;
    
    @BeforeEach
    void setUp() throws Exception
    {
        dbServer = TestUtils.getDBServer();
        TestUtils.truncateAllTables( dbServer );
        defList = new KCLSList( defType1, defLabel, defDialogTitle );
        defList.setCreationDate( defCreationDate );
        defList.setModifyDate( defModifyDate );
    }

    @AfterEach
    void tearDown() throws Exception
    {
//        dbServer.shutdown();
    }

    /**
     * Constructor test passes if setUp() doesn't fail.
     */
    @Test
    void testListsTable()
    {
    }
    
    @Test
    void testInsertList()
    {
        dbServer.insertList( defList );
        OptionalInt ident   = defList.getIdent();
        assertTrue( ident.isPresent() );
        
        KCLSList    listOut = dbServer.getList( ident.getAsInt() );
        assertEquals( defList, listOut );
        
        listOut = dbServer.getList( Integer.MAX_VALUE );
        assertNull( listOut );
    }
    
    @Test
    void testUpdateList()
    {
        dbServer.insertList( defList );
        OptionalInt ident   = defList.getIdent();
        assertTrue( ident.isPresent() );
        
        KCLSList    listOut = dbServer.getList( ident.getAsInt() );
        assertFalse( defList == listOut );
        assertEquals( defList, listOut );
        
        List<String>    headings    = new ArrayList<>();
        headings.add( "new heading 1" );
        headings.add( "new heading 2" );
        
        defList.setListType( defType2 );
        defList.setDialogTitle( defDialogTitle + "***" );
        defList.setComponentLabel( defLabel + "***" );
        defList.setCreationDate( defCreationDate.plusWeeks( 1 ) );
        defList.setModifyDate( defModifyDate.plusWeeks( 1 ) );
        dbServer.updateList( defList );
        
        listOut = dbServer.getList( ident.getAsInt() );
        assertFalse( defList == listOut );
        assertEquals( defList, listOut );
    }

    @Test
    void deleteListList()
    {
        KCLSList    list    = getUniqueList( 0 );
        dbServer.insertList( list );
        
        OptionalInt optIdent    = list.getIdent();
        assertTrue( optIdent.isPresent() );
        
        int         ident       = optIdent.getAsInt();
        KCLSList    check       = dbServer.getList( ident );
        assertEquals( list, check );
        
        dbServer.deleteList( list );
        check = dbServer.getList( ident );
        assertNull( check );
    }

    @Test
    void deleteListInt()
    {
        KCLSList    list    = getUniqueList( 0 );
        dbServer.insertList( list );
        
        OptionalInt optIdent    = list.getIdent();
        assertTrue( optIdent.isPresent() );
        
        int         ident       = optIdent.getAsInt();
        KCLSList    check       = dbServer.getList( ident );
        assertEquals( list, check );
        
        dbServer.deleteList( ident );
        check = dbServer.getList( ident );
        assertNull( check );
    }
    
    /* *****************************************************************
     * getList tests...
     * Run a bunch of tests with different fields uninitialized
     * on input. No need to test dates because those fields are
     * never uninitialized.
     */
    
    @Test
    void testgetList1()
    {
        KCLSList    list    = new KCLSList( TITLE_TYPE, "name1" );
        dbServer.insertList( list );
        OptionalInt ident   = list.getIdent();
        assertTrue( ident.isPresent() );
        
        KCLSList    listOut = dbServer.getList( ident.getAsInt() );
        assertEquals( list, listOut );
    }
    
    @Test
    void testgetList2()
    {
        KCLSList    list    = 
            new KCLSList( TITLE_TYPE, "name2", "label2" );
        dbServer.insertList( list );
        OptionalInt ident   = list.getIdent();
        assertTrue( ident.isPresent() );
        
        KCLSList    listOut = dbServer.getList( ident.getAsInt() );
        assertEquals( list, listOut );
    }
    
    @Test
    void testgetList3()
    {
        KCLSList    list    = 
            new KCLSList( TITLE_TYPE, "name3", "label3" );
        list.setDialogTitle( "title3" );
        dbServer.insertList( list );
        OptionalInt ident   = list.getIdent();
        assertTrue( ident.isPresent() );
        
        KCLSList    listOut = dbServer.getList( ident.getAsInt() );
        assertEquals( list, listOut );
    }
    
    @Test
    void testgetList4()
    {
        List<String>    headings    = new ArrayList<>();
        headings.add( "heading1" );
        headings.add( "heading2" );
        
        KCLSList    list    = 
            new KCLSList( TITLE_TYPE, "name4", "label4" );
        list.setDialogTitle( "title4" );
        dbServer.insertList( list );
        OptionalInt ident   = list.getIdent();
        assertTrue( ident.isPresent() );
        
        KCLSList    listOut = dbServer.getList( ident.getAsInt() );
        assertEquals( list, listOut );
    }
    
    @Test
    void getAllListsTest()
    {
        List<KCLSList>   expLists   = new ArrayList<>();
        for ( int inx = 1 ; inx <= 3 ; ++inx )
            expLists.add( getUniqueList( inx ) );
        
        for ( KCLSList list : expLists )
            dbServer.insertList( list );
        
        // tried to simplify comparison by converting Lists
        // to sets, but couldn't get it to work.
        List<KCLSList>  actLists    = dbServer.getAllLists();
        expLists.sort( sortByIdent );
        actLists.sort( sortByIdent );
        
        assertEquals( actLists, actLists );
            
    }
    
    @Test
    void testGetAuthorTitleLists()
    {
        List<KCLSList>  titleLists  = new ArrayList<>();
        List<KCLSList>  authorLists = new ArrayList<>();
        int             inx         = 0;
        for ( int count = 0 ; count < 5 ; ++count )
        {
            KCLSList    tList   = getUniqueList( inx++ );
            tList.setListType( TITLE_TYPE );
            titleLists.add( tList );
            dbServer.insertList( tList );
            
            KCLSList    aList   = getUniqueList( inx++ );
            aList.setListType( AUTHOR_TYPE );
            authorLists.add( aList );
            dbServer.insertList( aList );
            
            List<KCLSList>  actTitleLists   = dbServer.getTitleLists();
            titleLists.sort( sortByIdent );
            actTitleLists.sort( sortByIdent );
            assertEquals( titleLists, actTitleLists );
            
            List<KCLSList>  actAuthorLists  = dbServer.getAuthorLists();
            authorLists.sort( sortByIdent );
            actAuthorLists.sort( sortByIdent );
            assertEquals( authorLists, actAuthorLists );
        }
    }
    
    @Test
    public void testGetListIDForLabel()
    {
        String[]    allLabels   =
        {
            "test label 1",
            "test Label 2",
            "test Label 3",
        };
        
        String[]    allNotLabels    = new String[allLabels.length];
        
        KCLSList[]  allLists        = new KCLSList[allLabels.length];
        
        for ( int inx = 0 ; inx < allLabels.length ; ++inx )
        {
            String      label       = allLabels[inx];
            String      notLabel    = "not " + label;
            KCLSList    list        = new KCLSList( AUTHOR_TYPE, label );
            
            allNotLabels[inx] = notLabel;
            allLists[inx] = list;
            dbServer.insertList( list );
            System.out.println( list.getIdent() );
        }
        
        for ( KCLSList list : allLists )
        {
            OptionalInt expIdent    = list.getIdent();
            int         actIdent = 
                dbServer.getListID( list.getDialogTitle() );
            assertTrue( expIdent.isPresent() );
            assertEquals( expIdent.getAsInt(), actIdent );
        }
    
        for ( String notLabel : allNotLabels )
        {
            int ident   = dbServer.getListID( notLabel );
            assertTrue( ident < 0 );
        }
    }
    
    /**
     * Test some of the error paths.
     */
    @Test
    public void testGoWrong()
    {
        KCLSList                list        = getUniqueList( 0 );
        Class<KCLSException>    excClass    = KCLSException.class;
        
        assertThrows( excClass, () -> dbServer.updateList( list ) );
        assertThrows( excClass, () -> dbServer.deleteList( list ) );
    }
    
    private KCLSList getUniqueList( int suffix )
    {
        int         type    = defType2;
        String      label   = defLabel + suffix;
        String      title   = defDialogTitle + suffix;
        LocalDate   creDate = defCreationDate.plusMonths( suffix + 1 );
        LocalDate   modDate = defModifyDate.plusMonths( suffix + 1 );
        KCLSList    list    = new KCLSList( type, label, title );
        list.setCreationDate( creDate );
        list.setModifyDate( modDate );
        return list;
    }
}
