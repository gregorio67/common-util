import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import stis.framework.spring.PropertiesUtil;

public class JwtUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

	private static final String key = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";

	public static String generateJwt(String id) throws Exception {
		return generateJwt(id, PropertiesUtil.getString("jwt.issuer.name"));
	}
	public static String generateJwt(String id, String issuer) throws Exception {
		return generateJwt(id, issuer, PropertiesUtil.getString("jwt.subject.name"));
		
	}
	public static String generateJwt(String id, String issuer, String subject) throws Exception {
		return generateJwt(id, issuer, subject, PropertiesUtil.getLong("jwt.expire.time"));
	}
	
	public static String generateJwt(String id, String issuer, String subject, long ttlMillis) throws Exception {

		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(PropertiesUtil.getString("jwt.security.key"));
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		/** Let's set the JWT Claims **/
		JwtBuilder builder = Jwts.builder()
								.setId(id)
								.setIssuedAt(now)
								.setSubject(subject)
								.setIssuer(issuer)
				.signWith(signatureAlgorithm, signingKey);

		/** if it has been specified, let's add the expiration  8*/
		if (ttlMillis >= 0) {
			long expMillis = nowMillis + ttlMillis;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}

		/** Builds the JWT and serializes it to a compact, URL-safe string **/
		return builder.compact();

	}

	public static boolean validateJwt(String jwt, String id) throws Exception {
		return validateJwt(jwt, id, PropertiesUtil.getString("jwt.issuer.name"));
	}

	public static boolean validateJwt(String jwt, String id, String issuer) throws Exception {
		return validateJwt(jwt, id, issuer, PropertiesUtil.getString("jwt.subject.name"));
		
	}

	public static boolean validateJwt(String jwt, String id, String issuer, String subject) throws Exception {
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
		Claims claims = Jwts.parser().setSigningKey(apiKeySecretBytes).parseClaimsJws(jwt).getBody();

		String jwtId = claims.getId();
		String jwtSubject = claims.getSubject();
		String jwtIssuer = claims.getIssuer();
		Date expiration = claims.getExpiration();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ID: " + claims.getId());
			LOGGER.debug("Subject: " + claims.getSubject());
			LOGGER.debug("Issuer: " + claims.getIssuer());
			LOGGER.debug("Expiration: " + claims.getExpiration());			
		}
		
		if (!jwtId.equals(id) || !jwtSubject.equals(subject) || !jwtIssuer.equals(issuer)) {
			return false;
		}
		else {
			Date today = new Date();
			if (expiration.compareTo(today) < 0) {
				return false;
			}
		}
		return true;
	}

	public static void main(String args[]) throws Exception {
		String jwt = generateJwt("kimdoy", "STIS", "STIS_PRJ", 100000);
		System.out.println("jwt ::" + jwt);
		validateJwt(jwt, "kimdoy", "STIS", "STIS_PRJ");
	}

}
