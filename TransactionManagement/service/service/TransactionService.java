package service;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface TransactionService  extends java.rmi.Remote {
	void informBeginning(String transactionID) throws RemoteException;
	void informEnd(String transactionID) throws RemoteException;
	public boolean getStatus(ArrayList<String> starterComponents) throws
	RemoteException;


}
