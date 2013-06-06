/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

/**
 * <p>Title: CommonAddEditAction Class>
 * <p>Description:	This Class is used to Add/Edit the data in the database.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 21, 2005
 */

package edu.wustl.common.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.beans.AddNewSessionDataBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.bizlogic.QueryBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractBizLogicFactory;
import edu.wustl.common.factory.AbstractDomainObjectFactory;
import edu.wustl.common.factory.AbstractForwardToFactory;
import edu.wustl.common.factory.MasterFactory;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.AbstractForwardToProcessor;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.dbManager.HibernateMetaData;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * This Class is used to Add/Edit data in the database.
 * @author gautam_shetty
 */
public class CommonAddEditAction extends Action
{

    /**
     * Overrides the execute method of Action class.
     * Adds / Updates the data in the database.
     * */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException
    {
        String target = null;
        AbstractDomainObject abstractDomain = null;
        
        try
        {
            AbstractActionForm abstractForm = (AbstractActionForm) form;
            IBizLogic bizLogic = AbstractBizLogicFactory.getBizLogic(
                    							ApplicationProperties.getValue("app.bizLogicFactory"),
                  								"getBizLogic", abstractForm.getFormId());
            
            Logger.out.debug("Got BizLogic from BizLogicfactory.............");
            
            QueryBizLogic queryBizLogic = (QueryBizLogic)AbstractBizLogicFactory.getBizLogic(
                    	ApplicationProperties.getValue("app.bizLogicFactory"),
						"getBizLogic", Constants.QUERY_INTERFACE_ID);
            
			AbstractDomainObjectFactory abstractDomainObjectFactory = 
            	(AbstractDomainObjectFactory) MasterFactory.getFactory(
            	        ApplicationProperties.getValue("app.domainObjectFactory"));
            
            //The object name which is to be added.
            String objectName = abstractDomainObjectFactory.getDomainObjectName(abstractForm.getFormId());
            
            ActionMessages messages = null;                    
            
            if (abstractForm.isAddOperation())
            {
                //If operation is add, add the data in the database.
                abstractDomain = abstractDomainObjectFactory.getDomainObject(abstractForm.getFormId(), abstractForm);
                bizLogic.insert(abstractDomain, getSessionData(request), Constants.HIBERNATE_DAO);
                
                target = new String(Constants.SUCCESS);
                
                // The successful add messages. Changes done according to bug# 945, 947
                messages = new ActionMessages();                
                addMessage(messages, abstractDomain, "add", queryBizLogic, objectName);
                
                //Setting the system identifier after inserting the object in the DB.
                if (abstractDomain.getId() != null)
                {
                    abstractForm.setId(abstractDomain.getId().longValue());
                    request.setAttribute(Constants.SYSTEM_IDENTIFIER, abstractDomain.getId());
                    Logger.out.debug("New id in CommonAddEditAction===>"+abstractDomain.getId());
                    abstractForm.setMutable(false);
                }
                
                //Attributes to decide AddNew action
                String submittedFor = (String) request.getParameter(Constants.SUBMITTED_FOR);
                
                Logger.out.debug("Checking parameter SubmittedFor in CommonAddEditAction--->"+request.getParameter(Constants.SUBMITTED_FOR));
                Logger.out.debug("SubmittedFor attribute of Form-Bean received---->"+abstractForm.getSubmittedFor());
                
                //------------------------------------------------ AddNewAction Starts----------------------------
                //if AddNew action is executing, load FormBean from Session and redirect to Action which initiated AddNew action
                if( (submittedFor !=null)&& (submittedFor.equals("AddNew")) )
                {
                    Logger.out.debug("SubmittedFor is AddNew in CommonAddEditAction...................");
                    
                    HttpSession session = request.getSession();
                    Stack formBeanStack = (Stack)session.getAttribute(Constants.FORM_BEAN_STACK);
                    
        	        if(formBeanStack !=null)
        	        {
        	            //Retrieving AddNewSessionDataBean from Stack
        	            AddNewSessionDataBean addNewSessionDataBean = (AddNewSessionDataBean)formBeanStack.pop();
        	            
        	            if(addNewSessionDataBean != null)
        	            {
	        	            //Retrieving FormBean stored into AddNewSessionDataBean 
	        	            AbstractActionForm sessionFormBean = addNewSessionDataBean.getAbstractActionForm();
	        	            
	        	            String forwardTo = addNewSessionDataBean.getForwardTo();
	        	            Logger.out.debug("forwardTo in CommonAddEditAction--------->"+forwardTo);
	        	            
	        	            //Setting Identifier of new object into the FormBean to populate it on the JSP page 
	        	            sessionFormBean.setAddNewObjectIdentifier(addNewSessionDataBean.getAddNewFor(), abstractDomain.getId());
	        	            
	        	            sessionFormBean.setMutable(false);
	        	            
	        	            //cleaning FORM_BEAN_STACK from Session if no AddNewSessionDataBean available... Storing appropriate value of SUBMITTED_FOR attribute
	        	            if(formBeanStack.isEmpty())
	        	            {
	        	                session.removeAttribute(Constants.FORM_BEAN_STACK);
	        	                request.setAttribute(Constants.SUBMITTED_FOR, "Default");
	        	                Logger.out.debug("SubmittedFor set as Default in CommonAddEditAction===========");
	        	                
	        	                Logger.out.debug("cleaning FormBeanStack from session*************");
	        	            }
	        	            else
	        	            {
	        	                request.setAttribute(Constants.SUBMITTED_FOR, "AddNew");
	        	            }
	        	            
	        	            //Storing FormBean into Request to populate data on the page being forwarded after AddNew activity, 
	        	            //FormBean should be stored with the name defined into Struts-Config.xml to populate data properly on JSP page 
	        	            String formBeanName = Utility.getFormBeanName(sessionFormBean);
	        	            request.setAttribute(formBeanName, sessionFormBean);
	        	            
	        	            Logger.out.debug("InitiliazeAction operation=========>"+sessionFormBean.getOperation());
	        	            
	        	            //Storing Success messages into Request to display on JSP page being forwarded after AddNew activity
	        	            if (messages != null)
		                    {
		                        saveMessages(request,messages);
		                    }
		                    
	        	            //Status message key.
		                    String statusMessageKey = String.valueOf(abstractForm.getFormId() +
		        					"."+String.valueOf(abstractForm.isAddOperation()));
		                    request.setAttribute(Constants.STATUS_MESSAGE_KEY,statusMessageKey);
		                    
	        	            //Changing operation attribute in parth specified in ForwardTo mapping, If AddNew activity started from Edit page
	        	            if( (sessionFormBean.getOperation().equals("edit") ) )
	        	            {
	        	                Logger.out.debug("Edit object Identifier while AddNew is from Edit operation==>"+sessionFormBean.getId());
	        	                ActionForward editForward = new ActionForward();
	        	                
	        	                String addPath = (mapping.findForward(forwardTo)).getPath();
	        	                Logger.out.debug("Operation before edit==========>"+addPath);
	        	                
	        	                String editPath = addPath.replaceFirst("operation=add","operation=edit");
	        	                Logger.out.debug("Operation edited=============>"+editPath);
	                       		editForward.setPath(editPath);
	                       		
	                       		return editForward;
	        	            }   
	        	            
	        	            return (mapping.findForward(forwardTo));
        	            }
        	            //Setting target as FAILURE if AddNewSessionDataBean is null
        	            else
        	            {
        	                target = new String(Constants.FAILURE);
                            
                            ActionErrors errors = new ActionErrors();
                        	ActionError error = new ActionError("errors.item.unknown",
                        	        				AbstractDomainObject.parseClassName(objectName));
                        	errors.add(ActionErrors.GLOBAL_ERROR,error);
                        	saveErrors(request,errors);
        	            }
        	        }
                }
                //------------------------------------------------ AddNewAction Ends----------------------------                
               //----------ForwardTo Starts----------------
 	           else if( (submittedFor !=null)&& (submittedFor.equals("ForwardTo")) )
 	           {
 	               Logger.out.debug("SubmittedFor is ForwardTo in CommonAddEditAction...................");
 	               
 	               //Storing appropriate value of SUBMITTED_FOR attribute
 	               request.setAttribute(Constants.SUBMITTED_FOR, "Default");

 	               //storing HashMap of forwardTo data into Request
 	               request.setAttribute("forwardToHashMap", generateForwardToHashMap(abstractForm, abstractDomain));
 	           }
 	           //----------ForwardTo Ends----------------
 	           
 	           //setting target to ForwardTo attribute of submitted Form 
 	           if(abstractForm.getForwardTo()!= null && abstractForm.getForwardTo().trim().length()>0  )
	           {
	           		String forwardTo = abstractForm.getForwardTo(); 
	           		Logger.out.debug("ForwardTo in Add :-- : "+ forwardTo);
	           		target = forwardTo;
	           		//return (mapping.findForward(forwardTo));
	           }
	               Logger.out.debug("Target in CommonAddEditAction===> "+ target); 
            }
            else
            {
                target = new String(Constants.SUCCESS);
                
            	//If operation is edit, update the data in the database.
                
                List list = bizLogic.retrieve(objectName, Constants.SYSTEM_IDENTIFIER,
										  new Long(abstractForm.getId()));
                if (!list.isEmpty())
                {
                    List listOld = bizLogic.retrieve(objectName, Constants.SYSTEM_IDENTIFIER,
							  new Long(abstractForm.getId()));

                    AbstractDomainObject abstractDomainOld = (AbstractDomainObject) listOld.get(0);
                    
                    
                	abstractDomain = (AbstractDomainObject) list.get(0);
                    abstractDomain.setAllValues(abstractForm);
                    
                    
                    bizLogic.update(abstractDomain, abstractDomainOld, Constants.HIBERNATE_DAO, getSessionData(request));
                                     
                    // -- Direct to Main Menu if record is disabled
                    if((abstractForm.getActivityStatus() != null) &&
                            (Constants.ACTIVITY_STATUS_DISABLED.equals(abstractForm.getActivityStatus())))
                    {
                    	String moveTo = abstractForm.getOnSubmit(); 
                    	Logger.out.debug("MoveTo in Disabled :-- : "+ moveTo);
                   		ActionForward reDirectForward = new ActionForward();
                   		reDirectForward.setPath(moveTo );
                   		return reDirectForward;
                    }
                    
                    // OnSubmit
                    if(abstractForm.getOnSubmit()!= null && abstractForm.getOnSubmit().trim().length()>0  )
                    {
                    	String forwardTo = abstractForm.getOnSubmit(); 
                    	Logger.out.debug("OnSubmit :-- : "+ forwardTo);
                        return (mapping.findForward(forwardTo));
                    }

                    String submittedFor = (String) request.getParameter(Constants.SUBMITTED_FOR);
                    Logger.out.debug("Submitted for in Edit CommonAddEditAction===>" + submittedFor);
                    
                    //----------ForwardTo Starts----------------
                    if( (submittedFor !=null)&& (submittedFor.equals("ForwardTo")) )
      	            {
                        Logger.out.debug("SubmittedFor is ForwardTo in CommonAddEditAction...................");
                        
                        //Storing appropriate value of SUBMITTED_FOR attribute
      	                request.setAttribute(Constants.SUBMITTED_FOR, "Default");
      	                
      	                //storing HashMap of forwardTo data into Request
      	                request.setAttribute("forwardToHashMap", generateForwardToHashMap(abstractForm, abstractDomain));
      	            }
                    //----------ForwardTo Ends----------------

                    //setting target to ForwardTo attribute of submitted Form
                    if(abstractForm.getForwardTo()!= null && abstractForm.getForwardTo().trim().length()>0  )
  		            {
  		           	    String forwardTo = abstractForm.getForwardTo(); 
  		           		Logger.out.debug("ForwardTo in Edit :-- : "+ forwardTo);
  		           		form=null;
  		           		target = forwardTo;
  		           		//return (mapping.findForward(forwardTo));
  		            }
                    
                    // Forward the page to edit success in the Advance query search if the edit is through Object view of Advance Search
                   String pageOf = (String)request.getParameter(Constants.PAGEOF);
                   Logger.out.debug("pageof for query edit=="+pageOf);
                   if(pageOf != null)
                   {
                   	if(pageOf.equals(Constants.QUERY))
                   	{
                   		target = pageOf;
                   	}
                   }
                   
                   
                   // The successful edit message. Changes done according to bug# 945, 947
                   messages = new ActionMessages();
                   addMessage(messages, abstractDomain, "edit", queryBizLogic, objectName);
                }
                else
                {
                    target = new String(Constants.FAILURE);
                    
                    ActionErrors errors = new ActionErrors();
                	ActionError error = new ActionError("errors.item.unknown",
                	        				AbstractDomainObject.parseClassName(objectName));
                	errors.add(ActionErrors.GLOBAL_ERROR,error);
                	saveErrors(request,errors);
                }

            }
            
            if (messages != null)
            {
                saveMessages(request,messages);
            }
            
            //Status message key.
            String statusMessageKey = String.valueOf(abstractForm.getFormId() +
					"."+String.valueOf(abstractForm.isAddOperation()));
            
            request.setAttribute(Constants.STATUS_MESSAGE_KEY,statusMessageKey);
        }
        catch (BizLogicException excp)
        {
        	ActionErrors errors = new ActionErrors();
        	ActionError error = new ActionError("errors.item",excp.getMessage());
        	errors.add(ActionErrors.GLOBAL_ERROR,error);
        	saveErrors(request,errors);
            target = new String(Constants.FAILURE);
            
            Logger.out.error(excp.getMessage(), excp);
        }
        catch (DAOException excp)
        {
            target = new String(Constants.FAILURE);
            Logger.out.debug("excp "+excp.getMessage());
            Logger.out.error(excp.getMessage(), excp);
        }
        catch (UserNotAuthorizedException excp)
        {
            
            ActionErrors errors = new ActionErrors();
            SessionDataBean sessionDataBean =getSessionData(request);
            String userName;
        	if(sessionDataBean == null)
        	{
        	    userName = "";
        	}
        	else
        	{
        	    userName = sessionDataBean.getUserName();
        	}
        	ActionError error = new ActionError("access.addedit.object.denied",userName,abstractDomain.getClass().getName());
        	errors.add(ActionErrors.GLOBAL_ERROR,error);
        	saveErrors(request,errors);
        	target = new String(Constants.FAILURE);
            Logger.out.debug("excp "+excp.getMessage());
            Logger.out.error(excp.getMessage(), excp);
        }
        catch (AssignDataException excp)
        {
            target = new String(Constants.FAILURE);
            Logger.out.debug("excp "+excp.getMessage());
            Logger.out.error(excp.getMessage(), excp);
        }
        
        Logger.out.debug("target....................."+target); 
        return (mapping.findForward(target));
    }
    
