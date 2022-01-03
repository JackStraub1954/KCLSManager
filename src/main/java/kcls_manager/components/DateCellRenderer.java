package kcls_manager.components;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DateCellRenderer extends DefaultTableCellRenderer
{
    private static final String className   = DateCellRenderer.class.getName();
    private static final String loggerName  = DateCellRenderer.class.getName();
    private static final Logger logger      = Logger.getLogger( loggerName );
    
    private static final SimpleDateFormat   dateFormatter   =
        new SimpleDateFormat( "dd-MM-yyyy" );

    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column
    )
    {
        Component   comp        = null;
        Object      newValue    = value;
        if ( !(value instanceof Date) )
        {
            String  actual  = value.getClass().getName();
            String  message = 
                "Unexpected type; expected: " + className
                + "actual: " + actual;
            logger.severe( message );
        }
        else
            newValue = dateFormatter.format( value );
        comp = super.getTableCellRendererComponent(
            table, 
            newValue, 
            isSelected, 
            hasFocus, 
            row, 
            column
        );
    
        return comp;
    }
}
