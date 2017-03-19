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

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;

public class StartServiceKlient {
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


		//########################################SERVICE SPECIFICATION#############################################################################################
		//---------------------SPECIFICATION OF THE FILE UPLOAD SERVICE THAT SHOULD EXPOSE THE FILE UPLOAD POD TO THE OUTSIDE WORLD---------------------------
		//--------------------------THE DATA USED FOR THE CREATION OF THIS SERVICE OF COURSE DEPENDS ON THE DATA OF THE POD-----------------------------------
		//--------------------------WE WILL CONNECT THE POD AND THE SERVICE THROUGH THE LABELS THAT WERE SET FOR THE POD--------------------------------------
		//----------------------------------------------LETS CREATE A FABRIC8 Service OBJECT------------------------------------------------------------------
		Service srv=new Service();		
		ObjectMeta om=new ObjectMeta();
		srv.setMetadata(om);
		//if we will not specify the name of the service then my REST API will auto-generate the name (random UUID) on the server side
		om.setName("imetegaleservisa");

		//set the labels of the service
		Map<String, String> srvLabels=new HashMap<String, String>();
		srvLabels.put("app", "fileupload2");
		om.setLabels(srvLabels);

		ServiceSpec srvSpec = new ServiceSpec(); 
		srv.setSpec(srvSpec); 
		srvSpec.setType("NodePort");
		List<ServicePort> srvPorts=new ArrayList<ServicePort>(1);
		//The constructor of ServicePort takes the following parameters: (String name, Integer nodePort, Integer port, String protocol, IntOrString targetPort) 
		//name --> name of the port
		//nodePort --> if you expose to the outside world the service will be accessible on this port on any cluster node
		//port --> this is the number that is assigned to the load balancer - and the service will be accessible through load balance with IP-OF-LOAD-BALANCER:port combination
		//protocol --> the traffic type that will go in and out (TCP or UDP)
		//targetPort --> this is the port of the container (e.g. if Tomcat is running in container this port will be 8080). If this is not specified then it will take the same value as is specified in the "port" parameter!
		IntOrString targetPort=new IntOrString(8080);
		ServicePort srvport01=new ServicePort("servicefileuploadport", 30890, 8080, ProtocolType.TCP.getValue(), targetPort);
		srvPorts.add(srvport01);
		srvSpec.setPorts(srvPorts);
		//now we need to specify which pods will this service target (=expose). We will define this by specifying the labels of the podd that we want to target!
		Map<String,String> podSelector=new HashMap<String, String>();
		//this means that this service will target all pods that have value "fileupload2" in "app" label and value "frontend" in "tier" label
		podSelector.put("app", "fileupload2");
		podSelector.put("tier", "frontend");
		srvSpec.setSelector(podSelector);
		//------------------------------------------------------------------------------------------------------------------------------------------------
		//########################################END SERVICE SPECIFICATION#############################################################################################




		//%%%%%%%%%%%%%%%%%%%%%%%%NOW WE NEED TO SUBMIT THIS TO THE APPROPRIATE API ENDPOINT%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		//%%%%%%%%%%%%%%%%%%%%%%%%%HOWEVER I BUILT SEVERAL FUNCTIONS DOING THE SAME BUT IN DIFFERENT WAYS%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		//%%%%%%%%%%%%%%%%%%%%%%%%%LESTS USE OPTION 1 WHICH IS NOT SO RESTFULL%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		/*
		String uri="http://localhost:8080/KubernetesFabric8RESTapi/API/v01/startServiceInDefaultNamespace";
		//String uri="http://194.249.0.47:8080/KubernetesFabric8RESTapi/API/v01/startServiceInDefaultNamespace";
		//String uri="http://localhost:9999/KubernetesFabric8RESTapi/API/v01/startServiceInDefaultNamespace"; //used for TCP/IP monitor in Eclipse - the monitor should be configured properly to forward packets to final server
		//ClientConfig config = new ClientConfig().register(JacksonFeatures.class);
		ClientConfig config = new ClientConfig().register(LoggingFilter.class);
		Client client = ClientBuilder.newClient(config);
		WebTarget service = client.target(UriBuilder.fromUri(uri).build());


		//now use all the data to meka an instance of the data that is needed for instantiation of the pod
		ServiceInitializationData srvInit=new ServiceInitializationData(clusterId, srv);		
		Invocation.Builder invocationBuilder =  service.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.json(srvInit));
		if(response.getStatus()!=200){
			System.out.println("Something went wrong on the server!");
		}
		else{
			String theNameOfCreatedService = response.readEntity(String.class);
			System.out.println("Ustvarjeni service ima ime: "+ theNameOfCreatedService);
		}
		 */



		//%%%%%%%%%%%%%%%%%%%%%%%%%OR RATHER USE OPTION 2 WHICH IS MORE RESTFULL%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		String namespace="default";
		String uri="http://localhost:8080/KubernetesFabric8RESTapi/API/v01/"+clusterId+"/namespaces/"+namespace+"/services";
		//String uri="http://localhost:9999/KubernetesFabric8RESTapi/API/v01/"+clusterId+"/namespaces/"+namespace+"/services"; //used for TCP/IP monitor in Eclipse - the monitor should be configured properly to forward packets to final server
		//ClientConfig config = new ClientConfig().register(JacksonFeatures.class);
		ClientConfig config = new ClientConfig().register(LoggingFilter.class);
		Client client = ClientBuilder.newClient(config);
		WebTarget service = client.target(UriBuilder.fromUri(uri).build());
		Invocation.Builder invocationBuilder =  service.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.json(srv));
		if(response.getStatus()!=200){
			System.out.println("Something went wrong on the server!");
		}
		else{
			String theNameOfCreatedService = response.readEntity(String.class);
			System.out.println("Ustvarjeni service ima ime: "+theNameOfCreatedService);
		}

	}

}
