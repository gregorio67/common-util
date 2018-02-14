
import java.util.List;

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.util.Assert;

public class BaseDBItemWriter<T> implements ItemWriter<T>, InitializingBean {

	private String queryId;
	private SqlSessionFactory sqlSessionFactory;
	private SqlSessionTemplate sqlSessionTemplate;
	private boolean assertUpdates = true;


	@Override
	public void write(List<? extends T> items) throws Exception {
		if (!items.isEmpty()) {


			for (T item : items) {
				sqlSessionTemplate.update(queryId, item);
			}

			List<BatchResult> results = sqlSessionTemplate.flushStatements();

			if (assertUpdates) {
				if (results.size() != 1) {
					throw new InvalidDataAccessResourceUsageException("Batch execution returned invalid results. "
							+ "Expected 1 but number of BatchResult objects returned was " + results.size());
				}

				int[] updateCounts = results.get(0).getUpdateCounts();

				for (int i = 0; i < updateCounts.length; i++) {
					int value = updateCounts[i];
					if (value == 0) {
						throw new EmptyResultDataAccessException("Item " + i + " of " + updateCounts.length
								+ " did not update any rows: [" + items.get(i) + "]", 1);
					}
				}
			}
		}
	}
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(sqlSessionFactory, "A SqlSessionFactory or a SqlSessionTemplate is required.");	    
		if (sqlSessionTemplate == null) {
		    sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH);	    	
	    }
		Assert.isTrue(ExecutorType.BATCH == sqlSessionTemplate.getExecutorType(),
				"SqlSessionTemplate's executor type must be BATCH");

	    Assert.notNull(queryId, "A statementId is required.");
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


	public void setAssertUpdates(boolean assertUpdates) {
		this.assertUpdates = assertUpdates;
	}
	

}
