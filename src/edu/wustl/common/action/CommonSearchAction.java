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
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
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
        long st = System.currentTimeMillis();
    	Logger.out.debug("*******In here************");
        
        /** 
         * Represents whether the search operation was successful or not.
         */
        String target = null;
        
        /* Get the id whose information is to be searched */
        Long identifier = 	Long.valueOf(request.getParameter(Constants.SYSTEM_IDENTIFIER)); 
        if(identifier == null || identifier.longValue() == 0  )
        {
        	identifier = ((Long)request.getAttribute(Constants.SYSTEM_IDENTIFIER));
        	//Deepti for futureSCG 
        	//In case of CP based view when for any CPR there are no SCGs present then identifier will be null.
        	if (identifier == null )
        	{
        		 String pageOf = (String)request.getAttribute(Constants.PAGEOF);
                 if (pageOf == null)
                 	pageOf = (String)request.getParameter(Constants.PAGEOF);
                 target = pageOf;
                 HashMap<String, Long> forwardToHashMap = new HashMap<String, Long>();
                 forwardToHashMap.put("collectionProtocolId",new Long((String)request.getParameter("cpSearchCpId")));
                 forwardToHashMap.put("participantId", new Long((String)request.getParameter("cpSearchParticipantId")));
                 forwardToHashMap.put("COLLECTION_PROTOCOL_EVENT_ID", new Long((String)request.getParameter("COLLECTION_PROTOCOL_EVENT_ID")));
                 request.setAttribute("forwardToHashMap", forwardToHashMap);
                 long et = System.currentTimeMillis();
                 Logger.out.info("Sachin1: " + ((et-st)/1000));
                 
                 return (mapping.findForward(target));
        	}
        }
        
        target = openPageInEdit( form, identifier, request);
        
        
        long et = System.currentTimeMillis();
        Logger.out.info("Sachin2: " + ((et-st)/1000));
        return (mapping.findForward(target));
    }
    
    
    private String openPageInEdit(ActionForm form, Long identifier, HttpServletRequest request)
    {
    	AbstractActionForm abstractForm = (AbstractActionForm) form;
    	String target = null;

	    try
	    {
	        //Retrieves the information to be edited.
	    	//IBizLogic bizLogic = AbstractBizLogicFactory.getBizLogic(ApplicationProperties.getValue("app.bizLogicFactory"), "getBizLogic", abstractForm.getFormId());
	        
	        AbstractDomainObjectFactory abstractDomainObjectFactory = (AbstractDomainObjectFactory) MasterFactory
	            				.getFactory(ApplicationProperties.getValue("app.domainObjectFactory"));
	        String objName = abstractDomainObjectFactory.getDomainObjectName(abstractForm.getFormId());
	        
	        DefaultBizLogic bizLogic = new DefaultBizLogic();
	        
	        long st1 = System.currentTimeMillis();
	        //List list= bizLogic.retrieve(objName,Constants.SYSTEM_IDENTIFIER, identifier.toString());
	        boolean isSuccess = bizLogic.retrieveForEditMode(objName,Constants.SYSTEM_IDENTIFIER, identifier.toString(), abstractForm);
	        long et1 = System.currentTimeMillis();
	        Logger.out.info("Time to retrive for EDIT: " + ((et1-st1)/1000));
	        
	        //if (list!=null && list.size() != 0)
	        if(isSuccess)
	        {
	            /* 
	              If the record searched is present in the database,
	              populate the formbean with the information retrieved.
	             */
//	        	AbstractDomainObject abstractDomain = (AbstractDomainObject)list.get(0);
//	            abstractForm.setAllValues(abstractDomain);
	            
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
	            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.unknown", AbstractDomainObject.parseClassName(objName)));
	            saveErrors(request,errors);
	            target = new String(Constants.FAILURE);
	        }
	    }
//	    catch (BizLogicException excp)
//	    {
//	        target = Constants.FAILURE;
//	        Logger.out.error(excp.getMessage(), excp);
//	    }
	    catch (DAOException excp)
	    {
	        target = Constants.FAILURE;
	        Logger.out.error(excp.getMessage(), excp);
	    }
	    return target;
    }
}