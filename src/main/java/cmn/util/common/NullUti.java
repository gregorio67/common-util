package cmn.util.common;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NullUtil {

    /**
     * 입력값이 널인지 여부를 검사한다.
     * 단 기본은 ""과 null을 모두 true로 리턴한다.
     * 모든 DB Access메소드는 null체크가 필요한 경우에 이것을 이용한다.
     *
     * @param value
     * @return boolean
     * @throws FrmException
     */
    public static boolean isNull(String value) {
        //return value==null  ;
        return value == null || "".equals(value);
    }


    /**
     * 입력값이 널인지 여부를 검사한다.
     * 단 기본은 ""과 null을 모두 true로 리턴한다.
     * 모든 DB Access메소드는 null체크가 필요한 경우에 이것을 이용한다.
     *
     * @param String[] value
     * @return boolean
     */

    public static boolean isNull(String[] value) {
        if ((value == null) || (value.length < 1)) {
            return true;
        }

        for (int i = 0; i < value.length; i++) {
            if (isNull(value[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * 입력값이 널인지 여부를 검사한다.
     * 단 기본은 ""과 null을 모두 true로 리턴한다.
     * 모든 DB Access메소드는 null체크가 필요한 경우에 이것을 이용한다.
     *
     * @param value
     * @return boolean
     */
    public static boolean isNull(Object value) {
        return value == null;
    }

    /**
     * 입력값이 널인지 여부를 검사한다.
     * 단 기본은 ""과 null을 모두 true로 리턴한다.
     * 모든 DB Access메소드는 null체크가 필요한 경우에 이것을 이용한다.
     *
     * @param List<?> value
     * @return boolean
     */
    public static boolean isNull(List<?> value) {
        if ((value == null) || (value.size() < 1)) {
            return true;
        }

        final int size = value.size();
        for (int i = 0; i < size; i++) {
        	// if (isNull((String) value.get(i))) { syc.mod
            if (isNull((Object) value.get(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param value
     * @return boolean
     */
    public static boolean isNone(String value) {
        return (value == null || value.length() == 0);
    }

    /**
     * @param value
     * @return boolean
     */
    public static boolean isNone(Number value) {
        return (value == null || value.doubleValue() == 0);
    }

    /**
     * @param value
     * @return boolean
     */
    public static boolean isNone(List<?> value) {
       return (value == null || value.size() == 0);
    }

    /**
     * @param value
     * @return boolean
     */
    public static boolean isNone(Object[] value) {
       return (value == null || value.length == 0);
    }

    /**
     * @param <K>
     * @param value
     * @return boolean
     */
    public static <K, V> boolean isNone(Map<K, V> value) {
       return (value == null || value.size() == 0);
    }


    public static boolean notNone(String value)
	{
	    return ((value != null) && (value.length() > 0));
	}


	  public static String nvl(String originalStr, String defaultStr)
	  {
	    if ((originalStr == null) || (originalStr.length() < 1)) return defaultStr;
	    return originalStr;
	  }

	  public static String nvl(Object object, String defaultValue)
	  {
	    if (object == null) return defaultValue;
	    return nvl(object.toString(), defaultValue);
	  }

	  public static String print(Object o)
	  {
	    if (o == null) return "";
	    return o.toString();
	  }

		@SuppressWarnings("rawtypes")
		public static Map<String, Object> nullToEmptyString(Map<String, Object> map) {
			if (map != null) {
				Set set = map.entrySet();
				Iterator it = set.iterator();

				while (it.hasNext()) {
					Map.Entry e = (Map.Entry) it.next();
					map.put((String) e.getKey(), e.getValue() == null ? "" : e.getValue());
				}
			}

			return map;
		}

		public static List<Map<String, Object>> nullToEmptyString(List<Map<String, Object>> list) {
			if (list != null) {
				for (Map<String, Object> map : list) {
					map = nullToEmptyString(map);
				}
			}
			return list;
		}

		public static Object nullToEmptyString(Object obj) {
			return obj == null ? "" : obj;
		}
}
