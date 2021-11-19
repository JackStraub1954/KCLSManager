package kcls_manager.main;

import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.TITLE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.OptionalInt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import util.TestUtils;

class KCLSListTest
{

    @BeforeAll
    static void setUpBeforeClass() throws Exception
    {
    }

    @BeforeEach
    void setUp() throws Exception
    {
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testKCLSListIntString()
    {
        String      listName    = "list A";
        KCLSList    list        = new KCLSList( TITLE_TYPE, listName );
        assertEquals( listName, list.getComponentLabel() );
        assertEquals( listName, list.getDialogTitle() );
        assertEquals( TITLE_TYPE, list.getListType() );
        
        list = new KCLSList( AUTHOR_TYPE, listName );
        assertEquals( listName, list.getComponentLabel() );
        assertEquals( listName, list.getDialogTitle() );
        assertEquals( AUTHOR_TYPE, list.getListType() );
    }

    @Test
    void testKCLSListIntStringString()
    {
        String      listName    = "list A";
        String      listLabel   = "Label A";
        KCLSList    list        = 
            new KCLSList( TITLE_TYPE, listLabel, listName );
        assertEquals( listLabel, list.getComponentLabel() );
        assertEquals( listName, list.getDialogTitle() );
        assertEquals( TITLE_TYPE, list.getListType() );
        
        list = new KCLSList( AUTHOR_TYPE, listLabel, listName );
        assertEquals( listLabel, list.getComponentLabel() );
        assertEquals( listName, list.getDialogTitle() );
        assertEquals( AUTHOR_TYPE, list.getListType() );
    }

    @Test
    void testSetGetIdent()
    {
        String      listName    = "list A";
        KCLSList    list        = new KCLSList( TITLE_TYPE, listName );
        int         ident1      = 1;
        int         ident2      = 10;
        assertEquals( list.getIdent(), OptionalInt.empty() );

        list.setIdent( ident1 );
        assertEquals( list.getIdent(), OptionalInt.of( ident1 ) );

        list.setIdent( OptionalInt.of( ident2 ) );
        assertEquals( list.getIdent(), OptionalInt.of( ident2 ) );
    }

    @Test
    void testSetGetDialogTitle()
    {
        String      dialogTitleA    = "list A";
        String      dialogTitleB    = dialogTitleA + "***";
        KCLSList    list        = new KCLSList( TITLE_TYPE, dialogTitleA );
        assertEquals( dialogTitleA, list.getDialogTitle() );
        
        list.setDialogTitle( dialogTitleB );
        assertEquals( dialogTitleB, list.getDialogTitle() );
    }

    @Test
    void testSetGetComponentLabel()
    {
        String      listName    = "list A";
        String      listLabelA  = "Label A";
        String      listLabelB  = "Label B";
        KCLSList    list        = 
            new KCLSList( TITLE_TYPE, listLabelA, listName );
        assertEquals( listLabelA, list.getComponentLabel() );
        assertEquals( listName, list.getDialogTitle() );
        
        list.setComponentLabel( listLabelB );
        assertEquals( listLabelB, list.getComponentLabel() );
        assertEquals( listName, list.getDialogTitle() );
    }

    @Test
    void testSetGetListType()
    {
        String      listName    = "list A";
        String      listLabel   = "Label A";
        KCLSList    list        = 
            new KCLSList( TITLE_TYPE, listLabel, listName );
        assertEquals( TITLE_TYPE, list.getListType() );
        
        list = new KCLSList( AUTHOR_TYPE, listLabel, listName );
        assertEquals( AUTHOR_TYPE, list.getListType() );
        
        list = new KCLSList( TITLE_TYPE, listLabel, listName );
        assertEquals( TITLE_TYPE, list.getListType() );
    }

    @Test
    void testSetGetCreationDate()
    {
        LocalDate   now         = TestUtils.nowAccountingForMidnight();
        String      listName    = "list A";
        String      listLabel   = "Label A";
        KCLSList    list        = 
            new KCLSList( TITLE_TYPE, listLabel, listName );
        assertEquals( now, list.getCreationDate() );
        
        LocalDate   then        = now.minusDays( 1 );
        list.setCreationDate( then );
        assertEquals( then, list.getCreationDate() );
    }

    @Test
    void testSetGetModifyDate()
    {
        LocalDate   now         = TestUtils.nowAccountingForMidnight();
        String      listName    = "list A";
        String      listLabel   = "Label A";
        KCLSList    list        = 
            new KCLSList( TITLE_TYPE, listLabel, listName );
        assertEquals( now, list.getModifyDate() );
        
        LocalDate   then        = now.minusDays( 1 );
        list.setModifyDate( then );
        assertEquals( then, list.getModifyDate() );
    }

    @Test
    void testEqualsHashObject()
    {
//        private OptionalInt     ident   = OptionalInt.empty();
//        
//        private int             listType;
//        private String          dialogTitle;
//        private String          componentLabel;
//        private LocalDate       creationDate;
//        private LocalDate       modifiyDate;
        int         ident1          = 1;
        int         ident2          = ident1 + 1;
        int         listType1       = TITLE_TYPE;
        int         listType2       = AUTHOR_TYPE;
        String      label1          = "Label " + ident1;
        String      label2          = "Label " + ident2;
        String      dialogTitle1    = "Dialog Title " + ident1;
        String      dialogTitle2    = "Dialog Title " + ident2;
        LocalDate   modifyDate1     = TestUtils.nowAccountingForMidnight();
        LocalDate   modifyDate2     = modifyDate1.minusDays( 1 );
        LocalDate   creationDate1   = modifyDate1.minusWeeks( 1 );
        LocalDate   creationDate2   = modifyDate2.minusWeeks( 1 );
        KCLSList    listA           = 
            new KCLSList( listType1, label1, dialogTitle1 );
        KCLSList    listB           = 
            new KCLSList( listType1, label1, dialogTitle1 );
        
        assertNotEquals( listA, null );
        assertNotEquals( listA, new Object() );
        assertEquals( listA, listB );
        assertEquals( listB, listA );
        assertEquals( listA.hashCode(), listB.hashCode() );
        
        listA.setIdent( ident1 );
        assertNotEquals( listA, listB );
        listB.setIdent( ident1 );
        assertEquals( listA, listB );
        assertEquals( listB, listA );
        assertEquals( listA.hashCode(), listB.hashCode() );
        
        listA.setIdent( ident2 );
        assertNotEquals( listA, listB );
        listB.setIdent( ident2 );
        assertEquals( listA, listB );
        assertEquals( listB, listA );
        assertEquals( listA.hashCode(), listB.hashCode() );
        
        listA.setListType( listType2 );
        assertNotEquals( listA, listB );
        listB.setListType( listType2 );
        assertEquals( listA, listB );
        assertEquals( listB, listA );
        assertEquals( listA.hashCode(), listB.hashCode() );
        
        listA.setComponentLabel( label2 );
        assertNotEquals( listA, listB );
        listB.setComponentLabel( label2 );
        assertEquals( listA, listB );
        assertEquals( listB, listA );
        assertEquals( listA.hashCode(), listB.hashCode() );
        
        listA.setDialogTitle( dialogTitle2 );
        assertNotEquals( listA, listB );
        listB.setDialogTitle( dialogTitle2 );
        assertEquals( listA, listB );
        assertEquals( listB, listA );
        assertEquals( listA.hashCode(), listB.hashCode() );
        
        listA.setModifyDate( modifyDate1 );
        listB.setModifyDate( modifyDate1 );
        assertEquals( listA, listB );
        assertEquals( listB, listA );
        listA.setModifyDate( modifyDate2 );
        assertNotEquals( listA, listB );
        listB.setModifyDate( modifyDate2 );
        assertEquals( listA, listB );
        assertEquals( listB, listA );
        assertEquals( listA.hashCode(), listB.hashCode() );
        
        listA.setCreationDate( creationDate1 );
        listB.setCreationDate( creationDate1 );
        assertEquals( listA, listB );
        assertEquals( listB, listA );
        listA.setCreationDate( creationDate2 );
        assertNotEquals( listA, listB );
        listB.setCreationDate( creationDate2 );
        assertEquals( listA, listB );
        assertEquals( listB, listA );
        assertEquals( listA.hashCode(), listB.hashCode() );
    }

    @Test
    void testToString()
    {
        testToString( TITLE_TYPE, "titles" );
        testToString( AUTHOR_TYPE, "authors" );
    }
    
    private void testToString( int listType, String strListType )
    {
        LocalDate   now         = TestUtils.nowAccountingForMidnight();
        LocalDate   modifyDate  = now;
        LocalDate   createDate  = now.minusDays( 1 );
        String      dialogTitle = "Dialog Title A";
        String      listLabel   = "Label A";
        KCLSList    list        = 
            new KCLSList( listType, listLabel, dialogTitle );
        list.setCreationDate( createDate );
        list.setModifyDate( modifyDate );
        
        String      str         = list.toString();
        System.out.println( str );
        
        String      testFor     = "ident=null";
        assertTrue( str.contains( testFor ), testFor );
        
        testFor     = "creationDate=" + createDate;
        assertTrue( str.contains( testFor ), testFor );
        
        testFor     = "modifyDate=" + modifyDate;
        assertTrue( str.contains( testFor ), testFor );
        
        testFor     = "dialogTitle=" + dialogTitle;
        assertTrue( str.contains( testFor ), testFor );
        
        testFor     = "componentLabel=" + listLabel;
        assertTrue( str.contains( testFor ), testFor );
        
        testFor     = "listType=" + strListType;
        assertTrue( str.contains( testFor ), testFor );
        
        int ident   = 5;
        list.setIdent( ident );
        str = list.toString();
        System.out.println( str );        
        testFor     = "ident=" + ident;
        assertTrue( str.contains( testFor ), testFor );
    }
}
