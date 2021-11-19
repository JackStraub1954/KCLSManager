package kcls_manager.main;

import static kcls_manager.main.Constants.AUTHOR_NAME;
import static kcls_manager.main.Constants.CHECK_DATE_NAME;
import static kcls_manager.main.Constants.CHECK_QPOS_NAME;
import static kcls_manager.main.Constants.CREATION_DATE_NAME;
import static kcls_manager.main.Constants.LIST_NAME_NAME;
import static kcls_manager.main.Constants.MEDIA_TYPE_NAME;
import static kcls_manager.main.Constants.MODIFY_DATE_NAME;
import static kcls_manager.main.Constants.RANK_NAME;
import static kcls_manager.main.Constants.RATING_NAME;
import static kcls_manager.main.Constants.RECKON_DATE_NAME;
import static kcls_manager.main.Constants.RECKON_QPOS_NAME;
import static kcls_manager.main.Constants.SOURCE_NAME;
import static kcls_manager.main.Constants.TITLE_NAME;
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

import util.TitleFactory;

class TitleContentTranslatorTest
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
        TITLE_NAME,
        AUTHOR_NAME, 
        LIST_NAME_NAME,
        MEDIA_TYPE_NAME,
        CHECK_QPOS_NAME,
        RECKON_QPOS_NAME,
        CHECK_DATE_NAME,
        RECKON_DATE_NAME,
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
        TITLE_NAME,
//        AUTHOR_NAME, 
        MEDIA_TYPE_NAME,
//        CHECK_QPOS_NAME,
        RECKON_QPOS_NAME,
//        CHECK_DATE_NAME,
        RECKON_DATE_NAME,
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
        RANK_NAME, 
//        RATING_NAME,
        SOURCE_NAME,
//        COMMENTS_NAME,
//        CREATION_DATE_NAME, 
        MODIFY_DATE_NAME,
//        TITLE_NAME,
        AUTHOR_NAME, 
//        MEDIA_TYPE_NAME,
        CHECK_QPOS_NAME,
//        RECKON_QPOS_NAME,
        CHECK_DATE_NAME,
