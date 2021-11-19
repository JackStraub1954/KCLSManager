package kcls_manager.main;

import java.time.LocalDate;
import java.util.Objects;

public class Author
    extends LibraryItem 
    implements Comparable<Author>
{
    private String      author          = "";
    private int         lastCount       = 0;
    private int         currCount       = 0;
    
    public Author()
    {
    }
    
    public Author( String author )
    {
        this( LocalDate.now(), author, "" );
    }
    
    public Author( String author, String listName )
    {
        this( LocalDate.now(), author, listName );
        setAuthor( author );
        setListName( listName );
    }
    
    public Author( LocalDate creationDate, String author, String list )
    {
        super( creationDate );
        setAuthor( author );
        setListName( list );
    }
    
    /**
     * Copy constructor.
     * 
     * @param author    the Author object to copy from
     */
    public Author( Author author )
    {
        copyFrom( author );
    }

    /**
     * Copies all properties (except IDENT) from a given Author object
     * to this object.
     * 
     * @param from  the given Author object.
     */
    public void copyFrom( Author from )
    {
        super.copyFrom( from );
        this.setAuthor( from.getAuthor() );
        this.setLastCount( from.getLastCount() );
        this.setCurrentCount( from.getCurrentCount() );
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor( String author )
    {
        this.author = author;
    }
    
    public int getLastCount()
    {
        return lastCount;
    }

    public void setLastCount(int count)
    {
        this.lastCount = count;
    }
    
    public int getCurrentCount()
    {
        return currCount;
    }

    public void setCurrentCount(int count)
    {
        this.currCount = count;
    }

    /**
     * Gets a string representation of this object.
     * 
     * @return a string representation of this object
     */
    @Override
    public String toString()
    {
        StringBuilder   bldr    = new StringBuilder();
        bldr.append( "author=" ).append( getAuthor() ).append( "," )
            .append( "lastCount=" ).append( getLastCount() ).append( "," )
            .append( "currCount=" ).append( getCurrentCount() ).append( "," )
            .append( super.toString() );
        return bldr.toString();
    }
    
    /**
     *  Generate a hash code for this Author.
     *  Overridden here because equals is overridden.
     */
    @Override
    public int hashCode()
    {
        int hash    = Objects.hash(
            super.hashCode(),
            getAuthor(),
            getLastCount(),
            getCurrentCount()
        );
        return hash;      
    }
    
    /**
     * Compare this object to a given object for equality.
     * This object is equal to the given object if
     * <ul>
     *      <li>The given object is non-null</li>
     *      <li>The given object is type Author</li>
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
        if ( obj == null || !(obj instanceof Author) )
            return false;
        if ( this == obj )
            return true;

        Author  that    = (Author)obj;
        boolean rcode   = 
            itemEquals( that ) // check properties of superclass
            && Objects.equals( getAuthor(), that.getAuthor() )
            && Objects.equals( this.getLastCount(), that.getLastCount() )
            && Objects.equals( this.getCurrentCount(), that.getCurrentCount() );

        return rcode;
    }

    @Override
    public int compareTo( Author that )
    {
        int     rcode   = super.compareTo( that );
        if ( rcode != 0 )
            ;
        else if ( (rcode = this.getAuthor()
            .compareTo( that.getAuthor()) ) != 0 )
            ;
        else if ( (rcode = this.getLastCount() - that.getLastCount() ) != 0 )
            ;
        else if ( (rcode = this.getCurrentCount() 
            - that.getCurrentCount() ) != 0 )
            ;
        
        return rcode;
    }

}
