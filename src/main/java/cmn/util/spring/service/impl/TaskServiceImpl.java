import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import stis.framework.base.BaseService;
import stis.framework.spring.service.TaskService;

@Service("taskService")
public class TaskServiceImpl extends BaseService implements TaskService {

	@Override
	public boolean checkTaskStatus(String mapperName, String jobId) throws Exception {
		String taskStatus = getMapper(mapperName).select("cmn.task.selTaskStatus", jobId);
		if ("Started".equals(taskStatus)) {
			return false;
		}
		else if ("Completed".equals(taskStatus)) {
			updateTaskStatus(mapperName, jobId, true);
			return true;
		}
		return false;
	}

	@Override
	public int updateTaskStatus(String mapperName, String jobId, boolean isStarted) throws Exception {
		Map<String, Object> taskMap = new HashMap<String, Object>();
		taskMap.put("taskNm", jobId);
		if (isStarted) {
			taskMap.put("startTime", new Date());
			taskMap.put("taskStatus", "Started");
		}
		else {
			taskMap.put("endTime", new Date());
			taskMap.put("taskStatus", "Completed");
		}
		return getMapper(mapperName).update("cmn.task.updTask", taskMap);
	}

}

/**
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org/DTD Config 3.0//EN"	"http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="cmn.task">
	<select id="selTaskStatus" parameterType="string" resultType="string">
		SELECT
			TASK_STATUS
		FROM TB_TASK
		WHERE 1 = 1
		AND TASK_NM = #{_parameter}
	</select>

	<update id="updTask" parameterType="java.util.HashMap">
		UPDATE TB_TASK
		SET 	
			<if test="startTime != null">
		         START_TIME = #{startTime},
		    </if>
			<if test="endTime != null">		         
				END_TIME = #{endTime},
			</if>	
				TASK_STATUS = #{taskStatus}
		WHERE 1 = 1
		AND TASK_NM = #{taskNm}		
	</update>
</mapper>


<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:p="http://www.springframework.org/schema/p"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:task="http://www.springframework.org/schema/task"
        xmlns:mvc="http://www.springframework.org/schema/mvc"
        xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
                http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.0.xsd
                http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-4.0.xsd
                http://www.springframework.org/schema/task	http://www.springframework.org/schema/task/spring-task-4.0.xsd">

	<task:annotation-driven  executor="taskExecutor" scheduler="scheduler"/>
	<!-- mvc:annotation-driven>
		<mvc:async-support default-timeout="2500" task-executor="taskExecutor">
			<mvc:callable-interceptors>
				<bean class="org.springframework.web.servlet.config.MvcNamespaceTests.TestCallableProcessingInterceptor" />
			</mvc:callable-interceptors>
			<mvc:deferred-result-interceptors>
				<bean class="org.springframework.web.servlet.config.MvcNamespaceTests.TestDeferredResultProcessingInterceptor" />
			</mvc:deferred-result-interceptors>
		</mvc:async-support>
	</mvc:annotation-driven-->	
	
	<!-- Async Task -->
	<!-- task:annotation-driven scheduler="scheduler" executor="taskExecutor"/>
	<task:scheduler id="scheduler" pool-size="10"/-->
	
	<bean id="taskExecutor" class="org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor">
	    <property name="corePoolSize" value="100"/>
	    <property name="maxPoolSize" value="100"/>
	    <property name="queueCapacity" value="2000000000"/>
	    <property name="keepAliveSeconds" value="120"/>
	</bean> 
	
	<task:scheduler id="scheduler" pool-size="10"/>
	
	
	    <!-- job bean -->
    <bean id="scheduleJob" class="stis.sb.sample.service.impl.SchedulerServiceImpl" />
    
    <task:scheduled-tasks> <!-- scheduled job list -->
        <task:scheduled ref="scheduleJob" method="executeJob" cron="0/30 * * * * ?"/>
        <!-- add more job here -->
    </task:scheduled-tasks>
</beans>
**/
