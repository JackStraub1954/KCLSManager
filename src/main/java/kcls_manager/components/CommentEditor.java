package kcls_manager.components;

import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.CANCEL;
import static kcls_manager.main.Constants.CANCEL_TEXT;
import static kcls_manager.main.Constants.DISCARD_TEXT;
import static kcls_manager.main.Constants.OKAY;
import static kcls_manager.main.Constants.SAVE_TEXT;
import static kcls_manager.main.Constants.TITLE_TYPE;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.Document;

import kcls_manager.main.Comment;
import kcls_manager.main.LibraryItem;
import kcls_manager.main.Title;

/**
 * A dialog for editing comments.
 * 
 * @author jstra
 *
 */
public class CommentEditor extends JDialog
{
    /** Generated serial version UID */
    private static final long serialVersionUID = -2022075362458625900L;
    
    private static final String loggerName  = CommentEditor.class.getName();
    private static final Logger logger      = Logger.getLogger( loggerName );
    
    private final JTextArea             textArea    = new JTextArea( 5, 30 );
    private final JTable                table       = new JTable();    
    private final DefaultTableModel     tableModel;
    private final ListSelectionModel    listModel;
    
    /** 
     * JTable column headings, 
     * including the "hidden column
     * which contains the source Comment object.
     */
    private final Object[]  columnHeadings  = { "Comment", "hidden" };
    /** The column that contains the text of the comment. */
    private final int       textColumn      = 0;
    /** The "hidden" column that contains the source Comment object. */
    private final int       dataColumn      = columnHeadings.length - 1;
    
    // Buttons at the bottom of the dialog that control
    // insert/delete etc.. They're global so that some of them
    // can be enabled/disabled depending on the state of
    // the table.
    private final JButton   okay    = new JButton( "OK" );
    private final JButton   cancel  = new JButton( "Cancel" );
    private final JButton   delete  = new JButton( "Delete" );
    private final JButton   insert  = new JButton( "Insert" );
    
    /** Comment type: TITLE_TYPE or AUTHOR_TYPE */
    private int     commentType     = 0;
    
    /** Currently selected row; the text of this row is reflected in the text area. */
    private int     selectedRow     = -1;
    
    /** 
     * True if a change has been made 
     * to the set of comments contained in the dialog.
     */
    private boolean modified        = false;
    
    /** Status on exit: OKAY or CANCEL */
    private int     selection       = 0;
    
    /**
     * Dialog for editing attributes 
     * (key/value pairs of Strings).
     * 
     * @param owner Owner of this dialog; may be null.
     */
    public CommentEditor( Window owner )
    {
        super( 
            owner, 
            "Comment Editor",
            Dialog.ModalityType.APPLICATION_MODAL
        );
        
        tableModel = new LocalTableModel( columnHeadings );
        table.setModel( tableModel );
        
        // Hide last column of table (the column containing the Comment object)
        TableColumnModel    columnModel = table.getColumnModel();
        columnModel.removeColumn( columnModel.getColumn( dataColumn ) );
        
        listModel = table.getSelectionModel();
        listModel.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        
        // Build the GUI
        setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        addWindowListener( new WindowMonitor() );
        
        JPanel  pane    = (JPanel)getContentPane();
        pane.setLayout( new BorderLayout() );
        
        Border  bevel   =
            BorderFactory.createBevelBorder( BevelBorder.LOWERED );
        Border  empty   = BorderFactory.createEmptyBorder( 10, 10, 10, 10 );
        Border  border  = BorderFactory.createCompoundBorder( empty, bevel );
        textArea.setBorder( border );
        textArea.setWrapStyleWord( true );
        textArea.setLineWrap( true );
        
        // Document stuff: indicates when text has been changed, potentially 
        // resulting in "do you want to save..." logic
        Document    document    = textArea.getDocument();
        document.addDocumentListener( new TextAreaListener() );

        pane.add( textArea, BorderLayout.NORTH );
        pane.add( new MainPanel(), BorderLayout.CENTER );
        pane.add( new ButtonPanel(), BorderLayout.SOUTH );
        
        border  = BorderFactory.createEmptyBorder( 5, 5, 5, 5 );
        pane.setBorder( border );
        pack();
    }
    
