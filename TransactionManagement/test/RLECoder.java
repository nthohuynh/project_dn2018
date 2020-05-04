
public class RLECoder {
	public static String encode(String source) {
	    StringBuffer dest = new StringBuffer();
	    for (int i = 0; i < source.length(); i++) {
	        int runLength = 1;
	        while (i+1 < source.length() && source.charAt(i) == source.charAt(i+1)) {
	            runLength++;
	            i++;
	        }
	        dest.append(runLength);
	        dest.append(source.charAt(i));
	    }
	    return dest.toString();
	}
	
	public static String decompress(String compressedText){
	    char c = ' ';
	    String temp;

	    if(compressedText.length() != 1){
	        c = compressedText.charAt(1);
	    }if(compressedText.length() == 1){
	        return compressedText;       
	    }else if(compressedText.charAt(0) == '0' && compressedText.length() != 2){
	        return decompress(compressedText.substring(2));
	    }else if(compressedText.charAt(0) == '0' && compressedText.length() == 2){
	        compressedText = "\0";
	        return compressedText;
	    }else if(Character.isDigit(compressedText.charAt(0)) == true){
	        int i = Integer.parseInt(compressedText.substring(0,1));
	        i = i-1;
	        temp = Integer.toString(i);
	        return c + decompress(temp.concat(compressedText.substring(1)));
	    }
	    return compressedText.charAt(0)+decompress(compressedText.substring(1));
	}
	public static void main(String arg[]) {
		System.out.println(RLECoder.encode("aaaaaaaaacccccccccdddddddxxaaz"));
		System.out.println(RLECoder.decompress(RLECoder.encode("aaaaaaaaacccccccccdddddddxxaaz")));
	}
}
