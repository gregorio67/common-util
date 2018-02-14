/*------------------------------------------------------------------------------
 * PROJ   : UPLUS 해외 송금 프로젝트
 * NAME   : CustomConnectionKeepAliveStrategy.java
 * DESC   : ConnectionKeepAliveStrategy 커스텀
 * Author : 윤순혁
 * VER    : 1.0
 * Copyright 2014 LG CNS All rights reserved
 *------------------------------------------------------------------------------
 *                  변         경         사         항
 *------------------------------------------------------------------------------
 *    DATE       AUTHOR                      DESCRIPTION
 * ----------    ------  ---------------------------------------------------------
 * 2015. 7. 8.   윤순혁    최초 프로그램 작성
 */

package cmn.util.net.http.client;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;


public class CustomConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {

	public static final CustomConnectionKeepAliveStrategy INSTANCE = new CustomConnectionKeepAliveStrategy();

	/* (non-Javadoc)
	 * @see org.apache.http.conn.ConnectionKeepAliveStrategy#getKeepAliveDuration(org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext)
	 */
	public long getKeepAliveDuration( HttpResponse response, HttpContext context ) {
		Args.notNull( response, "HTTP response" );
		HeaderElementIterator headerElements = new BasicHeaderElementIterator( response.headerIterator( HTTP.CONN_KEEP_ALIVE ) );

		while ( headerElements.hasNext() ) {
			HeaderElement headerElement = headerElements.nextElement();
			String param = headerElement.getName();
			String value = headerElement.getValue();
			if ( value != null && param.equalsIgnoreCase( "timeout" ) ) {
				try {
					return (Long.parseLong( value ) * 1000L);
				} catch ( NumberFormatException ignore ) { // NOPMD - Ignore
				}
			}
		}
		return 3 * 1000L;
	}
}
