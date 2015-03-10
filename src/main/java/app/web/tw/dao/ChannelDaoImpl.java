package app.web.tw.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import app.web.tw.domain.Channel;

@Repository
@Transactional
public class ChannelDaoImpl extends BaseDao implements ChannelDao {

	private static final Logger logger = LoggerFactory.getLogger(ChannelDaoImpl.class);

	public List<Channel> getList() {
		Criteria cri = getSession().createCriteria(Channel.class);

		@SuppressWarnings("unchecked")
		List<Channel> list = (List<Channel>) cri.list();
		logger.debug("list size : {}", list.size());
		return list;
	}
}
