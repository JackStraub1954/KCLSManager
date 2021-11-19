package kcls_manager.main;

import static kcls_manager.main.Constants.AUTHOR_NAME;
import static kcls_manager.main.Constants.CREATION_DATE_NAME;
import static kcls_manager.main.Constants.RANK_NAME;
import static kcls_manager.main.Constants.RATING_NAME;
import static kcls_manager.main.Constants.TITLE_NAME;
import static kcls_manager.main.Constants.TITLE_TYPE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import kcls_manager.database.DBServer;

/**
 * Formats rows to populate a table based on a set of Title objects.
 * The content of each column is based on the name of that column;
 * for example, if the name of a column is "Rank," that column 
 * will be populated with the rank property of each title.
 * For this reason column names should always be represented
 * as a symbol from the Contants class.
 * 
 * @author jstra
 * 
 * @see #titles
 * @see #columnNames
 *
 */
public class TitleContentProvider extends ContentProvider
{
    /** The collection of titles used to populate the rows of a table. */
    private final List<Title>       titles;
    
    /**
     * Default set of column names.
     * 
     * @see #columnNames
     */
    private static final String[]   defColumnNames = 
    { 
        TITLE_NAME,
        AUTHOR_NAME, 
        RANK_NAME, 
        RATING_NAME, 
        CREATION_DATE_NAME, 
        "hidden" 
    };
    
    /**
     * The names of the columns comprising each row 
     * in the associated table.
     * <em>Must</em> be taken from the Constants class.
     */
    private final String[] columnNames;
    
    /**
     */
    private final TitleContentTranslator    xlator;
    
    /** 
     * The title of the dialog that contains the table
     * associated with this content provider.
     */
    private final String        dialogTitle;
    
    /**
     *  The DBServer singleton.
     *  It is presumed that the singleton has already been instantiated
     *  with the appropriate database connection parameters.
     */
    private static final DBServer      dbServer    = DataManager.getDBServer();
    
    /**
     * Constructor. Provides access to titles corresponding to
     * a given list. The list must already exist.
     * 
     * @param listName the name of the given list
     */
    public TitleContentProvider( String listName )
    {
        this( listName, defColumnNames );
    }
    
    /**
     * Constructor. Provides access to titles corresponding to
     * a given list. The list must already exist;
     * the caller must supply the column headings
     * for displaying Title fields in a table.
     * 
     * @param listName      the name of the given list
     * @param columnNames   the required column headings
     */
    public TitleContentProvider( String listName, String[] columnNames )
    {
        dialogTitle = inferListLabel( listName );
        titles = new ArrayList<>();
        titles.addAll( getTitlesForList( listName ) );
        this.columnNames = columnNames;
        xlator = new TitleContentTranslator( columnNames );
    }
    
    /**
     * Returns the index to the last column of the corresponding table.
     * This is the hidden column that contains the Title instances
     * represented by this content provider.
     * 
     * @return the index to the last column of the corresponding table
     */
    public int getDataColumn()
    {
        return columnNames.length - 1;
    }
    
    /**
     * Returns a collection containing the names
     * of all the lists of titles in the database.
     * 
     * @return  a collection containing the names
     *          of all the lists in the database
     */
    public static List<String> getAllListNames()
    {
        // TODO is this method necessary? Does it belong in an
        //      abstract superclass?
        List<String>        allListNames    = new ArrayList<>();
        List<KCLSList>      allLists        = dbServer.getAllLists();
        for ( KCLSList list : allLists )
            if ( list.getListType() == TITLE_TYPE )
                allListNames.add( list.getDialogTitle() );
        return allListNames;
    }
    
    /**
     * Returns a collection of all titles belonging to a given list.
     * 
     * @param listName  the name of the given list
     * 
     * @return a collection of all titles belong to the given list
     */
    public static List<Title> getTitlesForList( String listName )
    {
        List<Title>         list    = new ArrayList<>();
        List<Title>         titles  = dbServer.getTitlesForList( listName );
        list.addAll( titles );
        return list;
    }
    
