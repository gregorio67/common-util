package cmn.util.net.http.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;
import org.springframework.ws.transport.http.HttpsTransportException;


public class CustomHttpsUrlConnectionMessageSender extends HttpUrlConnectionMessageSender implements InitializingBean{

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomHttpsUrlConnectionMessageSender.class);
    /** The default SSL protocol. */
    public static final String DEFAULT_SSL_PROTOCOL = "ssl";

    private String sslProtocol = DEFAULT_SSL_PROTOCOL;

    private String sslProvider;

    private KeyManager[] keyManagers;

    private TrustManager[] trustManagers;

    private HostnameVerifier hostnameVerifier;

    private SecureRandom rnd;

    private SSLSocketFactory sslSocketFactory;

    /**
     * Set default connection timeout is 5 seconds
     */
    private int connectTimeout = 5000;

    /**
     * Set default read timeout is 5 seconds
     */
    private int readTimeout = 5000;

    /**
     * Sets the SSL protocol to use. Default is {@code ssl}.
     *
     * @see SSLContext#getInstance(String, String)
     */
    public void setSslProtocol(String sslProtocol) {
        Assert.hasLength(sslProtocol, "'sslProtocol' must not be empty");
        this.sslProtocol = sslProtocol;
    }

    /**
     * Sets the SSL provider to use. Default is empty, to use the default provider.
     *
     * @see SSLContext#getInstance(String, String)
     */
    public void setSslProvider(String sslProvider) {
        this.sslProvider = sslProvider;
    }

    /**
     * Specifies the key managers to use for this message sender.
     *
     * <p>Setting either this property or {@link #setTrustManagers(TrustManager[]) trustManagers}  is required.
     *
     * @see SSLContext#init(KeyManager[], TrustManager[], SecureRandom)
     */
    public void setKeyManagers(KeyManager[] keyManagers) {
        this.keyManagers = keyManagers;
    }

    /**
     * Specifies the trust managers to use for this message sender.
     *
     * <p>Setting either this property or {@link #setKeyManagers(KeyManager[]) keyManagers}  is required.
     *
     * @see SSLContext#init(KeyManager[], TrustManager[], SecureRandom)
     */
    public void setTrustManagers(TrustManager[] trustManagers) {
        this.trustManagers = trustManagers;
    }

    /**
     * Specifies the host name verifier to use for this message sender.
     *
     * @see HttpsURLConnection#setHostnameVerifier(HostnameVerifier)
     */
    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    /**
     * Specifies the secure random to use for this message sender.
     *
     * @see SSLContext#init(KeyManager[], TrustManager[], SecureRandom)
     */
    public void setSecureRandom(SecureRandom rnd) {
        this.rnd = rnd;
    }

    /**
     * Specifies the SSLSocketFactory to use for this message sender.
     *
     * @see HttpsURLConnection#setSSLSocketFactory(SSLSocketFactory sf)
     */
    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }



    public int getConnectTimeout(){
        return connectTimeout;
    }


    public void setConnectTimeout(int connectTimeout){
        this.connectTimeout = connectTimeout;
    }


    public int getReadTimeout(){
        return readTimeout;
    }


    public void setReadTimeout(int readTimeout){
        this.readTimeout = readTimeout;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(
                !(ObjectUtils.isEmpty(keyManagers) && ObjectUtils.isEmpty(trustManagers) && (sslSocketFactory == null)),
                "Setting either 'keyManagers', 'trustManagers' or 'sslSocketFactory' is required");
    }

    @Override
    protected void prepareConnection(HttpURLConnection connection) throws IOException {

        super.prepareConnection(connection);
        /**
         * Setting connection timeout and read timeout
         */
        connection.setConnectTimeout( connectTimeout );
        connection.setReadTimeout( readTimeout );

        LOGGER.info( "Ria Web service Timeout : " + "Connect Timeout [" +  connectTimeout + "]:: Read Timeout [" + readTimeout + "]");

        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
            httpsConnection.setSSLSocketFactory(createSslSocketFactory());

            if (hostnameVerifier != null) {
                httpsConnection.setHostnameVerifier(hostnameVerifier);
            }
        }
    }

    private SSLSocketFactory createSslSocketFactory() throws HttpsTransportException {
        if (this.sslSocketFactory != null) {
            return this.sslSocketFactory;
        }
        try {
            SSLContext sslContext =
                    StringUtils.hasLength(sslProvider) ? SSLContext.getInstance(sslProtocol, sslProvider) :
                            SSLContext.getInstance(sslProtocol);
            sslContext.init(keyManagers, trustManagers, rnd);
            if (logger.isDebugEnabled()) {
                logger.debug("Initialized SSL Context with key managers [" +
                        StringUtils.arrayToCommaDelimitedString(keyManagers) + "] trust managers [" +
                        StringUtils.arrayToCommaDelimitedString(trustManagers) + "] secure random [" + rnd +
                        "]");
            }
            return sslContext.getSocketFactory();
        }
        catch (NoSuchAlgorithmException ex) {
            throw new HttpsTransportException("Could not create SSLContext: " + ex.getMessage(), ex);
        }
        catch (NoSuchProviderException ex) {
            throw new HttpsTransportException("Could not create SSLContext: " + ex.getMessage(), ex);
        }
        catch (KeyManagementException ex) {
            throw new HttpsTransportException("Could not initialize SSLContext: " + ex.getMessage(), ex);
        }

    }
}
