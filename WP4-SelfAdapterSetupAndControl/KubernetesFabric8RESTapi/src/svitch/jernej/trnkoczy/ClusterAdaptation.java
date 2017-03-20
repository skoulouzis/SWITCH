/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/


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

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;





//This will be a plain old Java Object - it does not extend a class (e.g. Servlet) or implement an interface

//Sets the path to base URL (combination of project name and path defined in web.xml) + /v01
@Path("/v01")
public class ClusterAdaptation {

	//This one is just for testing purposes. When a GET HTTP request requiring MediaType.TEXT_PLAIN will arrive at URL http://localhost:8080/KubernetesFabric8RESTapi/API/v01/ (API je 
	//definiran v web.xml, /v01 pa vidis da je dolocen kar v Java kodi) - the function sayPlainTextHello() will be called and will retunr TEXT_PLAIN HTTP message.
	@GET
	@Path("/hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello() {
		return "This is the Unifying API root resource - use it by providing the appropriate sub-resources!";
	}


	/**
	 * when a POST HTTP request requiring MediaType.APPLICATION_JSON will arrive at URL http://localhost:8080/KubernetesFabric8RESTapi/API/v01/startPodInDefaultNamespace - the function startPodInDefaultNamespace() will be called
	 * this request is carrying the JSON data (PodInitializationData object that contains the idOfCluster where to instantiate the pod and the 
	 * serialized import io.fabric8.kubernetes.api.model.Pod object).
	 * 
	 * the method takes parameter from the request, obtains the required certificates from the Knowledge Base, and then instantiates the Pod in the chosen cluster.
	 * if the name of the Pod specified in the arriving io.fabric8.kubernetes.api.model.Pod object was NOT ASSIGNED then we will create a RANDOM POD NAME and assign it to the Pod!	
	 *
	 * @deprecated use {@link #startPod(Pod podSpec, @PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace)} instead.  
	 */
	@Deprecated	
	@POST
	@Path("startPodInDefaultNamespace")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	//TODO: make custom Exception reporting (instead of throw new WebApplicationException();) - so we will know what went wrong on the server!
	public Response startPodInDefaultNamespace(PodInitializationData podSpec) {
		String theNameOfCreatedPod=podSpec.getPod().getMetadata().getName();
		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}


		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(podSpec.getClusterID());
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO INSTANTIATE A POD IN THE CLUSTER
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, "default");
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			if(theNameOfCreatedPod==null){
				theNameOfCreatedPod=MyUtilities.generateKubernetesFriendlyShortUuid(24);
			}
			try {				
				//create the pod now
				//MyUtilities.createThePod(kube, uniqueIdentifierOfPod);
				MyUtilities.createThePod(kube, theNameOfCreatedPod, podSpec.getPod());	
				return Response.ok(theNameOfCreatedPod).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();				
			} finally {
				kube.close();			
			}

		}
		else{
			throw new WebApplicationException();
		}	
	}




	/**
	 * 
	 * when a POST HTTP request requiring MediaType.APPLICATION_JSON will arrive at URL http://localhost:8080/KubernetesFabric8RESTapi/API/v01/startServiceInDefaultNamespace
	 * the function startServiceInDefaultNamespace(ServiceInitializationData srvSpec) will be called
	 * this request is carrying the JSON data (the serialized import io.fabric8.kubernetes.api.model.Service object)
	 * 
	 * the method takes parameter from the request, obtains the required certificates from the Knowledge Base, and then instantiates the Service in the chosen cluster.
	 * if the name of the Service specified in the arriving io.fabric8.kubernetes.api.model.Service object was NOT ASSIGNED then we will create a RANDOM SERVICE NAME and assign it to the Service!
	 *
	 * @deprecated use {@link #startService(Service srvSpec, @PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace)} instead.  
	 */
	@Deprecated	
	@POST
	@Path("startServiceInDefaultNamespace")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response startServiceInDefaultNamespace(ServiceInitializationData srvSpec) {
		String theNameOfCreatedService=srvSpec.getService().getMetadata().getName();
		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}

		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(srvSpec.getClusterID());
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO INSTANTIATE A SERVICE IN THE CLUSTER
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, "default");
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			if(theNameOfCreatedService==null){
				theNameOfCreatedService=MyUtilities.generateKubernetesFriendlyShortUuid(24);
			}
			try {				
				//create the service now
				MyUtilities.createTheService(kube, theNameOfCreatedService, srvSpec.getService());
				return Response.ok(theNameOfCreatedService).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();
			} finally {
				kube.close();			
			}	
		}
		else{
			throw new WebApplicationException();
		}		
	}






	/**
	 * 
	 * when a POST HTTP request requiring MediaType.APPLICATION_JSON will arrive at URL http://localhost:8080/KubernetesFabric8RESTapi/API/v01/stopPodByNameInDefaultNamespace
	 * the function stopPodByNameInDefaultNamespace(PodNameIdData podNameId) will be called
	 * this request is carrying the JSON data (the serialized PodNameIdData object)
	 * 
	 * the method takes parameter from the request, obtains the required certificates from the Knowledge Base, and then stops the Pod by it's name in the chosen cluster.
	 * 
	 * @deprecated use {@link #stopPodByName(@PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace, @PathParam("theNameOfThePod") String podName) } instead.  
	 */
	@Deprecated	
	@POST
	@Path("stopPodByNameInDefaultNamespace")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response stopPodByNameInDefaultNamespace(PodNameIdData podNameId) {
		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}

		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(podNameId.getClusterID());
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO DELETE A POD IN THE CLUSTER
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, "default");
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			try {				
				//delete the pod now
				MyUtilities.deletePodByName(kube, podNameId.getPodName());
				return Response.ok(podNameId.getPodName()).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();
			} finally {
				kube.close();			
			}	
		}
		else{
			throw new WebApplicationException();
		}		
	}





	/**
	 * 
	 * when a POST HTTP request requiring MediaType.APPLICATION_JSON will arrive at URL http://localhost:8080/KubernetesFabric8RESTapi/API/v01/stopServiceByNameInDefaultNamespace - the function stopServiceByNameInDefaultNamespace() will be called
	 * this request is carrying the JSON data (the idOfCluster and the name of the Service to stop)
	 * 
	 * the method takes parameter from the request, obtains the required certificates from the Knowledge Base, and then stops the Service in the chosen cluster.
	 * 
	 * @deprecated use {@link #stopServiceByName(@PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace, @PathParam("theNameOfTheService") String srvName) } instead.  
	 */
	@Deprecated	
	@POST
	@Path("stopServiceByNameInDefaultNamespace")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response stopServiceByNameInDefaultNamespace(ServiceNameIdData srvNameId) {
		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}
		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(srvNameId.getClusterID());
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO DELETE A SERVICE IN THE CLUSTER
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, "default");
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			try {				
				//delete the service now
				MyUtilities.deleteServiceByName(kube, srvNameId.getServiceName());
				return Response.ok(srvNameId.getServiceName()).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();				
			} finally {
				kube.close();			
			}			
		}
		else{
			throw new WebApplicationException();
		}	
	}






	/**
	 * 	
	 * when a POST HTTP request requiring MediaType.APPLICATION_JSON will arrive at URL http://localhost:8080/KubernetesFabric8RESTapi/API/v01/getPodHostIPInDefaultNamespace
	 * the function getPodHostIPInDefaultNamespace(PodNameIdData podNameId) will be called
	 * this request is carrying the JSON data (the serialized PodNameIdData object) 
	 * 
	 * the method takes parameter from the request, obtains the required certificates from the Knowledge Base, and then tries to find the Pod's IP by it's name in the
	 * default namespace of the chosen cluster. If the pod is not in the "running" phase yet, or if the pod with this name does not exist - then null (empty string) will
	 * be returned.	 * 
	 * 
	 * @deprecated use {@link #getPodHostIP(@PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace, @PathParam("theNameOfThePod") String podName) } instead.  
	 */
	@Deprecated	
	@POST
	@Path("getPodHostIPInDefaultNamespace")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)	
	public Response getPodHostIPInDefaultNamespace(PodNameIdData podNameId) {
		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}

		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(podNameId.getClusterID());
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO INSTANTIATE A CONNECTION AND THEN OBTAIN THE IP OF HOST WHERE THE POD IS RUNNING
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, "default");
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			try {				
				//get the pod IP now
				String ip=MyUtilities.getPodHostExternalIP(kube, podNameId.getPodName());
				return Response.ok(ip).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();
			} finally {
				kube.close();			
			}	
		}
		else{
			throw new WebApplicationException();
		}		
	}




	/**
	 *
	 * when a POST HTTP request requiring MediaType.APPLICATION_JSON will arrive at URL http://localhost:8080/KubernetesFabric8RESTapi/API/v01/notifyWhenThePodInDefaultNamespaceBecomesRunning
	 * the function notifyWhenThePodInDefaultNamespaceBecomesRunning(PodNameIdData podNameId) will be called
	 * this request is carrying the JSON data (the serialized PodNameIdData object)
	 *
	 * the method takes parameter from the request, obtains the required certificates from the Knowledge Base, and then checks if the Pod is in the "Running" phase. If 
	 * the pod exists but it is still in "Pending"phase, then we will register a listener and wait until the pod reaches the "Running" phase. If the pod is in the
	 * "Succeeded" or "Failed" phase - then it will probably never reach "Running" phase - so in this case we will just return the String Never: phase="+phase (ie: Never: phase=Failed) . We will return "Never: pod does not exist"
	 * if the pod with given name does not exist in Kubernetes default namespace.
	 * When the pod becomes "Running" we will return string "Now: phase=Running". 
	 * more on Pod phases here --> http://kubernetes.io/docs/user-guide/pod-states/ 
	 * 
	 * @deprecated use {@link #notifyWhenThePodBecomesRunning(@PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace, @PathParam("theNameOfThePod") String podName) } instead.  
	 */
	@Deprecated	
	@POST
	@Path("notifyWhenThePodInDefaultNamespaceBecomesRunning")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)	
	public Response notifyWhenThePodInDefaultNamespaceBecomesRunning(PodNameIdData podNameId) {
		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}

		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(podNameId.getClusterID());
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO INSTANTIATE A CONNECTION TO CLUSTER AND THEN WAIT UNTIL THE POD IS IN PHASE "Running" (AND RETURN "Now") OR RETURN "Never"
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, "default");
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			try {				
				//get the status of pod now, if the status is "Pending" then register a listener and wait until it becomes "Running". If the pod is in phase
				//"Succeeded" or "Failed" - then just return (because it will never reach "Running" phase).
				PodIsRunningInfo podIsRunningInfo=MyUtilities.notifyWhenPodRunning(kube, podNameId.getPodName());
				return Response.ok(podIsRunningInfo).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();
			} finally {
				kube.close();			
			}	
		}
		else{
			throw new WebApplicationException();
		}		
	}	




	/**
	 *
	 * when a POST HTTP request requiring MediaType.APPLICATION_JSON will arrive at URL http://localhost:8080/KubernetesFabric8RESTapi/API/v01/getPodStatusInDefaultNamespace
	 * the function getPodStatusInDefaultNamespace(PodNameIdData podNameId) will be called
	 * this request is carrying the JSON data (the serialized PodNameIdData object)
	 *
	 * the method takes parameter from the request, obtains the required certificates from the Knowledge Base, and then checks what is the status of the Pod 
	 * with given name. The valid Pod phases (see http://kubernetes.io/docs/user-guide/pod-states/) are Pending, Running, Succeeded, Failed, Unknown
	 * If the pod with given name does not exists in the default namespace of the cluster with clusterId - then we will return "Pod does not exist".
	 *
	 * @deprecated use {@link #getPodStatus(@PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace, @PathParam("theNameOfThePod") String podName) } instead.  
	 */
	@Deprecated	
	@POST
	@Path("getPodStatusInDefaultNamespace")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)	
	public Response getPodStatusInDefaultNamespace(PodNameIdData podNameId) {
		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}

		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(podNameId.getClusterID());
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO INSTANTIATE A CONNECTION TO CLUSTER AND THEN WAIT UNTIL THE POD IS IN PHASE "Running" (AND RETURN "Now") OR RETURN "Never"
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, "default");
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			try {				
				//get the status of pod now.
				String podStatus=MyUtilities.getPodStatus(kube, podNameId.getPodName());
				return Response.ok(podStatus).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();
			} finally {
				kube.close();			
			}	
		}
		else{
			throw new WebApplicationException();
		}		
	}



	/**
	 *	
	 * when a POST HTTP request requiring MediaType.APPLICATION_JSON will arrive at URL http://localhost:8080/KubernetesFabric8RESTapi/API/v01/getNodePortsUsedByService
	 * the function getNodePortsUsedByService(ServiceNameIdData srvNameId) will be called
	 * the request is carrying information about clusterID and service name - of which we want to find the NodePorts 
	 * 
	 * the method obtains a list of NodePort ports used by the service with the given name (and in the cluster with given clusterID). 
	 * If the service has no NodePorts associated an empty list will be returned.
	 * If the service with given name does not exist in cluster with given clusterID then null (which will get serialized into an empty string) will be returned.
	 *	 
	 * @deprecated use {@link #getNodePortsUsedByService(@PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace, @PathParam("theNameOfTheService") String srvName)  } instead.  
	 */
	@Deprecated		
	@POST
	@Path("getNodePortsUsedByService")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)	
	public Response getNodePortsUsedByService(ServiceNameIdData srvNameId) {
		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}

		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(srvNameId.getClusterID());
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO INSTANTIATE A CONNECTION TO CLUSTER AND THEN OBTAIN A LIST OF NodePorts THAT ARE ASSOCIATED WITH THE SERVICE WITH GIVEN NAME
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, "default");
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			try {				
				List<Integer> nodePorts=MyUtilities.getNodePortsOfTheService(kube, srvNameId.getServiceName());
				return Response.ok(nodePorts).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();
			} finally {
				kube.close();			
			}	
		}
		else{
			throw new WebApplicationException();
		}		
	}








	/**
	 *	
	 * when a POST HTTP request requiring MediaType.APPLICATION_JSON will arrive at URL http://localhost:8080/KubernetesFabric8RESTapi/API/v01/getFreeNodePorts
	 * the function getFreeNodePortS(String clusterId) will be called
	 * the request is carrying information about clusterID where we want to find free ports 
	 *
	 * the method obtains a list of free NodePort ports in the cluster defined by clusterID in request. 
	 * These ports will be (by default) in the range from 30000 to 32767 - see http://kubernetes.io/docs/user-guide/services/ 
	 * If no free ports are available then an empty list will be returned.
	 *	 
	 * @deprecated use {@link #getFreeNodePorts(@PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace) } instead.  
	 */
	@Deprecated		
	@POST
	@Path("getFreeNodePorts")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	//-----TODO: THERE MIGHT BE PROBLEMS BY THIS APPROACH DUE TO ASYNCHRONOUS NATURE OF KUBERNETES API
	//For example one client wants to make a service and queries for a free port beforehand. He finds that the port 30001. The client then starts to make
	//a service using this free port. Just one milisecond after that another client queries which ports are still available - hi might get information that port 
	//30001 is still not used - because the first client did not finish creating the service yet.
	public Response getFreeNodePorts(String clusterId) {
		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}

		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(clusterId);
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO INSTANTIATE A CONNECTION TO CLUSTER AND THEN OBTAIN A FREE PORT AND RETURN IT
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, "default");
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			try {				
				List<Integer> freePorts=MyUtilities.getFreeNodePortsInNamespaceAndCluster(kube);
				return Response.ok(freePorts).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();
			} finally {
				kube.close();			
			}	
		}
		else{
			throw new WebApplicationException();
		}		
	}










	//---------OK - THE METHODS WRITTEN ABOVE ARE BASICALLY DOING THE SAME THINGS AS THE METHODS BELOW. BUT THEY ARE NOT VERY RESTFULL -------------
	//---------------check how this is done in proper way here - http://kubernetes.io/kubernetes/third_party/swagger-ui/#/--------------------------
	//----------------------------LETS TRY MAKE IT BETTER-------------------------------------------------------------------------------------------
	//---------------------------see also CcgMini project for more info-----------------------------------------------------------------------------


	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	//The pod should be created by POST-ing Pod specification to the address http://IP:PORT/KubernetesFabric8RESTapi/API/v01/{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/pods
	//so we should define the "variables" in the URL path and then use these variables
	@Path("{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/pods")	
	public Response startPod(Pod podSpec, @PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace) {
		String theNameOfCreatedPod=podSpec.getMetadata().getName();
		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}


		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(clusterId);
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO INSTANTIATE A POD IN THE RIGHT CLUSTER AND RIGHT NAMESPACE
		//based on the type of credentials and the Kubernetes namespace we want to use appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, namespace);
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			if(theNameOfCreatedPod==null){
				theNameOfCreatedPod=MyUtilities.generateKubernetesFriendlyShortUuid(24);
			}
			try {				
				//create the pod now
				//MyUtilities.createThePod(kube, uniqueIdentifierOfPod);
				MyUtilities.createThePod(kube, theNameOfCreatedPod, podSpec);	
				return Response.ok(theNameOfCreatedPod).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();				
			} finally {
				kube.close();			
			}

		}
		else{
			throw new WebApplicationException();
		}	
	}





	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	//The pod should be deleted by sending DELETE request to the address http://IP:PORT/KubernetesFabric8RESTapi/API/v01/{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/pods/{theNameOfThePod}
	//so we should define the "variables" in the URL path and then use these variables
	@Path("{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/pods/{theNameOfThePod}")	
	public Response stopPodByName(@PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace, @PathParam("theNameOfThePod") String podName) {

		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}

		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(clusterId);
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO DELETE THE RIGHT POD IN THE RIGHT CLUSTER
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, namespace);
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			try {				
				//delete the pod now
				MyUtilities.deletePodByName(kube, podName);
				return Response.ok(podName).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();
			} finally {
				kube.close();			
			}	
		}
		else{
			throw new WebApplicationException();
		}
	}





	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	//The service should be created by POST-ing Service specification to the address http://IP:PORT/KubernetesFabric8RESTapi/API/v01/{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/services
	//so we should define the "variables" in the URL path and then use these variables
	@Path("{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/services")	
	//the method takes parameter from the request, obtains the required certificates from the Knowledge Base, and then instantiates the Service in the chosen cluster.
	//if the name of the Service specified in the arriving io.fabric8.kubernetes.api.model.Service object was NOT ASSIGNED then we will create a RANDOM SERVICE NAME and assign it to the Service!
	public Response startService(Service srvSpec, @PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace) {
		String theNameOfCreatedService=srvSpec.getMetadata().getName();
		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}


		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(clusterId);
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO INSTANTIATE A SERVICE IN THE RIGHT CLUSTER AND RIGHT NAMESPACE
		//based on the type of credentials and the Kubernetes namespace we want to use appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, namespace);
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			if(theNameOfCreatedService==null){
				theNameOfCreatedService=MyUtilities.generateKubernetesFriendlyShortUuid(24);
			}
			try {				
				//create the service now
				//MyUtilities.createThePod(kube, uniqueIdentifierOfPod);
				MyUtilities.createTheService(kube, theNameOfCreatedService, srvSpec);	
				return Response.ok(theNameOfCreatedService).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();				
			} finally {
				kube.close();			
			}

		}
		else{
			throw new WebApplicationException();
		}			
	}



	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	//The service should be deleted by sending DELETE request to the address http://IP:PORT/KubernetesFabric8RESTapi/API/v01/{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/services/{theNameOfTheService}
	//so we should define the "variables" in the URL path and then use these variables
	@Path("{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/services/{theNameOfTheService}")	
	public Response stopServiceByName(@PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace, @PathParam("theNameOfTheService") String srvName) {

		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}

		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(clusterId);
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO DELETE THE RIGHT SERVICE IN THE RIGHT CLUSTER
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, namespace);
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			try {				
				//delete the service now
				MyUtilities.deleteServiceByName(kube, srvName);
				return Response.ok(srvName).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();
			} finally {
				kube.close();			
			}	
		}
		else{
			throw new WebApplicationException();
		}
	}




	@GET
	@Produces(MediaType.APPLICATION_JSON)
	//The external IP of the host machine where Pod is running should be retrieved by sending GET request to the address http://IP:PORT/KubernetesFabric8RESTapi/API/v01/{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/pods/{theNameOfThePod}/hostIP
	//so we should define the "variables" in the URL path and then use these variables
	@Path("{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/pods/{theNameOfThePod}/hostIP")	
	public Response getPodHostIP(@PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace, @PathParam("theNameOfThePod") String podName) {

		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}

		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(clusterId);
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO INSTANTIATE A CONNECTION AND THEN OBTAIN THE IP OF HOST WHERE THE POD IS RUNNING
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, namespace);
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			try {				
				//get the pod IP now
				String ip=MyUtilities.getPodHostExternalIP(kube, podName);
				return Response.ok(ip).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();
			} finally {
				kube.close();			
			}	
		}
		else{
			throw new WebApplicationException();
		}		
	}





	@GET
	@Produces(MediaType.APPLICATION_JSON)
	//The notification when a certain Pod reaches the "running" state should be retrieved by sending GET request to the address http://IP:PORT/KubernetesFabric8RESTapi/API/v01/{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/pods/{theNameOfThePod}/notifyWhenRunning
	//so we should define the "variables" in the URL path and then use these variables
	@Path("{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/pods/{theNameOfThePod}/notifyWhenRunning")	
	public Response notifyWhenThePodBecomesRunning(@PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace, @PathParam("theNameOfThePod") String podName) {

		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}

		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(clusterId);
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO INSTANTIATE A CONNECTION TO CLUSTER AND THEN WAIT UNTIL THE POD IS IN PHASE "Running" (AND RETURN "Now") OR RETURN "Never"
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, namespace);
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			try {				
				//get the status of pod now, if the status is "Pending" then register a listener and wait until it becomes "Running". If the pod is in phase
				//"Succeeded" or "Failed" - then just return (because it will never reach "Running" phase).
				PodIsRunningInfo podIsRunningInfo=MyUtilities.notifyWhenPodRunning(kube, podName);
				return Response.ok(podIsRunningInfo).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();
			} finally {
				kube.close();			
			}	
		}
		else{
			throw new WebApplicationException();
		}		
	}


	//TODO: create a method that watches when the service becomes "ACTIVE"
	//-----> DONE SOME EXPERIMENTS AND SEEMS THAT THIS IS NOT NEEDED - AS SOON AS YOU CREATE THE SERVICE IT SHOULD BECOME AVAILABLE
	//-----> ADDITIONAL PROBLEM IS THAT THIS METHOD COULD POTENTIALLY BLOCK FOREVER. AS SOON AS YOU CREATE THE SERVICE THE "ACTIVE" EVENT HAPPENS
	//-----> AND THEN NO NEW EVENTS SHOULD HAPPEN UNTIL THE SERVICE IS DELETED AND AN "DELETED" EVENT HAPPENS. SO IF WE REGISTER A LISTENER AFTER
	//-----> THE "ADDED" EVENT ALREADY HAPPENED - THEN THE LISTENRE WILL BLOCK FOREVER (UNTIL DELETED EVENT HAPPENS) - AND THIS IS NO GOOD!
	//-----> THE PROBLEM IS THAT YOU CANNOT CHECK THE "STATUS" OF THE SERVICE - LIKE WE DID FOR THE POD (SO YOU CANNOT CHECK WHETHER IT WAS ALREADY "ADDED" OR NOT)



	@GET
	@Produces(MediaType.APPLICATION_JSON)
	//The Pod status should be retrieved by sending GET request to the address http://IP:PORT/KubernetesFabric8RESTapi/API/v01/{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/pods/{theNameOfThePod}/status
	//so we should define the "variables" in the URL path and then use these variables
	@Path("{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/pods/{theNameOfThePod}/status")	
	public Response getPodStatus(@PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace, @PathParam("theNameOfThePod") String podName) {

		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}

		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(clusterId);
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO INSTANTIATE A CONNECTION TO THE RIGHT CLUSTER AND NAMESPACE AND 
		//IF POD IS RUNNING RETURN IT'S PHASE OTHERWISE RETURN "This pod does not exist!"
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, namespace);
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			try {				
				//get the status of pod now.
				String podStatus=MyUtilities.getPodStatus(kube, podName);
				return Response.ok(podStatus).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();
			} finally {
				kube.close();			
			}	
		}
		else{
			throw new WebApplicationException();
		}		
	}




	@GET
	@Produces(MediaType.APPLICATION_JSON)
	//A list of NodePort ports used by certain Service should be obtained by sending GET request to the address http://IP:PORT/KubernetesFabric8RESTapi/API/v01/{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/services/{theNameOfTheService}/nodePorts
	//so we should define the "variables" in the URL path and then use these variables
	//If there is not a single NodePort associated with the service then an empty list is retunred.
	//If we gave the wrong service name (and the service doest not exist) then an exception is thrown
	//TODO: I should consolidate exception handling also in other methods (so that if the resource we are targeting does not exist shoudl return exception - not some String or empty or null object)
	@Path("{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/services/{theNameOfTheService}/nodePorts")	
	public Response getNodePortsUsedByService(@PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace, @PathParam("theNameOfTheService") String srvName) {

		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}

		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(clusterId);
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO INSTANTIATE A CONNECTION TO CLUSTER AND THEN OBTAIN A LIST OF NodePorts THAT ARE ASSOCIATED WITH THE SERVICE WITH GIVEN NAME
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, namespace);
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			try {				
				List<Integer> nodePorts=MyUtilities.getNodePortsOfTheService(kube, srvName);
				return Response.ok(nodePorts).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();
			} finally {
				kube.close();			
			}	
		}
		else{
			throw new WebApplicationException();
		}			
	}	




	@GET
	@Produces(MediaType.APPLICATION_JSON)
	//A list of available/free NodePort ports in a given cluster/namespace should be obtained by sending GET request to the address http://IP:PORT/KubernetesFabric8RESTapi/API/v01/{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/availableNodePorts
	//so we should define the "variables" in the URL path and then use these variables
	//If there is not a single NodePort available then an empty list is retunred.
	//If we gave the wrong cluster/namespace name then an exception is thrown
	@Path("{theIdOfTheCluster}/namespaces/{theNameOfNamespace}/availableNodePorts")	
	//-----TODO: THERE MIGHT BE PROBLEMS BY THIS APPROACH DUE TO ASYNCHRONOUS NATURE OF KUBERNETES API
	//For example one client wants to make a service and queries for a free port beforehand. He finds that the port 30001. The client then starts to make
	//a service using this free port. Just one milisecond after that another client queries which ports are still available - hi might get information that port 
	//30001 is still not used - because the first client did not finish creating the service yet.
	public Response getFreeNodePorts(@PathParam("theIdOfTheCluster") String clusterId, @PathParam("theNameOfNamespace") String namespace) {

		//get the certificates from the Knowledge Base (these are in the Json format)
		//TODO: implement the retrieval of certificates from the REAL knowledge base 
		//the JSON format to obtain from KB is like this: {"masterUrl":"https://194.249.0.45","clientPublicCred":"the client public credential","clientPrivateCred":"the client private credential","credentialsType":"PASSWORD","certificateAuthority":"the caCert"}

		//QUERY KNOWLEDGE BASE FOR THE CERTIFICATES
		ClusterCredentials cred;
		try {
			cred = MyUtilities.getClusterCredentials(clusterId);
		} catch (IOException e1) {
			throw new WebApplicationException();
		}

		//NOW USE THE OBTAINED CREDENTIALS TO INSTANTIATE A CONNECTION TO CLUSTER AND THEN OBTAIN A FREE PORT AND RETURN IT
		//based on the type of credentials and the Kubernetes namespace we want to use create appropriate io.fabric8.kubernetes.client.Config object
		Config kuberConf= MyUtilities.getKubernetesConfig(cred, namespace);
		if(kuberConf!=null){
			//TODO: we are making new KubernetesClient for every incoming HTTP request - which is bad. I think that we should have all the clients for all our
			//clusters permanently opened (in some global class) - and based on the information of clusterID we should only select appropriate client object and then
			//use it to send the request!
			KubernetesClient kube = new DefaultKubernetesClient(kuberConf);
			try {				
				List<Integer> freePorts=MyUtilities.getFreeNodePortsInNamespaceAndCluster(kube);
				return Response.ok(freePorts).build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException();
			} finally {
				kube.close();			
			}	
		}
		else{
			throw new WebApplicationException();
		}				
	}	

} 
