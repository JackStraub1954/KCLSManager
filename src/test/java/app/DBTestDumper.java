package app;

import static kcls_manager.database.DBConstants.AUTHORS_TABLE_NAME;
import static kcls_manager.database.DBConstants.COMMENTS_TABLE_NAME;
import static kcls_manager.database.DBConstants.LISTS_TABLE_NAME;

import java.util.Arrays;

import kcls_manager.database.DBServer;
import util.TestUtils;

public class DBTestDumper
{
    private final DBServer  dbServer    = TestUtils.getDBServer();

    public static void main(String[] args)
    {
        new DBTestDumper();
    }
    
    public DBTestDumper()
    {
//        DBDumper    dumper  = new DBDumper();
//        dumper.dbServer.dumpTable( LISTS_TABLE_NAME );
        String[]    allTables   =
        {
            LISTS_TABLE_NAME,
            COMMENTS_TABLE_NAME,
            AUTHORS_TABLE_NAME
        };
        Arrays.stream( allTables ).forEach(t -> dbServer.dumpTable( t ) );
//        dbServer.dumpTable( LISTS_TABLE_NAME );
        
//        System.out.println( "***** LISTS TABLE: ");
//        List<KCLSList>  lists    = dbServer.getAllLists();
//        for ( KCLSList list : lists )
//            System.out.println( list );
//        
//        System.out.println( "*** title lists: ");
//        lists    = dbServer.getTitleLists();
//        for ( KCLSList list : lists )
//            System.out.println( list );
//        
//        System.out.println( "*** author lists: ");
//        lists    = dbServer.getAuthorLists();
//        for ( KCLSList list : lists )
//            System.out.println( list );
    }
}
