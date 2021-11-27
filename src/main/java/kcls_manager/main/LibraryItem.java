package kcls_manager.main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

public abstract class LibraryItem
{
    /**
     * This is the primary key from the database.
     * It will be set if the item has been retrieved from or
     * added to the database. If not set the database server
     * will create a new record for the title.
     */
    private OptionalInt         ident           = OptionalInt.empty();
    
    private int                 rank            = 0;
    private int                 rating          = 0;
    private String              source          = "";
    private String              listName        = "";
    private List<Comment>       comments        = new ArrayList<>();
    private LocalDate           creationDate    = LocalDate.now();
    private LocalDate           modifyDate      = LocalDate.now();
    
    public LibraryItem()
    {
    }
    
    /**
     * Copy constructor.
     * 
     * @param from  object to copy
     */
    public LibraryItem( LibraryItem from )
    {
        copyFrom( from );
    }
    
    public LibraryItem( LocalDate creDate )
    {
        setCreationDate( creDate );
        setModifyDate( creDate );
    }
    
    /**
     * Getter for the primary database key of this item.
     * May be empty.
     * 
     * @return  the primary database key of this item
     */
    public OptionalInt getIdent()
    {
        return ident;
    }
    
    /**
     * Setter for the primary database key of this item.
     * 
     * @param    the primary database key for this item
     */
    public void setIdent( int ident )
    {
        this.ident = OptionalInt.of( ident );
    }
    
    /**
     * Setter for the primary database key of this item
     * 
     * @param   ident   the primary database key for this item;
     *                  may be empty
     */
    public void setIdent( OptionalInt ident )
    {
        this.ident = ident;
    }

    public LocalDate getCreationDate()
    {
        return creationDate;
    }
    
    public void copyFrom( LibraryItem from )
    {
        setIdent( from.getIdent() );
        setRank( from.getRank() );
        setRating( from.getRating() );
        setSource( from.getSource() );
        setListName( from.getListName() );
        setComments( from.getComments() );
        setCreationDate( from.getCreationDate() );
        setModifyDate( from.getModifyDate() );
    }

    public void setCreationDate(LocalDate creationDate)
    {
        this.creationDate = creationDate;
    }
    
    /**
     * Gets the name of the list that this item belongs to.
     * 
     * @return the listName
     */
    public String getListName()
    {
        return listName;
    }

    /**
     * Sets the name of the list that this item belongs to.
     * 
     * @param listName the listName to set
     */
    public void setListName(String listName)
    {
        this.listName = listName;
    }

    /**
     * Gets an unmodifiable list of all comments.
     * 
     * @return Unmodifiable list of all comments
     */
    public List<Comment> getComments()
    {
        List<Comment>   list   = new ArrayList<>( comments );
        return list;
    }
    
    /**
     * Clears the collection of comments.
     */
    public void clearComments()
    {
        comments.clear();
    }
    
    /**
     * Gets the number of comments associated with this item.
     * 
     * @return The number of comments associated with this item
     */
    public int getNumComments()
    {
        return comments.size();
    }
    
    /**
     * Adds a comment to the list of comments associated
     * with this item.
     * 
     * @param comment   The comment to add
     */
    public void addComment( Comment comment )
    {
        comments.add( comment );
    }
    
    /**
     * Removes a comment to the list of comments associated
     * with this item.
     * 
     * @param comment   The comment to remove
     * 
     * @return true if the operation succeeds
     */
    public boolean removeComment( Comment comment )
    {
        boolean rval    = comments.remove( comment );
        return rval;
    }
    
    /**
     * Replaces the current comments with a new set of comments.
     * Modification to the input list will not change the value of the
     * comments maintained for this item.
     * 
     * @param comments  The given set of comments
     * 
     * @throws NullPointerException if argument is null
     */
    public void setComments( Collection<Comment> comments )
        throws NullPointerException
    {
        this.comments.clear();
        this.comments.addAll( comments );
    }
    
    /**
     * Get the rank for this item.
     * 
     * @return  The rank for this item
     */
    public int getRank()
    {
        return rank;
    }
    
    /**
     * Set the rank attribute for this item.
     */
    public void setRank( int rank )
    {
        this.rank = rank;
    }
    
    /**
     * Get the rating for this item.
     * 
     * @return  The rating for this item
     */
    public int getRating()
    {
        return rating;
    }
    
    /**
     * Set the rating attribute for this item.
     */
    public void setRating( int rating )
    {
        this.rating = rating;
    }
    
