package taxris.framework.batch.mybatis;

import static org.springframework.util.Assert.notNull;
import static org.springframework.util.ClassUtils.getShortName;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.beans.factory.InitializingBean;


public class BaseDBItemReader<T> extends AbstractPagingItemReader<T> implements InitializingBean {

	private String queryId;
	private SqlSessionFactory sqlSessionFactory;
	private SqlSessionTemplate sqlSessionTemplate;
	private Map<String, Object> parameterValues;
	
	BaseDBItemReader() {
		setName(getShortName(MyBatisPagingItemReader.class));		
	}
	
	@Override
	protected void doReadPage() {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    if (parameterValues != null) {
	      parameters.putAll(parameterValues);
	    }
	    parameters.put("_page", getPage());
	    parameters.put("_pagesize", getPageSize());
	    parameters.put("_skiprows", getPage() * getPageSize());
	    if (results == null) {
	      results = new CopyOnWriteArrayList<T>();
	    } else {
	      results.clear();
	    }
	    results.addAll(sqlSessionTemplate.<T> selectList(queryId, parameters));		
	}

	@Override
	protected void doJumpToPage(int itemIndex) {
	
	}

	  /**
	   * Check mandatory properties.
	   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	   */
	
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
	    notNull(sqlSessionFactory);
	    if (sqlSessionTemplate == null) {
		    sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH);	    	
	    }
	    notNull(queryId);
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	public void setParameterValues(Map<String, Object> parameterValues) {
		this.parameterValues = parameterValues;
	}
}

