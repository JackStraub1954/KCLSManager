package app;

import static kcls_manager.database.DBConstants.LISTS_TABLE_NAME;

import java.util.List;

import kcls_manager.database.DBServer;
import kcls_manager.main.KCLSList;

public class DBDumper
{
    private final DBServer  dbServer    = new DBServer();

    public static void main(String[] args)
    {
        new DBDumper();
    }
    
    public DBDumper()
    {
//        DBDumper    dumper  = new DBDumper();
//        dumper.dbServer.dumpTable( LISTS_TABLE_NAME );
        dbServer.dumpTable( LISTS_TABLE_NAME );
        
        System.out.println( "***** LISTS TABLE: ");
        List<KCLSList>  lists    = dbServer.getAllLists();
        for ( KCLSList list : lists )
            System.out.println( list );
        
        System.out.println( "*** title lists: ");
        lists    = dbServer.getTitleLists();
        for ( KCLSList list : lists )
            System.out.println( list );
        
        System.out.println( "*** author lists: ");
        lists    = dbServer.getAuthorLists();
        for ( KCLSList list : lists )
            System.out.println( list );
    }
}
