/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.tags.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public class Tag<T> implements Serializable
{

	private static final long serialVersionUID = 1L;
	private long identifier;
	private String label;
	private Set<TagItem<T>> tagItem;
	private Set<Long> sharedUserIds = new HashSet<Long>();
	private long userId;

	public long getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(long identifier)
	{
		this.identifier = identifier;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public Set<TagItem<T>> getTagItem()
	{
		return tagItem;
	}

	public void setTagItem(Set<TagItem<T>> tagItem)
	{
		this.tagItem = tagItem;
	}

	public long getUserId()
	{
		return userId;
	}

	public void setUserId(long userId)
	{
		this.userId = userId;
	}

	public Set<Long> getSharedUserIds() {
		return sharedUserIds;
	}

	public void setSharedUserIds(Set<Long> sharedUserIds) {
		this.sharedUserIds = sharedUserIds;
	}
}
