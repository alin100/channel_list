package app.web.tw.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import app.web.tw.service.ChannelService;

@Controller
@RequestMapping("/test")
public class TestController {
	
	private static final Logger logger = LoggerFactory.getLogger(TestController.class);

	@Autowired
	private ChannelService channelService;
	
	@RequestMapping("/")
	public void test() {
		logger.debug("channel : {}", channelService);
		channelService.test();
	}
}