    /**
     * Set the visibility of this dialog.
     * If visibility is set to true, 
     * the initial content of the attributes table
     * will be any values passed to set/mergeAttributes()
     * along plus any "left-over" attributes from
     * the dialog's last use, merged with the map
     * indicated by the given map.
     * The return value indicates whether the dialog was dismissed
     * by selecting "OK" or "Cancel."
     * 
     * @param   visible     True to make the dialog visible;
     *                      false to hide the dialog
     * @param   item        Item with which to associate the comments;
     *                      may be <em>null</em> if visible is <em>false</em>
     * 
     * @return  A value indicating how the dialog was dismissed
     */
    public int showDialog( boolean visible, LibraryItem item )
    {
        logger.info( "show dialog: " + visible + ", " + item );
        if ( visible )
        {
            commentType = item instanceof Title ? TITLE_TYPE : AUTHOR_TYPE;
            // Delete all rows from the table
            int numRows = tableModel.getRowCount();
            for ( int inx = numRows - 1 ; inx >= 0 ; --inx )
            {
                tableModel.removeRow( inx );
            }

            // Add comment text to first column of table;
            // add comment to second ("hidden") column.
            for ( Comment comment : item.getComments() )
            {
                String      text    = comment.getText();
                Object[]    row     = { text, new Comment( comment ) };
                tableModel.addRow( row );
            }
            
            // Set components will enabled/disabled as necessary.
            listModel.clearSelection();
            setComponentState();
            setModified( false );
        }
        setVisible( visible );
        return selection;
    }
    
    /**
     * Gets a <em>copy</em> of the current comment list.
     * Changes to the copy will not be reflected in the original.
     * 
     * @return A <em>copy</em> of the current comment list.
     */
    public List<Comment> getComments()
    {
        List<Comment>   tempCollection  = new ArrayList<>();
        int             rowCount        = table.getRowCount();
        
        TableModel  model       = table.getModel();
        for ( int row = 0 ; row < rowCount ; ++row )
        {
            String  text    = (String)model.getValueAt( row, textColumn );
            Comment comment = (Comment)model.getValueAt( row, dataColumn );
            comment.setText( text );
            tempCollection.add( comment );
        }
        return tempCollection;
    }
    
    /**
     * Gets the value of the last selection to dismiss the dialog
     * (OK or CANCEL).
     * 
     * @return The value of the last selection to dismiss the dialog
     */
    public int getSelection()
    {
        return selection;
    }
    
    /**
     * Sets a property indicating whether or not the dialog has been modified.
     * Currently the property is set, but never reset,
     * except when the dialog is newly displayed.
     * @param modified
     */
    private void setModified( boolean modified )
    {
        this.modified = modified;
    }
    
    /**
     * Upon OK selection, the comments list must be made 
     * equivalent to the contents of the JTable.
     */
    private void selectOK()
    {
        int     rowCount    = table.getRowCount();
        for ( int row = 0 ; row < rowCount ; ++row )
        {
            String  text    = (String)tableModel.getValueAt( row, textColumn );
            Comment comment = (Comment)tableModel.getValueAt( row, dataColumn );
            comment.setText( text );
        }
        
        selection = OKAY;
        showDialog( false, null );
    }
    
    /**
     * Upon Cancel selection, the comments list must not be
     * modified; i.e., changes to the JTable are discarded.
     */
    private void selectCancel()
    {
        selection = CANCEL;
        showDialog( false, null );
    }
    
    /*
     * Enable/disable components as indicated:
     * <ul>
     * <li>
     * Row selected
     * <ol>
     * <li>Delete enabled</li>
     * <li>Text area enabled</li>
     * </ol>
     * </li>
     * <li>
     * No row selected
     * <ol>
     * <li>Delete disabled</li>
     * <li>Text area disabled</li>
     * </ol>
     * </li>
     * </ul>
    */
    private void setComponentState()
    {
        int     selectedRow = table.getSelectedRow();
        boolean enabled     = selectedRow < 0 ? false : true;
        delete.setEnabled( enabled );
        textArea.setEnabled( enabled );
        if ( !enabled )
            textArea.setText( "" );
    }
    
    /**
     * Adds a new comment to the table
     * based on the content of the insert row
     * (row 0);
     * subsequently the insert row
     * is initialized to the empty string
     * and the insert row is deselected.
     * 
     * If the comment is empty,
     * no action is taken.
     */
    private void insert()
    {
        Object[]    newRow  = { "", new Comment( commentType, "" ) };
        listModel.clearSelection();
        tableModel.insertRow( 0, newRow );
        table.setRowSelectionInterval( 0, 0 );
        
        Rectangle   rect    = table.getCellRect( 0, 0, true );
        table.scrollRectToVisible( rect );
        
        textArea.grabFocus();
    }
    
    private void selectRow( ListSelectionEvent evt )
    {
        if ( !evt.getValueIsAdjusting() )
        {
            selectedRow = table.getSelectedRow();
//            if ( selectedRow >= tableModel.getRowCount() )
//                selectedRow = -1;
            if ( selectedRow >= 0 )
            {
                String  text    = 
                    (String)tableModel.getValueAt( selectedRow, textColumn );
                textArea.setText( text );
                textArea.setEnabled( true );
            }
            setComponentState();
        }
    }
    
    /**
     * Delete the selected row from the table.
     */
    private void delete()
    {
        selectedRow = table.getSelectedRow();
        if ( selectedRow >= 0 )
        {
            tableModel.removeRow( selectedRow );
            setModified( true );
        }
    }
        
