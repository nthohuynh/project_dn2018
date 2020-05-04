package utils;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import deployment.DeploymentPackage;
import deployment.Node;
import deployment.NodeList;

public class Deployment {
	NodeList nodeList;
	public Deployment(String deploymentFile) {
		
		//deploymentFile= "modelinstances//Deployment.xmi";
		
		DeploymentPackage.eINSTANCE.eClass();
		Resource resource;
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		try {
			reg.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		} catch (Exception e){
		}
		ResourceSet resourceSet = new ResourceSetImpl();
		String filename = new File(deploymentFile).getAbsolutePath();
		URI uri = URI.createFileURI(filename);
		resource = resourceSet.getResource(uri, true);
		this.nodeList = (NodeList)resource.getContents().get(0);
		
	}
	public int getApplicationPort(String component) {
		int applicationPort = 0;
		for (Node node : nodeList.getNodes()) {
			if (node.getComponentInstanceName().equals(component)) {
				return node.getApplicationPort();
			}
		}
		return applicationPort;
	}
	
	public int getControlPort(String component) {
		int controlPort = 0;
		for (Node node : nodeList.getNodes()) {
			if (node.getComponentInstanceName().equals(component)) {
				return node.getControlPort();
			}
		}
		return controlPort;
	}
	
	public String getAddress(String component) {
		String address = null;
		for (Node node : nodeList.getNodes()) {
			if (node.getComponentInstanceName().equals(component)) {
				return node.getAddressIP();
			}
		}
		return address;
	}
	public String getUUID(String component) {
		String uuid = null;
		for (Node node : nodeList.getNodes()) {
			if (node.getComponentInstanceName().equals(component)) {
				return node.getFrameworkUUID();
			}
		}
		return uuid;
	}
	public static void main(String arg[]) {
		Deployment deploy = new Deployment(null);
		System.out.println(deploy.getAddress("Compression"));
		System.out.println(deploy.getApplicationPort("Compression"));
	}
}
