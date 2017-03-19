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

//This is only a dummy REST service that should later be replaced by real Knowledge Base - the service receives the clusterID and returns the credentials that can
//be used to log-in to the cluster with that ID.

package svitch.jernej.trnkoczy;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;



//Sets the path to base URL + /switch
@Path("/kbdummy")
public class KnowledgeBaseDummy {
	private static final ClusterCredentials arnesCredentials=new ClusterCredentials(
			CredentialsType.CERTIFICATES,
			"https://194.249.0.45/",

			"-----BEGIN CERTIFICATE-----\n"
					+"MIICozCCAYsCCQDUS3h1k387BDANBgkqhkiG9w0BAQsFADASMRAwDgYDVQQDDAdr\n"
					+"dWJlLWNhMB4XDTE2MTAyMDE1NDMzMVoXDTE5MDcxNzE1NDMzMVowFTETMBEGA1UE\n"
					+"AwwKa3ViZS1hZG1pbjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKfa\n"
					+"YkSFviVBrRIK6SvyFxtB4J80lS4mh/zpN8crMNnD31wQBsCWyqbiM8ibB4e6fXCZ\n"
					+"e/udyYgTn6l9JzN2nhEvfQyNvfC0c6b5LN79guPD++W1YXOICKnpbUeENdWdit7K\n"
					+"+PmcVE0O2Udlqnh0I/WbL52TbM15g7izEWjAnwAYL5K+AdpxGoftHyly/VpV+Oqf\n"
					+"Y8vXcS9Lbw51KKxLlE7Zi3kPqxZvsX7i9u4zcwIkduQf1tJf8vR3XU8kM/s8q2Gu\n"
					+"foRf+3bdYpj5+UxaNAyh8v7282YF7fVeKSvquYFRr8gFXW0d0Mnf4/a2Q7GtrA0F\n"
					+"HaGOU3Tze6VV+rkSjOECAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAaxKCgI+O5RBq\n"
					+"iqLwfOXOKiqFhcM5xHLNyfRsJCp7XMOVz0deW95HM9QWFeFCOAQDGTjWzavPL9M8\n"
					+"tsdXpDkkbZNZqzXNI2sR4FPqVK6PDbSizDeI9OU74nT2mt7o+SnR09O1k6/ERqXx\n"
					+"pLOOyI81ux2cYBM5/ub181XqiybOwZsA3jM6ckpTHw9nYNsZAGS5jfSP7iXPk/5K\n"
					+"XudU5ctrcKeOvJuYf0bEnt8ZGct0QlA9vcLm7M00gXIXoWcdlH6+I/krmfNpF8Wa\n"
					+"ogrPvMmXZxt265ZJDKqWwSWdhDE+UWfLIkj+2LA5tPLamAl/kzBAVJCCmQP4TA3I\n"
					+"bVbeLre/Wg==\n"
					+"-----END CERTIFICATE-----",

					"-----BEGIN RSA PRIVATE KEY-----\n"
							+"MIIEpAIBAAKCAQEAp9piRIW+JUGtEgrpK/IXG0HgnzSVLiaH/Ok3xysw2cPfXBAG\n"
							+"wJbKpuIzyJsHh7p9cJl7+53JiBOfqX0nM3aeES99DI298LRzpvks3v2C48P75bVh\n"
							+"c4gIqeltR4Q11Z2K3sr4+ZxUTQ7ZR2WqeHQj9ZsvnZNszXmDuLMRaMCfABgvkr4B\n"
							+"2nEah+0fKXL9WlX46p9jy9dxL0tvDnUorEuUTtmLeQ+rFm+xfuL27jNzAiR25B/W\n"
							+"0l/y9HddTyQz+zyrYa5+hF/7dt1imPn5TFo0DKHy/vbzZgXt9V4pK+q5gVGvyAVd\n"
							+"bR3Qyd/j9rZDsa2sDQUdoY5TdPN7pVX6uRKM4QIDAQABAoIBAQCH3xUQlpp2mhU4\n"
							+"t7BLrGnb3JWxCeWAOBx4M2i9Rk8C17UkVqzAlM9yC0UTq7qKugD0dqmP4gfSqN/j\n"
							+"WLW0yN3m12QIF2ybPdcoYCdcUiXzrR9osEw0MvegYp3c+D6DrkVNV0v28f6jadOk\n"
							+"Ib2+R3UcSQRgL4gxQ53Gkt0SyGlsrtdDsZpj+sLoFXDdOKtql8R3XkZq5yuMoUa4\n"
							+"1PMGSfNZQqSmQab5gXwEglQXUtuehtphhTbZIU7M6LjoV0C7EJyoHGxS8DIXGdy8\n"
							+"H0jKs50cgk8QLtLQM4hdeocwSev1viG05Cqih83MJ0lOgi+3tJCYIET+MVs9/FPw\n"
							+"NNciWAGJAoGBANmtk2dEFJHJbeiXVs0pE0HFSdV9SzKmpPID55EEx5tONM0cOf8K\n"
							+"ovrGdty8LLvBZi5SxuGRXK9sksPCQUzPMV45k5XpGVvfeNP/9pUlGsbVIxnCK3/S\n"
							+"SxW98ToZRkpbBe4lRm8j3mbMO69lN58F6Wg+iFaTosPoAgADSMx3H3XDAoGBAMVn\n"
							+"RWK/Qgv6YaMYmlPKNVB4WdKh1KVcAlyuE4EmuWdjTvua8uPh/PZ3kt5b9NjGVXxO\n"
							+"cmMpNkLucKZ6WHDRJFkIrOREPPoQ2Dpyxlonl98trF21Xl40RvCSSbaSATreyglK\n"
							+"nuQP41lVOLPSRE9TRdTt785AgqxmwVo3vSzBhjSLAoGAXBIW7u7U5HUWj/KzPEPL\n"
							+"aV5RRIOicuIZkmQkZipnY0FJBBiUT1Poa7nWPLc7FI9Q8PcCOBaL5Yc+7J4VkK9y\n"
							+"ajBgRGA74ag8Ky9w1NTE5qTwG80NKRuBtsiIZ9KY8Ipfj8Mkb430W/a2qBFf3Vta\n"
							+"xkTs1fHMPqC7d9XrzM/19pECgYEArB3tG7pTJgp70y8CcIhgj/c/qi28MSnWTWTi\n"
							+"ULuo8IJgPXTOzu8eFvAPOJ3JELFtm+LyTY+DZjxsMFuAfW19Wbv8s1LkL808G0oz\n"
							+"tCvNeaRjI5md3FmMePm0F+GZ/PCADDXp8EQCIMPzmqZ9NQUQYCXN73HD/fniIH11\n"
							+"Dm72Sx0CgYAhnxeHLGVRbIgtD3JuyuB6WVD+SEsX3+U2GE4h7E5LewLyZdTxZEpe\n"
							+"Q8QSK8tULOcq6fz4kZ4IoUyRE5Fhiw5PilpTjZJbgURUArXFOOGVd8N4L3tFO99T\n"
							+"Nn58Uvyxnayfc/zUATxOA0i9T5aq1v3iUWl6hZNxojQErj1e+hmT5w==\n"
							+"-----END RSA PRIVATE KEY-----",

							"-----BEGIN CERTIFICATE-----\n"
									+"MIIC9zCCAd+gAwIBAgIJAJ7uHJvhPuZ5MA0GCSqGSIb3DQEBCwUAMBIxEDAOBgNV\n"
									+"BAMMB2t1YmUtY2EwHhcNMTYxMDIwMTUwNTE1WhcNMTkwNzE3MTUwNTE1WjASMRAw\n"
									+"DgYDVQQDDAdrdWJlLWNhMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA\n"
									+"neaB3DPk+csqvTQTzL6ZVQdsh5DcmpPoyOZLVjC0MGTqjkQ/8tD6GHH6ZIZHdC7y\n"
									+"TxmXQdDDP1qukpyniq3E89h0LWjS8Z8SeOLV0aIcoFnfv+vIoR1wFZvVpfAuypmJ\n"
									+"r2CRpBCBb5zG118oG/0jtHgI9Q0Bho7KW0VfPbYLiA06Hj2bU5qMYuZRjYvRI28H\n"
									+"YkC5otkhKJz2Uzbcsoz1LABVd8bZoCmCBTOjtZHtFiZe0yMtLtYttwGpJGjz+YAR\n"
									+"KoCRaAl3QaOPfuzYQRzJchzoiEOnRL1TerEpSvtJui0eM4zjFAohq1QW9MIu4WPF\n"
									+"g3OFVJxvPIBtvXWark4OHQIDAQABo1AwTjAdBgNVHQ4EFgQU+KveWgGIg2cdUuXl\n"
									+"vii0v0dqeYowHwYDVR0jBBgwFoAU+KveWgGIg2cdUuXlvii0v0dqeYowDAYDVR0T\n"
									+"BAUwAwEB/zANBgkqhkiG9w0BAQsFAAOCAQEAlMfPaW03f0A7KRBh2VFNliOx+bCj\n"
									+"02TNymyQO7ymX9/BKaMnLFsiOYMGP7QYKGkKpA7vlMHV2Hr/huX+Lwm44J7sgRCY\n"
									+"lU0EPmwkQIP/fp2dD4F1XpHe4rEO7XJtAQkW/eV23wloVMrcR4n0yjv7NF8fIIXT\n"
									+"3DK3Q63PLpjD0yCTgPx5NC/oJYOdcL6oEFT3rYoVQrQXfKj9VndJLiY3VD4aAShL\n"
									+"hHWFifPCCLeLdjnLmONlLWdMvP1yfLjDIvX8m9cGuQ5me9XztNuybRIDdHf4NFJg\n"
									+"SEjIG9HTOhXqG/IMDFPZzd6/5xFOPtOmpMOjjXnmKprDdaQJfzD3waekGA==\n"
									+"-----END CERTIFICATE-----");