    /**
     * Gets the date of last modification.
     * 
     * @return  The date of last modification
     */
    public LocalDate getModifyDate()
    {
        return modifyDate;
    }
    
    /**
     * Sets the date of last modification.
     * 
     * @param date  The date of last modification
     */
    public void setModifyDate( LocalDate date )
    {
        modifyDate = date;
    }
    
    /**
     * Gets the library item source.
     * For example, netflix, library, amazon, etc.

     * @return  The library item source
     */
    public String getSource()
    {
        return source;
    }
    
    /**
     * Sets the library item source.
     * For example, netflix, library, amazon, etc.
     * 
     * @param source    The library item source
     */
    public void setSource( String source )
    {
        this.source = source;
    }
    
    /**
     * Determine whether the LibraryItem attributes of this item
     * are equal to those of a given item.
     * Mainly used by subclasses.
     * 
     * @param that  the given item
     * 
     * @return  true if the LibraryItem attributes of this item
     *          are equal to those of the given item
     */
    public boolean itemEquals( LibraryItem that )
    {
        if ( that == null )
            return false;
        if ( this == that )
            return true;
        
        boolean rcode   = 
            Objects.equals( this.getIdent(), that.getIdent() )
            && Objects.equals( this.getRank(), that.getRank() )
            && Objects.equals( this.getRating(), that.getRating() )
            && Objects.equals( this.getSource(), that.getSource() )
            && Objects.equals( this.getListName(), that.getListName() )
            && Utils.equals( this.comments, that.comments )
            && Objects.equals( this.getCreationDate(), that.getCreationDate() )
            && Objects.equals( this.getModifyDate(), that.getModifyDate() );
        return rcode;
    }

    @Override
    public int hashCode()
    {
        int hash    = 
            Objects.hash(
                getIdent(),
                getRank(),
                getRating(),
                getSource(),
                getListName(),
                getCreationDate(),
                getModifyDate()
            );
        return hash;
    }
    
    /**
     * Compares two LibraryItems for magnitude.
     * Here for support of subclass implementations
     * of Comparable.
     * 
     * @param item1 the first LibraryItem to compare
     * @param that the second LibraryItem to compare
     * 
     * @return 
     *      negative number if item1 < item2; 
     *      0 if item1 == item2;
     *      positive number if item1 > item2
     */
    public int compareTo( LibraryItem that )
    {
        if ( this == that )
            return 0;
        if ( that == null )
            return 1;
        
        int         rval    = 0;
        OptionalInt ident1  = this.getIdent();
        OptionalInt ident2  = that.getIdent();
        if ( (rval = Utils.compareTo( ident1, ident2 )) != 0 )
            return rval;
        
        if ( (rval = this.getRank() - that.getRank()) != 0)
            return rval;
        
        if ( (rval = this.getRating() - that.getRating()) != 0)
            return rval;
        
        String  source1 = this.getSource();
        String  source2 = that.getSource();
        if ( (rval = source1.compareTo( source2 )) != 0 )
            return rval;
        
        String  listName1   = this.getListName();
        String  listName2   = that.getListName();
        if ( (rval = listName1.compareTo( listName2 )) != 0 )
            return rval;
        
        List<Comment>   comments1   = this.getComments();
        List<Comment>   comments2   = that.getComments();
        if ( (rval = Utils.compareTo( comments1, comments2 )) != 0 )
            return rval;
        
        LocalDate       creDate1    = this.getCreationDate();
        LocalDate       creDate2    = that.getCreationDate();
        if ( (rval = creDate1.compareTo( creDate2 )) != 0 )
            return rval;
        
        LocalDate       modDate1    = this.getModifyDate();
        LocalDate       modDate2    = that.getModifyDate();
        if ( (rval = modDate1.compareTo( modDate2 )) != 0 )
            return rval;
        
        return 0;
    }
    
    /**
     * Returns a string representation of this LibraryItem.
     */
    @Override
    public String toString()
    {
        StringBuilder   bldr        = new StringBuilder();
        String          strIdent    = "null";
        if ( ident.isPresent() )
            strIdent = "" + ident.getAsInt();
        bldr.append( "ident=" ).append( strIdent ).append( "," )
        .append( "rank=" ).append( rank ).append( "," )
        .append( "rating=" ).append( rating ).append( "," )
        .append( "source=" ).append( source ).append( "," )
        .append( "listName=" ).append( listName ).append( "," )
        .append( "creDate=" ).append( creationDate ).append( "," )
        .append( "modDate=" ).append( modifyDate ).append( "," )
        .append( "comments=" ).append( comments.toString() );
        return bldr.toString();
    }
}
