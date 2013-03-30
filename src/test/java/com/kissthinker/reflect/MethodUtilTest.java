package com.kissthinker.reflect;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Test;

/**
 * @author David Ainslie
 */
public class MethodUtilTest
{
    /**
     *
     */
	@Test
	public void acquireMethod()
	{
		Method method = MethodUtil.acquireMethod(new Pojo(), "method", new ArrayList<String>());
		assertNotNull(method);
		assertTrue(method.getParameterTypes()[0].isInstance(new ArrayList<String>()));

		method = MethodUtil.acquireMethod(new Pojo(), "method", new LinkedList<String>());
		assertNotNull(method);
		assertTrue(method.getParameterTypes()[0].isInstance(new LinkedList<String>()));
	}

	/**
	 *
	 */
	@Test
	public void isGetter()
	{
		assertTrue(MethodUtil.isGetter(MethodUtil.acquireMethod(new Pojo(), "getDecorated")));
		assertTrue(MethodUtil.isGetter(MethodUtil.acquireMethod(new Pojo(), "isDecorated")));
		assertFalse(MethodUtil.isGetter(MethodUtil.acquireMethod(new Pojo(), "method", new LinkedList<String>())));
	}
}