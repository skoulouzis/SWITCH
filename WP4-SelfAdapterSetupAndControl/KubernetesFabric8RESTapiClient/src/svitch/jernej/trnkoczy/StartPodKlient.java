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

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;

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

public class StartPodKlient {

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
		
		
		
		
		//#######################################NOW WE NEED TO MAKE A POD SPECIFICATION - THIS COULD BE IN VARIOUS FORMS############################################
		/*		
		//-----------------------------------SPECIFICATION OF THE MCU POD EXPOSED WITH hostPort--------------------------------------------------------
		//-----------------------------------LETS CREATE A FABRIC8 Pod OBJECT------------------------------------------------------------------
		Pod pod = new Pod(); 
		ObjectMeta om=new ObjectMeta();
		pod.setMetadata(om);

		//set the labels of the pod
		Map<String, String> podLabels=new HashMap<String, String>();
		podLabels.put("tier", "frontend");
		podLabels.put("app", "testmcu");
		om.setLabels(podLabels);

		//lets create all the containers (that will end up in the podSpec part of the Kubernetes .yaml file)
		//but in our case there will be only one container anyway!
		List<Container> containers = new ArrayList<Container>(); 

		Container curContainer = new Container(); 	
		curContainer.setName("mcu-kontejner"); 
		curContainer.setImage("jernejtrnkoczy/jitsimeet004");

		Map<String, Quantity> resourceRequests=new HashMap<String, Quantity>();
		resourceRequests.put("cpu", new Quantity("100m"));
		resourceRequests.put("memory", new Quantity("100Mi"));
		//Map<String, Quantity> resourceLimits=new HashMap<String, Quantity>();
		//resourceLimits.put("cpu", new Quantity("100m"));
		//resourceLimits.put("memory", new Quantity("100Mi"));

		//ResourceRequirements resourceReq=new ResourceRequirements(resourceLimits, resourceRequests);
		ResourceRequirements resourceReq=new ResourceRequirements(null, resourceRequests);
		curContainer.setResources(resourceReq);		

		List<EnvVar> env = new ArrayList<EnvVar>(1);
		env.add(new EnvVar("GET_HOSTS_FROM", "dns", null));		    
		curContainer.setEnv(env);
		List<ContainerPort> ports = new ArrayList<ContainerPort>();
		ContainerPort cp1=new ContainerPort(444, null, 444, "mcutcpport", ProtocolType.TCP.getValue());
		ports.add(cp1);
		ContainerPort cp2=new ContainerPort(10000, null, 10000, "mcuudpport", ProtocolType.UDP.getValue());
		ports.add(cp2);	
		curContainer.setPorts(ports);
		//curContainer.setCommand(List<String> cmd);
		//curContainer.setArgs(List<String> args);

		containers.add(curContainer); 

		PodSpec podSpec = new PodSpec(); 
		pod.setSpec(podSpec); 
		podSpec.setContainers(containers);
		//podSpec.setRestartPolicy("Never");		
		//------------------------------------------------------------------------------------------------------------------------------------------------
		 */



		/*
		//-----------------------------------SPECIFICATION OF THE FILE UPLOAD POD EXPOSED WITH hostPort--------------------------------------------------------
		//-----------------------------------LETS CREATE A FABRIC8 Pod OBJECT------------------------------------------------------------------
		Pod pod = new Pod(); 
		ObjectMeta om=new ObjectMeta();
		pod.setMetadata(om);

		//set the labels of the pod
		Map<String, String> podLabels=new HashMap<String, String>();
		podLabels.put("tier", "frontend");
		podLabels.put("app", "fileuploadhostport");
		om.setLabels(podLabels);

		//lets create all the containers (that will end up in the podSpec part of the Kubernetes .yaml file)
		//but in our case there will be only one container anyway!
		List<Container> containers = new ArrayList<Container>(); 

		Container curContainer = new Container(); 	
		curContainer.setName("tomcat-file-upload-monitoring"); 
		curContainer.setImage("jernejtrnkoczy/tomcatfileupload03");

		Map<String, Quantity> resourceRequests=new HashMap<String, Quantity>();
		resourceRequests.put("cpu", new Quantity("100m"));
		resourceRequests.put("memory", new Quantity("100Mi"));
		//Map<String, Quantity> resourceLimits=new HashMap<String, Quantity>();
		//		resourceLimits.put("cpu", new Quantity("100m"));
		//		resourceLimits.put("memory", new Quantity("100Mi"));

		//ResourceRequirements resourceReq=new ResourceRequirements(resourceLimits, resourceRequests);
		ResourceRequirements resourceReq=new ResourceRequirements(null, resourceRequests);
		curContainer.setResources(resourceReq);		

		List<EnvVar> env = new ArrayList<EnvVar>(1);
		env.add(new EnvVar("GET_HOSTS_FROM", "dns", null));		    
		curContainer.setEnv(env);
		List<ContainerPort> ports = new ArrayList<ContainerPort>();
		ContainerPort cp1=new ContainerPort(8080, null, 8080, "tomcatport", ProtocolType.TCP.getValue());
		ports.add(cp1);
		//TODO: actuall I think that the specification of this port is not needed for the pod to run sucesfully (nobody will ever call the monitoring agent)
		ContainerPort cp2=new ContainerPort(4242, null, 4242, "agentport", ProtocolType.TCP.getValue());
		ports.add(cp2);	
		curContainer.setPorts(ports);
		//curContainer.setCommand(List<String> cmd);
		List<String> arguments=new ArrayList<String>();
		arguments.add("--monitoringServerIP=194.249.0.185");
		curContainer.setArgs(arguments);

		containers.add(curContainer); 

		PodSpec podSpec = new PodSpec(); 
		pod.setSpec(podSpec); 
		podSpec.setContainers(containers);
		//podSpec.setRestartPolicy("Never");		
		//------------------------------------------------------------------------------------------------------------------------------------------------
		 */



