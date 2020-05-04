package service;

import javax.jws.WebService;

@WebService(targetNamespace = "http://service/")
public interface CompressionService {
	public String compress(String str);
}
