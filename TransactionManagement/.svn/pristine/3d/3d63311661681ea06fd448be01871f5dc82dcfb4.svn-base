package addID;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
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

public class MyServerOutInterceptor extends AbstractSoapInterceptor {

	public MyServerOutInterceptor() {
		super(Phase.PRE_LOGICAL);
	}

	@Override
	public void handleMessage(SoapMessage paramT) throws Fault {
		// TODO Auto-generated method stub
		List<Header> head = new ArrayList<Header>();

		head.addAll(paramT.getHeaders());
		for (Iterator<Header> i = head.iterator(); i.hasNext();) {
			Header h = i.next();
			Element n = (Element) h.getObject();

			System.out.println("out header name=" + n.getLocalName());
			System.out.println("out header content=" + n.getTextContent());
		}
	}
}
