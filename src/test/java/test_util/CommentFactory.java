package test_util;

import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.TITLE_TYPE;

import java.util.OptionalInt;

import kcls_manager.main.Comment;

public class CommentFactory
{
    private static final String     fmtStr          = "%s%03d";
    private static final String     defText         = "default text ";
    private static final int[]      allTypes        = 
        { TITLE_TYPE, AUTHOR_TYPE };
    private static final int        allTypesCount   = allTypes.length;
    private int     commentCounter  = 0;
    
    public Comment getUniqueComment( int ident )
    {
        OptionalInt optIdent    = 
            ident > 0 ? OptionalInt.of( ident ): OptionalInt.empty();
        return getUniqueComment( optIdent ); 
    }
    
    public Comment getUniqueComment( OptionalInt optIdent )
    {
        ++commentCounter;
        String      text    = 
            String.format( fmtStr, defText, commentCounter );
        OptionalInt  itemID = OptionalInt.of(commentCounter);    
        int         type    = allTypes[commentCounter % allTypesCount];
        
        Comment comment = new Comment( type, text );
        comment.setIdent( optIdent );
        comment.setItemID( itemID );
        return comment;
    }
}
