package kcls_manager.main;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Queue;

import org.junit.jupiter.api.Test;

class UtilsTest
{
    private static final int            intLow      = 10;
    private static final int            intHigh     = intLow + 100;
    private static final OptionalInt    optEmpty    = OptionalInt.empty();
    private static final OptionalInt    optLow      =
        OptionalInt.of( intLow );
    private static final OptionalInt    optHigh     = 
        OptionalInt.of( intHigh );
    private static final OptionalInt    optNull     = null;
    
    /*
     * String with "higher" names are expected to have
     * higher values.
     */
    private static final String         str0001     = "0001";
    private static final String         str0002     = "0002";
    private static final String         str0003     = "0003";
    private static final String         str0004     = "0004";
    
    // Note: List.of( T... returns an immutable list
    private static final List<String>   listNull    = null;
    private static final List<String>   listSmall   =
        new ArrayList<>( List.of( str0001, str0001 ) );
    private static final List<String>   listLarge   =
        new ArrayList<>( List.of( str0001, str0001, str0001 ) );
    private static final List<String>   listLow     =
        new ArrayList<>( List.of( str0001, str0001 ) );
    private static final List<String>   listHigh    =
        new ArrayList<>( List.of( str0001, str0002 ) );
    private static final List<String>   listEquals1 =
        new ArrayList<>( List.of( str0001, str0002, str0003, str0004 ) );
    private static final List<String>   listEquals2 =
        new ArrayList<>( listEquals1 );
    
    private static final Queue<String>    queueNull     = null;
    private static final Queue<String>    queueSmall    = 
        new LinkedList<>( listSmall );
    private static final Queue<String>    queueLarge    = 
        new LinkedList<>( listLarge );
    private static final Queue<String>    queueLow      = 
        new LinkedList<>( listLow );
    private static final Queue<String>    queueHigh     = 
        new LinkedList<>( listHigh );
    private static final Queue<String>    queueEquals1  = 
        new LinkedList<>( listEquals1 );
    private static final Queue<String>    queueEquals2  = 
        new LinkedList<>( listEquals1 );

    @Test
    public void testUtils()
    {
        // to improve test coverage:
        // exercise default constructor
        new Utils();
    }
    
    @Test
    void testCompareToOptionalIntOptionalInt()
    {
        assertTrue( Utils.compareTo( optNull, optNull ) == 0 );
        assertTrue( Utils.compareTo( optNull, optNull ) == 0 );
        
        assertTrue( Utils.compareTo( optEmpty, optNull ) > 0 );
        assertTrue( Utils.compareTo( optNull, optEmpty ) < 0 );
        assertTrue( Utils.compareTo( optEmpty, optEmpty ) == 0 );
        
        assertTrue( Utils.compareTo( optLow, optEmpty ) > 0 );
        assertTrue( Utils.compareTo( optEmpty, optLow ) < 0 );
        assertTrue( Utils.compareTo( optLow, optLow ) == 0 );
        
        assertTrue( Utils.compareTo( optHigh, optLow ) > 0 );
        assertTrue( Utils.compareTo( optLow, optHigh ) < 0 );
        assertTrue( Utils.compareTo( optHigh, optHigh ) == 0 );
    }

    @Test
    void testEqualsListOfTListOfT()
    {
        assertTrue( Utils.equals( listNull, listNull ) );
        assertFalse( Utils.equals( listSmall, listNull ) );
        assertFalse( Utils.equals( listNull, listSmall ) );
        assertTrue( Utils.equals( listSmall, listSmall ) );
        
        assertFalse( Utils.equals( listSmall, listLarge ) );
        assertFalse( Utils.equals( listLarge, listSmall ) );
        
        assertFalse( Utils.equals( listLow, listHigh ) );
        assertFalse( Utils.equals( listHigh, listLow ) );
        
        assertTrue( Utils.equals( listEquals1, listEquals2 ) );
        assertTrue( Utils.equals( listEquals2, listEquals1 ) );
    }

    @Test
    void testEqualsCollectionOfTCollectionOfT()
    {
        assertTrue( Utils.equals( queueNull, queueNull ) );
        assertFalse( Utils.equals( queueSmall, queueNull ) );
        assertFalse( Utils.equals( queueNull, queueSmall ) );
        assertTrue( Utils.equals( queueSmall, queueSmall ) );
        
        assertFalse( Utils.equals( queueSmall, queueLarge ) );
        assertFalse( Utils.equals( queueLarge, queueSmall ) );
        
        assertFalse( Utils.equals( queueLow, queueHigh ) );
        assertFalse( Utils.equals( queueHigh, queueLow ) );
        
        assertTrue( Utils.equals( queueEquals1, queueEquals2 ) );
        assertTrue( Utils.equals( queueEquals2, queueEquals1 ) );
    }

    @Test
    void testCompareToListOfEListOfE()
    {
        assertTrue( Utils.compareTo( listNull, listNull ) == 0 );
        assertTrue( Utils.compareTo( listSmall, listNull ) > 0 );
        assertTrue( Utils.compareTo( listNull, listSmall ) < 0 );
        assertTrue( Utils.compareTo( listSmall, listSmall ) == 0 );
        
        assertTrue( Utils.compareTo( listSmall, listLarge ) < 0 );
        assertTrue( Utils.compareTo( listLarge, listSmall ) > 0 );
        assertTrue( Utils.compareTo( listLarge, listLarge ) == 0 );
        
        assertTrue( Utils.compareTo( listLow, listHigh ) < 0 );
        assertTrue( Utils.compareTo( listHigh, listLow ) > 0 );
    }

    @Test
    void testCompareToCollectionOfTCollectionOfT()
    {
        assertTrue( Utils.compareTo( queueNull, queueNull ) == 0 );
        assertTrue( Utils.compareTo( queueSmall, queueNull ) > 0 );
        assertTrue( Utils.compareTo( queueNull, queueSmall ) < 0 );
        assertTrue( Utils.compareTo( queueSmall, queueSmall ) == 0 );
        
        assertTrue( Utils.compareTo( queueSmall, queueLarge ) < 0 );
        assertTrue( Utils.compareTo( queueLarge, queueSmall ) > 0 );
        assertTrue( Utils.compareTo( queueLarge, queueLarge ) == 0 );
        
        assertTrue( Utils.compareTo( queueLow, queueHigh ) < 0 );
        assertTrue( Utils.compareTo( queueHigh, queueLow ) > 0 );
    }

}
