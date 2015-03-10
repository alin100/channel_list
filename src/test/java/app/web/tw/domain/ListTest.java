package app.web.tw.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import app.web.tw.BaseTest;
import app.web.tw.dao.ChannelDao;
import app.web.tw.dao.OperatorDao;

public class ListTest extends BaseTest {

	private static final Logger logger = LoggerFactory.getLogger(ListTest.class);

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private ChannelDao channelDao;
	
	@Autowired
	private OperatorDao operatorDao;

	@Test
	public void test1() {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;

		try {

			transaction = session.beginTransaction();
			
			Operator operator = getFakeOperator();			
			session.save(operator);
			
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		logger.debug("test 22222222222");
		test2();

		logger.debug("test 33333333333");
		test3();
	}
	
	public void test2() {
//		Session session = sessionFactory.openSession();
//		Criteria cri = session.createCriteria(Channel.class);
//		
		List<Channel> list = channelDao.getList();
		logger.debug("size : {}", list.size());
		for (Channel c: list) {
			logger.debug(c.toString());
		}
	}
	
	public void test3() {
		List<Operator> list = operatorDao.getList();
		logger.debug("size : {}", list.size());
		for (Operator c: list) {
			logger.debug(c.toString());
		}
	}
	
	public Operator getFakeOperator() {
		List<Channel> list = new ArrayList<Channel>();
		Operator operator = new Operator();
		operator.setName("Test1");
		operator.setOperUrl("http://Test1");
		for (int i=0; i<100; i++) {
			Channel channel = new Channel();
			channel.setNum("chan:"+String.valueOf(i));
			channel.setOperator(operator);
			list.add(channel);
		}
		operator.setChannelList(list);
		return operator;
	}
}
