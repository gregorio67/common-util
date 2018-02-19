package cmn.util.spring.message;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Locale;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import cmn.util.exception.BizException;

public class DBMessageSource extends DatabaseMessageSource {

	@Override
	protected Messages extractMessageData(ResultSet rs) throws SQLException, DataAccessException {
		Messages messages = new Messages();
		if ("column".equals(tableType)) {
			ResultSetMetaData metaData = rs.getMetaData();
			int noOfColumns = metaData.getColumnCount();
			
			try {
				/** Table column : Code, EN, KO, CH **/
				while (rs.next()) {
					String key = rs.getString("code");
					for (int i = 1; i <= noOfColumns; i++) {
						String columnName = metaData.getColumnName(i);
						if (!"code".equalsIgnoreCase(columnName)) {
							Locale locale = new Locale(columnName);
							String msg = rs.getString(columnName);
							messages.addMessage(key, locale, msg);
						}
					}
				}				
			}
			catch(Exception ex) {
				throw new BizException(ex.getMessage());
			}
			finally {
				rs.close();
			}
		}
		/** Table Column : Code, Locale, Message **/
		else if ("row".equals(tableType)){
			try {
				while (rs.next()) {
					Locale locale = new Locale(rs.getString(localeColumnName));
					messages.addMessage(rs.getString(msgCodeColumnName), locale, rs.getString(msgColumnName));
				}							
			}
			catch(Exception ex) {
				throw new BizException(ex.getMessage());
			}
			finally {
				rs.close();
			}
		}
		else {
			throw new BizException("Table Type should be set correctly");
		}
		return messages;
	}
	
	public void refresh() throws Exception {
		this.messages.clear();
		this.messages = jdbcTemplate.query(defaultSql,
				new ResultSetExtractor<Messages>() {
					@Override
					public Messages extractData(ResultSet rs)
							throws SQLException, DataAccessException {

						return extractMessageData(rs);
					}
				});
		
	}
}
