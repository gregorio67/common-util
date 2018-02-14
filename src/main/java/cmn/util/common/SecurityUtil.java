package cmn.util.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cmn.util.exception.UtilException;
import cmn.util.spring.MessageUtil;


public class SecurityUtil {

	/**LOGGER SET **/
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtil.class);

    private static Pattern pattern1 = Pattern.compile("\'|\"|<script>|</script>|<object>|</object>", Pattern.CASE_INSENSITIVE);
	private static final String PSWD_SALT = "_framework_taxris_lgcns_com_2017_rev_";
//	private static final String PSWD_SALT = "12345678901234567890123456789012";
	private static String[] mstrSqlInj = {"-", "/"};//(new LConfiguration()).getByEnv("/configuration/security/sql/injection/keyword").Split('|');
	private static boolean mbActive = true;//"Y".Equals((new LConfiguration()).getByEnv("/configuration/security/sql/injection/active"));
	private final static int AES_KEY_SIZE = 128;
    // 파일구분자
    private static final char FILE_SEPARATOR = File.separatorChar;

    private static final int BUFFER_SIZE = 1024;


	//XSS Pattern 설정
	private static Pattern[] patterns = new Pattern[] {
			Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
			Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'",Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"",	Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			Pattern.compile("</script>", Pattern.CASE_INSENSITIVE), Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE	| Pattern.MULTILINE | Pattern.DOTALL),
			Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE), Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
			Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)

	};

	//Pattern default replace
	private static final String PATTERN_REPLACE_STR = "";

	//Default Encoding
	private static final String DEFAULT_ENCODING = "UTF-8";

	//Default SHA algorithm
	private static final String DEFAULT_SHA_ALGORITHM = "SHA-256";
	/**
	 * html과 관련된 특정 문자를 escape한다.
	 * 예) "&"=>"&amp;", "<"=>"&lt;", ">"=>"&gt;", """=>"&quot;", "\'"=>"&#039;", "&"=>"&amp;"
	 * @param str String
	 */
	public static String escape(String str) {

		if ( str == null ){
			return null;
		}

		// Remove all sections that match a pattern
		for (Pattern scriptPattern : patterns) {
			if ( scriptPattern.matcher(str).find() ) {
				str = scriptPattern.matcher(str).replaceAll(PATTERN_REPLACE_STR);
				LOGGER.info("match pattern :: {}, {}", scriptPattern.pattern(), str);
			}
		}

		StringBuffer escapedStr = new StringBuffer();
		char[] ch = str.toCharArray();
		int charSize = ch.length;
		for ( int i=0; i < charSize; i++) {
			if ( ch[i] == '&' )
				escapedStr.append("&amp;");
			else if ( ch[i] == '<' )
				escapedStr.append("&lt;");
			else if ( ch[i] == '>' )
				escapedStr.append("&gt;");
			else if ( ch[i] == '"' )
				escapedStr.append("&quot;");
			else if ( ch[i] == '\'')
				escapedStr.append("&#039;");
			else
				escapedStr.append(ch[i]);
		}

		return escapedStr.toString();
	}



	/**
	 * escape와 반대의 기능을 한다.
	 * 예) "&"<="&amp;", "<"<="&lt;", ">"<="&gt;", """<="&quot;", "\'"<="&#039;", "&"<="&amp;"
	 * @param str String
	 */
	public static String unEscape(String str) {

		if ( str == null ){
			return null;
		}

		str = str.replaceAll("&amp;",  "&");
		str = str.replaceAll("&lt;",   "<");
		str = str.replaceAll("&gt;",   ">");
		str = str.replaceAll("&quot;", "\"");
		str = str.replaceAll("&#039;", "'");

		return str;
	}



	/**
	 * Quotation 문자를 escape한다.
	 * 예) """ => "&quot;", "'" => "&#039;"
	 * @param str String
	 */
	public static String escapeQuot(String str) {

		if ( str == null ){
			return null;
		}

		StringBuffer escapedStr = new StringBuffer();
		char[] ch = str.toCharArray();
		int charSize = ch.length;
		for ( int i=0; i < charSize; i++) {
			if ( ch[i] == '"' )
				escapedStr.append("&quot;");
			else if ( ch[i] == '\'')
				escapedStr.append("&#039;");
			else
				escapedStr.append(ch[i]);
		}

		return escapedStr.toString();
	}



	/**
	 * escapeQuot와 반대의 기능을 한다.
	 * 예) """ <= "&quot;", "'" <= "&#039;"
	 * @param str String
	 */
	public static String unEscapeQuot(String str) {

		if ( str == null ){
			return null;
		}

		str = str.replaceAll("&quot;", "\"");
		str = str.replaceAll("&#039;", "'");

		return str;
	}



	/**
	 * 파일 다운로드 요청 파라미터에서 상위경로를 참조하는지 여부를 확인한다.
	 * @param fileStr String
	 */
	public static String checkFileParam(String fileStr) throws Exception {

		if (fileStr == null) {
			LOGGER.error("SecurityUtil : 파일경로 값이 없습니다.");
			throw new Exception("SecurityUtil : 파일경로 값이 없습니다.");
		}

		if ( (fileStr.indexOf("../") != -1) || (fileStr.indexOf(".\\./") != -1) || (fileStr.indexOf("..\\") != -1) ) {
			LOGGER.error("SecurityUtil : 파일경로에 사용 불가능한 문자가 있습니다. : " + fileStr);
			throw new Exception("SecurityUtil : 파일경로에 사용 불가능한 문자가 있습니다.");
		}

		return fileStr;
	}

	/**
	 * 보안에 문제를 발생시키는 문자열을 대체시킨다.
	 *
	 * <script></script> => 제거
	 * <object></object> => 제거
	 * ' => ''
	 * " => ""
	 *
	 * @param str String 대체 대상 String
	 * @return String 변횐된 String
	 */
	public static String replaceForSecurity(String str)
	{
		if ( str == null ){
			return null;
		}

        Matcher m = pattern1.matcher(str);
        StringBuffer sb = new StringBuffer();
        String replaceStr = "";
        while (m.find()) {

        	if(m.group().toLowerCase().equals("\'")) replaceStr = "\'\'";
        	//else if(m.group().toLowerCase().equals("\"")) replaceStr = "\"\"";
        	else replaceStr = "";
        	LOGGER.info("Invalid String [" + m.group() + "] was replaced " + "["+ replaceStr +"]");
            m.appendReplacement(sb, replaceStr);
        }
        m.appendTail(sb);


		return sb.toString();
	}

	/**
	 * File download시 입력된 파라미터 치환
	 * <pre>
	 *
	 * </pre>
	 * @param param String
	 * @return String
	 * @throws Exception
	 */
	public static String downloadForSecurity(String param) throws Exception {
	 	String resultStr = new String();

	 	/**
	 	 * Get file extension
	 	 */
        String extension = "";
        int i = param.lastIndexOf(".");
        int p = Math.max(param.lastIndexOf("/"), param.lastIndexOf("\\"));
        if (i > p) {
        	extension = param.substring(i+1);
        }

        String fileName = param.substring(0, i);

        if(param != null || !"".equals(param))
        {
            resultStr = fileName.replaceAll("/", "");
            resultStr = resultStr.replaceAll("\"", "");
            resultStr = resultStr.replaceAll("&", "");
            resultStr = resultStr.replaceAll("%2e", "");
            resultStr = resultStr.replaceAll("%2f", "");
            resultStr = resultStr.replaceAll("[..]+", ".");
            resultStr = resultStr.replaceAll("../", "");
            if(LOGGER.isDebugEnabled())
                LOGGER.debug("Origianl :: {} --> Replace :: {}", param, resultStr + "." + extension);
        }
        String resultFileName = null;
        if (extension != null) {
        	resultFileName = resultStr + "." + extension;
        }
        else {
        	resultFileName = resultStr + extension;
        }
        return resultFileName;
	}
	/**
    * byte[] ret = HashUtil.digest("SHA-256", "abcd".getBytes());
    *  처럼 호출
    */

	/**
	 * AS-IS Password 암호화 모듈(SHA-256 Hasgh 사용)
	 * <pre>
	 *
	 * </pre>
	 * @param str String
	 * @return String SHA-256
	 */
   public static String encryptSHA256(String str) throws Exception {

   	String sha = "";

   	try{
   		MessageDigest md = MessageDigest.getInstance(DEFAULT_SHA_ALGORITHM);

   		md.update(str.getBytes());
   		byte byteData[] = md.digest();

   		StringBuffer sb = new StringBuffer();
   		for(int i = 0 ; i < byteData.length ; i++){
   			sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
   		}

   		sha = sb.toString();

   	}catch(NoSuchAlgorithmException e){
   		LOGGER.error("Encrypt Error - NoSuchAlgorithmException:" + str);
   		sha = null;
   	}

   	return sha;
   }

   /**
    *
    *<pre>
    *
    *</pre>
    * @param str
    * @return
    * @throws Exception
    */
	public static String encryptSHA256UTF8(String str) throws Exception {

		String SHA = "";
		try {
			MessageDigest digest = MessageDigest.getInstance(DEFAULT_SHA_ALGORITHM);

			digest.update(str.getBytes(DEFAULT_ENCODING));

			byte byteData[] = digest.digest();

			SHA =  new String( Base64.encodeBase64( byteData ));
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("SecurityUtil::encryptSHA256UTF8 => Error - NoSuchAlgorithmException :" +  str);
			throw new UtilException(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("SecurityUtil::encryptSHA256UTF8 => Error - UnsupportedEncodingException:" +  str);
			throw new UtilException(e.getMessage(), e);
		}

		return SHA;
	}



	/**
	 * AES 암호화 알고리즘은 KEY size에 따라 128(16byte), 192(24byte), 256(32byte)을 지원 한다.
	 * decryptOption = "AES/CBC/PKCS5Padding"; // AES 암호화 옵션
	 * encryptOption = "AES/CBC/PKCS7Padding"; // AES 복호화 옵션
	 * AES128 복호화 메소드
	 */
	public String decryptAES128(String text, String key, String option) throws Exception {
		Cipher cipher = Cipher.getInstance(option);
		byte[] keyBytes = new byte[16];
		byte[] b = key.getBytes("UTF-8");
		int len = b.length;
		if(len > keyBytes.length)
			len = keyBytes.length;
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec   keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec  = new IvParameterSpec(keyBytes);

		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

//		BASE64Decoder base64Decoder = new BASE64Decoder();
		byte [] c = Base64.decodeBase64(text);
		byte [] results = cipher.doFinal(c);

		return new String(results, "UTF-8");
	}

	/**
	 * AES128 암호화 메소드
	 */
	public String encryptAES128(String text, String key, String option) throws Exception {
		Cipher cipher = Cipher.getInstance(option);
		byte[] keyBytes = new byte[16];
		byte[] b = key.getBytes("UTF-8");
		int len = b.length;
		if(len > keyBytes.length)
			len = keyBytes.length;
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec   keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec  = new IvParameterSpec(keyBytes);

		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

		byte [] results = cipher.doFinal(text.getBytes("UTF-8"));
//		Base64.encodeBase64(results);
//		BASE64Encoder base64 = new BASE64Encoder();
		return new String(Base64.encodeBase64(results));
	}

	/**
	 *
	 *<pre>
	 *
	 *</pre>
	 * @param pstrSrc
	 * @return
	 */
	public static String makeHash(String pstrSrc) throws Exception{
		String rstrHashedValue = "";

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] barrDigest = md.digest(pstrSrc.getBytes());

			LOGGER.info(String.valueOf(Hex.encodeHex(barrDigest)));

			rstrHashedValue = new String(Hex.encodeHex(barrDigest));
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error(e.getMessage());
			throw new UtilException(e.getMessage(), e);
		}

		return rstrHashedValue;
	}

	public static String encryptAES(String pstrSrc) throws Exception {
		return encryptAES(pstrSrc, PSWD_SALT);
	}

	/**
	 *
	 *<pre>
	 *
	 *</pre>
	 * @param pstrSrc
	 * @param pstrPswd
	 * @return
	 */
	public static String encryptAES(String pstrSrc, String pstrPswd) throws Exception {
		byte[] key = null;
		byte[] text = null;
		byte[] encrypted = null;

		try {
			key = pstrPswd.getBytes("UTF-8");
			key = Arrays.copyOf(key, AES_KEY_SIZE / 8);
			text = pstrSrc.getBytes("UTF-8");

			// AES/EBC/PKCS5Padding
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
			encrypted = cipher.doFinal(text);
		} catch (Exception e) {
			encrypted = null;
			LOGGER.error(e.getMessage());
			throw new UtilException(e.getMessage(), e);
		}

		return new String(Base64.encodeBase64(encrypted));
	}

	public static String decryptAES(String pstrSrc) throws Exception {
		return decryptAES(pstrSrc, PSWD_SALT);
	}

	public static String decryptAES(String pstrSrc, String pstrPswd) throws Exception {
		byte[] key = null;
		byte[] decrypted = null;

		try {
			key = pstrPswd.getBytes("UTF-8");
			key = Arrays.copyOf(key, AES_KEY_SIZE / 8);

			// AES/EBC/PKCS5Padding
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));

			decrypted = cipher.doFinal(Base64.decodeBase64(pstrSrc));
		} catch (Exception e) {
			decrypted = null;
			LOGGER.error(e.getMessage());
			throw new UtilException(e.getMessage(), e);
		}

		return new String(decrypted);
	}

	public static boolean hasSQLInjectionString(Object pobjValue) {
		return hasSQLInjectionString((pobjValue == null) ? "" : pobjValue.toString());
	}

	public static boolean hasSQLInjectionString(String pstrValue) {
		if (mbActive) {
			for (String strKey : mstrSqlInj) {
				pstrValue = pstrValue + "";
				pstrValue = (pstrValue.endsWith("=") ? pstrValue.substring(0, pstrValue.length() - 1) : pstrValue);
				pstrValue = (pstrValue.endsWith("=") ? pstrValue.substring(0, pstrValue.length() - 1) : pstrValue);
				if ((pstrValue + "").contains(strKey)) {
					System.out.println("[SQL Injection Warning] " + pstrValue);
					return true;
				}
			}
		}
		return false;
	}

	public static boolean hasSQLInjectionString(String[] pstrArrValues) {
		if (mbActive) {
			for (String strValue : pstrArrValues) {
				if (hasSQLInjectionString(strValue)) return true;
			}
		}
		return false;
	}

    /**
     * 파일을 암호화하는 기능
     *
     * @param String source 암호화할 파일
     * @param String target 암호화된 파일
     * @return boolean result 암호화여부 True/False
     * @exception Exception
     */
    public static boolean encryptFile(String source, String target) throws Exception {

		// 암호화 여부
		boolean result = false;

		String sourceFile = source.replace('\\', FILE_SEPARATOR).replace('/', FILE_SEPARATOR);
		String targetFile = target.replace('\\', FILE_SEPARATOR).replace('/', FILE_SEPARATOR);
		File srcFile = new File(sourceFile);

		BufferedInputStream input = null;
		BufferedOutputStream output = null;

		byte[] buffer = new byte[BUFFER_SIZE];

		try {
		    if (srcFile.exists() && srcFile.isFile()) {

				input = new BufferedInputStream(new FileInputStream(srcFile));
				output = new BufferedOutputStream(new FileOutputStream(targetFile));

				int length = 0;
				while ((length = input.read(buffer)) >= 0) {
					byte[] data = new byte[length];
					System.arraycopy(buffer, 0, data, 0, length);
					output.write(encodeBinary(data).getBytes());
					output.write(System.getProperty("line.separator").getBytes());
				}
				result = true;
			}
		}
		catch(Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new UtilException(ex.getMessage(), ex);
		}
		finally {
			input.close();
			output.close();
		}

		return result;
    }

    /**
     * 파일을 복호화하는 기능
     *
     * @param String source 복호화할 파일
     * @param String target 복호화된 파일
     * @return boolean result 복호화여부 True/False
     * @exception Exception
     */
    public static boolean decryptFile(String source, String target) throws Exception {

		// 복호화 여부
		boolean result = false;

		String sourceFile = source.replace('\\', FILE_SEPARATOR).replace('/', FILE_SEPARATOR);
		String targetFile = target.replace('\\', FILE_SEPARATOR).replace('/', FILE_SEPARATOR);
		File srcFile = new File(sourceFile);

		BufferedReader input = null;
		BufferedOutputStream output = null;

		//byte[] buffer = new byte[BUFFER_SIZE];
		String line = null;

		try {
		    if (srcFile.exists() && srcFile.isFile()) {

			input = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile)));
			output = new BufferedOutputStream(new FileOutputStream(targetFile));

			while ((line = input.readLine()) != null) {
			    byte[] data = line.getBytes();
			    output.write(decodeBinary(new String(data)));
			}

			result = true;
		    }
		}
		catch(Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new UtilException(ex.getMessage(), ex);
		}
		finally {
			ResourceCloseHelper.close(input, output);
		}

		return result;
    }

    /**
     * 데이터를 암호화하는 기능
     *
     * @param byte[] data 암호화할 데이터
     * @return String result 암호화된 데이터
     * @exception Exception
     */
    public static String encodeBinary(byte[] data) throws Exception {
		if (data == null) {
		    return "";
		}

		return new String(Base64.encodeBase64(data));
    }

    /**
     * 데이터를 암호화하는 기능
     *
     * @param String data 암호화할 데이터
     * @return String result 암호화된 데이터
     * @exception Exception
     */
    @Deprecated
    public static String encode(String data) throws Exception {
    	return encodeBinary(data.getBytes());
    }

    /**
     * 데이터를 복호화하는 기능
     *
     * @param String data 복호화할 데이터
     * @return String result 복호화된 데이터
     * @exception Exception
     */
    public static byte[] decodeBinary(String data) throws Exception {
    	return Base64.decodeBase64(data.getBytes());
    }

    /**
     * 데이터를 복호화하는 기능
     *
     * @param String data 복호화할 데이터
     * @return String result 복호화된 데이터
     * @exception Exception
     */
    @Deprecated
    public static String decode(String data) throws Exception {
    	return new String(decodeBinary(data));
    }

    /**
     * 비밀번호를 암호화하는 기능(복호화가 되면 안되므로 SHA-256 인코딩 방식 적용).
     *
     * deprecated : 보안 강화를 위하여 salt로 ID를 지정하는 encryptPassword(password, id) 사용
     *
     * @param String data 암호화할 비밀번호
     * @return String result 암호화된 비밀번호
     * @exception Exception
     */
    public static String encryptPassword(String data) throws Exception {

		if (data == null) {
		    return "";
		}

		byte[] plainText = null; // 평문
		byte[] hashValue = null; // 해쉬값
		plainText = data.getBytes();

		MessageDigest md = MessageDigest.getInstance("SHA-256");

		hashValue = md.digest(plainText);

		return new String(Base64.encodeBase64(hashValue));
    }

    /**
     * 비밀번호를 암호화하는 기능(복호화가 되면 안되므로 SHA-256 인코딩 방식 적용)
     *
     * @param password 암호화될 패스워드
     * @param id salt로 사용될 사용자 ID 지정
     * @return
     * @throws Exception
     */
    public static String encryptPassword(String password, String id) throws Exception {

		if (password == null) {
		    return "";
		}

		byte[] hashValue = null; // 해쉬값

		MessageDigest md = MessageDigest.getInstance("SHA-256");

		md.reset();
		md.update(id.getBytes());

		hashValue = md.digest(password.getBytes());

		return new String(Base64.encodeBase64(hashValue));
    }

    /**
     * 비밀번호를 암호화하는 기능(복호화가 되면 안되므로 SHA-256 인코딩 방식 적용)
     * @param data 암호화할 비밀번호
     * @param salt Salt
     * @return 암호화된 비밀번호
     * @throws Exception
     */
    public static String encryptPassword(String data, byte[] salt) throws Exception {

		if (data == null) {
		    return "";
		}

		byte[] hashValue = null; // 해쉬값

		MessageDigest md = MessageDigest.getInstance("SHA-256");

		md.reset();
		md.update(salt);

		hashValue = md.digest(data.getBytes());

		return new String(Base64.encodeBase64(hashValue));
    }

    /**
     * 비밀번호를 암호화된 패스워드 검증(salt가 사용된 경우만 적용).
     *
     * @param data 원 패스워드
     * @param encoded 해쉬처리된 패스워드(Base64 인코딩)
     * @return
     * @throws Exception
     */
    public static boolean checkPassword(String data, String encoded, byte[] salt) throws Exception {
    	byte[] hashValue = null; // 해쉬값

    	MessageDigest md = MessageDigest.getInstance("SHA-256");

    	md.reset();
    	md.update(salt);
    	hashValue = md.digest(data.getBytes());

    	return MessageDigest.isEqual(hashValue, Base64.decodeBase64(encoded.getBytes()));
    }

    /**
     *
     *<pre>
     *
     *</pre>
     * @param value
     * @return
     */
	public static String clearXSSMinimum(String value) {
		if (value == null || value.trim().equals("")) {
			return "";
		}

		String returnValue = value;

		returnValue = returnValue.replaceAll("&", "&amp;");
		returnValue = returnValue.replaceAll("<", "&lt;");
		returnValue = returnValue.replaceAll(">", "&gt;");
		returnValue = returnValue.replaceAll("\"", "&#34;");
		returnValue = returnValue.replaceAll("\'", "&#39;");
		returnValue = returnValue.replaceAll(".", "&#46;");
		returnValue = returnValue.replaceAll("%2E", "&#46;");
		returnValue = returnValue.replaceAll("%2F", "&#47;");
		return returnValue;
	}

	/**
	 *
	 *<pre>
	 *
	 *</pre>
	 * @param value
	 * @return
	 */
	public static String clearXSSMaximum(String value) {
		String returnValue = value;
		returnValue = clearXSSMinimum(returnValue);

		returnValue = returnValue.replaceAll("%00", null);

		returnValue = returnValue.replaceAll("%", "&#37;");

		// \\. => .

		returnValue = returnValue.replaceAll("\\.\\./", ""); // ../
		returnValue = returnValue.replaceAll("\\.\\.\\\\", ""); // ..\
		returnValue = returnValue.replaceAll("\\./", ""); // ./
		returnValue = returnValue.replaceAll("%2F", "");

		return returnValue;
	}

	public static String filePathBlackList(String value) {
		String returnValue = value;
		if (returnValue == null || returnValue.trim().equals("")) {
			return "";
		}

		returnValue = returnValue.replaceAll("\\.\\./", ""); // ../
		returnValue = returnValue.replaceAll("\\.\\.\\\\", ""); // ..\

		return returnValue;
	}

	/**
	 * 행안부 보안취약점 점검 조치 방안.
	 *
	 * @param value
	 * @return
	 */
	public static String filePathReplaceAll(String value) {
		String returnValue = value;
		if (returnValue == null || returnValue.trim().equals("")) {
			return "";
		}

		returnValue = returnValue.replaceAll("/", "");
		returnValue = returnValue.replaceAll("\\", "");
		returnValue = returnValue.replaceAll("\\.\\.", ""); // ..
		returnValue = returnValue.replaceAll("&", "");

		return returnValue;
	}

	public static String filePathWhiteList(String value) {
		return value;
	}

	 public static boolean isIPAddress(String str) {
		Pattern ipPattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

		return ipPattern.matcher(str).matches();
    }

	 public static String removeCRLF(String parameter) {
		 return parameter.replaceAll("\r", "").replaceAll("\n", "");
	 }

	 public static String removeSQLInjectionRisk(String parameter) {
		 return parameter.replaceAll("\\p{Space}", "").replaceAll("\\*", "").replaceAll("%", "").replaceAll(";", "").replaceAll("-", "").replaceAll("\\+", "").replaceAll(",", "");
	 }

	 public static String removeOSCmdRisk(String parameter) {
		 return parameter.replaceAll("\\p{Space}", "").replaceAll("\\*", "").replaceAll("|", "").replaceAll(";", "");
	 }
}
