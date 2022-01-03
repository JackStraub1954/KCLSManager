package app;

import static kcls_manager.database.DBConstants.AUTHORS_TABLE_NAME;
import static kcls_manager.database.DBConstants.LISTS_TABLE_NAME;
import static kcls_manager.database.DBConstants.TITLES_TABLE_NAME;
import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.TITLE_TYPE;

import java.util.logging.Logger;

import kcls_manager.database.DBServer;
import kcls_manager.main.KCLSList;
import kcls_manager.main.Title;
import util.TitleFactory;

public class InitSampleDB
{
    private static final String loggerName  = InitListTable.class.getName();
    private static final Logger logger      = Logger.getLogger( loggerName );
    
    private static final String[]   titleLists      =
    {
        "Wish List Titles",
        "Later Titles",
        "In Progress Titles",
        "Completed Titles"      
    };
    
    private static final String[]   authorLists =
    {
        "List One Authors",
        "List Two Authors",
        "List Three Authors",
        "List Four Authors",
    };

    private final DBServer  dbServer    = new DBServer();
    
    public static void main(String[] args)
    {
        logger.info( "Beginning LISTS table initialization" );
        new InitSampleDB().execute();
        logger.info( "LISTS table initialization complete" );
    }
    
    private void execute()
    {
        truncate( TITLES_TABLE_NAME );
        truncate( AUTHORS_TABLE_NAME );
        truncate( LISTS_TABLE_NAME );
        
        initLists();
        initTitles();
    }
    
    private void truncate( String tableName )
    {
        logger.info( "truncating " + tableName + " table" );
        dbServer.truncateTable( tableName );
        logger.info( tableName + " truncated" );
    }
    
    private void initLists()
    {
        for ( String titleList : titleLists )
        {
            KCLSList    list = new KCLSList( TITLE_TYPE, titleList );
            dbServer.insertList( list );
        }
        
        for ( String authorList : authorLists )
        {
            KCLSList    list = new KCLSList( AUTHOR_TYPE, authorList );
            dbServer.insertList( list );
        }
    }
    
    private void initTitles()
    {
        TitleFactory    titleFactory    = new TitleFactory();
        
        for ( String listName : titleLists )
        {
            for ( int inx = 0 ; inx < 5 ; ++inx )
            {
                Title   title   = titleFactory.getUniqueTitle( 5 );
                title.setListName( listName );
                System.out.println( title );
                dbServer.insertTitle( title );
            }
        }
    }
}
