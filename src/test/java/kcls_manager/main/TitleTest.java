package kcls_manager.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import util.CommentFactory;
import util.TestUtils;

class TitleTest
{
    private static final String     defText         = "Default Title";
    private static final String     defAuthor       = "Default Author";
    private static final String     defMediaType    = "Default Media Type";
    private static final int        defReckonQPos   = 5;
    private static final int        defCheckQPos    = 2 * defReckonQPos;
    private static final LocalDate  defReckonDate   = 
        LocalDate.now().plusDays( 2 );
    private static final LocalDate  defCheckDate    = 
        defReckonDate.plusDays( 2 );
    private static final LocalDate  defCreationDate = 
        defReckonDate.minusDays( 2 );
    private static final String     defListName     = "Default List Name";
    
    private Title           defTitle;
    private CommentFactory  commentFactory;

    @BeforeAll
    static void setUpBeforeClass() throws Exception
    {
    }

    @BeforeEach
    void setUp() throws Exception
    {
        commentFactory = new CommentFactory();
        defTitle = 
            new Title( defCreationDate, defText, defListName, defAuthor );
        defTitle.setMediaType( defMediaType );
        defTitle.setReckonQPos( defReckonQPos );
        defTitle.setCheckQPos( defCheckQPos );
        defTitle.setReckonDate( defReckonDate );
        defTitle.setCheckDate( defCheckDate );
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testToString()
    {
        String  string  = defTitle.toString();
        System.out.println( string );
        
        assertTrue( string.contains( "title=" + defText ) );
        assertTrue( string.contains( "author=" + defAuthor ) );
        assertTrue( string.contains( "listName=" + defListName ) );
        assertTrue( string.contains( "mediaType=" + defMediaType ) );
        assertTrue( string.contains( "reckonQPos=" + defReckonQPos ) );
        assertTrue( string.contains( "checkQPos=" + defCheckQPos ) );
        assertTrue( string.contains( "reckonDate=" + defReckonDate ) );
        assertTrue( string.contains( "checkDate=" + defCheckDate ) );
    }

    @Test
    void testTitle()
    {
        LocalDate   now     = TestUtils.nowAccountingForMidnight();
        Title       title   = new Title();
        assertEquals( "", title.getTitle() );
        assertEquals( "", title.getAuthor() );
        assertEquals( "unspec", title.getMediaType() );
        assertEquals( 0, title.getReckonQPos() );
        assertEquals( 0, title.getCheckQPos() );
        assertEquals( now, title.getReckonDate() );
        assertEquals( now, title.getCheckDate() );
    }

    @Test
    void testTitleTitle()
    {
        Title   dest    = new Title( defTitle );
        testCopyTitle( dest, defTitle );
    }

    @Test
    void testTitleString()
    {
        String  text    = "A Title";
        Title   title   = new Title( text );
        assertEquals( text, title.getTitle() );
    }

    @Test
    void testTitleStringString()
    {
        String  text        = "A Title";
        String  listName    = "Author, Anne";
        Title   title       = new Title( text, listName );
        assertEquals( text, title.getTitle() );
        assertEquals( listName, title.getListName() );
    }

    @Test
    void testTitleLocalDateStringString()
    {
        LocalDate   creDate     = LocalDate.now().minusWeeks( 1 );
        String      text        = "A Title";
        String      listName    = "Author, Anne";
        Title       title       = new Title( creDate, text, listName );
        assertEquals( text, title.getTitle() );
        assertEquals( listName, title.getAuthor() );
        assertEquals( creDate, title.getCreationDate() );
    }

    @Test
    void testTitleLocalDateStringStringString()
    {
        LocalDate   creDate = LocalDate.now().minusWeeks( 1 );
        String      text        = "A Title";
        String      listName    = "Some List Name";
        String      author      = "Author, Anne";
        Title       title       = 
            new Title( creDate, text, listName, author );
        assertEquals( text, title.getTitle() );
        assertEquals( author, title.getAuthor() );
        assertEquals( creDate, title.getCreationDate() );
        assertEquals( listName, title.getListName() );
    }

    @Test
    void testCopyFromTitle()
    {
        Title   dest    = new Title();
        dest.copyFrom( defTitle );
        testCopyTitle( dest, defTitle );
    }

    @Test
    void testSetGetTitle()
    {
        String  newTitle    = "0010 " + defTitle;
        defTitle.setTitle(newTitle);
        assertEquals( newTitle, defTitle.getTitle() );
    }

    @Test
    void testSetGetAuthor()
    {
        String  newAuthor   = "0010 " + defTitle;
        defTitle.setAuthor( newAuthor );
        assertEquals( newAuthor, defTitle.getAuthor() );
    }

    @Test
    void testSetGetCheckDate()
    {
        LocalDate   checkDate   = defCheckDate.minusWeeks( 2 );
        defTitle.setCheckDate(checkDate);
        assertEquals( checkDate, defTitle.getCheckDate() );
    }

    @Test
    void testSetGetReckonDate()
    {
        LocalDate   reckonDate  = defCheckDate.minusWeeks( 2 );
        defTitle.setReckonDate(reckonDate);
        assertEquals( reckonDate, defTitle.getReckonDate() );
    }

    @Test
    void testSetGetMediaType()
    {
        String  newMediaType    = "0010 " + defMediaType;
        defTitle.setMediaType( newMediaType );
        assertEquals( newMediaType, defTitle.getMediaType() );
    }

    @Test
    void testSGetCheckQPos()
    {
        int newCheckQPos    = defCheckQPos * 3;
        defTitle.setCheckQPos( newCheckQPos );
        assertEquals( newCheckQPos, defTitle.getCheckQPos() );
    }

    @Test
    void testGetReckonQPos()
    {
        int newReckonQPos   = defReckonQPos * 3;
        defTitle.setReckonQPos( newReckonQPos );
        assertEquals( newReckonQPos, defTitle.getReckonQPos() );
    }

    @Test
    void testHashEqualsObject1()
    {
        String      text1       = "0001 Title";
        String      text2       = "0002 Title";
        String      author1     = "0001 Author";
        String      author2     = "0002 Author";
        String      mediaType1  = "0001 Media Type";
        String      mediaType2  = "0002 Media Type";
        String      listName1   = "0001 List Name";
        String      listName2   = "0002 List Name";
        int         reckonQPos1 = 10;
        int         reckonQPos2 = 20;
        int         checkQPos1  = 30;
        int         checkQPos2  = 40;
        LocalDate   reckonDate1 = LocalDate.now();
        LocalDate   reckonDate2 = reckonDate1.plusDays( 2 );
        LocalDate   checkDate1  = reckonDate2.plusDays( 2 );
        LocalDate   checkDate2  = checkDate1.plusDays( 2 );
        LocalDate   creDate1    = reckonDate1.minusDays( 2 );
        LocalDate   creDate2    = creDate1.minusDays( 2 );
        
        Title       titleA      = 
            new Title( creDate1, text1, listName1, author1 );
        titleA.setMediaType( mediaType1 );
        titleA.setListName( listName1 );
        titleA.setReckonQPos( reckonQPos1 );
        titleA.setCheckQPos( checkQPos1 );
        titleA.setReckonDate( reckonDate1 );
        titleA.setCheckDate( checkDate1 );
        titleA.setCreationDate( creDate1 );
        
        Title       titleB      = new Title( titleA );
        assertEquals( titleA, titleB );
        assertEquals( titleB, titleA );
        assertEquals( titleA, titleA );
        assertNotEquals( titleA, null );
        assertNotEquals( titleA, new Object() );
        assertEquals( titleA.hashCode(), titleB.hashCode() );
        
        titleB.setTitle( text2 );
        assertNotEquals( titleA, titleB );
        titleA.setTitle( text2 );
        assertEquals( titleA, titleB );
        assertEquals( titleA.hashCode(), titleB.hashCode() );
        
        titleB.setAuthor( author2 );
        assertNotEquals( titleA, titleB );
        titleA.setAuthor( author2 );
        assertEquals( titleA, titleB );
        assertEquals( titleA.hashCode(), titleB.hashCode() );
        
        titleB.setMediaType( mediaType2 );
        assertNotEquals( titleA, titleB );
        titleA.setMediaType( mediaType2 );
        assertEquals( titleA, titleB );
        assertEquals( titleA.hashCode(), titleB.hashCode() );
        
        titleB.setListName( listName2 );
        assertNotEquals( titleA, titleB );
        titleA.setListName( listName2 );
        assertEquals( titleA, titleB );
        assertEquals( titleA.hashCode(), titleB.hashCode() );
        
        titleB.setReckonQPos( reckonQPos2 );
        assertNotEquals( titleA, titleB );
        titleA.setReckonQPos( reckonQPos2 );
        assertEquals( titleA, titleB );
        assertEquals( titleA.hashCode(), titleB.hashCode() );
        
        titleB.setCheckQPos( checkQPos2 );
        assertNotEquals( titleA, titleB );
        titleA.setCheckQPos( checkQPos2 );
        assertEquals( titleA, titleB );
        assertEquals( titleA.hashCode(), titleB.hashCode() );
        
        titleB.setReckonDate( reckonDate2 );
        assertNotEquals( titleA, titleB );
        titleA.setReckonDate( reckonDate2 );
        assertEquals( titleA, titleB );
        assertEquals( titleA.hashCode(), titleB.hashCode() );
        
        titleB.setCheckDate( checkDate2 );
        assertNotEquals( titleA, titleB );
        titleA.setCheckDate( checkDate2 );
        assertEquals( titleA, titleB );
        assertEquals( titleA.hashCode(), titleB.hashCode() );
        
        titleB.setCreationDate( creDate2 );
        assertNotEquals( titleA, titleB );
        titleA.setCreationDate( creDate2 );
        assertEquals( titleA, titleB );
        assertEquals( titleA.hashCode(), titleB.hashCode() );
    }

    @Test
    void testHashEqualsObject2()
    {
        int     commentCount    = 10;
        Title   newTitle        = new Title( defTitle );
        assertEquals( defTitle, newTitle );
        assertEquals( defTitle.hashCode(), newTitle.hashCode() );
        
        for ( int inx = 0 ; inx < commentCount ; ++inx )
        {
            Comment comment = 
                commentFactory.getUniqueComment( inx );
            defTitle.addComment( comment );
            assertNotEquals( defTitle, newTitle );
            newTitle.addComment( comment );
            assertEquals( defTitle, newTitle );
            assertEquals( defTitle.hashCode(), newTitle.hashCode() );
        }
        
        List<Comment>   temp    = new ArrayList<>();
        temp.addAll( defTitle.getComments() );
        while ( !temp.isEmpty() )
        {
            int     last    = temp.size() - 1;
            Comment comment = temp.remove( last );
            assertNotNull( comment );
            assertTrue( defTitle.removeComment( comment ) );
            assertNotEquals( defTitle, newTitle );
            assertTrue( newTitle.removeComment( comment ) );
            assertEquals( defTitle, newTitle );
            assertEquals( defTitle.hashCode(), newTitle.hashCode() );
            
            comment = 
                commentFactory.getUniqueComment( OptionalInt.empty() );
            defTitle.addComment( comment );
            assertNotEquals( defTitle, newTitle );
            newTitle.addComment( comment );
            assertEquals( defTitle, newTitle );
            assertEquals( defTitle.hashCode(), newTitle.hashCode() );
        }
        
        temp.addAll( defTitle.getComments() );
        while ( !temp.isEmpty() )
        {
            int     last    = temp.size() - 1;
            Comment comment = temp.remove( last );
            assertNotNull( comment );
            assertTrue( defTitle.removeComment( comment ) );
            assertNotEquals( defTitle, newTitle );
            assertTrue( newTitle.removeComment( comment ) );
            assertEquals( defTitle, newTitle );
            assertEquals( defTitle.hashCode(), newTitle.hashCode() );
        }
        assertTrue( defTitle.getComments().isEmpty() );
        assertTrue( newTitle.getComments().isEmpty() );
    }
    
    @Test
    public void testCompareTo()
    {
        String      textLow         = "0000Title";
        String      textMid         = "0010Title";
        String      textHigh        = "0020Title";

        String      authorLow       = "0100Author";
        String      authorMid       = "0110Author";
        String      authorHigh      = "0120Author";

        String      mediaTypeLow    = "0200MediaType";
        String      mediaTypeMid    = "0210MediaType";
        String      mediaTypeHigh   = "0220MediaType";
        
        int         reckonQPosLow   = 300;
        int         reckonQPosMid   = 310;
        int         reckonQPosHigh  = 320;
        
        int         checkQPosLow    = 400;
        int         checkQPosMid    = 410;
        int         checkQPosHigh   = 420;
        
        LocalDate   reckonDateLow   = LocalDate.now();
        LocalDate   reckonDateMid   = reckonDateLow.plusDays( 2 );
        LocalDate   reckonDateHigh  = reckonDateMid.plusDays( 2 );
        
        LocalDate   checkDateLow    = LocalDate.now().plusWeeks( 1 );
        LocalDate   checkDateMid    = checkDateLow.plusDays( 2 );
        LocalDate   checkDateHigh   = checkDateMid.plusDays( 2 );

        Title       titleA          = new Title( textMid );
        titleA.setAuthor( authorMid );
        titleA.setMediaType(mediaTypeMid);
        titleA.setReckonQPos( reckonQPosMid );
        titleA.setCheckQPos( checkQPosMid );
        titleA.setReckonDate( reckonDateMid );
        titleA.setCheckDate( checkDateMid );
        
        Title       titleB          = new Title( titleA );
        
        assertTrue( titleA.compareTo( null ) > 0 );
        assertTrue( titleA.compareTo( titleA ) == 0 );
        assertTrue( titleA.compareTo( titleB ) == 0 );
        assertTrue( titleB.compareTo( titleA ) == 0 );
        
        titleA.setTitle( textLow );
        assertTrue( titleA.compareTo( titleB ) < 0 );
        titleA.setTitle( textHigh );
        assertTrue( titleA.compareTo( titleB ) > 0 );
        titleB.setTitle( textHigh );;
        assertTrue( titleA.compareTo( titleB ) == 0 );
        
        titleA.setAuthor( authorLow );
        assertTrue( titleA.compareTo( titleB ) < 0 );
        titleA.setAuthor( authorHigh );
        assertTrue( titleA.compareTo( titleB ) > 0 );
        titleB.setAuthor( authorHigh );;
        assertTrue( titleA.compareTo( titleB ) == 0 );
        
//      private String      title       = "";
//      private String      author      = "";
//      private String      mediaType   = "unspec";
//      private int         reckonQPos  = 0;
//      private int         checkQPos   = 0;
//      private LocalDate   reckonDate  = LocalDate.now();
//      private LocalDate   checkDate   = LocalDate.now();
        
        titleA.setMediaType( mediaTypeLow );
        assertTrue( titleA.compareTo( titleB ) < 0 );
        titleA.setMediaType( mediaTypeHigh );
        assertTrue( titleA.compareTo( titleB ) > 0 );
        titleB.setMediaType( mediaTypeHigh );;
        assertTrue( titleA.compareTo( titleB ) == 0 );
        
        titleA.setReckonQPos( reckonQPosLow );
        assertTrue( titleA.compareTo( titleB ) < 0 );
        titleA.setReckonQPos( reckonQPosHigh );
        assertTrue( titleA.compareTo( titleB ) > 0 );
        titleB.setReckonQPos( reckonQPosHigh );
        assertTrue( titleA.compareTo( titleB ) == 0 );
        
        titleA.setCheckQPos( checkQPosLow );
        assertTrue( titleA.compareTo( titleB ) < 0 );
        titleA.setCheckQPos( checkQPosHigh );
        assertTrue( titleA.compareTo( titleB ) > 0 );
        titleB.setCheckQPos( checkQPosHigh );
        assertTrue( titleA.compareTo( titleB ) == 0 );
        
        titleA.setReckonDate( reckonDateLow );
        assertTrue( titleA.compareTo( titleB ) < 0 );
        titleA.setReckonDate( reckonDateHigh );
        assertTrue( titleA.compareTo( titleB ) > 0 );
        titleB.setReckonDate( reckonDateHigh );
        assertTrue( titleA.compareTo( titleB ) == 0 );
        
        titleA.setCheckDate( checkDateLow );
        assertTrue( titleA.compareTo( titleB ) < 0 );
        titleA.setCheckDate( checkDateHigh );
        assertTrue( titleA.compareTo( titleB ) > 0 );
        titleB.setCheckDate( checkDateHigh );
        assertTrue( titleA.compareTo( titleB ) == 0 );
    }

    private void testCopyTitle( Title dest, Title source )
    {
        assertEquals( source.getTitle(), dest.getTitle() );
        assertEquals( source.getAuthor(), dest.getAuthor() );
        assertEquals( source.getMediaType(), dest.getMediaType() );
        assertEquals( source.getReckonQPos(), dest.getReckonQPos() );
        assertEquals( source.getCheckQPos(), dest.getCheckQPos() );
        assertEquals( source.getReckonDate(), dest.getReckonDate() );
        assertEquals( source.getCheckDate(), dest.getCheckDate() );
    }
}
