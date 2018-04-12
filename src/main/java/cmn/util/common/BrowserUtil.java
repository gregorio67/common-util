import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BrowserUtil {

	/**
	 * Disposition 지정합니다.
	 * @param filename
	 * @param request
	 * @param response
	 * @throws Exception
	 * @return void
	 */
	public static void setDisposition(String filename, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (filename == null || filename.equals("")) {
			return;
		}
		if (request == null) {
			return;
		}
		if (response == null) {
			return;
		}
		
		String browser = getBrowser(request);
		
		String dispositionPrefix = "attachment; filename=";
		String encodedFilename = null;
		
		if (browser.equals("MSIE")) {
		    encodedFilename = URLEncoder.encode(filename, "UTF-8");
		} else if (browser.equals("Firefox")) {
		    encodedFilename = "\"" + filename + "\"";
		} else if (browser.equals("Opera")) {
		    encodedFilename = "\"" + filename + "\"";
		} else if (browser.equals("Chrome")
                || browser.equals("Gecko")) { // ie11용
		    StringBuffer sb = new StringBuffer();
		    
		    int fileNmLength = filename.length();
		    
		    for (int i = 0; i < fileNmLength; i++) {
		    	char c = filename.charAt(i);
				if (c > '~') {
				    sb.append(URLEncoder.encode("" + c, "UTF-8"));
				} else {
				    sb.append(c);
				}
		    }
		    encodedFilename = sb.toString();
		} else {
		    throw new IOException("Not supported browser");
		}
		
		response.setHeader("Content-Disposition", dispositionPrefix + encodedFilename);
	
		if ("Opera".equals(browser)){
		    response.setContentType("application/octet-stream;charset=UTF-8");
		}
    }
	
	/**
	 * 브라우저를 구분합니다.
	 * @param request
	 * @return
	 * @return String
	 */
	public static String getBrowser(HttpServletRequest request) {
        String header = request.getHeader("User-Agent");
        if (header.indexOf("Gecko") > -1) {   // ie11용
        	return "Gecko";
        }
        if (header.indexOf("MSIE") > -1) {
            return "MSIE";
        } else if (header.indexOf("Chrome") > -1) {
            return "Chrome";
        } else if (header.indexOf("Opera") > -1) {
            return "Opera";
        }
        return "Firefox";
    }
	
}
