package receiver;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.felix.ipojo.annotations.Component;

import receiver.control.ReceiverInInterceptor;
import receiver.control.ReceiverOutInterceptor;
import service.DecompressionService;
import service.SendingService;


@Component(name="Receiver")
public class Receiver {
	Server server;
	String fullDecompressionAddress = "udp://192.168.56.1:9002/decompression";
	
	JaxWsServerFactoryBean svrFactory; 
	JaxWsProxyFactoryBean decompressionFactory ;
	DecompressionService decompress;
	public Receiver() {
		System.out.println("start Receiver");
		SendingImpl sendingImpl = new SendingImpl();
		svrFactory = new JaxWsServerFactoryBean();
		svrFactory.setServiceClass(SendingService.class);
		svrFactory.setAddress("udp://192.168.56.1:9000/sending");
		svrFactory.setServiceBean(sendingImpl);
		svrFactory.getInInterceptors().add(new ReceiverInInterceptor());
		
		server = svrFactory.create();
		server.start();
		
		//connect to decompression
		decompressionFactory = new JaxWsProxyFactoryBean();
		decompressionFactory.setServiceClass(DecompressionService.class);
		decompressionFactory.setAddress(fullDecompressionAddress);
		decompressionFactory.getOutInterceptors().add(new ReceiverOutInterceptor());
		decompress = (DecompressionService)decompressionFactory.create();
	}
	public static void main(String arg[]) {
		new Receiver();
	}

	class SendingImpl implements SendingService {

		@Override
		public void send(String str) {
			// TODO Auto-generated method stub
			System.out.println("....................Receiver: the compressed text:"+str);
			System.out.println("....................Receiver: the plan text: "+decompress.decompress(new String(str)));
		}

	}
}
