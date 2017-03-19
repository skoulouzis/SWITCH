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
public class PodNameIdData {
	private String clusterID;
	private String podName;
	

	//no arg constructor needed for serialization
	public PodNameIdData(){}

	public PodNameIdData(
			String clusterID,
			String podName				
			){
		this.clusterID=clusterID;
		this.podName=podName;		
	}



	//------------------------------GETTERS------------------------------------------------------------------------------
	public String getClusterID() {
		return this.clusterID;
	}

	public String getPodName(){
		return this.podName;
	}	
	//-------------------------------------------------------------------------------------------------------------------


}
