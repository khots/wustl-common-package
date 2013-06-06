/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.tokenprocessor;

import edu.wustl.common.exception.ApplicationException;

public class DummyTokenLabelGenerator implements ILabelTokens
{

	public String getTokenValue(Object object) throws ApplicationException
	{
		return "1234";
	}

}