    /**
     * Returns a collection of all titles associated with
     * this content provider.
     * 
     * @return  a collection of all titles associated with
     *          this content provider
     */
    public List<Title> getTitles()
    {
        // TODO does this method belong in an abstract superclass?
        return titles;
    }
    
    /**
     * Copies a given collection of titles to be used
     * as the base for this content provider
     * 
     * @param titles    the given collection of titles
     */
    public void setTitles( Collection<Title> titles )
    {
        // TODO does this method belong in an abstract superclass?
        this.titles.clear();
        this.titles.addAll( titles );
    }
    
    /**
     * Creates a collection of titles to be used
     * as the base for this content provider
     * using the rows of a given JTable.
     * Title objects are assumed to be in the last
     * ("hidden") column of the table.
     * 
     * @param table the given JTable
     */
    public void setTitles( JTable table )
    {
        TableModel  model       = table.getModel();
        int         rowCount    = model.getRowCount();
        int         dataColumn  = getDataColumn();
        
        titles.clear();
        for ( int inx = 0 ; inx < rowCount ; ++inx )
        {
            Object  obj     = model.getValueAt( inx, dataColumn );
            if ( !(obj instanceof Title) )
            {
                String  name    = obj.getClass().getName();
                String  message = "Expected type Title; was: " + name;
                throw new KCLSException( message );
            }
            titles.add( (Title)obj );
        }
    }
    
    /**
     * Returns the title of the dialog containing the table
     * associated with this content provider.
     * The dialog title is provided by the KCLSList
     * associated with this content provider.
     * 
     * @return  the title of the dialog containing the table
     *          associated with this content provider.
     */
    public String getDialogTitle()
    {
        return dialogTitle;
    }

    /**
     * Returns an iterator that streams the rows
     * representing the titles
     * associated with this content provider.
     * 
     * @return  an iterator that streams the titles
     *          associated with this content provider
     *          
     * @see TitleContentProvider.TitleIterator
     */
    @Override
    public Iterator<Object[]> iterator()
    {
        Iterator<Object[]>  iter    = new TitleIterator( titles.iterator() );
        return iter; 
    }

    /**
     * Returns an array of column names for the table
     * associated with this content provider.
     * 
     * @return  an array of column names for the table
     *          associated with this content provider
     */
    public String[] getHeaders()
    {
        return columnNames;
    }
    
    /**
     * Given a list name, infers a label for a GUI component
     * associated with this content provider.
     * For example, the list name "onHold" would be translated
     * to "On Hold Titles."
     * 
     * @param listName the given list name
     * 
     * @return  a label, suitable for use on a GUI component,
     *          inferred from the given list name
     */
    private String inferListLabel( String listName )
    {
        return listName;
//        String  title   = null;
//        
//        String[]    parts   = listName.split( "([A-Z])" );
//        if ( parts.length == 0 )
//            throw new KCLSException( "Unexpected empty list name" );
//        StringBuilder   bldr    = new StringBuilder();
//        bldr.append( Character.toUpperCase( parts[0].charAt( 0 ) ) );
//        bldr.append( parts[0].substring( 1 ) );
//        int part1Len    = parts[0].length();
//
//        if ( part1Len < listName.length() )
//        {
//            bldr.append( " " );
//            bldr.append( listName.substring( part1Len ) );
//        }
//        
//        bldr.append( " Titles" );
//        
//        title = bldr.toString();
//        System.out.println( title );
//        return title;
    }

    /**
     * Encapsulation of an iterator for the rows
     * representing the titles associated with this content provider.
     * 
     * @author jstra
     *
     *@see #iterator
     */
    private class TitleIterator implements Iterator<Object[]>
    {
        /** Iterator associated with a collection titles. */
        private final Iterator<Title>   allTitles;
        
        /**
         * Constructor.
         * 
         * @param iter  iterator associated with a collection titles
         */
        public TitleIterator( Iterator<Title> iter )
        {
            allTitles = iter;
        }
        
        @Override
        public boolean hasNext()
        {
            return allTitles.hasNext();
        }

        @Override
        public Object[] next()
        {
            Title       title   = allTitles.next();
            Object[]    row     = xlator.translateFieldsToTable( title );

            return row;
        }
    }
}
