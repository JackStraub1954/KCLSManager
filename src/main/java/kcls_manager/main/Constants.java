package kcls_manager.main;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Constants
{
    private Constants()
    {
    }
        /* ****************************************
         * TASK STATUS
         */
    /** OK button was selected */
    public static final int     OKAY            = 0;

    /** Cancel button was selected */
    public static final int     CANCEL          = 1;

    /** 'Save' button was selected */
    public static final int     SAVE            = 2;

    /** 'Apply' button was selected */
    public static final int     APPLY           = 3;

        /* ****************************************
         * COMPONENT TEXT
         */    
    /** OK Button text */
    public static final String  OK_TEXT         = "OK";
    
    /** Cancel Button text */
    public static final String  CANCEL_TEXT     = "Cancel";
    
    /** Save Button text */
    public static final String  SAVE_TEXT       = "Save";
    
    /** Apply Button text */
    public static final String  APPLY_TEXT      = "Apply";
    
    /** Delete Button text */
    public static final String  DELETE_TEXT     = "Delete";
    
    /** Insert Button text */
    public static final String  INSERT_TEXT     = "Insert";
    
    /** Discard button text */
    public static final String  DISCARD_TEXT    = "Discard";
    
        /* ****************************************
         * ELEMENT TAG NAMES
         */    
    /** The root element name */
    public static final String  DOC_ROOT        = "librarian";
    
    /** The name of the tag for the Netflix section */
    public static final String  NETFLIX         = "netflix";
    
    /** The name of the tag for a titles section */
    public static final String  TITLES          = "titles";
    
    /** The name of the tag for a title element. */
    public static final String  TITLE           = "title";
    
    /** The name of the tag for the wishList section */
    public static final String  WISH_LIST       = "wishList";
    
    /** The name of the tag for the completed section */
    public static final String  COMPLETED       = "completed";
    
    /** The name of the tag for the later section */
    public static final String  LATER           = "later";

    /** The name of the tag for an authors section */
    public static final String  ON_HOLD         = "onHold";

    /** The name of the tag for an authors section */
    public static final String  CHECKED_OUT     = "checkedOut";

    /** The name of the tag for an authors section...
     *  this tag can appear as a branch, e.g. under wishList,
     *  or as an element nested in a title element.
     */
    public static final String  AUTHORS         = "authors";
    
    /** The name of the tag for an author element. */
    public static final String  AUTHOR          = "author";
    
    /** The name of the tag for a comments section */
    public static final String  COMMENTS        = "comments";
    
    /** The name of the tag for a comment element. */
    public static final String  COMMENT         = "comment";
    
    /** The name of the tag for a title text element
     * (the element that contains the "title" of the title). 
     */
    public static final String  TEXT            = "text";
    
    /** The name of the tag for an author name element. */
    public static final String  NAME            = "name";

    /* ****************************************
     * ATTRIBUTE NAMES
     */
    /** The name of the count attribute */
    public static final String  COUNT           = "count";
    
    /** The name of the rank attribute */
    public static final String  RANK            = "rank";
    
    /** The name of the rating attribute */
    public static final String  RATING          = "rating";
    
    /** The name of the date-created attribute */
    public static final String  CREATION_DATE   = "creationDate";
    
    /** The name of the date-modified attribute */
    public static final String  MODIFIED_DATE   = "modifiedDate";
    
    /** The name of the date-placed attribute;
      *  used to calculate ready date for on-hold titles
      */
    public static final String  PLACED_DATE     = "placedDate";
    
    /** The name of the date-reckoned attribute;
     * used to calculate ready date for on-hold titles
     */
    public static final String  RECKON_DATE     = "reckonDate";
    
    /** The name of the date-checked attribute;
     * used to calculate ready date for on-hold titles
     */
    public static final String  CHECK_DATE      = "checkDate";
    
    /** The name of the date-ready attribute;
     *  the estimated date that an on-hold title
     *  will become available.
     */
    public static final String  READY_DATE      = "readyDate";
    
    /** The name of the status attribute */
    public static final String  STATUS          = "status";
    
    /** The name of the place-queue-position attribute */
    public static final String  PLACE_QPOS      = "placeQPos";
    
    /** The name of the reckon-queue-position attribute */
    public static final String  RECKON_QPOS    = "reckonQPos";
    
    /** The name of the check-queue-position attribute */
    public static final String  CHECK_QPOS     = "checkQPos";
    
    /** The name of the media-type attribute */
    public static final String  MEDIA_TYPE     = "mediaType";
    
    /** The name of the source attribute */
    public static final String  SOURCE         = "source";
    
    /** the type of a list containing authors */
    public static final int     AUTHOR_TYPE     = 0;
    
    /** the type of a list containing titles */
    public static final int     TITLE_TYPE      = 1;

    /* ****************************************
     * ATTRIBUTE VALUES
     */
    /** The TV media-type */
    public static final String  TV          = "tv";
    
    /** The movie media-type */
    public static final String  MOVIE       = "movie";
    
    /** The book media-type */
    public static final String  BOOK        = "book";

    /* ****************************************
     * COMPONENT NAMES FOR GUI ELEMENTS
     */
    /** The title text field */
    public static final String  TITLE_FIELD     = "title";
    
    /** The name of the author field */
    public static final String  AUTHOR_FIELD    = "author";
    
    /** The name of the rating field */
    public static final String  RATING_FIELD    = "rating";
    
    /** The name of the rank field */
    public static final String  RANK_FIELD      = "rank";
    
    /** The name of the attributes field */
    public static final String  ATTR_FIELD      = "attributes";
    
    /** The name of the comments field */
    public static final String  COMMENTS_FIELD  = "comments";
    
    /** The name of the date-created field */
    public static final String  CREATED_FIELD   = "date_created";
    
    /** The name of the date-modified field */
    public static final String  MODIFIED_FIELD  = "date_modified";
    
    /** The name of the date-checked field */
    public static final String  CHECKED_FIELD   = "date_checked";
    
    /** The name of the date-reckoned-from field */
    public static final String  RECKONED_FIELD  = "date_reckoned";
    
    /** The name of the check-queue-position field */
    public static final String  CHECK_QPOS_FIELD    = "qpos-check";
    
    /** The name of the reckon-queue-position field */
    public static final String  RECKON_QPOS_FIELD   = "qpos-reckon";
    
    /** The name of the source field */
    public static final String  SOURCE_FIELD    = "source";
    
    /* ****************************************
     * COLUMN HEADINGS
     * When a column in a JTable corresponds to one of these strings,
     * that column will be used as a supplier for the given field.
     * This is how data in a table is translated into LibraryItem
     * properties, so it is important that column headers are chosen from
     * below.
     */
    /** Title column heading */
    public static final String  TITLE_NAME          = "Title";
    
    /** Author column heading */
    public static final String  AUTHOR_NAME         = "Author";
    
    /** Author column heading */
    public static final String  LIST_NAME_NAME      = "List Name";
    
    /** Rank column heading */
    public static final String  RANK_NAME           = "Rank";
    
    /** Rank column heading */
    public static final String  RATING_NAME         = "Rating";
    
    /** Source column heading */
    public static final String  SOURCE_NAME         = "Source";
    
    /** Creation date column heading */
    public static final String  CREATION_DATE_NAME  = "Creation Date";
    
    /** Last-modified date column heading */
    public static final String  MODIFY_DATE_NAME    = "Last Modified";
    
    /** Check date column heading */
    public static final String  CHECK_DATE_NAME     = "Check Date";
    
    /** Reckon date column heading 
     *  This is the date from which an estimated 
     *  hold-ready-date is calculated. Since an item
     *  is often put on hold before it is ready for
     *  distribution, this is not necessarily the same
     *  as the creation date.
     */
    public static final String  RECKON_DATE_NAME    = "Reckon Date";
    
    /** Estimated ready date column heading.
     *  This heading is included here for consistency, but
     *  the ready-date property is calculated and cannot
     *  be edited
     */
    public static final String  READY_DATE_NAME     = "Ready Date";
    
    /** Comments column heading */
    public static final String  COMMENTS_NAME       = "Comments";
    
    /** Attributes column heading */
    public static final String  ATTRIBUTES_NAME     = "Attributes";
    
    /** Media-type column heading */
    public static final String  MEDIA_TYPE_NAME     = "Media Type";
    
    /** Queue position when a hold was checked */
    public static final String  CHECK_QPOS_NAME     = "Check QPos";
    
    /** Reckon position of an item on hold.
     *  This is the position from which an estimated 
     *  hold-ready-date is calculated. Since an item
     *  is often put on hold before it is ready for
     *  distribution, this is not necessarily the same
     *  as the position it was originally placed in.
     */
    public static final String  RECKON_QPOS_NAME    = "Reckon QPos";
    
    /** Current estimated count of titles associated with an author.
     *  When compared to <em>current count<em> suggests that this number
     *  has changed, possibly meaning the author has new titles to
     *  examine.
     *  @see #CURRENT_COUNT_NAME
     */
    public static final String  LAST_COUNT_NAME     = "Last Count";
    
    /** Estimated count of titles previously associated with an author.
     *  When compared to <em>last count<em> suggests that this number
     *  has changed, possibly meaning the author has new titles to
     *  examine.
     *  @see #LAST_COUNT_NAME
     */
    public static final String  CURRENT_COUNT_NAME  = "Current Count";

    /* ****************************************
     * Title-Provider Identifiers
     */
    public static final int LATER_TITLE_PROVIDER        = 0;
    public static final int COMPLETED_TITLE_PROVIDER    = 1;
    public static final int ON_HOLD_TITLE_PROVIDER      = 2;
    public static final int NETFLIX_TITLE_PROVIDER      = 3;
    public static final int CHECKED_OUT_TITLE_PROVIDER  = 4;
    public static final int WISH_LIST_TITLE_PROVIDER    = 5;

    /* ****************************************
     * MISCELLANEOUS
     */
    /** Line separator */
    public static final String  NEWL           = System.lineSeparator();
    
    /** Formatter for dates */
    public static final DateTimeFormatter  dtFormatter = 
        DateTimeFormatter.ofPattern( "MM/dd/yyyy" );
    
    /** The ID of the local time zone */
    public static final ZoneId  ZONE_ID    = ZoneId.systemDefault();
}
