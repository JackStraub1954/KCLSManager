package app;

import static kcls_manager.database.DBConstants.LISTS_TABLE_NAME;
import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.TITLE_TYPE;

import java.util.logging.Logger;

import kcls_manager.database.DBServer;
import kcls_manager.main.KCLSList;

/**
 * Initializes the database LIST table.
 * Typically this program should be executed <em>once,</em>
 * immediately following database creation.
 * 
 * @author jstra
 *
 */
public class InitListTable
{
    private static final String loggerName  = InitListTable.class.getName();
    private static final Logger logger      = Logger.getLogger( loggerName );
    
    private final DBServer  dbServer    = new DBServer();
    
    public static void main(String[] args)
    {
        logger.info( "Beginning LISTS table initialization" );
        new InitListTable().execute();
        logger.info( "LISTS table initialization complete" );
    }
    
    private void execute()
    {
        // start with an empty list
        logger.info( "truncating " + LISTS_TABLE_NAME + " table" );
        dbServer.truncateTable( LISTS_TABLE_NAME );
        logger.info( LISTS_TABLE_NAME + " truncated" );
        
        KCLSList    list;
        
        String[]    mostTitleLists  =
        {
            "Wish List Titles",
            "Later Titles",
            "In Progress Titles",
            "Completed Titles"      
        };
        
        for ( String titleList : mostTitleLists )
        {
            list = new KCLSList( TITLE_TYPE, titleList );
            dbServer.insertList( list );
        }
        
        list = new KCLSList( TITLE_TYPE, "On Hold Titles" );
        dbServer.insertList( list );
        
        list = new KCLSList( AUTHOR_TYPE, "Wish List Authors" );
        dbServer.insertList( list );
    }
}
