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
import java.util.regex.Pattern;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.PasswordEncryptionException;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
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

public final class PasswordManager
{

	/**
	 * private constructor.
	 */
	private PasswordManager()
	{

	}

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(PasswordManager.class);
	/**
	 * Constant for TEN.
	 */
	private static final int TEN = 10;
	/**
	 * Constant for TWO.
	 */
	private static final int TWO = 2;
	/**
	 * Constant for FOUR.
	 */
	private static final int FOUR = 4;
	/**
	 * Key used for password encryption.
	 */
	private static final String KEY = "AWelcomeTocaTISSUECOREOfThisIsThe" +
			"FirstReleaseOfcaTISSUECOREDevelopedByWashUAtPersistentSystemsPrivateLimited";

	/**
	 * array used for password encryption.
	 */
	private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
			'b', 'c', 'd', 'e', 'f'};
	/**
	 *  constant for MINIMUM_PASSWORD_LENGTH.
	 */
	public static final String MINIMUM_PASSWORD_LENGTH = "minimumPasswordLength";
	/**
	 *  constant for USER_CLASS_NAME.
	 */
	public static final String USER_CLASS_NAME = "edu.wustl.common.domain.User";
	/**
	 * Code for no error.
	 */
	public static final int NOT_FAILED = -1;
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
	 *  15 in hexa-decimal.
	 */
	public static final int HEX_15 = 0x0f;
	/**
	 *  240 in hexa-decimal.
	 */
	public static final int HEX_240 = 0xf0;
	/**
	 * This map contains error message for different error code.
	 */
	private static Map<Integer, String> errorMess;
	static
	{
		errorMess = new HashMap<Integer, String>();
		int minimumPasswordLength = Integer.parseInt(XMLPropertyHandler
				.getValue(MINIMUM_PASSWORD_LENGTH));
		List<String> placeHolders = new ArrayList<String>();
		placeHolders.add(Integer.valueOf(minimumPasswordLength).toString());
		String errorMsg = ApplicationProperties.getValue("errors.newPassword.length", placeHolders);
		errorMess.put(FAIL_LENGTH, errorMsg);
		errorMess.put(FAIL_SAME_AS_OLD, ApplicationProperties
				.getValue("errors.newPassword.sameAsOld"));
		errorMess.put(FAIL_SAME_AS_USERNAME, ApplicationProperties
				.getValue("errors.newPassword.sameAsUserName"));
		errorMess
				.put(FAIL_IN_PATTERN, ApplicationProperties.
						getValue("errors.newPassword.pattern"));
		errorMess.put(FAIL_SAME_SESSION, ApplicationProperties
				.getValue("errors.newPassword.sameSession"));
		errorMess.put(FAIL_WRONG_OLD_PASSWORD, ApplicationProperties
				.getValue("errors.oldPassword.wrong"));
		errorMess.put(FAIL_INVALID_SESSION, ApplicationProperties
				.getValue("errors.newPassword.genericmessage"));
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

		Random random = new Random();
		StringBuffer passwordBuff = new StringBuffer();
		//This password must satisfy the following criteria:New Password must include at least one
		//Upper Case, Lower Case letter and a Number. It must not include Space.
		for (int i = 0; i < TWO; i++)
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
		StringBuffer inString = getInString(input);

		try
		{
			StringBuffer stringBuffer = getEncodedString(inString);
			encodedString = stringBuffer.toString();
		}
		catch (Exception exception)
		{
			logger.warn("Problems in Encryption/Decryption in CommonJdao ", exception);
		}
		return encodedString;
	}

	/**
	 * @param inString a string for encryption.
	 * @return Encoded string.
	 */
	private static StringBuffer getEncodedString(StringBuffer inString)
	{
		byte[] bytes = inString.toString().getBytes();
		StringBuffer stringBuffer = new StringBuffer(bytes.length * TWO);

		for (int i = 0; i < bytes.length; i++)
		{
			byte singleByte = bytes[i];
			stringBuffer.append(DIGITS[(singleByte & HEX_240) >> FOUR]);
			stringBuffer.append(DIGITS[singleByte & HEX_15]);
		}
		return stringBuffer;
	}

	/**
	 * Create a string for encryption.
	 * @param input input-string.
	 * @return a string for encryption.
	 */
	private static StringBuffer getInString(String input)
	{
		StringBuffer inString = new StringBuffer();
		for (int i = 0; i < input.length(); i++)
		{
			inString.append(input.charAt(i));
			inString.append(KEY.charAt(i));
		}
		return inString;
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
			for (int i = 0; i < sin.length(); i += TWO)
			{
				sout.append(sin.substring(i, i + 1));
			}
			decodedString = sout.toString();
		}
		catch (Exception exeption)
		{
			logger.warn("Problems in Decription/Encription", exeption);
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
		byte[] bytes = new byte[len / TWO];
		for (int i = 0; i < bytes.length; i++)
		{
			int digit1 = decodeString.charAt(i * TWO);
			int digit2 = decodeString.charAt(i * TWO + 1);
			digit1 = getEncodedDigit(digit1);
			digit2 = getEncodedDigit(digit2);
			bytes[i] = (byte) ((digit1 << FOUR) + digit2);
		}
		return bytes;
	}

	/**
	 * @param digit digit to be encoded.
	 * @return encoded digit.
	 */
	private static int getEncodedDigit(int digit)
	{
		int encodedDigit = digit;
		if ((encodedDigit >= '0') && (encodedDigit <= '9'))
		{
			encodedDigit -= '0';
		}
		else if ((encodedDigit >= 'a') && (encodedDigit <= 'f'))
		{
			encodedDigit -= 'a' - TEN;
		}
		return encodedDigit;
	}

	/**
	 * This method returns the validation results on Form Bean. This method should be called
	 * from validate method of form bean.
	 * @param newPassword New Password value
	 * @param oldPassword Old Password value
	 * @param pwdChangedInsameSession : its value will come from session and passed to this method like,
	 * (Boolean) httpSession .getAttribute(Constants.PASSWORD_CHANGE_IN_SESSION);
	 * @return SUCCESS if all condition passed
	 *   else return respective error code (constant int) value
	 */
	public static int validatePasswordOnFormBean(String newPassword, String oldPassword,
			Boolean pwdChangedInsameSession)
	{
		int errorNo = NOT_FAILED;
		errorNo = chkChangePassInSameSession(pwdChangedInsameSession, errorNo);
		errorNo = chkPassForMinLength(newPassword, errorNo);
		errorNo = chkPassForOldPass(newPassword, oldPassword, errorNo);
		errorNo = getErrorNumber(newPassword, errorNo);
		return errorNo;
	}

	/**
	 * to Check new password is different as old password ,if both are same return FAIL_SAME_AS_OLD.
	 * @param newPassword new password
	 * @param oldPassword old password.
	 * @param erNo error number-method body executes only when there is no error earlier.
	 * @return int error number or -1
	 */
	private static int chkPassForOldPass(String newPassword, String oldPassword, int erNo)
	{
		int erroNo = erNo;
		if (NOT_FAILED == erNo && newPassword.equals(oldPassword))
		{
			logger.debug("Password is not valid returning FAIL_SAME_AS_OLD");
			erroNo = FAIL_SAME_AS_OLD;
		}
		return erroNo;
	}

	/**
	 * @param newPassword New Password.
	 * @param erNo error number-method body executes only when there is no error i.e. -1
	 * @return int error number or -1
	 */
	private static int chkPassForMinLength(String newPassword, int erNo)
	{
		int erroNo = erNo;
		if (NOT_FAILED == erNo)
		{
			int minimumPasswordLength = Integer.parseInt(XMLPropertyHandler
					.getValue(MINIMUM_PASSWORD_LENGTH));
			// to Check length of password,if not valid return FAIL_LENGTH
			if (newPassword.length() < minimumPasswordLength)
			{
				logger.debug("Password is not valid returning FAIL_LENGHT");
				erroNo = FAIL_LENGTH;
			}
		}
		return erroNo;
	}

	/**
	 * to check whether password change in same session
	 * get attribute (Boolean) from session object stored when password is changed successfully.
	 * @param passwordChangedInsameSession tru if password change in same session otherwise false.
	 * @param erNo error number-method body executes only when there is no error earlier.
	 * @return int error number or -1
	 */
	private static int chkChangePassInSameSession(Boolean passwordChangedInsameSession, int erNo)
	{
		int erroNo = erNo;
		if (NOT_FAILED == erNo)
		{
			if (passwordChangedInsameSession != null && passwordChangedInsameSession.booleanValue())
			{
				// return error code if attribute (Boolean) is in session
				logger
						.debug("Attempt to change Password" +
								" in same session Returning FAIL_SAME_SESSION");
				erroNo = FAIL_SAME_SESSION;
			}
		}
		return erroNo;
	}

	/**
	 * following code checks pattern i.e password must include atleast one UCase,LCASE and Number
	 * and must not contain space character.
	 * @param newPassword New password
	 * @return int error number or -1
	 */
	private static int validateUpLowerNumSpaceCharInPass(String newPassword)
	{
		int erroNo = NOT_FAILED;
		final String regExpUpperCase = "^.*\\p{Upper}.*$";
		final String regExpLowerCase = "^.*\\p{Lower}.*$";
		final String regExpDigit = "^.*\\p{Digit}.*$";
		final String regExpSpace = "^.*\\p{Space}.*$";

		//boolean to check UCase character found in string
		boolean foundUCase = Pattern.matches(regExpUpperCase, newPassword);
		//boolean to check LCase character found in string
		boolean foundLCase = Pattern.matches(regExpLowerCase, newPassword);
		//boolean to check Digit/Number character found in string
		boolean foundNumber = Pattern.matches(regExpDigit, newPassword);
		//boolean to check space in String
		boolean foundSpace = Pattern.matches(regExpSpace, newPassword);

		// condition to check whether all above condotion is satisfied
		if (!foundUCase || !foundLCase || !foundNumber || foundSpace)
		{
			logger.debug("Password is not valid returning FAIL_IN_PATTERN");
			erroNo = FAIL_IN_PATTERN;
		}
		return erroNo;
	}

	/**
	 * This method combines UI validation and business rules validation which is incorrect.
	 * Call validatePasswordOnFormBean for Form bean validations.
	 * Write your own methods for business validations.
	 * @param newPassword New Password value
	 * @param oldPassword Old Password value
	 * @param pwdChangedInsameSession : this will come from session like
	 * (Boolean) httpSession .getAttribute(Constants.PASSWORD_CHANGE_IN_SESSION);
	 * @param sessionData : SessionDataBean this will come from session.
	 * httpSession.getAttribute(Constants.SESSION_DATA)
	 * @return SUCCESS (constant int 0) if all condition passed
	 *   else return respective error code (constant int) value
	 */
	public static int validate(String newPassword, String oldPassword,
			Boolean pwdChangedInsameSession,SessionDataBean sessionData)
	{
		int errorNo = NOT_FAILED;
		String userName = "";
		if (sessionData == null)
		{
			errorNo = FAIL_INVALID_SESSION;
		}
		else
		{
			userName = sessionData.getUserName();
		}
		errorNo = validateOldPassword(oldPassword, errorNo, userName);
		errorNo = chkChangePassInSameSession(pwdChangedInsameSession, errorNo);
		errorNo = chkPassForMinLength(newPassword, errorNo);
		errorNo = chkPassForOldPass(newPassword, oldPassword, errorNo);
		errorNo = checkPassWithEmail(newPassword, userName, errorNo);
		errorNo = chkPassWithUserName(newPassword, userName, errorNo);
		errorNo = getErrorNumber(newPassword, errorNo);
		return errorNo;
	}

	/**
	 * @param newPassword new Password
	 * @param errorNo error No.
	 * @return error Number
	 */
	private static int getErrorNumber(String newPassword, int errorNo)
	{
		int errNumber = errorNo;
		if (NOT_FAILED == errNumber)
		{
			errNumber = validateUpLowerNumSpaceCharInPass(newPassword);
		}
		if (NOT_FAILED == errNumber)
		{
			errNumber = SUCCESS;
		}
		return errNumber;
	}

	/**
	 * to check password is differnt than user name if same return FAIL_SAME_AS_USERNAME =4
	 * eg. username=abc@abc.com newpassword=abc is not valid.
	 * @param newPassword new Password.
	 * @param userName user name.
	 * @param erNo error number-method body executes only when there is no error i.e. -1
	 * @return int error number or -1
	 */
	private static int checkPassWithEmail(String newPassword, String userName, int erNo)
	{
		int errorNo = erNo;
		if (NOT_FAILED == erNo)
		{
			int usernameBeforeMailaddress = userName.indexOf('@');
			// get substring of username before '@' character
			String name = userName.substring(0, usernameBeforeMailaddress);
			if (name != null && newPassword.equals(name))
			{
				logger.debug("Password is not valid returning FAIL_SAME_AS_USERNAME");
				errorNo = FAIL_SAME_AS_USERNAME; // return int value 3
			}
		}
		return errorNo;
	}

	/**
	 * to check password is differnt than user name if same return FAIL_SAME_AS_USERNAME =4
	 * eg. username=abc@abc.com newpassword=abc@abc.com is not valid
	 * @param newPassword New Password.
	 * @param userName User nmae
	 * @param erNo error number-method body executes only when there is no error i.e. -1
	 * @return int error number or -1
	 */
	private static int chkPassWithUserName(String newPassword, String userName, int erNo)
	{
		int errorNo = erNo;
		if (NOT_FAILED == erNo && newPassword.equals(userName))
		{
			logger.debug("Password is not valid returning FAIL_SAME_AS_USERNAME");
			errorNo = FAIL_SAME_AS_USERNAME; // return int value 3
		}
		return errorNo;
	}

	/**
	 * @param oldPassword Old Password.
	 * @param erNo error number-method body executes only when there is no error i.e. -1
	 * @param userName User Name
	 * @return int: error number or -1
	 */
	private static int validateOldPassword(String oldPassword, int erNo, String userName)
	{
		int errorNo = erNo;
		if (NOT_FAILED == erNo)
		{
			// to check whether user entered correct old password
			try
			{
				String password = getOldPassword(userName);
				//compare password stored in database with value of old password
				//currently entered by user for Change Password operation
				if (!oldPassword.equals(PasswordManager.decode(password)))
				{
					errorNo = FAIL_WRONG_OLD_PASSWORD; //retun value is int 6
				}
			}
			catch (Exception e)
			{
				// if error occured during password comparision
				logger.error(e.getMessage(), e);
				errorNo = FAIL_WRONG_OLD_PASSWORD;
			}
		}
		return errorNo;
	}

	/**
	 * @param userName user Name.
	 * @return old password of a user.
	 * @throws DAOException databse exception.
	 * @throws BizLogicException BizLogicException
	 */
	private static String getOldPassword(String userName) throws DAOException, BizLogicException
	{
		// retrieve User DomainObject by user name
		IBizLogic bizLogic = new DefaultBizLogic();
		String[] selectColumnNames = {"password"};
		String[] whereColumnNames = {"loginName"};
		String[] whereColumnCondition = {"="};
		String[] whereColumnValues = {userName};

		//Gautam_COMMON_TEMP_FIX USER_CLASS_NAME
		List userList = bizLogic.retrieve(USER_CLASS_NAME, selectColumnNames, whereColumnNames,
				whereColumnCondition, whereColumnValues, null);
		String password = null;
		if (userList != null && !userList.isEmpty())
		{
			password = (String) userList.get(0);
		}
		return password;
	}

	/**
	 * Error message.
	 * @param errorCode int value return by validate() method
	 * @return String error message with respect to error code
	 */
	public static String getErrorMessage(int errorCode)
	{
		String errMsg = errorMess.get(errorCode);
		if (null == errMsg)
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