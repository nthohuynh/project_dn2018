package addID;

import org.apache.cxf.binding.soap.interceptor.SoapPreProtocolOutInterceptor;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.binding.soap.interceptor.SoapPreProtocolOutInterceptor;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;

public abstract class MessageChangeInterceptor extends AbstractPhaseInterceptor<Message> {
	public MessageChangeInterceptor() {
		super(Phase.PRE_STREAM);
		addBefore(SoapPreProtocolOutInterceptor.class.getName());
	}

	protected abstract Logger getLogger();

	protected abstract String changeOutboundMessage(String currentEnvelope);

	protected abstract String changeInboundMessage(String currentEnvelope);

	public void handleMessage(Message message) {
		boolean isOutbound = false;
		isOutbound = message == message.getExchange().getOutMessage()
				|| message == message.getExchange().getOutFaultMessage();

		if (isOutbound) {
			OutputStream os = message.getContent(OutputStream.class);

			CachedStream cs = new CachedStream();
			message.setContent(OutputStream.class, cs);

			message.getInterceptorChain().doIntercept(message);

			try {
				cs.flush();
				IOUtils.closeQuietly(cs);
				CachedOutputStream csnew = (CachedOutputStream) message
						.getContent(OutputStream.class);

				String currentEnvelopeMessage = IOUtils.toString(
						csnew.getInputStream(), "UTF-8");
				csnew.flush();
				IOUtils.closeQuietly(csnew);

//				if (getLogger().isDebugEnabled()) {
//					getLogger().debug(
//							"Outbound message: " + currentEnvelopeMessage);
//				}

				String res = changeOutboundMessage(currentEnvelopeMessage);
//				if (res != null) {
//					if (getLogger().isDebugEnabled()) {
//						getLogger().debug(
//								"Outbound message has been changed: " + res);
//					}
//				}
				res = res != null ? res : currentEnvelopeMessage;

				InputStream replaceInStream = IOUtils.toInputStream(res,
						"UTF-8");

				IOUtils.copy(replaceInStream, os);
				replaceInStream.close();
				IOUtils.closeQuietly(replaceInStream);

				os.flush();
				message.setContent(OutputStream.class, os);
				IOUtils.closeQuietly(os);

			} catch (IOException ioe) {
				getLogger().warn("Unable to perform change.", ioe);
				throw new RuntimeException(ioe);
			}
		} else {
			try {
				InputStream is = message.getContent(InputStream.class);
				String currentEnvelopeMessage = IOUtils.toString(is, "UTF-8");
				IOUtils.closeQuietly(is);

				if (getLogger().isDebugEnabled()) {
					getLogger().debug(
							"Inbound message: " + currentEnvelopeMessage);
				}

				String res = changeInboundMessage(currentEnvelopeMessage);
				if (res != null) {
					if (getLogger().isDebugEnabled()) {
						getLogger().debug(
								"Inbound message has been changed: " + res);
					}
				}
				res = res != null ? res : currentEnvelopeMessage;

				is = IOUtils.toInputStream(res, "UTF-8");
				message.setContent(InputStream.class, is);
				IOUtils.closeQuietly(is);
			} catch (IOException ioe) {
				getLogger().warn("Unable to perform change.", ioe);

				throw new RuntimeException(ioe);
			}
		}
	}

	public void handleFault(Message message) {
	}

	private class CachedStream extends CachedOutputStream {
		public CachedStream() {
			super();
		}

		protected void doFlush() throws IOException {
			currentStream.flush();
		}

		protected void doClose() throws IOException {
		}

		protected void onWrite() throws IOException {
		}
	}
}
