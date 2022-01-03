package kcls_manager.main;

import static kcls_manager.main.Constants.AUTHOR_NAME;
import static kcls_manager.main.Constants.CREATION_DATE_NAME;
import static kcls_manager.main.Constants.CURRENT_COUNT_NAME;
import static kcls_manager.main.Constants.LAST_COUNT_NAME;
import static kcls_manager.main.Constants.LIST_NAME_NAME;
import static kcls_manager.main.Constants.MODIFY_DATE_NAME;
import static kcls_manager.main.Constants.RANK_NAME;
import static kcls_manager.main.Constants.RATING_NAME;
import static kcls_manager.main.Constants.SOURCE_NAME;
import static kcls_manager.main.Constants.ZONE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import test_util.AuthorFactory;

class AuthorContentTranslatorTest
{
    /** All possible column headers. */
    private static final String[]   allColumnHeaders =
    {
        RANK_NAME, 
        RATING_NAME,
        SOURCE_NAME,
//        COMMENTS_NAME,
        CREATION_DATE_NAME, 
        MODIFY_DATE_NAME,
        AUTHOR_NAME, 
        LIST_NAME_NAME,
        LAST_COUNT_NAME,
        CURRENT_COUNT_NAME,
        "hidden" 
    };

    /**
     * Half the column headers. Complementary to 
     * <em>otherHalfColumnHeaders.</>
     * 
     * @see #otherHalfColumnHeaders
     */
    private static final String[]   oneHalfColumnHeaders =
    {
//        RANK_NAME, 
        RATING_NAME,
//        SOURCE_NAME,
//        COMMENTS_NAME,
        CREATION_DATE_NAME, 
//        MODIFY_DATE_NAME,
        AUTHOR_NAME, 
//        LIST_NAME_NAME,
        LAST_COUNT_NAME,
//        CURRENT_COUNT_NAME,
        "hidden" 
    };

    /**
     * Half the column headers. Complementary to 
     * <em>oneHalfColumnHeaders.</>
     * 
     * @see #oneHalfColumnHeaders
     */
    private static final String[]   otherHalfColumnHeaders =
    {
//        RANK_NAME, 
        RATING_NAME,
//        SOURCE_NAME,
//        COMMENTS_NAME,
        CREATION_DATE_NAME, 
//        MODIFY_DATE_NAME,
        AUTHOR_NAME, 
//        LIST_NAME_NAME,
        LAST_COUNT_NAME,
//        CURRENT_COUNT_NAME,
    };
    
    private AuthorFactory   authorFactory;
    
    private Author          defAuthor;
    
    /** Prefix for generating unique String value. */
    private int nextStringPrefix    = 1;
    
    /** Value for generating unique Date values. */
    private int nextDateIncr        = 1;
    
    /** Value for generating unique integer values. */
    private int nextIntIncr         = 1;

    @BeforeAll
    static void setUpBeforeClass() throws Exception
    {
    }

