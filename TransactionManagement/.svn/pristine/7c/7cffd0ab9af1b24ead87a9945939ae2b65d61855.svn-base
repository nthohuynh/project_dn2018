package addID;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;


public class Receiver {
	Server server;
	JaxWsServerFactoryBean svrFactory;
	public Receiver(){
		System.out.println("start Compression");
		SendingImpl sendingImpl = new SendingImpl();
		svrFactory = new JaxWsServerFactoryBean();
		svrFactory.setServiceClass(Service.class);
		svrFactory.setDestinationFactory(new MyTransportFactory());
		svrFactory.setAddress("udp://localhost:9010/sending");
		svrFactory.getInInterceptors().add(new MyServerInInterceptor());

		svrFactory.getOutInterceptors().add(new MyServerOutInterceptor());

		svrFactory.setServiceBean(sendingImpl);
		server = svrFactory.create();
		server.start();
	}
	public static void main(String arg[]) {
		new Receiver();
	}
	
	class SendingImpl implements Service {

		@Override
		public String send(String str) {
			// TODO Auto-generated method stub
			System.out.println(str);
			return "server "+str;
		}
		
	}
}