    private class MainPanel extends JPanel
    {
        /** Generated Serial Version UID */
        private static final long serialVersionUID = 8101447693673412903L;

        public MainPanel()
        {
            super( new BorderLayout() );
            table.setPreferredScrollableViewportSize(new Dimension( 500, 100 ));
            table.setFillsViewportHeight(true);
            table.setAutoCreateRowSorter(true);
            
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            //Create the scroll pane and add the table to it.
            JScrollPane scrollPane = new JScrollPane(table);

            //Add the scroll pane to this panel.
            add(scrollPane);
        }
    }
    
    private class ButtonPanel extends JPanel
    {
        /** Generated Serial Version UID */
        private static final long serialVersionUID = -2300840594653999035L;

        public ButtonPanel()
        {
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            
            Insets  insets  = ComponentUtils.toInsetsFromEM( this, .5f );
            Border  border  = new EmptyBorder( insets );
            setBorder( border );
            
            Dimension   horizontalDim   = 
                ComponentUtils.toDimensionFromEM( this, .5f, 0 );
            Dimension   verticalDim     =
                ComponentUtils.toDimensionFromEM( this, 0, .5f );
            
            JPanel  topPanel        = new JPanel();
            topPanel.setLayout( new BoxLayout( topPanel, BoxLayout.X_AXIS ) );
            topPanel.add( delete );
            topPanel.add( Box.createRigidArea( horizontalDim ) );
            topPanel.add( insert );
            add( topPanel );
            
            add( Box.createRigidArea( verticalDim ) );
            
            JPanel  botPanel        = new JPanel();
            botPanel.setLayout( new BoxLayout( botPanel, BoxLayout.X_AXIS ) );
            botPanel.add( okay );
            botPanel.add( Box.createRigidArea( horizontalDim ) );
            botPanel.add( cancel );
            add( botPanel );
            
            insert.addActionListener( e -> insert() );
            cancel.addActionListener( e -> selectCancel() );
            okay.addActionListener( e -> selectOK() );
            delete.addActionListener( e -> delete() );
            listModel.addListSelectionListener( e -> selectRow( e ) );
        }
    }
    
    /**
     * Monitors changes to the JTextArea.
     * 
     * @author jstra
     */
    private class TextAreaListener implements DocumentListener
    {
        @Override
        public void insertUpdate(DocumentEvent e)
        {
            update();
        }

        @Override
        public void removeUpdate(DocumentEvent e)
        {
            update();
        }

        @Override
        public void changedUpdate(DocumentEvent e)
        {
            // not invoked by this application
        }
        
        /**
         * Invoked whenever a change is made to the text area.
         * The contents of the text area are transferred 
         * to the currently select row in the JTable,
         * and the dialog is marked "modified."
         */
        private void update()
        {
            int row = table.getSelectedRow();
            if ( row >= 0 )
            {
                String  text    = textArea.getText();
                tableModel.setValueAt( text, row, textColumn );
            }
            setModified( true );
        }
    }
    
    /**
     * Defines the behavior when the operator closes the dialog
     * (via the dialog close button or keyboard shortcut).
     * Gives the operator the chance to save changes before exiting.
     * 
     * @author jstra
     */
    private class WindowMonitor extends WindowAdapter
    {
        @Override
        public void windowClosing( WindowEvent evt )
        {
            System.out.println( "window closing" );
            final String    save        = SAVE_TEXT;
            final String    discard     = DISCARD_TEXT;
            final String    cancel      = CANCEL_TEXT;
            
            final String    message     = "Do you want to save your changes?";
            final String    title       = "Save or Discard Changes";
            final int       optionType  = JOptionPane.DEFAULT_OPTION;
            final int       messageType = JOptionPane.QUESTION_MESSAGE;
            final Icon      icon        = null;
            final String[]  options     = { save, discard, cancel };
            final String    initialVal  = save;
            
            if ( modified )
            {
                int option  = JOptionPane.showOptionDialog(
                    CommentEditor.this, 
                    message, 
                    title, 
                    optionType, 
                    messageType, 
                    icon, 
                    options, 
                    initialVal
                );
                
                String  selection   = option >= 0 ? options[option] : "";
                
                switch ( selection )
                {
                case save:
                    selectOK();
                    break;
                case discard:
                    selectCancel();
                    break;
                case cancel:
                    break;
                default:
                    break;
                }
            }
            else
                selectCancel();
        }
    }
    
    /**
     * Represents the TableModel for the contained JTable.
     * It is used to force cells to be non-editable.
     * 
     * @author jstra
     */
    private class LocalTableModel extends DefaultTableModel
    {
        /** Generated serial version UID */
        private static final long serialVersionUID = -7084345663961459127L;

        public LocalTableModel( Object[] comments )
        {
            super( null, comments );
        }
        
        @Override
        public boolean isCellEditable( int row, int col )
        {
            return false;
        }
    }
}
