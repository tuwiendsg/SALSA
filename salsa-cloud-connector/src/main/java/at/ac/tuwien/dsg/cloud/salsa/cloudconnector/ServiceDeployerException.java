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
package at.ac.tuwien.dsg.cloud.salsa.cloudconnector;

public class ServiceDeployerException extends Exception {
	private static final long serialVersionUID = 5244389801522133902L;

	public ServiceDeployerException() {
	}

	public ServiceDeployerException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ServiceDeployerException(String arg0) {
		super(arg0);
	}

	public ServiceDeployerException(Throwable arg0) {
		super(arg0);
	}
}
