package cmn.util.file;

import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cmn.util.spring.PropertiesUtil;


public class FileUploadSpec  {

	private static Logger LOGGER = LoggerFactory.getLogger(FileUploadSpec.class);

	private static Map<String, Object> specMap = null;

	private static Pattern allowPattern = null;

	private static Pattern denyPattern = null;

	public void init(Map<String, Object> map) throws Exception {
		specMap = map;

		String[] allows = String.valueOf(specMap.get("allowExtension")).split(",");
		String[] denies = String.valueOf(specMap.get("denyExtension")).split(",");

		allowPattern(allows);
		denyPattern(denies);
	}

	public boolean isAllowed(String fileName) throws Exception {
		String extension = FileUtil.getFileExtension(fileName);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("File Name :: {}, Extension :: {}", fileName, extension);
		}
		return extension == null ? true : allowPattern.matcher(extension).find();
	}

	public boolean isDenied(String fileName) throws Exception {
		String extension = FileUtil.getFileExtension(fileName);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("File Name :: {}, Extension :: {}", fileName, extension);
		}
		return extension == null ? false : denyPattern.matcher(extension).find();
	}

	public boolean isFileSizeExceed(long size) throws Exception {
		long specSize = (specMap.get("totaFileSize") != null ? Long.parseLong((String)specMap.get("maxFileSize")) : 0);

		return specSize > size;
	}

	public boolean isTotalFileSizeExceed(long size) throws Exception {

		long specSize = (specMap.get("totaFileSize") != null ? Long.parseLong((String)specMap.get("totaFileSize")) : 0);

		return specSize > size;
	}

	public boolean isZeroAllow() throws Exception {
		return Boolean.getBoolean((String)specMap.get("zeroSizeAllow"));
	}

	public boolean isDupFileDelete() throws Exception {
		return Boolean.getBoolean((String)specMap.get("deleteDuplicateFile"));
	}

	public String getUploadDir() throws Exception {
		return (specMap.get("uploadPath") != null ? (String)specMap.get("deleteDuplicateFile") : PropertiesUtil.getString("default.file.upload.dir"));
	}

	private void allowPattern(String[] patterns) throws Exception {
		int idx = 0;
		StringBuilder sb = new StringBuilder("([^\\s]+(\\.(?i)(");
		for (String str : patterns) {
			if (idx > 0) {
				sb.append("|").append(str);
			}
			else {
				sb.append(str);
			}
			idx++;
		}
		sb.append("))$)");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("allow pattern :: {}" , sb.toString());
		}
		allowPattern = Pattern.compile(sb.toString());
	}

	public String getDuplicatedFilePostfix() throws Exception {

		return specMap.get("duplicatedFilePostfix") != null ? (String)specMap.get("duplicatedFilePostfix") : null;
	}
	private void denyPattern(String[] patterns) throws Exception {
		StringBuilder sb = new StringBuilder("([^\\s]+(\\.(?i)(");
		int idx = 0;
		for (String str : patterns) {
			if (idx > 0) {
				sb.append("|").append(str);
			}
			else {
				sb.append(str);
			}
			idx++;
		}
		sb.append("))$)");

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("deny pattern :: {}" , sb.toString());
		}
		denyPattern = Pattern.compile(sb.toString());
	}
}
