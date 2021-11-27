package kcls_manager.main;

import static kcls_manager.main.Constants.TITLE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import util.CommentFactory;
import util.TestUtils;

class LibraryItemTest
{
    private Tester  tester;
    private static final int            defRank         = 5;
    private static final int            defRating       = 1;
    private static final String         defSource       = "Default Source";
    private static final String         defListName     = 
        "Default List Name";
    private static final List<Comment>  defComments     = new ArrayList<>();
    private static final LocalDate      defCreationDate;
    private static final LocalDate      defModifyDate;
    private static final OptionalInt    emptyOption     = OptionalInt.empty();
    
    private Tester  defTester               = new Tester();
    private CommentFactory  commentFactory  = new CommentFactory();
    
    static
    {
        LocalDate   now     = TestUtils.nowAccountingForMidnight();
        defCreationDate = now.minusDays( 1 );
        defModifyDate = defCreationDate.plusWeeks( 1 );
    }

    @BeforeAll
    static void setUpBeforeClass() throws Exception
    {
    }

    @BeforeEach
    void setUp() throws Exception
    {
        tester = new Tester();
        defTester.setRank( defRank );
        defTester.setRating( defRating );
        defTester.setSource(defSource);
        defTester.setListName(defListName);
        defTester.setComments(defComments);
        defTester.setCreationDate(defCreationDate);
        defTester.setModifyDate(defModifyDate);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testLibraryItem()
    {
        assertEquals( defRank, defTester.getRank() );
        assertEquals( defRating, defTester.getRating() );
        assertEquals( defSource, defTester.getSource() );
        assertEquals( defListName, defTester.getListName() );
        TestUtils.assertCommentsEqual( defComments, defTester.getComments() );
        assertEquals( defCreationDate, defTester.getCreationDate() );
        assertEquals( defModifyDate, defTester.getModifyDate() );
    }

    @Test
    void testLibraryItemLibraryItem()
    {
        Tester  tester  = new Tester( defTester );
        assertEquals( defTester.getIdent(), tester.getIdent() );
        assertEquals( defRank, tester.getRank() );
        assertEquals( defRating, defTester.getRating() );
        assertEquals( defSource, defTester.getSource() );
        assertEquals( defListName, defTester.getListName() );
        TestUtils.assertCommentsEqual( defComments, defTester.getComments() );
        assertEquals( defCreationDate, defTester.getCreationDate() );
        assertEquals( defModifyDate, defTester.getModifyDate() );
    }

    @Test
    void testLibraryItemLocalDate()
    {
        Tester  tester  = new Tester( defCreationDate );
        assertEquals( defCreationDate, tester.getCreationDate() );
    }

    @Test
    void testSetGetIdent()
    {
        int         ident       = 5;
        OptionalInt optIdent    = OptionalInt.of( ident );
        assertEquals( emptyOption, defTester.getIdent() );
        
        defTester.setIdent( optIdent );
        assertEquals( optIdent, defTester.getIdent() );
    }

    @Test
    void testSetIdentInt()
    {
        int         ident       = 5;
        assertEquals( emptyOption, defTester.getIdent() );
        
        defTester.setIdent( ident );
        assertEquals( ident, defTester.getIdent().getAsInt() );
    }

    @Test
    void testGetCreationDate()
    {
        LocalDate   origDate    = defTester.getCreationDate();
        LocalDate   newDate     = origDate.plusDays( 2 );
        defTester.setCreationDate( newDate );
        assertEquals( newDate, defTester.getCreationDate() );
    }

    @Test
    void testCopyFrom()
    {
        Tester  tester  = new Tester();
        tester.copyFrom( defTester );
        assertEquals( defTester.getIdent(), tester.getIdent() );
        assertEquals( defRank, tester.getRank() );
        assertEquals( defRating, defTester.getRating() );
        assertEquals( defSource, defTester.getSource() );
        assertEquals( defListName, defTester.getListName() );
        TestUtils.assertCommentsEqual( defComments, defTester.getComments() );
        assertEquals( defCreationDate, defTester.getCreationDate() );
        assertEquals( defModifyDate, defTester.getModifyDate() );
    }
    
    @Test
    void testSetGetListName()
    {
        String  newListName = "new list name";
        defTester.setListName( newListName );
        assertEquals( newListName, defTester.getListName() );
    }

    @Test
    void testGetNumComments()
    {
        assertEquals( 0, defTester.getNumComments() );
        for ( int inx = 1 ; inx <= 10 ; ++inx )
        {
            Comment nextComment = 
                commentFactory.getUniqueComment( emptyOption );
            defTester.addComment( nextComment );
            assertEquals( inx, defTester.getNumComments() );
        }
    }

    @Test
    void testAddComment()
    {
        assertTrue( defTester.getComments().isEmpty() );
        
        List<Comment>    expComments = new ArrayList<>();
        for ( int inx = 0 ; inx < 10 ; ++inx )
        {
            Comment nextComment = 
                commentFactory.getUniqueComment( emptyOption );
            defTester.addComment( nextComment );
            List<Comment>   actComments = defTester.getComments();
            assertNotEquals( expComments, actComments );
            expComments.add(nextComment);
            TestUtils.assertCommentsEqual( expComments, actComments );
        }
    }
    
    @Test
    void testClearComments()
    {
        assertTrue( defTester.getComments().isEmpty() );
        
        Comment comment = commentFactory.getUniqueComment( 1 );
        defTester.addComment( comment );
        assertFalse( defTester.getComments().isEmpty() );
        
        defTester.clearComments();
        assertTrue( defTester.getComments().isEmpty() );
    }

    @Test
    void testSetGetComments()
    {
        assertTrue( defTester.getComments().isEmpty() );
        
        List<Comment>   expComments = new ArrayList<>();
        for ( int inx = 0 ; inx < 10 ; ++inx )
        {
            expComments.add( commentFactory.getUniqueComment( emptyOption ) );
            defTester.setComments( expComments );
            List<Comment>   actComments = defTester.getComments();
            assertFalse( expComments == actComments );
            TestUtils.assertCommentsEqual( expComments, actComments );
        }
    }

    @Test
    void testSetGetRank()
    {
        int newRank = 10;
        defTester.setRank( newRank );
        assertEquals( newRank, defTester.getRank() );
    }

    @Test
    void testSetGetRating()
    {
        int newRating   = 10;
        defTester.setRating( newRating );
        assertEquals( newRating, defTester.getRating() );
    }

    @Test
    void testSetGetModifyDate()
    {
        LocalDate   origDate    = defTester.getModifyDate();
        LocalDate   newDate     = origDate.plusDays( 2 );
        defTester.setModifyDate( newDate );
        assertEquals( newDate, defTester.getModifyDate() );
    }

    @Test
    void testSetGetSource()
    {
        String  newSource   = "next source";
        defTester.setSource( newSource );
        assertEquals( newSource, defTester.getSource() );
    }

    @Test
    void testItemEquals()
    {
        int         ident1      = 1;
        int         ident2      = ident1 + 1;
        int         rank1       = 3;
        int         rank2       = rank1 + 1;
        int         rating1     = 10;
        int         rating2     = rating1 + 1;
        String      source1     = "Source 1";
        String      source2     = "Source 2";
        String      listName1   = "List Name 1";
        String      listName2   = "List Name 2";
        LocalDate   creDate1    = LocalDate.now().plusDays( 1 );
        LocalDate   creDate2    = creDate1.plusDays( 1 );
        LocalDate   modDate1    = creDate1.plusWeeks( 1 );
        LocalDate   modDate2    = modDate1.plusDays( 1 );
        
        Tester      testerA = new Tester();
        testerA.setIdent( ident1 );
        testerA.setRank( rank1 );
        testerA.setRating( rating1 );
        testerA.setSource( source1 );
        testerA.setListName( listName1 );
        testerA.setCreationDate( creDate1 );
        tester.setModifyDate( modDate1 );

        Tester      testerB = new Tester();
        testerB.setIdent( ident1 );
        testerB.setRank( rank1 );
        testerB.setRating( rating1 );
        testerB.setSource( source1 );
        testerB.setListName( listName1 );
        testerB.setCreationDate( creDate1 );
        tester.setModifyDate( modDate1 );
        
        assertNotEquals( testerA, null );
        assertNotEquals( testerA, new Object() );
        assertEquals( testerA, testerA );
        assertEquals( testerA, testerB );
        assertEquals( testerB, testerA );
        
        testerB.setIdent( ident2 );
        assertNotEquals( testerA, testerB );
        assertNotEquals( testerB, testerA );
        testerA.setIdent(ident2);
        assertEquals( testerA, testerB );
        assertEquals( testerB, testerA );
        
        testerB.setRank( rank2 );
        assertNotEquals( testerA, testerB );
        assertNotEquals( testerB, testerA );
        testerA.setRank(rank2);
        assertEquals( testerA, testerB );
        assertEquals( testerB, testerA );
        
        testerB.setRating( rating2 );
        assertNotEquals( testerA, testerB );
        assertNotEquals( testerB, testerA );
        testerA.setRating( rating2 );
        assertEquals( testerA, testerB );
        assertEquals( testerB, testerA );
        
        testerB.setSource( source2 );
        assertNotEquals( testerA, testerB );
        assertNotEquals( testerB, testerA );
        testerA.setSource( source2 );
        assertEquals( testerA, testerB );
        assertEquals( testerB, testerA );
        
        testerB.setListName( listName2 );
        assertNotEquals( testerA, testerB );
        assertNotEquals( testerB, testerA );
        testerA.setListName( listName2  );
        assertEquals( testerA, testerB );
        assertEquals( testerB, testerA );
        
        testerB.setCreationDate( creDate2 );
        assertNotEquals( testerA, testerB );
        assertNotEquals( testerB, testerA );
        testerA.setCreationDate( creDate2 );
        assertEquals( testerA, testerB );
        assertEquals( testerB, testerA );
        
        testerB.setModifyDate( modDate2 );
        assertNotEquals( testerA, testerB );
        assertNotEquals( testerB, testerA );
        testerA.setModifyDate( modDate2 );
        assertEquals( testerA, testerB );
        assertEquals( testerB, testerA );
        
        Comment newComment  = commentFactory.getUniqueComment( ident1 );
        testerB.addComment( newComment );
        assertNotEquals( testerA, testerB );
        assertNotEquals( testerB, testerA );
        testerA.addComment( newComment );
        assertEquals( testerA, testerB );
        assertEquals( testerB, testerA );
        
        newComment = commentFactory.getUniqueComment( ident2 );
        testerB.addComment( newComment );
        assertNotEquals( testerA, testerB );
        assertNotEquals( testerB, testerA );
        testerA.addComment( newComment );
        assertEquals( testerA, testerB );
        assertEquals( testerB, testerA );
        
        newComment = commentFactory.getUniqueComment( emptyOption );
        testerB.addComment( newComment );
        assertNotEquals( testerA, testerB );
        assertNotEquals( testerB, testerA );
        testerA.addComment( newComment );
        assertEquals( testerA, testerB );
        assertEquals( testerB, testerA );
    }
    
    @Test
    public void testItemEqualsHashNoComments()
    {
//        setIdent
//        setRank( from.getRank() );
//        setRating( from.getRating() );
//        setSource( from.getSource() );
//        setListName( from.getListName() );
//        setComments( from.getComments() );
//        setCreationDate( from.getCreationDate() );
//        setModifyDate( from.getModifyDate() );

        int         intIdent1       = 100;
        int         intIdent2       = 110;
        int         rank1           = 200;
        int         rank2           = 210;
        int         rating1         = 300;
        int         rating2         = 310;
        String      source1         = "0400Source 1";
        String      source2         = "0410Source 2";
        String      listName1       = "0500List Name 1";
        String      listName2       = "0510List Name 2";
        LocalDate   creDate1        = LocalDate.now();
        LocalDate   creDate2        = creDate1.plusDays( 2 );
        LocalDate   modDate1        = creDate2.plusWeeks( 1 );
        LocalDate   modDate2        = creDate2.plusDays( 2 );
        
        Tester      testerA           = new Tester();
        testerA.setIdent( null );
        testerA.setRank( rank1 );
        testerA.setRating( rating1 );
        testerA.setSource( source1 );
        testerA.setListName( listName1 );
        testerA.setCreationDate( creDate1 );
        testerA.setModifyDate( modDate1 );
        
        Tester      testerB         = new Tester( testerA );
        
        assertNotEquals( testerA, null );
        assertEquals( testerA, testerA );
        assertEquals( testerA, testerB );
        assertEquals( testerB, testerA );
        assertEquals( testerA.hashCode(), testerB.hashCode() );
        
        testerA.setIdent( emptyOption );
        assertNotEquals( testerA, testerB );
        testerB.setIdent( emptyOption );
        assertEquals( testerA, testerB );
        assertEquals( testerA.hashCode(), testerB.hashCode() );
        
        testerA.setIdent( intIdent2 );
        assertNotEquals( testerA, testerB );
        testerB.setIdent( intIdent1 );
        assertNotEquals( testerA, testerB );
        testerB.setIdent( intIdent2 );
        assertEquals( testerA, testerB );
        assertEquals( testerA.hashCode(), testerB.hashCode() );
        
        testerA.setRank( rank2 );
        assertNotEquals( testerA, testerB );
        testerB.setRank( rank2 );
        assertEquals( testerA, testerB );
        assertEquals( testerA.hashCode(), testerB.hashCode() );
        
        testerA.setRating( rating2 );
        assertNotEquals( testerA, testerB );
        testerB.setRating( rating2 );
        assertEquals( testerA, testerB );
        assertEquals( testerA.hashCode(), testerB.hashCode() );
        
        testerA.setSource( source2 );
        assertNotEquals( testerA, testerB );
        testerB.setSource( source2 );
        assertEquals( testerA, testerB );
        assertEquals( testerA.hashCode(), testerB.hashCode() );
        
        testerA.setListName( listName2 );
        assertNotEquals( testerA, testerB );
        testerB.setListName( listName2 );
        assertEquals( testerA, testerB );
        assertEquals( testerA.hashCode(), testerB.hashCode() );
        
        testerA.setCreationDate( creDate2 );
        assertNotEquals( testerA, testerB );
        testerB.setCreationDate( creDate2 );
        assertEquals( testerA, testerB );
        assertEquals( testerA.hashCode(), testerB.hashCode() );
        
        testerA.setModifyDate( modDate2 );
        assertNotEquals( testerA, testerB );
        testerB.setModifyDate( modDate2 );
        assertEquals( testerA, testerB );
        assertEquals( testerA.hashCode(), testerB.hashCode() );
    }
    
    @Test
    public void testRemoveComment()
    {
        int             testCount       = 10;
        List<Comment>   commentTracker  = new ArrayList<>();
        for ( int inx = 0 ; inx < testCount ; ++inx )
        {
            Comment comment = commentFactory.getUniqueComment( inx );
            commentTracker.add( comment );
            defTester.addComment( comment );
            TestUtils.assertCommentsEqual( commentTracker, defTester.getComments() );
        }
        
        for ( Comment comment : commentTracker )
        {
            assertTrue( defTester.removeComment( comment ) );
            assertFalse( defTester.removeComment( comment ) );
        }
        assertTrue( defTester.getComments().isEmpty() );
    }
    
    /**
     * Spends extra time testing collections of comments for equality.
     * For whatever reason, testing collections of comments for equality
     * has been an ongoing problem. This test attempts to exercise that
     * functionality. If the problem persists, coming back here to
     * beef up the test may be a good idea.
     */
    @Test
    public void testCommentSetEquality()
    {
        List<Comment>   expSet  = new ArrayList<>();
        List<Comment>   actSet  = defTester.getComments();
        List<Comment>   temp    = new ArrayList<>();
        
        TestUtils.assertCommentsEqual( expSet, actSet );
        for ( int inx = 0 ; inx < 10 ; ++inx )
        {
            Comment comment = commentFactory.getUniqueComment( emptyOption );
            expSet.add( comment );
            assertNotEquals( expSet, defTester.getComments() );
            defTester.addComment(comment);
            assertEquals( expSet, defTester.getComments() );
        }
        
        actSet = defTester.getComments();
        Iterator<Comment>   iter    = actSet.iterator();
        while ( iter.hasNext() )
        {
            iter.next();
            if ( iter.hasNext() )
                temp.add( iter.next() );
        }
        
        while ( !temp.isEmpty() )
        {
            int     last        = temp.size() - 1;
            Comment toDelete    = temp.remove(last);
            defTester.removeComment( toDelete );
            actSet = defTester.getComments();
            assertNotEquals( expSet, actSet );
            expSet.remove( toDelete );
            assertEquals( expSet, actSet );
            
            Comment toAdd       = 
                commentFactory.getUniqueComment( emptyOption );
            defTester.addComment( toAdd );
            actSet = defTester.getComments();
            assertNotEquals( expSet, actSet );
            expSet.add( toAdd );
            assertEquals( expSet, actSet );
        }
        
        temp.addAll( expSet );
        while ( !temp.isEmpty() )
        {
            int     last        = temp.size() - 1;
            Comment toDelete    = temp.remove(last);
            defTester.removeComment( toDelete );
            actSet = defTester.getComments();
            assertNotEquals( expSet, actSet );
            expSet.remove( toDelete );
            assertEquals( expSet, actSet );
        }
        
        assertTrue( expSet.isEmpty() );
        assertTrue( actSet.isEmpty() );
    }

    @Test
    void testToString()
    {
        String  str = defTester.toString();
        System.out.println( str );
        assertTrue( str.contains( "ident=null" ) );
        assertTrue( str.contains( "rank=" + defRank ) );
        assertTrue( str.contains( "rating=" + defRating ) );
        assertTrue( str.contains( "source=" + defSource ) );
        assertTrue( str.contains( "listName=" + defListName ) );
        assertTrue( str.contains( "creDate=" + defCreationDate ) );
        assertTrue( str.contains( "modDate=" + defModifyDate ) );
        assertTrue( str.contains( "[]" ) );
        
        int testIdent   = 5;
        defTester.setIdent( testIdent );
        str = defTester.toString();
        assertTrue( str.contains( "ident=" + testIdent ) );
    }
    
    @Test
    void testCompareToIdent()
    {
        OptionalInt empty       = emptyOption;
        
        int         identLow    = 10;
        int         identMid    = 20;
        int         identHigh   = 30;
        
        Tester      test1       = defTester;
        Tester      test2       = new Tester( defTester );
        
        test1.setIdent( null );
        test2.setIdent( null );
        assertTrue( test1.compareTo( test2 ) == 0 );
        assertTrue( test2.compareTo( test1 ) == 0 );
        
        test1.setIdent( empty );
        assertTrue( test1.compareTo( test2 ) > 0 );
        assertTrue( test2.compareTo( test1 ) < 0 );
        test2.setIdent( empty );
        assertTrue( test1.compareTo( test2 ) == 0 );
        assertTrue( test2.compareTo( test1 ) == 0 );
        
        test1.setIdent( identMid );
        assertTrue( test1.compareTo( test2 ) > 0 );
        assertTrue( test2.compareTo( test1 ) < 0 );
        test2.setIdent( identMid );
        assertTrue( test1.compareTo( test2 ) == 0 );
        assertTrue( test2.compareTo( test1 ) == 0 );
        
        test1.setIdent( identHigh );
        assertTrue( test1.compareTo( test2 ) > 0 );
        assertTrue( test2.compareTo( test1 ) < 0 );
        test1.setIdent( identLow );
        assertTrue( test1.compareTo( test2 ) < 0 );
        assertTrue( test2.compareTo( test1 ) > 0 );
    }

    @Test
    void testCompareToNoIdentNoComments()
    {
        int         rankLow     = 100;
        int         rankMid     = 150;
        int         rankHigh    = 200;
        
        int         ratingLow   = 100;
        int         ratingMid   = 150;
        int         ratingHigh  = 200;
        
        String      sourceLow   = "0000Source";
        String      sourceMid   = "0010Source";
        String      sourceHigh  = "0100Source";
        
        String      listNameLow   = "0200ListName";
        String      listNameMid   = "0210ListName";
        String      listNameHigh  = "0300ListName";
        
        LocalDate   creDateLow  = LocalDate.now();
        LocalDate   creDateMid  = creDateLow.plusDays( 2 );
        LocalDate   creDateHigh = creDateMid.plusDays( 2 );
        
        LocalDate   modDateLow  = LocalDate.now();
        LocalDate   modDateMid  = modDateLow.plusWeeks( 2 );
        LocalDate   modDateHigh = modDateMid.plusWeeks( 2 );
        
        Tester      test1       = new Tester();
        test1.setRank( rankMid );
        test1.setRating( ratingMid );
        test1.setSource( sourceMid );
        test1.setListName( listNameMid );
        test1.setCreationDate( creDateMid );
        test1.setModifyDate( modDateMid );
        
        Tester      test2       = new Tester( test1 );
        assertTrue( test1.compareTo( test2 ) == 0 );
        assertTrue( test2.compareTo( test1 ) == 0 );
        
        assertTrue( test1.compareTo( null ) > 0 );
        assertTrue( test1.compareTo( test1 ) == 0 );
        
        test1.setRank( rankLow );
        assertTrue( test1.compareTo( test2 ) < 0 );
        test1.setRank( rankHigh );
        assertTrue( test1.compareTo( test2 ) > 0 );
        test1.setRank( rankMid );
        assertTrue( test1.compareTo( test2 ) == 0 );
        
        test1.setRating( ratingLow );
        assertTrue( test1.compareTo( test2 ) < 0 );
        test1.setRating( ratingHigh );
        assertTrue( test1.compareTo( test2 ) > 0 );
        test1.setRating( ratingMid );
        assertTrue( test1.compareTo( test2 ) == 0 );
        
        test1.setSource( sourceLow );
        assertTrue( test1.compareTo( test2 ) < 0 );
        test1.setSource( sourceHigh );
        assertTrue( test1.compareTo( test2 ) > 0 );
        test1.setSource( sourceMid );
        assertTrue( test1.compareTo( test2 ) == 0 );
        
        test1.setListName( listNameLow );
        assertTrue( test1.compareTo( test2 ) < 0 );
        test1.setListName( listNameHigh );
        assertTrue( test1.compareTo( test2 ) > 0 );
        test1.setListName( listNameMid );
        assertTrue( test1.compareTo( test2 ) == 0 );
        
        test1.setCreationDate( creDateLow );
        assertTrue( test1.compareTo( test2 ) < 0 );
        test1.setCreationDate( creDateHigh );
        assertTrue( test1.compareTo( test2 ) > 0 );
        test1.setCreationDate( creDateMid );
        assertTrue( test1.compareTo( test2 ) == 0 );
        
        test1.setModifyDate( modDateLow );
        assertTrue( test1.compareTo( test2 ) < 0 );
        test1.setModifyDate( modDateHigh );
        assertTrue( test1.compareTo( test2 ) > 0 );
        test1.setModifyDate( modDateMid );
        assertTrue( test1.compareTo( test2 ) == 0 );
    }
    
    @Test
    public void testCompareToComments()
    {
        defTester.clearComments();
        Tester  tester  = new Tester( defTester );
        
        Comment commentLow  = new Comment( TITLE_TYPE, "0000Comment 0000" );
        Comment commentMid  = new Comment( TITLE_TYPE, "0010Comment 0010" );
        Comment commentHigh = new Comment( TITLE_TYPE, "0020Comment 0020" );
        
        // empty comments
        assertTrue( defTester.compareTo( tester ) == 0 );
        assertTrue( tester.compareTo( defTester ) == 0 );
        
        // different size collection of comments
        defTester.addComment( commentMid );
        assertTrue( defTester.compareTo( tester ) > 0 );
        assertTrue( tester.compareTo( defTester ) < 0 );
        tester.addComment( commentMid );
        assertTrue( defTester.compareTo( tester ) == 0 );
        
        // comment text magnitude
        defTester.clearComments();
        tester.clearComments();
        defTester.addComment( commentMid );
        tester.addComment( commentLow );
        assertTrue( defTester.compareTo( tester ) > 0 );
        assertTrue( tester.compareTo( defTester ) < 0 );
        
        defTester.clearComments();
        tester.clearComments();
        defTester.addComment( commentMid );
        tester.addComment( commentHigh );
        assertTrue( defTester.compareTo( tester ) < 0 );
        assertTrue( tester.compareTo( defTester ) > 0 );
    }

    /**
     * At a minimum, needed because LibraryItem is an abstract class.
     * 
     * @author jstra
     *
     */
    private static class Tester extends LibraryItem
    {
        Tester()
        {
        }
        
        public Tester(LocalDate creDate)
        {
            super(creDate);
        }

        Tester( LibraryItem item )
        {
            super( item );
        }
        
        @Override
        public int hashCode()
        {
            return super.hashCode();
        }
        
        @Override
        public boolean equals( Object obj )
        {
            if ( obj != null && !(obj instanceof LibraryItem ) )
                return false;
            return super.itemEquals( (LibraryItem)obj );
        }
    }
}
