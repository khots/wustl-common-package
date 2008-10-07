
package edu.wustl.common.util.dbManager;

import org.hibernate.mapping.Collection;

/**This class contains all the relationship related data i.e className, relatedClass,
 * relationshipType.
 * @author vaishali_khandelwal
 */
public class ClassRelationshipData
{

	/*
	 Explanation of variables
	 consider Following block from Specimen.hbm.xml
	 <set
	 name="biohazardCollection" -------------------------> Attribute Name
	 table="CATISSUE_SPECIMEN_BIOHZ_REL" ----------------> Table Name
	 lazy="false"
	 inverse="false"
	 cascade="none"
	 sort="unsorted">
	<key
	       column="SPECIMEN_ID" --------------------------> Key Id
	 />
	 <many-to-many
	     class="edu.wustl.catissuecore.domain.Biohazard" --> Related Class
	     column="BIOHAZARD_ID" ----------------------------> Role Id
	     outer-join="auto"
	  />

	 </set>
	 */
	/**
	 * Name of class.
	 */
	private String className;
	/**
	 * Name of related class.
	 */
	private String relatedClassName;
	/**
	 * attribute signifying the role..
	 */
	private String roleAttribute;
	/**
	 * specify the type of relation.
	 */
	private String relationType;
	/**
	 * specify the table.
	 */
	private String relationTable;
	/**
	 * specify the key Id.
	 */
	private String keyId;
	/**
	 * specify role Id.
	 */
	private String roleId;

	/**
	 * Default Constructor.
	 */
	public ClassRelationshipData()
	{

	}

	public ClassRelationshipData(Collection col, String relType)
	{
		this.className = col.getOwner().getClassName();
		this.relatedClassName = col.getElement().getType().getName();
		this.roleAttribute = col.getRole();
		this.relationType = relType;
		this.relationTable = col.getElement().getTable().getName();
		this.keyId = HibernateMetaData.getKeyId(roleAttribute);
		this.roleId = HibernateMetaData.getRoleKeyId(roleAttribute);

	}
	public ClassRelationshipData(String className, ClassRelationshipData refObject)
	{
		this(refObject);
		this.className =className;
	}
	public ClassRelationshipData(String className,String relatedClassName, ClassRelationshipData refObject)
	{
		this(refObject);
		this.className =className;
		this.relatedClassName = relatedClassName;
	}
	
	private ClassRelationshipData(ClassRelationshipData refObject)
	{
		this.className =refObject.className ;
		this.relatedClassName = refObject.relatedClassName;
		this.roleAttribute =refObject.roleAttribute;
		this.relationType = refObject.relationType;
		this.relationTable = refObject.relationTable;
		this.keyId = refObject.keyId;
		this.roleId = refObject.roleId;
	}

	/**
	 * Full parameterized Constructor.
	 * @param className class name.
	 * @param relatedClassName related class name.
	 * @param roleAttribute attribute for role.
	 * @param relationType type of relation.
	 * @param relationTable table of relation.
	 * @param keyId key id.
	 * @param roleId id for role.
	 */
	public ClassRelationshipData(String className, String relatedClassName, String roleAttribute,
			String relationType, String relationTable, String keyId, String roleId)
	{
		this.className = className;
		this.relatedClassName = relatedClassName;
		this.roleAttribute = roleAttribute;
		this.relationType = relationType;
		this.relationTable = relationTable;
		this.keyId = keyId;
		this.roleId = roleId;
	}

	/**
	 * Minimal Constructor.
	 * @param className class name.
	 * @param relatedClassName related class name.
	 * @param roleAttribute attribute for role.
	 */
	public ClassRelationshipData(String className, String relatedClassName, String roleAttribute)
	{
		this.className = className;
		this.relatedClassName = relatedClassName;
		this.roleAttribute = roleAttribute;

	}

	/**
	 * @return the name of class.
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * set the name of class.
	 * @param className class name.
	 */
	public void setClassName(String className)
	{
		this.className = className;
	}

	/**
	 * @return the key id.
	 */
	public String getKeyId()
	{
		return keyId;
	}

	/**
	 * set the key id.
	 * @param keyId key id.
	 */
	public void setKeyId(String keyId)
	{
		this.keyId = keyId;
	}

	/**
	 * @return the Related Class Name which has relation with class.
	 */
	public String getRelatedClassName()
	{
		return relatedClassName;
	}

	/**
	 * set the Related Class Name which has relation with class.
	 * @param relatedClassName related class name.
	 */
	public void setRelatedClass(String relatedClassName)
	{
		this.relatedClassName = relatedClassName;
	}

	/**
	 * @return the table.
	 */
	public String getRelationTable()
	{
		return relationTable;
	}

	/**
	 * set the table.
	 * @param relationTable table of relation.
	 */
	public void setRelationTable(String relationTable)
	{
		this.relationTable = relationTable;
	}

	/**
	 * @return the relation type i.e Many-To-Many or  One-To-Many.
	 */
	public String getRelationType()
	{
		return relationType;
	}

	/**
	 * set the relation type i.e Many-To-Many or  One-To-Many.
	 * @param relationType type of relation.
	 */
	public void setRelationType(String relationType)
	{
		this.relationType = relationType;
	}

	/**
	 * @return the attribute role.
	 */
	public String getRoleAttribute()
	{
		return roleAttribute;
	}

	/**
	 * set the attribute role.
	 * @param roleAttribute attribute role.
	 */
	public void setRoleAttribute(String roleAttribute)
	{
		this.roleAttribute = roleAttribute;
	}

	/**
	 * @return the role Id.
	 */
	public String getRoleId()
	{
		return roleId;
	}

	/**
	 * set the role Id.
	 * @param roleId id for role.
	 */
	public void setRoleId(String roleId)
	{
		this.roleId = roleId;
	}

	/** This function checks weather obj is equal to the obj which invoked this method.
	 * For equality checking we are using Class Name, Related Class Name and Role Attribute Name because
	 * these three variables will be unique for any relationship.
	 * @param obj the object for which equality is checked.
	 * @return boolean
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		boolean isEqual=false;
		if (obj instanceof ClassRelationshipData)
		{
			ClassRelationshipData crd = (ClassRelationshipData) obj;
			if (this.getClassName().equals(crd.getClassName())
					&& this.getRelatedClassName().equals(crd.getRelatedClassName())
					&& this.getRoleAttribute().equals(crd.getRoleAttribute()))

			{
				isEqual=true;
			}
		}
		return isEqual;
	}

	/** This function returns the hashCode value for the object which invoked this method.
	 * 	@return int - hash code value
	 */
	public int hashCode()
	{
		return getHashValue(className)+getHashValue(relatedClassName)+getHashValue(roleAttribute);
	}
	/**
	 * @param prop String -property of this class
	 * @return int -0 if property is null or hash code of that property
	 */
	private int getHashValue(String prop)
	{
		int hashValue=0;
		if(null!=prop)
		{
			hashValue=prop.hashCode();
		}
		return hashValue;
	}
}