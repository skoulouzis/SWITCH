/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
/* 
* =================================================================================
* This file is part of: Client of Self-Adapter Setup and Control component 
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

import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;

public class GetNodePortsUsedByServiceKlient {
	public static void main(String[] args) {

		//I've put the properties file in the src folder together with all .java files. This folder is in the classpath (so the properties file is discovered 
		//automatically) 			
		Properties properties = new Properties();
		try {
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("KubernetesKlient.properties"));
		} catch (IOException e) {			
			e.printStackTrace();
		}		
		//Lets read which cluster we want to use from properties file.
		//MODIFY THE KubernetesKlient.properties FILE IF YOU WANT TO CHANGE THE CLUSTER TO BE USED!
		String clusterId=properties.getProperty("ClusterID");


		//%%%%%%%%%%%%%%%%%%%%%%%%NOW WE NEED TO SUBMIT THIS TO THE APPROPRIATE API ENDPOINT%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		//%%%%%%%%%%%%%%%%%%%%%%%%%HOWEVER I BUILT SEVERAL FUNCTIONS DOING THE SAME BUT IN DIFFERENT WAYS%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		//%%%%%%%%%%%%%%%%%%%%%%%%%LESTS USE OPTION 1 WHICH IS NOT SO RESTFULL%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		/*
		String uri="http://localhost:8080/KubernetesFabric8RESTapi/API/v01/getNodePortsUsedByService";
		//String uri="http://194.249.0.47:8080/KubernetesFabric8RESTapi/API/v01/getNodePortsUsedByService";
		//String uri="http://localhost:9999/KubernetesFabric8RESTapi/API/v01/getNodePortsUsedByService"; //used for TCP/IP monitor in Eclipse - the monitor should be configured properly to forward packets to final server
		//ClientConfig config = new ClientConfig().register(JacksonFeatures.class);
		ClientConfig config = new ClientConfig().register(LoggingFilter.class);
		Client client = ClientBuilder.newClient(config);
		WebTarget service = client.target(UriBuilder.fromUri(uri).build());		

		String serviceName="imetegaleservisa";
		ServiceNameIdData srvNameId=new ServiceNameIdData(clusterId, serviceName);

		Invocation.Builder invocationBuilder =  service.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.json(srvNameId));
		if(response.getStatus()!=200){
			System.out.println("Something went wrong on the server!");
		}
		else{
			//get the returned String (but of course we could serialize this string into the List<Integer> as well)
			String theSerializedListOfNodePorts = response.readEntity(String.class);
			System.out.println("Torej service z imenom "+serviceName+" ima definirane sledece NodePort porte: "+ theSerializedListOfNodePorts);
		}
		 */


		//%%%%%%%%%%%%%%%%%%%%%%%%%OR RATHER USE OPTION 2 WHICH IS MORE RESTFULL%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		String namespace="default";
		String srvName="imetegaleservisa";		
		String uri="http://localhost:8080/KubernetesFabric8RESTapi/API/v01/"+clusterId+"/namespaces/"+namespace+"/services/"+srvName+"/nodePorts";
		//String uri="http://localhost:9999/KubernetesFabric8RESTapi/API/v01/"+clusterId+"/namespaces/"+namespace+"/services/"+srvName+"/nodePorts"; //used for TCP/IP monitor in Eclipse - the monitor should be configured properly to forward packets to final server
		//ClientConfig config = new ClientConfig().register(JacksonFeatures.class);
		ClientConfig config = new ClientConfig().register(LoggingFilter.class);
		Client client = ClientBuilder.newClient(config);
		WebTarget service = client.target(UriBuilder.fromUri(uri).build());
		Invocation.Builder invocationBuilder =  service.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		if(response.getStatus()!=200){
			System.out.println("Something went wrong on the server!");
		}
		else{
			//get the returned String (but of course we could serialize this string into the List)
			String theSerializedListOfNodePorts = response.readEntity(String.class);
			System.out.println("Service z imenom "+srvName+" ima dodeljene sledece NodePort porte: "+ theSerializedListOfNodePorts);
		}




	}

}
