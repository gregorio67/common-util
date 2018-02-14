package cmn.util.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cmn.util.exception.UtilException;

public class ExecuteCommand {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteCommand.class);

	private static final String DEFAULT_UNIX_COMMAND = "sh";
	
	private static final int DEFAULT_WAIT_TIME = 60 * 1000;
	
	public static int executeCommand(String cmd) throws Exception {
		
		if (NullUtil.isNull(cmd)) {
			LOGGER.error("Command should be set");
			throw new UtilException("Command should be set");
		}
		return executeCommand(cmd, null);
	}
	
	/**
	 * 
	 *<pre>
	 * Execute command with argument
	 *</pre>
	 * @param cmd
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static int executeCommand(String cmd, String[] params) throws Exception {
		CommandLine cmdLine = new CommandLine(cmd);
		if (!NullUtil.isNull(params)) {
			cmdLine.addArguments(params);			
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} is started with {} arguments", cmd, Arrays.toString(cmdLine.getArguments()));
		}
		
		return execute(cmdLine);
	}

	
	public static int executeCommand(String shellName, String jobPath, String jobId, Map<String, Object> paramMap) throws Exception {
		return executeCommand(DEFAULT_UNIX_COMMAND, shellName, jobPath, jobId, paramMap);
	}
		
	/**
	 * 
	 *<pre>
	 * Batch Shell execute 
	 *</pre>
	 * @param cmd String command
	 * @param jobPath String Spring Batch Job Path
	 * @param jobId String Spring Batch Job ID
	 * @param paramMap 
	 * @return Map<String, Object> Spring Batch Parameter
	 * @throws Exception
	 */
	public static int executeCommand(String cmd, String shellName, String jobPath, String jobId, Map<String, Object> paramMap) throws Exception {
		
		if (NullUtil.isNull(shellName) || NullUtil.isNull(jobPath) || NullUtil.isNull(jobId)) {
			LOGGER.error("Command, Job Path or Job Id should be set");
			throw new UtilException("Command, Job Path or Job Id should be set");
		}

		CommandLine cmdLine = new CommandLine(cmd);
		cmdLine.addArgument(jobPath);
		cmdLine.addArgument(jobId);
		
		Iterator<String> itr = paramMap.keySet().iterator();
		StringBuilder sb = new StringBuilder("'");
		while(itr.hasNext()) {
			String key = itr.next();
			String value = paramMap.get(key) != null ? String.valueOf(paramMap.get(key)) : "";
			if (!NullUtil.isNull(value)) {
				sb.append(key).append("=").append(value).append(" ");
			}
		}
		sb = new StringBuilder("'");		
		cmdLine.addArgument(sb.toString());
		
		return execute(cmdLine);
	}
	
	/**
	 * 
	 *<pre>
	 * Execute command with arguments
	 *</pre>
	 * @param cmdLine CommandLine
	 * @return int
	 * @throws Exception
	 */
	private static int execute(CommandLine cmdLine) throws Exception {
		
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		
		ExecuteWatchdog watchdog = new ExecuteWatchdog(DEFAULT_WAIT_TIME);
		
		Executor executor = new DefaultExecutor();
		executor.setExitValue(1);
		executor.setWatchdog(watchdog);

		executor.execute(cmdLine, resultHandler);
		
		resultHandler.waitFor();
		resultHandler.getException().getMessage();
		return resultHandler.getExitValue();
		
	}

	public static List<String> executeWihtResult(CommandLine cmdLine) throws Exception {
		
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		
		ExecuteWatchdog watchdog = new ExecuteWatchdog(DEFAULT_WAIT_TIME);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PumpStreamHandler ps = new PumpStreamHandler(baos);

		Executor executor = new DefaultExecutor();
		executor.setExitValue(1);
		executor.setWatchdog(watchdog);
		executor.setStreamHandler(ps);

		executor.execute(cmdLine, resultHandler);
		Reader reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
		BufferedReader br = new BufferedReader(reader);
		
		resultHandler.waitFor();
		resultHandler.getException().getMessage();
		String temp = null;
		List<String> resultList = new ArrayList<String>();
		while((temp = br.readLine()) != null) {
			resultList.add(temp);
		}
		return resultList;
	}
	
	public static void main(String args[]) throws Exception {
		
		String params[] = {"test", "kim"};
		int retCode = executeCommand("D:/temp/shell/test.cmd", params);
		LOGGER.debug("return Code :: {}", retCode);
	}
}
