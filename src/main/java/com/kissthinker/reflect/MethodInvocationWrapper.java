package com.kissthinker.reflect;

/**
 * @author David Ainslie
 */
public class MethodInvocationWrapper
{
    /** */
    private Object result;

    /** */
    private Throwable throwable;

    /**
     *
     * @return
     */
    public boolean isThrowable()
    {
        return throwable == null ? false : true;
    }

    /**
     *
     * @param result
     */
    public void result(Object result)
    {
        this.result = result;
    }

    /**
     *
     * @return
     */
    public Object result()
    {
        return result;
    }

    /**
     *
     * @param throwable
     */
    public void throwable(Throwable throwable)
    {
        this.throwable = throwable;
    }

    /**
     *
     * @return
     */
    public Throwable throwable()
    {
        return throwable;
    }
}