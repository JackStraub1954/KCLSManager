package app;

import kcls_manager.components.TitleEditor;
import kcls_manager.main.Title;
import util.TitleFactory;

public class ShowTitleEditor
{
    public static void main(String[] args)
    {
        TitleFactory    titleFactory    = new TitleFactory();
        Title           title           = titleFactory.getUniqueTitle( 5 );
        TitleEditor     editor          = new TitleEditor( null );
        System.out.println( editor.isVisible() );
        int             selection       = editor.display( true , title );
        System.out.println( editor.isVisible() );
        System.out.println( selection );
        editor.dispose();
    }
}
