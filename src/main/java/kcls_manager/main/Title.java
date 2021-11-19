package kcls_manager.main;

import java.time.LocalDate;
import java.util.Objects;

public class Title extends LibraryItem implements Comparable<Title>
{
    public static final String  DEF_AUTHOR_NAME = "";
    
    private String      title       = "";
    private String      author      = "";
    private String      mediaType   = "unspec";
    private int         reckonQPos  = 0;
    private int         checkQPos   = 0;
    private LocalDate   reckonDate  = LocalDate.now();
    private LocalDate   checkDate   = LocalDate.now();

    public Title()
    {
        this( LocalDate.now(), "", "" );
    }
    
    public Title( Title from )
    {
        super( from );
        copyFrom( from );
    }

    public Title( String title  )
    {
        this( LocalDate.now(), title, "", DEF_AUTHOR_NAME );
    }

    public Title( String title, String listName  )
    {
        this( LocalDate.now(), title, listName, DEF_AUTHOR_NAME );
    }

    public Title( LocalDate creationDate, String title, String author )
    {
        this( creationDate, title, "", author );
    }

    public Title( 
        LocalDate creationDate, 
        String title, 
        String listName, 
        String author 
    )
    {
        super( creationDate );
        setListName( listName );
        setTitle( title );
        setAuthor( author );
    }

    /**
     * Copies all properties (except IDENT) from a given Title object
     * to this object.
     * 
     * @param from  the given Title object.
     */
    public void copyFrom( Title from )
    {
        this.setRank( from.getRank());
        this.setRating( from.getRating() );
        this.setSource( from.getSource() );
        this.setComments( from.getComments() );
        this.setTitle( from.getTitle() );
        this.setAuthor( from.getAuthor() );
        this.setListName( from.getListName() );
        this.setMediaType( from.getMediaType() );
        this.setCheckQPos( from.getCheckQPos() );
        this.setReckonQPos( from.getReckonQPos() );
        this.setCheckDate( from.getCheckDate() );
        this.setReckonDate( from.getReckonDate() );
        this.setCreationDate( from.getCreationDate() );
        this.setModifyDate( from.getModifyDate() );
    }
    
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }
    
    public LocalDate getCheckDate()
    {
        return checkDate;
    }

    public void setCheckDate(LocalDate checkDate)
    {
        this.checkDate = checkDate;
    }

    public LocalDate getReckonDate()
    {
        return reckonDate;
    }

    public void setReckonDate(LocalDate reckonDate)
    {
        this.reckonDate = reckonDate;
    }

    public String getMediaType()
    {
        return mediaType;
    }

    public void setMediaType(String type)
    {
        this.mediaType = type;
    }
    
    /**
     * Gets the hold queue position at the time hold
     * status was last checked.
     * 
     * @return  the hold queue position at the time hold
     *          status was last checked.
     */
    public int getCheckQPos()
    {
        return checkQPos;
    }

    /**
     * Sets the hold queue position at the time hold
     * status was last checked to the given value.
     * 
     * @param qPos
     */
    public void setCheckQPos(int qPos)
    {
        this.checkQPos = qPos;
    }

    /**
     * Gets the hold queue position at the time ready-date
     * estimation was begun.
     * 
     * @return  the hold queue position at the time ready-date
     *          estimation was begun.
     */
    public int getReckonQPos()
    {
        return reckonQPos;
    }

    /**
     * Sets the hold queue position at the time ready-date
     * estimation was begun to the given value.
     * 
     * @param qPos  the given value
     */
    public void setReckonQPos(int qPos)
    {
        reckonQPos = qPos;
    }

    @Override
    public String toString()
    {
        StringBuilder   bldr    = new StringBuilder();
        bldr.append( "title=").append( getTitle() ).append( "," )
            .append( "author=" ).append( getAuthor() ).append( ",")
            .append( "mediaType=" ).append( getMediaType() ).append( ",")
            .append( "checkQPos=" ).append( getCheckQPos() ).append( ",")
            .append( "reckonQPos=" ).append( getReckonQPos() ).append( ",")
            .append( "checkDate=" ).append( getCheckDate() ).append( ",")
            .append( "reckonDate=" ).append( getReckonDate() ).append( ",") 
            .append( super.toString() );
        return bldr.toString();
    }
    
    /**
     *  Generate a hash code for this Title.
     *  Overridden here because equals is overridden.
     */
    @Override
    public int hashCode()
    {
        int hash    = Objects.hash(
            super.hashCode(),
            getTitle(),
            getAuthor(),
            getListName(),
            getMediaType(),
            getCheckQPos(),
            getReckonQPos(),
            getCheckDate(),
            getReckonDate()
        );
        return hash;      
    }

    /**
     * Compare this object to a given object for equality.
     * This object is equal to the given object if
     * <ul>
     *      <li>The given object is non-null</li>
     *      <li>The given object is type Title</li>
     *      <li>
     *          Every field of this object is equal to
     *          the corresponding field in the given object.
     *      </li>
     * </ul>
     * 
     * @param obj   The given object
     * 
     * @return  true, if this object is equal to the given object
     */
    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null || !(obj instanceof Title) )
            return false;
        if ( this == obj )
            return true;

        Title   that    = (Title)obj;
        boolean rcode   = 
            itemEquals( that ) // check properties of superclass
        && Objects.equals( this.getTitle(), that.getTitle() )
        && Objects.equals( getAuthor(), that.getAuthor() )
        && Objects.equals( this.getMediaType(), that.getMediaType() )
        && Objects.equals( this.getCheckQPos(), that.getCheckQPos() )
        && Objects.equals( this.getReckonQPos(), that.getReckonQPos() )
        && Objects.equals( this.getCheckDate(), that.getCheckDate() )
        && Objects.equals( this.getReckonDate(), that.getReckonDate() );

        return rcode;
    }

    @Override
    public int compareTo(Title that)
    {
        int     rcode   = super.compareTo( that );
        if ( rcode != 0 )
            ;
        else if ( (rcode = this.getTitle()
            .compareTo( that.getTitle()) ) != 0 )
            ;
        else if ( (rcode = this.getAuthor()
            .compareTo( that.getAuthor()) ) != 0 )
            ;
        else if ( (rcode = this.getMediaType()
            .compareTo( that.getMediaType() )) != 0)
            ;
        else if ( (rcode = this.getReckonQPos() 
            - that.getReckonQPos() ) != 0 )
            ;
        else if ( (rcode = this.getCheckQPos() 
            - that.getCheckQPos() ) != 0 )
            ;
        else if ( (rcode = this.getReckonDate()
            .compareTo( that.getReckonDate() )) != 0)
            ;
        else if ( (rcode = this.getCheckDate()
            .compareTo( that.getCheckDate() )) != 0)
            ;
        
        return rcode;
    }
}
