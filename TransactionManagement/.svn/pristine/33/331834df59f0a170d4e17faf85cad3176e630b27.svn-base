package addID;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.ConduitSelector;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.ConduitInitiatorManager;



public class Sender {
	JaxWsProxyFactoryBean factory;
	String address = "udp://localhost:9010/sending";
	Service client; 
	public Sender() {
		factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(Service.class);
		
		
//		Bus bus = BusFactory.getThreadDefaultBus();
//		MyTransportFactory customTransport = new MyTransportFactory();
//		ConduitInitiatorManager extension = bus.getExtension(ConduitInitiatorManager.class);
//		extension.registerConduitInitiator(MyTransportFactory.TRANSPORT_ID, customTransport);

		factory.getOutInterceptors().add(new MyOutInterceptor());
		factory.getInInterceptors().add(new MyInInterceptor());
		factory.setAddress(address);
		client = (Service)factory.create();
		System.out.println(client.send("123123123123"));
	}
	public static void main(String arg[]) {
		new Sender();
	}
}
