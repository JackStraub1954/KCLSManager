package app;

import static kcls_manager.database.DBConstants.DB_URL;
import static kcls_manager.main.Constants.TITLE_TYPE;

import java.util.List;

import kcls_manager.components.TitleDialog;
import kcls_manager.database.DBServer;
import kcls_manager.main.DataManager;
import kcls_manager.main.KCLSList;
import kcls_manager.main.TitleContentProvider;

public class ShowTitleDialog
{
    private static final DBServer   dbServer    = 
        DataManager.getDBServer( DB_URL );
    
    public static void main(String[] args)
    {
        List<KCLSList>  allLists    = dbServer.getAllLists();
        if ( allLists == null || allLists.size() == 0 )
            throw new IllegalStateException( "no lists found" );
        KCLSList        titleList   = null;
        int             size        = allLists.size();
        for (int inx = 0 ; inx < size && titleList == null ; ++inx )
        {
            KCLSList    list    = allLists.get( inx );
            if ( list.getListType() == TITLE_TYPE )
                titleList = list;
        }
        if ( titleList == null )
            throw new IllegalStateException( "no title list found" );
        String                  listName    = titleList.getDialogTitle();
        TitleContentProvider    provider    = new TitleContentProvider( listName );
        TitleDialog             dialog      = new TitleDialog( provider );
        int                     status      = dialog.showDialog( true );
        System.out.println( status );
        dialog.dispose();
    }

}
