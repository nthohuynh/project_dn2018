package sender;


import java.util.Scanner;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.felix.ipojo.annotations.Component;

import sender.control.CompressingInInterceptor;
import sender.control.SendingOutInterceptor;
import service.CompressionService;
import service.SendingService;

@Component(name="Sender", immediate=true)

public class Sender {
	String fullCompressionAddress = "udp://192.168.56.1:9001/compression";
//	String fullCompressionAddress = "udp://192.168.56.1:9000/rlecompression";
	String fullReceiverAddress = "udp://192.168.56.1:9000/sending";
	
//	String uri_prefix = "http://";
	JaxWsProxyFactoryBean factory, compressionFactory ;
	
	SendingService client; //see as a property
    CompressionService compress;
	@SuppressWarnings("resource")
	public Sender() {
		
		//set address to compression
//		Scanner reader = new Scanner(System.in);  
//		System.out.print("Enter compresser address (e.g., 192.168.56.3:9011): ");
//		String compressionAddress = reader.next();
//		fullCompressionAddress = uri_prefix+compressionAddress+"/compression";
		
		//connect to compression
		compressionFactory = new JaxWsProxyFactoryBean();
		compressionFactory.setServiceClass(CompressionService.class);
		compressionFactory.setAddress(fullCompressionAddress);
		//compressionFactory.getOutInterceptors().add(new CompressingOutInterceptor());
		compressionFactory.getInInterceptors().add(new CompressingInInterceptor());
		compress = (CompressionService)compressionFactory.create();
		
		
		//set address to receiver
//		Scanner reader2 = new Scanner(System.in);  
//		System.out.print("Enter receiver address (e.g., 192.168.56.2:9011): ");
//		String receiverAddress = reader2.next();
//		fullCompressionAddress = uri_prefix+receiverAddress+"/sending";
		
		//connect to receiver
		factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(SendingService.class);
		factory.setAddress(fullReceiverAddress);
		factory.getOutInterceptors().add(new SendingOutInterceptor());
		client = (SendingService)factory.create();
				
		for (int i = 0; i < 3; i++) {
			Sending send0 = new Sending("Tittotatu");
			send0.start();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
    } 
    
//  @Property (name="setAddr")
//	public void setAddress(String add) {
//    	this.address = add;
//    	//factory.getBus().shutdown(true);
//    	factory.setAddress(add+"/HelloWorld");
//    	client = (SendingService)factory.create();
//    }
   
    class Sending extends Thread {
    	String str;
		public Sending(String string) {
			// TODO Auto-generated constructor stub
			this.str = string;
		}

		public void run() {
			
				for (int i = 0; i < 3; i++) {
					String compressedMsg = compress.compress(str);
					System.out.println(compressedMsg);
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//client.send(compressedMsg); //send to receiver
					
				}
		}
	}
    public static void main(String arg[]) {
    	new Sender();
    }
}
