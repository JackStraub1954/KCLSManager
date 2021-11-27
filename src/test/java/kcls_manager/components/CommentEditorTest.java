package kcls_manager.components;

import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.CANCEL_TEXT;
import static kcls_manager.main.Constants.DELETE_TEXT;
import static kcls_manager.main.Constants.INSERT_TEXT;
import static kcls_manager.main.Constants.OKAY;
import static kcls_manager.main.Constants.OK_TEXT;
import static kcls_manager.main.Constants.TITLE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Component;
import java.awt.Container;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kcls_manager.main.Author;
import kcls_manager.main.Comment;
import kcls_manager.main.LibraryItem;
import kcls_manager.main.Title;
import util.AuthorFactory;
import util.CommentFactory;
import util.TestUtils;
import util.TitleFactory;

class CommentEditorTest
{
    private static final String loggerName  = 
        CommentEditorTest.class.getName();
    private static final Logger logger      = Logger.getLogger( loggerName );
    
    private JTextArea           jTextArea;
    private JTable              jTable;
    private TableModel          tableModel;
    private ListSelectionModel  selectionModel;
    private JButton             deleteButton;
    private JButton             insertButton;
    private JButton             okButton;
    private JButton             cancelButton;
    
    private int                 dialogStatus;
    private TitleFactory        titleFactory;
    private AuthorFactory       authorFactory;
    private CommentFactory      commentFactory;
    private Robot               robot;
    private final int           clickButtonDelay    = 50;
    
    private static final CommentEditor commentEditor    = new CommentEditor( null );
    
    @BeforeAll
    static void setUpBeforeClass() throws Exception
    {
    }

    @BeforeEach
    void setUp() throws Exception
    {
        titleFactory = new TitleFactory();
        authorFactory = new AuthorFactory();
        robot = new Robot();
        robot.setAutoDelay( 10 );
        
        BiPredicate<Component,Object>   byText  = TestUtils.textFinder;
        BiPredicate<Component,Object>   byType  = (c,t) -> c.getClass() == t;
        Container                       pane    = 
            commentEditor.getContentPane();
        
        jTextArea = (JTextArea)
            TestUtils.getComponent( pane, byType, JTextArea.class );
        assertNotNull( jTextArea );
        
        jTable = (JTable)
            TestUtils.getComponent( pane, byType, JTable.class );
        assertNotNull( jTable );
        tableModel = jTable.getModel();
        selectionModel = jTable.getSelectionModel();
        
        deleteButton = (JButton)
            TestUtils.getComponent( pane, byText, DELETE_TEXT );
        assertNotNull( deleteButton );
        
        insertButton = (JButton)
            TestUtils.getComponent( pane, byText, INSERT_TEXT );
        assertNotNull( insertButton );
        
        okButton = (JButton)
            TestUtils.getComponent( pane, byText, OK_TEXT );
        assertNotNull( okButton );
        
        cancelButton = (JButton)
            TestUtils.getComponent( pane, byText, CANCEL_TEXT );
        assertNotNull( cancelButton );
    }

    @AfterEach
    void tearDown() throws Exception
    {
        commentEditor.setVisible( false );
    }

    @Test
    void testCommentEditor()
    {
        // tested by default in "commentEditor" declaration
    }

    @Test
    void testShowDialogBooleanTitle()
    {
        Title           title       = titleFactory.getUniqueTitle( 0 );
        String          fmt         = "%03d this is a title comment";
        List<Comment>   comments    = new ArrayList<>();
        
        runDialog( title );
        for ( int inx = 0 ; inx < 5 ; ++inx )
        {
            String  text    = String.format( fmt, inx );
            Comment comment = new Comment( TITLE_TYPE, text );
            comments.add( comment );
            insertComment( text );
        }
        clickButton( okButton );
        assertEquals( dialogStatus, OKAY );
        List<Comment>   actualComments  = commentEditor.getComments();
        TestUtils.assertCommentsEqual( comments, actualComments );
    }

    @Test
    void testShowDialogBooleanAuthor()
    {
        Author          author      = authorFactory.getUniqueAuthor( 0 );
        String          fmt         = "%03d this is an author comment";
        List<Comment>   comments    = new ArrayList<>();
        
        runDialog( author );
        for ( int inx = 0 ; inx < 5 ; ++inx )
        {
            String  text    = String.format( fmt, inx );
            Comment comment = new Comment( AUTHOR_TYPE, text );
            comments.add( comment );
            insertComment( text );
        }
        clickButton( okButton );
        assertEquals( dialogStatus, OKAY );
        List<Comment>   actualComments  = commentEditor.getComments();
        TestUtils.assertCommentsEqual( comments, actualComments );
    }

