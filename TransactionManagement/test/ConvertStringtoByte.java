import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class ConvertStringtoByte {
    public static void main(String[] args) throws UnsupportedEncodingException {


    String address = "stack:overflow";
        


	String oldAddress = address.substring(0, address.indexOf(":"));
	String newAddress = address.substring(address.indexOf(":")+1,address.length());
	System.out.println(oldAddress +","+newAddress);

	ArrayList<String> list = new ArrayList<String>();
	list.add("compression");
	list.add("decompression");
	
	if (list.contains("compression")) System.out.print("ok"); 
	
    
    }
    
    
    
    
}