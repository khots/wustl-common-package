
package edu.wustl.common.audit.util;

import java.io.InputStream;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;

import edu.wustl.common.audit.AuditableMetaData;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;

/**
 * This class reads the Castor mappings present in the host application to read a list of the objects
 * to be audited and also their attributes to be audited.
 * @author niharika_sharma
 */
public class AuditableMetadataUtil
{

	/**
	 * LOGGER Logger - Generic LOGGER.
	 */
	private static final Logger logger = Logger
			.getCommonLogger(AuditableMetadataUtil.class);
	/**
	 * Single instance of the AuditableMetaData class.
	 */
	private static AuditableMetaData metadata = null;

	/**
	 * Private method to initialize the AuditableMetaData instance.
	 * @throws AuditException  Exception while auditing.
	 */
	private static void init() throws AuditException
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
			logger.error(e.getMessage(), e);
			throw new AuditException(ErrorKey.getErrorKey("problem.parsing.xml"),null, "");
		}
	}

	/**
	 * Public constructor that initializes the AuditableMetaData instance.
	 *  in case it is not already initialized.
	 * @throws AuditException  Exception while auditing.
	 */
	public AuditableMetadataUtil() throws AuditException
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
