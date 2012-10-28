
package edu.wustl.common.tags.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.tags.bizlogic.ITagBizlogic;
import edu.wustl.common.tokenprocessor.PropertyHandler;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

public class TagBizlogicFactory
{

	/** Logger object. */
	private static final Logger LOGGER = Logger.getCommonLogger(TagBizlogicFactory.class);

	/**
	 * Private constructor.
	 */
	private TagBizlogicFactory()
	{

	}
	private static Properties tagProperties = null;
	/** Singleton instance of Tag and TagItem. */
	public static Map<String, Object> tagMap = new HashMap<String, Object>();

	public static synchronized ITagBizlogic getBizLogicInstance(String entityType) throws ApplicationException
	{
		try
		{
			Class bizlogicClass = (Class)tagMap.get(entityType);
			if (bizlogicClass == null) {
				String className = getValue(entityType);
				if (Validator.isEmpty(className)) {
					
				}
				bizlogicClass = (Class)Class.forName(className);
				tagMap.put(entityType, bizlogicClass);
			}
			return (ITagBizlogic)bizlogicClass.newInstance();
		} catch (final Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new ApplicationException(null, e, "Could not create BizLogic instance: "
					+ e.getMessage());
		}
	}

	public static synchronized String getValue(String propertyName) throws Exception
	{

		if (tagProperties == null)
		{
			init(Constants.TAG_FOLDER_PROP_FILE_NAME);
		}
		return (String) tagProperties.get(propertyName);

	}
	public static void init(String path) throws Exception
	{
		final String absolutePath = CommonServiceLocator.getInstance().getPropDirPath()
				+ File.separator + path;
		final InputStream inputStream = new FileInputStream(new File(absolutePath));
		tagProperties = new Properties();
		tagProperties.load(inputStream);
	}
 
	 
}
