package kcls_manager.main;

import java.util.Objects;
import java.util.OptionalInt;
import static kcls_manager.main.Constants.AUTHOR_TYPE;
import static kcls_manager.main.Constants.TITLE_TYPE;

/**
 * Encapsulates a comment associated with a library item.
 * 
 * @author jstra
 *
 */
/**
 * @author jstra
 *
 */
public class Comment implements Comparable<Comment>
{
//    private static final String loggerName  = Comment.class.getName();
//    private static final Logger logger      = Logger.getLogger( loggerName );
    
    /** Auto-generated key to the comments table */
    private OptionalInt commentsID;
    
    /**  Text of the comment */
    private String      text;
    
    /** 
     * Key into AUTHORS or TITLES table, depending on <em>type</em>.
     * @see #type
     */
    private OptionalInt itemID;
    
    /** 
     * Type of the comment; TITLE_TYPE or AUTHOR_TYPE.
     * @see #itemID
     */
    private int         type;
    
    /**
     * Constructor.
     * 
     * @param type  the type of the comment; 
     *              typically TITLE_TYPE or AUTHOR_TYPE 
     * @param text  text of the comment
     */
    public Comment( int type, String text )
    {
        setType( type );
        setText( text );
        setIdent( OptionalInt.empty() );
        setItemID( OptionalInt.empty() );
    }
    
    /**
     * Copy constructor.
     * 
     * @param toCopy    Comment instance to copy.
     */
    public Comment( Comment toCopy )
    {
        setType( toCopy.getType() );
        setText( toCopy.getText() );
        setIdent( toCopy.getIdent() );
        setItemID( toCopy.getItemID() );
    }

    /**
     * CommentsID getter.
     * @return the commentsID
     */
    public OptionalInt getIdent()
    {
        return commentsID;
    }

    /**
     * CommentsID setter.
     * @param commentsID the commentsID to set
     */
    public void setIdent( OptionalInt ident )
    {
        commentsID = ident;
    }

    /**
     * CommentsID setter.
     * @param commentsID the commentsID to set
     */
    public void setIdent( int ident )
    {
        commentsID = OptionalInt.of( ident );
    }

    /**
     * Text getter.
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * Text setter.
     * @param text the text to set
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * ItemID getter.
     * @return the itemID
     */
    public OptionalInt getItemID()
    {
        return itemID;
    }

    /**
     * ItemID setter.
     * @param itemID to set
     */
    public void setItemID( int itemID )
    {
        this.itemID = OptionalInt.of( itemID );
    }

    /**
     * ItemID setter.
     * @param itemID to set
     */
    public void setItemID( OptionalInt itemID )
    {
        this.itemID = itemID;
    }

    /**
     * Gets the comment type (typically TITLE_TYPE or AUTHOR_TYPE)
     * @return the type
     */
    public int getType()
    {
        return type;
    }

    /**
     * Sets the comment type (typically TITLE_TYPE or AUTHOR_TYPE)
     * @param type the type to set
     */
    public void setType(int type)
    {
        this.type = type;
    }
    
    @Override
    public String toString()
    {
        int             type        = getType();
        String          strType;
        if ( type == TITLE_TYPE )
            strType = "title";
        else if ( type == AUTHOR_TYPE )
            strType = "author";
        else
            strType = "\"unknown\"";
        
        OptionalInt     optIdent    = getIdent();
        Integer         intIdent    =
            optIdent.isEmpty() ? null : optIdent.getAsInt();
        
        StringBuilder   bldr        = new StringBuilder();
        bldr.append( "{" )
            .append( "ident=" ).append( intIdent ).append( "," )
            .append( "text=" ).append( getText() ).append( "," )
            .append( "itemID=" ).append( getItemID() ).append( "," )
            .append( "type=" ).append( strType )
            .append( "}" );
        return bldr.toString();
    }
    
    @Override
    public int hashCode()
    {
        int hash    = Objects.hash(
            getIdent(),
            getText(),
            getItemID(),
            getType()
            );
        return hash;
    }
    
    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null )
            return false;
        if ( this == obj )
            return true;
        if ( !(obj instanceof Comment) )
            return false;
        Comment that    = (Comment)obj;
        boolean rcode   = 
            Objects.equals( this.getIdent(), that.getIdent() )
            && Objects.equals( this.getText(), that.getText() )
            && Objects.equals( this.getItemID(), that.getItemID() )
            && Objects.equals( this.getType(), that.getType() );
        return rcode;
    }

    /**
     * Compares this Comment against a given Comment for magnitude.
     * Specifically:
     * <ul>
     * <li>
     *      if ( this.equals( that ) ) result == 0;
     * </li>
     * <li>
     *      if ( that == null ) result > 0;
     * </li>
     * <li>
     *      otherwise magnitude is determined by comparing,
     *      on the following order:
     *      <ol>
     *      <li>text;</li>
     *      <li>ident;</li>
     *      <li>comments</li>
     *      <li>item ID;</li> and
     *      <li>type</li>
     *      </ol>
     * </li>
     * </ul>
     */
    @Override
    public int compareTo( Comment that )
    {
        int rcode   = 0;
        
        if ( equals( that ) )
            rcode = 0;
        else if ( that == null )
            rcode = 1;
        else if ( (rcode = text.compareTo( that.text )) != 0 )
            ;
        else if ( (rcode = 
            Utils.compareTo( this.commentsID, that.commentsID )) != 0 )
            ;
        else if ( (rcode = Utils.compareTo( this.itemID, that.itemID )) != 0)
            ;
        else
            rcode = this.type - that.type;
        
        return rcode;
    }
}
