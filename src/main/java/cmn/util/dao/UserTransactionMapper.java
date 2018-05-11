
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserTransactionMapper {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	
	private SqlSessionFactory sqlSessionFactory;


	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}	

	
	public SqlSession getSqlSession() throws Exception {
		return getSqlSession(true);
	}
	
	public SqlSession getSqlSession(boolean autoCommit) throws Exception {
		SqlSession sqlSession = null;
		if (this.sqlSessionFactory != null) {
			sqlSession = sqlSessionFactory.openSession(autoCommit);
		}
		sqlSession.getConnection().setAutoCommit(autoCommit);
		return sqlSession;
	}
	
	public void commit(SqlSession sqlSession) throws Exception {
		if (sqlSession != null) {
			sqlSession.commit();
			close(sqlSession);
		}
	}
	
	
	
	public void rollback(SqlSession sqlSession) throws Exception {
		if (sqlSession != null) {
			sqlSession.rollback();
			close(sqlSession);
		}
	}
	
	public void rollback(SqlSession sqlSession, boolean force) throws Exception {
		if (sqlSession != null) {
			sqlSession.rollback(force);
			close(sqlSession);
		}		
	}
	
	public void close(SqlSession sqlSession) throws Exception {
//		sqlSession.getConnection().setAutoCommit(true);
		sqlSession.close();
	}
	
	public <T> T select(SqlSession sqlSession, String sqlId, T param) throws Exception {
		return sqlSession.selectOne(sqlId, param);
	}
	

	public <E> List<E> selectList(SqlSession sqlSession, String sqlId, Object param) throws Exception {
		return sqlSession.selectList(sqlId, param);
	}
	
	
	public <T> int insert(SqlSession sqlSession, String sqlId, T param) throws Exception {
		return sqlSession.update(sqlId, param);
	}
	
	public <T> int update(SqlSession sqlSession, String sqlId, T param) throws Exception {
		return sqlSession.update(sqlId, param);
	}
	
	public <T> int delete(SqlSession sqlSession, String sqlId, T param) throws Exception {
		return sqlSession.delete(sqlId, param);
	}
	
}

/**
  <!--Data Soure Setting -->
  
	<bean id="jndiDataSource" class="org.springframework.jndi.JndiObjectFactoryBean">
	    <property name="jndiName" value="java:comp/env/STISSB" />
    </bean>

	<!-- SQL Log 생성 -->
    <bean id="userDataSource" class="net.sf.log4jdbc.Log4jdbcProxyDataSource">
        <constructor-arg ref="jndiDataSource" />
        <property name="logFormatter">
            <bean class="net.sf.log4jdbc.tools.Log4JdbcCustomFormatter">
                <property name="loggingType" value="MULTI_LINE" />
                <property name="sqlPrefix" value="SQL : " />
            </bean>
        </property>
    </bean>
    
    <!-- Auto Commit Enabling -->
	<bean id="transactionFactory" class="org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory" />
	
	<bean id="userSqlSession" class="org.mybatis.spring.SqlSessionFactoryBean">
		<property name="dataSource" ref="userDataSource" />
		<property name="configLocation" value="classpath:/sqlmap/config/mybatis-config.xml" />
		<property name="mapperLocations" value="classpath:/sqlmap/sb/**/*SQL.xml" />
		<property name="transactionFactory" ref="transactionFactory" />
	</bean>

	<bean id="userTransactionMapper" class="stis.framework.mapper.UserTransactionMapper">
		<property name="sqlSessionFactory" ref="userSqlSession" />
	</bean>
	  
  <!--Service Example -->  
	@Resource(name = "userTranService")
	private UserTranService UserTranService;
	
	@Override
	public List<Map<String, Object>> selectUserList(NexacroMapDTO dto) throws Exception {
		Map<String, Object> searchMap = getDataSet(dto, "searchMap");
		
		List<Map<String, Object>> result = getMapper("sbMapper").selectList("user.selUser", searchMap);
		
		for (Map<String, Object> map : result) {
			UserTranService.newTranUser(map);
//			UserTranService.saveUser(map);
		}
		return getMapper("sbMapper").selectList("user.selUser", searchMap);
	}
  
  
  
  	@Override
	public int newTranUser(Map<String, Object> param) throws Exception {
		boolean isException = false;
		SqlSession sqlSession = userTransactionMapper.getSqlSession(false);
		try {
			userTransactionMapper.insert(sqlSession, "user.insUser1", param);		
			if (param.get("ID").equals("doo")) {
				throw new BizException("TEST");
			}
		}
		catch(Exception ex) {
			LOGGER.error("Service error :: {}", param);
			isException = true;
			sqlSession.rollback();
		}

		if (!isException) {
			userTransactionMapper.commit(sqlSession);			
		}
		return 0;
	}
  
  	<tx:advice id="txAdvice" transaction-manager="transactionManager">
		<tx:attributes>
			<tx:method name="retrieve*" read-only="true" />
			<tx:method name="select*" read-only="true" />
			<tx:method name="insert*" rollback-for="Exception" />
			<tx:method name="update*" rollback-for="Exception" />
			<tx:method name="delete*" rollback-for="Exception" />		
			<tx:method name="save*" rollback-for="Exception" />		
			<tx:method name="newTran*" propagation="REQUIRES_NEW"/>
		</tx:attributes>
	</tx:advice>
**/
