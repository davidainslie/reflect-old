package com.kissthinker.reflect;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Test;

/**
 * @author David Ainslie
 */
public class ConstructorUtilTest
{
    /**
     *
     */
	@Test
	public void getConstructor()
	{
		PojoInterface pojoInterface = new Pojo(new Pojo());
		assertNotNull(ConstructorUtil.constructor(pojoInterface, pojoInterface.getDecorated()));

		pojoInterface = new Pojo();
		assertNotNull(ConstructorUtil.constructor(pojoInterface));
	}

	/**
	 *
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@Test
	public void getConstructorOverloaded() throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		Constructor<Pojo> constructor = ConstructorUtil.constructor(Pojo.class, Integer.class);
		System.out.println(constructor);

		constructor = ConstructorUtil.constructor(Pojo.class, Double.class);
		System.out.println(constructor);

		constructor = ConstructorUtil.constructor(Pojo.class, int.class);
		System.out.println(constructor);

		constructor = ConstructorUtil.constructor(Pojo.class, ArrayList.class, int.class);
		System.out.println(constructor);
		constructor.newInstance(new ArrayList<String>(), new Integer(1));

		constructor = ConstructorUtil.constructor(Pojo.class, LinkedList.class, int.class);
		System.out.println(constructor);
	}

	/**
	 *
	 */
	@Test
	public void newInstance()
	{
		PojoInterface pojoInterface = ConstructorUtil.newInstance(Pojo.class);
		assertNotNull(pojoInterface);

		pojoInterface = ConstructorUtil.newInstance(Pojo.class, new Pojo());
		assertNotNull(pojoInterface);
	}
}