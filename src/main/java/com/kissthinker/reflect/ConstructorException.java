package com.kissthinker.reflect;

/**
 * @author David Ainslie
 *
 */
public class ConstructorException extends RuntimeException
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public ConstructorException()
    {
    }

    /**
     *
     * @param message
     */
    public ConstructorException(String message)
    {
        super(message);
    }

    /**
     *
     * @param cause
     */
    public ConstructorException(Throwable cause)
    {
        super(cause);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ConstructorException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
