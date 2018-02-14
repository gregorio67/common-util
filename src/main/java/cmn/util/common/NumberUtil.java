package cmn.util.common;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;



public class NumberUtil {


    /**
     * @param value
     * @param delim
     * @param idx
     * @return String
     */
    public static String token(String value, String delim, int idx) {
        if (value == null)
            return null;
        StringTokenizer st = new StringTokenizer(value, delim);
        int i   = 0;
        while (st.hasMoreTokens()) {
            if ((i++) == idx)
                return st.nextToken();
            else
                st.nextToken();
        }
        return null;
    }



    /**
     * 하나의 문자열을 기준 문자열을 준으로 두개로 분리한다.
     * @param target
     * @param cut_str
     * @return String[]
     */
    public static String[] divide(String target, String cut_str) {
        if (target == null)
            return new String[] { "", "" };
        if (cut_str == null || cut_str.length() == 0)
            return new String[] { target, "" };
        int idx = target.indexOf(cut_str);
        if (idx < 0)
            return new String[] { target, "" };
        else
            return new String[] { target.substring(0, idx), target.substring(idx + cut_str.length())};
    }
    /**
    * Double은 바로 BigDecimal의 생성자의 파라미터로 사용하면 소수점이하의 변형이 발생하여
    * String으로 변환한 후에 생성자의 파라미터로 사용한다.
    * @param value
    * @param scale
    * @return Float 형의 올림된 결과
    */
    public static Float roundup(Float value, int scale) {
        if (value == null)
            return null;
        BigDecimal b = new BigDecimal(StringUtil.toString(value));
        return new Float(b.setScale(scale, BigDecimal.ROUND_UP).toString());
    }
    /**
     * 숫자에 대한 내림
     * @param value
     * @param scale
     * @return Float형의 내림된 결과
     */
    public static Float rounddown(Float value, int scale) {
        if (value == null)
            return null;
        BigDecimal b = new BigDecimal(StringUtil.toString(value));
        return new Float(b.setScale(scale, BigDecimal.ROUND_DOWN).toString());
    }
    /**
     * 반올림을 수행한다.
     * @param value
     * @param scale
     * @return Float형의 반올림된 결과
     */
    public static Float roundhalfup(Float value, int scale) {
        if (value == null)
            return null;
        BigDecimal b = new BigDecimal(StringUtil.toString(value));
        return new Float(b.setScale(scale, BigDecimal.ROUND_HALF_UP).toString());
    }
    /**
     * 숫자에 대한 올림
     * @param value
     * @param scale
     * @return Double형의 올림된 결과
     */
    public static Double roundup(Double value, int scale) {
        if (value == null)
            return null;
        BigDecimal b = new BigDecimal(StringUtil.toString(value));
        return new Double(b.setScale(scale, BigDecimal.ROUND_UP).toString());
    }
    /**
     * 숫자에 대한 내림
     * @param value
     * @param scale
     * @return  Double형의 내림된 결과
     */
    public static Double rounddown(Double value, int scale) {
        if (value == null)
            return null;
        BigDecimal b = new BigDecimal(StringUtil.toString(value));
        return new Double(b.setScale(scale, BigDecimal.ROUND_DOWN).toString());
    }
    /**
     * 숫자에 대한 반올림.
     * @param value
     * @param scale
     * @return Double형의 반올림된 결과
     */
    public static Double roundhalfup(Double value, int scale) {
        if (value == null)
            return null;
        BigDecimal b = new BigDecimal(StringUtil.toString(value));
        return new Double(b.setScale(scale, BigDecimal.ROUND_HALF_UP).toString());
    }


    /**
     * @param data
     * @return 인코딩된 문자열
     * @throws IOException
     */
    public static String encode(Map<String, Object> data) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bo);
        os.writeObject(data);
        return Base64.encodeBase64String(bo.toByteArray());
    }

    /**
     * @param text
     * @return 디코딩된 문자열
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object decode(String text) throws IOException, ClassNotFoundException {
        byte o[] = Base64.decodeBase64(text);
        ByteArrayInputStream bi = new ByteArrayInputStream(o);
        ObjectInputStream os = new ObjectInputStream(bi);
        Object object = os.readObject();
        return  object;
    }

}
