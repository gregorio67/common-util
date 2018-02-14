package cmn.util.spring;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cmn.util.dao.BaseDao;

public class BaseService {
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
//	protected final Logger LOGGER = LogManager.getLogger(getClass());
		
	/**
	 * BaseDao for processing sql with mybatis
	 */
	@Resource(name = "baseDao")
	protected BaseDao baseDao;
	
	
	@Resource(name = "sqlSessionFactory")
	SqlSessionFactory sqlSessionFactory;

	/**
	 * Get SQL statement with bound parameter
	 * <pre>
	 *
	 * </pre>
	 * @param sqlId String
	 * @param param Map<K, V>
	 * @return String
	 * @throws Exception
	 */
	protected <K, V> String  getSQLStatement(String sqlId, Map<K,V> param) throws Exception {
		
		Configuration configuration = sqlSessionFactory.getConfiguration();
		MappedStatement statement = configuration.getMappedStatement(sqlId);
		
		BoundSql boundSql = statement.getBoundSql(param);
		String sql = boundSql.getSql();
		
		sql = sql.replaceAll("\t", "").replaceAll("\n", " ");
		
		return sql;
				
	}
}
