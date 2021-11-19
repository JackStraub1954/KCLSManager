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

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Translate data between properties in an Author object
 * and rows in a table.
 * An array of column headers controls the mapping
 * between a column of a row,
 * and a property of an Author object.
 * 
 * @author jstra
 *
 */
public class AuthorContentTranslator extends ContentTranslator
{
    /** 
     * The total number of columns in s row
     * that are used to store data associated 
     * with an Author property. 
     * This does not include the <em>hidden</em> column
     * which is used to store the associated Author object.
     */
    private final int       numDataCols;
    
    /** 
     * Map to take a cell value from a row and translate it to a 
     * field in an Author object.
     */
    private final Map<Integer,Consumer<Object>> 
        toAuthorMap     = new HashMap<>();

    /**
     * Map to take a value from a field in an Author object
     * and translate it to a cell in a row.
     */
    private final Map<Integer,Supplier<Object>> 
        fromAuthorMap     = new HashMap<>();
    
    /** Principal object of translation */
    private Author  author;
    
    /**
     * Constructor.
     * Determines how to map columns in a row of a table
     * into updates to the corresponding field
     * in an Author object.
     * This mapping is based on the table's column headings;
     * for example, if the heading for column 3 in a table
     * is "Rating" (Constants.RATING_NAME) 
     * the type of the input data is Integer
     * and the target property in the Author object
     * is the rating.
     * 
     * The caller supplies the column headings in sequential order.
     * Not that the last ("hidden") column is expected to store
     * the target Author object, and is not used in the 
     * column-to-Author-field mapping.
     * 
     * @param columnIDs The headings of the columns in a table
     */
    public AuthorContentTranslator( Object[] columnIDs )
    {
        // -1 because the last column is the hidden column that contains
        // the Author objects.
        numDataCols = columnIDs.length - 1;
        for ( int inx = 0 ; inx < numDataCols ; ++inx )
        {
            Consumer<Object>    consumer    = getConsumer( columnIDs[inx] );
            toAuthorMap.put( inx, consumer );
            
            Supplier<Object>    supplier    = getSupplier( columnIDs[inx] );
            fromAuthorMap.put( inx, supplier );
        }
    }
    
    /**
     * Translate the data from the rows of a table into updates
     * to their corresponding Author objects. 
     * The expected type of the data, and the destination field
     * within the Author object are determined by:
     * <ul>
     * <li>a value's columnar position within a row</li>
     * <li>the column's heading</li>
     * </ul>
     * An array of column headings is provided
     * when instantiating this object.
     * The Author object to update is found in the last ("hidden")
     * column of each row.
     * 
     * @param rows  the rows of the table to translate into
     *              Author object updates
     * 
     * @see #translateFieldsFromTable(Object[])
     */
    public void translateFieldsFromTable( Object[][] rows )
    {
        for ( Object[] row : rows )
            translateFieldsFromTable( row );
    }
    
    /**
     * Translate the data from one row of a table into updates
     * to its corresponding Author object. 
     * The expected type of the data, and the destination field
     * within the Author object are determined by:
     * <ul>
     * <li>a value's columnar position within a row</li>
     * <li>the column's heading</li>
     * </ul>
     * An array of column headings is provided
     * when instantiating this object.
     * The Author object to update is found in the last ("hidden")
     * column of the row.
     * 
     * @param row   the row to translate
     * 
     * @throws KCLSException if row contains incorrect number of columns
     */
    public void translateFieldsFromTable( Object[] row )
        throws KCLSException
    {
        // +1 for "hidden" column
        int expColCount = numDataCols + 1;
        int actColCount = row.length;
        if ( expColCount != actColCount )
        {
            String  message =
                "Expect # columns: " + expColCount
                + "Actual #columns: " + actColCount;
            throw new KCLSException( message );
        }
        author = castToAuthor( row[numDataCols] );
        for ( int inx = 0 ; inx < numDataCols ; ++inx )
            translateFieldFromTable( inx, row );
    }
    
    /**
     * Translate the properties of an Author object into 
     * a row for insertion into a table.
     * The expected type of the data, and the destination field
     * within the Author object are determined by:
     * <ul>
     * <li>a value's columnar position within a row</li>
     * <li>the column heading used to instantiate this object</li>
     * </ul>
     * An array of column headings is provided
     * when instantiating this object.
     * The source Author object is placed in the last ("hidden")
     * column of the row.
     * 
     * @param author     the Author object to translate into a row
     * 
     * @return the translated row
     * 
     * @see #translateFieldToTable(int, Object[])
     */
    public Object[] translateFieldsToTable( Author author )
    {
        this.author = author;
        // + 1 for "hidden" column
        Object[] row = new Object[numDataCols + 1];
        row[numDataCols] = author;
        for ( int inx = 0 ; inx < numDataCols ; ++inx )
        {
            translateFieldToTable( inx, row );
        }
        
        return row;
    }

