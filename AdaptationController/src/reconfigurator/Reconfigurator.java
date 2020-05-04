package reconfigurator;

import java.awt.image.ComponentSampleModel;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.eclipse.emf.common.util.EList;
import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.FrameworkMBean;

import reconfigurationPlan.ActivationAction;
import reconfigurationPlan.ConnectionAction;
import reconfigurationPlan.DeactivationAction;
import reconfigurationPlan.GettingStatusAction;
import reconfigurationPlan.IsolationAction;
import reconfigurationPlan.PassivationAction;
import reconfigurationPlan.PlacementComponentsGroup;
import reconfigurationPlan.ReintegrationAction;
import reconfigurationPlan.TransferAction;
import utils.Deployment;
import utils.ReconfigurationPlan;
import statetransfer.StateTransferPoint;
import statetransfer.Variable;
import utils.ExtBundle;
import utils.MyMessage;
import service.ControlService;
import service.TransactionManagerService;

public class Reconfigurator {

	public Reconfigurator() {
		// TODO Auto-generated constructor stub
		ReconfigurationPlan reconfigurationPlan = new ReconfigurationPlan("modelinstances//plan.xmi");
		Deployment deploy = new Deployment("modelinstances//Deployment.xmi");

//		first step: isolate components
//		isolate("RLECompression", "192.168.56.2", 9010, true);
		EList<IsolationAction> isolationList = reconfigurationPlan.getIsolationAction();
		
		for (IsolationAction isolation : isolationList) {
			String componentName = isolation.getComponent(); 
			String address = deploy.getAddress(componentName);
			int port = deploy.getControlPort(componentName);
			
			System.out.println("isolate: "+componentName+"-"+address+"-"+port);
			PlacementComponentsGroup placementComponentGroup = isolation.getPlacementComponentsGroup();
			EList<String> componentsInGroup = placementComponentGroup.getComponents();
			isolate(componentName, address, port, componentsInGroup);
		}

		
		//second step
		EList<PassivationAction> passivationList = reconfigurationPlan.getPassivationAction();
		for (PassivationAction  passitionAction : passivationList) {
			String componentName = passitionAction.getComponent(); 
			String address = deploy.getAddress(componentName);
			int port = deploy.getControlPort(componentName);
			
			passivate(componentName, address, port);
			
		}
		
		
		// verify all global transactions finished
		boolean resultFromManager = false;
		EList<String> starterComponents = reconfigurationPlan.getStarterComponents();
		String managerAddress = "";
		int managerPort = 0;
		while (!resultFromManager) {
			resultFromManager = getManagerStatus(managerAddress, managerPort, starterComponents);
		}
		
		EList<GettingStatusAction> statusActionList = reconfigurationPlan.getGettingStatusAction();
		boolean resultFromComponents = true;
		while (!resultFromComponents) {
			for (GettingStatusAction  statusAction : statusActionList) {
				String componentName = statusAction.getComponent(); 
				String address = deploy.getAddress(componentName);
				int port = deploy.getControlPort(componentName);
				resultFromComponents = resultFromComponents && getComponentStatus(componentName, address, port);
			}
			resultFromComponents = true;
		}
		
		
//		// second step 
//		// add components
//		// use JMX service in Karaf
//		activate("LZCompression", "192.168.56.2", "80f6ff10-ff01-4ecd-8ae9-8cf9e1330648"); 
//		activate("LZDecompression", "192.168.56.3", "2a6246cc-052d-4ed0-9a08-6b479f66cc37"); 
//		
		EList<ActivationAction> activationList = reconfigurationPlan.getActivationAction();
		
		for (ActivationAction activation : activationList) {
			String componentName = activation.getComponent();
			String address = deploy.getAddress(componentName);
			String uuid = deploy.getUUID(componentName);
			System.out.println("activate:"+componentName +":"+ address+":"+ uuid);
			activate(componentName, address, uuid);
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		// drive messages from compression to lzcompression
//		redirect("Compression", "192.168.56.1", 9011,"udp://192.168.56.2:9001/lzcompression");
//		 "192.168.56.1", 9011 is address of controller part of Compression
//		drive messages from decompression to lzdecompression
//		redirect("Decompression", "192.168.56.1", 9012, "udp://192.168.56.3:9001/lzdecompression");
		EList<ConnectionAction> connectionList = reconfigurationPlan.getConnectionAction();
		for (ConnectionAction connection : connectionList) {
			String component = connection.getSrcComponent();
			String address = deploy.getAddress(component);
			int port = deploy.getControlPort(component);
			
			String oldDestination = connection.getOldDstComponent();
			String oldAddress = deploy.getAddress(oldDestination);
			int oldPort = deploy.getApplicationPort(oldDestination);
			
			String newDestination = connection.getNewDstComponent();
			String newAddress = deploy.getAddress(newDestination);
			int dstPort = deploy.getApplicationPort(newDestination);
			
			System.out.println("Connect:"+component +":"+ address+":"+ port+" to "+"udp://"+newAddress+":"+dstPort+"/"+newDestination.toLowerCase());
			redirect(component, address, port,"udp://:"+oldPort+"/"+oldDestination.toLowerCase(), "udp://"+newAddress+":"+dstPort+"/"+newDestination.toLowerCase());
		}
//		
		
		
//		// transfer messages
//		CircularFifoQueue<MyMessage> listMsgs = readMessages("RLECompression", "192.168.56.2", 9010);
//		System.out.println("get "+listMsgs.size()+" msgs");
//		writeMessage("LZCompression", listMsgs, "192.168.56.2", 9011);
		EList<TransferAction> transferList = reconfigurationPlan.getTransferActions();
		for (TransferAction transfer : transferList) {
			StateTransferPoint point = transfer.getPoint();
			
			Variable srcVariable = point.getSrcVariable().get(0);
			String srcComponent = srcVariable.getVariableHandle().getMofRef();
			String address = deploy.getAddress(srcComponent);
			int port = deploy.getControlPort(srcComponent);
			System.out.println("read msgs from: "+srcComponent+":"+
					address+":"+ port);
			CircularFifoQueue<MyMessage> listMsgs = getState(srcComponent,address, port);
			
			Variable dstVariable = (Variable)point.getDstVariable();
			String dstComponent = dstVariable.getVariableHandle().getMofRef();
			String dstAddress = deploy.getAddress(dstComponent);
			int dstPort = deploy.getControlPort(dstComponent);
			System.out.println("write msgs into: "+dstComponent+":"+
					dstAddress+":"+ dstPort);
			setState(dstComponent, listMsgs, dstAddress, dstPort);
		}
		
		
		
//		// deactivate isolation
//		isolate("LZCompression", "192.168.56.2", 9011, false);
		EList<ReintegrationAction> combinationList = reconfigurationPlan.getReintegrationActions();
		for (ReintegrationAction combination : combinationList) {
			String component = combination.getComponent();
			String address = deploy.getAddress(component);
			int port = deploy.getControlPort(component);

			System.out.println("unisolate:"+component+":"+ address+":"+ port);
			reintegrate(component, address, port);
		}
		
		
//		// remove components
//		// use JMX service in Karaf
//		deactivate("RLECompression", "192.168.56.2", "80f6ff10-ff01-4ecd-8ae9-8cf9e1330648"); 
//		deactivate("RLEDecompression", "192.168.56.3", "2a6246cc-052d-4ed0-9a08-6b479f66cc37"); 
		EList<DeactivationAction> deactivationList = reconfigurationPlan.getDeactivationActions();
		for (DeactivationAction deactivation: deactivationList) {
			String component = deactivation.getComponent();
			String address = deploy.getAddress(component);
			String uuid = deploy.getUUID(component);
			
			System.out.println("deactivate:"+component +":"+ address+":"+ uuid);

			deactivate(component, address, uuid); 
				
		}
	}
	
	/*
	 * isolate a component 
	 */
	public void isolate(String componentName, String address, int port, EList<String> placementComponentsGroup) {
		ControlService controlService;
		Registry registry;

		try {
			registry = LocateRegistry.getRegistry(address, port);
			controlService = (ControlService) (registry.lookup(componentName
					+ "Controller"));
			controlService.isolate(true, placementComponentsGroup);

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	/*
	 * reintegrate a component 
	 */
	public void reintegrate(String componentName, String address, int port) {
		ControlService controlService;
		Registry registry;

		try {
			registry = LocateRegistry.getRegistry(address, port);
			controlService = (ControlService) (registry.lookup(componentName
					+ "Controller"));
			controlService.isolate(false, null);

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void passivate(String componentName, String address, int port) {
		ControlService controlService;
		Registry registry;

		try {
			registry = LocateRegistry.getRegistry(address, port);
			controlService = (ControlService) (registry.lookup(componentName
					+ "Controller"));
			controlService.passivate();

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean getComponentStatus(String componentName, String address, int port) {
		ControlService controlService;
		Registry registry;
		boolean result = false; // component is busy
		try {
			registry = LocateRegistry.getRegistry(address, port);
			controlService = (ControlService) (registry.lookup(componentName
					+ "Controller"));
			result = controlService.getStatus();
			return result;

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean getManagerStatus(String address, int port, EList<String> starterComponents) {
		 TransactionManagerService transactionService;
		 
		  Registry registry;
		  boolean result = false;
		  try {
			  registry = LocateRegistry.getRegistry(address, port);
			  transactionService = (TransactionManagerService)(registry.lookup("TransactionManager"));
			  result = transactionService.getStatus(starterComponents);
		 
		  } catch (RemoteException e) {
			  e.printStackTrace();
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
		  return result;
	}
	
	
	/**
	 * update properties (name = synchroneProp) of component instance
	 * ComponentInstance im;
	 * Properties props = new Properties();
	 * props.put("synchroneProp", "yes");
	 * im.reconfigure(props);
	 * @param property
	 * @param value
	 * @param component
	 * @return Void    
	 */
	public void redirect(String srcComponent, String address, int port, String oldAddress,
			String toNewAddress) {
		ControlService controlService;
		Registry registry;

		try {
			registry = LocateRegistry.getRegistry(address, port);
			controlService = (ControlService) (registry.lookup(srcComponent
					+ "Controller"));
			controlService.redirect(oldAddress,toNewAddress);

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	public CircularFifoQueue<MyMessage> getState(String component, String address, int port) {
		ControlService service;
		Registry registry;
		CircularFifoQueue<MyMessage> msg = new CircularFifoQueue<MyMessage>(50);
		System.setProperty("java.security.policy", "java.security.AllPermission");
		System.setProperty("java.rmi.server.hostname", address);
		
		try {
			registry = LocateRegistry.getRegistry(address, port);
			service = (ControlService) (registry.lookup(component+"Controller"));
			msg = service.getState();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	public void setState(String component, CircularFifoQueue<MyMessage> msg, String address, int port) {
		/*
		 * connecto to component and write messages
		 */
		ControlService service;
		Registry registry;

		try {
			registry = LocateRegistry.getRegistry(address, port);
			service = (ControlService) (registry.lookup(component+"Controller"));
			service.setState(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void activate(String bundleName, String host, String felixFrameworkUUID) {
		System.out.println("start activate");
		MBeanServerConnection server = connecToRemoteHost(host);
		long bundleid = installBundleOnRemoteHost(server, "file:./availability/"+bundleName.toLowerCase()+".jar", felixFrameworkUUID);
		startBundleOnRemoteHost(server, bundleid, felixFrameworkUUID); 
		System.out.println("finish activate");
	}
	/**
	 * stop component instance (I) 
	 * Factory fct;
	 * instanceList = fct.getInstances
	 * search in instanceList for I
	 * i.stop()
	 * Bundle srcBundle = ctx.getBundle(srcComponentBundleFile);
	 * srcBundle.uninstall();
	 */
	public void deactivate(String bundleName, String host, String felixFrameworkUUID) {
		MBeanServerConnection server = connecToRemoteHost(host);
		ExtBundle bundle = getBundle(server, felixFrameworkUUID, bundleName);
		uninstallBundleOnRemoteHost(server, bundle.getBundleId(), felixFrameworkUUID); 
	}
	
	/**
	 * change connection of A to S2 instead of S1
	 * S1 and S2 implement Service 
	 * Component A uses interface Service from S1 (current conf)
	 * through a variable (e.g. Service sv)
	 * d is dependency of sv 
	 * Filter filter = FrameworkUtil.createFilter("(instance.name="+S2 instance name+")");
	 * d.setFilter(filter);
	 * 
	 */
//	public void changeConnector(String componentName, 
//			String placementComponent, 
//			String replacementComponent) {
//		
//	}
	
	
	
	
	/**
	 * create connection to remote host containing Karaf to manage bundle, etc
	 * @param host ip address of host containing Karaf
	 * @param karafHostName name of Karaf on the host, default: root
	 * @param username user name uses Karaf, default: karaf
	 * @param password password of user, default: karaf
	 * @return MBeanConnection
	 */
	public MBeanServerConnection connecToRemoteHost(String host) {
		String username = "karaf";
		String password = "karaf";
		String karafHostName = "root";
		HashMap<String, String[]> environment = new HashMap<String, String[]>();
		String[] credentials = new String[] { username, password };
		environment.put("jmx.remote.credentials", credentials);
		
		JMXServiceURL url;
		JMXConnector jmxc;
		MBeanServerConnection mbsc = null;
		
		try {
			url = new JMXServiceURL("service:jmx:rmi://"+host+":44444/jndi/rmi://"+host+":1099/karaf-"+karafHostName);
			jmxc = JMXConnectorFactory.connect(url, environment);
			mbsc = jmxc.getMBeanServerConnection();
			System.out.println("Connect to Karaf ok");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Can not connecto to "+host);
			e.printStackTrace();
		}
		return mbsc;
	}
	
	public List<ExtBundle> getBundleListOnRemoteHost(MBeanServerConnection mbsc,
			String felixFrameworkUUID) {
		
		List<ExtBundle> listRemoteBundle = new ArrayList<ExtBundle>();
		ObjectName objState;
		BundleStateMBean bundleState;
		TabularData bundleData;
		try {
			objState = new ObjectName("osgi.core:type=bundleState,version=1.7,"
			 		+ "framework=org.apache.felix.framework,uuid="+felixFrameworkUUID);
			bundleState = JMX.newMBeanProxy(mbsc, objState,
					 BundleStateMBean.class);
			bundleData=bundleState.listBundles();
			for (Object v : bundleData.values()) {
				 CompositeData row = (CompositeData) v;
				 
	 			 ExtBundle extBundle = new ExtBundle();
	 			 int i = 0;
				 for (Object rv: row.values()) {
					 i ++;
					 switch (i) {
					case 7:
						extBundle.setBundleId(Integer.parseInt(rv.toString()));
						break;
					case 10:
						extBundle.setBundleLocation(rv.toString());
						break;
					case 19:
						extBundle.setState(rv.toString());
						break;
					case 20:
						extBundle.setBundleName(rv.toString());
						break;
					case 21:
						extBundle.setVersion(rv.toString());
						break;
					default:
						break;
					}
				 }
				 listRemoteBundle.add(extBundle);
			 }
		} catch (MalformedObjectNameException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listRemoteBundle;
	}
	
	public ExtBundle getBundle(MBeanServerConnection mbsc,
			String felixFrameworkUUID, String bundleName) {
		ExtBundle bundle = null;
		
		
		 List<ExtBundle> listBundle = getBundleListOnRemoteHost(mbsc, felixFrameworkUUID);
	
		 for (ExtBundle bdl : listBundle) {
			 if (bdl.getBundleName().equals(bundleName.toLowerCase())) {
				 return bdl;
			 }
		 }
		 
		return bundle; 
		
	}
	/**
	 * install a bundle on a remote host 
	 * @param mbsc
	 * @param locationBundle bundle location on the host , e.g., file:/home/abc.jar
	 * @param felixFrameworkUUID
	 * @return id of installed bundle
	 */
	public long installBundleOnRemoteHost(MBeanServerConnection mbsc, 
			String locationBundle,
			String felixFrameworkUUID) {
		long result = 0;
		ObjectName mbeanName;
		try {
			mbeanName = new ObjectName("osgi.core:type=framework,version=1.7,"
				 		+ "framework=org.apache.felix.framework,uuid="+felixFrameworkUUID);
			FrameworkMBean osgiFrameworkProxy = JMX.newMBeanProxy(mbsc, mbeanName,
					FrameworkMBean.class);
		 long bundleNumber = osgiFrameworkProxy.installBundle(locationBundle);
		 return bundleNumber;
		} catch (MalformedObjectNameException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public boolean startBundleOnRemoteHost(MBeanServerConnection mbsc, 
			long bundleId,
			String felixFrameworkUUID) {
		boolean result = false;
		ObjectName mbeanName;
		try {
			mbeanName = new ObjectName("osgi.core:type=framework,version=1.7,"
				 		+ "framework=org.apache.felix.framework,uuid="+felixFrameworkUUID);
			FrameworkMBean osgiFrameworkProxy = JMX.newMBeanProxy(mbsc, mbeanName,
					FrameworkMBean.class);
			System.out.println("start bundle id: "+bundleId);
			osgiFrameworkProxy.startBundle(bundleId);
			return true;
		} catch (MalformedObjectNameException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 
	 * @param mbsc
	 * @param bundleId
	 * @param felixFrameworkUUID
	 * @return
	 */
	public boolean uninstallBundleOnRemoteHost(MBeanServerConnection mbsc, 
			long bundleId,
			String felixFrameworkUUID) {
		boolean result = false;
		ObjectName mbeanName;
		try {
			mbeanName = new ObjectName("osgi.core:type=framework,version=1.7,"
				 		+ "framework=org.apache.felix.framework,uuid="+felixFrameworkUUID);
			FrameworkMBean osgiFrameworkProxy = JMX.newMBeanProxy(mbsc, mbeanName,
					FrameworkMBean.class);
			osgiFrameworkProxy.uninstallBundle(bundleId);
			return true;
		} catch (MalformedObjectNameException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	

}
