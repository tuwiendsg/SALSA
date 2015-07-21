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
package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.rSYBL.deploymentDescription;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeploymentUnit")
public class DeploymentUnit {
    @XmlAttribute(name = "defaultImage")
	private String defaultImage="";
 @XmlAttribute(name = "defaultFlavor")
 private String defaultFlavor="";
 @XmlAttribute(name = "serviceUnitID")
 private String serviceUnitID="";
 @XmlElement(name = "AssociatedVM")
 private List<AssociatedVM> associatedVMs=new ArrayList<AssociatedVM>();
 @XmlElement(name = "ElasticityCapability")
 private List<ElasticityCapability> elasticityCapabilities=new ArrayList<ElasticityCapability>();
public String getDefaultFlavor() {
	return defaultFlavor;
}
public void setDefaultFlavor(String defaultFlavor) {
	this.defaultFlavor = defaultFlavor;
}
public String getDefaultImage() {
	return defaultImage;
}
public void setDefaultImage(String defaultImage) {
	this.defaultImage = defaultImage;
}
public String getServiceUnitID() {
	return serviceUnitID;
}
public void setServiceUnitID(String serviceUnitID) {
	this.serviceUnitID = serviceUnitID;
}
public List<AssociatedVM> getAssociatedVM() {
	return associatedVMs;
}
public void setAssociatedVM(List<AssociatedVM> associatedVMs) {
	this.associatedVMs = associatedVMs;
}
public void addAssociatedVM(AssociatedVM associatedVMs) {
	this.associatedVMs.add(associatedVMs);
}
public List<ElasticityCapability> getElasticityCapabilities() {
	return elasticityCapabilities;
}
public void setElasticityCapabilities(List<ElasticityCapability> elasticityCapabilities) {
	this.elasticityCapabilities = elasticityCapabilities;
}
}
