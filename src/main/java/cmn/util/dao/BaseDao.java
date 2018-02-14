package cmn.util.dao;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class BaseDao extends SqlSessionDaoSupport implements InitializingBean {
	
	private static  Logger LOGGER = LoggerFactory.getLogger(BaseDao.class);
	
	
	public <T> int insert(String sqlId, T params) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Excuted SQL ID :: {}", sqlId);
		}
		return getSqlSession().insert(sqlId, params);
	}
	
	public <T> int update(String sqlId, T params) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Excuted SQL ID :: {}", sqlId);
		}
		return getSqlSession().update(sqlId, params);
	}

	public <T> int delete(String sqlId, T params) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Excuted SQL ID :: {}", sqlId);
		}
		return getSqlSession().delete(sqlId, params);
	}

	public <T, V> T select(String sqlId, V param) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Excuted SQL ID :: {}", sqlId);
		}
		return getSqlSession().selectOne(sqlId, param);
	}

	@SuppressWarnings("unchecked")
	public <T, V> T selectList(String sqlId, V param) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Excuted SQL ID :: {}", sqlId);
		}
		return (T) getSqlSession().selectList(sqlId, param);
	}
	
}
