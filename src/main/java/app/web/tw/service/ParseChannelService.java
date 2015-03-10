package app.web.tw.service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import app.web.tw.domain.Operator;
import app.web.tw.domain.Show;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class ParseChannelService extends BaseParserService {

	private static final Logger logger = LoggerFactory.getLogger(ParseChannelService.class);
	
	private HttpClient client;
	
	private String viewState;
	private String viewValidation;
	private String site;
	private int port;
	
	private Gson gson = new Gson();
	private Type typeOfSrc = new TypeToken<Collection<Show>>(){}.getType();
	
	public Operator getOper() {
		Operator oper = new Operator();
		oper.setName("紅樹林");
		oper.setOperUrl("http://123.193.111.60/TFM/channelsearch.aspx");
		return oper;
	}
	
	public List<String> initGetChannelList(Operator o) throws Exception {
		String u = o.getOperUrl();
		URL url = new URL(u);
		site = url.getHost();
		port = url.getPort();
		
		client = getClient(site, port);
		List<String> list = getPage(url.toURI().toString());
		
		return list;
	}
	
	public String getDailyData(Operator o, String date, String channel) throws Exception {
		String u = o.getOperUrl();
		URL url = new URL(u);
		String uri = url.toURI().toString();
		String data = getRemoteData(uri, date, channel);
		logger.debug("r:{}", data);
		List<Show> showList = parseShowList(data);
		String json = gson.toJson(showList, typeOfSrc);
		logger.debug("j:{}", json);
		return json;
	}
	
	private List<Show> parseShowList(String htmlContent) {
		Source source=new Source(htmlContent);
		Element eValidation = source.getElementById("__EVENTVALIDATION");
		Element eViewState = source.getElementById("__VIEWSTATE");
		
		viewState = eViewState.getAttributeValue("value");
		viewValidation = eValidation.getAttributeValue("value");
		
		List<Element> tableContent = source.getAllElementsByClass("default");
		logger.debug("size : {}", tableContent.size());
		List<Show> showList = new ArrayList<Show>();
		for (Element element : tableContent) {
				System.out.println("element : " + element.toString());
				List<Element> childElement = element.getAllElements("tr");
				logger.debug("size child : {}", childElement.size());
				String hour = null;
				String showName = null;
				for (Element e:childElement) {
					List<Element> he = e.getAllElementsByClass("pagebreadtxt");
					List<Element> se = e.getAllElementsByClass("ch_TXT_blue");
					hour = getFirstEContent(he);
					showName = getFirstEContent(se);
					if (hour!=null && showName != null) {
						Show s = new Show();
						s.setName(showName);
						s.setStartTime(hour);
						showList.add(s);
					}
				}
		}
		return showList;
	}
	
	private String getFirstEContent(List<Element> list) {
		if (list!=null && list.size()>=1) {
			String s = list.get(0).getContent().toString().trim();
			String s2 = null;
			try {
				s2 = new String(s.getBytes(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return s2;
		}
		return null;
	}
	
	/**
	 * take care of __VIEWSTATE & __EVENTVALIDATION * cookie etc.
	 */
	private List<String> getPage(String actionUrl) {
		GetMethod method = new GetMethod(actionUrl);
        
		try {
			method.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
			
			int statusCode = client.executeMethod(method);
			logger.debug("get page statusCode : " + statusCode);
			String response = method.getResponseBodyAsString();
//			logger.debug("get page response : {}", response);
			method.releaseConnection();
			
			if ((statusCode == HttpStatus.SC_OK) || (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) || (statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
					|| (statusCode == HttpStatus.SC_SEE_OTHER) || (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
				Source s = new Source(response);
				Element eValidation = s.getElementById("__EVENTVALIDATION");
				Element eViewState = s.getElementById("__VIEWSTATE");
				
				viewState = eViewState.getAttributeValue("value");
				viewValidation = eValidation.getAttributeValue("value");
				logger.debug("viewstate : {}", viewState);
				logger.debug("validation : {}", viewValidation);
				
				Element ce = s.getElementById("ddlChn");
				List<Element> optionList = ce.getAllElements("option");
				List<String> list = new ArrayList<String>();
				for(Element e: optionList) {
					list.add(e.getAttributeValue("value"));
				}
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getRemoteData(String uri, String date, String channel) {

		PostMethod method = new PostMethod(uri);
		method = (PostMethod) setRequestHeader(method);
		method.addParameter("ddlDate", date);
		method.addParameter("ddlChn", channel);
		method.addParameter("btnQry", "查詢");
		method.addParameter("__VIEWSTATE", viewState);
		method.addParameter("__EVENTVALIDATION", viewValidation);
		method.getParams().setCookiePolicy(CookiePolicy.RFC_2109);

		try {
			int statusCode = client.executeMethod(method);
			logger.debug("query statusCode : " + statusCode);

			String response = method.getResponseBodyAsString();
//			logger.debug("query response : {}", response);
			method.releaseConnection();

			if ((statusCode == HttpStatus.SC_OK) || (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) || (statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
					|| (statusCode == HttpStatus.SC_SEE_OTHER) || (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
				return response;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private HttpMethodBase setRequestHeader(HttpMethodBase method) {
		method.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		method.setRequestHeader("Accept-Charset", "UTF-8,*;q=0.5");
		method.setRequestHeader("Accept-Encoding", "gzip,deflate,sdch");
		method.setRequestHeader("Accept-Language", "zh-TW,en-US;q=0.8,en;q=0.6");
		method.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");		
		method.setRequestHeader("Referer", "http://123.193.111.60/TFM/channelsearch.aspx");
		method.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.46 Safari/535.11");
		return method;
	}	
}
