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

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TitleContentTranslator extends ContentTranslator
{
    /** 
     * The total number of columns in s row
     * that are used to store data associated 
     * with a Title property. 
     * This does not include the <em>hidden</em> column
     * which is used to store the associated Title object.
     */
    private final int       numDataCols;
    
    /** 
     * Map to take a cell value from a row and translate it to a 
     * field in a Title object.
     */
    private final Map<Integer,Consumer<Object>> 
        toTitleMap      = new HashMap<>();

    /**
     * Map to take a value from a field in a Title object
     * and translate it to a cell in a row.
     */
    private final Map<Integer,Supplier<Object>> 
        fromTitleMap    = new HashMap<>();
    
    /** Principal object of translation */
    private Title           title;
    
    /**
     * Constructor.
     * Determines how to map columns in a row of a table
     * into updates to the corresponding field
     * in a Title object.
     * This mapping is based on the table's column headings;
     * for example, if the heading for column 3 in a table
     * is "Reckon Date" (Constants.RECKON_DATE_NAME) 
     * the type of the input data is java.util.Date,
     * and the target property in the Title object
     * is the reckon-date.
     * 
     * The caller supplies the column headings in sequential order.
     * Not that the last ("hidden") column is expected to store
     * the target Title object, and is not used in the 
     * column-to-Title-field mapping.
     * 
     * @param columnIDs The headings of the columns in a table
     */
    public TitleContentTranslator( Object[] columnIDs )
    {
        // -1 because the last column is the hidden column that contains
        // the title objects.
        numDataCols = columnIDs.length - 1;
        for ( int inx = 0 ; inx < numDataCols ; ++inx )
        {
            Consumer<Object>    consumer    = getConsumer( columnIDs[inx] );
            toTitleMap.put( inx, consumer );
            
            Supplier<Object>    supplier    = getSupplier( columnIDs[inx] );
            fromTitleMap.put( inx, supplier );
        }
    }
    
    /**
     * Translate the data from the rows of a table into updates
     * to their corresponding Title objects. 
     * The expected type of the data, and the destination field
     * within the Title object are determined by:
     * <ul>
     * <li>a value's columnar position within a row</li>
     * <li>the column's heading</li>
     * </ul>
     * An array of column headings is provided
     * when instantiating this object.
     * The Title object to update is found in the last ("hidden")
     * column of each row.
     * 
     * @param rows  the rows of the table to translate into
     *              Title object updates
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
     * to its corresponding Title object. 
     * The expected type of the data, and the destination field
     * within the Title object are determined by:
     * <ul>
     * <li>a value's columnar position within a row</li>
     * <li>the column's heading</li>
     * </ul>
     * An array of column headings is provided
     * when instantiating this object.
     * The Title object to update is found in the last ("hidden")
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
        title = castToTitle( row[numDataCols] );
        for ( int inx = 0 ; inx < numDataCols ; ++inx )
            translateFieldFromTable( inx, row );
    }
    
    /**
     * Translate the properties of a Title object into 
     * a row for insertion into a table.
     * The expected type of the data, and the destination field
     * within the Title object are determined by:
     * <ul>
     * <li>a value's columnar position within a row</li>
     * <li>the column title used to instantiate this object</li>
     * </ul>
     * An array of column headings is provided
     * when instantiating this object.
     * The source Title object is placed in the last ("hidden")
     * column of the row.
     * 
     * @param title     the title object to translate into a row
     * 
     * @return the translated row
     * 
     * @see #translateFieldToTable(int, Object[])
     */
    public Object[] translateFieldsToTable( Title title )
    {
        this.title = title;
        // + 1 for "hidden" column
        Object[] row = new Object[numDataCols + 1];
        row[numDataCols] = title;
        for ( int inx = 0 ; inx < numDataCols ; ++inx )
        {
            translateFieldToTable( inx, row );
        }
        
        return row;
    }

    /**
     * Read a value from a cell in a table, and translate it into
     * an update to a Title object. 
     * The horizontal position of the data is given as input.
     * The expected type of the data, and the destination field
     * within the Title object are determined by the column headings
     * used to instantiate this object. 
     * The Title object to update is found in the last ("hidden")
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
        title = castToTitle( row[numDataCols] );
        Consumer<Object>    consumer    = toTitleMap.get( col );
        consumer.accept( row[col] );
    }

    /**
     * Read a value from a cell in a table, and translate it into
     * an update to a Title object. 
     * The horizontal position of the data is given as input.
     * The expected type of the data, and the destination field
     * within the Title object are determined by the column headings
     * used to instantiate this object. 
     * The Title object to update is found in the last ("hidden")
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
        Supplier<Object>    supplier    = fromTitleMap.get( col );
        Object              value       = supplier.get();
        row[col] = value;
    }
    
    /**
     * Gets a Consumer that translates a value from a cell in a table
     * into an update to a Title object.
     * The heading of the cell's column is given as input.
     * The expected type of the data, and the destination field
     * within the Title object are determined by the column heading.
     * 
     * @param columnID  heading of the column that contains the source data
     * 
     * @return  A Consumer that translates a value into an update
     *          to a Title object
     */
    private Consumer<Object> getConsumer( Object columnID )
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
        case TITLE_NAME:
            consumer = o -> setTitle( o );
            break;
        case AUTHOR_NAME:
            consumer = o -> setAuthor( o );
            break;
        case LIST_NAME_NAME:
            consumer = o -> setListName( o );
            break;
        case CREATION_DATE_NAME:
            consumer = o -> setCreationDate( o );
            break;
        case MODIFY_DATE_NAME:
            consumer = o -> setModifyDate( o );
            break;
        case CHECK_DATE_NAME:
            consumer = o -> setCheckDate( o );
            break;
        case RECKON_DATE_NAME:
            consumer = o -> setReckonDate( o );
            break;
        case CHECK_QPOS_NAME:
            consumer = o -> setCheckQPos( o );
            break;
        case RECKON_QPOS_NAME:
            consumer = o -> setReckonQPos( o );
            break;
        case MEDIA_TYPE_NAME:
            consumer = o -> setMediaType( o );
            break;
            
        default:
            String  message = "\"" + heading + "\": no recognized column heading";
            throw new KCLSException( message );
        }
        
        return consumer;
    }
    
    /**
     * Gets a Supplier that translates a field in a Title object
     * into a value to be placed in the appropriate column
     * of a row in a table.
     * The target column's heading is given as input.
     * The source field of the value is determined by the column heading. 
     * 
     * @param columnID  heading of the column that contains the supplied data
     * 
     * @return  A Supplier that translates a field in a Title object
     *          to a value to be stored in a row of a table
     */
    private Supplier<Object> getSupplier( Object columnID )
    {
        Supplier<Object>    supplier    = null;
        String              heading     = castToString( columnID );

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
        case TITLE_NAME:
            supplier = () -> getTitle();
            break;
        case AUTHOR_NAME:
            supplier = () -> getAuthor();
            break;
        case LIST_NAME_NAME:
            supplier = () -> getListName();
            break;
        case CREATION_DATE_NAME:
            supplier = () -> getCreationDate();
            break;
        case MODIFY_DATE_NAME:
            supplier = () -> getModifyDate();
            break;
        case CHECK_DATE_NAME:
            supplier = () -> getCheckDate();
            break;
        case RECKON_DATE_NAME:
            supplier = () -> getReckonDate();
            break;
        case CHECK_QPOS_NAME:
            supplier = () -> getCheckQPos();
            break;
        case RECKON_QPOS_NAME:
            supplier = () -> getReckonQPos();
            break;
        case MEDIA_TYPE_NAME:
            supplier = () -> getType();
            break;
            
        default:
            String  message = "\"" + heading + "\": no recognized column heading";
            throw new KCLSException( message );
        }
        
        return supplier;
    }

    /**
     * Sets the title of the target Title object.
     * Input is expected to be type String.
     * 
     * @param obj   The title of the target Title object
     */
    private void setTitle( Object obj )
    {
        String  text    = castToString( obj );
        title.setTitle( text );
    }

    /**
     * Sets the author of the target Title object.
     * Input is expected to be type String.
     * 
     * @param obj   The author of the target Title object
     */
    private void setAuthor( Object obj )
    {
        String  author  = castToString( obj );
        title.setAuthor( author );
    }

    /**
     * Sets the list name of the target Title object.
     * Input is expected to be type String.
     * 
     * @param obj   The author of the target Title object
     */
    private void setListName( Object obj )
    {
        String  listName    = castToString( obj );
        title.setListName( listName );
    }
    
    /**
     * Sets the rank of the target Title object.
     * Input is expected to be type Integer.
     * 
     * @param obj   The rank of the target Title object
     */
    private void setRank( Object obj )
    {
        int rank    = castToInt( obj );
        title.setRank( rank );
    }
    
    /**
     * Sets the rating of the target Title object.
     * Input is expected to be type Integer.
     * 
     * @param obj   The rating of the target Title object
     */
    private void setRating( Object obj )
    {
        int rating  = castToInt( obj );
        title.setRating( rating );
    }
    
    /**
     * Sets the source of the target Title object.
     * Input is expected to be type String.
     * 
     * @param obj   The source of the target Title object
     */
    private void setSource( Object obj )
    {
        String  source  = castToString( obj );
        title.setSource( source );
    }
    
    /**
     * Sets the media type of the target Title object.
     * Input is expected to be type String.
     * 
     * @param obj   The media type of the target Title object
     */
    private void setMediaType( Object obj )
    {
        String  type    = castToString( obj );
        title.setMediaType( type );
    }

    /**
     * Sets the creation date of the target Title object.
     * Input is expected to be type java.util.Date.
     * 
     * @param obj   The creation date of the target Title object
     */
    private void setCreationDate( Object obj )
    {
        LocalDate   date    = getLocalDate( obj );
        title.setCreationDate( date );
    }

    /**
     * Sets the modification date of the target Title object.
     * Input is expected to be type java.util.Date.
     * 
     * @param obj   The modification date of the target Title object
     */
    private void setModifyDate( Object obj )
    {
        LocalDate   date    = getLocalDate( obj );
        title.setModifyDate( date );
    }

    /**
     * Sets the check date of the target Title object.
     * Input is expected to be type java.util.Date.
     * 
     * @param obj   The check date of the target Title object
     */
    private void setCheckDate( Object obj )
    {
        LocalDate   date    = getLocalDate( obj );
        title.setCheckDate( date );
    }

    /**
     * Sets the reckon date of the target Title object.
     * Input is expected to be type java.util.Date.
     * 
     * @param obj   The reckon date  of the target Title object
     */
    private void setReckonDate( Object obj )
    {
        LocalDate   date    = getLocalDate( obj );
        title.setReckonDate( date );
    }

    /**
     * Sets the check-queue position of the target Title object.
     * Input is expected to be type java.util.Date.
     * 
     * @param obj   The check-queue position  of the target Title object
     */
    private void setCheckQPos( Object obj )
    {
        int qPos    = castToInt( obj );
        title.setCheckQPos( qPos );
    }

    /**
     * Sets the reckon-queue position of the target Title object.
     * Input is expected to be type java.util.Date.
     * 
     * @param obj   The reckon-queue position  of the target Title object
     */
    private void setReckonQPos( Object obj )
    {
        int qPos    = castToInt( obj );
        title.setReckonQPos( qPos );
    }

    /**
     * Gets the title of the source Title object.
     * 
     * @return  The title of the target Title object
     */
    private String getTitle()
    {
        String  text    = title.getTitle();
        return text;
    }

    /**
     * Gets the author of the source Title object.
     * 
     * @return  The author of the source Title object
     */
    private String getAuthor()
    {
        String  author  = title.getAuthor();
        return author;
    }

    /**
     * Gets the list name of the source Title object.
     * 
     * @return  The list name of the source Title object
     */
    private String getListName()
    {
        String  listName    = title.getListName();
        return listName;
    }
    
    /**
     * Gets the rank of the source Title object.
     * 
     * @return  The rank of the source Title object
     */
    private Integer getRank()
    {
        Integer  rank  = title.getRank();
        return rank;
    }
    
    /**
     * Gets the rating of the source Title object.
     * 
     * @return  The rating of the source Title object
     */
    private Integer getRating()
    {
        Integer  rating  = title.getRating();
        return rating;
    }
    
    /**
     * Gets the source property of the source Title object.
     * 
     * @return  The rating of the source Title object
     */
    private String getSource()
    {
        String  source  = title.getSource();
        return source;
    }
    
    /**
     * Gets the media type of the source Title object.
     * 
     * @return  The media type of the source Title object
     */
    private String getType()
    {
        String  type  = title.getMediaType();
        return type;
    }