    /**
     * Verify that comments are not modified directly by the dialog.
     */
    @Test
    void testSelect()
    {
        final BiPredicate<Comment,Object>   pred    = 
            (c,o) -> c.getText().startsWith( o.toString() );
        String          fmt         = "%03d this is a comment";
        List<Comment>   copies      = new ArrayList<>();
        List<Comment>   synch       = new ArrayList<>();
        
        // Simulate reading "count" comments from the database
        int             initCount   = 5;
        Author          author      = authorFactory.getUniqueAuthor( 0 );
        for ( int inx = 0 ; inx < initCount ; ++inx )
        {
            // Prefix (inx) can be used to identify a specific comment.
            // This is important because it is unknown in what sequence
            // the comments will appear in the dialog's list.
            //
            // The ident (inx + 1) is important because it should retain intact
            // in initial comments (whether modified or not) but be absent
            // in inserted comments.
            String  text    = String.format( fmt, inx );
            Comment comment = new Comment( AUTHOR_TYPE, text );
            author.addComment( comment );
            author.setIdent( inx + 1 );
            
            // Store a **copy** of the original of the original.
            // This will be used to verify that actual comments are not modified
            // until the dialog's getComments method is called.
            copies.add( new Comment( comment ) );
            
            // Keep another copy to maintain in parallel with edits.
            // This will be used to verify the accuracy of modified comments 
            // returned by the getComments method.
            synch.add( new Comment( comment ) );
        }
        
        runDialog( author );
        // modify the first and last comments
        int     first   = 0;
        int     last    = synch.size() - 1;
        String  suffix  = " modified";
        for ( int row : new int[] { first, last } )
        {
            appendToCommentAt( row, suffix );
            
            // reflect change in synchronized list
            String      text    = jTextArea.getText();
            String[]    parts   = text.split( " " );
            assertTrue( parts.length >= 1 );
            Comment comment = getComment( synch, parts[0], pred );
            assertNotNull( comment );
            comment.setText( text );
        }
        
        // Verify original comments not changed
        TestUtils.assertCommentsEqual( copies, author.getComments() );
        for ( int inx = 0 ; inx < 2 ; ++inx )
        {
            String  text    = "new comment " + inx;
            Comment comment = new Comment( AUTHOR_TYPE, text );
            synch.add( comment );
            insertComment( text );
        }
        
        clickButton( okButton );
        assertEquals( dialogStatus, OKAY );
        List<Comment>   actualComments  = commentEditor.getComments();
        TestUtils.assertCommentsEqual( synch, actualComments );
    }
    
    @Test
    public void testClose()
    {
        int     commentCount    = 5;
        int     rowNum          = commentCount / 2 ;
        
        // Sanity check: rowNum intended  to be:
        // 0 < rowNum < commentCount
        assertTrue( rowNum > 0 );
        
        Author  author  = authorFactory.getUniqueAuthor( 5 );
        runDialog( author );
        appendToCommentAt( rowNum, " suffix" );
        WindowEvent event   = 
            new WindowEvent( commentEditor, WindowEvent.WINDOW_CLOSING );
        commentEditor.dispatchEvent( event );
        TestUtils.pause( 10000 );
    }

    @Test
    void testGetComments()
    {
        fail("Not yet implemented");
    }

    @Test
    void testGetSelection()
    {
        fail("Not yet implemented");
    }
    
    private void runDialog( LibraryItem item )
    {
        DialogRunner    runner  = new DialogRunner( item );
        Thread          thread  = new Thread( runner, "Dialog Runner" );
        thread.start();
        TestUtils.pause( 100 );
    }
    
    private void selectQuestionDialogButton( String buttonText )
    {
//        Component   comp    = TestUtils.
    }
    
    /**
     * Insert a comment and validate the result.
     * 
     * First poke the insert button; validate:
     * <ol>
     * <li>A new row appears at the top of the table.</li>
     * <li>The new row is selected.</li>
     * <li>The text of the new row is empty.</li>
     * <li>The text area is enabled.</li>
     * <li>The text area is empty.</li>
     * <li>The text area has focus.</li>
     * </ol>
     * 
     * Enter the given text; validate:
     * <ol>
     * <li>Text is correctly inserted in text area.</li>
     * <li>Text is reflected in the first row of the table.</li>
     * <li>Delete button is enabled.</li>
     * </ol>
     * 
     * @param text  the given text
     */
    private void insertComment( String text )
    {
        int     startRowCount   = jTable.getRowCount();
        int     expRowCount     = startRowCount + 1;
        
        clickButton( insertButton );
        int     actRowCount     = jTable.getRowCount();
        assertEquals( expRowCount, actRowCount );
        
        int     selectedRow     = jTable.getSelectedRow();
        assertEquals( 0, selectedRow );
        
        String  rowText         = (String)tableModel.getValueAt( 0, 0 );
        assertTrue( rowText.isEmpty() );
        
        String  areaText        = jTextArea.getText();
        assertTrue( areaText.isEmpty() );
        assertTrue( jTextArea.isEnabled() );
        assertTrue( jTextArea.hasFocus() );
        assertTrue( deleteButton.isEnabled() );
        
        sendKeys( text );
        assertEquals( text, jTextArea.getText() );
        assertEquals( text, tableModel.getValueAt( 0, 0 ) );
    }
    
