package cmn.util.spring.batch.tasklet;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;

import cmn.util.dao.BaseDao;
import cmn.util.spring.PropertiesUtil;
import org.springframework.util.Assert;


public abstract class BaseTasklet implements Tasklet, InitializingBean{

	/** LOGGER **/
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	protected BaseDao baseDao;
	
	/** Input Parameter **/
	protected Map<String, Object> parameterValues;

	protected Integer pageSize = 0;
	

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		RepeatStatus repeatStatus = this.executeStep(contribution, chunkContext);
		LOGGER.info("Tasklet End status ::" + repeatStatus);
		return repeatStatus;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (pageSize <= 0) {
			pageSize = PropertiesUtil.getInt("batch.tasklet.page.size");
		}
		
		Assert.notNull(baseDao, "DAO should be configured");
	}


	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	public void setParameterValues(Map<String, Object> parameterValues) {
		this.parameterValues = parameterValues;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public abstract RepeatStatus executeStep(StepContribution contribution, ChunkContext context) throws Exception;

}