		//---------------------SPECIFICATION OF THE FILE UPLOAD POD THAT WILL BE EXPOSED AS KUBERNETES SERVICE---------------------------------
		//-----------------------------------LETS CREATE A FABRIC8 Pod OBJECT------------------------------------------------------------------
		Pod pod = new Pod(); 
		ObjectMeta om=new ObjectMeta();
		pod.setMetadata(om);
		//if we will not specify the name of the pod then my REST API will auto-generate the name (random UUID) on the server side
		om.setName("imetegalepoda");

		//set the labels of the pod
		Map<String, String> podLabels=new HashMap<String, String>();
		podLabels.put("tier", "frontend");
		podLabels.put("app", "fileupload2");
		om.setLabels(podLabels);

		//lets create all the containers (that will end up in the podSpec part of the Kubernetes .yaml file)
		//but in our case there will be only one container anyway!
		List<Container> containers = new ArrayList<Container>(); 

		Container curContainer = new Container(); 	
		curContainer.setName("tomcat-file-upload"); 
		curContainer.setImage("jernejtrnkoczy/tomcatfileupload01");

		Map<String, Quantity> resourceRequests=new HashMap<String, Quantity>();
		resourceRequests.put("cpu", new Quantity("100m"));
		resourceRequests.put("memory", new Quantity("100Mi"));		
		ResourceRequirements resourceReq=new ResourceRequirements(null, resourceRequests);
		curContainer.setResources(resourceReq);		

		List<EnvVar> env = new ArrayList<EnvVar>(1);
		env.add(new EnvVar("GET_HOSTS_FROM", "dns", null));		    
		curContainer.setEnv(env);
		List<ContainerPort> ports = new ArrayList<ContainerPort>();
		ContainerPort cp1=new ContainerPort(8080, null, null, "tomcatport", ProtocolType.TCP.getValue());
		ports.add(cp1);
		curContainer.setPorts(ports);

		containers.add(curContainer);
		PodSpec podSpec = new PodSpec(); 
		pod.setSpec(podSpec); 
		podSpec.setContainers(containers);		
		//------------------------------------------------------------------------------------------------------------------------------------------------
		//########################################END POD SPECIFICATION#############################################################################################
		
		

		//%%%%%%%%%%%%%%%%%%%%%%%%NOW WE NEED TO SUBMIT THIS TO THE APPROPRIATE API ENDPOINT%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		//%%%%%%%%%%%%%%%%%%%%%%%%%HOWEVER I BUILT SEVERAL FUNCTIONS DOING THE SAME BUT IN DIFFERENT WAYS%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		//%%%%%%%%%%%%%%%%%%%%%%%%%LESTS USE OPTION 1 WHICH IS NOT SO RESTFULL%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		/*
		String uri="http://localhost:8080/KubernetesFabric8RESTapi/API/v01/startPodInDefaultNamespace";
		//String uri="http://194.249.0.47:8080/KubernetesFabric8RESTapi/API/v01/startPodInDefaultNamespace";
		//String uri="http://localhost:9999/KubernetesFabric8RESTapi/API/v01/startPodInDefaultNamespace"; //used for TCP/IP monitor in Eclipse - the monitor should be configured properly to forward packets to final server
		//ClientConfig config = new ClientConfig().register(JacksonFeatures.class);
		ClientConfig config = new ClientConfig().register(LoggingFilter.class);
		Client client = ClientBuilder.newClient(config);
		WebTarget service = client.target(UriBuilder.fromUri(uri).build());
		//now use all the data to meka an instance of the data that is needed for instantiation of the pod
		PodInitializationData podInit=new PodInitializationData(clusterId, pod);		
		Invocation.Builder invocationBuilder =  service.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.json(podInit));
		if(response.getStatus()!=200){
			System.out.println("Something went wrong on the server!");
		}
		else{
			String theNameOfCreatedPod = response.readEntity(String.class);
			System.out.println("Ustvarjeni pod ima ime: "+theNameOfCreatedPod);
		}
		*/
		
		//%%%%%%%%%%%%%%%%%%%%%%%%%OR RATHER USE OPTION 2 WHICH IS MORE RESTFULL%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		String namespace="default";
		String uri="http://localhost:8080/KubernetesFabric8RESTapi/API/v01/"+clusterId+"/namespaces/"+namespace+"/pods";
		//String uri="http://localhost:9999/KubernetesFabric8RESTapi/API/v01/"+clusterId+"/namespaces/"+namespace+"/pods"; //used for TCP/IP monitor in Eclipse - the monitor should be configured properly to forward packets to final server
		//ClientConfig config = new ClientConfig().register(JacksonFeatures.class);
		ClientConfig config = new ClientConfig().register(LoggingFilter.class);
		Client client = ClientBuilder.newClient(config);
		WebTarget service = client.target(UriBuilder.fromUri(uri).build());
		Invocation.Builder invocationBuilder =  service.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.json(pod));
		if(response.getStatus()!=200){
			System.out.println("Response status is: "+response.getStatus()+". Something went wrong on the server!");
		}
		else{
			String theNameOfCreatedPod = response.readEntity(String.class);
			System.out.println("Ustvarjeni pod ima ime: "+theNameOfCreatedPod);
		}


	}
}
