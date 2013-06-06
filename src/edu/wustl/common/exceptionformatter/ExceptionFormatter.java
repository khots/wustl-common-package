/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.exceptionformatter;
/**
 * 
 * @author sachin_lale
 * Description: Interface defines method for formatting the database specific Exception message  
 */
public interface ExceptionFormatter {
	public String formatMessage(Exception objExcp,Object args[]);
}
