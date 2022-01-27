package kcls_manager.components;

import static kcls_manager.main.Constants.ZONE_ID;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import kcls_manager.main.KCLSException;

public class SpinnerLocalDateModel extends SpinnerDateModel
{
    public void setDate( LocalDate localDate )
    {
        Date    date    = getDate( localDate );
        setValue( date );
    }
    
    @Override
    public Object getValue()
    {
        Date                date        = super.getDate();
        LocalDate           localDate   = getLocalDate( date );
        return localDate;
    }
    
    @Override
    public void setValue( Object value )
    {
        Date    date    = null;
        if ( value instanceof Date )
            date = (Date)value;
        else if ( value instanceof LocalDate )
            date = getDate( (LocalDate)value );
        else if ( value instanceof LocalDateTime )
            date = getDate( ((LocalDateTime)value).toLocalDate() );
        else
        {
            String  message = 
                "Invalid input; object type \""
                + value.getClass().getName()
                + "\" not allowed";
            throw new KCLSException( message );
        }
        super.setValue( date );
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
