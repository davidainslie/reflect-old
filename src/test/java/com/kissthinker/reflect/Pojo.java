package com.kissthinker.reflect;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author David Ainslie
 */
public class Pojo implements PojoInterface
{
    /** */
	private static final Logger LOGGER = Logger.getLogger("Pojo");

	/** */
	private PojoInterface decorated;

	/**
	 *
	 */
	public Pojo()
	{
	}

	/**
	 *
	 * @param integer
	 */
	public Pojo(Integer integer)
	{
	}

	/**
	 *
	 * @param double_
	 */
	public Pojo(Double double_)
	{
	}

	/**
	 *
	 * @param int_
	 */
	public Pojo(int int_)
	{
	}

	/**
	 *
	 * @param list
	 * @param int_
	 */
	public Pojo(List<Integer> list, int int_)
	{
	}

	/**
	 *
	 * @param list
	 * @param int_
	 */
	public Pojo(ArrayList<Integer> list, int int_)
	{
	}

	/**
	 *
	 * @param decorated
	 */
	public Pojo(PojoInterface decorated)
	{
		this.decorated = decorated;
	}

	/**
	 *
	 * @see com.ddtechnology.PojoInterface#getDecorated()
	 */
	@Override
	public PojoInterface getDecorated()
	{
		return decorated;
	}

	/**
	 *
	 * @return
	 */
	public boolean isDecorated()
	{
		if (decorated == null)
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
	 * @param strings
	 */
	public void method(List<String> strings)
	{
		LOGGER.info("method(List<String>)");
	}

	/**
	 *
	 * @param strings
	 */
	public void method(ArrayList<String> strings)
	{
		LOGGER.info("method(ArrayList<String>)");
	}
}