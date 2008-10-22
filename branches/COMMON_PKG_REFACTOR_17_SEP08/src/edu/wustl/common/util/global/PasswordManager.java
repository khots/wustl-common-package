/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright: (c) Washington University, School of Medicine 2004</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *@author Aarti Sharma
 *@version 1.0
 */

package edu.wustl.common.util.global;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpSession;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.security.exceptions.PasswordEncryptionException;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.logger.Logger;
import gov.nih.nci.security.util.StringEncrypter;
import gov.nih.nci.security.util.StringEncrypter.EncryptionException;

/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright: (c) Washington University, School of Medicine 2005</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *@author Aarti Sharma
 *@version 1.0
 */

public class PasswordManager
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(PasswordManager.class);
	/**
	 * specify Success.
	 */
	public static final int SUCCESS = 0;
	/**
	 * specify fail password length.
	 */
	public static final int FAIL_LENGTH = 1;
	/**
	 * specify fail password same as old.
	 */
	public static final int FAIL_SAME_AS_OLD = 2;
	/**
	 * specify fail password same as username.
	 */
	public static final int FAIL_SAME_AS_USERNAME = 3;
	/**
	 * specify fail password in pattern.
	 */
	public static final int FAIL_IN_PATTERN = 4;
	/**
	 * specify fail same session.
	 */
	public static final int FAIL_SAME_SESSION = 5;
	/**
	 * specify fail wrong old password.
	 */
	public static final int FAIL_WRONG_OLD_PASSWORD = 6;
	/**
	 *  specify fail invalid session.
	 */
	public static final int FAIL_INVALID_SESSION = 7;
	/**
	 * This map contains error message for different error code.
	 */
	private static Map<Integer,String> errorMess;
	static
	{
		errorMess= new HashMap<Integer,String>();
		int minimumPasswordLength = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.MINIMUM_PASSWORD_LENGTH));
		List<String> placeHolders = new ArrayList<String>();
		placeHolders.add(Integer.valueOf(minimumPasswordLength).toString());
		String errorMsg = ApplicationProperties.getValue("errors.newPassword.length", placeHolders);
		errorMess.put(FAIL_LENGTH, errorMsg);
		errorMess.put(FAIL_SAME_AS_OLD, ApplicationProperties.getValue("errors.newPassword.sameAsOld"));
		errorMess.put(FAIL_SAME_AS_USERNAME,
				ApplicationProperties.getValue("errors.newPassword.sameAsUserName"));
		errorMess.put(FAIL_IN_PATTERN, ApplicationProperties.getValue("errors.newPassword.pattern"));
		errorMess.put(FAIL_SAME_SESSION, ApplicationProperties.getValue("errors.newPassword.sameSession"));
		errorMess.put(FAIL_WRONG_OLD_PASSWORD, ApplicationProperties.getValue("errors.oldPassword.wrong"));
		errorMess.put(FAIL_INVALID_SESSION,
				ApplicationProperties.getValue("errors.newPassword.genericmessage"));
	}
	/**
	 * Generate random alpha numeric password.
	 * @return Returns the generated password.
	 */
	public static String generatePassword()
	{
		//Define a Constants alpha-numeric String
		final String upperCharString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final String lowerCharString = "abcdefghijklmnopqrstuvwxyz";
		final String digitString = "0123456789";

		// Generate password of length 6
		//final int PASSWORD_LENGTH = 6;

		Random random = new Random();
		StringBuffer passwordBuff = new StringBuffer();
		//This password must satisfy the following criteria:New Password must include at least one
		//Upper Case, Lower Case letter and a Number. It must not include Space.
		for (int i = 0; i < 2; i++)
		{
			//Generate a random number from 0(inclusive) to lenght of CHAR_STRING(exclusive).
			//Get the character corrosponding to random number and append it to password buffer.
			int randomVal = random.nextInt(upperCharString.length());
			passwordBuff.append(upperCharString.charAt(randomVal));
			randomVal = random.nextInt(lowerCharString.length());
			passwordBuff.append(lowerCharString.charAt(randomVal));
			randomVal = random.nextInt(digitString.length());
			passwordBuff.append(digitString.charAt(randomVal));
		}
		return passwordBuff.toString();
	}

	/**
	 * specify stringEncrypter.
	 */
	private static StringEncrypter stringEncrypter = null;

	/**
	 * TO get the instance of StringEncrypter class.
	 * @return The Object reference of StringEncrypter class.
	 * @throws EncryptionException generic EncryptionException
	 */
	private static StringEncrypter getEncrypter() throws EncryptionException
	{
		if (stringEncrypter == null)
		{
			stringEncrypter = new StringEncrypter();
		}
		return stringEncrypter;
	}

	/**
	 * TO get Encrypted password for the given password.
	 * @param password The password to be encrypted.
	 * @return The Encrypted password for the given password.
	 * @throws PasswordEncryptionException generic PasswordEncryptionException
	 */
	public static String encrypt(String password) throws PasswordEncryptionException
	{
		try
		{
			return getEncrypter().encrypt(password);
		}
		catch (EncryptionException e)
		{
			throw new PasswordEncryptionException(e.getMessage(), e);
		}
	}

	/**
	 * TO get Decrypted password for the given password.
	 * @param password The password to be Decrypted.
	 * @return The Decrypted password for the given password.
	 * @throws PasswordEncryptionException generic PasswordEncryptionException
	 */
	public static String decrypt(String password) throws PasswordEncryptionException
	{
		try
		{
			return getEncrypter().decrypt(password);
		}
		catch (EncryptionException e)
		{
			throw new PasswordEncryptionException(e.getMessage(), e);
		}
	}

	/**
	 * This method encode input string.
	 * @param input string
	 * @return string
	 */
	@Deprecated
	public static String encode(String input)
	{
		String encodedString = null;
		char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
				'f'};
		StringBuffer key = new StringBuffer
