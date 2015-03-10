package app.web.tw.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class UrlUtil {
	
	private static UrlUtil instance;
	private UrlUtil() { }
	private String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	private Pattern patt = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	
	public static UrlUtil getInstance() {
		if (instance == null) {
			instance = new UrlUtil();
		}
		return instance;
	}

	public String[] parseUrl(String url) {
		String[] args = new String[3];
		try {
			URL u = new URL( url );
			args[0] = u.getProtocol();
			
			int dp = StringUtils.equalsIgnoreCase("http", args[0])==true?80:443;				
			int p = u.getPort()==-1?dp:u.getPort();
			
			args[1] = String.valueOf(p);
			args[2] = u.getPath();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return args;
	}
}
