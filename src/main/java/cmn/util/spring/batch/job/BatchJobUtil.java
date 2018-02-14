package cmn.util.spring.batch.job;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.CommandLine;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;

import cmn.util.common.NullUtil;
import cmn.util.spring.BeanUtil;
import cmn.util.spring.PropertiesUtil;


public class BatchJobUtil {

	@Resource(name = "jobExplorer")
	private JobExplorer jobExplorer;
	
	/**
	 * Return shell with full path
	 * <pre>
	 *
	 * </pre>
	 * @param groupId String 
	 * @param jobName String
	 * @return String
	 * @throws Exception
	 */
	public String getBatchSheel(String groupId, String jobName) throws Exception {
				
		/** Get Bean in context-jobshell.xml **/
		Map<String,String> interfaceMap = BeanUtil.getBean( groupId);
		
		String shelltDir = PropertiesUtil.getString( interfaceMap.get( "directory" ) );
		String shellName = interfaceMap != null ? interfaceMap.get( jobName ) : null;
		
		/** Set Shell full name **/
		String execShell = shelltDir + "/" + shellName;
		
		return execShell;
	}
	
	/**
	 * Run Shell with shell and parameter
	 * <pre>
	 *
	 * </pre>
	 * @param execShell String
	 * @param shellParams String
	 * @return int
	 * @throws Exception
	 */
	public static Map<String, Object> runBatchShell(String execShell, String[] shellParams) throws Exception {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		int result = 0;
		
		/** Initiate Executor **/
		DefaultExecutor executor = new DefaultExecutor();
		
		/** Initiate Command Line **/
		CommandLine cmdLine = CommandLine.parse(execShell);
		
		/** Set parameter to command line **/
		StringBuilder sb = new StringBuilder().append("[");
		if (!NullUtil.isNull(shellParams) ) {
			for (String param : shellParams) {
				cmdLine.addArgument(param);
				sb.append(param).append(" ");
			}			
		}
		sb.append("]");
		/** Execute shell **/
		try {
			result = executor.execute(cmdLine);
			resultMap.put("resultCode", result);
//			resultMap.put("message", new String("", execShell + " successfully started with " + sb.toString()));
		}
		catch(Exception ex) {
//			resultMap.put("message", String.join("", execShell + " failed to start shell with " + sb.toString()));
		}
		
		return resultMap;
	}
	
	/**
	 * Check Batch Job running (If jobName is null, return false)
	 * <pre>
	 *
	 * </pre>
	 * @param jobName String
 	 * @return boolean
	 * @throws Exception
	 */
	public boolean isJobRunning(String jobName) throws Exception {
		/** jobName is null, return true **/
		if (NullUtil.isNull(jobName)) {
			return false;
		}
		
		boolean isRun = false;

		Set<JobExecution> executions = jobExplorer.findRunningJobExecutions(jobName);
		for(JobExecution execution : executions ){
			if (execution.getStatus() == BatchStatus.STARTED) {
				isRun = true;
			}
		}
		return isRun;
	}
}

