package addID;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.cxf.Bus;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.helpers.LoadingByteArrayOutputStream;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.AbstractDestination;
import org.apache.cxf.transport.Conduit;

import org.apache.cxf.workqueue.AutomaticWorkQueue;
import org.apache.cxf.workqueue.WorkQueueManager;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.stream.StreamIoHandler;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioDatagramSession;



class MyDestination extends AbstractDestination {
    private static final Logger LOG = LogUtils.getL7dLogger(MyDestination.class); 
    private static final AttributeKey KEY_IN = new AttributeKey(StreamIoHandler.class, "in");
    private static final AttributeKey KEY_OUT = new AttributeKey(StreamIoHandler.class, "out");
    
    NioDatagramAcceptor acceptor;
    AutomaticWorkQueue queue;
    volatile MulticastSocket mcast;
    
    public MyDestination(Bus b, EndpointReferenceType ref, EndpointInfo ei) {
        super(b, ref, ei);
    }

    class MCastListener implements Runnable {
        public void run() {
            while (true) {
                if (mcast == null) {
                    return;
                }
                try {
                    byte bytes[] = new byte[64 * 1024];
                    final DatagramPacket p = new DatagramPacket(bytes, bytes.length);
                    mcast.receive(p);
                    
                    LoadingByteArrayOutputStream out = new LoadingByteArrayOutputStream() {
                        public void close() throws IOException {
                            super.close();
                            final DatagramPacket p2 = new DatagramPacket(getRawBytes(),
                                                                         0,
                                                                         this.size(),
                                                                         p.getSocketAddress());
                            mcast.send(p2);
                        }
                    };
                    
                    UDPConnectionInfo info = new UDPConnectionInfo(null,
                                                                   out,
                                                                   new ByteArrayInputStream(bytes, 0, p.getLength()));
                    
                    final MessageImpl m = new MessageImpl();
                    final Exchange exchange = new ExchangeImpl();
                    exchange.setDestination(MyDestination.this);
                    m.setDestination(MyDestination.this);
                    exchange.setInMessage(m);
                    m.setContent(InputStream.class, info.in);
                    m.put(UDPConnectionInfo.class, info);
                    queue.execute(new Runnable() {
                        public void run() {
                            getMessageObserver().onMessage(m);
                        }
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }            
        }
    }
    
    
    /** {@inheritDoc}*/
    @Override
    protected Conduit getInbuiltBackChannel(Message inMessage) {
        if (inMessage.getExchange().isOneWay()) {
            return null;
        }
        final UDPConnectionInfo info = inMessage.get(UDPConnectionInfo.class);
        return new AbstractBackChannelConduit() {
            public void prepare(Message message) throws IOException {
                message.setContent(OutputStream.class, info.out);
            }
        };
    }

    /** {@inheritDoc}*/
    @Override
    protected Logger getLogger() {
        return LOG;
    }

    protected void activate() {
        WorkQueueManager queuem = bus.getExtension(WorkQueueManager.class);
        queue = queuem.getNamedWorkQueue("udp-transport");
        if (queue == null) {
            queue = queuem.getAutomaticWorkQueue();
        }
        
        try {
            URI uri = new URI(this.getAddress().getAddress().getValue());
            InetSocketAddress isa = null;
            if (StringUtils.isEmpty(uri.getHost())) {
                String s = uri.getSchemeSpecificPart();
                if (s.startsWith("//:")) {
                    s = s.substring(3);
                }
                if (s.indexOf('/') != -1) {
                    s = s.substring(0, s.indexOf('/'));
                }
                int port = Integer.parseInt(s);
                isa = new InetSocketAddress(port);
            } else {
                isa = new InetSocketAddress(uri.getHost(), uri.getPort());
            }
            if (isa.getAddress().isMulticastAddress()) {
                //ouch...
                MulticastSocket socket = new MulticastSocket(null);
                socket.setReuseAddress(true);
                socket.setReceiveBufferSize(64 * 1024);
                socket.setSendBufferSize(64 * 1024);
                socket.setTimeToLive(1);
                socket.bind(new InetSocketAddress(isa.getPort()));
                socket.joinGroup(isa.getAddress());
                mcast = socket;
                queue.execute(new MCastListener());
            } else {
                
                acceptor = new NioDatagramAcceptor();
                acceptor.setHandler(new UDPIOHandler());
                
                acceptor.setDefaultLocalAddress(isa);
                DatagramSessionConfig dcfg = acceptor.getSessionConfig();
                dcfg.setReadBufferSize(64 * 1024);
                dcfg.setSendBufferSize(64 * 1024);
                dcfg.setReuseAddress(true);
                acceptor.bind();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    protected void deactivate() {
        if (acceptor != null) {
            acceptor.unbind();
            acceptor.dispose();
        }
        acceptor = null;
        if (mcast != null) {
            mcast.close();
            mcast = null;
        }
    }
    
    static class UDPConnectionInfo {
        final IoSession session;
        final OutputStream out;
        final InputStream in;
        
        public UDPConnectionInfo(IoSession io, OutputStream o, InputStream i) {
            session = io;
            out = o;
            in = i;
        }
    }
    
    
    class UDPIOHandler extends StreamIoHandler {
        
    	OutputStream out;
        @Override
        public void sessionOpened(IoSession session) {
            // Set timeouts
            session.getConfig().setWriteTimeout(getWriteTimeout());
            session.getConfig().setIdleTime(IdleStatus.READER_IDLE, getReadTimeout());

            // Create streams
            InputStream in = new IoSessionInputStream();
            out = new IoSessionOutputStream(session);
            session.setAttribute(KEY_IN, in);
            session.setAttribute(KEY_OUT, out);
            processStreamIo(session, in, out);
        }
        
        protected void processStreamIo(IoSession session, InputStream in, OutputStream out) {
            final MessageImpl m = new MessageImpl();
            final Exchange exchange = new ExchangeImpl();
            exchange.setDestination(MyDestination.this);
            m.setDestination(MyDestination.this);
            exchange.setInMessage(m);
            m.setContent(InputStream.class, in);
            out = new UDPDestinationOutputStream(out);
            m.put(UDPConnectionInfo.class, new UDPConnectionInfo(session, out, in));
            queue.execute(new Runnable() {
                public void run() {
                    getMessageObserver().onMessage(m);
                }
            });
        }
        
        public void sessionClosed(IoSession session) throws Exception {
            final InputStream in = (InputStream) session.getAttribute(KEY_IN);
            final OutputStream out = (OutputStream) session.getAttribute(KEY_OUT);
            try {
                in.close();
            } finally {
                out.close();
            }
        }

        public void messageReceived(IoSession session, Object buf) {
            final IoSessionInputStream in = (IoSessionInputStream) session
                    .getAttribute(KEY_IN);
            in.setBuffer((IoBuffer) buf);
        }

        public void exceptionCaught(IoSession session, Throwable cause) {
            final IoSessionInputStream in = (IoSessionInputStream) session
                    .getAttribute(KEY_IN);

            IOException e = null;
            if (cause instanceof StreamIoException) {
                e = (IOException) cause.getCause();
            } else if (cause instanceof IOException) {
                e = (IOException) cause;
            }

            if (e != null && in != null) {
                in.throwException(e);
            } else {
                session.close(true);
            }
        }
        public void sessionIdle(IoSession session, IdleStatus status) {
            if (status == IdleStatus.READER_IDLE) {
                throw new StreamIoException(new SocketTimeoutException(
                        "Read timeout"));
            }
        }
    }
    private static class StreamIoException extends RuntimeException {
        private static final long serialVersionUID = 3976736960742503222L;

        public StreamIoException(IOException cause) {
            super(cause);
        }
    }
    
    public class UDPDestinationOutputStream extends OutputStream {
        final OutputStream out;
        IoBuffer buffer = IoBuffer.allocate(64 * 1024 - 42); //max size
        boolean closed;
        
        public UDPDestinationOutputStream(OutputStream out) {
            this.out = out;
        }

        public void write(int b) throws IOException {
            buffer.put(new byte[] {(byte)b}, 0, 1);
        }
        public void write(byte b[], int off, int len) throws IOException {
            while (len > buffer.remaining()) {
                int nlen = buffer.remaining();
                buffer.put(b, off, nlen);
                len -= nlen;
                off += nlen;
                send();
                buffer = IoBuffer.allocate((64 * 1024) - 42);
            }
            buffer.put(b, off, len);
        }
        private void send() throws IOException {
            buffer.flip();
            out.write(buffer.array(), 0, buffer.limit());
        }
        public void close() throws IOException {
            if (closed) {
                return;
            }
            closed = true;
            send();
            out.close();
        }
    }
    
}