    protected SessionDataBean getSessionData(HttpServletRequest request) {
		Object obj = request.getSession().getAttribute(Constants.SESSION_DATA);
		 /**
		  *  This if loop is specific to Password Security feature.
		  */
		if(obj == null)
		{
			obj = request.getSession().getAttribute(Constants.TEMP_SESSION_DATA);
		}
		if(obj!=null)
		{
			SessionDataBean sessionData = (SessionDataBean) obj;
			return  sessionData;
		} 
		return null;
		//return (String) request.getSession().getAttribute(Constants.SESSION_DATA);
	}
    
    /**
     * This method generates HashMap of data required to be forwarded if Form is submitted for ForwardTo request
     * @param abstractForm	Form submitted
     * @param abstractDomain	DomainObject Added/Edited
     * @return	HashMap of data required to be forwarded
     */
    private HashMap generateForwardToHashMap(AbstractActionForm abstractForm, AbstractDomainObject abstractDomain)throws BizLogicException
    {
        HashMap forwardToHashMap;
        //getting instance of ForwardToProcessor
//	        AbstractForwardToProcessor forwardToProcessor= ForwardToFactory.getForwardToProcessor();
        
        AbstractForwardToProcessor forwardToProcessor=AbstractForwardToFactory.getForwardToProcessor(
					ApplicationProperties.getValue("app.forwardToFactory"),
					"getForwardToProcessor");
        
        //Populating HashMap of the data required to be forwarded on next page
        forwardToHashMap = (HashMap)forwardToProcessor.populateForwardToData(abstractForm,abstractDomain);

        return forwardToHashMap;
    }
    
