import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;

public class BaseMapper extends EgovAbstractMapper implements InitializingBean {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseMapper.class);
	
	private SqlSessionFactory sqlSessionFactory;
	
	
	BaseMapper() {
		super();
	}
	
	@Required
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public int insert(String sqlId, Object param) {
		return super.insert(sqlId, param);
	}
	
	public int update(String sqlId, Object param) {
		return super.insert(sqlId, param);
	}
	
	public int delete(String sqlId, Object param) {
		return super.insert(sqlId, param);
	}

	public <T> T select(String sqlId, Object param) {
		return super.selectOne(sqlId, param);
	}

	public <E> List<E> selectList(String sqlId, Object param) {
		return super.selectList(sqlId, param);
	}
	
	public List<?> listWithPaging(String sqlId, Object param, int pageIndex, int pageSize) {
		return super.listWithPaging(sqlId, param, pageIndex, pageSize);
	}
	
	
	public <T> void getDataResultHandler(String sqlId, Object param, ResultHandler<T> handler) throws Exception {
		try {
			getSqlSession().select(sqlId, param, handler);			
		}
		catch(NullPointerException ne) {
			LOGGER.error("NullPointException :: {}", ne.getMessage());			
		}
		catch(Exception ex) {
			LOGGER.error("Exception :: {}", ex.getMessage());
		}
		
		
	}
	
	/**
	 * SqlSessionFactory를 Parent에 전달한다.
	 * <pre>
	 *
	 * </pre>
	 * @throws Exception
	 */
	@PostConstruct
	private void afterPropertySet() throws Exception {
		
		super.setSqlSessionFactory(sqlSessionFactory);
	}
	
}
