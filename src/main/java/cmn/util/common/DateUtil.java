package cmn.util.common;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cmn.util.base.BaseConstants;
import cmn.util.base.BaseUtil;

public class DateUtil extends BaseUtil {

	private static Calendar oCal = Calendar.getInstance();
	private static SimpleDateFormat oFormat = new SimpleDateFormat();

	public DateUtil() {
	}

	/**
	 * Convert Date Format
	 * <pre>
	 *
	 * </pre>
	 * @param date String
	 * @param srcFormat String
	 * @param trgFormat String
	 * @return String
	 * @throws ParseException
	 */
	public static String convertDateFormat(String date, String srcFormat, String trgFormat) throws ParseException {

		java.text.SimpleDateFormat dateFormatSource = new java.text.SimpleDateFormat(srcFormat);
		java.util.Date sourceDate = dateFormatSource.parse(date);
		java.text.SimpleDateFormat dateFormatTarget = new java.text.SimpleDateFormat(trgFormat);

		return dateFormatTarget.format(sourceDate);
	}


	public static String convertDateFormat(String date, String trgFormat) {

		if (date == null)
			return null;

		if (checkDate(date, "yyyy-MM-dd")) {
			java.text.SimpleDateFormat dateFormatSource = new java.text.SimpleDateFormat("yyyy-MM-dd");

			java.util.Date sourceDate;
			try {
				sourceDate = dateFormatSource.parse(date);
			} catch (ParseException e) {
				System.out.println(e);
				return null;
			}

			java.text.SimpleDateFormat dateFormatTarget = new java.text.SimpleDateFormat("yyyyMMdd");
			return dateFormatTarget.format(sourceDate);
		} else if (checkDate(date, "yyyyMMdd")) {
			return date;
		} else {
			return null;
		}
	}

	public static Date convertStringToDate(String date, String format) throws ParseException {
		java.text.SimpleDateFormat dateFormatSource = new java.text.SimpleDateFormat(format);
		return dateFormatSource.parse(date);
	}

	public static Date convertStringToDate(String date) throws ParseException {
		return convertStringToDate(date, BaseConstants.DEFAULT_DATE_FORMAT);
	}

	/**
	 * 현재 시스템의 시간을 반환한다.
	 * @return
	 */
	public static java.sql.Date getCurrentDate() {
		return new java.sql.Date(System.currentTimeMillis());
	}

	/**
	 * 현재 시간을 문자로 반환한다.
	 *
	 * @return
	 */
	public static String getCurrentDate(String format) {
		Date currentDate = new Date();
		java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(format);
		return dateFormat.format(currentDate);
	}

