package app.web.tw.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import app.web.tw.domain.Operator;

@Repository
@Transactional
public class OperatorDaoImpl extends BaseDao implements OperatorDao {
	private static final Logger logger = LoggerFactory.getLogger(OperatorDaoImpl.class);

	public List<Operator> getList() {
		Criteria cri = getSession().createCriteria(Operator.class);

		@SuppressWarnings("unchecked")
		List<Operator> list = (List<Operator>) cri.list();
		logger.debug("list size : {}", list.size());
		return list;
	}
}
