package sender.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Element;

public class CompressingInInterceptor extends AbstractSoapInterceptor{
	static String transactionID;
	static List<String> transactionList = new ArrayList<String>();

	static ReadWriteLock rwlock = new ReentrantReadWriteLock();
	public CompressingInInterceptor() {
		super(Phase.USER_PROTOCOL);
	}

	synchronized void insertToList(String id) {
		rwlock.writeLock().lock();
		try {
			transactionList.add(id);
		} finally {
			rwlock.writeLock().unlock();
		}   
	}
	
	static synchronized String getFromList() {
		rwlock.readLock().lock();
		try {
			return transactionList.remove(0);
		} finally {
			rwlock.readLock().unlock();
		}
	}
	@Override
	synchronized public void handleMessage(SoapMessage arg0) throws Fault {
		// TODO Auto-generated method stub
		List<Header> head = new ArrayList<Header>();
		head.addAll(arg0.getHeaders());
		for (Iterator<Header> i = head.iterator(); i.hasNext();) {
			Header h = i.next();
			Element n = (Element) h.getObject();
			if (n.getLocalName().equals("transactionID")) {
				// code to store transactionID
				
				transactionID = n.getTextContent();
				insertToList(transactionID);
				System.out.println("TID in response from Compression:"+transactionID);
			}
		}
	}
}