	/**
	 * 시간을 문자로 반환한다.
	 *
	 * @return
	 */
	public static String convertDateToString(Date date, String format) {
		java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	/**
	 * 두 날짜의 차이 구하기
	 *
	 * @param cType
	 *			char : 'S':초, 'M':분, 'H':시, 'D':날짜
	 * @param szDateSrt
	 *			String : 기준 날짜 (DATE1)
	 * @param szDateEnd
	 *			String : 기준 날짜 (DATE2)
	 * @param szDateFormat
	 *			String : 기준 날짜 포맷
	 * @return
	 */
	public static long dateDifference(char cType, String szDateSrt, String szDateEnd, String szDateFormat) {

		Date oDate1 = null;
		Date oDate2 = null;

		long lnResult = 0;
		long lnConvBase = 0;

		oFormat.applyPattern(szDateFormat);

		try {
			oDate1 = oFormat.parse(szDateSrt);
		} catch (ParseException e) {
			;
		}
		;
		try {
			oDate2 = oFormat.parse(szDateEnd);
		} catch (ParseException e) {
			;
		}
		;

		switch (cType) {
		case 'S':
			lnConvBase = 1000;
			break;
		case 'M':
			lnConvBase = 1000 * 60;
			break;
		case 'H':
			lnConvBase = 1000 * 60 * 60;
			break;
		case 'D':
			lnConvBase = 1000 * 60 * 60 * 24;
			break;
		}
		lnResult = (oDate2.getTime() - oDate1.getTime()) / lnConvBase;

		return lnResult;

	}

	/**
	 * 날짜 더하기
	 *
	 * @param cType
	 *			char : 'S':초, 'M':분, 'H':시, 'D':날짜
	 * @param iAdd
	 *			int : 더할 초/분/시/날짜
	 * @param szDate
	 *			String : 기준 날짜
	 * @param szDateFormat
	 *			String : 기준 날짜 포맷
	 * @return
	 */
	public static Date dateAdd(char cType, int iAdd, String szDate, String szDateFormat) {

		Date oDate1 = null;
		Date oDate2 = null;

		long lnTimestamp = 0;
		long lnConvBase = 0;

		oFormat.applyPattern(szDateFormat);

		try {
			oDate1 = oFormat.parse(szDate);
		} catch (ParseException e) {
			;
		}
		;

		lnTimestamp = oDate1.getTime();
		switch (cType) {
		case 'S':
			lnConvBase = 1000;
			break;
		case 'M':
			lnConvBase = 1000 * 60;
			break;
		case 'H':
			lnConvBase = 1000 * 60 * 60;
			break;
		case 'D':
			lnConvBase = 1000 * 60 * 60 * 24;
			break;
		}
		lnTimestamp = lnTimestamp + (lnConvBase * iAdd);

		oDate2 = new Date(lnTimestamp);

		return oDate2;

	}

	/**
	 * 날짜를 던져서 요일 알아내기
	 *
	 * @param cReturnType
	 *			char : 'K'-한글, 'E'-영문, 'A'-영문1, 'B'-영문2
	 * @param iYear
	 *			int : 년
	 * @param iMonth
	 *			int : 월
	 * @param iDay
	 *			int : 일
	 * @return
	 */
	public static String dayOfWeek(char cReturnType, int iYear, int iMonth, int iDay) {

		String szDayOfWeek[][] = { { "일", "월", "화", "수", "목", "금", "토" }, { "S", "M", "T", "W", "T", "F", "S" },{ "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" },{ "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY" } };

		String szResult = new String("");
		int iDayOfWeek = 0;

		oCal.set(Calendar.YEAR, iYear);
		oCal.set(Calendar.MONTH, iMonth - 1);
		oCal.set(Calendar.DAY_OF_MONTH, iDay);

		iDayOfWeek = oCal.get(Calendar.DAY_OF_WEEK);

		switch (cReturnType) {
		case 'K':
			szResult = szDayOfWeek[0][iDayOfWeek - 1];
			break;
		case 'E':
			szResult = szDayOfWeek[1][iDayOfWeek - 1];
			break;
		case 'A':
			szResult = szDayOfWeek[2][iDayOfWeek - 1];
			break;
		case 'B':
			szResult = szDayOfWeek[3][iDayOfWeek - 1];
			break;
		default:
			szResult = Integer.toString(iDayOfWeek);
		}

		return szResult;

	}

	/**
	 * 날짜 포맷 변경
	 *
	 * @param szVictim
	 *			String 포맷 변경할 날짜
	 * @param szFormatCur
	 *			String 현재 포맷 형태
	 * @param szFormatNew
	 *			String 새로운 포맷 형태
	 * @return
	 */
	public static String reformatDate(String szVictim, String szFormatCur, String szFormatNew) {

		Date oDate1 = null;

		String szResult = new String("");
		SimpleDateFormat oDateFormat = new SimpleDateFormat();

		oDateFormat.applyPattern(szFormatCur);

		try {
			oDate1 = oDateFormat.parse(szVictim);
		} catch (Exception e) {
			e.printStackTrace();
		}

		oDateFormat.applyPattern(szFormatNew);
		szResult = oDateFormat.format(oDate1);

		return szResult;

	}

	/**
	 * 날짜, 시간 유효성 체크
	 * 문자열 형식의 날짜 값과 Format 형태를 넘기면, 해당 날짜를 파싱해보고
	 * 파싱 에러가 나면 잘못된 날짜
	 * 파싱 에러가 나지 않으면 정상 날짜
	 *
	 * @param szDate
	 *			String : 체크할 날짜
	 * @param szFormat
	 *			String : 체크할 날짜 포맷
	 * @return 날짜 유효성 통과 여부
	 */
	public static boolean checkDate(String szDate, String szFormat) {

		boolean bResult = true;
		SimpleDateFormat oDateFormat = new SimpleDateFormat();

		oDateFormat.applyPattern(szFormat);
		oDateFormat.setLenient(false); // 엄밀하게 검사한다는 옵션 (반드시 있어야 한다)

		try {
			oDateFormat.parse(szDate);
		} catch (ParseException e) {
			bResult = false;
		}

		return bResult;

	}

	/**
	 * 지정된 포맷으로 날짜를 가져온다.
	 *
	 * @param iAddDay
	 *			int : 몇일전 or 몇일후 날짜를 가져올까? (0 으로 설정하면 오늘 날짜 가져옴)
	 * @param szFormat
	 *			String : 날짜 포맷 (yyyy-MM-dd)
	 * @return 요청한 날짜
	 *		 -
	 */
	public static String getDate(int iAddDay, String szFormat) {

		DecimalFormat oDf = new DecimalFormat("00");

		szFormat = (szFormat.length() <= 0) ? BaseConstants.DEFAULT_DATE_FORMAT : szFormat;

		oCal.add(Calendar.DATE, iAddDay);

		String szYear = Integer.toString(oCal.get(Calendar.YEAR));
		String szMonth = oDf.format(oCal.get(Calendar.MONTH) + 1);
		String szDay = oDf.format(oCal.get(Calendar.DATE));

		int iAmpm = oCal.get(Calendar.AM_PM);
		int iHour = oCal.get(Calendar.HOUR);

		String szHour = oDf.format(iHour + (iAmpm * 12));
		String szMinute = oDf.format(oCal.get(Calendar.MINUTE));
		String szSecond = oDf.format(oCal.get(Calendar.SECOND));

		return reformatDate(szYear + szMonth + szDay + " " + szHour + szMinute + szSecond, BaseConstants.DEFAULT_DATE_TIME_FORMAT, szFormat);

	}

	/**
	 * 초(sec)를 인자로 받아
	 * Human Readable Format 으로 리턴해 준다.
	 * 177981 초 -> 2.01:26:21 (2일 01시간 26분 21초)
	 * -
	 */
	public static String makeHRDate(long lnSeconds) {

		String szResult = new String("");

		String szDay = new String("");
		String szHour = new String("");
		String szMin = new String("");
		String szSec = new String("");

		long lnValue = 0;
		long lnRemain = 0;

		int iOneDay = 60 * 60 * 24;
		int iOneHour = 60 * 60;
		int iOneMin = 60;

		lnRemain = lnSeconds;

		lnValue = lnRemain / iOneDay;
		lnRemain = lnRemain % iOneDay;
		szDay = Long.toString(lnValue);

		lnValue = lnRemain / iOneHour;
		lnRemain = lnRemain % iOneHour;
		szHour = Long.toString(lnValue);

		lnValue = lnRemain / iOneMin;
		lnRemain = lnRemain % iOneMin;
		szMin = Long.toString(lnValue);

		szSec = Long.toString(lnRemain);

		szResult = szDay + "." + makeLen(szHour, 2, '0') + ":" + makeLen(szMin, 2, '0') + ":" + makeLen(szSec, 2, '0');

		return szResult;

	}

	/**
	 * 문자열 길이를 맞춘다 (5 -> 0005)
	 *
	 * @param szVictim
	 *			String : 길이를 맞출 원본 문자열
	 * @param iLength
	 *			int : 몇자리 길이로 만들 것인가?
	 * @param cMarker
	 *			char : 빈 자리를 채울 문자
	 * @return 만들어진 문자열
	 *		 -
	 */
	public static String makeLen(String szVictim, int iLength, char cMarker) {

		String szResult = new String("");
		int iAddLen = 0;

		if (szVictim.length() < iLength) {

			iAddLen = iLength - szVictim.length();

			for (int iIdx = 0; iIdx < iAddLen; iIdx++) {
				szResult += cMarker;
			}

			szResult += szVictim;

		} else {

			szResult = szVictim;

		}

		return szResult;
	}

	/**
	 * 년/월 을 인자로 넘겨 해당 년/월의 마지막 날짜를 리턴한다.
	 *
	 * @param iYear
	 * @param iMonth
	 * @return 해당 월의 마지막 날짜
	 */
	public static int getLastDay(int iYear, int iMonth) {

		int iDay = 0;

		try {
			oCal.set(Calendar.YEAR, iYear);
			oCal.set(Calendar.MONTH, iMonth - 1);
			iDay = oCal.getActualMaximum(Calendar.DAY_OF_MONTH);
		} catch (Exception e) {
			iDay = 30;
		}

		return iDay;
	}

	public static String endofDay() {
		return endofDay(BaseConstants.DEFAULT_DATE_FORMAT);
	}

	/**
	 * Last day of month
	 * <pre>
	 *
	 * </pre>
	 * @param strDate
	 * @param strFormat
	 * @return
	 */
	public static String endofDay(String strFormat) {

        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);

        Date lastDayOfMonth = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);

