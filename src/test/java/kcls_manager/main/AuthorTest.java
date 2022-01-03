package kcls_manager.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.OptionalInt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import test_util.AuthorFactory;
import test_util.CommentFactory;

class AuthorTest
{
//    private String      author          = "";
//    private String      listName        = "";
//    private int         lastCount       = 0;
//    private int         currCount       = 0;
    private static final String defText         = "Name, Default Author";
    private static final String defListName     = "Default List Name";
    private static final int    defLastCount    = 5;
    private static final int    defCurrCount    = 4 * defLastCount;
    
    private Author          defAuthor;
    private AuthorFactory   authorFactory;
    private CommentFactory  commentFactory;
    
    @BeforeAll
    static void setUpBeforeClass() throws Exception
    {
    }

    @BeforeEach
    void setUp() throws Exception
    {
        defAuthor = new Author( defText, defListName );
        defAuthor.setLastCount( defLastCount );
        defAuthor.setCurrentCount( defCurrCount );
        
        authorFactory = new AuthorFactory();
        commentFactory = new CommentFactory();
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testSetGetListName()
    {
        String  newListName = defListName + "-----";
        defAuthor.setListName(newListName);
        assertEquals( newListName, defAuthor.getListName() );
    }

    @Test
    void testToString()
    {
        // Null ident, no comments
        testToString( defAuthor );
        
        // Non-null ident, no comments
        defAuthor.setIdent( 5 );
        testToString( defAuthor );
        
        // Non-null ident, one comment
        defAuthor.addComment( commentFactory.getUniqueComment( 1 ) );
        testToString( defAuthor );
        
        // Non-null ident, two comments
        defAuthor.addComment( commentFactory.getUniqueComment( 1 ) );
        testToString( defAuthor );
        
        // Non-null ident, three comments
        defAuthor.addComment( commentFactory.getUniqueComment( 1 ) );
        testToString( defAuthor );
    }

    @Test
    void testAuthor()
    {
        Author  author      = new Author();
        String  actText     = author.getAuthor();
        String  actListName = author.getListName();
        assertNotNull( actText );
        assertTrue( actText.isEmpty() );
        assertNotNull( actListName );
        assertTrue( actListName.isEmpty() );
        assertEquals( 0, author.getRank() );
        assertEquals( 0, author.getRating() );
    }

    @Test
    void testAuthorString()
    {
        Author  author      = new Author( defText );
        String  actListName = author.getListName();
        assertEquals( defText, author.getAuthor() );
        assertNotNull( actListName );
        assertTrue( actListName.isEmpty() );
        assertEquals( 0, author.getRank() );
        assertEquals( 0, author.getRating() );
    }

    @Test
    void testAuthorStringString()
    {
        Author  author      = new Author( defText, defListName );
        assertEquals( defText, author.getAuthor() );
        assertEquals( defListName, author.getListName() );
        assertEquals( 0, author.getRank() );
        assertEquals( 0, author.getRating() );
    }

    @Test
    void testAuthorLocalDateStringString()
    {
        LocalDate   expDate = LocalDate.now().minusMonths( 2 );
        Author      author  = new Author( expDate, defText, defListName );
        assertEquals( defText, author.getAuthor() );
        assertEquals( defListName, author.getListName() );
        assertEquals( expDate, author.getCreationDate() );
        assertEquals( 0, author.getRank() );
        assertEquals( 0, author.getRating() );
    }

    @Test
    void testAuthorAuthor()
    {
        int     commentCount    = 10;
        Author  fromAuthor      = 
            authorFactory.getUniqueAuthor( commentCount );
        assertEquals( commentCount, fromAuthor.getComments().size() );
        
        Author  toAuthor        = new Author( fromAuthor );
        testCopyAuthor( toAuthor, fromAuthor );
    }

    @Test
    void testCopyFromAuthor()
    {
        int     commentCount    = 10;
        Author  fromAuthor      = 
            authorFactory.getUniqueAuthor( commentCount );
        Author  toAuthor        = new Author();
        assertEquals( commentCount, fromAuthor.getComments().size() );
        toAuthor.copyFrom( fromAuthor );
        testCopyAuthor( toAuthor, fromAuthor );
    }

    @Test
    void testSetGetAuthor()
    {
        String  newText = defAuthor.getAuthor() + "*****";
        defAuthor.setAuthor( newText );
        assertEquals( newText, defAuthor.getAuthor() );
    }

    @Test
    void testSetGetLastCount()
    {
        int newLastCount    = defLastCount * 2;
        defAuthor.setLastCount( newLastCount );
        assertEquals( newLastCount, defAuthor.getLastCount() );
    }

    @Test
    void testSetGetCurrentCount()
    {
        int newCurrCount    = defCurrCount * 2;
        defAuthor.setCurrentCount( newCurrCount );
        assertEquals( newCurrCount, defAuthor.getCurrentCount() );
    }

    @Test
    void testHashCodeEqualsObject()
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
        
        String      text1       = "Author Number 1";
        String      text2       = "Author Number 2";
        int         lastCount1  = 10;
        int         lastCount2  = lastCount1 + 1;
        int         currCount1  = 2 * lastCount1;
        int         currCount2  = currCount1 + 1;
        
        Author      authorA = new Author();
        authorA.setIdent( ident1 );
        authorA.setRank( rank1 );
        authorA.setRating( rating1 );
        authorA.setSource( source1 );
        authorA.setListName( listName1 );
        authorA.setCreationDate( creDate1 );
        authorA.setModifyDate( modDate1 );
        authorA.setAuthor( text1 );
        authorA.setLastCount( lastCount1 );
        authorA.setCurrentCount( currCount1 );

        Author      authorB = new Author();
        authorB.setIdent( ident1 );
        authorB.setRank( rank1 );
        authorB.setRating( rating1 );
        authorB.setSource( source1 );
        authorB.setListName( listName1 );
        authorB.setCreationDate( creDate1 );
        authorB.setModifyDate( modDate1 );
        authorB.setAuthor( text1 );
        authorB.setLastCount( lastCount1 );
        authorB.setCurrentCount( currCount1 );
        
        assertNotEquals( authorA, null );
        assertNotEquals( authorA, new Object() );
        assertEquals( authorA, authorA );
        assertEquals( authorA, authorB );
        assertEquals( authorB, authorA );
        assertEquals( authorA.hashCode(), authorB.hashCode() );
        
        authorB.setIdent( ident2 );
        assertNotEquals( authorA, authorB );
        assertNotEquals( authorB, authorA );
        authorA.setIdent(ident2);
        assertEquals( authorA, authorB );
        assertEquals( authorB, authorA );
        assertEquals( authorA.hashCode(), authorB.hashCode() );
        
        authorB.setRank( rank2 );
        assertNotEquals( authorA, authorB );
        assertNotEquals( authorB, authorA );
        authorA.setRank(rank2);
        assertEquals( authorA, authorB );
        assertEquals( authorB, authorA );
        assertEquals( authorA.hashCode(), authorB.hashCode() );
        
        authorB.setRating( rating2 );
        assertNotEquals( authorA, authorB );
        assertNotEquals( authorB, authorA );
        authorA.setRating( rating2 );
        assertEquals( authorA, authorB );
        assertEquals( authorB, authorA );
        assertEquals( authorA.hashCode(), authorB.hashCode() );
        
        authorB.setSource( source2 );
        assertNotEquals( authorA, authorB );
        assertNotEquals( authorB, authorA );
        authorA.setSource( source2 );
        assertEquals( authorA, authorB );
        assertEquals( authorB, authorA );
        assertEquals( authorA.hashCode(), authorB.hashCode() );
        
        authorB.setListName( listName2 );
        assertNotEquals( authorA, authorB );
        assertNotEquals( authorB, authorA );
        authorA.setListName( listName2  );
        assertEquals( authorA, authorB );
        assertEquals( authorB, authorA );
        assertEquals( authorA.hashCode(), authorB.hashCode() );
        
        authorB.setCreationDate( creDate2 );
        assertNotEquals( authorA, authorB );
        assertNotEquals( authorB, authorA );
        authorA.setCreationDate( creDate2 );
        assertEquals( authorA, authorB );
        assertEquals( authorB, authorA );
        assertEquals( authorA.hashCode(), authorB.hashCode() );
        
        authorB.setModifyDate( modDate2 );
        assertNotEquals( authorA, authorB );
        assertNotEquals( authorB, authorA );
        authorA.setModifyDate( modDate2 );
        assertEquals( authorA, authorB );
        assertEquals( authorB, authorA );
        assertEquals( authorA.hashCode(), authorB.hashCode() );
        
        Comment newComment  = commentFactory.getUniqueComment( ident1 );
        authorB.addComment( newComment );
        assertNotEquals( authorA, authorB );
        assertNotEquals( authorB, authorA );
        authorA.addComment( newComment );
        assertEquals( authorA, authorB );
        assertEquals( authorB, authorA );
        assertEquals( authorA.hashCode(), authorB.hashCode() );
        
        newComment = commentFactory.getUniqueComment( ident2 );
        authorB.addComment( newComment );
        assertNotEquals( authorA, authorB );
        assertNotEquals( authorB, authorA );
        authorA.addComment( newComment );
        assertEquals( authorA, authorB );
        assertEquals( authorB, authorA );
        assertEquals( authorA.hashCode(), authorB.hashCode() );
        
        authorB.setAuthor( text2 );
        assertNotEquals( authorA, authorB );
        assertNotEquals( authorB, authorA );
        authorA.setAuthor( text2 );
        assertEquals( authorA, authorB );
        assertEquals( authorB, authorA );
        assertEquals( authorA.hashCode(), authorB.hashCode() );
        
        authorB.setLastCount( lastCount2 );
        assertNotEquals( authorA, authorB );
        assertNotEquals( authorB, authorA );
        authorA.setLastCount( lastCount2 );
        assertEquals( authorA, authorB );
        assertEquals( authorB, authorA );
        assertEquals( authorA.hashCode(), authorB.hashCode() );
        
        authorB.setCurrentCount( currCount2 );
        assertNotEquals( authorA, authorB );
        assertNotEquals( authorB, authorA );
        authorA.setCurrentCount( currCount2 );
        assertEquals( authorA, authorB );
        assertEquals( authorB, authorA );
        assertEquals( authorA.hashCode(), authorB.hashCode() );
        
        OptionalInt nullOption  = OptionalInt.empty();
        newComment = commentFactory.getUniqueComment( nullOption );
        authorB.addComment( newComment );
        assertNotEquals( authorA, authorB );
        assertNotEquals( authorB, authorA );
        authorA.addComment( newComment );
        assertEquals( authorA, authorB );
        assertEquals( authorB, authorA );
        assertEquals( authorA.hashCode(), authorB.hashCode() );
    }
    
