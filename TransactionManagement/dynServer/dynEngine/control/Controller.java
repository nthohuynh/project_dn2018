package decompression.control;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.InstanceManager;

import service.ControlService;
import utils.MyMessage;

public class Controller {
	private Factory[] factories;
	String address="192.168.56.1";
	int port=9012;
	
	CircularFifoQueue<MyMessage> buffer; 
	
	public Controller() {
		System.setProperty("java.security.policy", "java.security.AllPermission");
		System.setProperty("java.rmi.server.hostname", "192.168.56.1");
	
		try {
			new ControllerServiceImpl(address, port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void setFactory(Factory[] factories) {
		this.factories = factories;	
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
				registry.rebind("DecompressionController", this);
			} catch (RemoteException e) {
				throw e;
			}
		}

		@Override
		public void isolate(boolean bool) {
			// TODO Auto-generated method stub

			// activate of barrier
			for (Factory factory : factories) {
				if (factory.getName().equals("Decompression")) { // Client is
																	// default
																	// name of a
																	// component
																	// name
					ComponentInstance im = (ComponentInstance) factory
							.getInstances().get(0);

					// get buffer
					// boolean buffer = (boolean)
					// (InstanceManager)im.getFieldValue("enableProcess");
					// System.out.println(buffer);

					ComponentInstance ci = (ComponentInstance) im;
					Properties props = new Properties();
					Boolean bool1 = new Boolean(bool);
					props.put("enableProcess", bool1);
					im.reconfigure(props);

					boolean buffer = (boolean) ((InstanceManager) im)
							.getFieldValue("enableProcess");
					if (buffer)
						System.out.println("start process");
					else
						System.out.println("stop process");
				}

			}
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
			// TODO Auto-generated method stub

			return null;
		}

		@Override
		public void writeMessages(CircularFifoQueue<MyMessage> msg)
				throws RemoteException {

		}

		public void informBeginning(String transactionID)
				throws RemoteException {
			// TODO Auto-generated method stub

			/*
			 * todo: nothing because this component uses the informBeginning of
			 * reconfigurator
			 */

		}

		public void informEnd(String transactionID) throws RemoteException {
			// TODO Auto-generated method stub

			/*
			 * todo: nothing because this component uses the informEnd of
			 * reconfigurator
			 */

		}

		public void redirect(String oldAddress, String newAddress) throws RemoteException {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			System.out.println("Change connection of Decompression to: "+address);
			for (Factory factory : factories) {
				if (factory.getName().equals("Decompression")) { // Client is
																	// default
																	// name of a
																	// component
																	// name
					ComponentInstance im = (ComponentInstance) factory
							.getInstances().get(0);

					ComponentInstance ci = (ComponentInstance) im;
					Properties props = new Properties();
					// address = "udp://192.168.56.2:9002/lzcompression"
					props.put("setAddress", new String(oldAddress+"_"+newAddress));// setAddress is property

					im.reconfigure(props);

//					String buffer = (String) ((InstanceManager) im)
//							.getFieldValue("address");
//					System.out.println(buffer);
				}

			}
		}

	}	

}
