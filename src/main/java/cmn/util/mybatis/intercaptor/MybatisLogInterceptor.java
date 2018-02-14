package kics.framework.mybatis.interceptor;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Intercepts({
	@Signature(type=StatementHandler.class, method="update", args={Statement.class}),
	@Signature(type=StatementHandler.class, method="query", args={Statement.class, ResultHandler.class})
})
public class MybatisLogInterceptor implements Interceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(MybatisLogInterceptor.class);
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler handler = (StatementHandler)invocation.getTarget();
		
		BoundSql boundSql = handler.getBoundSql();
		
		String sql = boundSql.getSql();
		
		Object param = handler.getParameterHandler().getParameterObject();
		
		if (param == null) {
			sql = sql.replaceFirst("\\?", "''");
		}
		else {
			if (param instanceof Integer || param instanceof Long || param instanceof Float || param instanceof Double) {
				sql = sql.replaceFirst("\\?",param.toString());
			}
			else if (param instanceof String) {
				sql = sql.replaceFirst("\\?", "'" + param + "'");
			}
			/** Parameter is Map class **/
			else if (param instanceof Map) {
				List<ParameterMapping> paramMappings = boundSql.getParameterMappings();
				for (ParameterMapping mapping : paramMappings) {
					String propValue = mapping.getProperty();
					Object value = ((Map)param).get(propValue);
					if (value instanceof String) {
						sql = sql.replaceFirst("\\?", "'" + value + "'");
					}
					else {
						sql = sql.replaceFirst("\\?", value.toString());
					}
				}
			}
			/** User defined class **/
			else {
				List<ParameterMapping> paramMappings = boundSql.getParameterMappings();
				Class<? extends Object> paramClass = param.getClass();
				
				for (ParameterMapping mapping : paramMappings) {
					String propValue = mapping.getProperty();
					Field field = paramClass.getDeclaredField(propValue);
					field.setAccessible(true);
					
					Class<?> javaType = mapping.getJavaType();
					
					if (String.class == javaType) {
						sql = sql.replaceFirst("\\?", "'" + field.get(param) + "'");						
					}
					else {
						sql = sql.replaceFirst("\\?", field.get(param).toString());						
					}
				}
			}
		}
		
		LOGGER.info("SQL :: {}", sql );
		
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {

	}

}
