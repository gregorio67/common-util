package cmn.util.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import org.springframework.context.MessageSource;

public class BaseException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private String message;
	private String code;
	private Object[] param;
	
	public BaseException() {
		super();
		message = "";
		code = "";
		param = null;;
	}

	public BaseException(String message) {
		super(message);
		this.code = "";
		this.param = null;
		this.message = message;
	}

	public BaseException(String code, Object[] param) {
		super();
		this.message="";
		this.code = code;
		this.param = param;
	}
	
	public BaseException(Throwable cause) {
		super(cause);
		this.message = "";
		this.code = "";
		this.param = null;
	}
	
	public BaseException(String code, String message) {
		super(message);
		this.message = message;
		this.code = code;
		this.param = null;
	}

	public BaseException(String code, String message, Object[] param) {
		super(message);
		this.message = message;
		this.code = code;
		this.param = param;
	}
	
	
	public BaseException(String message, Throwable cause) {
		super(message, cause);
		this.message = "";
		this.code = "";
		this.message = message;
	}

	public BaseException(String code, MessageSource messageSource) {
		this(messageSource.getMessage(code, null, Locale.getDefault()));
		this.code = code;
	}

	public BaseException(String code, MessageSource messageSource,
			Object messageParameters[]) {
		this(messageSource.getMessage(code, messageParameters,
				Locale.getDefault()));
		this.code = code;
	}

	public BaseException(String code, MessageSource messageSource,
			Object messageParameters[], Locale locale) {
		this(messageSource.getMessage(code, messageParameters, locale));
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	protected void setMessage(String message) {
		this.message = message;
	}

	protected void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public Object[] getParam() {
		return param;
	}

	public void setParam(Object[] param) {
		this.param = param;
	}
	public Throwable getRootCause() {
		Throwable tempCause;
		for (tempCause = getCause(); tempCause != null
				&& tempCause.getCause() != null; tempCause = tempCause
				.getCause())
			;
		return tempCause;
	}

	public String getStackTraceString() {
		StringWriter s = new StringWriter();
		super.printStackTrace(new PrintWriter(s));
		return s.toString();
	}

	public void printStackTrace(PrintWriter log) {
		log.println(getStackTraceString());
	}

}
