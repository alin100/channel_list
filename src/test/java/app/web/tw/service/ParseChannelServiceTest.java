package app.web.tw.service;

import java.io.File;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import app.web.tw.BaseTest;
import app.web.tw.domain.Operator;
import app.web.tw.util.ChineseYearUtil;


public class ParseChannelServiceTest extends BaseTest {

	private static final Logger logger = LoggerFactory.getLogger(ParseChannelServiceTest.class);

	private static NumberFormat format3 = new DecimalFormat("000");
	
	@Autowired
	private ParseChannelService parseChannelService;
	
	String folderBase = "/Volumes/1TB/Users/alanlin/Downloads/temp/";


	@Test //紅樹林
	public void test1() throws Exception {		
		String date = ChineseYearUtil.getToday();
		Map<String, String> operMap = new HashMap<String, String>();		
		Operator o = parseChannelService.getOper();
		List<String> list = parseChannelService.initGetChannelList(o);
		File folder = new File(folderBase + date);
		folder.mkdir();
		for(String channel:list) {			
			String json = parseChannelService.getDailyData(o, date, channel);
			String channelNo = format3.format(Integer.parseInt(channel));
			File file = new File(folder, channelNo + ".json");
			FileWriter fw = new FileWriter(file);
			fw.write(json);
			IOUtils.closeQuietly(fw);
			if (StringUtils.isNotBlank(json)) {
				operMap.put(channel, json);
			}
			Thread.sleep(1000);
		}
		logger.debug("j:{}", operMap);
	}	
	
	@Test //test parse html
	public void test2() throws Exception {
		String sourceUrlString="file:" + folderBase + "008_1010211.html";
		Source source=new Source(new URL(sourceUrlString));		
		
		List<Element> tableContent = source.getAllElementsByClass("default");
		logger.debug("size : {}", tableContent.size());
		Map<String, String> showMap = new TreeMap<String, String>();
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
						showMap.put(hour, showName);
					}
				}
		}
		logger.debug("map : {}", showMap.toString());
	}
	
	public String getFirstEContent(List<Element> list) {
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
}
