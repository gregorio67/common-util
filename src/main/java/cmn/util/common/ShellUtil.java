mport java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;


public class ShellUtil {
	
	
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
		Map<String,String> interfaceMap = BeanUtil.getBean( groupId  );
		
		String shelltDir = interfaceMap.get( "directory" );
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
		
		Map<String, Object> resultMap = null;
		
		/** Initiate Executor **/
		DefaultExecutor executor = new DefaultExecutor();
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		
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
			executor.execute(cmdLine, resultHandler);
			resultMap = MessageUtil.getSuccessMessage(String.format("%s is started with %s parameter", execShell, sb.toString()));
			/** No wait **/
			/** if you want to get result remove comment **/
//			resultHandler.waitFor();
//			if(resultHandler.getException() != null) {
//				throw new BizException("");		
//			}
//			else {
//				int retCode = resultHandler.getExitValue();
//				if (retCode == )
//				resultMap.put("resultCode", result);
//				resultMap.put("message", String.format("%s successfully started with %s", execShell, sb.toString()));
//				
//			}
		}
		catch(Exception ex) {
			resultMap = MessageUtil.getErrorMessage(String.format("%s failed to start shell with %s", execShell, sb.toString()));
		}
		
		return resultMap;
	}
	
	/** 
	 * 
	 *<pre>
	 * 1.Description: Execute shell with job path, job name
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param jobPath
	 * @param jobName
	 * @param execShell
	 * @param shellParams
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> runBatchShell(String jobPath, String jobName, String execShell, String[] shellParams) throws Exception {
		return runBatchShell(jobPath, jobName, null, execShell, shellParams);
	}

	public static Map<String, Object> runBatchShell(String jobPath, String jobName, String jobOption, String execShell, String[] shellParams) throws Exception {
		StringBuilder sb = new StringBuilder("\"");
		for (String s : shellParams) {
			sb.append(s).append(" ");
		}
		sb.append("\"");
		return runBatchShell(jobPath, jobName, jobOption, execShell, sb.toString());
	}
	
	public static Map<String, Object> runBatchShell(String jobPath, String jobName, String jobOption, String execShell, String shellParams) throws Exception {
		
		Map<String, Object> resultMap = null;
		
		/** Initiate Executor **/
		DefaultExecutor executor = new DefaultExecutor();
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		
		/** Initiate Command Line **/
		CommandLine cmdLine = CommandLine.parse(execShell);
		
		/** Set parameter to command line **/
		cmdLine.addArgument(jobPath);
		cmdLine.addArgument(jobName);
		if (!NullUtil.isNull(jobOption)) {
			cmdLine.addArgument(jobOption);
		}

		/** Set job parameter **/
		cmdLine.addArgument(shellParams);
		/** Execute shell **/
		try {
			executor.execute(cmdLine, resultHandler);
			resultMap = MessageUtil.getSuccessMessage(String.format("%s is started with %s parameter", execShell, shellParams));
			/** No wait **/
			/** if you want to get result remove comment **/
//			resultHandler.waitFor();
//			if(resultHandler.getException() != null) {
//				throw new BizException("");		
//			}
//			else {
//				int retCode = resultHandler.getExitValue();
//				if (retCode == )
//				resultMap.put("resultCode", result);
//				resultMap.put("message", String.format("%s successfully started with %s", execShell, sb.toString()));
//				
//			}
		}
		catch(Exception ex) {
			resultMap = MessageUtil.getErrorMessage(String.format("%s failed to start shell with %s", execShell, shellParams));
		}
		
		return resultMap;
	}
