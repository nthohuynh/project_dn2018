package manager;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.eclipse.emf.common.util.EList;

import utils.MyMessage;
import service.TransactionManagerService;

public class TransactionManager {
	ArrayList<String> transactionList = new ArrayList<String>();

	String thisAddress = "192.168.56.1";
	int thisPort = 9014;
	
	public TransactionManager() {
		// initiate reconfigurator to receive transaction information
		System.out.println("Start transaction manager");
		
		System.out.println("Enter IP of manager: ");
		Scanner scanner = new Scanner(System.in);
		thisAddress = scanner.nextLine();
		
		System.out.println("Enter port of manager: ");
		thisPort = scanner.nextInt();
		
		// set policy for rmi server
		System.setProperty("java.security.policy",
				"java.security.AllPermission");
		System.setProperty("java.rmi.server.hostname", thisAddress);
		try {
			new TransactionManagerServiceImpl(thisAddress, thisPort);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class TransactionManagerServiceImpl extends UnicastRemoteObject implements
	TransactionManagerService, Serializable {
		private static final long serialVersionUID = 1L;
		int thisPort;
		String thisAddress;
		Registry registry; // rmi registry for lookup the remote objects.
		
		public TransactionManagerServiceImpl(String add, int port)
				throws RemoteException {
			this.thisAddress = add;
			this.thisPort = port;
			// System.setProperty("java.security.policy",
			// "java.security.AllPermission");
			// System.setProperty("java.rmi.server.hostname", "192.168.56.1");
			try {
				// create the registry and bind the name and object.
				registry = LocateRegistry.createRegistry(thisPort);
				registry.rebind("TransactionManager", this);
		
			} catch (RemoteException e) {
				throw e;
			}
		}
		
		@Override
		public void informBeginning(String transactionID) {
			// TODO Auto-generated method stub
		
			/*
			 * begin a transaction
			 */
			System.out.println("Transaction Manager receives -> Beginning of transaction: "
							+ transactionID);		
			transactionList.add(transactionID);
		}
		
		@Override
		public void informEnd(String transactionID) {
			// TODO Auto-generated method stub
		
			/*
			 * end the transaction
			 */
			System.out
					.println("Transaction Manager receives -> End of transaction: "
							+ transactionID);
			transactionList.remove(transactionList.indexOf(transactionID));
		}
		
		@Override
		public boolean getStatus(EList<String> starterComponents)
				throws RemoteException {
			//ex: transactionID = id + ":" + componentname
			for (String id : transactionList) {
				String componentName = id.substring(id.indexOf(":")+1,id.length());
				if (starterComponents.contains(componentName)) return false; 
			}
			return true;
		}
	}
}
