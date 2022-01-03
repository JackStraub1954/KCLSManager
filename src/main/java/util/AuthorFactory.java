/**
 * 
 */
package util;

import static kcls_manager.main.Constants.AUTHOR_TYPE;

import java.time.LocalDate;
import java.util.HashSet;

import kcls_manager.main.Author;
import kcls_manager.main.Comment;

/**
 * Facility for generating Author objects for testing purposes.
 * 
 * @author jstra
 *
 */
public class AuthorFactory
{
    private static final int        defRank         = 3;
    private static final int        defRating       = defRank + 2;
    private static final String     defSource       = "default source";
    private static final String     defComment      = "Default comment";
    private static final LocalDate  defCreationDate = 
        LocalDate.now().plusDays( 1 );
    private static final LocalDate  defModifyDate   = 
        defCreationDate.plusDays( 1 );
    private static final String     defName         = "Name, Author";
    private static final String     defListName     = "Default List Name";
    private static final int        defLastCount    = 20;
    private static final int        defCurrentCount = defLastCount + 10;

    /**
     * Prefix for creating unique Titles.
     */
    private int         nextAuthorPrefix     = 0;
    
    /**
     * Prefix for creating unique Comments.
     */
    private int         nextCommentPrefix   = 0;
    
    /**
     * Generate an Author object with a unique value.
     * The generated author is guaranteed do be unique
     * only among those authors generated by this method.
     * 
     * @param   numComments the number of comments to generate
     *          for the target author
     * 
     * @return a unique Author object.
     */
    public Author getUniqueAuthor( int numComments )
    {
        String      prefix      = String.format( "%04d", ++nextAuthorPrefix );
        
        int         rank        = defRank + nextAuthorPrefix;
        int         rating      = defRating + nextAuthorPrefix;
        String      source      = defSource + nextAuthorPrefix;
        String      listName    = prefix + defListName;
        LocalDate   creDate     = defCreationDate.plusDays( nextAuthorPrefix );
        LocalDate   modDate     = defModifyDate.plusDays( nextAuthorPrefix );
        String      name        = prefix + defName;
        int         lastCount   = defLastCount + nextAuthorPrefix;
        int         currCount   = defCurrentCount + nextAuthorPrefix;

        Author      author      = new Author( creDate, name, listName );
        author.setRank( rank );
        author.setRating( rating );
        author.setSource(source);
//        author.setCreationDate( creDate );
        author.setModifyDate( modDate );
//        author.setAuthor( name );
//        author.setList( listName );
        author.setLastCount( lastCount );
        author.setCurrentCount( currCount );
        getComments( numComments, author );

        return author;
    }
    
    public void getComments( int commentCount, Author author )
    {
        author.setComments( new HashSet<Comment>() );
        addComments( commentCount, author );
    }

    /**
     * Add comments to an Author's list of comments.
     * The difference between this and getComments is that
     * the Author's list of comments is not cleared first.
     * 
     * @param commentCount  The number of comments to generate
     * @param author        The owner of the new comments
     */
    public void addComments( int commentCount, Author author )
    {
        for ( int count = 0 ; count < commentCount ; ++count )
        {
            String  nextComment = 
                String.format( "%04d - %s", ++nextCommentPrefix, defComment );
            Comment comment     = new Comment( AUTHOR_TYPE, nextComment );
            author.addComment( comment );
        }
    }
}