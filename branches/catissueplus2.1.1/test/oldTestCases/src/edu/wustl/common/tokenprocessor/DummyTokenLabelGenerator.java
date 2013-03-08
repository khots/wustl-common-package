
package edu.wustl.common.tokenprocessor;

import edu.wustl.common.exception.ApplicationException;

public class DummyTokenLabelGenerator implements ILabelTokens
{

	public String getTokenValue(Object object) throws ApplicationException
	{
		return "1234";
	}

}