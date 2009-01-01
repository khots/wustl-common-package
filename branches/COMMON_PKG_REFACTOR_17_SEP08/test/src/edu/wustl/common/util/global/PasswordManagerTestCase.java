package edu.wustl.common.util.global;

import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.exception.PasswordEncryptionException;
import edu.wustl.common.util.logger.Logger;


public class PasswordManagerTestCase extends CommonBaseTestCase
{
	/**
	 * Generic Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(PasswordManagerTestCase.class);

	static
	{
		ApplicationProperties.initBundle("ApplicationResources");
	}
	public void testGeneratePassword()
	{
		String password=PasswordManager.generatePassword();
		assertTrue(password.length()>0);
	}

	public void testEncryptDecrypt()
	{
		String password="password";
		try
		{
			String encryptedPass = PasswordManager.encrypt(password);
			String decryptedPass = PasswordManager.decrypt(encryptedPass);
			assertEquals(password, decryptedPass);
		}
		catch (PasswordEncryptionException exception)
		{
			logger.debug("encrypt-decrypt not successful.",exception);
			fail("encrypt-decrypt not successful.");
		}
	}

	public void testEncodeDecode()
	{
		String string="password";
		try
		{
			String encodedString = PasswordManager.encode(string);
			String decodedString = PasswordManager.decode(encodedString);
			assertEquals(string, decodedString);
		}
		catch (Exception exception)
		{
			logger.debug("Encode-Decode not successful.",exception);
			fail("Encode-Decode not successful.");
		}
	}

	public void testGetErrorMessage()
	{
		try
		{
			String errorMessage = PasswordManager.getErrorMessage(PasswordManager.FAIL_IN_PATTERN);
			assertTrue(errorMessage.length()>0);
		}
		catch (Exception exception)
		{
			logger.debug("Fail in getting error message.",exception);
			fail("Fail in getting error message.");
		}
	}
}
