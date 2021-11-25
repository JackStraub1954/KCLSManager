package app;

import java.util.HashSet;
import java.util.Set;

import kcls_manager.components.CommentEditor;
import kcls_manager.main.Comment;
import kcls_manager.main.LibraryItem;
import kcls_manager.main.Title;

public class ShowCommentEditor
{
    public static void main(String[] args)
    {
        Set<Comment>    comments    = new HashSet<>();
        for ( int inx = 0 ; inx < 10 ; ++inx )
        {
            String  text    = String.format( "Comment #%02d", inx );
            Comment comment = new Comment( 0, text );
            comments.add( comment );
        }
        
        LibraryItem         item        = new Title( "List 1", "Some Title" );
        item.setComments( comments );
        CommentEditor       editor      = new CommentEditor( null );
        int                 selected    = editor.showDialog( true, item );
        editor.dispose();
        System.out.println( "main; selected: "  + selected );
    }
}
