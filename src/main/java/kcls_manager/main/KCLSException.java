package kcls_manager.main;

/**
 * Encapsulates an unchecked exception that is thrown
 * when an error occurs during KCLS data processing.
 * All I/O errors in this package are treated as unchecked
 * exceptions.
 * 
 * @author jstra
 *
 */
public class KCLSException extends RuntimeException
{
    /** Generated serial version UID. */
    private static final long serialVersionUID = -6139498683356517295L;

    /**
     * Default constructor.
     */
    public KCLSException()
    {
        super();
    }

    /**
     * Creates and exception with a given message.
     * 
     * @param message   the given message
     */
    public KCLSException(String message)
    {
        super(message);
    }

    /**
     * Creates and exception with a given cause.
     * 
     * @param cause   the given cause
     */
    public KCLSException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Creates an exception with a given message and a given cause.
     * 
     * @param message   The given message.
     * @param cause     The given cause.
     */
    public KCLSException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
