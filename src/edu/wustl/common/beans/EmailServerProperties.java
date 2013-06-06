/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.beans;

public class EmailServerProperties {
	 
     private String serverPort;
     
     private String serverHost;

     private String fromAddr;

     private String fromPassword;
    
     private String isSMTPAuthEnabled;
     
     private String isStartTLSEnabled;
      
     public String getServerHost() {
    	 return serverHost;
     }

     public void setServerHost(String serverHost) {
    	 this.serverHost = serverHost;
     }

     public String getServerPort() {
    	 return serverPort;
     }

     public void setServerPort(String serverPort) {
    	 this.serverPort = serverPort;
     }

     public String getFromAddr() {
       	 return fromAddr;
     }

     public void setFromAddr(String fromAddr) {
       	 this.fromAddr = fromAddr;
     }

     public String getFromPassword() {
       	 return fromPassword;
     }

     public void setFromPassword(String fromPassword) {
      	 this.fromPassword = fromPassword;
     }

     public String getIsSMTPAuthEnabled() {
      	 return isSMTPAuthEnabled;
     }

     public void setIsSMTPAuthEnabled(String isSMTPAuthEnabled) {
       	 this.isSMTPAuthEnabled = isSMTPAuthEnabled;
     }

     public String getIsStartTLSEnabled() {
       	 return isStartTLSEnabled;
     }

     public void setIsStartTLSEnabled(String isStartTLSEnabled) {
       	 this.isStartTLSEnabled = isStartTLSEnabled;
     }
}

