package kcls_manager.components;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import kcls_manager.main.KCLSException;
import kcls_manager.main.Title;
import kcls_manager.main.TitleContentProvider;

/**
 * Dialog that displays lists of titles. 
 * Permits editing, inserting and deleting titles.
 * 
 * @author johns
 * @see TitleEditor
 *
 */
public class TitleDialog extends JDialog
{
    private static final String loggerName  = TitleDialog.class.getName();
    private static final Logger logger      = Logger.getLogger( loggerName );
    
    private final JTable                table       = new JTable();    
    private final DefaultTableModel     tableModel;
    private final ListSelectionModel    listModel;
    private final TitleContentProvider  contentProvider;
    
    /** Headings for the JTable columns. */
    private final Object[]              columnHeadings;
    /** The "hidden" column that contains the source Title object. */
    private final int                   dataColumn;

    public TitleDialog( TitleContentProvider contentProvider )
    {
        super( 
            null, 
            contentProvider.getDialogTitle(),
            Dialog.ModalityType.APPLICATION_MODAL
        );
        this.contentProvider = contentProvider;
        
        columnHeadings = contentProvider.getHeaders();
        dataColumn = columnHeadings.length - 1;
        tableModel = new LocalTableModel( columnHeadings );
        table.setModel( tableModel );
        table.setDefaultRenderer( Date.class, new DateCellRenderer() );
        
        // Hide last column of table (the column containing the Comment object)
        TableColumnModel    columnModel = table.getColumnModel();
        columnModel.removeColumn( columnModel.getColumn( dataColumn ) );
        
        listModel = table.getSelectionModel();
        listModel.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        
        Iterator<Object[]>  iter    = contentProvider.iterator();
        while ( iter.hasNext() )
            tableModel.addRow( iter.next() );
        
        // Build the GUI
        setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        addWindowListener( new WindowMonitor() );
        
        JPanel  pane    = (JPanel)getContentPane();
        pane.setLayout( new BorderLayout() );
        Border  border      = 
            BorderFactory.createEmptyBorder( 10, 10, 10, 10 );
        pane.setBorder( border );
        
        table.setPreferredScrollableViewportSize(new Dimension( 500, 100 ));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(table);
        pane.add( scrollPane, BorderLayout.CENTER );
        
        pane.add( new ButtonPanel(), BorderLayout.SOUTH );
        pack();
    }
    
    public int showDialog( boolean visible )
    {
        setVisible( visible );
        return 0;
    }
    
    private void addRow()
    {
        Title   title   = new Title();
        // showTitleEditor( title );
    }
    
    private void editSelected()
    {
        Title   title   = getSelectedTitle();
        // showTitleEditor( title );
    }
    
    private void deleteSelected()
    {
        int     selectedInx     = getSelectedRow();
        table.remove( selectedInx );
    }
    
    private void apply()
    {
        
    }
    
    private void cancel()
    {
        setVisible( false );
    }
    
    private void close()
    {
        apply();
        setVisible( false );
    }
    
    private int getSelectedRow()
    {
        int     selectedInx     = table.getSelectedRow();
        if ( selectedInx < 0 )
        {
            String  message = 
                "Edit button selected when no active selection";
            throw new KCLSException( message );
        }
        return selectedInx;
    }
    
    private Title getSelectedTitle()
    {
        int     selectedInx     = getSelectedRow();
        Object  obj             = 
            tableModel.getValueAt( selectedInx,  dataColumn );
        if ( !(obj instanceof Title) )
        {
            String  type    = obj.getClass().getName();
            String  message = 
                "Expected type \"Title\"; was \"" + type + "\"";
            throw new KCLSException( message );
        }
        
        Title   title   = (Title)obj;
        return title;
    }
    
    private class WindowMonitor extends WindowAdapter
    {
        @Override
        public void windowClosing( WindowEvent evt )
        {
            System.out.println( "window closing" );
            showDialog( false );
        }
    }
    
    private class ButtonPanel extends JPanel
    {
        public ButtonPanel()
        {
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            JPanel  topPanel    = new JPanel();
            JPanel  bottomPanel = new JPanel();
            add( topPanel );
            add( bottomPanel );
            
            JButton addRow  = new JButton( "Add Row" );
            JButton edit    = new JButton( "Edit Selected" );
            JButton delete  = new JButton( "Delete Selected" );
            topPanel.add( addRow );
            topPanel.add( edit );
            topPanel.add( delete );
            
            JButton close   = new JButton( "Save and Close" );
            JButton apply   = new JButton( "Apply" );
            JButton cancel  = new JButton( "Cancel" );
            bottomPanel.add( close );
            bottomPanel.add( apply );
            bottomPanel.add( cancel );
            
            addRow.addActionListener( e -> addRow() );
            edit.addActionListener( e -> editSelected() );
            delete.addActionListener( e -> deleteSelected() );
            close.addActionListener( e -> close() );
            apply.addActionListener( e -> apply() );
            cancel.addActionListener( e -> cancel() );
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

        /**
         * Initializes this TableModel.
         * 
         * @param columnNames   names of the columns in the model
         */
        public LocalTableModel( Object[] columnNames )
        {
            super( null, columnNames );
        }
        
        /**
         *  Obtain the class of data for a particular column.
         *  This allows data to be formatted correctly, e.g.,
         *  numeric fields are right justified, date fields
         *  are displayed correctly.
         *  
         *  @param  columnIndex index of target column
         */
        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            Object      data    = getValueAt( 0, columnIndex );
            Class<?>    clazz   = data.getClass();
            return clazz;
        }
        @Override
        public boolean isCellEditable( int row, int col )
        {
            return false;
        }
    }
}
