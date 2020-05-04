package sender.control;

import java.util.Random;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.phase.Phase;

public class SendingOutInterceptor extends AbstractSoapInterceptor {
	static String transactionID;
	public SendingOutInterceptor() {
		super(Phase.PRE_LOGICAL);
		//extras.add(new SAAJOutInterceptor());
	}

	@Override
	synchronized public void handleMessage(SoapMessage paramT) throws Fault {
		// TODO Auto-generated method stub
		try {
			transactionID = CompressingInInterceptor.getFromList();
			System.out.println("ID in msg send to Receive: "+transactionID);
			paramT.getHeaders().add(new Header(new QName("transactionID"), transactionID, new JAXBDataBinding(String.class)));
             } catch (JAXBException e) {
                 e.printStackTrace();
             }
	}
}
