package app.web.tw.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.web.tw.dao.ChannelDao;

@Service
public class ChannelService {
	
	@Autowired
	private ChannelDao channelDao;
	
	private static final Logger logger = LoggerFactory.getLogger(ChannelService.class);
	
	public void test() {
		logger.debug("channelDao : {}", channelDao);
	}
	
}
