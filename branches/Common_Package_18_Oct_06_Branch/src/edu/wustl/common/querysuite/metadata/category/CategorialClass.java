
package edu.wustl.common.querysuite.metadata.category;

import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.metadata.path.IPath;

/**
 * @version 1.0
 * @created 28-Dec-2006 2:01:13 AM
 */
public class CategorialClass
{

	private long id;
	private long deEntityId;
	public Category category;
	public Set<CategorialAttribute> categorialAttributeCollection;
	public Set<CategorialClass> children;
	public CategorialClass parent;
	public IPath pathFromParent;

	public CategorialClass()
	{

	}

	public void finalize() throws Throwable
	{

	}

	public EntityInterface getDynamicExtensionsEntity()
	{
		return null;
	}

	public Set<CategorialAttribute> getCategorialAttributeCollection()
	{
		return categorialAttributeCollection;
	}

	public Set<CategorialClass> getChildren()
	{
		return children;
	}

	public IPath getPathFromParent()
	{
		return pathFromParent;
	}

	public CategorialClass getParent()
	{
		return parent;
	}

	public AttributeInterface findSourceAttribute(AttributeInterface catAttr)
	{
		for (CategorialAttribute categorialAttribute : getCategorialAttributeCollection())
		{
			if (catAttr.equals(categorialAttribute.getCategoryAttribute()))
			{
				return categorialAttribute.getSourceClassAttribute();
			}
		}
		return null;
	}
}