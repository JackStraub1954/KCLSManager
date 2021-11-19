package kcls_manager.components;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

/**
 * A dialog for editing comments.
 * 
 * @author jstra
 *
 */
public class CommentEditor extends JDialog
{
    private final Set<String>           comments    = new HashSet<>();
    
    private final JTextArea             textArea    = new JTextArea( 5, 30 );
    private final JTable                table       = new JTable();    
    private final DefaultTableModel     tableModel;
    private final ListSelectionModel    listModel;
    private int                         selection       = 0;
    
    // Buttons at the bottom of the dialog that control
    // insert/delete etc.. They're global so that some of them
    // can be enabled/disabled depending on the state of
    // the table.
    private final JButton   okay    = new JButton( "OK" );
    private final JButton   cancel  = new JButton( "Cancel" );
    private final JButton   delete  = new JButton( "Delete" );
    private final JButton   insert  = new JButton( "Insert" );
    private final JButton   edit    = new JButton( "Edit" );
    private final JButton   apply   = new JButton( "Apply" );
    
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
        
        // Initialize the JTable, including the insert row
        Object[]    columns     = { "Comment" };
        
        tableModel = new LocalTableModel( columns );
        table.setModel( tableModel );
        
        listModel = table.getSelectionModel();
        listModel.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        listModel.addListSelectionListener( e -> monitorSelection( e ) );
        
        // Build the GUI
        setDefaultCloseOperation( JDialog.HIDE_ON_CLOSE );
        
        JPanel  pane    = (JPanel)getContentPane();
        pane.setLayout( new BorderLayout() );
        
        Border  bevel   =
            BorderFactory.createBevelBorder( BevelBorder.LOWERED );
        Border  empty   = BorderFactory.createEmptyBorder( 10, 10, 10, 10 );
        Border  border  = BorderFactory.createCompoundBorder( empty, bevel );
        textArea.setBorder( border );
        textArea.setWrapStyleWord( true );
        textArea.setLineWrap( true );

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
     * the initial content of the comments table
     * will be any values passed to set/mergeComments()
     * along with any "left-over" comments from
     * the dialog's last use.
     * The return value indicates whether the dialog was dismissed
     * by selecting "OK" or "Cancel."
     * 
     * @param visible   True to make the dialog visible;
     *                  false to hide the dialog.
     * 
     * @return  A value indicating how the dialog was dismissed
     */
    public int display( boolean visible )
    {
        return display( visible, null );
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
     *                      false to hide the dialog.
     * @param   inputComments  The given map
     * 
     * @return  A value indicating how the dialog was dismissed
     */
    public int display( boolean visible, Collection<String> inputComments )
    {
        if ( visible )
        {
            comments.clear();
            if ( inputComments != null )
                comments.addAll( inputComments );

            // Delete all rows from the table
            int numRows = tableModel.getRowCount();
            for ( int inx = numRows - 1 ; inx >= 0 ; --inx )
            {
                tableModel.removeRow( inx );
            }
            
            for ( String comment : comments )
            {
                Object[]    row     = { comment };
                tableModel.addRow( row );
            }
            
            // Set components will enabled/disabled as necessary.
            listModel.clearSelection();
            setComponentState();
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
    public List<String> getComments()
    {
        List<String>    tempCollection    = new ArrayList<>();
        tempCollection.addAll( comments );
        return tempCollection;
    }
    
    /**
     * Empties the current attribute map.
     */
    public void clearComments()
    {
        comments.clear();
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
     * Update the selected row with the contents of the text area.
     * Ignore if text area is empty.
     */
    private void updateRow()
    {
    }
    
    /**
     * Edit the first selected row.
     * Copy the selected comment into the text area,
     * then give the text area focus.
     */
    private void editRow()
    {
    }
    
    /**
     * Upon OK selection, the comments list must be made 
     * equivalent to the contents of the JTable.
     */
    private void selectOK()
    {
    }
    
    /**
     * Upon Cancel selection, the comments list must not be
     * modified; i.e., changes to the JTable are discarded.
     */
    private void selectCancel()
    {
    }
    
    /**
     * Monitor row selection.
     * 
     * @param evt   The object associated with this event.
     */
    private void monitorSelection( ListSelectionEvent evt )
    {
        setComponentState();
     }
    
    /*
     * Enable/disable components as indicated:
     * <ul>
     * <li>
     * Row selected
     * <ol>
     * <li>Delete enabled</li>
     * <li>Insert enabled</li>
     * <li>Update enabled</li>
     * <li>Text area enabled</li>
     * </ol>
     * </li>
     * <li>
     * No row selected
     * <ol>
     * <li>Delete disabled</li>
     * <li>Insert enabled</li>
     * <li>Update disabled</li>
     * <li>Text area disabled</li>
     * </ol>
     * </li>
     * </ul>
    */
    private void setComponentState()
    {
        int[]   selectedRows    = listModel.getSelectedIndices();
        if ( selectedRows.length > 0 )
        {
            insert.setEnabled( true );
            apply.setEnabled( true );
            delete.setEnabled( true );
            textArea.setEnabled( true );
            
            int row = selectedRows[0];
            Object  comment = tableModel.getValueAt( row, 0 );
            textArea.setText( comment.toString() );
        }
        else
        {
            insert.setEnabled( true );
            apply.setEnabled( false );
            delete.setEnabled( false );
            edit.setEnabled( false );
            textArea.setText( "" );
            textArea.setEnabled( false );
        }
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
    }
    
    /**
     * Delete the selected row from the table.
     * Be sure not to delete the insert row.
     */
    private void delete()
    {
    }
        
    private class MainPanel extends JPanel
    {
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
            topPanel.add( edit );
            topPanel.add( Box.createRigidArea( horizontalDim ) );
            topPanel.add( insert );
            add( topPanel );
            
            add( Box.createRigidArea( verticalDim ) );
            
            JPanel  botPanel        = new JPanel();
            botPanel.setLayout( new BoxLayout( botPanel, BoxLayout.X_AXIS ) );
            botPanel.add( apply );
            botPanel.add( Box.createRigidArea( horizontalDim ) );
            botPanel.add( okay );
            botPanel.add( Box.createRigidArea( horizontalDim ) );
            botPanel.add( cancel );
            add( botPanel );
        }
    }
    
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