    @Test
    public void testCompareTo()
    {
        String  authorLow       = "0000Author";
        String  authorMid       = "0010Author";
        String  authorHigh      = "0020Author";
        
        int     lastCountLow    = 100;
        int     lastCountMid    = 110;
        int     lastCountHigh   = 120;
        
        int     currCountLow    = 200;
        int     currCountMid    = 210;
        int     currCountHigh   = 220;
        
        Author  authorA         = new Author( authorMid );
        authorA.setLastCount( lastCountMid );
        authorA.setCurrentCount( currCountMid );
        Author  authorB         = new Author( authorA );
        
        assertTrue( authorA.compareTo( null ) > 0 );
        assertTrue( authorA.compareTo( authorA ) == 0 );
        assertTrue( authorA.compareTo( authorB ) == 0 );
        assertTrue( authorB.compareTo( authorA ) == 0 );
        
        authorA.setAuthor( authorLow );
        assertTrue( authorA.compareTo( authorB ) < 0 );
        authorA.setAuthor( authorHigh );
        assertTrue( authorA.compareTo( authorB ) > 0 );
        authorB.setAuthor( authorHigh );
        assertTrue( authorA.compareTo( authorB ) == 0 );
        
        authorA.setLastCount( lastCountLow );
        assertTrue( authorA.compareTo( authorB ) < 0 );
        authorA.setLastCount( lastCountHigh );
        assertTrue( authorA.compareTo( authorB ) > 0 );
        authorB.setLastCount( lastCountHigh );
        assertTrue( authorA.compareTo( authorB ) == 0 );
        
        authorA.setCurrentCount( currCountLow );
        assertTrue( authorA.compareTo( authorB ) < 0 );
        authorA.setCurrentCount( currCountHigh );
        assertTrue( authorA.compareTo( authorB ) > 0 );
        authorB.setCurrentCount( currCountHigh );
        assertTrue( authorA.compareTo( authorB ) == 0 );
    }

