package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test
{
    public static void main(String[] args)
    {
        List<Tester>    list1   = new ArrayList<>();
        List<Tester>    list2   = new ArrayList<>();
        int             count   = 10;
        for ( int inx = 0 ; inx < count ; ++inx )
        {
            list1.add( new Tester( inx ) );
            list2.add( new Tester( count - inx - 1 ) );
        }
//        list1.sort( (t1,t2) -> t1.value - t2.value );
//        list2.sort( (t1,t2) -> t1.value - t2.value );
        System.out.println( list1 );
        System.out.println( list2 );
        Set<Tester>    set1    = new HashSet<>( list1 );
        Set<Tester>    set2    = new HashSet<>( list2 );
        System.out.println( set1 );
        System.out.println( set2 );
        System.out.println( set1.equals( set2 ) );
    }
    
    private static class Tester
    {
        private final int value;
        
        public Tester( int val )
        {
            value = val;
        }
        
        @Override
        public int hashCode()
        {
            return 7919 * value;
        }
        
        @Override
        public String toString()
        {
            return "" + value;
        }
    }
}
