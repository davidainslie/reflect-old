package com.kissthinker.reflect;

/**
 * @author David Ainslie
 *
 */
public class MethodException extends RuntimeException
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public MethodException()
    {
    }

    /**
     *
     * @param message
     */
    public MethodException(String message)
    {
        super(message);
    }

    /**
     *
     * @param cause
     */
    public MethodException(Throwable cause)
    {
        super(cause);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public MethodException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
