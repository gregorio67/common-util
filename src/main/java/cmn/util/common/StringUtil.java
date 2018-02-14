package cmn.util.common;


import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StringUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(StringUtil.class);
    public StringUtil(){}


	/**
	 * limit
	 *
	 * @param str
	 * @param limit
	 * @return 변환된 문자열
	 * @exception 	Exception
	 */
	public static String shortCutString(String str, int limit) throws Exception	{

      try{

      	if (str == null || limit < 4) return str;

        int len = str.length();
        int cnt=0, index=0;

        while (index < len && cnt < limit)
        {
            if (str.charAt(index++) < 256) {
                cnt++;
            } else {
                cnt += 2;
            }
        }

        if (index < len)
            str = str.substring(0, index) + "...";

      }catch( Exception e ){
          throw new Exception("[StringUtil][shortCutString]"+e.getMessage() , e );
      }

      return str;

	}

	/**
	 * @param strTarget
	 * @param strDelete
	 * @exception 	Exception
	 * @return 삭제 후 문자열
	 */
	public static String delete(String strTarget, String strDelete) throws Exception	{
		return replace(strTarget,strDelete,"");
	}



    /**
     * <PRE>
     * 숫자를 천단위로 "," 구분자를 추가하여 변환
     * </PRE>
     * @param amount
     * @return 숫자를 천단위로 변환한 결과  ex)"1,234","200,324"
     */
    public static String getAmount( String amount ) {

        String convertedAmount = "";
        if ( amount != null && amount.length() != 0 ) {

            StringBuffer buffer = new StringBuffer();
            for( int i = 0; i < amount.length(); i++ ) {
                int j = ( amount.length() - ( i + 1 ) ) % 3;

                if( i != ( amount.length() - 1 ) && j == 0 ) {
                    buffer.append( amount.charAt( i ) );
                    buffer.append( "," );
                } else {
                    buffer.append( amount.charAt( i ) );
                }
            }
            convertedAmount = buffer.toString();
        }

        return convertedAmount;
    }

    /**
     * <PRE>
     * 입력 스트링을 검사하여 null 이면 "" 인 스트링을 반환
     * </PRE>
     * @param test String
     * @return 입력 스트링이 null이면 공백문자열을 반환
     */
    public static String null2EmptyString( String test ) {

    	return (test==null ? "" : test );
    }

    /**
     * 전달받은 숫자를 지정된 형태로 출력한다.
     * 숫자가 아닌 값이 들어오면 입력값을 그대로 돌려준다.<BR><BR>
     *
     * 사용예) getFormattedNumber(1, "00000")<BR>
     * 결 과 ) "00001"<BR><BR>
     *
     * @param pInstr long
     * @return String
     */
    public static String getFormmatedNumber( long num, String format ) {
        StringBuffer formattedNum = new StringBuffer();
        String strNum = "" + num;

        if (format == null) {
            return strNum;
        }

        try {
            for ( int i=0 ; i < format.length()-strNum.length(); i++ ) {
                formattedNum.append(format.charAt(i));
            }
            formattedNum.append(strNum);
        } catch ( Exception e ) {};

        return formattedNum.toString();
    }

    /**
     * 전달받은 숫자를 지정된 형태로 출력한다.
     * 숫자가 아닌 값이 들어오면 입력값을 그대로 돌려준다.<BR><BR>
     *
     * 사용예) getFormattedNumber(1, "00000")<BR>
     * 결 과 ) "00001"<BR><BR>
     *
     * @param pInstr long
     * @return String
     */
    public static String getFormatedNumber( int num, String format ) {
        StringBuffer formattedNum = new StringBuffer();
        String strNum = "" + num;

        if (format == null) {
            return strNum;
        }

        try {
            for ( int i=0 ; i < format.length()-strNum.length(); i++ ) {
                formattedNum.append(format.charAt(i));
            }
            formattedNum.append(strNum);
        } catch ( Exception e ) {};

        return formattedNum.toString();
    }

    public static boolean isDigit( String digitStr ) {
        if (null == digitStr || digitStr.length() < 1) {
            return false;
        }

        return digitStr.replaceAll("\\D", "").length() == digitStr.length();
    }

    /**
     * 문자열을 원하는 길이만큼 지정한 문자로 padding 처리한다.
     *
     * @param origin padding 처리할 문자열
     * @param limit padding 처리할 범위
     * @param pad padding 될 문자
     * @return padding 처리된 문자열
     */
    public static String padding(String origin, int limit, String pad) {

        String originStr = "";
        if (origin != null) {
            originStr = origin;
        }

        String padStr = "";
        if (pad != null) {
            padStr = pad;
        }

        int size = origin.length();

        if (limit <= size) {
            return originStr;

        } else {
            StringBuffer sb = new StringBuffer(originStr);

            for (int inx=size; inx < limit; inx++) {
                sb.append(padStr);
            }

            return sb.toString();
        }
    }

    /**
     * 문자열을 원하는 길이만큼 지정한 문자로 left padding 처리한다.
     *
     * @param origin padding 처리할 문자열
     * @param limit padding 처리할 size
     * @param pad padding 될 문자
     * @return padding 처리된 문자열
     */
    public static String leftPadding(String origin, int limit, String pad) {
        String temp = pad;
        if (pad == null) {
            temp = "";
        }

        String originStr = "";
        if (origin != null) {
            originStr = origin;
        }

        int size = originStr.length();

        if (limit <= size) {
            return originStr;

        } else {
            StringBuffer sb = new StringBuffer(temp);

            for (int inx = size + 1; inx < limit; inx++) {
                sb.append(temp);
            }

            return sb.append(originStr).toString();
        }
    }

    /**
     * 해당문자열 중 특정 문자열을 치환한다. <BR><BR>
     *
     * 사용예) replaceSecOutput("&<>#\'" )<BR>
     * 결 과 ) &amp;&lt;&gt;&#35;&quot;&#39;<BR><BR>
     *
     * @param pInstr String
     * @return String 치환된 문자열
     */
    public static String replaceSecOutput(String pInstr) {

        if ( pInstr == null || pInstr.equals("") ) {
            return "";
        }

        String result = pInstr;
        try {
            result = replace(result, "&", "&amp;");
            result = replace(result, "<", "&lt;");
            result = replace(result, ">", "&gt;");
            result = replace(result, "#", "&#35;");
            result = replace(result, "\"", "&quot;");
            result = replace(result, "'", "&#39;");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return pInstr;
        }

        return result;
    }

    /**
     * 해당문자열 중 특정 문자열을 치환한다. <BR><BR>
     *
     * 사용예) replaceSecOutput("&<>#\'" )<BR>
     * 결 과 ) &amp;&lt;&gt;&#35;&quot;&#39;<BR><BR>
     *
     * @param pInstr String
     * @return String 치환된 문자열
     */
    public static String replaceSecXmlKeyOutput(String pInstr) {

         if ( pInstr == null || pInstr.equals("") ) {
             return "_";
         }

         String result = pInstr;
         if (!isValidXmlKey(pInstr)) {
             result = pInstr.trim();
             result = result.substring(0, 1).replaceFirst("[^a-zA-Z_]", "_")
                      + result.substring(1).replaceAll("[^a-zA-Z0-9_.-]", "_");
         }

         return result;
    }

    /**
     * XML 엘리먼트 명으로 합당한지 여부 체크
     * 1. 엘리먼트는 전체가 문자, 숫자, 특수기호(., _, -)로만 이루어져야 한다.
     * 2. 첫 시작은 문자, _ 만이 가능하다. (공백 안 됨) 편의상 영문자로 제한을 한다.
     *
     * @param pInstr
     * @return
     */
    public static boolean isValidXmlKey(String pInstr) {
        if (pInstr == null || pInstr.trim().length() < 1) {
            return false;
        }

        return pInstr.matches("[a-zA-Z_][a-zA-Z0-9_.-]*");
    }

    /**
     * 해당문자열 중 특정 문자열을 Javascript에서 사용할수 있도록 치환한다. <BR><BR>
     *
     * 사용예) replaceJSEncodeOutput(""'" )<BR>
     * 결 과 ) %22%27<BR><BR>
     *
     * @param pInstr String
     * @return String 치환된 문자열
     */
    public static String replaceJSEncodeOutput(String pInstr) {

        if ( pInstr == null || pInstr.equals("") ) {
            return "";
        }

        String result = pInstr;

        try {
            result = replace(result, "\"", "%22");
            result = replace(result, "'", "%27");
        } catch (Exception e) {
            return pInstr;
        }

        return result;
    }

    /**
     * 영문을 한글로 Conversion해주는 Method.
 	* (8859_1 --> KSC5601)
     * @param english 한글로 바꾸어질 영문 String
     * @return 한글로 바꾸어진 String
 	*/
 	public static synchronized String koreanForPortal( String english ) {
 		String korean = null;

 		if (english == null ) {
 			return null;
 		}

 		try {
 			korean = new String(english.getBytes("8859_1"), "euc-kr");
 		} catch( UnsupportedEncodingException e ){
 			korean = new String(english);
 		}
 		return korean;
 	}

 	/**
     * 숫자 앞의 빈칸을 자릿수만큼 메꿔주는 Method.
 	 * ex) blankToString( "1", 4, "0");
 	 *     "1" -> "0001"
     * @param orig 빈칸 채우기 전의 본래 String
     * @param length 문자의 자릿수 int
     * @param add 빈칸을 채울 문자 String
     * @return 빈칸이 채워진 String
 	 */
	public static String blankToString( String orig, int length, String add ) {
		if( orig == null ){
			orig = "";
		}
		int space = length - orig.length();

		int i = 0;

		String buf = "";

		for( i = 0; i < space; i++ )

			buf += add;

		orig = buf + orig;

		return orig;

	}

	/**
     * 숫자 앞의 빈칸을 자릿수만큼 0으로 메꿔주는 Method.
 	 * ex) blankToZero( "1", 4 );
 	 *     "1" -> "0001"
     * @param orig 빈칸 채우기 전의 본래 String
     * @param length 문자의 자릿수 int
     * @return 빈칸이 채워진 String
 	 */
	public static String blankToZero( String orig, int length ) {
		String num = "";
		num = blankToString( orig, length, "0" );

		return num;

	}

	/**
	 * String 배열 객체를 toString 하는 메소드
	 * ex) [AAA,BBB,CCC]
	 *
	 * @param inAraayStr 출력할 String 배열
	 * @return toString
	 */
	public static String toArrayString(String[] inAraayStr)
	{
		String result = null;
		if ( inAraayStr != null )
		{
			StringBuffer sb = new StringBuffer();

			sb.append("[");
			for(int i=0 ; i < inAraayStr.length ; i++)
			{
				sb.append(inAraayStr[i]);
				if (i < inAraayStr.length - 1) sb.append(",");
			}
			sb.append("]");

			result = sb.toString();
		}

		return result;
	}

	/**
	 * int 배열 객체를 toString 하는 메소드
	 * ex) [AAA,BBB,CCC]
	 *
	 * @param inAraayInt 출력할 I 배열
	 * @return toString
	 */
	public static String toArrayString(int[] inAraayInt)
	{
		String result = null;
		if ( inAraayInt != null )
		{
			StringBuffer sb = new StringBuffer();

			sb.append("[");
			for(int i=0 ; i < inAraayInt.length ; i++)
			{
				sb.append( String.valueOf( inAraayInt[i] ) );
				if (i < inAraayInt.length - 1) sb.append(",");
			}
			sb.append("]");

			result = sb.toString();
		}

		return result;
	}

	public static String lpad(int iSrc, int iDigit) {
		return lpad("" + iSrc, "0", iDigit);
	}

	public static String lpad(int iSrc, String strPaddingChar, int iDigit) {
		return lpad("" + iSrc, strPaddingChar, iDigit);
	}

	public static String lpad(String strSrc, int iDigit) {
		return lpad(strSrc, "0", iDigit);
	}

	public static String lpad(String strSrc, String strPaddingChar, int iDigit) {
		String strTmp = "";
		for (int i=0; i<iDigit; i++) strTmp += strPaddingChar;
		return (strTmp + strSrc).substring((strTmp + strSrc).length() - iDigit);
	}


