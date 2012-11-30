
package edu.wustl.common.tags.domain;

import java.io.Serializable;

public class TagItem<T> implements Serializable
{

	private static final long serialVersionUID = 1L;
	private long identifier;
	private long objId;
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

	public long getObjId() {
		return objId;
	}

	public void setObjId(long objId) {
		this.objId = objId;
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
