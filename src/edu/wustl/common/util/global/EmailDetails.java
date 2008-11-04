package edu.wustl.common.util.global;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * This class contains email details.
 * @author ravi_kumar
 *
 */
public class EmailDetails
{
	/**
	 * to Address.
	 */
	private List<String> toAddress;
	/**
	 * cc Address.
	 */
	private List<String> ccAddress;
	/**
	 * bcc Address.
	 */
	private List<String> bccAddress;

	/**
	 * email subject.
	 */
	private String subject;

	/**
	 * Email body.
	 */
	private String body;

	/**
	 * No argument constructor.
	 */
	public EmailDetails()
	{
		toAddress = new ArrayList<String>();
		ccAddress = new ArrayList<String>();
		bccAddress = new ArrayList<String>();
	}
	/**
	 * Add to recipient address.
	 * @param toAddress Address to add in recipient.
	 */
	public void addToAddress(String toAddress)
	{
		this.toAddress.add(toAddress);
	}

	/**
	 * Remove address from recipient list.
	 * @param toAddress -Address to remove from recipient.
	 */
	public void removeToAddress(String toAddress)
	{
		this.toAddress.add(toAddress);
	}

	/**
	 * @param toAddress TO email address.
	 */
	public void setToAddress(List<String> toAddress)
	{
		this.toAddress=toAddress;
	}

	/**
	 * @param toAddress TO email address.
	 */
	public void setToAddress(String[] toAddress)
	{
		this.toAddress=Arrays.asList(toAddress);
	}

	/**
	 * @return the toAddress.
	 */
	public List<String> getToAddress()
	{
	 return this.toAddress;
	}

	/**
	 * @return return to address as array of InternetAddress.
	 * @throws AddressException This exception thrown when a wrongly formatted address is encountered
	 */
	public InternetAddress[] getToInternetAddrArray() throws AddressException
	{
		return convertToInternetAddrArray(toAddress);
	}
	/**
	 * @return the ccAddress
	 */
	public List<String> getCcAddress()
	{
		return ccAddress;
	}

	/**
	 * @param ccAddress the ccAddress to set
	 */
	public void setCcAddress(List<String> ccAddress)
	{
		this.ccAddress = ccAddress;
	}

	/**
	 * @param ccAddress the ccAddress to set
	 */
	public void setCcAddress(String[] ccAddress)
	{
		this.ccAddress = Arrays.asList(ccAddress);
	}

	/**
	 * Add to cc address.
	 * @param toAddress Address to add in recipient.
	 */
	public void addCcAddress(String toAddress)
	{
		this.ccAddress.add(toAddress);
	}

	/**
	 * Remove from cc address.
	 * @param toAddress -Address to remove from recipient.
	 */
	public void removeCcAddress(String toAddress)
	{
		this.ccAddress.add(toAddress);
	}

	/**
	 * @return return cc address as array of InternetAddress.
	 * @throws AddressException This exception thrown when a wrongly formatted address is encountered
	 */
	public InternetAddress[] getCcInternetAddrArray() throws AddressException
	{
		return convertToInternetAddrArray(ccAddress);
	}

	/**
	 * @return the bccAddress
	 */
	public List<String> getBccAddress()
	{
		return bccAddress;
	}

	/**
	 * @param bccAddress the bccAddress to set
	 */
	public void setBccAddress(List<String> bccAddress)
	{
		this.bccAddress = bccAddress;
	}

	/**
	 * @param bccAddress the bccAddress to set
	 */
	public void setBccAddress(String[] bccAddress)
	{
		this.bccAddress = Arrays.asList(bccAddress);
	}

	/**
	 * Add to bcc address.
	 * @param toAddress Address to add in recipient.
	 */
	public void addBccAddress(String toAddress)
	{
		this.bccAddress.add(toAddress);
	}

	/**
	 * Remove from bcc address.
	 * @param toAddress -Address to remove from recipient.
	 */
	public void removeBccAddress(String toAddress)
	{
		this.bccAddress.add(toAddress);
	}

	/**
	 * @return return bcc address as array of InternetAddress.
	 * @throws AddressException This exception thrown when a wrongly formatted address is encountered
	 */
	public InternetAddress[] getBccInternetAddrArray() throws AddressException
	{
		return convertToInternetAddrArray(bccAddress);
	}

	/**
	 * @return the subject
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	/**
	 * @return the body
	 */
	public String getBody()
	{
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body)
	{
		this.body = body;
	}


	/**
	 * This method convert Array To InternetAddrArray.
	 * Added by kiran_pinnamaneni. code reviewer abhijit_naik
	 * @param arrayToConvert convert int internet address array
	 * @return internetAddress
	 * @throws AddressException This exception thrown when a wrongly formatted address is encountered
	 */
	private InternetAddress[] convertToInternetAddrArray(List<String> arrayToConvert)
		throws AddressException

	{
		int noOfAddress=arrayToConvert.size();
		InternetAddress[] internetAddress = new InternetAddress[noOfAddress];
		for (int i = 0; i < noOfAddress; i++)
		{
			internetAddress[i] = new InternetAddress(arrayToConvert.get(i));
		}
		return internetAddress;
	}
}