/**
 * <PRE>
 *
 *  사용예)
 *  Object[] objArray   =   extractBlock("1^2^3^",'^');
 *  objArray[0]     =   "1";
 *  objArray[1]     =   "2";
 *  objArray[2]     =   "3";
 *  objArray[3]     =   "";
 *
 * @param block
 * @param delimiter
 * @return Object[] 결과
 * </PRE>
 */
public static Object[] extractBlock(String block, char delimiter)
{
	List<String> v = new ArrayList<String>();
	String s = "";
	for ( int ii=0 ; ii < block.length() ; ii++) {
		char c = block.charAt(ii);

		if (c == delimiter) {v.add(s); s="";}
		else s += c;
	}
	v.add(s);
	return v.toArray();
}

/**
 * <PRE>
 *  사용예)
 *  Object[] objArray   =   extractBlock("1^2^3^",'^');
 *  objArray[0]     =   "1";
 *  objArray[1]     =   "2";
 *  objArray[2]     =   "3";
 *  objArray[3]     =   "";
 *
 * @param block
 * @param delimiter
 * @param escape
 * @param escapeOnlyDeletmeter
 * @return Object[] 결과
 * </PRE>
 */
public static Object[] extractBlock(String block, char delimiter, char escape, boolean escapeOnlyDeletmeter)
{
	List<String> v = new ArrayList<String>();
	String s = "";
	char beforeChar = ' ';
	char c = ' ';
	for ( int ii=0 ; ii < block.length() ; ii++) {
		c = block.charAt(ii);

        if (escapeOnlyDeletmeter && beforeChar == escape) {
            s += escape;
        }

        if (c == delimiter) {
			if (beforeChar == escape) {
				s += c;
			} else {
			    v.add(s);
			    s="";
			}
		} else if (c == escape) {
            if (beforeChar == escape) {
            	s += c;
            	beforeChar = ' ';
            	continue;
            }

		} else {
			s += c;
		}

		beforeChar = c;
	}
	v.add(s);
	return v.toArray();
}