	private static final ClusterCredentials gcpTaiwanCredentials=new ClusterCredentials(
			CredentialsType.PASSWORD,
			"https://104.199.179.57/",
			"admin",
			"0JmEWqcD6Cfh1c16",
			"-----BEGIN CERTIFICATE-----\n"
					+"MIIDIjCCAgqgAwIBAgIRAPaFG+FG81yi75Msn9/k+40wDQYJKoZIhvcNAQELBQAw\n"
					+"OjE4MDYGA1UEAwwvYXNpYS1lYXN0MS1jLTgyMjQ5MTc3OTg5OC1jbHVzdGVyLXR3\n"
					+"QDE0ODAzNDE1NDgwHhcNMTYxMTI4MTM1OTA4WhcNMjExMTI3MTM1OTA4WjA6MTgw\n"
					+"NgYDVQQDDC9hc2lhLWVhc3QxLWMtODIyNDkxNzc5ODk4LWNsdXN0ZXItdHdAMTQ4\n"
					+"MDM0MTU0ODCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBALfa8qSG3faG\n"
					+"sf4B1TAkqcU+YnKjaEHtmKmH3vnN91gAp+mupZLDVLsp1PZBtvHnQZOC7mU4ewJg\n"
					+"+Tv4IzaSGQy5wyKuSGfiS414XK6YAQpFJEO4uggt+ou/MVm5hw/77YNHwHexpk5s\n"
					+"S450vFZSDlA3gPQdKFifyqPY9A8/J5NoLC6bHr9IZGciBrq5BBlE9RCYSY3FKP/s\n"
					+"5jKDrWED0F6wRze8eAptZy9WC923ktUCxlea1CcefpCXFDJF37JsNmb8fIO45kfB\n"
					+"8/65zdV3rhOrGWYhpwYxEN2ghMulJD/cnuHXbd+SmISEotnqiKmxypYFCQu1pf4j\n"
					+"6y1Npor3V48CAwEAAaMjMCEwDgYDVR0PAQH/BAQDAgIEMA8GA1UdEwEB/wQFMAMB\n"
					+"Af8wDQYJKoZIhvcNAQELBQADggEBAEELDG+RE4FTnhRT1gkzxKDrSB6+mH80lfe0\n"
					+"OTD4ZYWAIWpahIXsJz6FEIoVV0lse+yI1G3+5bUMfkm/32p+cdHJXNY8kLokNWqf\n"
					+"qb9t0/iA41is7eODKEGCO+PoieiikQWURDG9t0KAPysCRusYozB4tIernhb3adK8\n"
					+"ICk4xYrCBhq4KNe50XzHW3RS8NhnqRcv+/WEAYwmRw5S6G3sQbHBpA8EyifweAM1\n"
					+"PZ/erubpWEREofDxJoA+iNbq0pnkWhotHPSKC65NT0D7p0pnQyxcmtXxWPI0L4Xc\n"
					+"/fE28rpxhrIRQwVxFCG+WFH2cLL5039ICue5krheUZsmasipm5M=\n"
					+"-----END CERTIFICATE-----");




	//when a POST HTTP request requiring MediaType.APPLICATION_JSON will arrive at URL http://localhost:8080/KubernetesFabric8RESTapi/AdaptationAPI/switch/getCredentials - the function getCredentials() will be called
	//the request is carrying the JSON data (idOfCluster where to instantiate the pod)
	//the response should contain JSON data in the format like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}
	@POST
	@Path("getCredentials")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	//the method takes parameter from the request and returns the required certificates 
	public ClusterCredentials getCredentials(String clusterID) throws IOException {
		ClusterCredentials cred=null;
		//based on give ID search in KB and find the certificates
		if(clusterID.equals("111abc11111111111")){
			//then start the pod in Arnes cloud - so return the Arnes credentials
			cred=arnesCredentials; 

		}
		else if(clusterID.equals("222abc22222222222")){
			//then start the pod in Google Taiwan cloud - so return GCP Taiwan credentials
			cred=gcpTaiwanCredentials;
		}

		return cred;		
	}

}
