package kics.framework.mybatis.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import kics.framework.exception.KicsRuntimeException;
import kics.framework.util.SecurityUtil;
import kics.framework.util.Util;


public class EncTypeHandler implements TypeHandler<String> {

	@Override
	public String getResult(ResultSet rs, String columnName) throws SQLException {	

		String s = rs.getString(columnName);

		/** Data is null,return null **/
		if (Util.isNull(s)) {
			return s;
		}
		
		String decText = null;
		try {
			decText = SecurityUtil.decFullData(s);				
		}
		catch(Exception ex) {
			throw new KicsRuntimeException(ex.getMessage(), "sys.err.frame.053");			
		}
		return decText;
	}

	@Override
	public String getResult(ResultSet rs, int index) throws SQLException {
		String s = rs.getString(index);

		/** Data is null,return null **/
		if (Util.isNull(s)) {
			return s;
		}
		
		String decText = null;
		try {
			decText = SecurityUtil.decFullData(s);				
		}
		catch(Exception ex) {
			throw new KicsRuntimeException(ex.getMessage(), "sys.err.frame.053");			
		}
		
		return decText;
	}

	@Override
	public String getResult(CallableStatement cs, int index) throws SQLException {
		String s = cs.getString(index);
		
		/** Data is null,return null **/
		if (Util.isNull(s)) {
			return s;
		}
		
		String decText = null;
		try {
			decText = SecurityUtil.decFullData(s);				
		}
		catch(Exception ex) {
			throw new KicsRuntimeException(ex.getMessage(), "sys.err.frame.053");			
		}
		
		return decText;
	}

	@Override
	public void setParameter(PreparedStatement ps, int index, String data, JdbcType jdbcType) throws SQLException {
		try {
			if (!Util.isNull(data)) {
				ps.setString(index, SecurityUtil.encFullData(data));							
			}
			else {
				ps.setString(index, data);
			}
		}
		catch(Exception ex) {
			throw new KicsRuntimeException(ex.getMessage(), "sys.err.frame.054");
		}
		
	}
	
}
