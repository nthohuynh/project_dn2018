package compression.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Element;

public class CompressionResponseInInterceptor extends AbstractSoapInterceptor{
	static String transactionID;

	public CompressionResponseInInterceptor() {
		super(Phase.USER_PROTOCOL);
	}

	@Override
	public void handleMessage(SoapMessage arg0) throws Fault {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub

		List<Header> head = new ArrayList<Header>();
		head.addAll(arg0.getHeaders());
		for (Iterator<Header> i = head.iterator(); i.hasNext();) {
			Header h = i.next();
			Element n = (Element) h.getObject();
			if (n.getLocalName().equals("transactionID")) {
				// code to store transactionID
				
				transactionID = n.getTextContent();
				System.out.println("Response from RLE:"+transactionID);
			}
		}
	}
}