/**
 * Vector 를 받아서 String[] 로 리턴한다.
 * DaoGenerator 로 생성된 dao 에서 사용됨
 *
 * @param v
 * @return Vector에서 String[]로 변환된 결과
 */
public static <V> String[] toArray(Vector<V> v) {
	String[] sa = new String[v.size()];
	v.copyInto(sa);
	return sa;
}

/**
 * Vector 를 받아서 String[] 로 리턴한다.
 * DaoGenerator 로 생성된 dao 에서 사용됨
 *
 * @param al
 * @return ArrayList에서 String[]로 변환된 결과
 */
public static <V> String[] toArray(ArrayList<V> al) {

	int size = al.size();
    String[] paramList = null;
    if (size > 0 ) {
        paramList = new String[size];
        for (int i = 0; i < size; i++) {
            paramList[i]= (String) al.get(i);
        }
    } else {
    	return new String[0];
    }
	return paramList;
}



/**
 *
 * 사용예) addTellFormat( "02", "567", "1234" )<BR>
 * 결 과 ) 02-567-1234<BR><BR>
 *
 * trustForm에서 칸칸이 따로 입력받은 node의 값을
 * DB에 넣기전 전화번호 포맷을 갖춰서 넣어준다.
 *
 * @param oneTellNo
 * @param twoTellNo
 * @param thrTellNo
 * @return String로 조합된 전화번호 결과
 */
 public static String addTellFormat( String oneTellNo, String twoTellNo, String thrTellNo ) {

     StringBuffer rStr = new StringBuffer();

     try {
    	rStr.append( oneTellNo );
    	rStr.append( "-" );
    	rStr.append( twoTellNo );
    	rStr.append( "-" );
    	rStr.append( thrTellNo );
     }
     catch ( Exception e ) {};

     return rStr.toString().trim();
	 }

 /**
  *
  * 사용예) removeTellFormat( "02-567-1234" )<BR>
  * 결 과 ) String[] removeTell = { "02", "567", "1234" }<BR><BR>
  *
  * DB에 저장된 전화번호를 "-"를 떼고 String[]으로
  * 각각 전화번호를 세등분하여 return한다.
  * @param  tellNo
  * @return String[]로 각각 분리된 전화번호 결과
  */
  public static String[] removeTellFormat( String tellNo )
  {
	  if ( tellNo == null  || tellNo.equals("")){
		  return null;
	  }
	  int first = tellNo.indexOf("-");
      String oneTellNo = tellNo.substring( 0, first ); //첫번째 칸 전화번호 ( 02 )

      String twoThrTellNo = tellNo.substring( tellNo.indexOf("-")+1 ); //두번째부터 뒤까지 전화번호(567-1234)
      int two = twoThrTellNo.indexOf("-"); //

   	  String twoTellNo = twoThrTellNo.substring( 0, two ); //두번째 칸 전화번호 (567)
   	  String threeTellNo = twoThrTellNo.substring( two + 1 );  //세번째 칸 전화번호 (1234)

   	  String[] removeTell = { oneTellNo, twoTellNo, threeTellNo };

      return removeTell;
  }

  /**
   * 특정 String 내의 일정한 pattern subString을  replace 문자열로
   *	대치한다.
   *
   * 사용예) replace("2002-02-10", "-", "/")<BR>
   * 결 과 ) "2002/02/10"<BR><BR>
   * @param  str
   * @param  pattern
   * @param  replace
   * @return 패턴이 변형된 String 결과
   */
  public static String replace(String str, String pattern, String replace) {
      int s = 0, e = 0;

		if ( str == null || str.equals("") ) return "";

      StringBuffer result = new StringBuffer();

      while ((e = str.indexOf(pattern, s)) >= 0) {
          result.append(str.substring(s, e));
          result.append(replace);
          s = e+pattern.length();
      }

      result.append(str.substring(s));
      return result.toString();
  }

  /**
   * xecure web 관련 암호화 데이터의 앞뒤에 특수문자 세팅
   * @param cipher 키 값
   * @return String
   */
   public static String makeCipherFlag(String cipher) {
    return "◐"+cipher+"◑";
   }


	 /**
     * vector에 다른 vector의 내용을 더해서 반환함.
     * @param v1 원본 vector.
     * @param v2 <code>v1</code>에 아이템을 더하고자 하는 벡터.
     * @return <code>v1</code>
     */
  public static <V> Vector<V> appendVector(Vector<V> v1, Vector<V> v2){
        if((v2 == null) || (v2.size() == 0))
            return(v1);
        Enumeration<V> e = v2.elements();
        while(e.hasMoreElements())
            v1.addElement(e.nextElement());
        return(v1);
  }

  /**
   * Float를 String으로 변환.
   * @param Float에서 String으로 변환된 문자열
   * @return String
   */
  public static String toString(Float f) {
      DecimalFormat df = new DecimalFormat("#0.0#################");
      return df.format(f.floatValue());
  }
  /**
   * Double를 String으로 변환.
   * @param Double에서 String으로 변환된 문자열
   * @return String
   */
  public static String toString(Double d) {
      DecimalFormat df = new DecimalFormat("#0.0#################");
      return df.format(d.doubleValue());
  }

  /**
   * Object를 String으로 변환.
   * @param Object에서 String으로 변환된 문자열
   * @return String
   */
  public static String toString(Object o) {
		return o == null ? "" : o.toString();
  }
}
