package cmn.util.spring.batch.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import cmn.util.dao.BaseDao;

public class BaseSetpListener implements StepExecutionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseSetpListener.class);
	
//	@Resource(name = "batchProcessSvc")
//	private BatchProcessSvc batchProcessSvc;
	
	private BaseDao baseDao;
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
		/** Get Job Execution ID **/
		JobExecution jobExecution = stepExecution.getJobExecution();
		long jobId = jobExecution.getJobId();
		
		/** Get Step Name **/
		String stepName = stepExecution.getStepName();
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("jobId", jobId);
		paramMap.put("stepName", stepName);
		paramMap.put("stepStatus", stepExecution.getStatus());
		
		/** Update BATCH_JOB_HIST **/
		try {
//			batchProcessSvc.processStartStop(paramMap);
			baseDao.update("Batch.Process.updStepHist", paramMap);
		}
		catch(Exception e) {
			LOGGER.error("Batch Job Step Update Error :: {}", e.getMessage());
		}
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		JobExecution jobExecution = stepExecution.getJobExecution();
		long jobId = jobExecution.getJobId();
		
		/** Get Step Name **/
		String stepName = stepExecution.getStepName();
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("jobId", jobId);
		paramMap.put("stepName", stepName);
		paramMap.put("stepStatus", stepExecution.getStatus());
		
		/** Update BATCH_JOB_HIST **/
		try {
			
			List<Throwable> exList = stepExecution.getFailureExceptions();
			StringBuilder sb = new StringBuilder();
			for (Throwable t : exList) {
				sb.append(t.getMessage()).append("-");
			}
			
			paramMap.put("errMessage", sb.toString());
			
			
//			batchProcessSvc.processStartStop(paramMap);
			baseDao.update("Batch.Process.updStepHist", paramMap);
		}
		catch(Exception e) {
			LOGGER.error("Batch Job Step Update Error :: {}", e.getMessage());
		}

		return null;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}
}

