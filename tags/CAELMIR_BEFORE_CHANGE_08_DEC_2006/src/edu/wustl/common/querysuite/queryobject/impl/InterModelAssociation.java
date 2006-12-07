
package edu.wustl.common.querysuite.queryobject.impl;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.wustl.common.querysuite.queryobject.IAttribute;
import edu.wustl.common.querysuite.queryobject.IInterModelAssociation;
import edu.wustl.common.util.global.Constants;

public class InterModelAssociation extends AbstractAssociation implements IInterModelAssociation
{

	private IAttribute sourceAttribute;

	private IAttribute targetAttribute;

	private Map<String, Set<String>> targetServiceUrls;

	public InterModelAssociation(IAttribute sourceAttribute, IAttribute targetAttribute)
	{
		super(sourceAttribute.getUMLClass(), targetAttribute.getUMLClass(), true);
		this.sourceAttribute = sourceAttribute;
		this.targetAttribute = targetAttribute;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5994329379075994140L;

	/**
	 * @return the sourceAttribute.
	 */
	public IAttribute getSourceAttribute()
	{
		return sourceAttribute;
	}

	/**
	 * @param sourceAttribute
	 *            the sourceAttribute to set.
	 */
	public void setSourceAttribute(IAttribute sourceAttribute)
	{
		this.sourceAttribute = sourceAttribute;
	}

	/**
	 * @return the targetAttribute.
	 */
	public IAttribute getTargetAttribute()
	{
		return targetAttribute;
	}

	/**
	 * @param targetAttribute
	 *            the targetAttribute to set.
	 */
	public void setTargetAttribute(IAttribute targetAttribute)
	{
		this.targetAttribute = targetAttribute;
	}

	public Set<String> getSourceServiceUrls()
	{
		return getTargetServiceUrls().keySet();
	}

	public Set<String> getTargetServiceUrls(String sourceServiceUrl)
	{
		Set<String> res = getTargetServiceUrls().get(sourceServiceUrl);
		if (res == null)
		{
			res = newStringSet();
			getTargetServiceUrls().put(sourceServiceUrl, res);
		}
		return res;
	}

	public boolean removeSourceServiceUrl(String sourceServiceUrl)
	{
		boolean res = getTargetServiceUrls().containsKey(sourceServiceUrl);
		getTargetServiceUrls().remove(sourceServiceUrl);
		return res;
	}

	public boolean removeTargetServiceUrl(String sourceServiceUrl, String targetServiceUrl)
	{
		return getTargetServiceUrls(sourceServiceUrl).remove(targetServiceUrl);
	}

	private Set<String> newStringSet()
	{
		return new LinkedHashSet<String>();
	}

	public void addSourceServiceUrl(String url)
	{
		getTargetServiceUrls().put(url, newStringSet());
	}

	public void addTargetServiceUrl(String sourceServiceUrl, String targetServiceUrl)
	{
		getTargetServiceUrls(sourceServiceUrl).add(targetServiceUrl);

	}

	public boolean isIntraModel()
	{
		return false;
	}

	public void setBidirectional(boolean bidirectional)
	{
		// do nothing

	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		if (sourceAttribute != null)
			hash = hash * Constants.HASH_PRIME + sourceAttribute.hashCode();
		if (targetAttribute != null)
			hash = hash * Constants.HASH_PRIME + targetAttribute.hashCode();
		hash = hash * Constants.HASH_PRIME + getTargetServiceUrls().hashCode();
		hash = hash * Constants.HASH_PRIME + super.hashCode();

		return hash;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		if (obj != null && this.getClass() == obj.getClass())
		{
			if (!super.equals(obj))
			{
				return false;
			}
			InterModelAssociation association = (InterModelAssociation) obj;
			if ((getSourceAttribute() == null
					? association.getSourceAttribute() == null
					: getSourceAttribute().equals(association.getSourceAttribute()))
					&& (getTargetAttribute() == null
							? association.getTargetAttribute() == null
							: getTargetAttribute().equals(association.getTargetAttribute()))
					&& getTargetServiceUrls().equals(association.getTargetServiceUrls()))
				;
			return true;
		}
		return false;
	}

	/**
	 * @return the targetServiceUrls.
	 */
	public Map<String, Set<String>> getTargetServiceUrls()
	{
		if (targetServiceUrls == null)
		{
			targetServiceUrls = new LinkedHashMap<String, Set<String>>();
		}
		return targetServiceUrls;
	}

	// public IInterModelAssociation cloneToInterModelAssociation() {
	// // TODO Auto-generated method stub
	// return null;
	// }

}
