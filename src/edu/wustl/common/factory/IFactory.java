/**
 *
 */
package edu.wustl.common.factory;

import edu.wustl.common.bizlogic.IBizLogic;


/**
 * @author prashant_bandal
 *
 */
public interface IFactory
{
	/**
	 * get the BizLogic.
	 * @param formId form Id
	 * @return IBizLogic.
	 */
	IBizLogic getBizLogic(int formId);
}
