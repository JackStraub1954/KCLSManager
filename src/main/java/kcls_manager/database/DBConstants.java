package kcls_manager.database;

public class DBConstants
{    
    /* ****************************************
     * Database Table Names
     */
    public static final String  DATABASE_NAME           = "kclsDB";

    /* ****************************************
     * Database Table Names
     */
    public static final String  TITLES_TABLE_NAME       = "TITLES";
    public static final String  AUTHORS_TABLE_NAME      = "AUTHORS";
    public static final String  LISTS_TABLE_NAME        = "LISTS";
    public static final String  COMMENTS_TABLE_NAME     = "COMMENTS";
    public static final String  ATTRIBUTES_TABLE_NAME   = "ATTRIBUTES";
    
    /* ****************************************
     * Database Table Field Names
     */
    /** Title id field (primary key) */
    public static final String  TITLES_ID_FIELD         = "title_id";
    
    /** Author id field (primary key) */
    public static final String  AUTHORS_ID_FIELD        = "author_id";
    
    /**
     *  Reference from COMMENTS row to either AUTHORS or TITLES
     *  table.
     *  
     *  @see #MEDIA_TYPE_FIELD
     */
    public static final String  ITEM_ID_FIELD           = "item_id";
    
    /** Comment id field (primary key) */
    public static final String  COMMENTS_ID_FIELD       = "comment_id";
    
    /** List id field (primary key) */
    public static final String  LISTS_ID_FIELD          = "list_id";
    
    /** creation date */
    public static final String  CREATION_DATE_FIELD     = "creation_date";
    
    /** modification date */
    public static final String  MODIFICATION_DATE_FIELD = "modification_date";
    
    /** title */
    public static final String  TITLE_FIELD             = "title";
    
    /** author */
    public static final String  AUTHOR_FIELD            = "author";
    
    /** type, e.g. "book", "movie" */
    public static final String  MEDIA_TYPE_FIELD        = "type";
    
    /** rank */
    public static final String  RANK_FIELD              = "rank";
    
    /** type, e.g. "book", "movie" */
    public static final String  RATING_FIELD            = "rating";
    
    /** text, e.g. the text of a comment */
    public static final String  TEXT_FIELD              = "text";
    
    /** source, e.g. "library", "amazon" */
    public static final String  SOURCE_FIELD            = "source";
    
    /** 
     * last count; with <em>current count</em>
     * used to detect when the number of titles listed for
     * an author changes
     * 
     * @see #CURRENT_COUNT_FIELD
     */
    public static final String  LAST_COUNT_FIELD        = "last_count";
    
    /** 
     * current count; with <em>last count</em>
     * used to detect when the number of titles listed for
     * an author changes
     * 
     * @see #LAST_COUNT_FIELD
     */
    public static final String  CURRENT_COUNT_FIELD     = "current_count";
    
    /** 
     * Title; used when displaying list of titles or authors. 
     * Example: <em>Wish List Titles</em>.
     */
    public static final String  LIST_TITLE_FIELD        = "dialog_title";
    
    /** 
     * GUI label; used when constructing user interface.
     * For example, a button may be labeled <em>Completed Titles</em>,
     * and poking the button will instantiate a dialog containing
     * a list of completed titles. 
     */
    public static final String  LABEL_FIELD            = "label";
    
    /** 
     * List-headings field; comma separated list of column headings
     * for displaying a list, for example:
     * <blockquote>
     *     "Title,Author,Rank,Rating,Creation Date"
     * </blockquote>
     */
    public static final String  LIST_HEADINGS_FIELD     = "list_headings";
    
    /** 
     * List-type field; distinguishes between types of lists
     * for example <em>list of titles</em> vs. <em>list of authors</em>.
     * 
     * @see #TITLES_TYPE
     * @see #AUTHORS_TYPE
     */
    public static final String  LIST_TYPE_FIELD        = "list_type";
    
    /** Foreign key into titles table */
    public static final String  TITLES_KEY_FIELD        = "titles_key";
    
    /** Foreign key into authors table */
    public static final String  AUTHORS_KEY_FIELD       = "authors_key";
    
    /** Foreign key into lists table */
    public static final String  LISTS_KEY_FIELD         = "lists_key";
    
    /** 
     * reckon date
     * Date used to calculate the estimated ready-date for an
     * on-hold item. Of particular interest is the difference between
     * the "reckon date" (start value for computing time-elapsed)
     * and "check date" (end value for computing time-elapsed).
     * 
     * @see #CHECK_DATE_FIELD
     * @see #CHECK_QPOS_FIELD
     * @see #RECKON_QPOS_FIELD
     */
    public static final String  RECKON_DATE_FIELD       = "reckon_date";
    
    /**
     * check date
     * Date used to calculate the estimated ready-date for an
     * on-hold item. Of particular interest is the difference between
     * the "reckon date" (start value for computing time-elapsed)
     * and "check date" (end value for computing time-elapsed).
     * 
     * @see #RECKON_DATE_FIELD
     * @see #CHECK_QPOS_FIELD
     * @see #RECKON_QPOS_FIELD
     */
    public static final String  CHECK_DATE_FIELD        = "check_date";
    
    /**
     * reckon-queue-position
     * Queue position used to calculate the estimated ready-date for an
     * on-hold item. Of particular interest is the difference between
     * the "reckon-queue-position" (start value for computing progress in
     * queue position) and "check-queue-position" (end value for computing
     * progress in queue position).
     * 
     * @see #CHECK_DATE_FIELD
     * @see #CHECK_DATE_FIELD
     * @see #CHECK_QPOS_FIELD
     */
    public static final String  RECKON_QPOS_FIELD       = "reckon_qpos";
    
    /**
     * check-queue-position
     * Queue position used to calculate the estimated ready-date for an
     * on-hold item. Of particular interest is the difference between
     * the "reckon-queue-position" (start value for computing progress in
     * queue position) and "check-queue-position" (end value for computing
     * progress in queue position).
     * 
     * @see #CHECK_DATE_FIELD
     * @see #CHECK_DATE_FIELD
     * @see #RECKON_QPOS_FIELD
     */
    public static final String  CHECK_QPOS_FIELD        = "check_qpos";
    
    /* ****************************************
     * List types
     */    
    /** 
     * The type of a list of titles
     * 
     * @see #LIST_TYPE
     */
//    public static final int  TITLES_LIST_TYPE           = 0;
    
    /** 
     * The type of a list of authors
     * 
     * @see #LIST_TYPE
     */
//    public static final int  AUTHORS_LIST_TYPE          = 1;
    
    /* ****************************************
     * Database Connection Parameters
     */
    /** Database Path */
    public static final String DB_PATH                  =
        "src/main/resources/" + DATABASE_NAME;
    
    /** Database Path for testing */
    public static final String TEST_DB_PATH             =
        "src/test/resources/" + DATABASE_NAME;
    
    /** Database URL */
    public static final String  DB_URL                  = 
        "jdbc:derby:" + DB_PATH;
    
    /** Database URL for testing */
    public static final String  TEST_DB_URL             = 
        "jdbc:derby:" + TEST_DB_PATH;
    
    /** Database User Name */
    public static final String  DB_USER_NAME            = "kclsUser";
    
    /** Database User Name */
    public static final String  DB_PASSWORD             = "kclsUser";
 
     /**
     * Default constructor; private to prevent instantiation 
     */
    private DBConstants()
    {
        // TODO Auto-generated constructor stub
    }

}
