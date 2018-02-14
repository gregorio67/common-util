package cmn.util.spring.batch.runner;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Project : SEHATI Project
 * @Class : BaseScheduleRunner.java
 * @Description :
 * @Author : LGCNS
 * @Since : 2018. 1. 22.
 *
 * @Copyright (c) 2017 SEHATI All rights reserved.
 *----------------------------------------------------------
 * Modification Information
 *----------------------------------------------------------
 * Date            Modifier             Change Reason
 *----------------------------------------------------------
 *2018. 1. 22.          LGCNS           Initial
 *----------------------------------------------------------
 */
public class BaseScheduleRunner {
	// 실행시 사용하는 Logger
	protected static final Logger LOGGER = LoggerFactory.getLogger(BaseScheduleRunner.class);
	
	// Job Context가 저장된 XML 파일 경로 
	private String contextPath;
	
	// Scheduler 관련 설정이 저장된 XML 파일 경로
	private String schedulerJobPath;
	
	// 수행할 Job이 저장된 XML 파일 경로
	private List<String> jobPaths;
	
	// Scheduling을 위한 대기시간
	private long delayTime;
	
	/**
	 * EgovSchedulerRunner 생성자
	 * 
	 * @param contextPath : Job Context가 저장된 XML 파일 경로 
	 * @param schedulerJobPath : Scheduler 관련 설정이 저장된 XML 파일 경로
	 * @param jobPaths : 수행할 Job이 저장된 XML 파일 경로
	 * @param delayTime : Scheduling을 위한 대기시간
	 */
	public BaseScheduleRunner(String contextPath, String schedulerJobPath, List<String> jobPaths, long delayTime) {
		this.contextPath = contextPath;
		this.schedulerJobPath = schedulerJobPath;
		this.jobPaths = jobPaths;
		this.delayTime = delayTime;
	}

	/**
	 * contextPath를 설정한다.
	 * @param contextPath
	 */
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	
	/**
	 * schedulerJobPath를 설정한다.
	 * @param schedulerJobPath
	 */
	public void setSchedulerJobPath(String schedulerJobPath) {
		this.schedulerJobPath = schedulerJobPath;
	}
	
	/**
	 * jobPaths를 설정한다.
	 * @param jobPaths
	 */
	public void setJobPath(List<String> jobPaths) {
		this.jobPaths = jobPaths;
	}
	
	/**
	 * delayTime를 설정한다.
	 * @param delayTime
	 */
	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}
	
	/**
	 * Scheduler를 시작한다.
	 * <br>
	 * Thread.sleep()을 이용하여, Scheduler가 schedulerJob을  실행할 때까지
	 * ApplicationContext를 종료하지 않도록 delayTime 동안 대기한다.
	 * ApplicationContext가 실행되고 있는 동안 Scheduler는 정해진 시간마다 Batch Job을 실행한다.
	 * 기본값: 매 10초마다 Batch Job 실행(Cron 표현식: 0/10 * * * * ?)
	 * 정의된 xml파일 위치: /egovframework/batch/batch-scheduler-runner-context.xml
	 */
	public void start() {		
		List<String> paths = new ArrayList<String>();
		
		paths.add(contextPath);
		paths.add(schedulerJobPath);
		
		// 모든 XML 파일을 ContextPath에 등록한다.
		for (int index = 0; index < jobPaths.size(); index++) {
			paths.add(jobPaths.get(index));
		}
		
		String[] locations = paths.toArray(new String[paths.size()]);
		
		// ApplicationContext를 생성한다.
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(locations, false);
		context.refresh();
		
		LOGGER.warn("ApplicationContext Running Time: " + delayTime / 1000 + " seconds");
		
		boolean mustContinue = false;
		long realDelayTIme = delayTime;
		
		if (delayTime < 0) {
			mustContinue = true;
			realDelayTIme = - 1 * delayTime;
		}
		
		if (mustContinue) {
			LOGGER.info("Continue...");
			while(!Thread.currentThread().isInterrupted()) {
				try {
					Thread.sleep(realDelayTIme);
				} catch (InterruptedException ie) {
					break;
				}
			}
			LOGGER.info("Sleeping ends...");
			context.close();
		} else {
			try {
				// 설정한 시간만큼 대기하고, 그 사이에 Scheduler가 수행된다.
				LOGGER.info("Sleeping Time: " + realDelayTIme / 1000 + " seconds");
				Thread.sleep(realDelayTIme);
				LOGGER.info("Sleeping ends...");
			} catch (InterruptedException ie) {
				/* interrupted by other thread */
			}
			context.close();
		}
	}
}
