
package edu.wustl.common.audit.util;

import java.io.InputStream;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;

import edu.wustl.common.audit.AuditableMetaData;

/**
 * This class reads the Castor mappings present in the host application to read a list of the objects
 * to be audited and also their attributes to be audited.
 * @author niharika_sharma
 */
public class AuditableMetadataUtil
{

	/**
	 * Single instance of the AuditableMetaData class.
	 */
	private static AuditableMetaData metadata = null;

	/**
	 * Private method to initialize the AuditableMetaData instance.
	 */
	private static void init()
	{
		try
		{
			// -- Load a mapping file
			Mapping mapping = new Mapping();
			//InputSource inputSource = new InputSource(AuditableMetadataUtil.
			//class.getResourceAsStream("mapping.xml"));
			mapping.loadMapping(AuditableMetadataUtil.class.getClassLoader().getResource(
					"mapping.xml"));
			Unmarshaller unmarshaller = new Unmarshaller(AuditableMetaData.class);
			unmarshaller.setMapping(mapping);

			// -- Read in the migration.xml using the mapping
			InputStream inputStream = AuditableMetadataUtil.class.getClassLoader()
					.getResourceAsStream("auditablemetadata.xml");
			InputSource inputSource = new InputSource(inputStream);
			//FileReader in = new FileReader("migrationmetadata.xml");
			metadata = (AuditableMetaData) unmarshaller.unmarshal(inputSource);
			//in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Public constructor that initializes the AuditableMetaData instance.
	 *  in case it is not already initialized.
	 */
	public AuditableMetadataUtil()
	{
		if (metadata == null)
		{
			init();
		}
	}

	/**
	 * This method returns the instance of AuditableMetaData.
	 *  which contains all the metadata required.
	 * for auditing the domain objects of the host application.
	 * @return AuditableMetaData object.
	 */
	public AuditableMetaData getAuditableMetaData()
	{
		return metadata;
	}

}
