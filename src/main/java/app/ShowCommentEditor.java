package app;

import java.util.HashSet;
import java.util.Set;

import kcls_manager.components.CommentEditor;

public class ShowCommentEditor
{
    public static void main(String[] args)
    {
        Set<String>     comments    = new HashSet<>();
        for ( int inx = 0 ; inx < 10 ; ++inx )
        {
            String  comment = String.format( "Comment #%02d", inx );
            comments.add( comment );
        }
        
        CommentEditor       editor      = new CommentEditor( null );
        int                 selected    = editor.display( true, comments );
        editor.dispose();
        System.out.println( "main; selected: "  + selected );
    }
}
