package lzdecompression;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Validate;

import lzdecompression.control.Controller;
import lzdecompression.control.LZDecompressionOutInterceptor;
import service.DecompressionService;
@Component(name = "LZDecompression")
public class LZDecompression {
	Server server;
	JaxWsServerFactoryBean svrFactory; 

	Factory[] factories;
	Controller controller;
	public LZDecompression() {
		System.out.println("start LZDecompression");
		
		
		controller = new Controller();
		controller.setFactory(factories);
		
		DecompressionImpl sendingImpl = new DecompressionImpl();
		svrFactory = new JaxWsServerFactoryBean();
		//MyDestinationFactory destFac = new MyDestinationFactory();
		//svrFactory.setDestinationFactory(destFac);

		svrFactory.setServiceClass(DecompressionService.class);
		svrFactory.setAddress("udp://192.168.56.3:9001/lzdecompression");
		svrFactory.setServiceBean(sendingImpl);
		svrFactory.getOutInterceptors().add(new LZDecompressionOutInterceptor());
		server = svrFactory.create();
		server.start();
	}
	@Validate
	public void start() throws Exception {
//		this.enableProcess = true;
//		server1 = svrFactory1.create();
//		server1.start();
	}

	@Invalidate
	public void stop() throws Exception {
//		this.enableProcess = false;
//		server1.stop();
//		svrFactory1.destroy();
//		controller.stop(); //quit server rmi
	}
	class DecompressionImpl implements DecompressionService {
		public String decompress(String compressed) {

			if (compressed == null)
				return "";
			if (compressed == "")
				return null;
			List<String> dictionary = new ArrayList<String>(200);
			double enlargeIn = 4;
			int dictSize = 4;
			int numBits = 3;
			String entry = "";
			StringBuilder result;
			String w;
			int bits;
			int resb;
			double maxpower;
			int power;
			String c = "";
			int d;
			Data data = Data.getInstance();
			data.string = compressed;
			data.val = (int) compressed.charAt(0);
			data.position = 32768;
			data.index = 1;

			for (int i = 0; i < 3; i += 1) {
				dictionary.add(i, "");
			}

			bits = 0;
			maxpower = Math.pow(2, 2);
			power = 1;
			while (power != Double.valueOf(maxpower).intValue()) {
				resb = data.val & data.position;
				data.position >>= 1;
				if (data.position == 0) {
					data.position = 32768;
					data.val = (int) data.string.charAt(data.index++);
				}
				bits |= (resb > 0 ? 1 : 0) * power;
				power <<= 1;
			}

			switch (bits) {
			case 0:
				bits = 0;
				maxpower = Math.pow(2, 8);
				power = 1;
				while (power != Double.valueOf(maxpower).intValue()) {
					resb = data.val & data.position;
					data.position >>= 1;
					if (data.position == 0) {
						data.position = 32768;
						data.val = (int) data.string.charAt(data.index++);
					}
					bits |= (resb > 0 ? 1 : 0) * power;
					power <<= 1;
				}
				c += (char) bits;
				break;
			case 1:
				bits = 0;
				maxpower = Math.pow(2, 16);
				power = 1;
				while (power != Double.valueOf(maxpower).intValue()) {
					resb = data.val & data.position;
					data.position >>= 1;
					if (data.position == 0) {
						data.position = 32768;
						data.val = (int) data.string.charAt(data.index++);
					}
					bits |= (resb > 0 ? 1 : 0) * power;
					power <<= 1;
				}
				c += (char) bits;
				break;
			case 2:
				return "";

			}

			dictionary.add(3, c);
			w = c;
			result = new StringBuilder(200);
			result.append(c);

			// w = result = c;

			while (true) {
				if (data.index > data.string.length()) {
					return "";
				}

				bits = 0;
				maxpower = Math.pow(2, numBits);
				power = 1;
				while (power != Double.valueOf(maxpower).intValue()) {
					resb = data.val & data.position;
					data.position >>= 1;
					if (data.position == 0) {
						data.position = 32768;
						data.val = (int) data.string.charAt(data.index++);
					}
					bits |= (resb > 0 ? 1 : 0) * power;
					power <<= 1;
				}

				switch (d = bits) {
				case 0:
					bits = 0;
					maxpower = Math.pow(2, 8);
					power = 1;
					while (power != Double.valueOf(maxpower).intValue()) {
						resb = data.val & data.position;
						data.position >>= 1;
						if (data.position == 0) {
							data.position = 32768;
							data.val = (int) data.string.charAt(data.index++);
						}
						bits |= (resb > 0 ? 1 : 0) * power;
						power <<= 1;
					}

					String temp = "";
					temp += (char) bits;
					dictionary.add(dictSize++, temp);

					d = dictSize - 1;

					enlargeIn--;

					break;
				case 1:
					bits = 0;
					maxpower = Math.pow(2, 16);
					power = 1;
					while (power != Double.valueOf(maxpower).intValue()) {
						resb = data.val & data.position;
						data.position >>= 1;
						if (data.position == 0) {
							data.position = 32768;
							data.val = (int) data.string.charAt(data.index++);
						}
						bits |= (resb > 0 ? 1 : 0) * power;
						power <<= 1;
					}

					temp = "";
					temp += (char) bits;

					dictionary.add(dictSize++, temp);

					d = dictSize - 1;

					enlargeIn--;

					break;

				case 2:
					return result.toString();
				}

				if (Double.valueOf(enlargeIn).intValue() == 0) {
					enlargeIn = Math.pow(2, numBits);
					numBits++;
				}

				if (d < dictionary.size() && dictionary.get(d) != null) {
					entry = dictionary.get(d);
				} else {
					if (d == dictSize) {
						entry = w + w.charAt(0);
					} else {
						return null;
					}
				}

				result.append(entry);

				// Add w+entry[0] to the dictionary.
				dictionary.add(dictSize++, w + entry.charAt(0));
				enlargeIn--;

				w = entry;

				if (Double.valueOf(enlargeIn).intValue() == 0) {
					enlargeIn = Math.pow(2, numBits);
					numBits++;
				}

			}
		}

	}
	
}
class Data {
	public int val;
	public String string;
	public int position;
	public int index;

	public static Data getInstance() {
		return new Data();
	}
}

