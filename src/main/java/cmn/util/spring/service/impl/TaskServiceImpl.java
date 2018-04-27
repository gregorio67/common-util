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
**/
