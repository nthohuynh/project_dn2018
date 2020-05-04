package planner;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import cvl.BCLConstraint;
import cvl.BCLExpression;
import cvl.OperationCallExp;
import cvl.VPackage;
import cvl.VPackageable;
import cvl.VSpec;
import cvl.VSpecRef;
import cvl.VariationPoint;
import cvl.cvlPackage;


public class VariabilityModel {
	VPackage vpackage = null;
	public VariabilityModel(String vSpecFileName) {
		Resource resource = null;
		cvlPackage.eINSTANCE.eClass();
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		try {
			//registry extent part of model file ex: *.variability
			reg.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		} catch (Exception e){
		}
		ResourceSet resourceSet = new ResourceSetImpl();
		String filename = new File(vSpecFileName).getAbsolutePath();
		URI uri = URI.createFileURI(filename);
		resource = resourceSet.getResource(uri, true);
		vpackage = (VPackage)  resource.getContents().get(0);
	}
	
	ArrayList<BCLExpression> getImplies() {
		ArrayList<BCLExpression> impliesList = new ArrayList<BCLExpression>();
		EList<VPackageable> packageElement = vpackage.getPackageElement();
		for (VPackageable pack : packageElement) {
			if (pack instanceof BCLConstraint) {
				EList<BCLExpression> expression = ((BCLConstraint) pack).getExpression();
				for (BCLExpression bcl : expression) {
					if (bcl instanceof OperationCallExp) {
						if (((OperationCallExp)bcl).getOperation()
								.getName().equals("logImplies")) {
							impliesList.add(bcl);
							
						}
					}
				}
			}
		}
		
		return impliesList;
	}
	ArrayList<VSpec> getChildVSpecList(VSpec vSpec) {
		ArrayList<VSpec> vSpecList = new ArrayList<VSpec>();
		vSpecList.add(vSpec);
		for (int i = 0; i < vSpec.getChild().size(); i++) {
			vSpecList.addAll(getChildVSpecList(vSpec.getChild().get(i)));
		}
		return vSpecList;
	}
	ArrayList<VSpec> getVSpecList() {
		ArrayList<VSpec> vSpecList = new ArrayList<VSpec>();
		EList<VPackageable> packageElement = vpackage.getPackageElement();
		VSpec vSpec = (VSpec) packageElement.get(0);
		vSpecList = getChildVSpecList(vSpec);
		
		return vSpecList;
	}
	
	ArrayList<BCLExpression> getDependsOn() {
		ArrayList<BCLExpression> dependsOnList = new ArrayList<BCLExpression>();
		EList<VPackageable> packageElement = vpackage.getPackageElement();
		for (VPackageable pack : packageElement) {
			if (pack instanceof BCLConstraint) {
				EList<BCLExpression> expression = ((BCLConstraint) pack).getExpression();
				for (BCLExpression bcl : expression) {
					if (bcl instanceof OperationCallExp) {
						if (((OperationCallExp)bcl).getOperation()
								.getName().equals("dependsOn")) {
							dependsOnList.add(bcl);
							
						}
					}
				}
			}
		}
		
		return dependsOnList;
	}
	
	ArrayList<VariationPoint> getVariationPoints() {
		ArrayList<VariationPoint> vps = new ArrayList<VariationPoint>();
		EList<VPackageable> packageElement = vpackage.getPackageElement();
		for (VPackageable pack : packageElement) {
			if (pack instanceof VariationPoint) {
				vps.add((VariationPoint)pack);
			}
		}
		
		return vps;
	}
	
	public static void main(String arg[]) {
		VariabilityModel vm = new VariabilityModel("modelinstances//encodingsystem//ESVariabilityModel.xmi");
		ArrayList<BCLExpression> dependsOn = vm.getDependsOn();
		for (BCLExpression bcl: dependsOn) {
			EList<BCLExpression> argument = ((OperationCallExp)bcl).getArgument();
			VSpecRef src = (VSpecRef)argument.get(0);
			VSpecRef dst = (VSpecRef)argument.get(1);
			
			System.out.println(src.getVSpec().getName()+" dependsOn "+dst.getVSpec().getName());

		}
		ArrayList<BCLExpression> implies = vm.getImplies();
		for (BCLExpression bcl: implies) {
			EList<BCLExpression> argument = ((OperationCallExp)bcl).getArgument();
			VSpecRef src = (VSpecRef)argument.get(0);
			VSpecRef dst = (VSpecRef)argument.get(1);
			
			System.out.println(src.getVSpec().getName()+" implies "+dst.getVSpec().getName());

		}
		
	}
}
