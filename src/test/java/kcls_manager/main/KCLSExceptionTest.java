package kcls_manager.main;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class KCLSExceptionTest
{
    private static final String     message     = "A message";
    private static final Throwable  cause       = new Exception();

    @Test
    void testKCLSException()
    {
        new KCLSException();
    }

    @Test
    void testKCLSExceptionString()
    {
        KCLSException   exc     = new KCLSException( message );
        assertEquals( message, exc.getMessage() );
    }

    @Test
    void testKCLSExceptionThrowable()
    {
        KCLSException   exc     = new KCLSException( cause );
        assertEquals( cause, exc.getCause() );
    }

    @Test
    void testKCLSExceptionStringThrowable()
    {
        KCLSException   exc     = new KCLSException( message, cause );
        assertEquals( message, exc.getMessage() );
        assertEquals( cause, exc.getCause() );
    }
}
