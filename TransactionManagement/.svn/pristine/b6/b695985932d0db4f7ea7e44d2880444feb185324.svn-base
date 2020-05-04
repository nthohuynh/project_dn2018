package addID;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.AbstractTransportFactory;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.ConduitInitiator;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.DestinationFactory;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.apache.cxf.ws.addressing.EndpointReferenceType;

public class MyTransportFactory  extends AbstractTransportFactory
implements DestinationFactory, ConduitInitiator {
	   
public static final String TRANSPORT_ID = "http://cxf.apache.org/transports/udp";
public static final List<String> DEFAULT_NAMESPACES 
    = Arrays.asList(TRANSPORT_ID);

private static final Logger LOG = LogUtils.getL7dLogger(MyTransportFactory.class);
private static final Set<String> URI_PREFIXES = new HashSet<String>();
static {
    URI_PREFIXES.add("udp://");
}

private Set<String> uriPrefixes = new HashSet<String>(URI_PREFIXES);

public MyTransportFactory() {
    super(DEFAULT_NAMESPACES);
}

public Destination getDestination(EndpointInfo ei, Bus bus) throws IOException {
    return getDestination(ei, null, bus);
}

protected Destination getDestination(EndpointInfo ei,
                                     EndpointReferenceType reference,
                                     Bus bus)
    throws IOException {
    if (reference == null) {
        reference = createReference(ei);
    }
    return new MyDestination(bus, reference, ei);
}


public Conduit getConduit(EndpointInfo ei, Bus bus) throws IOException {
    return getConduit(ei, null, bus);
}

public Conduit getConduit(EndpointInfo ei, EndpointReferenceType target, Bus bus) throws IOException {
    LOG.log(Level.FINE, "Creating conduit for {0}", ei.getAddress());
    if (target == null) {
        target = createReference(ei);
    }
    return new MyConduit(target, bus);
}


public Set<String> getUriPrefixes() {
    return uriPrefixes;
}
public void setUriPrefixes(Set<String> s) {
    uriPrefixes = s;
}
EndpointReferenceType createReference(EndpointInfo ei) {
    EndpointReferenceType epr = new EndpointReferenceType();
    AttributedURIType address = new AttributedURIType();
    address.setValue(ei.getAddress());
    epr.setAddress(address);
    return epr;
}

}
