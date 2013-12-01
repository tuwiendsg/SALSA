package at.ac.tuwien.dsg.cloud.salsa.tosca.extension;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/*
 * The property can be
 * 		<Properties>
 * 			<Compute>
 * 				infrastructure info
 * 			</Compute>
			<Packages>
				<Package>openjdk-7-jre</Package>
				<Package>python-pip</Package>	<!-- just for test -->
			</Packages>
		</Properties>
 */


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "VirtualMachineNodeProperties")
public class ToscaVMNodeTemplatePropertiesEntend {
	
	@XmlElement(name = "Provider")
	String cloudProvider;
	
	@XmlElement(name = "BaseImage")
	String baseImage;
	
	@XmlElement(name = "InstanceType")
	String instanceType;
	
//	@XmlElement(name = "Compute")
//	Compute compute;
	
//	@XmlElement(name = "Instances")
//	ToscaVMNodeTemplatePropertiesEntend.Instances instances;
	
	@XmlElement(name = "Packages")
	ToscaVMNodeTemplatePropertiesEntend.PackagesDependencies packagesDependencies;
	
//	
//	@XmlAccessorType(XmlAccessType.FIELD)
//	@XmlType(name = "Instances")
//	public static class Instances{
//		@XmlElement(name = "Instances")
//		List<SalsaInstanceDescription> instance;
//
//		public List<SalsaInstanceDescription> getInstance() {
//			return instance;
//		}
//
//		public void setInstance(List<SalsaInstanceDescription> instance) {
//			this.instance = instance;
//		}
//		
//	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "Packages")
	public static class PackagesDependencies{
		
		@XmlElement(name = "Package")
		List<String> packageDependency;

		
		public List<String> getPackageDependency() {
			return packageDependency;
		}

		public void setPackageDependency(List<String> packageDependency) {
			this.packageDependency = packageDependency;
		}

		@Override
		public String toString() {
			return "PackagesDependencies [packageDependency="
					+ packageDependency + "]";
		}			
		
	}


//	public ToscaVMNodeTemplatePropertiesEntend.Instances getInstances() {
//		return instances;
//	}
//
//	public void setInstances(ToscaVMNodeTemplatePropertiesEntend.Instances instances) {
//		this.instances = instances;
//	}

	public PackagesDependencies getPackagesDependencies() {
		return packagesDependencies;
	}

	public void setPackagesDependencies(PackagesDependencies packagesDependencies) {
		this.packagesDependencies = packagesDependencies;
	}

	public String getCloudProvider() {
		return cloudProvider;
	}

	public void setCloudProvider(String cloudProvider) {
		this.cloudProvider = cloudProvider;
	}

	public String getBaseImage() {
		return baseImage;
	}

	public void setBaseImage(String baseImage) {
		this.baseImage = baseImage;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	@Override
	public String toString() {
		return "ToscaVMNodeTemplatePropertiesEntend [cloudProvider="
				+ cloudProvider + ", baseImage=" + baseImage
				+ ", instanceType=" + instanceType + ", packagesDependencies=" + packagesDependencies + "]";
	}
	
	
}
