package com.kissthinker.reflect;

/**
 * @author David Ainslie
 */
public class Pojo1 implements PojoInterface
{
    /** */
	private PojoInterface decorated;

	/**
	 *
	 */
	public Pojo1()
	{
	}

	/**
	 *
	 * @param decorated
	 */
	public Pojo1(PojoInterface decorated)
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
}