/**
 * <p>Title: DefaultLookupParameters Class>
 * <p>Description:	This is the implementation class of LookupParameters which stores the object which is to be matched and the cutoff value </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author vaishali_khandelwal
 */
package edu.wustl.common.lookup;


public class DefaultLookupParameters implements LookupParameters
{
	Object object;
	Double cutoff;
	
	public Double getCutoff()
	{
		return cutoff;
	}
	
	public void setCutoff(Double cutoff)
	{
		this.cutoff = cutoff;
	}
	
	public Object getObject()
	{
		return object;
	}
	
	public void setObject(Object object)
	{
		this.object = object;
	}

}
