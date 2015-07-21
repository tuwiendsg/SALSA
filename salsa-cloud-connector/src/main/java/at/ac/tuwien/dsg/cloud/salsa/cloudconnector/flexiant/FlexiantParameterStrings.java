/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.cloudconnector.flexiant;

import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.ParameterStringsEnumInterface;


public enum FlexiantParameterStrings implements ParameterStringsEnumInterface {
	EMAIL("email"),
	CUSTOMER_UUID("customerUUID"),	
	PASSWORD("password"),
	ENDPOINT("endpoint"),
	VDC_UUID("vdcUUID"),
	DEFAULT_PRODUCT_OFFER_UUID("defaultProductOfferUUID"),
	CLUSTER_UUID("clusterUUID"),
	NETWORK_UUID("networkUUID"),
	SSH_KEY("sshkey");
	
	private String value;
	
	private FlexiantParameterStrings(String value){
		this.value = value;
	}
	
	public String getString() {
		return value;
	}

	@Deprecated
	public static FlexiantParameterStrings fromString(String text) {
	    if (text != null) {
	      for (FlexiantParameterStrings b : FlexiantParameterStrings.values()) {
	        if (text.equalsIgnoreCase(b.getString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

}