    /**
     * Read a value from a cell in a table, and translate it into
     * an update to an Author object. 
     * The horizontal position of the data is given as input.
     * The expected type of the data, and the destination field
     * within the Author object are determined by the column headings
     * used to instantiate this object. 
     * The Author object to update is found in the last ("hidden")
     * column of the row.
     * 
     * @param col   The column within a row of data 
     *              to find the value to translate
     * @param row   An array of objects representing one row of data
     *              from a table.
     * 
     * @see #translateFields(Object[][])
     */
    private void translateFieldFromTable( int col, Object[] row )
    {
        author = castToAuthor( row[numDataCols] );
        Consumer<Object>    consumer    = toAuthorMap.get( col );
        consumer.accept( row[col] );
    }

    /**
     * Read a value from a cell in a table, and translate it into
     * an update to an Author object. 
     * The horizontal position of the data is given as input.
     * The expected type of the data, and the destination field
     * within the Author object are determined by the column headings
     * used to instantiate this object. 
     * The Author object to update is found in the last ("hidden")
     * column of the row.
     * 
     * @param col   The column within a row of data 
     *              to find the value to translate
     * @param row   An array of objects representing one row of data
     *              from a table.
     * 
     * @see #translateFieldsFromTable(Object[][])
     */
    private void translateFieldToTable( int col, Object[] row )
    {
        Supplier<Object>    supplier    = fromAuthorMap.get( col );
        Object              value       = supplier.get();
        row[col] = value;
    }
    
    /**
     * Gets a Consumer that translates a value from a cell in a table
     * into an update to an Author object.
     * The heading of the cell's column is given as input.
     * The expected type of the data, and the destination field
     * within the Author object are determined by the column heading.
     * 
     * @param columnID  heading of the column that contains the source data
     * 
     * @return  A Consumer that translates a value into an update
     *          to an Author object
     *          
     * @throws KCLSException if the column ID does not match
     *         a field name from the Constants class
     */
    private Consumer<Object> getConsumer( Object columnID )
        throws KCLSException
    {
        Consumer<Object>    consumer    = null;
        String              heading     = castToString( columnID );

        switch ( heading )
        {
        case RANK_NAME:
            consumer = o -> setRank( o );
            break;
        case RATING_NAME:
            consumer = o -> setRating( o );
            break;
        case SOURCE_NAME:
            consumer = o -> setSource( o );
            break;
        case CREATION_DATE_NAME:
            consumer = o -> setCreationDate( o );
            break;
        case MODIFY_DATE_NAME:
            consumer = o -> setModifyDate( o );
            break;
        case AUTHOR_NAME:
            consumer = o -> setAuthor( o );
            break;
        case LIST_NAME_NAME:
            consumer = o -> setListName( o );
            break;
        case LAST_COUNT_NAME:
            consumer = o -> setLastCount( o );
            break;
        case CURRENT_COUNT_NAME:
            consumer = o -> setCurrentCount( o );
            break;
        default:
            String  message = "\"" + heading + "\": no recognized column heading";
            throw new KCLSException( message );
        }
        
        return consumer;
    }
    
    /**
     * Gets a Supplier that translates a field in an Author object
     * into a value to be placed in the appropriate column
     * of a row in a table.
     * The target column's heading is given as input.
     * The source field of the value is determined by the column heading. 
     * 
     * @param columnID  heading of the column that contains the supplied data
     * 
     * @return  A Supplier that translates a field in an Author object
     *          to a value to be stored in a row of a table
     */
    private Supplier<Object> getSupplier( Object columnID )
    {
        Supplier<Object>    supplier    = null;
        String              heading     = castToString( columnID );
//      private int                 rank            = 0;
//      private int                 rating          = 0;
//      private String              source          = "";
//      private Set<Comment>        comments        = new HashSet<>();
//      private LocalDate           creationDate    = LocalDate.now();
//      private LocalDate           modifyDate      = LocalDate.now();
//      private String      author          = "";
//      private String      list            = "";
//      private int         lastCount       = 0;
//      private int         currCount       = 0;

        switch ( heading )
        {
        case RANK_NAME:
            supplier = () -> getRank();
            break;
        case RATING_NAME:
            supplier = () -> getRating();
            break;
        case SOURCE_NAME:
            supplier = () -> getSource();
            break;
        case CREATION_DATE_NAME:
            supplier = () -> getCreationDate();
            break;
        case MODIFY_DATE_NAME:
            supplier = () -> getModifyDate();
            break;
        case AUTHOR_NAME:
            supplier = () -> getAuthor();
            break;
        case LIST_NAME_NAME:
            supplier = () -> getListName();
            break;
        case LAST_COUNT_NAME:
            supplier = () -> getLastCount();
            break;
        case CURRENT_COUNT_NAME:
            supplier = () -> getCurrentCount();
            break;
        default:
            String  message = "\"" + heading + "\": no recognized column heading";
            throw new KCLSException( message );
        }
        
        return supplier;
    }

