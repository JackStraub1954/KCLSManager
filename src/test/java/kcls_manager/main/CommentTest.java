package kcls_manager.main;

import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.TITLE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.OptionalInt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommentTest
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
    void testComment()
    {
        String  text    = "this is a comment";
        Comment comment = new Comment( AUTHOR_TYPE, text );
        assertFalse( comment.getIdent().isPresent() );
        assertEquals( text, comment.getText() );
    }

    @Test
    void testSetGetIdentOptionalInt()
    {
        String  text    = "this is a comment";
        Comment comment = new Comment( AUTHOR_TYPE, text );
        assertFalse( comment.getIdent().isPresent() );
        
        int ident1  = 5;
        
        comment.setIdent( OptionalInt.of( ident1 ) );
        assertTrue( comment.getIdent().isPresent() );
        assertEquals( ident1, comment.getIdent().getAsInt() );
        
        comment.setIdent( OptionalInt.empty() );
        assertFalse( comment.getIdent().isPresent() );
    }

    @Test
    void testSetIdentInt()
    {
        String  text    = "this is a comment";
        Comment comment = new Comment( AUTHOR_TYPE, text );
        assertFalse( comment.getIdent().isPresent() );
        
        int ident1  = 5;
        comment.setIdent( ident1 );
        OptionalInt optInt  = comment.getIdent();
        assertTrue( optInt.isPresent() );
        assertEquals( ident1, optInt.getAsInt() );
    }

    @Test
    void testSetGetText()
    {
        String  text    = "this is a comment";
        Comment comment = new Comment( AUTHOR_TYPE, text );
        assertEquals( text, comment.getText() );
        
        text += "***";
        comment.setText( text );
        assertEquals( text, comment.getText() );
    }

    @Test
    void testSetItemIDInt()
    {
        String  text    = "this is a comment";
        Comment comment = new Comment( AUTHOR_TYPE, text );
        assertFalse( comment.getItemID().isPresent() );
        
        int ident1  = 5;
        comment.setItemID( ident1 );
        OptionalInt optInt  = comment.getItemID();
        assertTrue( optInt.isPresent() );
        assertEquals( ident1, optInt.getAsInt() );
    }

    @Test
    void testSetGetItemIDOptionalInt()
    {
        String  text    = "this is a comment";
        Comment comment = new Comment( AUTHOR_TYPE, text );
        assertFalse( comment.getItemID().isPresent() );
        
        int ident1  = 5;
        
        comment.setItemID( OptionalInt.of( ident1 ) );
        assertTrue( comment.getItemID().isPresent() );
        assertEquals( ident1, comment.getItemID().getAsInt() );
        
        comment.setItemID( OptionalInt.empty() );
        assertFalse( comment.getItemID().isPresent() );
    }

    @Test
    void testSetGetType()
    {
        String  text    = "this is a comment";
        Comment comment = new Comment( AUTHOR_TYPE, text );
        assertEquals( AUTHOR_TYPE, comment.getType() );
        comment.setType( TITLE_TYPE );
        assertEquals( TITLE_TYPE, comment.getType() );
    }

    @Test
    void testToString()
    {
        String  text    = "this is a comment";
        Comment comment = new Comment( AUTHOR_TYPE, text );
        String  str     = comment.toString();
        assertTrue( str.contains( "ident=null" ) );
        assertTrue( str.contains( "text=" + text ) );
        assertTrue( str.contains( "type=author" ) );

        int ident = 42;
        comment = new Comment( TITLE_TYPE, text );
        comment.setIdent( ident );
        str  = comment.toString();
        assertTrue( str.contains( "ident="+ ident ) );
        assertTrue( str.contains( "text=" + text ) );
        assertTrue( str.contains( "type=title" ) );
        
        comment.setType( 10 );
        str  = comment.toString();
        assertTrue( str.contains( "ident="+ ident ) );
        assertTrue( str.contains( "text=" + text ) );
        assertTrue( str.contains( "type=\"unknown\"" ) );
    }
    
    @Test
    void testCompareTo()
    {
        int     identLow    = 10;
        int     identMid    = 20;
        int     identHigh   = 30;
        
        int     typeLow     = 200;
        int     typeMid     = 210;
        int     typeHigh    = 220;
        
        String  textLow     = "0000Comment";
        String  textMid     = "0010Comment";
        String  textHigh    = "0020Comment";
        
        int     itemIDLow   = 300;
        int     itemIDMid   = 310;
        int     itemIDHigh  = 320;
        
        Comment commentA    = new Comment( typeMid, textMid );
        Comment commentB    = new Comment( typeMid, textMid );
        commentA.setIdent( identMid );
        commentB.setIdent( identMid );
        commentA.setItemID( itemIDMid );
        commentB.setItemID( itemIDMid );
        
        assertTrue( commentA.compareTo( null ) > 0 );
        
        assertTrue( commentA.compareTo( commentB ) == 0 );
        assertTrue( commentB.compareTo( commentA ) == 0 );
        
        commentA.setIdent( identLow );
        assertTrue( commentA.compareTo( commentB ) < 0 );
        commentA.setIdent( identHigh );
        assertTrue( commentA.compareTo( commentB ) > 0 );
        commentB.setIdent( identHigh );
        assertTrue( commentA.compareTo( commentB ) == 0 );
        
        commentA.setType( typeLow );
        assertTrue( commentA.compareTo( commentB ) < 0 );
        commentA.setType( typeHigh );
        assertTrue( commentA.compareTo( commentB ) > 0 );
        commentB.setType( typeHigh );
        assertTrue( commentA.compareTo( commentB ) == 0 );
        
        commentA.setText( textLow );
        assertTrue( commentA.compareTo( commentB ) < 0 );
        commentA.setText( textHigh );
        assertTrue( commentA.compareTo( commentB ) > 0 );
        commentB.setText( textHigh );
        assertTrue( commentA.compareTo( commentB ) == 0 );
        
        commentA.setItemID( itemIDLow );
        assertTrue( commentA.compareTo( commentB ) < 0 );
        commentA.setItemID( itemIDHigh );
        assertTrue( commentA.compareTo( commentB ) > 0 );
        commentB.setItemID( itemIDHigh );
        assertTrue( commentA.compareTo( commentB ) == 0 );
    }

    @Test
    void testEqualsHash()
    {
        OptionalInt ident1      = OptionalInt.of( 1 );
        OptionalInt ident2      = OptionalInt.of( 2 );
        int         type1       = TITLE_TYPE;
        int         type2       = AUTHOR_TYPE;
        String      text1       = "comment";
        String      text2       = text1 + "***";
        OptionalInt itemID1     = OptionalInt.of( 10 );
        OptionalInt itemID2     = OptionalInt.of( 20 );
        Comment     commentA    = new Comment( type1, text1 );
        Comment     commentB    = new Comment( type1, text1 );
        
        assertNotEquals( commentA, null );
        assertNotEquals( commentA, new Object() );
        
        assertEquals( commentA, commentB );
        assertEquals( commentB, commentA );
        assertEquals( commentA.hashCode(), commentB.hashCode() );
        
        // note: original commentA.ident = empty
        commentB.setIdent( ident1 );
        assertNotEquals( commentA, commentB );
        assertNotEquals( commentB, commentA );
        commentA.setIdent( ident1 );
        assertEquals( commentA, commentB );
        assertEquals( commentA.hashCode(), commentB.hashCode() );
        
        commentB.setIdent( ident2 );
        assertNotEquals( commentA, commentB );
        assertNotEquals( commentB, commentA );
        commentA.setIdent( ident2 );
        assertEquals( commentA, commentB );
        assertEquals( commentA.hashCode(), commentB.hashCode() );
        
        commentB.setType( type2 );
        assertNotEquals( commentA, commentB );
        assertNotEquals( commentB, commentA );
        commentA.setType( type2 );
        assertEquals( commentA, commentB );
        assertEquals( commentA.hashCode(), commentB.hashCode() );
        
        commentB.setText( text2 );
        assertNotEquals( commentA, commentB );
        assertNotEquals( commentB, commentA );
        commentA.setText( text2 );
        assertEquals( commentA, commentB );
        assertEquals( commentA.hashCode(), commentB.hashCode() );
        
        // note: original commentA.itemID = empty
        commentB.setItemID( itemID1 );
        assertNotEquals( commentA, commentB );
        assertNotEquals( commentB, commentA );
        commentA.setItemID( itemID1 );
        assertEquals( commentA, commentB );
        assertEquals( commentA.hashCode(), commentB.hashCode() );
        
        commentB.setItemID( itemID2 );
        assertNotEquals( commentA, commentB );
        assertNotEquals( commentB, commentA );
        commentA.setItemID( itemID2 );
        assertEquals( commentA, commentB );
        assertEquals( commentA.hashCode(), commentB.hashCode() );
    }

}
