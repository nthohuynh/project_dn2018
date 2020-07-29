package lzdecompression.control;

import java.io.Serializable;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.felix.ipojo.Factory;

import service.ControlService;
import utils.MyMessage;

public class Controller {
	private Factory[] factories;
	String address = "192.168.56.3";
	int port = 9011;

	CircularFifoQueue<MyMessage> buffer;
	ControllerServiceImpl controllerServiceImpl;

	public Controller() {
		System.setProperty("java.security.policy", "java.security.AllPermission");
		System.setProperty("java.rmi.server.hostname", "192.168.56.3");
	
		try {
			controllerServiceImpl = new ControllerServiceImpl(address, port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setFactory(Factory[] factories) {
		this.factories = factories;
	}
	public void stop() {
		controllerServiceImpl.quit();
	}
	class ControllerServiceImpl extends UnicastRemoteObject implements
			ControlService, Serializable {

		private static final long serialVersionUID = 1L;
		int thisPort;
		String thisAddress;
		Registry registry; // rmi registry for lookup the remote objects.

		public ControllerServiceImpl(String add, int port)
				throws RemoteException {
			this.thisAddress = add;
			this.thisPort = port;

			try {
				// create the registry and bind the name and object.
				registry = LocateRegistry.createRegistry(thisPort);
				registry.rebind("LZDecompressionController", this);
			} catch (RemoteException e) {
				throw e;
			}
		}

		@Override
		public void isolate(boolean bool) {
		}

		@Override
		public void activate(String component) {
			// TODO Auto-generated method stub
			// no actions
		}

		@Override
		public void deactivate(String component) {
			// TODO Auto-generated method stub
			// no actions
		}

		@SuppressWarnings("unchecked")
		@Override
		public CircularFifoQueue<MyMessage> readMessages()
				throws RemoteException {
			
			return buffer;
		}

		@Override
		public void writeMessages(CircularFifoQueue<MyMessage> msg)
				throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		

		@Override
		public void redirect(String oldAddress, String newAddress) throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		public void quit() {
			try {
				System.out.println("quit rmi server");
				registry.unbind("LZDecompressionController");
				UnicastRemoteObject.unexportObject(registry, true);
					
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
	}

}
