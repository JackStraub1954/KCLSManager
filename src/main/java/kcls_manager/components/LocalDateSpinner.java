package kcls_manager.components;

import static kcls_manager.main.Constants.ZONE_ID;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import kcls_manager.main.KCLSException;

public class LocalDateSpinner extends JSpinner
{
    /** Generated serial version UID */
    private static final long serialVersionUID = 4080978752494737012L;

    public LocalDateSpinner()
    {
        setModel( new SpinnerDateModel() );
    }
    
    public void setLocalDate( LocalDate localDate )
    {
        Date    date    = getDate( localDate );
        getModel().setValue( date );
    }
    
    public LocalDate getLocalDate()
    {
        Object      value       = getValue();
        if ( !(value instanceof Date) )
        {
            String  message =
                "Expected type \"Date\"; was \""
                + value.getClass().getName();
            throw new KCLSException( message );
        }
        LocalDate   localDate   = getLocalDate( (Date)value );
        return localDate;
    }
    
    private static Date getDate( LocalDate localDate )
    {
        ZonedDateTime   dateTime    = localDate.atStartOfDay( ZONE_ID );
        Instant         instant     = dateTime.toInstant();
        Date            date        = Date.from( instant );
        return date;
    }
    
    private static LocalDate getLocalDate( Date date )
    {
        LocalDate           localDate   =
            date.toInstant().atZone( ZONE_ID ).toLocalDate();
        return localDate;
    }
}
