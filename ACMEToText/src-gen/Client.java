import org.apache.felix.ipojo.annotations.*;
@Component(name = "Client")
//begin the class
public class Client {

	@Property(name = "synchrone")
	String synchrone = "yes";

}
