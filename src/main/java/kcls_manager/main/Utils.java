package kcls_manager.main;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

/**
 * Utilities to support kcls_manager.main classes.
 * 
 * @author jstra
 *
 */
public class Utils
{
    /**
     * Compares two given OptionalInts for magnitude.
     * <ul>
     * <li>
     *      if both OptionalInts are null, they are considered equal;
     * </li>
     * <li>
     *      otherwise, if one OptionalInt is null, the null
     *      List is considered less than the non-null OptionalInt;
     * </li>
     * <li>
     *      otherwise, if both OptionalInts are empty, 
     *      they are considered equal;
     * </li>
     * <li>
     *      otherwise, if one OptionalInt is empty, the
     *      empty OptionalInt is considered less than the 
     *      non-empty OptionalInt;
     * </li>
     * <li>
     *      otherwise, their order is determined 
     *      by the respective magnitude of the integers
     *      they encapsulate.
     * </li>
     * </ul>
     * @param opt1  the first given OptionalInt
     * @param opt2  the second given OptionalInt
     * 
     * @return
     *      0, if the two OptionalInts are equal;
     *      a negative number if the first OptionalInt 
     *          is less than the second;
     *      a positive number if the first OptionalInt 
     *          is greater than the second.
     */
    public static int compareTo( OptionalInt opt1, OptionalInt opt2 )
    {
        int rcode   = 0;
        if ( opt1 == opt2 )
            rcode = 0;
        else if ( opt1 == null )
            rcode = -1;
        else if ( opt2 == null )
            rcode = 1;
        else if ( opt1.isEmpty() )
            rcode = -1;
        else if ( opt2.isEmpty() )
            rcode = 1;
        else
            rcode = opt1.getAsInt() - opt2.getAsInt();
        
        return rcode;
    }
    
    /**
     * Compares two given Lists for equality.
     * The type the lists, <T>, must implement Comparable<T>.
     * The objects in the given lists may be in any order;
     * to be equal, it is sufficient that the lists
     * satisfy the relationship <code>list1.containsAll(list2)
     * && list2.containsAll(list1).
     * 
     * If both List parameters are null,
     * the return value will be true.
     * 
     * @param <T>       the class of the objects to be compared
     * @param list1     the first given list
     * @param list2     the second given list
     * 
     * @return true if the lists contain exactly the same elements;
     *              false otherwise.
     */
    public static <T> boolean equals( List<T> list1, List<T> list2)
    {
        if ( list1 == list2 )
            return true;
        if ( list1 == null )
            return false;
        if ( list2 == null )
            return false;
        if ( list1.size() != list2.size() )
            return false;
        
        boolean rcode   = 
            list1.containsAll( list2 ) && list2.containsAll( list1 );
 
        return rcode;
    }
    
    /**
     * Compares two given collections for equality.
     * The collections are copied into lists,
     * and the resulting lists are compared
     * via {@linkplain #equals(List, List)}.
     * 
     * @param <T>       the type of element in each collection
     * @param coll1     the first given collection
     * @param coll2     the second given collection
     * 
     * @return true if the collections contain exactly the same elements;
     *         false otherwise.
     */    
    public static <T> boolean 
    equals( Collection<T> coll1, Collection<T> coll2 )
    {
         if ( coll1 == coll2 )
             return true;
         else if ( coll1 == null || coll2 == null )
             return false;
         else
             ;
         
         List<T>    temp1   = new ArrayList<>( coll1 );
         List<T>    temp2   = new ArrayList<>( coll2 );
         boolean    rcode   = equals( temp1, temp2 );

         return rcode;
    }

    /**
     * Compares two given Lists for magnitude.
     * <ul>
     * <li>if both lists are null, they are considered equal;</li>
     * <li>
     *      otherwise, if one list is null
     *      List is considered less than the non-null list;
     * </li>
     * <li>
     *      otherwise, of the lists are different sizes,
     *      the shorter list is considered less than the longer;
     * </li>
     * <li>
     *      otherwise both lists are sorted, 
     *      and compared element by element;
     *      the relative order of the list
     *      is determined by the first pair of elements
     *      that are not equal.
     * </li>
     * </ul>
     * 
     * @param <E>       the type of elements contained in the lists
     * @param list1     the first given list
     * @param list2     the second given list
     * 
     * @return
     *      0, if the two lists are equal;
     *      a negative number if the first list is less than the second;
     *      a positive number if the first list is greater than the second.
     *      
     * @see #compareTo(Collection, Collection)
     */
    public static <E extends Comparable<E>> int
    compareTo( List<E> list1, List<E> list2)
    {
        if ( list1 == list2 )
            return 0;
        else if ( list1 == null )
            return -1;
        else if ( list2 == null )
            return 1;
        else
            ;
        
        int count   = list1.size();
        int rcode   = count - list2.size();
        if ( rcode != 0 )
            return rcode;
        
        Collections.sort( list1 );
        Collections.sort( list2 );
        for ( int inx = 0 ; inx < count && rcode == 0 ; ++inx )
            rcode = list1.get( inx ).compareTo( list2.get( inx ) );
        return rcode;
    }

    /**
     * Compares two given collections for magnitude.
     * The collections are copied into lists,
     * and the resulting lists are compared
     * via {@linkplain #compareTo(List, List)}.
     * 
     * @param <T>       the type of element in each collection
     * @param coll1     the first given collection
     * @param coll2     the second given collection
     * 
     * @return
     *      0, if the two collections are equal;
     *      a negative number if the first is less than the second;
     *      a positive number if the first is greater than the second.
     */
    public static <T extends Comparable<T>> int
    compareTo( Collection<T> coll1, Collection<T> coll2)
    {
        if ( coll1 == coll2 )
            return 0;
        else if ( coll1 == null )
            return -1;
        else if ( coll2 == null )
            return 1;
        else
            ;
        
        List<T> temp1   = new ArrayList<>( coll1 );
        List<T> temp2   = new ArrayList<>( coll2 );
        int     rcode   = compareTo( temp1, temp2 );

        return rcode;
    }
    
    /**
     * Convert an em value to pixels (1em = font size in points).
     * Font is determined by a given component.
     * 
     * @param emm   em value
     * @param comp  given component
     * 
     * @return number of pixels equivalent to the em value
     * 
     * @see #cvtEmToPxInt(float, Component)
     */
    public static float cvtEmToPx( float emm, Component comp )
    {
        float   fontSize    = comp.getFont().getSize2D();
        float   result      = emm * fontSize;
        return result;
    }
    
    /**
     * Convert an em value to pixels (1em = font size in points),
     * rounding to the nearest integer (half-up).
     * Font is determined by a given component.
     * 
     * @param emm   em value
     * @param comp  given component
     * 
     * @return  number of pixels equivalent to the em value,
     *          rounded to the nearest integer
     *          
     * @see #cvtEmToPx(float, Component)
     */
    public static int cvtEmToPxInt( float emm, Component comp )
    {
        float   fontSize    = comp.getFont().getSize2D();
        int     result      = (int)(emm * fontSize);
        return result;
    }
}
