package service;

import javax.jws.WebService;

@WebService
public interface DecompressionService {
	public String decompress(String str);
}
