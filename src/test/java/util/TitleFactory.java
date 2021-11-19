package util;

import static kcls_manager.main.Constants.TITLE_TYPE;

import java.time.LocalDate;

import kcls_manager.main.Comment;
import kcls_manager.main.Title;

public class TitleFactory
{
    private static final int        defRank         = 3;
    private static final int        defRating       = defRank + 2;
    private static final String     defSource       = "default source";
    private static final String     defComment      = "Default comment";
    private static final LocalDate  defCreationDate = 
        LocalDate.now().plusDays( 1 );
    private static final LocalDate  defModifyDate   = 
        defCreationDate.plusDays( 1 );
    private static final String     defText        = "Default Title";
    private static final String     defAuthor      = "Name, Author Anne";
    private static final String     defListName    = "Default List Name";
    private static final String     defMediaType   = "unspec";
    private static final int        defCheckQPos   = 0;
    private static final int        defReckonQPos  = 0;
    private static final LocalDate  defCheckDate   = LocalDate.now();
    private static final LocalDate  defReckonDate  = LocalDate.now();

    /**
     * Prefix for creating unique Titles.
     */
    private int         nextTitlePrefix     = 0;
    
    /**
     * Prefix for creating unique Comments.
     */
    private int         nextCommentPrefix   = 0;

    public Title getUniqueTitle( int numComments )
    {
        String      prefix      = String.format( "%04d", ++nextTitlePrefix );
        
        String      name        = prefix + defText;
        String      author      = prefix + defAuthor;
        String      listName    = prefix + defListName;
        String      mediaType   = defMediaType;
        int         checkQPos   = defCheckQPos + nextTitlePrefix;
        int         reckonQPos  = defReckonQPos + nextTitlePrefix;
        int         rank        = defRank + nextTitlePrefix;
        int         rating      = defRating + nextTitlePrefix;
        String      source      = defSource + nextTitlePrefix;
        LocalDate   checkDate   = defCheckDate.plusWeeks( nextTitlePrefix );
        LocalDate   reckonDate  = defReckonDate.plusWeeks( nextTitlePrefix );
        LocalDate   creDate     = defCreationDate.plusDays( nextTitlePrefix );
        LocalDate   modDate     = defModifyDate.plusDays( nextTitlePrefix );

        Title       title       = 
            new Title( creDate, name, listName, author );
        // title.setTitle
        // title.setAuthor
        // title.setListName
        title.setMediaType( mediaType );
        title.setCheckQPos( checkQPos );
        title.setReckonQPos( reckonQPos );
        title.setRank( rank );
        title.setRating( rating );
        title.setSource(source);
        title.setCheckDate(checkDate);
        title.setReckonDate(reckonDate);
        // title.setCreationDate();
        title.setModifyDate( modDate );
        addComments( numComments, title );

        return title;
    }

    /**
     * Add comments to a Title's list of comments.
     * The difference between this and getComments is that
     * the Title's list of comments is not cleared first.
     * 
     * @param commentCount  The number of comments to generate
     * @param title         The owner of the new comments
     */
    public void addComments( int commentCount, Title title )
    {
        for ( int count = 0 ; count < commentCount ; ++count )
        {
            String  nextComment = 
                String.format( "%04d - %s", ++nextCommentPrefix, defComment );
            Comment comment     = new Comment( TITLE_TYPE, nextComment );
            title.addComment( comment );
        }
    }
}
