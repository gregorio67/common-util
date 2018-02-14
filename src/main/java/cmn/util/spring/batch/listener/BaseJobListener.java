package cmn.util.spring.batch.listener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import cmn.util.dao.BaseDao;


public class BaseJobListener implements JobExecutionListener,InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseSetpListener.class);

	/** Database Access **/
	private BaseDao baseDao;
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		/** JOB NAME **/
		paramMap.put("jobId",jobExecution.getJobId());
		
		/** Get Job Parameters **/
		JobParameters jobParams = jobExecution.getJobParameters();
		paramMap.put("jobName",jobParams.getString("jobName"));
		
		Map<String, JobParameter> params = jobParams.getParameters();
		
		paramMap.put("jobParam",params.toString());
		paramMap.put("jobStartTime", jobExecution.getStartTime());
		paramMap.put("jobStatus", jobExecution.getStatus());
		
		
		/** INSERT DATA **/
		LOGGER.info("Job Start :: {}", paramMap.toString());
		try {
			baseDao.insert("Batch.Process.insJobHist", paramMap);
//			batchProcessSvc.processStartJob(paramMap);			
		}
		catch(Exception ex) {
			LOGGER.error("Batch Job Insert Error :: {}", ex.getMessage());
		}
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		
		java.util.Date date;
		long startTime = 0L;
		long endTime = 0L;
		String endDate = sdf.format(jobExecution.getEndTime());
		String startDate = sdf.format(jobExecution.getStartTime());

		try {
			date = sdf.parse(startDate);
			startTime = date.getTime();
			date = sdf.parse(endDate);
			endTime = date.getTime();
		} catch (ParseException e) {
		}
		

		paramMap.put("jobId", jobExecution.getJobId());
		paramMap.put("jobEndTime", jobExecution.getEndTime());
		paramMap.put("elapseTime", (endTime - startTime));
		paramMap.put("jobStatus", jobExecution.getStatus());

		LOGGER.info("Job End :: {}", paramMap.toString());
		try {
//			batchProcessSvc.processEndJob(paramMap);			
			baseDao.update("Batch.Process.updJobHist", paramMap);
		}
		catch(Exception ex) {
			LOGGER.error("Batch Job Insert Error :: {}", ex.getMessage());
		}		
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
	
		Assert.notNull(baseDao, "DAO should be configured");
	}
}

