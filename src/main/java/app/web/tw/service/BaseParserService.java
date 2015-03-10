package app.web.tw.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BaseParserService {

	private static final Logger logger = LoggerFactory.getLogger(BaseParserService.class);

	private HttpClient clientInstance = null;

	public HttpClient getClient(String site, int port) {
		if (clientInstance == null) {
			clientInstance = new HttpClient();
			clientInstance.getHostConfiguration().setHost(site, port, "http");
			HttpState initialState = new HttpState();
			clientInstance.setState(initialState);
			clientInstance.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			clientInstance.getParams().setParameter("http.socket.timeout", new Integer(30000));
		}
		try {
			Thread.sleep(500);
			logger.debug("sleep 500 ms");
		} catch (Exception e) {}
		return clientInstance;
	}
}