package service;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;

public interface TransactionManagerService extends java.rmi.Remote {
	
	public boolean getStatus(EList<String> starterComponents) throws RemoteException;

	public void informBeginning(String transactionID) throws RemoteException; //ex: transactionID = id + ":" + component name 
	public void informEnd(String transactionID) throws RemoteException;

}
