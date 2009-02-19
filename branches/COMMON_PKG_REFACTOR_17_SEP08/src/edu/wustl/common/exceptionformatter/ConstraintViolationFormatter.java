/**
 * <p>Title: ConstraintViolationFormatter class>
 * <p>Description: ConstraintViolationFormatter is used to construct user readable constraint
 * violation messages</p>
 * @version 1.0
 * @author kalpana_thakur
 */
package edu.wustl.common.exceptionformatter;

import edu.wustl.dao.exception.DAOException;

/**
 * @author kalpana_thakur
 * Used to format the exception thrown to user readable form.
 */
public class ConstraintViolationFormatter implements ExceptionFormatter
{

	/**
	 * This will be called to format constraint violation exception messages.
	 * @param objExcp : Exception thrown.
	 * @throws DAOException : Database exception
	 * @return string : It return the formatted error messages.
	 */
	public String formatMessage(Exception objExcp) throws DAOException
	{
		String formatedMessage = null ;
		/*try
		{
			String appName = CommonServiceLocator.getInstance().getAppName();
			IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
			DAO dao = daoFactory.getDAO();
			formatedMessage = dao.formatMessage(objExcp);
		}
		catch(Exception exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"ConstraintViolationFormatter.java :");
		}*/
		return formatedMessage;
	}

	/**
	 * @param objExcp :
	 * @param args :
	 * This will be called to format exception messages.
	 * @return string :formated message.
	 */
	public String formatMessage(Exception objExcp, Object[] args)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
