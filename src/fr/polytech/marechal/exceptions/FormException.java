package fr.polytech.marechal.exceptions;

public class FormException extends RuntimeException
{
    private ErrorType errorType = ErrorType.UNDEFINED;
    private String message = "";

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public FormException ()
    {
    }


    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public FormException (ErrorType errorType)
    {
        this.errorType = errorType;
    }

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public FormException (String message)
    {
        this.message = message;
    }

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public FormException (ErrorType errorType, String message)
    {
        this.errorType = errorType;
        this.message = message;
    }


    public ErrorType getErrorType ()
    {
        return errorType;
    }

    public void setErrorType (ErrorType errorType)
    {
        this.errorType = errorType;
    }

    @Override
    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }
}