("AWelcomeTocaTISSUECOREOfThisIsTheFirstReleaseOfcaTISSUECOREDevelopedByWashUAtPersistentSystemsPrivateLimited");
		StringBuffer inString = new StringBuffer();
		for (int i = 0; i < input.length(); i++)
		{
			inString.append(input.substring(i, i + 1));
			inString.append(key.substring(i, i + 1));
		}

		try
		{
			byte[] bytes = inString.toString().getBytes();
			StringBuffer stringBuffer = new StringBuffer(bytes.length * 2);

			for (int i = 0; i < bytes.length; i++)
			{
				byte singleByte = bytes[i];
				stringBuffer.append(digits[(singleByte & 0xf0) >> 4]);
				stringBuffer.append(digits[singleByte & 0x0f]);
			}

			encodedString = stringBuffer.toString();
		}
		catch (Exception exception)
		{
			logger.warn("Problems in Encryption/Decryption in CommonJdao ",exception);
		}
		return encodedString;
	}

	/**
	 * This method decode string.
	 * @param decodeString string to decode
	 * @return  string
	 */
	@Deprecated
	public static String decode(String decodeString)
	{
		String decodedString = null;
		try
		{
			byte[] bytes = getStringAsBytes(decodeString);
			String sin = new String(bytes);
			StringBuffer sout = new StringBuffer();
			for (int i = 0; i < sin.length(); i += 2)
			{
				sout.append(sin.substring(i, i + 1));
			}
			decodedString = sout.toString();
		}
		catch (Exception exeption)
		{
			logger.warn("Problems in Decription/Encription",exeption);
		}
		return decodedString;
	}

	/**
	 * @param decodeString String to be decoded.
	 * @return string as byte array.
	 */
	private static byte[] getStringAsBytes(String decodeString)
	{
		int len = decodeString.length();
		byte[] bytes = new byte[len / 2];
		for (int i = 0; i < bytes.length; i++)
		{
			int digit1 = decodeString.charAt(i * 2);
			int digit2 = decodeString.charAt(i * 2 + 1);
			digit1 = getDigit(digit1);
			digit2 = getDigit(digit2);
			bytes[i] = (byte) ((digit1 << 4) + digit2);
		}
		return bytes;
	}

	/**
	 * @param digit digit to be encoded.
	 * @return encoded digit.
	 */
	private static int getDigit(int digit)
	{
		int encodedDigit=digit;
		if ((encodedDigit >= '0') && (encodedDigit <= '9'))
		{
			encodedDigit -= '0';
		}
		else if ((encodedDigit >= 'a') && (encodedDigit <= 'f'))
		{
			encodedDigit -= 'a' - 10;
		}
		return encodedDigit;
	}

	/**
	 * This method returns the validation results on Form Bean. This method should be called
	 * from validate method of form bean.
	 * @param newPassword New Password value
	 * @param oldPassword Old Password value
	 * @param httpSession HttpSession object
	 * @return SUCCESS if all condition passed
	 *   else return respective error code (constant int) value
	 */
	public static int validatePasswordOnFormBean(String newPassword, String oldPassword,
			HttpSession httpSession)
	{

		// to check whether password change in same session
		// get attribute (Boolean) from session object stored when password is changed successfully
		Boolean passwordChangedInsameSession = (Boolean) httpSession
				.getAttribute(Constants.PASSWORD_CHANGE_IN_SESSION);
		if (passwordChangedInsameSession != null
				&& passwordChangedInsameSession.booleanValue())
		{
			// return error code if attribute (Boolean) is in session
			logger.debug("Attempt to change Password in same session Returning FAIL_SAME_SESSION");
			return FAIL_SAME_SESSION;
		}
		int minimumPasswordLength = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.MINIMUM_PASSWORD_LENGTH));
		// to Check length of password,if not valid return FAIL_LENGTH
		if (newPassword.length() < minimumPasswordLength)
		{
			logger.debug("Password is not valid returning FAIL_LENGHT");
			return FAIL_LENGTH;
		}

		// to Check new password is different as old password ,if bot are same return FAIL_SAME_AS_OLD
		if (newPassword.equals(oldPassword))
		{
			logger.debug("Password is not valid returning FAIL_SAME_AS_OLD");
			return FAIL_SAME_AS_OLD;
		}

		/**
		 * following code checks pattern i.e password must include atleast one UCase,LCASE and Number
		 * and must not contain space charecter.
		 */
		char [] dest = new char[newPassword.length()];
		// get char array where values get stores in dest[]
		newPassword.getChars(0, newPassword.length(), dest, 0);
		boolean foundUCase = false; // boolean to check UCase character found in string
		boolean foundLCase = false; // boolean to check LCase character found in string
		boolean foundNumber = false; // boolean to check Digit/Number character found in string
		boolean foundSpace = false; // boolean to check space in String

		for (int i = 0; i < dest.length; i++)
		{
			// to check if character is a Space. if true break from loop
			if (Character.isSpaceChar(dest[i]))
			{
				foundSpace = true;
				break;
			}
			// to check whether char is Upper Case.
			if (!foundUCase  && Character.isUpperCase(dest[i]))
			{
				foundUCase = true;
			}

			// to check whether char is Lower Case
			if (!foundLCase && Character.isLowerCase(dest[i]))
			{
				foundLCase = true;
			}

			// to check whether char is Number/Digit
			if (!foundNumber && Character.isDigit(dest[i]))
			{
				foundNumber = true;
			}
		}
		// condition to check whether all above condotion is satisfied
		if (!foundUCase || !foundLCase || !foundNumber || foundSpace)
		{
			logger.debug("Password is not valid returning FAIL_IN_PATTERN");
			return FAIL_IN_PATTERN;
		}
		logger.debug("Password is Valid returning SUCCESS");
		return SUCCESS;
	}

	/**
	 * TODO Remove this method.
	 * This method combines UI validation and business rules validation which is incorrect.
	 * Call validatePasswordOnFormBean for Form bean validations.
	 * Write your own methods for business validations.
	 * @param newPassword New Password value
	 * @param oldPassword Old Password value
	 * @param httpSession HttpSession object
	 * @return SUCCESS (constant int 0) if all condition passed
	 *   else return respective error code (constant int) value
	 */
	public static int validate(String newPassword, String oldPassword, HttpSession httpSession)
	{
		// get SessionDataBean objet from session
		Object obj = httpSession.getAttribute(Constants.SESSION_DATA);
		SessionDataBean sessionData = null;
		String userName = "";
		if (obj == null)
		{
			return FAIL_INVALID_SESSION;
		}
		else
		{
			sessionData = (SessionDataBean) obj;
			userName = sessionData.getUserName();
		}
		// to check whether user entered correct old password

		try
		{
			// retrieve User DomainObject by user name
			IBizLogic bizLogic = new DefaultBizLogic();
			String[] selectColumnNames = {"password"};
			String[] whereColumnNames = {"loginName"};
			String[] whereColumnCondition = {"="};
			String[] whereColumnValues = {userName};

			//Gautam_COMMON_TEMP_FIX USER_CLASS_NAME
			List userList = bizLogic.retrieve(Constants.USER_CLASS_NAME, selectColumnNames,
					whereColumnNames, whereColumnCondition, whereColumnValues, null);
			String password = null;
			if (userList != null && !userList.isEmpty())
			{
				password = (String) userList.get(0);
			}

			// compare password stored in database with value of old password currently entered by
			// user for Change Password operation
			if (!oldPassword.equals(PasswordManager.decode(password)))
			{
				return FAIL_WRONG_OLD_PASSWORD; //retun value is int 6
			}
		}
		catch (Exception e)
		{
			// if error occured during password comparision
			logger.error(e.getMessage(), e);
			return FAIL_WRONG_OLD_PASSWORD;
		}

		// to check whether password change in same session
		// get attribute (Boolean) from session object stored when password is changed successfully
		Boolean passwordChange = null;
		passwordChange = (Boolean) httpSession.getAttribute(Constants.PASSWORD_CHANGE_IN_SESSION);
		logger.debug("passwordChange---" + passwordChange);
		if (passwordChange != null && passwordChange.booleanValue())
		{
			// return error code if attribute (Boolean) is in session
			logger.debug("Attempt to change Password in same session Returning FAIL_SAME_SESSION");
			return FAIL_SAME_SESSION; // return int value 5
		}
		int minimumPasswordLength = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.MINIMUM_PASSWORD_LENGTH));
		// to Check length of password,if not valid return FAIL_LENGTH = 2
		if (newPassword.length() < minimumPasswordLength)
		{
			logger.debug("Password is not valid returning FAIL_LENGHT");
			return FAIL_LENGTH; // return int value 1
		}

		// to Check new password is different as old password ,if bot are same return FAIL_SAME_AS_OLD = 3
		if (newPassword.equals(oldPassword))
		{
			logger.debug("Password is not valid returning FAIL_SAME_AS_OLD");
			return FAIL_SAME_AS_OLD; //return int value 2
		}

		// to check password is differnt than user name if same return FAIL_SAME_AS_USERNAME =4
		// eg. username=abc@abc.com newpassword=abc is not valid
		int usernameBeforeMailaddress = userName.indexOf('@');
		// get substring of username before '@' character
		String name = userName.substring(0, usernameBeforeMailaddress);
		logger.debug("usernameBeforeMailaddress---" + name);
		if (name != null && newPassword.equals(name))
		{
			logger.debug("Password is not valid returning FAIL_SAME_AS_USERNAME");
			return FAIL_SAME_AS_USERNAME; // return int value 3
		}
		//to check password is differnt than user name if same return FAIL_SAME_AS_USERNAME =4
		// eg. username=abc@abc.com newpassword=abc@abc.com is not valid
		if (newPassword.equals(userName))
		{
			logger.debug("Password is not valid returning FAIL_SAME_AS_USERNAME");
			return FAIL_SAME_AS_USERNAME; // return int value 3
		}

		// following is to checks pattern i.e password must include atleast one UCase,LCASE and Number
		// and must not contain space charecter.
		// define get char array whose length is equal to length of new password string.
		char []dest = new char[newPassword.length()];
		// get char array where values get stores in dest[]
		newPassword.getChars(0, newPassword.length(), dest, 0);
		boolean foundUCase = false; // boolean to check UCase character found in string
		boolean foundLCase = false; // boolean to check LCase character found in string
		boolean foundNumber = false; // boolean to check Digit/Number character found in string
		boolean foundSpace = false;

		for (int i = 0; i < dest.length; i++)
		{
			// to check if character is a Space. if true break from loop
			if (Character.isSpaceChar(dest[i]))
			{
				foundSpace = true;
				logger.debug("Found Space in Password");
				break;
			}
			// to check whether char is Upper Case.
			if (!foundUCase && Character.isUpperCase(dest[i]))
			{
				//foundUCase=true if char is Upper Case
				//and Upper Case is not found in previous char.
				foundUCase = true;
				logger.debug("Found UCase in Password");
			}

			// to check whether char is Lower Case
			if (!foundLCase && Character.isLowerCase(dest[i]))
			{
				//foundLCase=true if char is Lower Case
				//and Lower Case is not found in previous char.
				foundLCase = true;
				logger.debug("Found LCase in Password");
			}

			// to check whether char is Number/Digit
			if (!foundNumber && Character.isDigit(dest[i]))
			{
				//	foundNumber=true if char is Digit and Digit is not found in previous char.
				foundNumber = true;
				logger.debug("Found Number in Password");
			}
		}
		// condition to check whether all above condotion is satisfied
		if (!foundUCase || !foundLCase || !foundNumber || foundSpace)
		{
			logger.debug("Password is not valid returning FAIL_IN_PATTERN");
			return FAIL_IN_PATTERN; // return int value 4
		}
		logger.debug("Password is Valid returning SUCCESS");
		return SUCCESS;
	}

	/**
	 * Error message.
	 * @param errorCode int value return by validate() method
	 * @return String error message with respect to error code
	 */
	public static String getErrorMessage(int errorCode)
	{
		String errMsg = errorMess.get(errorCode);
		if(null==errMsg)
		{
			errMsg = ApplicationProperties.getValue("errors.newPassword.genericmessage");
		}
		return errMsg;
	}

	/**
	 *
	 * @param args filename,password.
	 * @throws PasswordEncryptionException generic PasswordEncryptionException
	 */
	public static void main(String[] args) throws PasswordEncryptionException
	{
		String pwd = "admin";
		String encodedPWD = encrypt(pwd);
		if (args.length > 1)
		{
			String filename = args[0];
			String password = args[1];
			encodedPWD = encrypt(password);
			writeToFile(filename, encodedPWD);
		}
	}

	/**
	 * This method writes the encoded password to the file.
	 * @param filename File to be written.
	 * @param encodedPassword Encoded password.
	 */
	private static void writeToFile(String filename, String encodedPassword)
	{
		try
		{
			File fileObject = new File(filename);
			FileWriter writeObject = new FileWriter(fileObject);
			writeObject.write("first.admin.encodedPassword=" + encodedPassword + "\n");
			writeObject.close();

		}
		catch (Exception ioe)
		{
			logger.warn("Problems in writing the encoded password to the file.");
		}
	}
}