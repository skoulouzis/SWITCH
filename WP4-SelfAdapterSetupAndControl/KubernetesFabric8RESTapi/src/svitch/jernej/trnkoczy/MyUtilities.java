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

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

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


public class MyUtilities {

	public static String generateKubernetesFriendlyShortUuid(int numberOfCharsInOutputString) {
		UUID uuid = UUID.randomUUID();
		String orig=uuid.toString();
		String str=orig.replaceAll("-", "");//remove the unnecessary hyphens
		char[] strip=str.toCharArray();        
		char[] shorten=new char[numberOfCharsInOutputString];
		for (int i = 0; i < numberOfCharsInOutputString; i++) {
			//the first character in the string has to be a letter not a number
			if(i==0){
				char curr;
				do{
					Random r = new Random();
					int ranNum = r.nextInt(32);//there should be 32 characters in the "stripped version" of UUID string, and we pick char in random position - so we need numbers from 0...31
					curr=strip[ranNum];
					shorten[i]=curr;	
				}
				while(!Character.isLetter(curr));
			}
			else{
				Random r = new Random();
				int ranNum = r.nextInt(32);//there should be 32 characters in the "stripped version" of UUID string, and we pick char in random position - so we need numbers from 0...31
				char curr=strip[ranNum];
				shorten[i]=curr;				
			}						
		}
		return new String(shorten);       
	}



	public static Config getKubernetesConfig(ClusterCredentials cred, String namespace){
		Config kuberConf=null;
		if(cred.getCredentialsType().equals(CredentialsType.CERTIFICATES)){
			kuberConf = new ConfigBuilder()
			.withMasterUrl(cred.getMasterUrl())
			.withTrustCerts(true)
			.withNamespace(namespace)		
			.withCaCertData(cred.getCertificateAuthority())
			.withClientCertData(cred.getClientPublicCred())
			.withClientKeyData(cred.getClientPrivateCred())
			.build();
		}
		else if (cred.getCredentialsType().equals(CredentialsType.PASSWORD)){
			kuberConf = new ConfigBuilder()		
			.withMasterUrl(cred.getMasterUrl())
			.withTrustCerts(true)
			.withNamespace(namespace)		
			.withCaCertData(cred.getCertificateAuthority())
			.withUsername(cred.getClientPublicCred())
			.withPassword(cred.getClientPrivateCred())
			.build();			
		}
		else{
			//nothing - we will return null object in this case
		}
		return kuberConf;		
	}


	//this is for testing purposes only - since the pod it creates is "fixed" - i.e. mcu pod
	public static void createThePod(KubernetesClient kubecl, String podId){
		Map<String, String> labels=new HashMap<String, String>();
		labels.put("id", podId);
		labels.put("app", "testmcu");

		Map<String, Quantity> resourceRequests=new HashMap<String, Quantity>();
		resourceRequests.put("cpu", new Quantity("100m"));
		resourceRequests.put("memory", new Quantity("100Mi"));
		Pod pod=new PodBuilder()
		.withNewMetadata()
		.withName(podId)
		.withLabels(labels)
		.endMetadata()
		.withNewSpec()
		.addNewContainer()
		.withName("mcu-kontejner")
		.withImage("jernejtrnkoczy/jitsimeet004")
		.withNewResources().withRequests(resourceRequests).endResources()
		.addNewEnv().withName("GET_HOSTS_FROM").withValue("dns").endEnv()
		.addNewPort().withName("mcutcpport").withContainerPort(444).withProtocol("TCP").withHostPort(444).endPort()
		.addNewPort().withName("mcuudpport").withContainerPort(10000).withProtocol("UDP").withHostPort(10000).endPort()
		.endContainer()
		.endSpec()			
		.build();
		kubecl.pods().create(pod);	
	}	


	//this method uses the kubernetes client (which is of course already connected to the right cluster) to submit the Pod specification (in default Kubernetes namespace).
	//it takes Pod specification as input, adds the name of the Pod to this specification and then submits this to the right Kubernetes cluster.
	public static void createThePod(KubernetesClient kubecl, String podId, Pod pod){
		pod.getMetadata().setName(podId);		
		kubecl.pods().create(pod);	
	}



	//this method uses the kubernetes client (which is of course already connected to the right cluster) to submit the Service specification (in default Kubernetes namespace).
	//it takes Service specification as input, adds the name of the Service to this specification and then submits this to the right Kubernetes cluster.
	public static void createTheService(KubernetesClient kubecl, String podId, Service srv){
		srv.getMetadata().setName(podId);		
		kubecl.services().create(srv);	
	}


