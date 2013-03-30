package com.kissthinker.reflect;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author David Ainslie
 *
 */
public class MethodWrapper
{
    /** */
    private final Class<?> returnType;

    /** */
    private final String name;

    /** */
    private final Class<?>[] paramaterTypes;

    /** */
    private final Class<?>[] exceptionTypes;

    /**
     *
     * @param method
     */
    public MethodWrapper(Method method)
    {
        this(method.getReturnType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes());
    }

    /**
     *
     * @param returnType
     * @param name
     * @param parameterTypes
     * @param exceptionTypes
     */
    public MethodWrapper(Class<?> returnType, String name, Class<?>[] parameterTypes, Class<?>[] exceptionTypes)
    {
        this.returnType = returnType;
        this.name = name;
        this.paramaterTypes = parameterTypes;
        this.exceptionTypes = exceptionTypes;
    }

    /**
     *
     * @return
     */
    public Class<?> returnType()
    {
        return returnType;
    }

    /**
     *
     * @return
     */
    public String name()
    {
        return name;
    }

    /**
     *
     * @return
     */
    public Class<?>[] parameterTypes()
    {
        return paramaterTypes.clone();
    }

    /**
     *
     * @return
     */
    public Class<?>[] exceptionTypes()
    {
        return exceptionTypes.clone();
    }

    /**
     *
     */
    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof MethodWrapper))
        {
            return false;
        }

        MethodWrapper methodWrapper = (MethodWrapper)other;

        return name.equals(methodWrapper.name) &&
               returnType.equals(methodWrapper.returnType) &&
               Arrays.equals(paramaterTypes, methodWrapper.paramaterTypes);
    }

    /**
     *
     */
    @Override
    public int hashCode()
    {
        return name.hashCode() + returnType.hashCode() + Arrays.hashCode(paramaterTypes);
    }

    /**
     *
     */
    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(returnType.getName()).append(" ").append(name).append("(");
        String comma = "";

        for (Class<?> paramType : paramaterTypes)
        {
            stringBuilder.append(comma).append(paramType.getName());
            comma = ", ";
        }

        stringBuilder.append(")");

        return stringBuilder.toString();
    }
}