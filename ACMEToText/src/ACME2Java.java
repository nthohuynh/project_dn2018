import java.io.FileNotFoundException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.mwe.utils.DirectoryCleaner;
import org.eclipse.xpand2.XpandExecutionContextImpl;
import org.eclipse.xpand2.XpandFacade;
import org.eclipse.xpand2.output.Outlet;
import org.eclipse.xpand2.output.Output;
import org.eclipse.xpand2.output.OutputImpl;
import org.eclipse.xtend.typesystem.emf.EmfRegistryMetaModel;

import ACME.*;

public class ACME2Java {

	public ACME2Java(String file) {
		// TODO Auto-generated constructor stub
		ACMEPackage.eINSTANCE.eClass();
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		ACME.System system = null;
		try {
			//registry extent part of model file ex: *.variability
			reg.getExtensionToFactoryMap().put("acme", new XMIResourceFactoryImpl());
			URI uri = URI.createFileURI(file);
			ResourceSet resourceSet = new ResourceSetImpl();
			Resource resource = resourceSet.getResource(uri, true);
			
			//get root of variability model 
			system = (ACME.System) resource.getContents().get(0);
		} catch (Exception e){
		}

	
        //Xpand
        Output out = new OutputImpl();
        out.addOutlet(new Outlet("src-gen"));
         
        Object [] params =null;
        String templatePath = "template::Template::system";
        //Configure the metamodels
        
        //clean src-gen
        DirectoryCleaner clean = new DirectoryCleaner();
        try {
			clean.cleanFolder("src-gen");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        XpandExecutionContextImpl execCtx = new XpandExecutionContextImpl(out, null);
        execCtx.registerMetaModel(new EmfRegistryMetaModel());
        XpandFacade facade = XpandFacade.create(execCtx);
        facade.evaluate(templatePath, system, params);
	    
        java.lang.System.out.println("End generate code");
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ACME2Java("src//generatedbase.acme");
	}

}
