package dispacher.control;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.phase.Phase;



public class DispacherOutInterceptor extends AbstractSoapInterceptor {
	String transactionID;
	public CompressionOutInterceptor() {
		super(Phase.PRE_LOGICAL);
		// TODO Auto-generated constructor stub
	}

	@Override
	synchronized public void handleMessage(SoapMessage paramT) throws Fault {
		// TODO Auto-generated method stub
		try {
			transactionID = CompressionResponseInInterceptor.transactionID;
			if (transactionID == null) transactionID = "null"; 
			paramT.getHeaders().add(new Header(new QName("transactionID"), transactionID, new JAXBDataBinding(String.class)));
             } catch (JAXBException e) {
                 e.printStackTrace();
             }
	}

}
