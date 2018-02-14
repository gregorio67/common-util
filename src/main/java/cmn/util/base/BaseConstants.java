package cmn.util.base;


public class BaseConstants {

	/** ERROR Status CODE*/
	public static final String STATUS_ERROR = "ERROR";

	/** SUCCESS Status CODE */
	public static final String STATUS_SUCCESS = "SUCCESS";

	/** Success code  **/
	public static final String SUCCESS_CODE = "0000";

	/** Error Code **/
	public static final String ERROR_CODE = "9999";
	
	/** Default Json View **/
	public static final String DEFAULT_VIEW_NAME = "jsonView";

	
	/** Default exception attribute **/
    public static final  String exceptionCodeAttribute = "exception";

    /** Default Session Name **/
	public static final String DEFAULT_SESSION_NAME = "sessionData";

    /** Default Encoding Character **/
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	public static final String DEFAULT_FILE_UPLOAD_POLICY = "defaultPolicy";

	/** Set Message source **/
	public static final String DEFAULT_MESSAGE_BEAN_NAME = "messageSource";

	/** Set Default Locale Resolver **/
	public static final String DEFAULT_LOCALE_RESOLVER = "localeResolver";

	/** Default Exception Message **/
	public static final String DEFAULT_EXCEPTION_MESSAGE = "The service is not successfully ended.";

	
	public static final String DEFAULT_EXCEPTION_ERROR_CODE = "err.com.service";

	public static final String DEFAULT_EXCEPTION_URIL_CODE = "err.sys.util";
	
	public static final String DEFAULT_EXCEPTION_DB_CODE = "err.sys.db";

	/** Default Date Format **/
	public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";

	/** Default Date and Time Format **/
	public static final String DEFAULT_DATE_TIME_FORMAT = "dd/MM/yyyy HHmmss";
	
	/** Restful Request Header **/
	public static final String REQUEST_PARAM_HEADER = "reqHeader";

	/** Restful Request Body **/
	public static final String REQUEST_PARAM_BODY = "reqData";
		
	/** Restful Request Body **/
	public static final String RESPONSE_BODY_DATA = "resData";
	
	/** Restful Request Header **/
	public static final String RESPONSE_BODY_MESSAGE = "resMessage";
	
	public static final String RESPONSE_MESSAGE_CODE = "code";
	
	public static final String RESPONSE_MESSAGE_NAME = "message";
	
	public static final String RESPONSE_STATUS_MESSAGE_CODE = "stautsCode";

	
//	public static final String TRAN_STARTED_CODE = "STARTED";
//
//	public static final String TRAN_COMPLETE_CODE = "COMPLETED";
//	
//	public static final String TRAN_FAILED_CODE = "FAILED";
//	
//	public static final String TRAN_UNKNOWN_CODE = "UNKNOWN";

	
	/**
	 * Transaction Status
	 * @author LGCNS
	 *
	 */
	public enum TransactionStatus {
		STARTED,   //Start Transaction
		COMPLETED, //Transaction successfully ended
		FAILED,    //Transaction failed
		UNKNOWN    // Unknown transaction status
	}
	
	/**
	 * Interface Logging Status
	 */
	public enum InterfaceLogStatus {

		SUCCESS, // 성공 2xx
		CLIENT_ERROR, // Exception thrown when an HTTP 4xx is received.
		IO_ERROR, // Exception thrown when an I/O error occurs.
		SERVER_ERROR, // Exception thrown when an HTTP 5xx is received.
		ETC_ERROR, // Base class for exceptions thrown
		UNKNOWN; // Unknown Error
	}
	

}