    /**
     * Append data to end of comment, and validate result.
     * 
     * <ol>
     * <li>Select given row.</li>
     * <li>Verify text from row is transferred to text area.</li>
     * <li>Append text to text area.</li>
     * <li>Verify text area is correctly modified.</li>
     * <li>Verify modification is reflected in selected row.</li>
     * </ol>
     * 
     * @param row       the row containing the comment to modify
     * @param toAppend  the test to append to the comment
     */
    private void appendToCommentAt( int row, String toAppend )
    {
        jTable.setRowSelectionInterval( row, row );
        jTextArea.grabFocus();
        String  areaText    = jTextArea.getText();
        Object  rowText     = tableModel.getValueAt( row, 0 );
        assertEquals( areaText, rowText );
        jTextArea.setCaretPosition( areaText.length() );
        sendKeys( toAppend );
        areaText += toAppend;
        assertEquals( areaText, jTextArea.getText() );
        rowText = tableModel.getValueAt( row, 0 );
        assertEquals( areaText, rowText );
    }
    
    /**
     * Select a given row in the table of comments.
     * Validate the resulting state:
     * 
     * <ol>
     * <li>The text of the comment is copied to the text area.</li>
     * <li>The text area is enabled.</li>
     * <li>The delete button is enabled.</li>
     * </ol>
     * 
     * @param row   the given row
     */
    private void selectCommentAt( int row )
    {
        selectionModel.setSelectionInterval( row, row );
        Object  expString   = jTable.getValueAt( row, 0 );
        String  actString   = jTextArea.getText();
        assertEquals( expString, actString );
        assertTrue( jTextArea.isEnabled() );
        assertTrue( deleteButton.isEnabled() );
    }
    
    /**
     * Delete a given row from the table.
     * Validate the result:
     * <ol>
     * <li>Table is shorter by one.</li>
     * <li>No rows in the table are selected.</li>
     * <li>JTextArea is empty.</li>
     * <li>JTextArea is disabled."</li>
     * <li>Delete button is disabled.</li>
     * </ol>
     * 
     * @param row   the given row
     */
    private  void deleteCommentAt( int row )
    {
        int     startSize   = tableModel.getRowCount();
        int     expSize     = startSize - 1;
        
        selectionModel.removeIndexInterval( row, row );
        int     actSize     = tableModel.getRowCount();
        assertEquals( expSize, actSize );
        
        int     selected    = jTable.getSelectedRow();
        assertTrue( selected < 0 );
        
        assertTrue( jTextArea.getText().isEmpty() );
        assertFalse( jTextArea.isEnabled() );
        assertFalse( deleteButton.isEnabled() );
    }
    
    /**
     * Get a comment from a Collection based on a predicate,
     * and an optional argument.
     * 
     * @param from      the Collection to search
     * @param match     the optional argument
     * @param pred      the given predicate
     * 
     * @return the first comment to match the predicate, or null if none
     */
    private Comment getComment(
        Collection<Comment> from, 
        Object match, 
        BiPredicate<Comment,Object> pred 
    )
    {
        for ( Comment comment : from )
        {
            if ( pred.test( comment, match ) )
                return comment;
        }
        return null;
    }
    
    /**
     * Click a button, and pause for the designated interval of time
     * to allow the GUI to catch up.
     * @param button
     */
    private void clickButton( AbstractButton button )
    {
        button.doClick();
        TestUtils.pause( clickButtonDelay );
    }

    /**
     * Use Robot to enter a String.
     * As of this time, only lower case characters, digits
     * and a handful of symbols can be typed.
     * @param keys
     */
    private void sendKeys( String keys)
    {
        final int       charUndefined = KeyEvent.CHAR_UNDEFINED;
        final String    err             = "key code not found";
        for (char c : keys.toCharArray())
        {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            assertNotEquals( charUndefined, keyCode, err );
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        }
    }
    
    private class DialogRunner implements Runnable
    {
        private final LibraryItem   item;
        
        public DialogRunner( LibraryItem item )
        {
            this.item = item;
        }
        
        public void run()
        {
            dialogStatus = commentEditor.showDialog( true, item );
        }
    }
}
