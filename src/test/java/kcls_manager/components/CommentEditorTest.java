package kcls_manager.components;

import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.CANCEL;
import static kcls_manager.main.Constants.CANCEL_TEXT;
import static kcls_manager.main.Constants.DELETE_TEXT;
import static kcls_manager.main.Constants.DISCARD_TEXT;
import static kcls_manager.main.Constants.INSERT_TEXT;
import static kcls_manager.main.Constants.OKAY;
import static kcls_manager.main.Constants.OK_TEXT;
import static kcls_manager.main.Constants.SAVE_TEXT;
import static kcls_manager.main.Constants.TITLE_TYPE;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiPredicate;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

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
//    private static final String loggerName  = 
//        CommentEditorTest.class.getName();
//    private static final Logger logger      = Logger.getLogger( loggerName );
    
    /**  MSecs to wait after starting comment editor. */
    private static final int    runDialogDelay      = 100;
    /** MSecs to wait after clicking a button. */
    private final int           clickButtonDelay    = 50;
    /** MSecs to wait after dispatching an event. */
    private static final int    dispatchEventDelay  = 100;
    
    private JTextArea           jTextArea;
    private JTable              jTable;
    private DefaultTableModel   tableModel;
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
        commentFactory = new CommentFactory();
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
        tableModel = (DefaultTableModel) jTable.getModel();
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
        Author          author          = authorFactory.getUniqueAuthor( 0 );
        int             commentCount    = 5;
        List<Comment>   comments        = 
            getSequentialComments( AUTHOR_TYPE, commentCount );
        
        runDialog( author );
        for ( Comment comment : comments )
            insertComment( comment.getText() );
        
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
            System.out.println( "appending to " + row );
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
        assertEquals( OKAY, dialogStatus );
        List<Comment>   actualComments  = commentEditor.getComments();
        TestUtils.assertCommentsEqual( synch, actualComments );
    }
    
    /**
     * Verify that, after being closed, the comment editor's
     * getSelection method
     * returns the correct status.
     */
    @Test
    public void testGetSelection()
    {
        Title   title   = titleFactory.getUniqueTitle( 0 );
        runDialog( title );
        clickButton( okButton );
        assertEquals( OKAY, dialogStatus );
        assertEquals( OKAY, commentEditor.getSelection() );
        
        runDialog( title );
        clickButton( cancelButton );
        assertEquals( CANCEL, dialogStatus );
        assertEquals( CANCEL, commentEditor.getSelection() );
    }
    
    /**
     * Click the comment editor's save button, prompting
     * a JOptionPane question dialog to be displayed.
     * Verify that clicking the save button in the JOptionPane
     * translates into clicking the OK button for the comment editor.
     */
    @Test
    public void testCloseSave()
    {
        testClose( SAVE_TEXT );
        assertEquals( OKAY, dialogStatus );
    }
    
    /**
     * Click the comment editor's close button, prompting
     * a JOptionPane question dialog to be displayed.
     * Verify that clicking the discard button in the JOptionPane
     * translates into clicking the cancel button for the comment editor.
     */
    @Test
    public void testCloseDiscard()
    {
        testClose( DISCARD_TEXT );
        assertEquals( CANCEL, dialogStatus );
    }
    
    /**
     * Verify: if no changes have been made to the comment editor, and
     * the comment editor frame's close button is poked
     * no confirmation message is displayed, the editor is made invisible,
     * and the status returned is CANCEL.
     */
    @Test
    public void testCloseNoMods()
    {
        Author          author  = authorFactory.getUniqueAuthor( 5 );
        List<Comment>   orig    = copyComments( author.getComments() );
        runDialog( author );
        
        WindowEvent event   = 
            new WindowEvent( commentEditor, WindowEvent.WINDOW_CLOSING );
        dispatchEvent( commentEditor, event );
        
        BiPredicate<Component,Object>   findOptionPanePred  =
            (c,o) -> c.isVisible() && c instanceof JOptionPane;
        Component   comp    = TestUtils.getComponent( findOptionPanePred, null );
        assertNull( comp );
        assertFalse( commentEditor.isVisible() );
        assertEquals( CANCEL, dialogStatus );
        TestUtils.assertCommentsEqual( orig, commentEditor.getComments() );
    }
    
    /**
     * Delete the first and last comment from a list, then delete one
     * from anywhere in the middle of the list. Validate the result.
     */
    @Test
    public void testDeleteSome()
    {
        int     commentCount    = 10;
        
        // Sanity check. We want to delete the first and last comment,
        // and a comment from the middle, and still have comments left over.
        // For this strategy, any comment count greater than five 
        // will suffice.
        assertTrue( commentCount > 5 );
        Title           title   = titleFactory.getUniqueTitle( commentCount );
        List<Comment>   orig    = copyComments( title.getComments() );
        runDialog( title );
        
        // -2: account for first deleted
        // -3: account for first and last deleted
        int[]       toDelete    = { 0, commentCount - 2, (commentCount - 3) / 2 };
        for ( int row : toDelete )
        {
            selectCommentAt( row );
            String      text    = jTextArea.getText();
            String[]    parts   = text.split( " " );
            assertTrue( parts.length >= 1 );
            
            BiPredicate<Comment,Object> pred    = 
                (c,o) -> c.getText().startsWith( o.toString() );
            Comment comment = getComment( orig, parts[0], pred );
            assertNotNull( comment );
            
            orig.remove( comment );
            deleteCommentAt( row );
        }
        
        List<Comment>   actual  = commentEditor.getComments();
        TestUtils.assertCommentsEqual( orig, actual );
    }
    
    /**
     * Delete the first and last comment from a list, then delete one
     * from anywhere in the middle of the list. Validate the result.
     */
    @Test
    public void testDeleteAll()
    {
        int             commentCount    = 5;
        Title           title           = 
            titleFactory.getUniqueTitle( commentCount );
        runDialog( title );
        
        while ( jTable.getRowCount() > 0 )
            deleteCommentAt( 0 );
        
        List<Comment>   actual  = commentEditor.getComments();
        assertTrue( actual.isEmpty() );
    }
    
    /**
     * Click the comment editor's close button, prompting
     * a JOptionPane question dialog to be displayed.
     * Verify that clicking the cancel button in the JOptionPane
     * stops the comment editor from being closed.
     */
    @Test
    public void testCloseCancel()
    {
        int unexpectedStatus    = -1;
        dialogStatus = unexpectedStatus;
        testClose( CANCEL_TEXT );
        assertEquals( unexpectedStatus, dialogStatus );
        assertTrue( commentEditor.isVisible() );
    }
    
    /**
     * Click the comment editor's close button, prompting
     * a JOptionPane question dialog to be displayed.
     * Verify that clicking the JOptionPane's close button
     * stops the comment editor from being closed.
     */
    @Test
    public void testCloseClose()
    {
        int unexpectedStatus    = -1;
        Author  author  = authorFactory.getUniqueAuthor( 5 );
        dialogStatus = unexpectedStatus;
        runDialog( author );
        
        // must make a change to the comment editor in order for the 
        // question dialog to be displayed; 
        // then cancel the comment editor.
        insertComment( "this is a new comment" );
        
        // Simulate poking of editor's close button.
        WindowEvent event   = 
            new WindowEvent( commentEditor, WindowEvent.WINDOW_CLOSING );
        dispatchEvent( commentEditor, event );
        
        // Simulate poking of question dialog's close button.
        JDialog     dialog      = getQuestionDialog();
        Frame       frame       = JOptionPane.getFrameForComponent( dialog );
        event = new WindowEvent( frame, WindowEvent.WINDOW_CLOSING );
        dispatchEvent( dialog, event );

        assertEquals( unexpectedStatus, dialogStatus );
        assertTrue( commentEditor.isVisible() );
    }
    
    /**
     * Executes most of the logic to test what happens when a user
     * selects the comment editor's close button.
     * What should happen is that the comment editor displays
     * a JOptionPane containing the choices save, discard or cancel.
     * 
     * <ol>
     * <li>Start the comment editor.</li>
     * <li>Make a modification to a comment in the editor.</li>
     * <li>Dispatch an event to select the editor's close button.</li>
     * <li>Click the indicated button in the JOptionPane.<li>
     * </ol>
     * 
     * @param buttonText    the text of the button in the JOptionPane to click.
     * 
     * @return  a list of comments that should be returned
     *          by commentEditor.getComments().
     */
    private void testClose( String buttonText )
    {
        int     commentCount    = 5;
        int     rowNum          = commentCount / 2 ;

        // Appended to existing comment to modify the content
        // of the dialog editor.
        final String    suffix  = " suffix";
        
        // Used later to find original the comment that will be updated
        // as part of this test.
        Object  origText        = null;
        
        // Sanity check: rowNum intended to be after the first row
        // and before the last row.
        assertTrue( rowNum > 0 && rowNum < commentCount - 1 );
        
        Author  author  = authorFactory.getUniqueAuthor( commentCount );
        
        // Keep copies of the original comments for later validation
        List<Comment>   exp     = copyComments( author.getComments() );
        runDialog( author );
        
        // Modify a comment in the comment editor;
        // keep track of the original text of the comment.
        origText = tableModel.getValueAt( rowNum, 0 );
        appendToCommentAt( rowNum, suffix );
        
        // Simulate poking of editor's close button.
        WindowEvent event   = 
            new WindowEvent( commentEditor, WindowEvent.WINDOW_CLOSING );
        dispatchEvent( commentEditor, event );
        
        // Find the question dialog that should now be displayed.
        JDialog dialog  = getQuestionDialog();
        
        // Bit of logic to shorten the subsequent couple of lines of code
        BiPredicate<Component,Object>   textFinder  = TestUtils.textFinder;
        
        // Find the button in the question dialog that should be clicked
        AbstractButton  targetButton    = (AbstractButton)
            TestUtils.getComponent( dialog, textFinder, buttonText );
        clickButton( targetButton );
        
        // verify that the original comments have NOT been changed (yet)
        TestUtils.assertCommentsEqual( exp, author.getComments() );
        
        // Synchronize the copy of the original comments with the 
        // changes made in the comment editor.
        Comment toSynch = null;
        for ( Comment comment : exp )
        {
            if ( origText.equals( comment.getText() ) )
            {
                toSynch = comment;
                break;
            }
        }
        assertNotNull( toSynch );
        toSynch.setText( origText + suffix );
        
        // Whatever button was used to dismiss the question dialog,
        // the comments returned by commentEditor.getComments()
        // should reflect the change that was made, above.
        TestUtils.assertCommentsEqual( exp, commentEditor.getComments() );
    }
    
    private void runDialog( LibraryItem item )
    {
        DialogRunner    runner  = new DialogRunner( item );
        Thread          thread  = new Thread( runner, "Dialog Runner" );
        thread.start();
        TestUtils.pause( runDialogDelay );
    }
    
    /**
     * Find the question dialog that is displayed when the operator
     * selects the comment editor's close button.
     * An assertion is thrown if the operation fails.
     * 
     * @return  the question dialog that is displayed when the operator
     *          selects the comment editor's close button
     */
    private JDialog getQuestionDialog()
    {
        BiPredicate<Component,Object>   findOptionPanePred  =
            (c,o) -> c.isVisible() && c instanceof JOptionPane;

        Component   comp    = TestUtils.getComponent( findOptionPanePred, null );
        assertNotNull( comp );
        assertTrue( comp instanceof JOptionPane);
        
        JDialog dialog  = TestUtils.getJDialogForComponent( comp, true );
        assertNotNull( dialog );
        
        return dialog;
        
    }
    
    /**
     * Start a new thread to dispatch an event.
     * Pause for a designated amount of time to give the event
     * a chance to be delivered.
     * 
     * @param comp      component to which an event is to be dispatches
     * @param event     event to be dispatched
     */
    private void dispatchEvent( Component comp, AWTEvent event )
    {
        Thread  thread  = 
            new Thread( () -> comp.dispatchEvent( event ), "Dispatch Event" );
        thread.start();
        TestUtils.pause( dispatchEventDelay ); 
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
        
        selectCommentAt( row );
//        tableModel.removeRow( row );
        deleteButton.doClick();
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
        if ( keys.equals( " modified" ) )
            System.out.println( keys );
        for (char c : keys.toCharArray())
        {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            assertNotEquals( charUndefined, keyCode, err );
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        }
        // give GUI thread a chance to settle
        TestUtils.pause( 100 );
    }
    
    /**
     * Make duplicates of all comments in a list
     * and return them in a new list.
     * 
     * @param from  list of comments to duplicate
     * 
     * @return list of duplicates of the objects from <em>from</em>.
     */
    private List<Comment> copyComments( List<Comment> from )
    {
        List<Comment>   too = new ArrayList<>();
        for ( Comment comment : from )
            too.add( new Comment( comment ) );
        return too;
    }
    
    /**
     * Gets a list of comments that begin with a unique, sequential prefix.
     * The text of each comment begins with <em>"nnn "</em>
     * where <em>n</em> is a digit between 0 and 9.
     * 
     * @param type      the type of the comment (TITLE_TYPE or AUTHOR_TYPE)
     * @param count     the number of comments to get
     * 
     * @return a list of comments that begin with a unique, sequential prefix
     */
    private List<Comment> getSequentialComments( int type, int count )
    {
        final String    fmt         = "%03d %s";
        List<Comment>   comments    = new ArrayList<>();
        for ( int inx = 0 ; inx < count ; ++inx )
        {
            Comment comment = commentFactory.getUniqueComment( -1 );
            comment.setType( type );
            comment.setItemID( OptionalInt.empty() );
            String  oldText = comment.getText();
            String  newText = String.format( fmt, inx, oldText );
            comment.setText( newText );
            comments.add( comment );
        }
        return comments;
    }
    
    private class DialogRunner implements Runnable, Comparator<Integer>
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

        @Override
        public int compare(Integer o1, Integer o2)
        {
            // TODO Auto-generated method stub
            return 0;
        }
    }
}
