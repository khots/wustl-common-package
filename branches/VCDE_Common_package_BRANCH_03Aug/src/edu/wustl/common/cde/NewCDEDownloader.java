/*
 * Created on Mar 9, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.wustl.common.cde;

import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.cadsr.umlproject.domain.UMLAttributeMetadata;
import gov.nih.nci.cadsr.umlproject.domain.UMLClassMetadata;
import gov.nih.nci.cadsr.umlproject.domain.impl.UMLClassMetadataImpl;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;
import java.util.Iterator;
import java.util.List;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
/**
 * @author Jane Jiang <a href="mailto: jane.jiang@oracle.com"></a>
 * @version 1.0
 */
/**
 * TestClient.java demonstartes various ways to execute searches with and without
 * using Application Service Layer (convenience layer that abstracts building
criteria
 * Uncomment different scenarios below to demonstrate the various types of searches
 */
/**
 * @author poornima_govindrao
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NewCDEDownloader
{

	public static void main(String[] args)
	{
		Project project = null;
		System.out.println("*** TestUml...");
		try {
			ApplicationService appService =	ApplicationService.getRemoteInstance("http://cabio.nci.nih.gov/cacore32/http/remoteService");
			System.out.println("Using basic search. Retrieving all projects");
//			DetachedCriteria projectCriteria = DetachedCriteria.forClass(Project.class);
//			projectCriteria.addOrder(Order.asc("shortName"));
			try 
			{
//				System.out.println("Scenario 1: Using basic search. Retrieving all projects, display version and context information...");
//				List<Project> resultList = appService.query(projectCriteria, Project.class.getName());
//				System.out.println(resultList.size() + " projects retrieved..");
//				for (Iterator resultsIterator = resultList.iterator();
//				resultsIterator.hasNext(); ) 
//				{
//					project = (Project)resultsIterator.next();
//					System.out.println("Project name: " + project.getShortName());
//					System.out.println(" version: " + project.getVersion());
//					System.out.println(" context: " + project.getClassificationScheme().getContext().getName());
//				}
//				System.out.println();
				System.out.println("Scenario 2: Retrieving class named Gene, display class information");
				UMLClassMetadata umlClass = new UMLClassMetadataImpl();
				umlClass.setName("gene");
				List resultList = appService.search(UMLClassMetadata.class, umlClass);
				System.out.println(resultList.size() + " classes retrieved..");
				for (Iterator resultsIterator = resultList.iterator();
				resultsIterator.hasNext(); ) 
				{
					umlClass = (UMLClassMetadata)resultsIterator.next();
					System.out.println(" class full name: " + umlClass.getFullyQualifiedName());
					System.out.println(" class description: " + umlClass.getDescription());
					System.out.println(" project version: " + umlClass.getProject().getVersion());
					System.out.println(" object class public id: " + umlClass.getObjectClass().getPublicID());
				}
				System.out.println();
				System.out.println("Scenario 3: Retrieving attributes for a class, display attribute information");
			
		}
				/*if (umlClass != null) {
	for (Iterator resultsIterator =
	umlClass.getUMLAttributeMetadataCollection().iterator();
	resultsIterator.hasNext(); ) {
	UMLAttributeMetadata umlAttribute =	(UMLAttributeMetadata)resultsIterator.next();
	printAttributeInfo(umlAttribute);
	}
	}
	System.out.println();
	System.out.println("Scenario 4: Retrieving attributes named *id, display attribute information");
	UMLAttributeMetadata umlAttr = new UMLAttributeMetadata();
	umlAttr.setName("*:id");
	resultList = appService.search(UMLAttributeMetadata.class, umlAttr);
	System.out.println(resultList.size() + " attributes retrieved..");
	} catch (Exception e) {
	e.printStackTrace();
	}
	} catch (RuntimeException e2) {
	e2.printStackTrace();
	}
	}
	private static void printAttributeInfo(UMLAttributeMetadata umlAttribute) {
	System.out.println(" Attribute name: " + umlAttribute.getName());
	System.out
	.println(" Attribute type: " + umlAttribute.getAttributeTypeMetadata()
	.getValueDomainDataType());
	System.out
	.println(" Data Element public id: " + umlAttribute.getDataElement()
	.getPublicID());
	}*/
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		} catch (RuntimeException e2) {
			e2.printStackTrace();
			}
			}	
	}
		
