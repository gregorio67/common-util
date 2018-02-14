package cmn.util.exception;

import java.util.Locale;

import org.springframework.context.MessageSource;

public class BatchException extends BaseException {

	private static final long serialVersionUID = 1L;

	public BatchException(String message) {
		super(message);
	}
	
	public BatchException(String code, String message) {
		super(code, message);
	}

	public BatchException(String code, Object[] param) {
		super(code, param);
	}	
	public BatchException(String code, String message, Object[] param) {
		super(code, message, param);
	}

	public BatchException(Throwable cause) {
		super(cause);
	}

	public BatchException(String message, Throwable cause) {
		super(message, cause);
	}

	public BatchException(String code, MessageSource messageSource) {
		super(code, messageSource);
	}

	public BatchException(String code, MessageSource messageSource,
			Object messageParameters[]) {
		super(code, messageSource, messageParameters);
	}

	public BatchException(String code, MessageSource messageSource,
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
