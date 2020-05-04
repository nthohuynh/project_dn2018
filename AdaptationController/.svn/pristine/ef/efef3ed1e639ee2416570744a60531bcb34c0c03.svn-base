package service;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.eclipse.emf.common.util.EList;

import utils.MyMessage;

public interface ControlService extends java.rmi.Remote {

	public void isolate(boolean bool, EList<String> placementComponentGroup) throws RemoteException;
	
	public void passivate() throws RemoteException;
	
	public void redirect(String oldAddress, String newAddress) throws RemoteException;
	
	public CircularFifoQueue <MyMessage> getState() throws RemoteException;
	public void setState(CircularFifoQueue <MyMessage> msg) throws RemoteException;
	
	public boolean getStatus() throws RemoteException;

}
