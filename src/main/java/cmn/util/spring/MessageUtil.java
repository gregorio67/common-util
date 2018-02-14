package cmn.util.spring;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import cmn.util.base.BaseConstants;
import cmn.util.exception.UtilException;

public class MessageUtil {

	/**LOGGER SET **/
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageUtil.class);

	  /**
	   * Get Message from message file
	   *<pre>
	   *
	   *</pre>
	   * @param messageSource MessageSource
	   * @param msgCode String
	   * @return String
	   * @throws Exception
	   */
	  public static String getMessage(String strMsgCd) throws Exception {
		  return getMessage(strMsgCd, null);
	  }

	  /**
	   *  Get Message from message file
	   *<pre>
	   *
	   *</pre>
	   * @param messageSource MessageSource
	   * @param msgCode String
	   * @param msgParam String
	   * @return String
	   * @throws Exception
	   */
	  public static <U extends Object> String getMessage(String strMsgCd, U arrMsgParam[]) throws Exception {
		  ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();

		  LocaleResolver localeResolver = BeanUtil.getBean(BaseConstants.DEFAULT_LOCALE_RESOLVER);
		  Locale locale = localeResolver.resolveLocale(attributes.getRequest());

		  return getMessage(strMsgCd, arrMsgParam, locale);
	  }

	  /**
	   * Get Message from message file
	   *<pre>
	   *
	   *</pre>
	   * @param messageSource MessageSource
	   * @param msgCode String
	   * @param param <U>
	   * @param locale Locale
	   * @return String
	   * @throws Exception
	   */

	  public static <U extends Object> String getMessage(String strMsgCd, U arrParam[], Locale locale) throws Exception {

		  MessageSource messageSource = BeanUtil.getBean(BaseConstants.DEFAULT_MESSAGE_BEAN_NAME);

		  if (messageSource == null) {
			  LOGGER.error("Message Bean Creation Error");
			  throw new UtilException("Message Bean Creation Error");
		  }
		  return messageSource.getMessage(strMsgCd, arrParam, locale) != null ? messageSource.getMessage(strMsgCd, arrParam, locale).trim(): "";
	  }
}
