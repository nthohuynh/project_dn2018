package service;

import java.rmi.RemoteException;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import utils.MyMessage;

public interface ControlService extends java.rmi.Remote {
	
	CircularFifoQueue <MyMessage> readMessages() throws RemoteException;
	void writeMessages(CircularFifoQueue <MyMessage> msg) throws RemoteException;
	
	void isolate(boolean bool) throws RemoteException;
	
	void activate(String component) throws RemoteException;
	void deactivate(String component) throws RemoteException;

	void redirect(String oldAddress, String newAddress) throws RemoteException;

}
