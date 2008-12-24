package edu.wustl.common.factory;

import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.util.AbstractForwardToProcessor;


public class MyBizLogicFactory implements IFactory,IForwordToFactory
{
	public IBizLogic getBizLogic(int formId)
	{
		return null;
	}

	public AbstractForwardToProcessor getForwardToPrintProcessor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractForwardToProcessor getForwardToProcessor()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