//        RECKON_DATE_NAME,
        "hidden" 
    };
    
    private TitleFactory    titleFactory;
    
    private Title   defTitle;
    
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
        titleFactory = new TitleFactory();
        defTitle = titleFactory.getUniqueTitle( 0 );
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    /**
     * Constructor tester.
     * Verify that all properties of a Title object
     * are correctly translated into rows.
     */
    @Test
    void testTitleContentTranslator()
    {
        testTranslateFieldsToTable( allColumnHeaders );
    }
    
    @Test
    public void testTranslateFieldsToTable()
    {
        testTranslateFieldsToTable( oneHalfColumnHeaders );
        testTranslateFieldsToTable( otherHalfColumnHeaders );
    }

    @Test
    void testTranslateFieldsFromTableObjectArrayArray()
    {
        TitleContentTranslator  trans   = 
            new TitleContentTranslator( allColumnHeaders );
        int         titleCount      = 5;
        Object[][]  allRows         = new Object[titleCount][];
        Title[]     expTitles       = new Title[titleCount];
        for ( int inx = 0 ; inx < titleCount ; ++inx )
        {
            Title       title   = titleFactory.getUniqueTitle( 0 );
            Object[]    row     = trans.translateFieldsToTable( title );
            allRows[inx] = row;
            expTitles[inx] = incrColumns( allColumnHeaders, row );
        }
        
        trans.translateFieldsFromTable( allRows );
        
        for ( int inx = 0 ; inx < titleCount ; ++inx  )
        {
            Title   expTitle    = expTitles[inx];
            Title   actTitle    = getTitle( allRows[inx] );
            assertEquals( expTitle, actTitle );
        }
    }

    @Test
    void testTranslateFieldsFromTableObjectArray()
    {
        int                     numCols = allColumnHeaders.length;
        TitleContentTranslator  trans   = 
            new TitleContentTranslator( allColumnHeaders );
        Object[]                row     =
            trans.translateFieldsToTable( defTitle );
        Title                   exp     =
            incrColumns( allColumnHeaders, row );
        trans.translateFieldsFromTable( row );
        Title                   act     = castToTitle( row[numCols - 1] );
        assertEquals( exp, act );
    }
    
    @Test
    public void testGoWrong()
    {
        Class<KCLSException>    kclsException   = KCLSException.class;
        Object[]    headers = new Object[2];
        Object[]    data    = new Object[2];
        
        headers[1] = "hidden";
        data[1] = defTitle;
        
        // fail on expect String
        headers[0] = TITLE_NAME;
        data[0] = 1;
        assertThrows( 
            kclsException, 
            () -> new TitleContentTranslator( headers )
            .translateFieldsFromTable( data )
        );
        
        // fail on expect Integer
        headers[0] = RANK_NAME;
        data[0] = "not an integer";
        assertThrows( 
            kclsException, 
            () -> new TitleContentTranslator( headers )
            .translateFieldsFromTable( data )
        );
        
        // fail on expect Date
        headers[0] = CREATION_DATE_NAME;
        data[0] = "not a date";
        assertThrows( 
            kclsException, 
            () -> new TitleContentTranslator( headers )
            .translateFieldsFromTable( data )
        );
        
        // fail on expect Title
        headers[0] = TITLE_NAME;
        data[0] = "some String, not important";
        data[1] = "not a Title";
        assertThrows( 
            kclsException, 
            () -> new TitleContentTranslator( headers )
            .translateFieldsFromTable( data )
        );
        
        // fail header/data array length
        headers[0] = TITLE_NAME;
        Object[]    temp    = { "title", defTitle, defTitle };
        assertThrows( 
            kclsException, 
            () -> new TitleContentTranslator( headers )
            .translateFieldsFromTable( temp )
        );
        
        // fail invalid column header (input)
        headers[0] = "not a valid column header";
        assertThrows( 
            kclsException, 
            () -> new TitleContentTranslator( headers )
        );
        
        // fail invalid column header (output)
        headers[0] = TITLE_NAME;
        final TitleContentTranslator    trans   = 
            new TitleContentTranslator( headers );
        headers[0] = "any mangled column header";
        assertThrows( 
            kclsException, 
            () -> trans.translateFieldsFromTable( temp )
        );
    }
    
    /**
     * Given a set of column headers, verify that all properties
     * of a Title object are correctly translated into
     * a row of data objects.
     * 
     * @param headers   given set of column headers
     */
    private void testTranslateFieldsToTable( Object headers[] )
    {
        TitleContentTranslator  trans   = 
            new TitleContentTranslator( headers );
        Object[]                row     = 
            trans.translateFieldsToTable( defTitle );
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
        
        assertTitleIdenticalToObj( defTitle, row );
        validateRow( headers, defTitle, row );
    }

    /**
     * Verify that the columns of given row match the corresponding fields
     * of a given Title.
     * The correspondence is determined by a list of headers;
     * for example, the header "Author" declares a correspondence
     * with the author property of the given title.
     * 
     * Note that every element of the headers array 
     * is expected to be type String.
     * 
     * @param headers   list of headers used to determine correspondence
     *                  between the columns of the given row
     *                  and the given Title
     * @param title     the given Title
     * @param row       the given row
     */
    private void validateRow( Object[] headers, Title title, Object[] row )
    {
        int headerLen   = headers.length;
        assertEquals( headerLen, row.length );
        
        // -1: last column contains Title object, so don't validate it.
        // That's done elsewhere.
        for ( int inx = 0 ; inx < headerLen - 1 ; ++inx )
        {
            String  header  = castToString( headers[inx] );
            Object  obj     = row[inx];
            switch ( header )
            {
            case RANK_NAME:
                assertRankEqualsObj( title, obj );
                break;
            case RATING_NAME:
                assertRatingEqualsObj( title, obj );
                break;
            case SOURCE_NAME:
                assertSourceEqualsObj( title, obj );
                break;
            case CREATION_DATE_NAME:
                assertCreationDateEqualsObj( title, obj );
                break;
            case MODIFY_DATE_NAME:
                assertModifyDateEqualsObj( title, obj );
                break;
            case TITLE_NAME:
                assertTextEqualsObj( title, obj );
                break;
            case AUTHOR_NAME:
                assertAuthorEqualsObj( title, obj );
                break;
            case LIST_NAME_NAME:
                assertListNameEqualsObj( title, obj );
                break;
            case MEDIA_TYPE_NAME:
                assertMediaTypeEqualsObj( title, obj );
                break;
            case CHECK_QPOS_NAME:
                assertCheckQPosEqualsObj( title, obj );
                break;
            case RECKON_QPOS_NAME:
                assertReckonQPosEqualsObj( title, obj );
                break;
            case CHECK_DATE_NAME:
                assertCheckDateEqualsObj( title, obj );
                break;
            case RECKON_DATE_NAME:
                assertReckonDateEqualsObj( title, obj );
                break;
            default:
                String  message = "Column header not found: " + header;
                fail( message );
                break;
            }
        }
    }
    
    /**
     * Increment every column in a row by a unique value;
     * perform parallel increments in a Title object.
     * The type and corresponding Title field are determined
     * by the column headings, which are passed as an argument.
     * The last ("hidden") column of the row
     * is expected to contain the Title object
     * that the row was created from.
     * 
     * Specifically:
     * <ol>
     * <li>Make a copy of the Title from the "hidden" column of the row.</li>
     * <li>For each column in the row prior to the "hidden" column:</li>
     * <ul>
     * <li>Increment the value in the column by a unique value</li>
     * <li>
     *      Perform the same increment on the corresponding field
     *      of the copy of the Title.
     * </li>
     * </ul>
     * </ol>
     * 
     * @param headers   the column headings for the row
     * @param row       the row of values
     * 
     * @return  a copy of the Title containing updates
     *          corresponding to the updates made
     *          to each column of the row
     */
    private Title incrColumns( Object[] headers, Object[] row )
    {
        int     colCount    = headers.length;
        assertEquals( colCount, row.length );
        
        int     dataCount   = colCount - 1;
        
        Title       title   = castToTitle( row[dataCount] );
        Title       temp    = new Title( title );
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
            case TITLE_NAME:
                string  = String.format( "%04d%s", nextStringPrefix, obj );
                row[inx] = string;
                string = String.format( 
                    "%04d%s", 
                    nextStringPrefix++, 
                    temp.getTitle()
                );
                temp.setTitle( string );
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
            case MEDIA_TYPE_NAME:
                string  = String.format( "%04d%s", nextStringPrefix, obj );
                row[inx] = string;
                string = String.format( 
                    "%04d%s", 
                    nextStringPrefix++, 
                    temp.getMediaType()
                );
                temp.setMediaType( string );
                break;
            case CHECK_QPOS_NAME:
                int nextCheckQPos   = (Integer)obj + nextIntIncr;
                row[inx] = nextCheckQPos;
                temp.setCheckQPos( temp.getCheckQPos() + nextIntIncr++ );
                break;
            case RECKON_QPOS_NAME:
                int nextReckonQPos  = (Integer)obj + nextIntIncr;
                row[inx] = nextReckonQPos;
                temp.setReckonQPos( temp.getReckonQPos() + nextIntIncr++ );
                break;
            case CHECK_DATE_NAME:
                date = castToDate( obj );
                localDate = getLocalDate( date );
                localDate = localDate.plusDays( nextDateIncr );
                row[inx] = getDate( localDate );

                localDate   = temp.getCheckDate();
                localDate = localDate.plusDays( nextDateIncr++ );
                temp.setCheckDate( localDate );
                break;
            case RECKON_DATE_NAME:
                date = castToDate( obj );
                localDate = getLocalDate( date );
                localDate = localDate.plusDays( nextDateIncr );
                row[inx] = getDate( localDate );

                localDate   = temp.getReckonDate();
                localDate = localDate.plusDays( nextDateIncr++ );
                temp.setReckonDate( localDate );
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
     * to a given Title object.
     * Note that <em>identical</em> implies equality per "==".
     * 
     * @param exp   the given Title
     * @param row   the given row of objects
     */
    private void assertTitleIdenticalToObj( Title exp, Object[] row )
    {
        int     hiddenCol   = row.length - 1;
        Object  act         = row[hiddenCol];
        assertTrue( exp == act );
    }
    
    /**
     * Verify that the rank of a given Title is equal to a given integer.
     * The object is expected to be type Integer.
     * 
     * @param   title   the given Title
     * @param   obj     the given Object
     */
    private void assertRankEqualsObj( Title title, Object obj )
    {
        int     expVal  = title.getRank();
        int     actVal  = castToInt( obj );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the rating of a given Title is equal to a given integer.
     * The object is expected to be type Integer.
     * 
     * @param   title   the given Title
     * @param   obj     the given Object
     */
    private void assertRatingEqualsObj( Title title, Object obj )
    {
        int     expVal  = title.getRating();
        int     actVal  = castToInt( obj );
        assertEquals( expVal, actVal );
    }

    // TODO: will comments ever be represented in a row of title properties?
//    /**
//     * Verify that the comments of a given Title is equal to a given object.
//     * The object is expected to be type ?????.
//     * 
//     * @param   title   the given Title
//     * @param   obj     the given Object
//     */
//    private void assertCommentsEqualsObj( Title title, Object obj )
//    {
//        List<Comment>   expVal  = title.getComments();
//        String  actVal  = castToString( obj );
//        assertEquals( expVal, actVal );
//    }

    /**
     * Verify that the source of a given Title is equal to a given object.
     * The object is expected to be type String.
     * 
     * @param   title   the given Title
     * @param   obj     the given Object
     */
    private void assertSourceEqualsObj( Title title, Object obj )
    {
        String  expVal  = title.getSource();
        String  actVal  = castToString( obj );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the creation date of a given Title is equal to a given object.
     * The object is expected to be type java.util.Date.
     * 
     * @param   title   the given Title
     * @param   obj     the given Object
     */
    private void assertCreationDateEqualsObj( Title title, Object obj )
    {
        LocalDate   expVal  = title.getCreationDate();
        Date        dateVal = castToDate( obj );
        LocalDate   actVal  = getLocalDate( dateVal );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the modify date of a given Title is equal to a given object.
     * The object is expected to be type java.util.Date.
     * 
     * @param   title   the given Title
     * @param   obj     the given Object
     */
    private void assertModifyDateEqualsObj( Title title, Object obj )
    {
        LocalDate   expVal  = title.getModifyDate();
        Date        dateVal = castToDate( obj );
        LocalDate   actVal  = getLocalDate( dateVal );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the text of a given Title is equal to a given object.
     * (The <em>text of a Title</em> is the actual title.)
     * The object is expected to be String.
     * 
     * @param   title   the given Title
     * @param   obj     the given Object
     */
    private void assertTextEqualsObj( Title title, Object obj )
    {
        String  expVal  = title.getTitle();
        String  actVal  = castToString( obj );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the author of a given Title is equal to a given object.
     * The object is expected to be String.
     * 
     * @param   title   the given Title
     * @param   obj     the given Object
     */
    private void assertAuthorEqualsObj( Title title, Object obj )
    {
        String  expVal  = title.getAuthor();
        String  actVal  = castToString( obj );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the name of the list assigned to 
     * a given Title is equal to a given object.
     * The object is expected to be String.
     * 
     * @param   title   the given Title
     * @param   obj     the given Object
     */
    private void assertListNameEqualsObj( Title title, Object obj )
    {
        String  expVal  = title.getListName();
        String  actVal  = castToString( obj );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the mediaType of a given Title is equal to a given object.
     * The object is expected to be type String.
     * 
     * @param   title   the given Title
     * @param   obj     the given Object
     */
    private void assertMediaTypeEqualsObj( Title title, Object obj )
    {
        String  expVal  = title.getMediaType();
        String  actVal  = castToString( obj );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the check-queue position
     * of a given Title is equal to a given object.
     * The object is expected to be type Integer.
     * 
     * @param   title   the given Title
     * @param   obj     the given Object
     */
    private void assertCheckQPosEqualsObj( Title title, Object obj )
    {
        int     expVal  = title.getCheckQPos();
        int     actVal  = castToInt( obj );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the reckon-queue position
     * of a given Title is equal to a given object.
     * The object is expected to be type Integer.
     * 
     * @param   title   the given Title
     * @param   obj     the given Object
     */
    private void assertReckonQPosEqualsObj( Title title, Object obj )
    {
        int     expVal  = title.getReckonQPos();
        int     actVal  = castToInt( obj );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the check date of a given Title
     * is equal to a given object.
     * The object is expected to be type java.util.Date.
     * 
     * @param   title   the given Title
     * @param   obj     the given Object
     */
    private void assertCheckDateEqualsObj( Title title, Object obj )
    {
        LocalDate   expVal  = title.getCheckDate();
        Date        dateVal = castToDate( obj );
        LocalDate   actVal  = getLocalDate( dateVal );
        assertEquals( expVal, actVal );
    }

    /**
     * Verify that the reckon date of a given Title
     * is equal to a given object.
     * The object is expected to be type java.util.Date.
     * 
     * @param   title   the given Title
     * @param   obj     the given Object
     */
    private void assertReckonDateEqualsObj( Title title, Object obj )
    {
        LocalDate   expVal  = title.getReckonDate();
        Date        dateVal = castToDate( obj );
        LocalDate   actVal  = getLocalDate( dateVal );
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
     * Translates an object into a Title.
     * An exception is thrown if the input is not a Title.
     * 
     * @param obj   The object to translate
     * 
     * @return  The translated value
     */
    private Title castToTitle( Object obj )
    {
        assertEquals( Title.class, obj.getClass() );
        return (Title)obj;
    }
    
    private Title getTitle( Object[] row )
    {
        int     hiddenCol   = row.length - 1;
        Title   title       = castToTitle( row[hiddenCol] );
        return title;
    }
}
