package kcls_manager.main;

import static kcls_manager.main.Constants.TITLE_TYPE;

import java.time.LocalDate;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * Encapsulates a list of library items to be represented in a GUI.
 * Properties include:
 * <ul>
 * <li>Type of list (title vs. author)</li>
 * <li>Title to place on dialog window when displaying the list</li>
 * <li>Label to place on GUI component that initiates list dialog</li>
 * <li>Column headings for fields displayed in a table</li>
 * </ul>
 * 
 * @author jstra
 *
 */
public class KCLSList
{
    /**
     * This is the primary key from the database.
     * It will be set if the title has been retrieved from or
     * added to the database. If not set the database server
     * will create a new record for the title.
     * 
     * Named KCLSList to avoid confusion with java.util.list.
     */
    private OptionalInt     ident   = OptionalInt.empty();
    
    private int             listType;
    private String          dialogTitle;
    private String          componentLabel;
    private LocalDate       creationDate;
    private LocalDate       modifiyDate;
    
    public KCLSList( int listType, String dialogTitle )
    {
        this( listType, dialogTitle, dialogTitle );
    }
    
    public KCLSList( int listType, String label, String title )
    {
        setListType( listType );
        setComponentLabel( label );
        setDialogTitle( title );
        setCreationDate( LocalDate.now() );
        setModifyDate( LocalDate.now() );
    }
    
    public OptionalInt getIdent()
    {
        return ident;
    }
    
    public String getDialogTitle()
    {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle)
    {
        this.dialogTitle = dialogTitle;
    }

    public String getComponentLabel()
    {
        return componentLabel;
    }

    public void setComponentLabel(String componentLabel)
    {
        this.componentLabel = componentLabel;
    }

    public void setIdent( int ident )
    {
        this.ident = OptionalInt.of( ident );
    }

    public void setIdent( OptionalInt ident )
    {
        this.ident = ident;
    }

    public int getListType()
    {
        return listType;
    }
    
    public void setListType(int listType)
    {
        this.listType = listType;
    }
    
    public LocalDate getCreationDate()
    {
        return creationDate;
    }
    
    public void setCreationDate(LocalDate creationDate)
    {
        this.creationDate = creationDate;
    }
    
    public LocalDate getModifyDate()
    {
        return modifiyDate;
    }
    
    public void setModifyDate(LocalDate modifyDate)
    {
        this.modifiyDate = modifyDate;
    }
    
    @Override
    public int hashCode()
    {
        int hash    = 
            Objects.hash( ident, listType, dialogTitle,
                          componentLabel, creationDate, modifiyDate );
//            Objects.hash( ident, listType, dialogTitle,
//                componentLabel, creationDate, modificationDate );
        return hash;
    }
    
    @Override
    public boolean equals( Object obj )
    {
        boolean result  = false;
        if ( (obj instanceof KCLSList) )
        {
            KCLSList    that    = (KCLSList)obj;
            if ( !Objects.equals( this.ident, that.ident ) )
                ;
            else if ( !Objects.equals( this.listType, that.listType ) )
                ;
            else if ( !Objects.equals( this.dialogTitle, that.dialogTitle ) )
                ;
            else if ( !Objects.equals(
                this.componentLabel, that.componentLabel ) )
                ;
            else if ( !Objects.equals( 
                this.creationDate, that.creationDate ) )
                ;
            else if ( !Objects.equals( 
                this.modifiyDate, that.modifiyDate ) )
                ;
            else
                result = true;
        }
        
        return result;
    }
    
    @Override
    public String toString()
    {
        StringBuilder   bldr    = new StringBuilder();
        
        bldr.append( "ident=" );
        OptionalInt optIdent    = getIdent();
        if ( optIdent.isPresent() )
            bldr.append( optIdent.getAsInt() );
        else
            bldr.append( "null" );
        
        bldr.append( ",listType=" );
        if ( getListType() == TITLE_TYPE )
            bldr.append( "titles" );
        else
            bldr.append( "authors" );
        
        bldr.append( ",dialogTitle=" ).append( getDialogTitle() );
        bldr.append( ",componentLabel=" ).append( getComponentLabel() );
        bldr.append( ",creationDate=" ).append( getCreationDate() );
        bldr.append( ",modifyDate=" ).append( getModifyDate() );
        
        return bldr.toString();
    }
}







