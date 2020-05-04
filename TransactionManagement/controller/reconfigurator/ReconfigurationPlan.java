package reconfigurator;

import java.io.File;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import reconfigurationPlan.ActivationAction;
import reconfigurationPlan.GettingManagedStatusAction;
import reconfigurationPlan.GettingOrdinaryStatusAction;
import reconfigurationPlan.GettingStatusAction;
import reconfigurationPlan.ReintegrationAction;
import reconfigurationPlan.ConnectionAction;
import reconfigurationPlan.DeactivationAction;
import reconfigurationPlan.IsolationAction;
import reconfigurationPlan.Plan;
import reconfigurationPlan.ReconfigurationPlanPackage;
import reconfigurationPlan.TransferAction;

public class ReconfigurationPlan {
	Plan planPackage;
	public ReconfigurationPlan(String reconfigurationPlanFile) {
		
		reconfigurationPlanFile= "modelinstances//plan.xmi";
		
		ReconfigurationPlanPackage.eINSTANCE.eClass();
		
		Resource resource;
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		try {
			reg.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		} catch (Exception e){
		}
		ResourceSet resourceSet = new ResourceSetImpl();
		String filename = new File(reconfigurationPlanFile).getAbsolutePath();
		URI uri = URI.createFileURI(filename);
		resource = resourceSet.getResource(uri, true);
		
		//get root of variability model 
		planPackage = null;
		planPackage = (Plan) resource.getContents().get(0);
		
	}
	
	EList<IsolationAction> getIsolationAction() {
		return planPackage.getIsolation().getActions();
	}
	
	EList<ActivationAction> getActivationAction() {
		return planPackage.getActivation().getActions();
	}
	EList<ConnectionAction> getConnectionAction() {
		return planPackage.getConnection().getActions();
	}
	EList<TransferAction> getTransferActions() {
		return planPackage.getTransfer().getActions();
	}
	EList<ReintegrationAction> getReintegrationActions() {
		return planPackage.getReintegration().getActions();
	}
	EList<DeactivationAction> getDeactivationActions() {
		return planPackage.getDeactivation().getActions();
	}
	EList<GettingOrdinaryStatusAction> getGettingAllStatusActions() {
		return planPackage.getGettingStatus().getOrdinaryStatusAction();
	}	
	EList<GettingManagedStatusAction> getGettingManagedStatusActions() {
		return planPackage.getGettingStatus().getManagedStatusAction();
	}	
	
	public static void main(String arg[]) {
		ReconfigurationPlan plan = new ReconfigurationPlan(null);
		EList<ActivationAction>  activationActionList = plan.getActivationAction();
		for (ActivationAction act : activationActionList) {
			System.out.println(act.getComponent());
		}
	}
}