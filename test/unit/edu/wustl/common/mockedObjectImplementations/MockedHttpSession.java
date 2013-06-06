/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.mockedObjectImplementations;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class MockedHttpSession implements HttpSession
{

	public Map<String, Object> attributeMap = new HashMap<String, Object>();

	public MockedHttpSession(Map<String, Object> attributeMap)
	{
		this.attributeMap = attributeMap;
	}

	public Object getAttribute(String arg0)
	{
		// TODO Auto-generated method stub
		return attributeMap.get(arg0);
	}

	public Enumeration getAttributeNames()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public long getCreationTime()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public String getId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public long getLastAccessedTime()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMaxInactiveInterval()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public ServletContext getServletContext()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public HttpSessionContext getSessionContext()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Object getValue(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getValueNames()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void invalidate()
	{
		// TODO Auto-generated method stub

	}

	public boolean isNew()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void putValue(String arg0, Object arg1)
	{
		// TODO Auto-generated method stub

	}

	public void removeAttribute(String arg0)
	{
		// TODO Auto-generated method stub

	}

	public void removeValue(String arg0)
	{
		// TODO Auto-generated method stub

	}

	public void setAttribute(String arg0, Object arg1)
	{
		// TODO Auto-generated method stub
		attributeMap.put(arg0, arg1);
	}

	public void setMaxInactiveInterval(int arg0)
	{
		// TODO Auto-generated method stub

	}

}
