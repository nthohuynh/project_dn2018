package rlecompression.control;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Element;

import service.ControlService;
import service.TransactionService;


public class RLECompressionOutInterceptor extends AbstractSoapInterceptor {
	//List<String> transactionList = new ArrayList<String>();
	
	TransactionService transactionService;
	Registry rmiServer;
	String transactionID;
	
	// reconfigurator address
	String address = "192.168.56.1";
	int port=9013;
	
	int i = 0; boolean bool = true;
	public RLECompressionOutInterceptor() {
		super(Phase.PRE_LOGICAL);
		// connect to transaction manager
		while ((i < 3) && bool) {
			try {
				rmiServer = LocateRegistry.getRegistry(address, port);
				transactionService = (TransactionService) (rmiServer.lookup("TransactionManager"));
				bool = false;
				System.out.println("Connected to reconfigurator");
						
			} catch (RemoteException e) {
				System.out.println("Can not connect from RLECompression controller to reconfigurator for transaction infos");
				System.out.println("Trying.....");
				i++;
				//e.printStackTrace();
			} catch (Exception e) {
				//System.out.println("Can not connect from RLECompression controller to reconfigurator for transaction infos");
				System.out.println("Trying.....");
				//e.printStackTrace();
				i++;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public void handleMessage(SoapMessage paramT) throws Fault {
		// TODO Auto-generated method stub
		
		try {
			transactionID = UUID.randomUUID().toString() + ":"+"RLECompression";
			System.out.println("ID out: "+transactionID);
			paramT.getHeaders().add(new Header(new QName("transactionID"), transactionID, 
					new JAXBDataBinding(String.class)));

			System.out.println("Beginning of transaction: " + transactionID);
			System.out.println("....................send transaction ID from RLECompression to Transaction Manager");
			
			
//			try {
//				transactionService.informBeginning(transactionID);
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			//transactionList.add(transactionID);
			System.out.println("");
			
		} catch (JAXBException e) {
                 e.printStackTrace();
        
		}
	}
}
