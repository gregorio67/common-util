package cmn.util.exception;

import java.util.Locale;

import org.springframework.context.MessageSource;

public class UtilException extends BaseException {

	private static final long serialVersionUID = 1L;

	public UtilException(String message) {
		super(message);
	}
	
	public UtilException(String code, String message) {
		super(message);
	}

	public UtilException(String code, Object[] param) {
		super(code, param);
	}
	
	public UtilException(String code, String message, Object[] param) {
		super(code, message, param);
	}

	public UtilException(Throwable cause) {
		super(cause);
	}

	public UtilException(String message, Throwable cause) {
		super(message, cause);
	}

	public UtilException(String code, MessageSource messageSource) {
		super(code, messageSource);
	}

	public UtilException(String code, MessageSource messageSource,
			Object messageParameters[]) {
		super(code, messageSource, messageParameters);
	}

	public UtilException(String code, MessageSource messageSource,
			Object messageParameters[], Locale locale) {
		super(code, messageSource, messageParameters, locale);
	}

	public String toString() {
		String s = getClass().getName();
		String message = super.getMessage();
		String code = super.getCode();
		StringBuilder stringBuilder = new StringBuilder(s);
		stringBuilder.append(message == null ? "" : (new StringBuilder())
				.append(": ").append(message).toString());
		stringBuilder.append(code == null || "".equals(code) ? " "
				: (new StringBuilder()).append("(").append(code).append(")")
						.toString());
		return stringBuilder.toString();
	}

}