		return sdf.format(lastDayOfMonth);
	}


	/**
	 * End of day
	 * <pre>
	 *
	 * </pre>
	 * @param strFormat String
	 * @return boolean
	 */
	public static boolean isEndofDay(String strFormat) {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);

		String strToday = sdf.format(today);

		return isEndofDay(strToday, strFormat);
	}

	/**
	 * End of day
	 * <pre>
	 *
	 * </pre>
	 * @param strToday String
	 * @param strFormat String
	 * @return boolean
	 */
	public static boolean isEndofDay(String strToday, String strFormat) {

		String endDay = endofDay(strFormat);

		return strToday.equals(endDay) == true ? true : false;
	}


	public static String getCurrDateTime() {
		Calendar cal = Calendar.getInstance();
		String strTmp = cal.get(Calendar.YEAR) + StringUtil.lpad(cal.get(Calendar.MONTH) + 1, 2) + StringUtil.lpad(cal.get(Calendar.DATE), 2) +
				StringUtil.lpad(cal.get(Calendar.HOUR_OF_DAY), 2) + StringUtil.lpad(cal.get(Calendar.MINUTE), 2) + StringUtil.lpad(cal.get(Calendar.SECOND), 2);

		return strTmp;
	}

	public static String getCurrDateTimeTmp() {
		Calendar cal = Calendar.getInstance();
		String strTmp = StringUtil.lpad(cal.get(Calendar.MONTH), 2) + StringUtil.lpad(cal.get(Calendar.DATE) + 1, 2) +
				StringUtil.lpad(cal.get(Calendar.HOUR_OF_DAY), 2) + StringUtil.lpad(cal.get(Calendar.MINUTE), 2) + StringUtil.lpad(cal.get(Calendar.SECOND), 2);

		return strTmp;
	}


//	public static void main(String args[]) {
//		System.out.println(isEndofDay("yyyy-MM-dd"));
//		System.out.println(isEndofDay("2017-09-30", "yyyy-MM-dd"));
//
//	}
}
