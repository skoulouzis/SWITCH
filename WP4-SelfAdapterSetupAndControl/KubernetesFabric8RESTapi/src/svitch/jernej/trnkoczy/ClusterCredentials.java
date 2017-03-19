/* 
* =================================================================================
* This file is part of: Self-Adapter Setup and Control component 
* Release version: 0.1
* =================================================================================
* Developer: Jernej Trnkoczy, University of Ljubljana, Slovenia
* 
* The project leading to this application has received funding
* from the European Union's Horizon 2020 research and innovation
* programme under grant agreement No 643963.
*
* Copyright 2016 
* Contact: Vlado Stankovski (vlado.stankovski@fgg.uni-lj.si)
* =================================================================================
* Licensed under the Apache License, Version 2.0 (the "License");
* you must not use this file except in compliance with the License.
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*
* For details see the terms of the License (see attached file: README).
* The License is also available at http://www.apache.org/licenses/LICENSE-2.0.txt.
* ================================================================================
*/

package svitch.jernej.trnkoczy;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class ClusterCredentials {
	
	private CredentialsType credType; //enumerated type "password" or "certificates"
	private String masterUrl; //string  , this containes the URL of the Kuberentes API server that is located on the Master host.
	private String clientPublicCred; //string , this contains username or clientCert (the public part of the client credentials) - depending on the type of credentials used for login
	private String clientPrivateCred; //string , this contains password of clientKey (the private part of the client credentials) - depending on the type of credentials used for login
	private String caCert; //string, this contains the public key of the certificate authority that was used to sign client credentials.


	//no arg constructor needed for JAXB
	public ClusterCredentials(){}

	public ClusterCredentials(
			CredentialsType credType, 
			String masterUrl, 
			String clientPublicCred, 
			String clientPrivateCred,			
			String caCert
			){
		this.credType=credType;
		this.masterUrl=masterUrl;
		this.clientPublicCred=clientPublicCred;
		this.clientPrivateCred=clientPrivateCred;
		this.caCert=caCert;		
	}



	//------------------------------GETTERS------------------------------------------------------------------------------
	public CredentialsType getCredentialsType() {
		return this.credType;
	}

	public String getMasterUrl() {
		return this.masterUrl;
	}

	public String getClientPublicCred() {
		return this.clientPublicCred;
	}
	
	public String getClientPrivateCred() {
		return this.clientPrivateCred;
	}	

	public String getCertificateAuthority() {
		return this.caCert;
	}	
	//-------------------------------------------------------------------------------------------------------------------

	//++++++++++++++++++++++++++++++SETTERS++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void setCredentialsType(CredentialsType credType) {
		this.credType=credType;
	}

	public void setMasterUrl(String masterUrl) {
		this.masterUrl=masterUrl;
	}

	public void setClientPublicCred(String clientPublicCred) {
		this.clientPublicCred=clientPublicCred;
	}	

	public void setClientPrivateCred(String clientPrivateCred) {
		this.clientPrivateCred=clientPrivateCred;
	}

	public void setCertificateAuthority(String caCert) {
		this.caCert=caCert;
	}
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

}