//
    
    /**
     * Gets the creation date of the source Title object.
     * 
     * @return  The creation date of the source Title object
     */
    private Date getCreationDate()
    {
        LocalDate   localDate   = title.getCreationDate();
        Date        dateOut     = getDate( localDate );
        return dateOut;
    }
    
    /**
     * Gets the creation date of the source Title object.
     * 
     * @return  The title of the source Title object
     */
    private Date getModifyDate()
    {
        LocalDate   localDate   = title.getModifyDate();
        Date        dateOut     = getDate( localDate );
        return dateOut;
    }
    
    /**
     * Gets the last-checked date of the source Title object.
     * 
     * @return  The check-date of the source Title object
     */
    private Date getCheckDate()
    {
        LocalDate   localDate   = title.getCheckDate();
        Date        dateOut     = getDate( localDate );
        return dateOut;
    }
    
    /**
     * Gets the reckon-date of the source Title object.
     * 
     * @return  The reckon-date of the source Title object
     */
    private Date getReckonDate()
    {
        LocalDate   localDate   = title.getReckonDate();
        Date        dateOut     = getDate( localDate );
        return dateOut;
    }

    /**
     * Gets the check-queue position of the source Title object.
     * 
     * @return  The check-queue position of the source Title object
     */
    private Integer getCheckQPos()
    {
        Integer  checkQPos  = title.getCheckQPos();
        return checkQPos;
    }

    /**
     * Gets the reckon-queue position of the source Title object.
     * 
     * @return  The reckon-queue position of the source Title object
     */
    private Integer getReckonQPos()
    {
        Integer  reckonQPos  = title.getReckonQPos();
        return reckonQPos;
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
        if ( !(obj instanceof Title) )
        {
            String  name    = obj.getClass().getName();
            String  message = "Object not type Title: " + name;
            throw new KCLSException( message );
        }
        return (Title)obj;
    }
}
