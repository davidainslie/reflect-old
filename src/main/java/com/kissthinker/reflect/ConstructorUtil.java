package com.kissthinker.reflect;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Ainslie
 */
public abstract class ConstructorUtil
{
    /** */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConstructorUtil.class);

	/**
	 *
	 * @param <O>
	 * @param object
	 * @param parameters
	 * @return
	 * @throws ConstructorException
	 */
	@SuppressWarnings("unchecked")
	public static <O> O newInstance(O object, Object... parameters) throws ConstructorException
	{
		try
		{
			Constructor<?> constructor = constructor(object, parameters);
			return (O)constructor.newInstance(parameters);
		}
		catch (Exception e)
		{
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw new ConstructorException(String.format("For given object %s, could not instantiate a new object with parameters %s", object, Arrays.toString(parameters)));
		}
	}

	/**
	 *
	 * @param <O>
	 * @param instantiationClass
	 * @param parameters
	 * @return
	 * @throws ConstructorException
	 */
	@SuppressWarnings("unchecked")
	public static <O> O newInstance(Class<?> instantiationClass, Object... parameters) throws ConstructorException
	{
	    // A naughty workaround.
        if ("java.util.Arrays$ArrayList".equals(instantiationClass.getName()))
        {
            return (O)new ArrayList<O>();
        }

		try
		{
			Constructor<?> constructor = constructor(instantiationClass, parameters);
			return (O)constructor.newInstance(parameters);
		}
		catch (Exception e)
		{
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw new ConstructorException(String.format("For given class %s, could not instantiate a new object with parameters %s", instantiationClass, Arrays.toString(parameters)));
		}
	}

	/**
	 *
	 * @param object
	 * @param parameters
	 * @return
	 * @throws ConstructorException
	 */
	public static Constructor<?> constructor(Object object, Object... parameters) throws ConstructorException
	{
		Class<?>[] parameterClasses = new Class[parameters.length];

		for (int i = 0; i < parameters.length; i++)
		{
			parameterClasses[i] = parameters[i].getClass();
		}

		return constructor(object, parameterClasses);
	}

	/**
	 *
	 * @param object
	 * @param parameterClasses
	 * @return
	 * @throws ConstructorException
	 */
	public static Constructor<?> constructor(Object object, Class<?>... parameterClasses) throws ConstructorException
	{
		return constructor(object.getClass(), parameterClasses);
	}

	/**
	 *
	 * @param constructorClass
	 * @param parameters
	 * @return
	 * @throws ConstructorException
	 */
	public static Constructor<?> constructor(Class<?> constructorClass, Object... parameters) throws ConstructorException
	{
		Class<?>[] parameterClasses = new Class<?>[parameters.length];

		for (int i = 0; i < parameters.length; i++)
		{
			parameterClasses[i] = parameters[i].getClass();
		}

		return constructor(constructorClass, parameterClasses);
	}

	/**
	 *
	 * @param constructorClass
	 * @param parameterClasses
	 * @return
	 * @throws ConstructorException
	 */
	@SuppressWarnings("unchecked")
	public static <O> Constructor<O> constructor(Class<?> constructorClass, Class<?>... parameterClasses) throws ConstructorException
	{
		try
		{
			return (Constructor<O>)constructorClass.getConstructor(parameterClasses);
		}
		catch (Exception e)
		{
			List<MatchConstructor> matchConstructors = new ArrayList<MatchConstructor>();

			// No exact matching constructor.
			// Find a suitable constructor where parameters adhere to constructor interfaces/parent classes.
			for (Constructor<?> constructor : constructorClass.getConstructors())
			{
				MatchConstructor matchConstructor = matchConstructor(constructor, parameterClasses);

	        	if (matchConstructor != null)
	        	{
	        		matchConstructors.add(matchConstructor);
	        	}
			}

			if (!matchConstructors.isEmpty())
	    	{
	    		MatchConstructor bestMatchConstructor = null;

	    		for (MatchConstructor matchConstructor : matchConstructors)
	    		{
	    			if (bestMatchConstructor == null || matchConstructor.matchingParametersCount() > bestMatchConstructor.matchingParametersCount())
	    			{
	    				bestMatchConstructor = matchConstructor;
	    			}
	    		}

	    		if (bestMatchConstructor != null)
	    		{
	    			return (Constructor<O>)bestMatchConstructor.constructor();
	    		}
	    	}
		}

		throw new ConstructorException(String.format("For given class %s, no suitable constructor was found for parameter classes %s", constructorClass, Arrays.toString(parameterClasses)));
	}

    /**
     *
     * @param constructor
     * @param parameterClasses
     * @return
     */
    private static MatchConstructor matchConstructor(Constructor<?> constructor, Class<?>... parameterClasses)
    {
    	Class<?>[] constructorParameterClasses = constructor.getParameterTypes();

    	if (constructorParameterClasses.length == parameterClasses.length)
        {
            int matchingParametersCount = 0;

            for (int i = 0; i < parameterClasses.length; i++)
            {
            	if (constructorParameterClasses[i].isPrimitive())
            	{
            		if (constructorParameterClasses[i].toString().equals(parameterClasses[i].toString()))
            		{
            			matchingParametersCount++;
            		}
            		else if (constructorParameterClasses[i].toString().equals("int") &&
            				 parameterClasses[i].toString().toUpperCase().endsWith("INTEGER"))
            		{
            			matchingParametersCount++;
            		}
            		else if (parameterClasses[i].toString().toUpperCase().endsWith(constructorParameterClasses[i].toString().toUpperCase()))
            		{
            			matchingParametersCount++;
            		}
            		else
            		{
            			return null;
            		}
            	}
            	else if (!constructorParameterClasses[i].isAssignableFrom(parameterClasses[i]))
                {
                    return null;
                }
            	else
            	{
            		if (constructorParameterClasses[i].equals(parameterClasses[i]))
            		{
            			matchingParametersCount++;
            		}
            	}
            }

            return new MatchConstructor(constructor, matchingParametersCount);
        }
    	else
    	{
    		return null;
    	}
    }
}

/**
 *
 * @author David Ainslie
 */
class MatchConstructor
{
    /** */
	private final Constructor<?> constructor;
	
	/** */
	private final Integer matchingParametersCount;

	/**
	 *
	 * @param method
	 * @param matchingParametersCount
	 */
	public MatchConstructor(Constructor<?> constructor, Integer matchingParametersCount)
	{
		this.constructor = constructor;
		this.matchingParametersCount = matchingParametersCount;
	}

	/**
	 *
	 * @return
	 */
	public Constructor<?> constructor()
	{
		return constructor;
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