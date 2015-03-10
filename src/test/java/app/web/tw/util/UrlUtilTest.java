package app.web.tw.util;

import org.junit.Test;


public class UrlUtilTest {

	private String url = "https://123.193.111.60/TFM/channelsearch.aspx";
	
	@Test
	public void test1() {
		String[] args = UrlUtil.getInstance().parseUrl(url);
		System.out.println("args : " + args.length);
		for (int i=0; i<args.length; i++) {
			System.out.println("args[" + i + "]" + args[i]);
		}
	}
}
