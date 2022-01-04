/**
 * 
 */
package kcls_manager.components;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

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
        
        table.setPreferredScrollableViewportSize(new Dimension( 500, 100 ));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(table);
        pane.add( scrollPane, BorderLayout.CENTER );
        pack();
    }
    
    public int showDialog( boolean visible )
    {
        setVisible( visible );
        return 0;
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

        public LocalTableModel( Object[] columnNames )
        {
            super( null, columnNames );
        }
        
        @Override
        public boolean isCellEditable( int row, int col )
        {
            return false;
        }
    }
}
