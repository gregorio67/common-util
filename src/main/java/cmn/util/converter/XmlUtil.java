package cmn.util.converter;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import cmn.util.base.BaseConstants;
import cmn.util.exception.UtilException;
import cmn.util.spring.MessageUtil;

public class XmlUtil {

	/**LOGGER SET **/
	private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtil.class);

	private static final String DEFAULT_ENCODING = "UTF-8";

	private static Jaxb2Marshaller jaxb2Marshaller = null;

	XmlUtil() {
	}


	public static String object2XmlString(Object object) throws Exception {
		return object2XmlString(object, DEFAULT_ENCODING);
	}

	/**
	 * Objcet를 XML로 변환함
	 * <pre>
	 *
	 * </pre>
	 * @param object Object
	 * @param encoding String
	 * @return String
	 * @throws Exception
	 */
	public static String object2XmlString(Object object, String encoding) throws Exception {
		JAXBContext jaxbContext = null;
		String xmlDoc = "";
		Marshaller marshaller = null;
		StringWriter writer = new StringWriter();

		try {
			jaxbContext = JAXBContext.newInstance(object.getClass());
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshaller.marshal(object, writer);
			xmlDoc = writer.toString();
			writer.close();
		}
		catch(Exception e) {
			LOGGER.error(e.getMessage());
			throw new UtilException(e.getMessage(), e);
		}
		return xmlDoc;
	}

	public static void object2XmlFile(Object object, String fileName) throws Exception {
		 object2XmlFile(object, fileName, DEFAULT_ENCODING);
	}

	public static void object2XmlFile(Object object, String fileName, String encoding) throws Exception {
		JAXBContext jaxbContext = null;
		Marshaller marshaller = null;
		try {
			jaxbContext = JAXBContext.newInstance(object.getClass());
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshaller.marshal(object, new File(fileName));
		}
		catch(Exception e) {
			LOGGER.error(e.getMessage());
			throw new UtilException(e.getMessage(), e);
		}
	}


	/**
	 * XML String을 Object로 변환한다.
	 * <pre>
	 *
	 * </pre>
	 * @param xmlDoc String
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T xmlString2Object(Class<T> clazz, String xmlDoc) throws Exception {
		JAXBContext context = null;
		Unmarshaller unmarshaller = null;

		try {
			context = JAXBContext.newInstance(clazz);
			unmarshaller = context.createUnmarshaller();
			Object object = unmarshaller.unmarshal(new StringReader(xmlDoc));
			return (T)object;
		}
		catch(Exception e) {
			LOGGER.error(e.getMessage());
			throw new UtilException(e.getMessage(), e);
		}
	}


	/**
	 * XML 파일을 읽어서 Object로 변환한다.
	 * <pre>
	 *
	 * </pre>
	 * @param clazz Class<T> 변환될 Class
	 * @param fileName String XML File명
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T xmlFile2Object(Class<T> clazz, String fileName) throws Exception {
		JAXBContext context = null;
		Unmarshaller unmarshaller = null;

		try {
			context = JAXBContext.newInstance(clazz);
			unmarshaller = context.createUnmarshaller();
			Object object = unmarshaller.unmarshal(new File(fileName));
			return (T)object;
		}
		catch(Exception e) {
			LOGGER.error(e.getMessage());
			throw new UtilException(e.getMessage(), e);
		}
	}

	/**
	 * Create Object to XML
	 * This method need to jaxb.index in the vo package
	 * <pre>
	 *
	 * </pre>
	 * @param source Object
	 * @param clazz Class
	 * @return String
	 * @throws Exception
	 */
	public static <T> String marshallToXml(Object source, Class<T> clazz) throws Exception {
		return marshallToXml(source, clazz, DEFAULT_ENCODING);
	}

	public static <T> String marshallToXml(Object source, Class<T> clazz, String encoding) throws Exception {
		/**
		 * Get jaxb2Marshall instance
		 */
		getInstanceJaxb2();

		jaxb2Marshaller.setClassesToBeBound(clazz);

		/**
		 * Properties set
		 */
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put(Marshaller.JAXB_ENCODING, encoding);
		properties.put(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		//JAXB_FRAGMENT true : XML header not generate
//		properties.put(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

		jaxb2Marshaller.setMarshallerProperties(properties);

		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		jaxb2Marshaller.marshal(source, result);

		return writer.toString();
	}

	/**
	 * This method convert XML to Object
	 * <pre>
	 *
	 * </pre>
	 * @param xml String
	 * @param clazz class
	 * @return Object
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unmarshallFromXml(String xml, Class<T> clazz) throws Exception {

		/* Get Jaxb Instance */
		getInstanceJaxb2();

		jaxb2Marshaller.setContextPath(clazz.getPackage().getName());

		return (T) jaxb2Marshaller.unmarshal(new StreamSource(new StringReader(xml)));

	}


	/**
	 * Get Jaxb2Marshall instance
	 * <pre>
	 *
	 * </pre>
	 * @throws Exception
	 */
	private static void getInstanceJaxb2() throws Exception {
		if ( jaxb2Marshaller == null ) {
			synchronized(XmlUtil.class) {
				jaxb2Marshaller = new Jaxb2Marshaller();
			}
		}
	}
}