	//this method uses the kubernetes client (which is of course already connected to the right cluster) to delete the Pod (in default Kubernetes namespace) with
	//the given name.
	public static void deletePodByName(KubernetesClient kubecl, String podName) throws Exception{
		if(kubecl.pods().withName(podName).get()!=null){
			kubecl.pods().withName(podName).delete();
		}
		else{
			throw new Exception("The pod that you wanted to stop does not exist!");
		}
	}

	//this method uses the kubernetes client (which is of course already connected to the right cluster) to delete the Service (in default Kubernetes namespace) with
	//the given name.
	public static void deleteServiceByName(KubernetesClient kubecl, String srvName) throws Exception{
		if(kubecl.services().withName(srvName).get()!=null){
			kubecl.services().withName(srvName).delete();
		}
		else{
			throw new Exception("The service you wanted to stop does not exist!");
		}
	}


	//this method uses the kubernetes client (which is of course already connected to the right cluster) to obtain the IP of host machine where a Pod with given
	//name is running.
	//TODO: this method is "provider specific" - which means that results will depend on cluster provider. See also comments below in the code. To make it "provider-generic"
	//we should invest more effort (and I do not know if it is possible at all). More here - https://github.com/kubernetes/kubernetes/issues/9267 - citing:
	//Only Openstack and AWS set InternalIP. Openstack and AWS also set ExternalIP, as does GCE. LegacyHostIP is set in a variety of ways in different cloudprovider
	//implementations. AWS sets LegacyHostIP to the InternalIP. In the case of Mesos, Ovirt, Vagrant, and Rackspace, they only set LegacyHostIP. In the case of no 
	//cloudprovider, the --hostname-override flag in Kubelet sets LegacyHostIP. In most of these cases, we don't know whether the LegacyHostIP is internal or external.
	//TODO: could be made more efficient - instead of looping through the lists....
	public static String getPodHostExternalIP(KubernetesClient kubecl, String podName){
		String publicallyRoutableIP=null;
		//first check if the pod with this name exists 
		if(kubecl.pods().withName(podName).get()!=null){
			//then check if the pod is in the phase "running" - if not then there is no point returning the IP - and we will rather return null
			if(kubecl.pods().withName(podName).get().getStatus().getPhase().equals("Running")){
				//obtain the IP where the pod is running. But depending on the cluster this will return different things. In GCP this will return the private IP of the host 
				//machine (they have private networking besides public). In Arnes this will return public IP of host machine (because on Arnes we cannot configure private
				//network) - and so on. So it is "provider specific" again...
				String hostIP=kubecl.pods().withName(podName).get().getStatus().getHostIP();

				//OK - we need to find public IP of the host machine where the pod is running. Obviously we cannot get this info from the "Pod". So we need to take the IP
				//that we obtained above and try to find it in the metadata information about all the nodes constituting the cluster. When we find it - then we can try to
				//obtain the public IP from the list.
				List<Node> nodes=kubecl.nodes().list().getItems();
				for (int i=0 ; i<nodes.size() ; i++) {
					Node node = (Node) nodes.get(i);
					//Each node in the cluster can have many addresses associated. These are of various Types - The official documentation http://kubernetes.io/docs/admin/node/#addresses
					//says that the Types could be Hostname, ExternalIP or InternalIP. However Arnes rather returns the type LegacyHostIP for all the addresses associated with node.
					//Here you can read that if this happens the cluster is actually misconfigured - https://github.com/kubernetes/kubernetes/issues/9267 
					List<NodeAddress> nodeAdresses=node.getStatus().getAddresses();
					for (int k=0 ; k<nodeAdresses.size() ; k++) {
						NodeAddress nodeAddress = nodeAdresses.get(k);
						String curAdressValue=nodeAddress.getAddress();					

						//if the current address equals the address that we obtained from the pod - then immediately loop through the list of addresses again and try to find 
						//an address with type of ExternalIP that belongs to this node. But if the address of type ExternalIP cannot be found then tra to find the
						//address of type LegacyHostIP
						if(curAdressValue.equals(hostIP)){
							String externalIP=null;
							String legacyHostIP=null;

							for (int j=0 ; j<nodeAdresses.size() ; j++) {
								NodeAddress nAddress = nodeAdresses.get(j);
								String cAdressType=nAddress.getType();
								String cAdressValue=nAddress.getAddress();						
								if(cAdressType.equals("LegacyHostIP")){
									legacyHostIP=cAdressValue;
								}
								else if(cAdressType.equals("ExternalIP")){
									externalIP=cAdressValue;
								}
							}
							if(externalIP!=null){
								publicallyRoutableIP=externalIP;
							}
							else if(legacyHostIP!=null){
								publicallyRoutableIP=legacyHostIP;
							}
							else{
								//just do nothing and we will return the null value in publicallyRoutableIP
							}
							break;
						}

					}
					if(publicallyRoutableIP!=null){
						break;
					}
				}
			}
		}
		return publicallyRoutableIP;
	}


