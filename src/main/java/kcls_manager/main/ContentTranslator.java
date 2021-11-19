package kcls_manager.main;

import static kcls_manager.main.Constants.ZONE_ID;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;

// TODO make listName a LibraryItem field
// TODO add translators for LibraryItem fields
/**
 * Support class for Title and Author data translators.
 * Contains utilities used by both translators.
 * @author jstra
 *
 */
public abstract class ContentTranslator
{
    /**
     * Translates an object into a String.
     * An exception is thrown if the input is not a String.
     * 
     * @param obj   The object to translate
     * 
     * @return  The translated value
     */
    public static String castToString( Object obj )
    {
        if ( !(obj instanceof String) )
        {
            String  name    = obj.getClass().getName();
            String  message = "Object not type String: " + name;
            throw new KCLSException( message );
        }
        return (String)obj;
    }
    
    /**
     * Translates an object into a java.util.Date.
     * An exception is thrown if the input is not a java.util.Date.
     * 
     * @param obj   The object to translate
     * 
     * @return  The translated value
     */
    public static Date castToDate( Object obj )
    {
        if ( !(obj instanceof Date) )
        {
            String  name    = obj.getClass().getName();
            String  message = "Object not type Date: " + name;
            throw new KCLSException( message );
        }
        return (Date)obj;
    }
    
    /**
     * Translates a java.util.Date object into a LocalDate object.
     * An exception is thrown if the input is not a Date object.
     * 
     * @param obj   The object to translate
     * 
     * @return  The translated value
     */
    public static LocalDate getLocalDate( Object obj )
    {
        Date        dateIn  = castToDate( obj );
        LocalDate   dateOut =
            dateIn.toInstant().atZone( ZONE_ID ).toLocalDate();
        return dateOut;
    }
    
    /**
     * Translates a LocalDate object into a java.util.Date object.
     * An exception is thrown if the input is not a LocalDate object.
     * 
     * @param obj   The object to translate
     * 
     * @return  The translated value
     */
    public static Date getDate( LocalDate dateIn )
    {
        ZonedDateTime   dateTime    = dateIn.atStartOfDay( ZONE_ID );
        Instant         instant     = dateTime.toInstant();
        Date            dateOut     = Date.from( instant );
        return dateOut;
    }
    
    /**
     * Translates an object into an integer.
     * An exception is thrown if the input is not an Integer.
     * 
     * @param obj   The object to translate
     * 
     * @return  The translated value
     */
    public static int castToInt( Object obj )
    {
        if ( !(obj instanceof Integer) )
        {
            String  name    = obj.getClass().getName();
            String  message = "Object not type Integer: " + name;
            throw new KCLSException( message );
        }
        return (int)obj;
    }
}
