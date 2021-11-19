package kcls_manager.main;

import kcls_manager.database.DBServer;

/**
 * Arbitrates access to a single instance of a DBServer.
 * 
 * @author jstra
 *
 * @see kcls_manager.database.DBServer
 */
public class DataManager
{
    /** DBServer singleton. */
    private static DBServer dbServer    = null;
    
    /**
     * Gets the singleton representing the DBServer class.
     * If the instance doesn't exist, it will be constructed
     * using the default URL, user name and user password.
     * 
     * @return the singleton representing the DBServer class
     */
    public static synchronized DBServer getDBServer()
    {
        if ( dbServer == null )
            dbServer = new DBServer();
        return dbServer;
    }
    
    /**
     * Gets the singleton representing the DBServer class.
     * If the instance doesn't exist, it will be constructed
     * using the given URL, and the default user name and user password.
     * 
     * @param dbURL the given URL
     * 
     * @return the singleton representing the DBServer class
     */
    public static synchronized DBServer getDBServer( String dbURL )
    {
        if ( dbServer == null )
            dbServer = new DBServer( dbURL );
        return dbServer;
    }
    
    /**
     * Gets the singleton representing the DBServer class.
     * If the instance doesn't exist, it will be constructed
     * using the given URL, user name and user password.
     * 
     * @param dbURL         the given URL
     * @param dbUserName    the given user name
     * @param dbPassword    the given user password
     * 
     * @return the singleton representing the DBServer class
     */
    public static synchronized DBServer 
    getDBServer( String dbURL, String dbUserName, String dbPassword )
    {
        if ( dbServer == null )
            dbServer = new DBServer( dbURL, dbUserName, dbPassword  );
        return dbServer;
    }
    
    /**
     * Shuts down the running database server, if any.
     */
    public static synchronized void closeConnection()
    {
        if ( dbServer != null )
        {
            dbServer.shutdown();
            dbServer = null;
        }
    }
}
