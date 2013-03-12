
package edu.wustl.common.mockedObjectImplementations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.scheduler.util.IHostAppUserDataRetriever;

public class MockedHostAppUserDataRetriever implements IHostAppUserDataRetriever
{

	public List<NameValueBean> getUserIdNameListForReport(Long id) throws Exception
	{
		// TODO Auto-generated method stub
		return Arrays.asList(new NameValueBean(12L, "mockedName1"), new NameValueBean(13L,
				"mockedName2"));
	}

	public List<Object[]> getUserIdAndMailAddressList(Collection<Long> userIdCollection)
			throws Exception
	{
		// TODO Auto-generated method stub
		List<Object[]> list = new ArrayList<Object[]>();
		for (Long userId : userIdCollection)
		{
			Object[] obj = {userId, "mock" + userId + "@mail.com"};
			list.add(obj);
		}
		return list;
	}

	public List<String> getUserNamesList(Collection<Long> idList) throws Exception
	{
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		for (Long userId : idList)
		{
			list.add("name" + userId);
		}
		return list;
	}

	public String getUserEmail(Long userId) throws Exception
	{
		// TODO Auto-generated method stub
		return "mock" + userId + "@mail.com";
	}

}
