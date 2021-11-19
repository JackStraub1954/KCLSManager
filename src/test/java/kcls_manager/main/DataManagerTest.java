package kcls_manager.main;

import static kcls_manager.database.DBConstants.TEST_DB_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import kcls_manager.database.DBServer;

class DataManagerTest
{
    @AfterEach
    public void afterEach()
    {
        DataManager.closeConnection();
    }
    
    /**
     * Improve test coverage; exercise default constructor.
     */
    @Test
    public void testDataManager()
    {
        new DataManager();
    }
    
    @Test
    void testGetDBServer()
    {
        // TODO how to exercise all of getDBServer()?
        DBServer    dbServerA   = DataManager.getDBServer( TEST_DB_URL );
        assertNotNull( dbServerA );
        DBServer    dbServerB   = DataManager.getDBServer();
        assertEquals( dbServerA, dbServerB );
    }

    @Test
    void testGetDBServerString()
    {
        DBServer    dbServerA   = DataManager.getDBServer( TEST_DB_URL );
        assertNotNull( dbServerA );
        DBServer    dbServerB   = DataManager.getDBServer( TEST_DB_URL );
        assertEquals( dbServerA, dbServerB );
    }

    @Test
    void testGetDBServerStringStringString()
    {
        DBServer    dbServerA   = DataManager.getDBServer( TEST_DB_URL, "", "" );
        assertNotNull( dbServerA );
        DBServer    dbServerB   = DataManager.getDBServer( TEST_DB_URL, "", "" );
        assertEquals( dbServerA, dbServerB );
    }

    @Test
    void testCloseConnection()
    {
        // Test branch where server instance has not yet been created
        DataManager.closeConnection();
        
        DBServer    dbServer = DataManager.getDBServer( TEST_DB_URL );
        assertNotNull( dbServer );
        DataManager.closeConnection();
    }

}