    /**
     * Sets the author of the target Author object.
     * Input is expected to be type String.
     * 
     * @param obj   The author of the target Author object
     */
    private void setAuthor( Object obj )
    {
        String  name    = castToString( obj );
        author.setAuthor( name );
    }

    /**
     * Sets the list name of the target Author object.
     * Input is expected to be type String.
     * 
     * @param obj   The author of the target Author object
     */
    private void setListName( Object obj )
    {
        String  listName    = castToString( obj );
        author.setListName( listName );
    }
    
    /**
     * Sets the rank of the target Author object.
     * Input is expected to be type Integer.
     * 
     * @param obj   The rank of the target Author object
     */
    private void setRank( Object obj )
    {
        int rank    = castToInt( obj );
        author.setRank( rank );
    }
    
    /**
     * Sets the rating of the target Author object.
     * Input is expected to be type Integer.
     * 
     * @param obj   The rating of the target Author object
     */
    private void setRating( Object obj )
    {
        int rating  = castToInt( obj );
        author.setRating( rating );
    }
    
    /**
     * Sets the source of the target Author object.
     * Input is expected to be type String.
     * 
     * @param obj   The source of the target Author object
     */
    private void setSource( Object obj )
    {
        String  source  = castToString( obj );
        author.setSource( source );
    }

    /**
     * Sets the creation date of the target Author object.
     * Input is expected to be type java.util.Date.
     * 
     * @param obj   The creation date of the target Author object
     */
    private void setCreationDate( Object obj )
    {
        LocalDate   date    = getLocalDate( obj );
        author.setCreationDate( date );
    }

    /**
     * Sets the modification date of the target Author object.
     * Input is expected to be type java.util.Date.
     * 
     * @param obj   The modification date of the target Author object
     */
    private void setModifyDate( Object obj )
    {
        LocalDate   date    = getLocalDate( obj );
        author.setModifyDate( date );
    }

    /**
     * Sets the last count of the target Author object.
     * Input is expected to be type Integer.
     * 
     * @param obj   The last count of the target Author object
     */
    private void setLastCount( Object obj )
    {
        int qPos    = castToInt( obj );
        author.setLastCount( qPos );
    }

    /**
     * Sets the current count of the target Author object.
     * Input is expected to be type Integer.
     * 
     * @param obj   The current count of the target Author object
     */
    private void setCurrentCount( Object obj )
    {
        int qPos    = castToInt( obj );
        author.setCurrentCount( qPos );
    }

    /**
     * Gets the author of the source Author object.
     * 
     * @return  The author of the source Author object
     */
    private String getAuthor()
    {
        String  name  = author.getAuthor();
        return name;
    }

    /**
     * Gets the list name of the source Author object.
     * 
     * @return  The list name of the source Author object
     */
    private String getListName()
    {
        String  listName    = author.getListName();
        return listName;
    }
    
    /**
     * Gets the rank of the source Author object.
     * 
     * @return  The rank of the source Author object
     */
    private Integer getRank()
    {
        Integer  rank  = author.getRank();
        return rank;
    }
    
    /**
     * Gets the rating of the source Author object.
     * 
     * @return  The rating of the source Author object
     */
    private Integer getRating()
    {
        Integer  rating  = author.getRating();
        return rating;
    }
    
    /**
     * Gets the source property of the source Author object.
     * 
     * @return  The rating of the source Author object
     */
    private String getSource()
    {
        String  source  = author.getSource();
        return source;
    }
    
    /**
     * Gets the creation date of the source Author object.
     * 
     * @return  The creation date of the source Author object
     */
    private Date getCreationDate()
    {
        LocalDate   localDate   = author.getCreationDate();
        Date        dateOut     = getDate( localDate );
        return dateOut;
    }
    
    /**
     * Gets the modification date of the source Author object.
     * 
     * @return  The modification date of the source Author object
     */
    private Date getModifyDate()
    {
        LocalDate   localDate   = author.getModifyDate();
        Date        dateOut     = getDate( localDate );
        return dateOut;
    }

    /**
     * Gets the last count of the source Author object.
     * 
     * @return  the last count of the source Author object
     */
    private Integer getLastCount()
    {
        Integer  lastCount  = author.getLastCount();
        return lastCount;
    }

    /**
     * Gets the current count of the source Author object.
     * 
     * @return  The current count of the source Author object
     */
    private Integer getCurrentCount()
    {
        Integer  reckonQPos  = author.getCurrentCount();
        return reckonQPos;
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
        if ( !(obj instanceof Author) )
        {
            String  name    = obj.getClass().getName();
            String  message = "Object not type Author: " + name;
            throw new KCLSException( message );
        }
        return (Author)obj;
    }
}
