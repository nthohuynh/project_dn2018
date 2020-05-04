package addID;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.saaj.SAAJOutInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptor;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.ContextUtils;

public class MyOutInterceptor extends AbstractSoapInterceptor {
	
	public MyOutInterceptor() {
		super(Phase.PRE_LOGICAL);
		//extras.add(new SAAJOutInterceptor());
	}

	@Override
	public void handleMessage(SoapMessage paramT) throws Fault {
		// TODO Auto-generated method stub
		try {
			Random randomGenerator = new Random();
			int randomInt = randomGenerator.nextInt(1000);
			
			for(Header header:paramT.getHeaders()) {
	            if(header.getName().getLocalPart().equalsIgnoreCase("MessageID")) {
	                System.out.println(header.getName().getLocalPart());
	            }
	        }
			
			paramT.getHeaders().add(new Header(new QName("MyCustomHeader"),
					paramT.getId()+""+randomInt, new JAXBDataBinding(String.class)));
             } catch (JAXBException e) {
                 e.printStackTrace();
             }
	}
		
}
	

