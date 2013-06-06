/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

/**
 * <p>Title: CommonSearchAction Class>
 * <p>Description:	This class is used to retrieve the information whose record
 * is to be modified.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 20, 2005
 */

package edu.wustl.common.action;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractBizLogicFactory;
import edu.wustl.common.factory.AbstractDomainObjectFactory;
import edu.wustl.common.factory.MasterFactory;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;


/**
 * This class is used to retrieve the information whose record is to be modified.
 * @author gautam_shetty
 */
public class CommonSearchAction extends Action
{
    /**
     * Overrides the execute method of Action class.
     * Retrieves and populates the information to be edited. 
     * */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServerException
    {
        Logger.out.debug("*******In here************");
        /** 
         * Represents whether the search operation was successful or not.
         */
        String target = null;
        
        AbstractActionForm abstractForm = (AbstractActionForm) form;
        /* Get the id whose information is to be searched */
        Long identifier = 	Long.valueOf(request.getParameter(Constants.SYSTEM_IDENTIFIER)); 
        if(identifier == null || identifier.longValue() == 0  )
        	identifier = ((Long)request.getAttribute(Constants.SYSTEM_IDENTIFIER)); 
        
        try
        {
            //Retrieves the information to be edited.
        	IBizLogic bizLogic = AbstractBizLogicFactory.getBizLogic(
                    						ApplicationProperties.getValue("app.bizLogicFactory"),
                    							"getBizLogic", abstractForm.getFormId());
            AbstractDomainObject abstractDomain = null;
            List list = null;
            
            AbstractDomainObjectFactory abstractDomainObjectFactory = 
                	(AbstractDomainObjectFactory) MasterFactory
                				.getFactory(ApplicationProperties.getValue("app.domainObjectFactory"));
            String objName = abstractDomainObjectFactory.getDomainObjectName(abstractForm.getFormId());
            list= bizLogic.retrieve(objName,Constants.SYSTEM_IDENTIFIER, identifier.toString());
            
            if (list!=null && list.size() != 0)
            {
                /* 
                  If the record searched is present in the database,
                  populate the formbean with the information retrieved.
                 */
                abstractDomain = (AbstractDomainObject)list.get(0);
                abstractForm.setAllValues(abstractDomain);
                String pageOf = (String)request.getAttribute(Constants.PAGEOF);
                if (pageOf == null)
                	pageOf = (String)request.getParameter(Constants.PAGEOF);
                target = pageOf;
                abstractForm.setMutable(false);
                
                String operation = (String)request.getAttribute(Constants.OPERATION);
                request.setAttribute(Constants.OPERATION,operation);
            }
            else
            {
                /* 
                  If the searched record is not present in the database,  
                  display an Error message.
                 */
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.unknown",
                		AbstractDomainObject.parseClassName(objName)));
                saveErrors(request,errors);
                target = new String(Constants.FAILURE);
            }
        }
        catch (BizLogicException excp)
        {
            target = Constants.FAILURE;
            Logger.out.error(excp.getMessage(), excp);
        }
        catch (DAOException excp)
        {
            target = Constants.FAILURE;
            Logger.out.error(excp.getMessage());
        }
        return (mapping.findForward(target));
    }
}