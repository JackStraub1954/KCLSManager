package app;

import static kcls_manager.database.DBConstants.AUTHORS_ID_FIELD;
import static kcls_manager.database.DBConstants.AUTHORS_TABLE_NAME;
import static kcls_manager.database.DBConstants.AUTHOR_FIELD;
import static kcls_manager.database.DBConstants.CHECK_DATE_FIELD;
import static kcls_manager.database.DBConstants.CHECK_QPOS_FIELD;
import static kcls_manager.database.DBConstants.COMMENTS_ID_FIELD;
import static kcls_manager.database.DBConstants.COMMENTS_TABLE_NAME;
import static kcls_manager.database.DBConstants.CREATION_DATE_FIELD;
import static kcls_manager.database.DBConstants.CURRENT_COUNT_FIELD;
import static kcls_manager.database.DBConstants.DB_URL;
import static kcls_manager.database.DBConstants.ITEM_ID_FIELD;
import static kcls_manager.database.DBConstants.LABEL_FIELD;
import static kcls_manager.database.DBConstants.LAST_COUNT_FIELD;
import static kcls_manager.database.DBConstants.LISTS_ID_FIELD;
import static kcls_manager.database.DBConstants.LISTS_TABLE_NAME;
import static kcls_manager.database.DBConstants.LIST_HEADINGS_FIELD;
import static kcls_manager.database.DBConstants.LIST_TITLE_FIELD;
import static kcls_manager.database.DBConstants.LIST_TYPE_FIELD;
import static kcls_manager.database.DBConstants.MEDIA_TYPE_FIELD;
import static kcls_manager.database.DBConstants.MODIFICATION_DATE_FIELD;
import static kcls_manager.database.DBConstants.RANK_FIELD;
import static kcls_manager.database.DBConstants.RATING_FIELD;
import static kcls_manager.database.DBConstants.RECKON_DATE_FIELD;
import static kcls_manager.database.DBConstants.RECKON_QPOS_FIELD;
import static kcls_manager.database.DBConstants.SOURCE_FIELD;
import static kcls_manager.database.DBConstants.TEST_DB_URL;
import static kcls_manager.database.DBConstants.TEXT_FIELD;
import static kcls_manager.database.DBConstants.TITLES_ID_FIELD;
import static kcls_manager.database.DBConstants.TITLES_TABLE_NAME;
import static kcls_manager.database.DBConstants.TITLE_FIELD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates the KCLS database; 
 * this program needs to be executed <em>only</em> when the database
 * needs to be created or recreated.
 * 
 * @author jstra
 *
 */
public class CreateDatabase
{
    private static final String loggerName  = CreateDatabase.class.getName();
    private static final Logger logger      = Logger.getLogger( loggerName );
    
    /** Create lists table SQL */
    public static final String  createListsTableSQL =
    "CREATE TABLE " + LISTS_TABLE_NAME + " ( "
        + LISTS_ID_FIELD + " int PRIMARY KEY GENERATED ALWAYS AS IDENTITY"
            + "(START WITH 1, INCREMENT BY 1), "
        + LIST_TYPE_FIELD + " int, "
        + LIST_TITLE_FIELD + " varchar( 127 ), "
        + LABEL_FIELD + " varchar( 63 ), "
        + LIST_HEADINGS_FIELD + " varchar(1023), "
        + CREATION_DATE_FIELD + " date, "
        + MODIFICATION_DATE_FIELD + " date "
    + " )";

    /** Create authors table SQL */
    public static final String  createAuthorsTableSQL =
    "CREATE TABLE " + AUTHORS_TABLE_NAME + " ( "
        + AUTHORS_ID_FIELD + " int PRIMARY KEY GENERATED ALWAYS AS IDENTITY"
            + "(START WITH 1, INCREMENT BY 1), "
        + AUTHOR_FIELD + " varchar( 255 ) NOT NULL, "
        + RATING_FIELD + " int, "
        + RANK_FIELD + " int, "
        + SOURCE_FIELD + " varchar( 255 ), "
        + LAST_COUNT_FIELD + " int, "
        + CURRENT_COUNT_FIELD + " int, "
        + LISTS_ID_FIELD + " int, "
        + CREATION_DATE_FIELD + " date, "
        + MODIFICATION_DATE_FIELD + " date, "
        + "FOREIGN KEY(" + LISTS_ID_FIELD + ") REFERENCES " 
            + LISTS_TABLE_NAME + "(" + LISTS_ID_FIELD + ")"
    + " )";