    private void testCopyAuthor( Author toAuthor, Author fromAuthor )
    {
        assertEquals( fromAuthor.getRank(), toAuthor.getRank() );
        assertEquals( fromAuthor.getRating(), toAuthor.getRating() );
        assertEquals( fromAuthor.getSource(), toAuthor.getSource() );
        assertEquals( fromAuthor.getComments(), toAuthor.getComments() );
        assertEquals( 
            fromAuthor.getCreationDate(), 
            toAuthor.getCreationDate() 
        );
        assertEquals( fromAuthor.getModifyDate(), toAuthor.getModifyDate() );
        assertEquals( fromAuthor.getAuthor(), toAuthor.getAuthor() );
        assertEquals( fromAuthor.getListName(), toAuthor.getListName() );
        assertEquals( fromAuthor.getLastCount(), toAuthor.getLastCount() );
        assertEquals( 
            fromAuthor.getCurrentCount(), 
            toAuthor.getCurrentCount() 
        );
    }
    
    private void testToString( Author author )
    {
        OptionalInt     optIdent    = defAuthor.getIdent();
        String          expIdent    =
            optIdent.isEmpty() ? "null" : "" + optIdent.getAsInt();
        int             expRank     = defAuthor.getRank();
        int             expRating   = defAuthor.getRating();
        String          expSource   = defAuthor.getSource();
        String          expListName = defAuthor.getListName();
        List<Comment>   comments    = defAuthor.getComments();
        String          expComments = comments.toString();
        LocalDate       expCreDate  = defAuthor.getCreationDate();
        LocalDate       expModDate  = defAuthor.getModifyDate();
        
        String  string  = defAuthor.toString();
        assertTrue( string.contains( "author=" + defText ) );
        assertTrue( string.contains( "lastCount=" + defLastCount ) );
        assertTrue( string.contains( "currCount=" + defCurrCount ) );
        assertTrue( string.contains( "ident=" + expIdent ) );
        assertTrue( string.contains( "rank=" + expRank ) );
        assertTrue( string.contains( "rating=" + expRating ) );
        assertTrue( string.contains( "source=" + expSource ) );
        assertTrue( string.contains( "comments=" + expComments ) );
        assertTrue( string.contains( "listName=" + expListName ) );
        assertTrue( string.contains( "creDate=" + expCreDate ) );
        assertTrue( string.contains( "modDate=" + expModDate ) );
    }
}

