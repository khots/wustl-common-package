/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.labelSQLApp.domain;

import java.io.Serializable;
import java.util.Collection;

public class LabelSQL implements Serializable
{
	private long id;
	private String label;
	private String query;
	
	
	public long getId()
	{
		return id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}

	
	public String getQuery()
	{
		return query;
	}

	
	public void setQuery(String query)
	{
		this.query = query;
	}

	
		
	
	
}