    /** Create titles table SQL */
    public static final String  createTitlesTableSQL =
    "CREATE TABLE " + TITLES_TABLE_NAME + " ( "
        + TITLES_ID_FIELD + " int PRIMARY KEY GENERATED ALWAYS AS IDENTITY"
            + "(START WITH 1, INCREMENT BY 1), "
        + TITLE_FIELD + " varchar( 255 ) NOT NULL, "
        + AUTHORS_ID_FIELD + " int, "
        + LISTS_ID_FIELD + " int, "
        + MEDIA_TYPE_FIELD + " varchar( 127 ), "
        + CHECK_QPOS_FIELD + " int, "
        + RECKON_QPOS_FIELD + " int, "
        + RANK_FIELD + " int, "
        + RATING_FIELD + " int, "
        + SOURCE_FIELD + " varchar( 127 ), "
        + CREATION_DATE_FIELD + " date, "
        + MODIFICATION_DATE_FIELD + " date, "
        + RECKON_DATE_FIELD + " date, "
        + CHECK_DATE_FIELD + " date "
//        + "FOREIGN KEY(" + AUTHORS_ID_FIELD + ") REFERENCES " 
//            + AUTHORS_TABLE_NAME + "(" + AUTHORS_ID_FIELD + "),"
//        + "FOREIGN KEY(" + LISTS_ID_FIELD + ") REFERENCES " 
//            + LISTS_TABLE_NAME + "(" + LISTS_ID_FIELD + ")"
    + " )";

    /** Create comments table SQL */
    public static final String  createCommentsTableSQL =
    "CREATE TABLE " + COMMENTS_TABLE_NAME + " ( "
        + COMMENTS_ID_FIELD + " int PRIMARY KEY GENERATED ALWAYS AS IDENTITY"
            + "(START WITH 1, INCREMENT BY 1), "
        + TEXT_FIELD + " varchar( 1023 ),"
        + ITEM_ID_FIELD + " int, "
        + LIST_TYPE_FIELD + " int "
//        + "FOREIGN KEY(" + AUTHORS_ID_FIELD + ") REFERENCES " 
//            + AUTHORS_TABLE_NAME + "(" + AUTHORS_ID_FIELD + "), "
//        + "FOREIGN KEY(" + TITLES_ID_FIELD + ") REFERENCES " 
//            + TITLES_TABLE_NAME + "(" + TITLES_ID_FIELD + ")"
    + " )";

    /** URL for database creation */
    public final String connectionPoint;
    
    /**
     * Initiate application.
     * 
     * @param args  Command line arguments; not used.
     */
    public static void main(String[] args)
    {
        boolean isTest  = false;
        if ( args.length > 0 )
            isTest = Boolean.parseBoolean( args[0] );
        new CreateDatabase( isTest );
    }

    /**
     * Constructor and workhorse. 
     * Create database, create all tables and shutdown.
     */
    public CreateDatabase( boolean isTest )
    {
        Properties props = new Properties(); // connection properties
        // client authentication is not currently enabled
        // props.put( "user", "user1" );
        // props.put( "password", "user1" );
        if ( isTest )
        {
            logger.info( "commencing test database creation" );
            connectionPoint = TEST_DB_URL + ";create=true";
        }
        else
        {
            logger.info( "commencing database creation" );
            connectionPoint = DB_URL + ";create=true";
        }

        try
        {
            logger.info( connectionPoint );
            Connection  conn    = 
                DriverManager.getConnection( connectionPoint, props );
            logger.info( "connection established" );
            
            // Don't commit changes until everything executes to completion
            conn.setAutoCommit(false);
            Statement   statement   = conn.createStatement();
                        
            statement.execute(createListsTableSQL);
            logger.info("Created table lists");
            statement.execute(createAuthorsTableSQL);
            logger.info("Created table authors");
            statement.execute(createTitlesTableSQL);
            logger.info("Created table titles");
            statement.execute(createCommentsTableSQL);
            logger.info("Created table comments");

            conn.commit();
            logger.info("Changes committed");
            statement.close();
            logger.info("Resources released");
        }
        catch ( SQLException exc )
        {
            logger.log( Level.SEVERE, "SQL error", exc );
        }
        shutdown();
        logger.info( "Application complete" );
    }
    
    /**
     * Shutdown the Derby embedded driver.
     * Implemented as a separate method because shutting down the
     * driver causes an exception to be thrown, and the exception has
     * to be examined to determine if shutdown completed successfully.
     */
    private void shutdown()
    {
        // Surely these constants are declared somewhere in the
        // Derby distribution, but I can't find them.
        // Constant values are taken from SimpleApp.java, which
        // is part of the Derby download.
        final int       derbySystemShutdownErrorCode    = 50000;
        final String    derbySystemShutdownState        = "XJ015";
        try
        {
            logger.info( "starting shutdown" );
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
            logger.info( "shutdown complete without exception" );
        }
        catch ( SQLException exc )
        {
            int     errorCode   = exc.getErrorCode();
            String  state       = exc.getSQLState();
            if ( errorCode == derbySystemShutdownErrorCode
                 && state.equals( derbySystemShutdownState )
               )
            {
                String  message = 
                    "shutdown complete with error code: " + errorCode
                    + " and system state: " + state;
                logger.info( message );
            }
            else
            {
                logger.log( Level.SEVERE, "shutdown incomplete", exc );
            }
        }
    }
}
