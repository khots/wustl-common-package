
package edu.wustl.common.tree;

import java.util.Date;

/**
 * A tree node to represent an experiment and an experiment group
 * in a hierarchical view using tree data structure.
 * @author chetan_bh
 */
public class ExperimentTreeNode extends TreeNodeImpl
{

    /**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -2865433413624982062L;

	/**
	 * Name of the experiment/experiment group.
	 */
	private String name;

	/**
	 * Description for the experiment/experiment group.
	 */
	private String desc;

	/**
	 * Date of creation.
	 */
	private Date createdOn;

	/**
	 * Date last updated.
	 */
	private Date lastUpdatedOn;

	/**
	 * boolean value to distinguish between experiment and experiment group.
	 */
	private transient boolean isExperimentGroup = false;

	/**
	 * Constructor.
	 */
	public ExperimentTreeNode()
	{
		super();
	}

	/**
	 * Constructor.
	 * @param identifier identifier.
	 */
	public ExperimentTreeNode(Long identifier)
	{
		this(identifier, null);
	}

	/**
	 * Constructor.
	 * @param identifier identifier
	 * @param name name
	 */
	public ExperimentTreeNode(Long identifier, String name)
	{
		super();
		setIdentifier(identifier);
		this.name = name;
	}

	/**
	 * Gets creation date.
	 * @return Date of creation.
	 */
	public Date getCreatedOn()
	{
		return (Date)createdOn.clone();
	}

	/**
	 * Sets creation date.
	 * @param createdOn Date of creation
	 */
	public void setCreatedOn(Date createdOn)
	{
		this.createdOn = (Date)createdOn.clone();
	}

	/**
	 * Gets description.
	 * @return Description for the experiment/experiment group
	 */
	public String getDesc()
	{
		return desc;
	}

	/**
	 * Sets Description.
	 * @param desc Description for the experiment/experiment group
	 */
	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	/**
	 * Sets experiment Name.
	 * @return Name of the experiment/experiment group.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets experiment name.
	 * @param name Name of the experiment/experiment group.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns true if the node is an experiment group node else false.
	 * @return boolean value to distinguish between experiment and experiment group.
	 */
	public boolean isExperimentGroup()
	{
		return isExperimentGroup;
	}

	/**
	 * Sets node type to experiment group if parameter passed is true, else experiment node.
	 * @param isExperimentGroup boolean value to distinguish between experiment and experiment group.
	 */
	public void setExperimentGroup(boolean isExperimentGroup)
	{
		this.isExperimentGroup = isExperimentGroup;
	}

	/**
	 * Gets last updated date.
	 * @return Date last updated.
	 */
	public Date getLastUpdatedOn()
	{
		return (Date)lastUpdatedOn.clone();
	}

	/**
	 * Sets last updated date.
	 * @param lastUpdatedOn Date last updated.
	 */
	public void setLastUpdatedOn(Date lastUpdatedOn)
	{
		this.lastUpdatedOn = (Date)lastUpdatedOn.clone();
	}

	/**
	 * overrides edu.wustl.common.tree.TreeNodeImpl.toString.
	 * @return name.
	 */
	public String toString()
	{
		return name;
	}

	/**
	 * overrides TreeNodeImpl.equals method .
	 * @param obj Object.
	 * @return true if equal.
	 */
	public boolean equals(Object obj)
	{
		return super.equals(obj);
	}
	/**
	 * overrides TreeNodeImpl.hashCode method.
	 * @return hashCode.
	 */
	public int hashCode()
	{
		return super.hashCode();
	}
}
