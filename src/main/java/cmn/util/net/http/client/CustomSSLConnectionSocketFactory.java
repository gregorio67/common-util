package cmn.util.net.http.client;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.FactoryBean;

public class CustomSSLConnectionSocketFactory implements FactoryBean<SSLConnectionSocketFactory> {

	private boolean allowAllHostname;

	private boolean allowSelfSignedCa;

	/**
	 * @param allowAllHostname the allowAllHostname to set
	 */
	public void setAllowAllHostname( boolean allowAllHostname ) {
		this.allowAllHostname = allowAllHostname;
	}

	/**
	 * @param allowSelfSignedCa the allowSelfSignedCa to set
	 */
	public void setAllowSelfSignedCa( boolean allowSelfSignedCa ) {
		this.allowSelfSignedCa = allowSelfSignedCa;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	public SSLConnectionSocketFactory getObject() throws Exception {
		/*
		 * SSLConnectionSocketFactory.getSocketFactory() 커스텀
		 *  - HTTPS VERIFIER 수정: NoopHostnameVerifier
		 *  - TRUST CA 전략 수정 : TrustSelfSignedStrategy
		 */
		// HTTPS 요청시 인증서 오류(개발/검증 서버의 사설 인증서 적용 상황)를 막기위한 SSLSocket Context 생성 과정
		HostnameVerifier hostnameVerifier = this.allowAllHostname ? NoopHostnameVerifier.INSTANCE : SSLConnectionSocketFactory.getDefaultHostnameVerifier();
		SSLContext sslContext = allowSelfSignedCa ? SSLContexts.custom().loadTrustMaterial( new TrustSelfSignedStrategy() ).build() : SSLContexts.createDefault();
		SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory( sslContext, hostnameVerifier );

		return sslSocketFactory;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	public Class<?> getObjectType() {
		return SSLConnectionSocketFactory.class;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return false;
	}
}
