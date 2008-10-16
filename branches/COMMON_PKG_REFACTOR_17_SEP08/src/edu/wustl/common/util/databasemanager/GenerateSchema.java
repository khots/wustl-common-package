
package edu.wustl.common.util.databasemanager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/*
 * Created on Feb 20, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

/**
 * @author kapil_kaveeshwar
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class GenerateSchema
{
	/**
	 * Configuring the properties file.
	 */
	/**
	 * @param args String[] arguements.
	 * @throws HibernateException exception of Hibernate.
	 * @throws IOException exception of I/O.
	 * @throws Exception generic exception.
	 */
	public static void main(String[] args) throws HibernateException, IOException, Exception
	{
		boolean isToPrint = false;
		boolean isToExecuteOnDB = false;
		if (args.length != 0)
		{
			String arg = args[0];
			if (arg.equalsIgnoreCase("true"))
			{
				isToPrint = true;
				isToExecuteOnDB = true;
			}
			if (arg.equalsIgnoreCase("false"))
			{
				isToPrint = false;
				isToExecuteOnDB = false;
			}
		}

		File file = new File("db.properties");
		BufferedInputStream stram = new BufferedInputStream(new FileInputStream(file));
		Properties prop = new Properties();
		prop.load(stram);
		stram.close();

		Configuration cfg = new Configuration();
		cfg.setProperties(prop);
		cfg.addDirectory(new File("./src"));
		new SchemaExport(cfg).setOutputFile("query.sql").setDelimiter(";").create(
				isToPrint, isToExecuteOnDB);
	}
}