    @BeforeEach
    void setUp() throws Exception
    {
        authorFactory = new AuthorFactory();
        defAuthor = authorFactory.getUniqueAuthor( 0 );
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testAuthorContentTranslator()
    {
        testTranslateFieldsToTable( allColumnHeaders );
    }

    @Test
    void testTranslateFieldsFromTableObjectArray()
    {
        int                     numCols = allColumnHeaders.length;
        AuthorContentTranslator trans   = 
            new AuthorContentTranslator( allColumnHeaders );
        Object[]                row     =
            trans.translateFieldsToTable( defAuthor );
        Author                  exp     =
            incrColumns( allColumnHeaders, row );
        trans.translateFieldsFromTable( row );
        Author                  act     = castToAuthor( row[numCols - 1] );
        assertEquals( exp, act );
    }

    @Test
    void testTranslateFieldsToTable()
    {
        testTranslateFieldsToTable( oneHalfColumnHeaders );
        testTranslateFieldsToTable( otherHalfColumnHeaders );
    }

    @Test
    void testTranslateFieldsFromTableObjectArrayArray()
    {
        AuthorContentTranslator  trans   = 
            new AuthorContentTranslator( allColumnHeaders );
        int         authorCount     = 5;
        Object[][]  allRows         = new Object[authorCount][];
        Author[]    expAuthors      = new Author[authorCount];
        for ( int inx = 0 ; inx < authorCount ; ++inx )
        {
            Author      author  = authorFactory.getUniqueAuthor( 0 );
            Object[]    row     = trans.translateFieldsToTable( author );
            allRows[inx] = row;
            expAuthors[inx] = incrColumns( allColumnHeaders, row );
        }
        
        trans.translateFieldsFromTable( allRows );
        
        for ( int inx = 0 ; inx < authorCount ; ++inx  )
        {
            Author  expAuthor   = expAuthors[inx];
            Author  actAuthor   = getAuthor( allRows[inx] );
            assertEquals( expAuthor, actAuthor );
        }
    }
    
    /**
     * Given a set of column headers, verify that all properties
     * of an Author object are correctly translated into
     * a row of data objects.
     * 
     * @param headers   given set of column headers
     */
    private void testTranslateFieldsToTable( Object headers[] )
    {
        AuthorContentTranslator trans   = 
            new AuthorContentTranslator( headers );
        Object[]                row     = 
            trans.translateFieldsToTable( defAuthor );
        int headerCount = headers.length;
        assertEquals( headerCount, row.length );
//          for ( int inx = 0 ; inx < headerCount ; ++inx )
//          {
//              String  colHeader   = castToString( headers[inx] );
//              Object  colValue    = row[inx];
//              String  className   = colValue.getClass().getName();
//              String  line    = colHeader + ": " + className;
//              System.out.println( line );
//          }
        
        assertAuthorIdenticalToObj( defAuthor, row );
        validateRow( headers, defAuthor, row );
    }

    /**
     * Verify that the columns of given row match the corresponding fields
     * of a given Author.
     * The correspondence is determined by a list of headers;
     * for example, the header "Last Count" declares a correspondence
     * with the lastCount property of the given author.
     * 
     * Note that every element of the headers array 
     * is expected to be type String.
     * 
     * @param headers   list of headers used to determine correspondence
     *                  between the columns of the given row
     *                  and the given Author
     * @param author    the given Author
     * @param row       the given row
     */
    private void validateRow( Object[] headers, Author author, Object[] row )
    {
        int headerLen   = headers.length;
        assertEquals( headerLen, row.length );
        
        // -1: last column contains  object, so don't validate it.
        // That's done elsewhere.
        for ( int inx = 0 ; inx < headerLen - 1 ; ++inx )
        {
            String  header  = castToString( headers[inx] );
            Object  obj     = row[inx];
            switch ( header )
            {
            case RANK_NAME:
                assertRankEqualsObj( author, obj );
                break;
            case RATING_NAME:
                assertRatingEqualsObj( author, obj );
                break;
            case SOURCE_NAME:
                assertSourceEqualsObj( author, obj );
                break;
            case CREATION_DATE_NAME:
                assertCreationDateEqualsObj( author, obj );
                break;
            case MODIFY_DATE_NAME:
                assertModifyDateEqualsObj( author, obj );
                break;
            case AUTHOR_NAME:
                assertAuthorEqualsObj( author, obj );
                break;
            case LIST_NAME_NAME:
                assertListNameEqualsObj( author, obj );
                break;
            case LAST_COUNT_NAME:
                assertLastCountEqualsObj( author, obj );
                break;
            case CURRENT_COUNT_NAME:
                assertCurrentCountEqualsObj( author, obj );
                break;
            default:
                String  message = "Column header not found: " + header;
                fail( message );
                break;
            }
        }
    }
    
    @Test
    public void testGoWrong()
    {
        Class<KCLSException>    kclsException   = KCLSException.class;
        Object[]    headers = new Object[2];
        Object[]    data    = new Object[2];
        
        headers[1] = "hidden";
        data[1] = defAuthor;
        
        // fail on expect String
        headers[0] = AUTHOR_NAME;
        data[0] = 1;
        assertThrows( 
            kclsException, 
            () -> new AuthorContentTranslator( headers )
            .translateFieldsFromTable( data )
        );
        
        // fail on expect Integer
        headers[0] = RANK_NAME;
        data[0] = "not an integer";
        assertThrows( 
            kclsException, 
            () -> new AuthorContentTranslator( headers )
            .translateFieldsFromTable( data )
        );
        
        // fail on expect Date
        headers[0] = CREATION_DATE_NAME;
        data[0] = "not a date";
        assertThrows( 
            kclsException, 
            () -> new AuthorContentTranslator( headers )
            .translateFieldsFromTable( data )
        );
        
        // fail on expect Author
        headers[0] = AUTHOR_NAME;
        data[0] = "some String, not important";
        data[1] = "not an Author";
        assertThrows( 
            kclsException, 
            () -> new AuthorContentTranslator( headers )
            .translateFieldsFromTable( data )
        );
        
        // fail header/data array length
        headers[0] = AUTHOR_NAME;
        Object[]    temp    = { "author", defAuthor, defAuthor };
        assertThrows( 
            kclsException, 
            () -> new AuthorContentTranslator( headers )
            .translateFieldsFromTable( temp )
        );
        
        // fail invalid column header (input)
        headers[0] = "not a valid column header";
        assertThrows( 
            kclsException, 
            () -> new AuthorContentTranslator( headers )
        );
        
        // fail invalid column header (output)
        headers[0] = AUTHOR_NAME;
        final AuthorContentTranslator    trans   = 
            new AuthorContentTranslator( headers );
        headers[0] = "any mangled column header";
        assertThrows( 
            kclsException, 
            () -> trans.translateFieldsFromTable( temp )
        );
    }

    /**
     * Increment every column in a row by a unique value;
     * perform parallel increments in an Author object.
     * The type and corresponding Author field are determined
     * by the column headings, which are passed as an argument.
     * The last ("hidden") column of the row
     * is expected to contain the Author object
     * that the row was created from.
     * 
     * Specifically:
     * <ol>
     * <li>Make a copy of the Author from the "hidden" column of the row.</li>
     * <li>For each column in the row prior to the "hidden" column:</li>
     * <ul>
     * <li>Increment the value in the column by a unique value.</li>
     * <li>
     *      Perform the same increment on the corresponding field
     *      of the copy of the Author.
     * </li>
     * </ul>
     * </ol>
     * 
     * @param headers   the column headings for the row
     * @param row       the row of values
     * 
     * @return  a copy of the Author containing updates
     *          corresponding to the updates made
     *          to each column of the row
     */
    private Author incrColumns( Object[] headers, Object[] row )
    {
        int     colCount    = headers.length;
        assertEquals( colCount, row.length );
        
        int     dataCount   = colCount - 1;
        
        Author      author  = castToAuthor( row[dataCount] );
        Author      temp    = new Author( author );
        Date        date;       // for use in switch statement
        LocalDate   localDate;  // for use in switch statement
        String      string;     // for use in switch statement
        for ( int inx = 0 ; inx < dataCount ; ++inx )
        {
            String  header  = castToString( headers[inx] );
            Object  obj     = row[inx];
            switch ( header )
            {
            case RANK_NAME:
                int nextRank    = (Integer)obj + nextIntIncr;
                row[inx] = nextRank;
                temp.setRank( temp.getRank() + nextIntIncr++ );
                break;
            case RATING_NAME:
                int nextRating  = (Integer)obj + nextIntIncr;
                row[inx] = nextRating;
                temp.setRating( temp.getRating() + nextIntIncr++ );
                break;
            case SOURCE_NAME:
                string  = String.format( "%04d%s", nextStringPrefix, obj );
                row[inx] = string;
                string = String.format( 
                    "%04d%s", 
                    nextStringPrefix++, 
                    temp.getSource()
                );
                temp.setSource( string );
                break;
            case CREATION_DATE_NAME:
                date = castToDate( obj );
                localDate = getLocalDate( date );
                localDate = localDate.plusDays( nextDateIncr );
                row[inx] = getDate( localDate );

                localDate   = temp.getCreationDate();
                localDate = localDate.plusDays( nextDateIncr++ );
                temp.setCreationDate( localDate );
                break;
            case MODIFY_DATE_NAME:
                date = castToDate( obj );
                localDate = getLocalDate( date );
                localDate = localDate.plusDays( nextDateIncr );
                row[inx] = getDate( localDate );

                localDate   = temp.getModifyDate();
                localDate = localDate.plusDays( nextDateIncr++ );
                temp.setModifyDate( localDate );
                break;
            case AUTHOR_NAME:
                string  = String.format( "%04d%s", nextStringPrefix, obj );
                row[inx] = string;
                string = String.format( 
                    "%04d%s", 
                    nextStringPrefix++, 
                    temp.getAuthor()
                );
                temp.setAuthor( string );
                break;
            case LIST_NAME_NAME:
                string  = String.format( "%04d%s", nextStringPrefix, obj );
                row[inx] = string;
                string = String.format( 
                    "%04d%s", 
                    nextStringPrefix++, 
                    temp.getListName()
                );
                temp.setListName( string );
                break;
            case LAST_COUNT_NAME:
                int nextLastCount   = (Integer)obj + nextIntIncr;
                row[inx] = nextLastCount;
                temp.setLastCount( temp.getLastCount() + nextIntIncr++ );
                break;
            case CURRENT_COUNT_NAME:
                int nextCurrentCount   = (Integer)obj + nextIntIncr;
                row[inx] = nextCurrentCount;
                temp.setCurrentCount( temp.getCurrentCount() + nextIntIncr++ );
                break;
            default:
                String  message = "Column header not found: " + header;
                fail( message );
                break;
            }
        }
        return temp;
    }

    /**
     * Verify that the last column of a given row of objects is identical
     * to a given Author object.
     * Note that <em>identical</em> implies equality per "==".
     * 
     * @param exp   the given Author
     * @param row   the given row of objects
     */
    private void assertAuthorIdenticalToObj( Author exp, Object[] row )
    {
        int     hiddenCol   = row.length - 1;
        Object  act         = row[hiddenCol];
        assertTrue( exp == act );
    }
    
    /**
     * Verify that the rank of a given Author is equal to a given integer.
     * The object is expected to be type Integer.
     * 
     * @param   author   the given Author
     * @param   obj     the given Object
     */
    private void assertRankEqualsObj( Author author, Object obj )
    {
        int     expVal  = author.getRank();
        int     actVal  = castToInt( obj );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the rating of a given Author is equal to a given integer.
     * The object is expected to be type Integer.
     * 
     * @param   author   the given Author
     * @param   obj     the given Object
     */
    private void assertRatingEqualsObj( Author author, Object obj )
    {
        int     expVal  = author.getRating();
        int     actVal  = castToInt( obj );
        assertEquals( expVal, actVal );
    }

    // TODO: will comments ever be represented in a row of  properties?
//    /**
//     * Verify that the comments of a given  is equal to a given object.
//     * The object is expected to be type ?????.
//     * 
//     * @param      the given 
//     * @param   obj     the given Object
//     */
//    private void assertCommentsEqualsObj(  , Object obj )
//    {
//        List<Comment>   expVal  = .getComments();
//        String  actVal  = castToString( obj );
//        assertEquals( expVal, actVal );
//    }

    /**
     * Verify that the source of a given Author is equal to a given object.
     * The object is expected to be type String.
     * 
     * @param   author  the given Author
     * @param   obj     the given Object
     */
    private void assertSourceEqualsObj( Author author, Object obj )
    {
        String  expVal  = author.getSource();
        String  actVal  = castToString( obj );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the creation date of a given Author is equal to a given object.
     * The object is expected to be type java.util.Date.
     * 
     * @param   author  the given Author
     * @param   obj     the given Object
     */
    private void assertCreationDateEqualsObj( Author author, Object obj )
    {
        LocalDate   expVal  = author.getCreationDate();
        Date        dateVal = castToDate( obj );
        LocalDate   actVal  = getLocalDate( dateVal );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the modify date of a given Author is equal to a given object.
     * The object is expected to be type java.util.Date.
     * 
     * @param   author  the given Author
     * @param   obj     the given Object
     */
    private void assertModifyDateEqualsObj( Author author, Object obj )
    {
        LocalDate   expVal  = author.getModifyDate();
        Date        dateVal = castToDate( obj );
        LocalDate   actVal  = getLocalDate( dateVal );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the name of a given Author is equal to a given object.
     * The object is expected to be String.
     * 
     * @param   author   the given Author
     * @param   obj     the given Object
     */
    private void assertAuthorEqualsObj( Author author, Object obj )
    {
        String  expVal  = author.getAuthor();
        String  actVal  = castToString( obj );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the name of the list assigned to 
     * a given  is equal to a given object.
     * The object is expected to be String.
     * 
     * @param   author   the given Author
     * @param   obj     the given Object
     */
    private void assertListNameEqualsObj( Author author, Object obj )
    {
        String  expVal  = author.getListName();
        String  actVal  = castToString( obj );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the last count property
     * of a given Author is equal to a given object.
     * The object is expected to be type Integer.
     * 
     * @param   author   the given Author
     * @param   obj     the given Object
     */
    private void assertLastCountEqualsObj( Author author, Object obj )
    {
        int     expVal  = author.getLastCount();
        int     actVal  = castToInt( obj );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the current count property
     * of a given Author is equal to a given object.
     * The object is expected to be type Integer.
     * 
     * @param   author   the given Author
     * @param   obj     the given Object
     */
    private void assertCurrentCountEqualsObj( Author author, Object obj )
    {
        int     expVal  = author.getCurrentCount();
        int     actVal  = castToInt( obj );
        assertEquals( expVal, actVal );
    }
    
    /**
     * Translates a java.util.Date object into a LocalDate object.
     * An exception is thrown if the input is not a Date object.
     * 
     * @param obj   The object to translate
     * 
     * @return  The translated value
     */
    private LocalDate getLocalDate( Date dateIn )
    {
        LocalDate   dateOut =
            dateIn.toInstant().atZone( ZONE_ID ).toLocalDate();
        return dateOut;
    }
    
    private Date getDate( LocalDate dateIn )
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
    private int castToInt( Object obj )
    {
        assertEquals( Integer.class, obj.getClass() );
        return (int)obj;
    }
    
    /**
     * Translates an object into a java.util.Date.
     * An exception is thrown if the input is not a java.util.Date.
     * 
     * @param obj   The object to translate
     * 
     * @return  The translated value
     */
    private Date castToDate( Object obj )
    {
        assertEquals( Date.class, obj.getClass() );
        return (Date)obj;
    }
    
    /**
     * Translates an object into a String.
     * An exception is thrown if the input is not a String.
     * 
     * @param obj   The object to translate
     * 
     * @return  The translated value
     */
    private String castToString( Object obj )
    {
        assertEquals( String.class, obj.getClass() );
        return (String)obj;
    }
    
    /**
     * Translates an object into an Author.
     * An exception is thrown if the input is not a Author.
     * 
     * @param obj   The object to translate
     * 
     * @return  The translated value
     */
    private Author castToAuthor( Object obj )
    {
        assertEquals( Author.class, obj.getClass() );
        return (Author)obj;
    }
    
    private Author getAuthor( Object[] row )
    {
        int     hiddenCol   = row.length - 1;
        Author  author      = castToAuthor( row[hiddenCol] );
        return author;
    }
}
