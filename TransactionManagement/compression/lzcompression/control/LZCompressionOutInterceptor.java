package lzcompression.control;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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


public class LZCompressionOutInterceptor extends AbstractSoapInterceptor {
	//List<String> transactionList = new ArrayList<String>();
	
	TransactionService transactionService;
	Registry rmiServer;
	
	// reconfigurator address
	String address = "192.168.56.1";
	int port=9013;
	
	public LZCompressionOutInterceptor() {
		super(Phase.PRE_LOGICAL);
		// connect to reconfigurator
		try {
			rmiServer = LocateRegistry.getRegistry(address, port);
			transactionService = (TransactionService) (rmiServer.lookup("TransactionManager"));
			System.out.println("Connected to transaction manager");

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void handleMessage(SoapMessage paramT) throws Fault {
		// TODO Auto-generated method stub
		List<Header> head = new ArrayList<Header>();

		head.addAll(paramT.getHeaders());
		for (Iterator<Header> i = head.iterator(); i.hasNext();) {
			Header h = i.next();
			Element n = (Element) h.getObject();
			if (n.getLocalName().equals("transactionID")) {
				//code to store transactionID
				//transactionList.add(n.getTextContent());
				
				String transactionID = n.getTextContent();
				System.out.println("Beginning of transaction: " + transactionID);
				System.out.println("....................send transaction ID from LZCompression to reconfigurator");

				/*
				 * send transaction id to the reconfigurator here
				 */
				
				try {
					transactionService.informBeginning(transactionID);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					paramT.getHeaders().add(new Header(new QName("transactionID"), transactionID, new JAXBDataBinding(String.class)));
				} catch (JAXBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
