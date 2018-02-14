package cmn.util.spring.batch.runner;

import org.springframework.batch.core.JobExecution;
import org.springframework.context.ApplicationEvent;


public class JobExecutionEvent extends ApplicationEvent{

	 /**
	 * 
	 */
	private static final long serialVersionUID = -4438475636795371193L;
	private final JobExecution execution; 
	 
	 /**
	  * Create a new {@link JobExecutionEvent} instance. 
	  * @param execution the job execution 
	  */ 
	 public JobExecutionEvent(JobExecution execution) { 
	  super(execution); 
	  this.execution = execution; 
	 } 
	 
	 /**
	  * Return the job execution. 
	  * @return the job execution 
	  */ 
	 public JobExecution getJobExecution() { 
	  return this.execution; 
	 } 
	 
}
