package cmn.util.spring.message;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import cmn.util.exception.BizException;


public abstract class DatabaseMessageSource extends AbstractMessageSource implements ResourceLoaderAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseMessageSource.class);
	
	private ResourceLoader resourceLoader;

	protected Messages messages;

	protected JdbcTemplate jdbcTemplate;
	
	protected DataSource dataSource;
	
	protected String defaultSql;
	
	protected String tableName;
	
	protected String localeColumnName;
	
	protected String msgCodeColumnName;
	
	protected String msgColumnName;
	
	protected String whereCondition;
	
	protected String tableType;
	
	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		String msg = messages.getMessage(code, locale);
		return createMessageFormat(msg, locale);

	}

//	@PostConstruct
	public void init() {

		if (dataSource == null && jdbcTemplate == null) {
			throw new BizException("You should set the configuration correctly. The dataSources or jdbcTemplate is null");
		}
		
		if (jdbcTemplate == null) {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		
		if (defaultSql == null) {
			throw new BizException("You should set the configuaraion correctly. The defaultSql is null");
		}

		LOGGER.info("Initializing message source using query [{}]", defaultSql);

		this.messages = jdbcTemplate.query(defaultSql,
				new ResultSetExtractor<Messages>() {
					@Override
					public Messages extractData(ResultSet rs)
							throws SQLException, DataAccessException {

						return extractMessageData(rs);
					}
				});
	}


	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setDefaultSql(String defaultSql) {
		this.defaultSql = defaultSql;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public void setLocaleColumnName(String localeColumnName) {
		this.localeColumnName = localeColumnName;
	}

	public void setMsgCodeColumnName(String msgCodeColumnName) {
		this.msgCodeColumnName = msgCodeColumnName;
	}

	public void setMsgColumnName(String msgColumnName) {
		this.msgColumnName = msgColumnName;
	}

	public void setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
	}

	
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	
	public void reload() {
		messages.clear();
		init();
	}
	
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = (resourceLoader != null ? resourceLoader : new DefaultResourceLoader());
    }	
	/**
	 * Extracts messages from the given result set.
	 * 
	 * @param rs
	 *            is a result set
	 * @return initialized {@link Messages} instance
	 * @throws SQLException
	 *             if a SQLException is encountered getting column values or
	 *             navigating (that is, there's no need to catch SQLException)
	 * @throws DataAccessException
	 *             in case of custom exceptions
	 */
	protected abstract Messages extractMessageData(ResultSet rs)
			throws SQLException, DataAccessException;

	/**
	 * 
	 * Messages bundle
	 */
	protected static final class Messages {

		/* <code, <locale, message>> */
		private Map<String, Map<Locale, String>> messages;

		public void addMessage(String code, Locale locale, String msg) {
			if (messages == null)
				messages = new HashMap<String, Map<Locale, String>>();

			Map<Locale, String> data = messages.get(code);
			if (data == null) {
				data = new HashMap<Locale, String>();
				messages.put(code, data);
			}

			data.put(locale, msg);
		}

		public String getMessage(String code, Locale locale) {
			Map<Locale, String> data = messages.get(code);
			return data != null ? data.get(locale) : null;
		}
		
		public void clear() {
			messages.clear();
		}
	}
}