    /**
     * This method will add the success message into ActionMessages object
     * @param messages ActionMessages
     * @param abstractDomain AbstractDomainObject
     * @param addoredit String
     * @param queryBizLogic QueryBizLogic
     * @param objectName String
     */
    private void addMessage(ActionMessages messages, AbstractDomainObject abstractDomain, String addoredit, QueryBizLogic queryBizLogic, String objectName) {
    	String message = abstractDomain.getMessageLabel();
    	Validator validator = new Validator();
    	boolean isEmpty = validator.isEmpty(message);
    	String displayName = null;
    	try
		{
    		displayName = queryBizLogic.getDisplayNamebyTableName(HibernateMetaData.getTableName(abstractDomain.getClass()));
		} 
    	catch(Exception excp)
		{
    		displayName = AbstractDomainObject.parseClassName(objectName);
    		Logger.out.error(excp.getMessage(), excp);
		}
    	
    	if (!isEmpty)    	{
        	messages.add(ActionErrors.GLOBAL_MESSAGE,new ActionMessage("object." + addoredit + ".success", displayName, message));
    	}
    	else
    	{
    		messages.add(ActionErrors.GLOBAL_MESSAGE,new ActionMessage("object." + addoredit + ".successOnly", displayName));
    	} 
    }
    
}