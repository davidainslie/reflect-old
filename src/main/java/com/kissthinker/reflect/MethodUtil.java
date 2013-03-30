package com.kissthinker.reflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Ainslie
 * Utilities for manipulating methods
 *
 */
public abstract class MethodUtil
{
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodUtil.class);

    /** */
    private enum DataAccessor {get, is, can, exists, values, length}
    
    /** */
    private enum DataMutator {set}

    /**
     * Invoke the best matching static method e.g. the best match of overloaded methods.
     * This is somewhat like "pattern matching" in functional programming.
     * If given "likeStaticMethodName" ends with "()", so any given parameters will be ignored.
     * This is useful if method overloading includes parameterless and parameters versions.
     * @param <O>
     * @param class_
     * @param likeStaticMethodName (case insensitive).
     * @param parameters
     * @return
     * @throws MethodException
     */
    @SuppressWarnings("unchecked")
	public static <O> O invoke(Class<?> class_, String likeStaticMethodName, Object... parameters) throws MethodException
    {
        Method method = acquireMethod(class_, likeStaticMethodName, parameters);

        try
        {
            return (O)method.invoke(null, parameters);
        }
        catch (Exception e)
        {
        	LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw new MethodException(e);
        }
    }

    /**
     * Invoke the best matching method e.g. the best match of overloaded methods.
     * This is somewhat like "pattern matching" in functional programming.
     * If given "likeMethodName" ends with "()", so any given parameters will be ignored.
     * This is useful if method overloading includes parameterless and parameters versions.
     * @param <O>
     * @param object
     * @param likeMethodName (case insensitive).
     * @param parameters
     * @return
     * @throws MethodException
     */
    @SuppressWarnings("unchecked")
	public static <O> O invoke(Object object, String likeMethodName, Object... parameters) throws MethodException
    {
    	if (object == null)
    	{
    		return null;
    	}

        Method method = acquireMethod(object, likeMethodName, parameters);

        try
        {
            return (O)method.invoke(object, parameters);
        }
        catch (Exception e)
        {
        	LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw new MethodException(e);
        }
    }

    /**
     * @see #invoke(java.lang.Object, java.lang.String, java.lang.Object...)
     * @param <O>
     * @param methodExceptionHandler
     * @param object
     * @param likeMethodName
     * @param parameters
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <O> O invoke(MethodExceptionHandler methodExceptionHandler, Object object, String likeMethodName, Object... parameters)
    {
        try
        {
            return (O)invoke(object, likeMethodName, parameters); // Unnecessary cast but required to avoid compilation error by javac.
        }
        catch (Exception e)
        {
        	LOGGER.error("MethodExceptionHandler to handle given error: " + e.getMessage());
            methodExceptionHandler.handle(e);
            return null;
        }
    }

    /**
     *
     * @param object
     * @param likePropertyName
     * @param parameter
     * @throws MethodException
     */
    public static void invokeSetter(Object object, String likePropertyName, Object parameter) throws MethodException
    {
        Method setterMethod = acquireSetterMethod(object, likePropertyName);

        try
        {
            setterMethod.invoke(object, parameter);
        }
        catch (Exception e)
        {
        	LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw new MethodException(e);
        }
    }

    /**
     *
     * @param object
     * @param parameterClass
     * @param parameter
     * @return
     * @throws MethodException
     */
    public static void invokeSetter(Object object, Class<?> parameterClass, Object parameter) throws MethodException
    {
        for (Method method : object.getClass().getMethods())
        {
            if (method.getName().startsWith(DataMutator.set.name()))
            {
                if (method.getParameterTypes().length == 1)
                {
                    if (method.getParameterTypes()[0].getName().equals(parameterClass.getName()))
                    {
                        // TODO Check return type is void.
                        try
                        {
                            method.setAccessible(true);
                            method.invoke(object, parameter);
                        }
                        catch (Exception e)
                        {
                        	LOGGER.error(ExceptionUtils.getStackTrace(e));
                            throw new MethodException(e);
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param <O>
     * @param object
     * @param likePropertyName
     * @return
     * @throws MethodException
     */
    @SuppressWarnings("unchecked")
	public static <O> O invokeGetter(Object object, String likePropertyName) throws MethodException
    {
        try
        {
            Method getterMethod = acquireGetterMethod(object, likePropertyName);

            if (getterMethod == null)
            {
                throw new MethodException(String.format("No '%s' getter method found for object %s", likePropertyName, object));
            }
            else
            {
                return (O)getterMethod.invoke(object);
            }
        }
        catch (Exception e)
        {
        	LOGGER.error(e.getMessage());
            throw new MethodException(e);
        }
    }

    /**
     *
     * @param object
     * @param likePropertyName (like meaning that case sensitivity is ignored - could provide a property "text" or "TEXT").
     * @return
     */
    public static Method acquireSetterMethod(Object object, String likePropertyName)
    {
        if (!likePropertyName.toLowerCase().startsWith("set"))
        {
            likePropertyName = DataMutator.set + likePropertyName;
        }

        // Methods of a class with any accessibility.
        for (Method method : object.getClass().getDeclaredMethods())
        {
            if (method.getName().equalsIgnoreCase(likePropertyName))
            {
                if (method.getParameterTypes().length > 0)
                {
                    method.setAccessible(true);
                    return method;
                }
            }
        }

        // Methods of a class's hierarchy with public accessibility.
        for (Method method : object.getClass().getMethods())
        {
            if (method.getName().equalsIgnoreCase(likePropertyName))
            {
                if (method.getParameterTypes().length > 0)
                {
                    method.setAccessible(true);
                    return method;
                }
            }
        }

        return null;
    }

    /**
     *
     * @param object
     * @param likePropertyName
     * @return
     */
    public static Method acquireGetterMethod(Object object, String likePropertyName)
    {
        // Methods of a class with any accessibility.
        for (Method method : object.getClass().getDeclaredMethods())
        {
            if (method.getName().equalsIgnoreCase(DataAccessor.get + likePropertyName) ||
                method.getName().equalsIgnoreCase(DataAccessor.is + likePropertyName))
            {
                if (method.getParameterTypes().length == 0)
                {
                    method.setAccessible(true);
                    return method;
                }
            }
        }

        // Methods of a class's hierarchy with public accessibility.
        for (Method method : object.getClass().getMethods())
        {
            if (method.getName().equalsIgnoreCase(DataAccessor.get + likePropertyName) ||
                method.getName().equalsIgnoreCase(DataAccessor.is + likePropertyName))
            {
                if (method.getParameterTypes().length == 0)
                {
                    method.setAccessible(true);
                    return method;
                }
            }
        }

        return null;
    }

    /**
     *  TODO Why did I write this and the above with different implementation? Reuse you fool!
     * @param object
     * @param returnClass
     * @return
     */
    public static Method acquireGetterMethod(Object object, Class<?> returnClass)
    {
        for (Method method : object.getClass().getMethods())
        {
            if (method.getName().startsWith(DataAccessor.get.name()) || method.getName().startsWith(DataAccessor.is.name()))
            {
                if (method.getParameterTypes().length == 0)
                {
                    if (method.getReturnType().getName().equals(returnClass.getName()))
                    {
                        method.setAccessible(true);
                        return method;
                    }
                }
            }
        }

        return null;
    }

    /**
     * @see #invoke(java.lang.Class, java.lang.String, java.lang.Object...)
     */
    public static Method acquireMethod(Class<?> class_, String likeMethodName, Object... parameters)
    {
        // Methods of a class with any accessibility.
        Method method = findMethod(class_.getDeclaredMethods(), likeMethodName, parameters);

        if (method == null)
        {
            // Methods of a class's hierarchy with public accessibility.
            method = findMethod(class_.getMethods(), likeMethodName, parameters);

            if (method == null)
            {
                LOGGER.warn(String.format("No method 'likeName' '%s' for %s with parameters %s%n", likeMethodName, class_, Arrays.toString(parameters)));
                return null;
            }
        }

        return method;
    }

    /**
     * @see #invoke(java.lang.Object, java.lang.String, java.lang.Object...)
     */
    public static Method acquireMethod(Object object, String likeMethodName, Object... parameters)
    {
        // Methods of a class with any accessibility.
        Method method = findMethod(object.getClass().getDeclaredMethods(), likeMethodName, parameters);

        if (method == null)
        {
            // Methods of a class's hierarchy with public accessibility.
            method = findMethod(object.getClass().getMethods(), likeMethodName, parameters);

            if (method == null)
            {
                LOGGER.warn(String.format("No method 'likeName' '%s' for %s with parameters %s%n", likeMethodName, object.getClass(), Arrays.toString(parameters)));
                return null;
            }
        }

        return method;
    }

    /**
     *
     * @param method
     * @return
     */
    public static boolean isSetter(Method method)
    {
        if (!method.getName().startsWith(DataMutator.set.name()))
        {
            return false;
        }
        else if (method.getParameterTypes().length != 1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     *
     * @param method
     * @return
     */
    public static boolean isGetter(Method method)
    {
		if (method.getName().startsWith(DataAccessor.get.name()) || method.getName().startsWith(DataAccessor.is.name()))
		{
			if (method.getParameterTypes().length != 0)
	        {
	            return false;
	        }
	        else if (void.class.equals(method.getReturnType()))
	        {
	            return false;
	        }
	        else
	        {
	            return true;
	        }
		}
		else
		{
			return false;
		}
    }

    /**
     *
     * @param object
     * @return
     */
    public static List<Method> acquireDataAccessorMethods(Object object)
    {
        List<Method> methods = new ArrayList<Method>();

        for (Method method : object.getClass().getMethods())
        {
            if (!"getClass".equals(method.getName()))
            {
                if (isDataAccessor(method))
                {
                    if (method.getParameterTypes().length == 0)
                    {
                        methods.add(method);
                    }
                }
            }
        }

        return methods;
    }

    /**
     *
     * @param name
     * @return
     */
    public static String toDataAccessorProperty(String name)
    {
        for (DataAccessor dataAccessor : DataAccessor.values())
        {
            if (!name.equalsIgnoreCase(dataAccessor.name()) && name.startsWith(dataAccessor.name()))
            {
                return name.substring(dataAccessor.name().length());
            }
        }

        return name;
    }

    /**
     *
     * @param methodName
     * @return
     */
    public static boolean isDataAccessor(String methodName)
    {
    	for (DataAccessor dataAccessor : DataAccessor.values())
        {
            if (methodName.startsWith(dataAccessor.name()))
            {
                return true;
            }
        }

        return false;
    }

    /**
     *
     */
    public static boolean isDataAccessor(Method method)
    {
    	return isDataAccessor(method.getName());
    }

    /**
     * Find a valid method that is also the best match i.e. there could be more than one valid method such as via overloading.
     * @see #invoke(java.lang.Object, java.lang.String, java.lang.Object...)
     */
    private static Method findMethod(Method[] methods, String likeMethodName, Object... parameters)
    {
    	List<MatchMethod> matchMethods = new ArrayList<MatchMethod>();

        for (Method method : methods)
        {
        	MatchMethod matchMethod = matchMethod(method, likeMethodName, parameters);

        	if (matchMethod != null)
        	{
        		matchMethods.add(matchMethod);
        	}
        }

    	if (matchMethods.isEmpty())
    	{
    		return null;
    	}
    	else
    	{
    		MatchMethod bestMatchMethod = null;

    		for (MatchMethod matchMethod : matchMethods)
    		{
    			if (bestMatchMethod == null || matchMethod.matchingParametersCount() > bestMatchMethod.matchingParametersCount())
    			{
    				bestMatchMethod = matchMethod;
    			}
    		}

    		if (bestMatchMethod == null)
    		{
    			return null;
    		}
    		else
    		{
    			Method method = bestMatchMethod.method();
    			method.setAccessible(true);
    			return method;
    		}
    	}
    }

    /**
     * @see #invoke(java.lang.Object, java.lang.String, java.lang.Object...)
     * @param method
     * @param likeMethodName
     * @param parameters
     * @return
     */
    private static MatchMethod matchMethod(Method method, String likeMethodName, Object... parameters)
    {
        boolean parameterlessMethod = false;

        if (likeMethodName.endsWith("()"))
        {
            parameterlessMethod = true;
            likeMethodName = likeMethodName.replaceAll("\\(\\)", "");
        }

        String methodName = method.getName();

        method.setAccessible(true);

        if (methodName.equalsIgnoreCase(likeMethodName))
        {
            if (parameterlessMethod && method.getParameterTypes().length == 0)
            {
                return new MatchMethod(method, 0);
            }
            else if (parameters == null && method.getParameterTypes().length == 0)
            {
                return new MatchMethod(method, 0);
            }
            else if (parameters.length == 0 && method.getParameterTypes().length == 0)
            {
                return new MatchMethod(method, 0);
            }
            else if (parameters.length == method.getParameterTypes().length)
            {
                int matchingParametersCount = 0;

                for (int i = 0; i < parameters.length; i++)
                {
                    if (parameters[i] == null)
                    {
                        // Not an exact parameter match. Cannot prove because of the given null.
                    }
                    else if (method.getParameterTypes()[i].isPrimitive())
                    {
                        if (method.getParameterTypes()[i].toString().equals(parameters[i].getClass().toString()))
                        {
                            matchingParametersCount++;
                        }
                        else if (method.getParameterTypes()[i].toString().equals("int") &&
                                 parameters[i].getClass().toString().toUpperCase().endsWith("INTEGER"))
                        {
                            matchingParametersCount++;
                        }
                        else if (parameters[i].getClass().toString().toUpperCase().endsWith(method.getParameterTypes()[i].toString().toUpperCase()))
                        {
                            matchingParametersCount++;
                        }
                        else
                        {
                            return null;
                        }
                    }
                    else if (!method.getParameterTypes()[i].isPrimitive() && !method.getParameterTypes()[i].isInstance(parameters[i]))
                    {
                        return null;
                    }
                    else
                    {
                        try
                        {
                            method.getParameterTypes()[i].asSubclass(parameters[i].getClass());
                            matchingParametersCount++;
                        }
                        catch (ClassCastException e)
                        {
                            // Not an exact parameter match.
                        }
                    }
                }

                return new MatchMethod(method, matchingParametersCount);
            }
        }

        return null;
    }
}

/**
 *
 * @author David Ainslie
 */
class MatchMethod
{
    /** */
	private final Method method;
	
	/** */
	private final Integer matchingParametersCount;

	/**
	 *
	 * @param method
	 * @param matchingParametersCount
	 */
	public MatchMethod(Method method, Integer matchingParametersCount)
	{
		this.method = method;
		this.matchingParametersCount = matchingParametersCount;
	}

	/**
	 *
	 * @return
	 */
	public Method method()
	{
		return method;
	}

	/**
	 *
	 * @return
	 */
	public Integer matchingParametersCount()
	{
		return matchingParametersCount;
	}
}