import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import stis.framework.base.BaseService;
import stis.framework.spring.service.AuthCheckService;


public class AuthCheckServiceImpl extends BaseService implements AuthCheckService {

	/** JDBC Template **/
	private JdbcTemplate jdbcTemplate;
	
	/** SQL for checking authorization **/
	private String defaultSql;
	
	/** Result column name from SQL **/
	private String resultColName;
	
	@Required
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Required
	public void setDefaultSql(String defaultSql) {
		this.defaultSql = defaultSql;
	}

	@Required
	public void setResultColName(String resultColName) {
		this.resultColName = resultColName;
	}

	@Override
	public int selectAuthCheck(Object[] params) throws Exception {
		
		Map<String, Object> resultMap = jdbcTemplate.queryForMap(defaultSql, params);
		return resultMap.get(resultColName) != null ? Integer.parseInt(String.valueOf(resultMap.get(resultColName))) : 0;
	}
	
}

/**
    <bean id="authCheckService" class="stis.framework.spring.service.impl.AuthCheckServiceImpl">
		<property name="jdbcTemplate" ref="jdbcTemplate" />
		<property name="defaultSql" value="#{system['auth.check.sql']}" />
		<property name="resultColName" value="cnt" />
	</bean>
	
auth.check.sql=SELECT COUNT(MENU_URI) CNT  \
			   FROM TB_MENU A, \
			   TB_USERROLE B, \
			   TB_ROLE C \
			   WHERE A.MENU_ID = C.MENU_ID \
			   AND B.ROLE_ID = C.ROLE_ID \
			   AND   B.USER_ID = ? \
			   AND A.MENU_URI = ?	
**/  
