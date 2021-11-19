package kcls_manager.main;

import static kcls_manager.main.Constants.ZONE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;

import org.junit.jupiter.api.Test;

class ContentTranslatorTest
{
    private static Class<KCLSException>   kclsException = KCLSException.class;
    
    @Test
    public void testContentTranslator()
    {
        // just to get coverage
        new ContentTranslator() {};
    }

    @Test
    void testCastToString()
    {
        // If the following doesn't throw an exception, it works
        ContentTranslator.castToString( "a string" );
        
        assertThrows( kclsException, () -> ContentTranslator.castToString( 0 ) );
    }

    @Test
    void testCastToDate()
    {
        // If the following doesn't throw an exception, it works
        ContentTranslator.castToDate( new Date() );
        
        assertThrows( kclsException, 
            () -> ContentTranslator.castToDate( "a" ) );
    }

    @Test
    void testGetLocalDate()
    {
        LocalDate       localDate   = LocalDate.now();
        ZonedDateTime   dateTime    = localDate.atStartOfDay( ZONE_ID );
        Instant         instant     = dateTime.toInstant();
        Date            utilDate    = Date.from( instant );
        LocalDate       actDate     = 
            ContentTranslator.getLocalDate( utilDate );
        assertEquals( localDate, actDate );
    }

    @Test
    void testGetDate()
    {
        LocalDate       localDate   = LocalDate.now();
        ZonedDateTime   dateTime    = localDate.atStartOfDay( ZONE_ID );
        Instant         instant     = dateTime.toInstant();
        Date            utilDate    = Date.from( instant );
        Date            actDate     = ContentTranslator.getDate( localDate );
        assertEquals( utilDate, actDate );
    }

    @Test
    void testCastToInt()
    {
        // If the following doesn't throw an exception, it works
        ContentTranslator.castToInt( Integer.valueOf( 0 ) );
        
        assertThrows( kclsException,
            () -> ContentTranslator.castToInt( "a" ) );
    }

}
