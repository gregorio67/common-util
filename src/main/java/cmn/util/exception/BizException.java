package cmn.util.exception;

import java.util.Locale;

import org.springframework.context.MessageSource;

public class BizException extends BaseException {

	private static final long serialVersionUID = 1L;

	public BizException() {
		super();
	}
	
	public BizException(String message) {
		super(message);
	}

	public BizException(String code, String message) {
		super(code, message);
	}
	
	public BizException(String code, Object[] param) {
		super(code, param);
	}
	
	public BizException(String code, String message, Object[] param) {
		super(code, message, param);
	}

	public BizException(Throwable cause) {
		super(cause);
	}

	public BizException(String message, Throwable cause) {
		super(message, cause);
	}

	public BizException(String code, MessageSource messageSource) {
		super(code, messageSource);
	}

	public BizException(String code, MessageSource messageSource,
			Object messageParameters[]) {
		super(code, messageSource, messageParameters);
	}

	public BizException(String code, MessageSource messageSource,
			Object messageParameters[], Locale locale) {
		super(code, messageSource, messageParameters, locale);
	}
	
	public String toString() {
		String s = getClass().getName();
		String message = this.getMessage();
		String code = this.getCode();
		StringBuilder stringBuilder = new StringBuilder(s);
		stringBuilder.append(message == null ? "" : (new StringBuilder())
				.append(": ").append(message).toString());
		stringBuilder.append(code == null || "".equals(code) ? " "
				: (new StringBuilder()).append("(").append(code).append(")")
						.toString());
		return stringBuilder.toString();
	}

}
