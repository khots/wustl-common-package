/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

/*
 * Created on May 26, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.wustl.common.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.CommonFileReader;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;


/**
 * @author kapil_kaveeshwar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ApplicationFooterAction extends Action
{
	public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
    {
		String pageTitle = (String)request.getParameter("PAGE_TITLE_KEY");
		String fileNameKey = (String)request.getParameter("FILE_NAME_KEY");
		
		Logger.out.debug("File key name : "+fileNameKey);
		String fileName = XMLPropertyHandler.getValue(fileNameKey);
		Logger.out.debug("File name : "+fileName);
		
		CommonFileReader reader = new CommonFileReader();
		
		String filePath = Variables.propertiesDirPath + System.getProperty("file.separator") + fileName;
		String contents = reader.readData(filePath);
		
		request.setAttribute("CONTENTS",contents);
		request.setAttribute("PAGE_TITLE",pageTitle);
		
	 	return mapping.findForward(Constants.SUCCESS);
    }
}