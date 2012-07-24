
package edu.wustl.common.tags.domain;

import java.io.Serializable;

public class TagItem<T> implements Serializable
{

	private static final long serialVersionUID = 1L;
	private long identifier;
	private T obj;
	private long tagId;
	private Tag<T> tag;

	 
	public long getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(long identifier)
	{
		this.identifier = identifier;
	}

	public T getObj()
	{
		return obj;
	}

	public void setObj(T obj)
	{
		this.obj = obj;
	}

	public long getTagId()
	{
		return tagId;
	}

	public void setTagId(long tagId)
	{
		this.tagId = tagId;
	}

	public Tag<T> getTag()
	{
		return tag;
	}

	public void setTag(Tag<T> tag)
	{
		this.tag = tag;
	}

}
