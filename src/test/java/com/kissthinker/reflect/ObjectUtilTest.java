package com.kissthinker.reflect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;

import org.junit.Test;

/**
 * @author David Ainslie
 */
public class ObjectUtilTest
{
    /**
     *
     */
    @Test
    public void getPackageName()
    {
        assertEquals("com.kissthinker.reflect", ObjectUtil.packageName(this));
        assertEquals("com.kissthinker.reflect", ObjectUtil.packageName(getClass()));
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void primitive() throws Exception
    {
        Integer integer = 0;

        assertTrue(integer.getClass().isInstance(0));
        assertNotNull(integer.getClass().cast(0));

        Constructor<?> constructor = integer.getClass().getConstructor(String.class);
        assertNotNull(constructor.newInstance(integer.toString()));

        Boolean bool = true;

        constructor = bool.getClass().getConstructor(String.class);
        assertNotNull(constructor.newInstance(bool.toString()));
    }
}