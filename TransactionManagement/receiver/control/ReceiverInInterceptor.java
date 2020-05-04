package control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Element;

public class ReceiverInInterceptor extends AbstractSoapInterceptor {
	static List<String> transactionList = new ArrayList<String>();
	static String transactionID;

	public ReceiverInInterceptor() {
		super(Phase.PRE_LOGICAL);
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
				transactionList.add(n.getTextContent());
				System.out.println("transaction id: " + n.getTextContent());
				transactionID = n.getTextContent();
			}
		}
	}

}
