package kcls_manager.main;

import static kcls_manager.main.Constants.AUTHOR_NAME;
import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.CREATION_DATE_NAME;
import static kcls_manager.main.Constants.CURRENT_COUNT_NAME;
import static kcls_manager.main.Constants.LAST_COUNT_NAME;
import static kcls_manager.main.Constants.RANK_NAME;
import static kcls_manager.main.Constants.RATING_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import kcls_manager.database.DBServer;

/**
 * Formats rows to populate a table based on a set of Author objects.
 * The content of each column is based on the name of that column;
 * for example, if the name of a column is "Rank," that column 
 * will be populated with the rank property of each author.
 * For this reason column names should always be represented
 * as a symbol from the Constants class.
 * 
 * @author jstra
 * 
 * @see #authors
 * @see #columnNames
 *
 */
public class AuthorContentProvider extends ContentProvider
{
    /** The collection of authors used to populate the rows of a table. */
    private final Set<Author>    authors;
    
    /**
     * Default set of column names.
     * 
     * @see #columnNames
     */
    private static final String[]   defColumnNames = 
    { 
        AUTHOR_NAME, 
        RANK_NAME, 
        RATING_NAME, 
        LAST_COUNT_NAME,
        CURRENT_COUNT_NAME,
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
    private final AuthorContentTranslator    xlator;
    
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
     * Constructor. Provides access to authors corresponding to
     * a given list. The list must already exist.
     * 
     * @param listName the name of the given list
     */
    public AuthorContentProvider( String listName )
    {
        this( listName, defColumnNames );
    }
    
    /**
     * Constructor. Provides access to authors corresponding to
     * a given list. The list must already exist;
     * the caller must supply the column headings
     * for displaying Author fields in a table.
     * 
     * @param listName      the name of the given list
     * @param columnNames   the required column headings
     */
    public AuthorContentProvider( String listName, String[] columnNames )
    {
        dialogTitle = inferListLabel( listName );
        authors = new HashSet<>();
        authors.addAll( getAuthorsForList( listName ) );
        this.columnNames = columnNames;
        xlator = new AuthorContentTranslator( columnNames );
    }
    
    /**
     * Returns the index to the last column of the corresponding table.
     * This is the hidden column that contains the Author instances
     * represented by this content provider.
     * 
     * @return the index to the last column of the corresponding table
     */
    public int getDataColumnIndex()
    {
        return columnNames.length - 1;
    }
    
    /**
     * Returns a list containing the names
     * of all the lists of authors in the database.
     * 
     * @return  a collection containing the names
     *          of all the lists of authors in the database
     */
    public static List<String> getAllListNames()
    {
        // TODO is this method necessary? Does it belong in an
        //      abstract superclass?
        List<String>    allListNames    = new ArrayList<>();
        List<KCLSList>  allLists        = dbServer.getAllLists();
        for ( KCLSList list : allLists )
            if ( list.getListType() == AUTHOR_TYPE )
                allListNames.add( list.getDialogTitle() );
        return allListNames;
    }
    
    /**
     * Returns a collection of all Authors belonging to a given list.
     * 
     * @param listName  the name of the given list
     * 
     * @return a collection of all Authors belonging to the given list
     */
    public static Set<Author> getAuthorsForList( String listName )
    {
        Set<Author>          list    = new HashSet<>();
        List<Author>         authors  = dbServer.getAuthorsForList( listName );
        list.addAll( authors );
        return list;
    }
    
    /**
     * Returns a collection of all Authors associated with
     * this content provider.
     * 
     * @return  a collection of all Authors associated with
     *          this content provider
     */
    public Set<Author> getAuthors()
    {
        // TODO does this method belong in an abstract superclass?
        return authors;
    }
    
    /**
     * Copies a given collection of Authors to be used
     * as the base for this content provider
     * 
     * @param authors    the given collection of authors
     */
    public void setAuthors( Collection<Author> authors )
    {
        // TODO does this method belong in an abstract superclass?
        this.authors.clear();
        this.authors.addAll( authors );
    }
    
    /**
     * Creates a collection of authors to be used
     * as the base for this content provider
     * using the rows of a given JTable.
     * Author objects are assumed to be in the last
     * ("hidden") column of the table.
     * 
     * @param table the given JTable
     */
    public void setAuthors( JTable table )
    {
        TableModel  model       = table.getModel();
        int         rowCount    = model.getRowCount();
        int         dataColumn  = getDataColumnIndex();
        
        authors.clear();
        for ( int inx = 0 ; inx < rowCount ; ++inx )
        {
            Object  obj     = model.getValueAt( inx, dataColumn );
            if ( !(obj instanceof Author) )
            {
                String  name    = obj.getClass().getName();
                String  message = "Expected type Author; was: " + name;
                throw new KCLSException( message );
            }
            authors.add( (Author)obj );
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
     * representing the Authors
     * associated with this content provider.
     * 
     * @return  an iterator that streams the Authors
     *          associated with this content provider
     *          
     * @see AuthorContentProvider.AuthorIterator
     */
    @Override
    public Iterator<Object[]> iterator()
    {
        Iterator<Object[]>  iter    = new AuthorIterator( authors.iterator() );
        return iter; 
    }

    /**
     * Returns an array of column names for the table
     * associated with this content provider.
     * 
     * @return  an array of column names for the table
     *          associated with this content provider
     */
    public Object[] getHeaders()
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
     * representing the Authors associated with this content provider.
     * 
     * @author jstra
     *
     *@see #iterator
     */
    private class AuthorIterator implements Iterator<Object[]>
    {
        /** Iterator associated with a collection authors. */
        private final Iterator<Author>   allAuthors;
        
        /**
         * Constructor.
         * 
         * @param iter  iterator associated with a collection authors
         */
        public AuthorIterator( Iterator<Author> iter )
        {
            allAuthors = iter;
        }
        
        @Override
        public boolean hasNext()
        {
            return allAuthors.hasNext();
        }

        @Override
        public Object[] next()
        {
            Author      author  = allAuthors.next();
            Object[]    row     = xlator.translateFieldsToTable( author );

            return row;
        }
    }
}
