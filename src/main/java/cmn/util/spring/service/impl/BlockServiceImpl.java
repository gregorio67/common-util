import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import egovframework.rte.psl.dataaccess.util.EgovMap;
import stis.framework.base.BaseService;
import stis.framework.spring.service.BlockService;
import stis.framework.util.NullUtil;

public class BlockServiceImpl extends BaseService implements BlockService {
	
	/** JDBC Template **/
	private JdbcTemplate jdbcTemplate;
	
	/** SQL for reading block service **/
	private String defaultSql;
	
	/** Service Name **/
	private String serviceColumnName;
	
	/** URI Name **/
	private String uriColumnName;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			
	private List<Map<String, Object>> blockServices = new ArrayList<Map<String, Object>>();
	
	@Required
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Required
	public void setDefaultSql(String defaultSql) {
		this.defaultSql = defaultSql;
	}

	@Required
	public void setServiceColumnName(String serviceColumnName) {
		this.serviceColumnName = serviceColumnName;
	}
	
	@Required
	public void setUriColumnName(String uriColumnName) {
		this.uriColumnName = uriColumnName;
	}
	
	
	@Override
	public void selectBlockService() throws Exception {
		
		/** Get current Date **/
		String date = sdf.format(new Date());
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("SQL Parameter :: {}", date);
		}

		/** SQL Parameter **/
		Object[] params = {date, date};
		/** Occurred exception, just logging **/
		try {
			this.blockServices = jdbcTemplate.queryForList(defaultSql, params);			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Read Count :: {}, Read Data :: {}", blockServices.size(), blockServices.toString());
			}
		}
		catch(Exception ex) {
			LOGGER.error("Error : {}", ex.getMessage());
		}
		
//		this.blockServices = jdbcTemplate.query(defaultSql,
//				new ResultSetExtractor<List<EgovMap>>() {
//					@Override
//					public List<EgovMap> extractData(ResultSet rs)
//							throws SQLException, DataAccessException {
//
//						return extractMessageData(rs);
//					}
//				});
	}
	
	private List<EgovMap> extractMessageData(ResultSet rs) throws SQLException, DataAccessException {
		List<EgovMap> result = new ArrayList<EgovMap>();
		
		
		int readCnt = rs.getFetchSize();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Total Read Count :: {}", readCnt);
		}		
		while (rs.next()) {
			
			EgovMap egovMap = new EgovMap();
			
			egovMap.put(serviceColumnName, rs.getString(serviceColumnName));
			egovMap.put(uriColumnName, rs.getString(uriColumnName));
			result.add(egovMap);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Read Block Service :: {}", egovMap);
			}
		}
		return result;
	}
	
	@Override
	public List<String> getBlockList() throws Exception {
		List<String> result = new ArrayList<String>();
		
		for (Map<String, Object> map : blockServices) {
			result.add(String.valueOf(map.get(uriColumnName)));
		}
		return result;
	}
	
	public void init() throws Exception {
		selectBlockService();			
	}
	
	@Override
	public void refresh() throws Exception {
		synchronized(blockServices) {
			blockServices.clear();
			selectBlockService();			
		}
	}
		
}



/**
** Configuration
** 
    <bean id="blockService" class="cmn.framework.spring.service.impl.BlockServiceImpl" init-method="init">
		<property name="jdbcTemplate" ref="jdbcTemplate" />
		<property name="defaultSql" value="#{system['servic.block.sql']}" />
		<property name="serviceColumnName" value="service_name" />
		<property name="uriColumnName" value="service_uri" />
	</bean>
**/