	public static PodIsRunningInfo notifyWhenPodRunning(KubernetesClient kubecl, String podName) throws Exception{
		//first check if the pod with this name exists 
		if(kubecl.pods().withName(podName).get()!=null){
			//if the pod exists then check what is the current phase of pod - if it is "running" then we can return immediately (without registering the listener)
			//more about possible Pod phases here --> http://kubernetes.io/docs/user-guide/pod-states/
			if(kubecl.pods().withName(podName).get().getStatus().getPhase().equals("Running")){
				PodIsRunningInfo p=new PodIsRunningInfo(true, "Running");
				return p;

			}
			//Succeeded: All containers in the pod have terminated in success, and will not be restarted.
			else if(kubecl.pods().withName(podName).get().getStatus().getPhase().equals("Succeeded")){
				PodIsRunningInfo p=new PodIsRunningInfo(false, "Succeeded");
				return p;

			}
			//Failed: All containers in the pod have terminated, at least one container has terminated in failure (exited with non-zero exit status or was terminated by the system).
			else if(kubecl.pods().withName(podName).get().getStatus().getPhase().equals("Failed")){
				PodIsRunningInfo p=new PodIsRunningInfo(false, "Failed");
				return p;

			}
			//Pending: The pod has been accepted by the system, but one or more of the container images has not been created. This includes time before being scheduled as well as time spent downloading images over the network, which could take a while.
			//Unknown: For some reason the state of the pod could not be obtained, typically due to an error in communicating with the host of the pod.
			//so in this case we have to register a listener and wait for the pod to become "Running"
			else if((kubecl.pods().withName(podName).get().getStatus().getPhase().equals("Pending")) || (kubecl.pods().withName(podName).get().getStatus().getPhase().equals("Unknown"))){
				PodsWatcher podWatcher=new PodsWatcher();
				//JUST REGISTER WATCHER ON THIS PARTICULAR POD (NOT ON ALL PODS IN CLUSTER)
				Watch w=kubecl.pods().withName(podName).watch(podWatcher);
				//once we get the right pod and register a watcher on it - we can close the KubernetesClient
				kubecl.close();	
				//we go into a deadly loop that "breaks" only when the pod status becomes "Running" --> the "watcher" will then set it's variable podWatcher.thePodIsUpAndRunning to value "true" 
				while(true){
					try {
						Thread.sleep(500);
						if(podWatcher.thePodIsUpAndRunning==true){											
							break;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//when we finally break out of the deadly loop - we can be sure that the pod is "up and running"
				//so close the "watcher" and then return the PodIsRunningInfo object that tells that the Pod is now running
				w.close();
				PodIsRunningInfo p=new PodIsRunningInfo(true, "Running");
				return p;
			}
			else{
				throw new Exception ("The pod is in a phase that we do not know about (i.e. not in Running or Succeeded or Failed or Pending or Unknown). This is most probably an exception!");
			}
		}
		else{
			//ok if we asked for the information about some Pod that does not exist in the namespace/cluster --> then we must inform the client
			//however this is not exception --> so we will rather return the PodIsRunningInfo object
			PodIsRunningInfo p=new PodIsRunningInfo(false, "The pod for which you wanted to get the phase information does not exist!");
			return p;			
		}
	}
	
	
	
	public static String getPodStatus(KubernetesClient kubecl, String podName) throws Exception{
		//first check if the pod with this name exists 
		if(kubecl.pods().withName(podName).get()!=null){
			//if the pod exists then check what is the current phase of pod 
			return kubecl.pods().withName(podName).get().getStatus().getPhase();
		}
		else{
			//ok if we asked for the information about some Pod that does not exist in the default namespace/cluster --> then we must inform the client
			//however this is not exception --> so we will rather return the string with info. that pod does not exist
			return "This pod does not exist!";		
		}
	}



	public static ClusterCredentials getClusterCredentials (String clusterID) throws IOException{
		//how-to properties files and Servlets - see http://stackoverflow.com/questions/2161054/where-to-place-and-how-to-read-configuration-resource-files-in-servlet-based-app/2161583#2161583
		//see also http://javahonk.com/load-properties-file-servlet-java/
		//I have put the file in the classpath - just dropped it in the src folder in my Eclipse project		
		Properties properties = new Properties();

		properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("KubernetesFabric8RESTapi.properties"));

		//String uri="http://localhost:8080/KubernetesFabric8RESTapi/AdaptationAPI/switch/getCredentials";
		String uri=properties.getProperty("KBapiURL"); 

		//TODO: I think that I should not make a new client for every call of this method (instead I should have one global client object which should of course be thread safe)
		ClientConfig config = new ClientConfig().register(LoggingFilter.class);
		Client client = ClientBuilder.newClient(config);
		WebTarget service = client.target(UriBuilder.fromUri(uri).build());
		Invocation.Builder invocationBuilder =  service.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.json(clusterID));
		ClusterCredentials cred = response.readEntity(ClusterCredentials.class);
		if(cred==null){
			throw new IOException("The credentials obtained from the KB are null!");
		}
		return cred;
	}


	public static List<Integer> getFreeNodePortsInNamespaceAndCluster (KubernetesClient kubecl) {
		//the "valid port range" by default is 30000-32767 (https://coreos.com/kubernetes/docs/latest/kubernetes-networking.html)
		//it is possible to expand this range as explained here - https://github.com/kubernetes/kubernetes/issues/11690 - but is tricky and can lead to hard-to-debug failures
		//Also some ports can already be taken by the other Kubernetes services
		//So lets first find a port that can be used (= is in range 30000-32767 and is not already taken)		
		int end=32767;
		int begin=30000;
		List<Integer> validRangePorts = new ArrayList<Integer>(end - begin + 1);
		for(int i = begin-1; i <= end-1; i++, validRangePorts.add(i));
		//now get the ports that are already taken
		List<Integer> takenPortsList=new ArrayList<Integer>();
		ServiceList services = kubecl.services().list();
		List<Service> serviceItems = services.getItems();
		for (Service service : serviceItems) {
			//it seems like every service can have many ports defined and each of those ports can have it's own nodePort.
			//for example Integer nodePort = service.getSpec().getPorts().get(0).getNodePort(); will get the nodePort of the first port defined for the service.  
			//But because there could be potentially many of these - wee need to iterate.			
			List<ServicePort> listOfServicePorts=service.getSpec().getPorts();
			for (ServicePort individualPort : listOfServicePorts) {
				Integer currentNodePort=individualPort.getNodePort();
				//this can be null - because some "ports" of services have no "external" ports (=nodePort) defined
				if(currentNodePort!=null){
					takenPortsList.add(currentNodePort);
				}
			}			
		}
		//then remove from the original list all the ports that are already taken by Kubernetes
		validRangePorts.removeAll(takenPortsList);

		return validRangePorts;

	}



	public static List<Integer> getNodePortsOfTheService (KubernetesClient kubecl, String srvName) throws Exception {
		List<Integer> nodePorts=new ArrayList<Integer>();
		Service srv=kubecl.services().withName(srvName).get();
		if(srv!=null){
			//find out what are the exposed ports (NodePorts) of this service --> take into account that one service can be exposed on many ports
			List<ServicePort> listOfResourcePorts=srv.getSpec().getPorts();
			for (ServicePort individualPort : listOfResourcePorts) {
				Integer currNodePort=individualPort.getNodePort();
				//this can be null - because some "ports" of services have no "external" ports (=nodePort) defined
				if(currNodePort!=null){
					nodePorts.add(currNodePort);
				}
			}
		}
		else{
			//if the service with given name does not exist then we will throw an exception
			throw new Exception("The service with given name does not exist!");
		}
		return nodePorts;
	}





	//This is an inner class - an instance of this class represents the listener to events related to Pods
	//TODO: I am not totally sure if this is a reliable way of checking when the pod is "up and running"
	static class PodsWatcher implements Watcher<Pod>{
		//"semaphore" used to identify when the pod is "up and running" 
		boolean thePodIsUpAndRunning=false;		

		@Override
		public void eventReceived(Action action, Pod resource) {
			String podStatus=resource.getStatus().getPhase();

			if(!action.toString().equals("DELETED")){
				if(podStatus!=null){
					if(podStatus.equals("Running")){
						this.thePodIsUpAndRunning=true;						
					}
				}

			}
		}

		@Override
		public void onClose(KubernetesClientException e) {
			if (e != null) {
				e.printStackTrace();
				System.out.println("Somebody closed the watcher and this is obviously an exception!");				
			}
		}
	}



}
