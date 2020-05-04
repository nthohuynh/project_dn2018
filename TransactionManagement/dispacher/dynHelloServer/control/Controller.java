package rlecompression.control;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
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
	String address="192.168.56.2";
	int port=9010;
	
	CircularFifoQueue<MyMessage> buffer; 
	ControllerServiceImpl controllerServiceImpl;

	public Controller() {
		System.setProperty("java.security.policy", "java.security.AllPermission");
		System.setProperty("java.rmi.server.hostname", "192.168.56.2");
	
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

class ControllerServiceImpl extends UnicastRemoteObject implements ControlService, Serializable  {
		
		private static final long serialVersionUID = 1L;
		int thisPort;
		String thisAddress;
		Registry registry; // rmi registry for lookup the remote objects.
		
		public ControllerServiceImpl(String add, int port) throws RemoteException
		{
			this.thisAddress = add;
			this.thisPort = port; 
		
			try {
				// create the registry and bind the name and object.
				registry = LocateRegistry.createRegistry(thisPort);
				registry.rebind("RLECompressionController", this);
			}
			catch (RemoteException e) {
				throw e;
			}
		}
		
		@Override
		public void isolate(boolean bool) {
			// TODO Auto-generated method stub
			System.out.println("RLEController receives isolate command");
			// activate of barrier
			for (Factory factory : factories) {
//				System.out.println(factory.getName());
				if (factory.getName().equals("RLECompression")) { //Client is default name of a component name
					ComponentInstance im = (ComponentInstance) factory.getInstances().get(0);
			    	
					
					ComponentInstance ci = (ComponentInstance) im;
					Properties props = new Properties();
					Boolean bool1 = new Boolean(bool);
					props.put("enableProcess", bool1);
					im.reconfigure(props);
					
					
					boolean chk = (boolean) ((InstanceManager)im).getFieldValue("enableProcess");
					if (chk) System.out.println("un-isolate RLEComponent");
					else System.out.println("isolate RLEComponent");
				}
				
	    	}
		}

	
		@Override
		public void activate(String component) {
			// TODO Auto-generated method stub
			//no actions			
		}

		@Override
		public void deactivate(String component) {
			// TODO Auto-generated method stub
			//no actions
		}

		@SuppressWarnings("unchecked")
		@Override
		public CircularFifoQueue<MyMessage> readMessages()
				throws RemoteException {
			// TODO Auto-generated method stub
			
			//introspect the buffer of Server1 and getBuffer
			for (Factory factory : factories) {
				if (factory.getName().equals("RLECompression")) { //Client is default name of a component name
					InstanceManager im = (InstanceManager) factory.getInstances().get(0);
			    	buffer = (CircularFifoQueue<MyMessage>) im.getFieldValue("buffer"); //buffer is variable name in Server1
			    	System.out.println("RLEComponent have "+buffer.size()+"messages");
				}
			}
			System.out.println("get msgs in RLECompression -> ok");
			return buffer;
		}

		@Override
		public void writeMessages(CircularFifoQueue<MyMessage> msg)
				throws RemoteException {
			// TODO Auto-generated method stub
			for (Factory factory : factories) {
				if (factory.getName().equals("RLECompression")) { //Client is default name of a component name
					ComponentInstance ci = (ComponentInstance) factory.getInstances().get(0);
					Properties props = new Properties();
					props.put("buffer_", msg);
					ci.reconfigure(props);
					//System.out.println("set buffer to server2");
					break;
				}
			}
			System.out.println("Set msgs to RLECompression -> ok");

		}

		

		@Override
		public void redirect(String oldAddress, String newAddress) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		public void quit() {
			try {
				System.out.println("quit rmi server");
				registry.unbind("RLECompressionController");
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
