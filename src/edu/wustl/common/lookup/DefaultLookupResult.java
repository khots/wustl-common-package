/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

/**
 * <p>Title: LookupResult Class>
 * <p>Description:	This contains the matching object with probablity match.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author vaishali_khandelwal
 */
package edu.wustl.common.lookup;

public class DefaultLookupResult
{
	Object object;
	Double probablity;
	
	public Object getObject()
	{
		return object;
	}
	
	public void setObject(Object object)
	{
		this.object = object;
	}
	
	public Double getProbablity()
	{
		return probablity;
	}
	
	public void setProbablity(Double probablity)
	{
		this.probablity = probablity;
	}
	
	
}
