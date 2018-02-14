package cmn.util.common;

import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.xml.security.utils.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RandomUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(RandomUtil.class);
	
	
	/**
	 * 
	 *<pre>
	 * Generate number with length
	 *</pre>
	 * @param len in length
	 * @return
	 * @throws Exception
	 */
	public static int generateNumber(int len) throws Exception {

		if (len <= 0 ) {
			return 0;
		}
		RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder()
				.withinRange('0', '9')
                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                .build();
		
		return Integer.parseInt(randomStringGenerator.generate(len));
	}

	/**
	 * 
	 *<pre>
	 * Generate char with length
	 *</pre>
	 * @param len in length
	 * @return
	 * @throws Exception
	 */

	public static String generateChar(int len) throws Exception {

		if (len <= 0 ) {
			return null;
		}
		
		RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder()
				.withinRange('A', 'z')
                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                .build();
		
		return randomStringGenerator.generate(len);
	}

	/**
	 * 
	 *<pre>
	 * Generate char and number combination with length
	 *</pre>
	 * @param len in length
	 * @return
	 * @throws Exception
	 */
	public static String generateCharWithNumber(int len) throws Exception {

		if (len <= 0 ) {
			return null;
		}
		
		RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder()
				.withinRange('0', 'z')
                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                .build();
		
		return randomStringGenerator.generate(len);
	}
	
	/**
	 *
	 *<pre>
	 * Create UUID
	 *</pre>
	 * @return String
	 * @throws Exception
	 */
	public static String getUUID() throws Exception {
		return getUUID(true);
	}

	/**
	 *
	 *<pre>
	 * Create UUID without "-" char
	 *</pre>
	 * @return String
	 * @throws Exception
	 */
	public static String getUUID(boolean original) throws Exception {
		if (original) {
			return String.valueOf(UUID.randomUUID());			
		}
		
		else {
			String uuid = String.valueOf(UUID.randomUUID());
			uuid = uuid.replaceAll("-", "");
			return uuid;
			
		}
	}

	/**
	 * 
	 *<pre>
	 * Encode base64 with value
	 *</pre>
	 * @param value String
	 * @return String
	 * @throws Exception
	 */
	
	public static String baseEncode(String value) throws Exception {
		if (NullUtil.isNull(value)) {
			return null;
		}
		return Base64.encode(value.getBytes());
	}
	
	/**
	 * 
	 *<pre>
	 * Decode base64 String
	 *</pre>
	 * @param value String
	 * @return String
	 * @throws Exception
	 */
	public static String baseDecode(String value) throws Exception {
		if (NullUtil.isNull(value)) {
			return null;
		}
		return new String(Base64.decode(value));
	}
	
	public static void main(String args) throws Exception {
		
		String uuid = getUUID();
		System.out.println("uuid :: " + uuid );
		String baseUUID =  baseEncode(uuid);
		System.out.println("encoded UUID :: " + baseUUID);
		System.out.println("decode UUID :: " + baseDecode(baseUUID));
	}

}
