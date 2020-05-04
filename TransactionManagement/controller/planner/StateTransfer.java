package planner;
import java.io.File;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import statetransfer.StateTransferModel;
import statetransfer.StateTransferPoint;
import statetransfer.StatetransferPackage;
import statetransfer.Variable;
import cvl.cvlPackage;


public class StateTransfer {
	StateTransferModel stm;
	public StateTransfer(String stateTransferFile) {
		Resource resource = null;
		
		StatetransferPackage.eINSTANCE.eClass();
		
		cvlPackage.eINSTANCE.eClass();
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		try {
			//registry extent part of model file ex: *.variability
			reg.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		} catch (Exception e){
		}
		ResourceSet resourceSet = new ResourceSetImpl();
		String filename = new File(stateTransferFile).getAbsolutePath();
		URI uri = URI.createFileURI(filename);
		resource = resourceSet.getResource(uri, true);
		stm = (StateTransferModel)resource.getContents().get(0);
	}
	EList<StateTransferPoint> getStateTransferPoint() {
		return stm.getPoints();
	}
	public static void main(String arg[]) {
		StateTransfer st = new StateTransfer("modelinstances//StateTransferModel.xmi");
		for (StateTransferPoint p : st.getStateTransferPoint()) {
			System.out.println(p.getSrcVariable().get(0).getVariableHandle().getMofRef().toString());
			if (p.getDstVariable() instanceof Variable) {
				System.out.println(((Variable)p.getDstVariable()).getVariableHandle().getMofRef().toString());
				
			}
			
		}
	}
}
