import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import stis.framework.util.BrowserUtil;
import stis.framework.util.NullUtil;

public class ExcelDownloadView extends AbstractExcelView {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDownloadView.class);
	
	private static final String EXCEL_NUMBER_FORMAT = "#,##0.00";
	
	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		int globalIdx = 0;
		
		Map<String, Object> excelMap = (Map<String, Object>) model.get("excelMap");

		/**
		 * Sheet name 
		 */
		String sheetName = excelMap.get("sheetName") != null ?  String.valueOf(excelMap.get("sheetName")) : "Sheet1";
		HSSFSheet sheet  = workbook.createSheet(sheetName);
				
		String[] headers = (String[]) (excelMap.get("headers") != null ? excelMap.get("headers") : null);
		if (headers != null) {
			int headerSize = headers.length;
			
			/**
			 * Create Header Name
			 */
			String headerName = excelMap.get("headerName") != null ? String.valueOf(excelMap.get("headerName")) : "";
			if (!NullUtil.isNull(headerName)) {
				HSSFCell cell = getCell(sheet, globalIdx, 0);
				cell.setCellValue(headerName);
				
				Font font = workbook.createFont();
				font.setBold(true);
				
				HSSFCellStyle style = workbook.createCellStyle();
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setAlignment(HorizontalAlignment.CENTER);
				style.setFont(font);
				cell.setCellStyle(style);
				
				/** Cell Merge **/
				sheet.addMergedRegion(new CellRangeAddress(globalIdx, globalIdx, 0, headerSize - 1));
				
				globalIdx++;
			}
			/**
			 * If header is exists
			 */
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("header {} ", Arrays.toString(headers));				
			}
			int cellIdx = 0;
			HSSFCellStyle headerStyle = getHeaderStyle(workbook);
				
			for (String header : headers) {
				HSSFCell cell = getCell(sheet, globalIdx, cellIdx++);
				cell.setCellStyle(headerStyle);
				cell.setCellValue(header);;
			}
		}

		globalIdx++;

		/**
		 * Data처리
		 */
		List<?> data = (List<?>)excelMap.get("data");
		if (data != null) {
			
			int size = data.size();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("list size {} ", size);				
			}
			
			CellStyle dataCellStyle = getDataStyle(workbook);
			for (int i = 0; i < size; i++) {
				
				Object obj = data.get(i);
				/**
				 * Map Data를 Excel로 처리
				 */
				if (obj instanceof Map) {
					map2Excel(sheet, (Map<String, Object>)obj, dataCellStyle, globalIdx++);
				}
				else {
					valueObject2Excel(sheet, obj, dataCellStyle,  globalIdx++);
				}
			}
		}
		String fileName = excelMap.get("fileName") != null ? String.valueOf(excelMap.get("fileName")) : "UNKNOWN.xls";
		BrowserUtil.setDisposition(fileName, request, response);
	}
	
	/**
	 * 
	 * <pre>
	 *
	 * </pre>
	 *
	 * @param sheet
	 * @param map
	 * @param idx
	 * @throws Exception
	 */
	private <U extends Map<String, Object>> void map2Excel(HSSFSheet sheet, U map, CellStyle cellStyle,  int idx) throws Exception {
		
		Iterator<String> keys = map.keySet().iterator();
		String key = null;
		int cellIdx = 0;
	
		while(keys.hasNext()) {
			key = keys.next();
			HSSFCell cell = getCell( sheet, idx, cellIdx++);
			sheet.autoSizeColumn(cellIdx);
			
			if (map.get(key) instanceof Integer) {
				int value = map.get(key) != null ? Integer.parseInt(String.valueOf(map.get(key))) : 0;

				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(EXCEL_NUMBER_FORMAT));
				cell.setCellStyle(cellStyle);
				cell.setCellValue(value);
			}
			else if (map.get(key) instanceof Long) {
				long value = map.get(key) != null ? Long.parseLong(String.valueOf(map.get(key))) : 0L;

				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(EXCEL_NUMBER_FORMAT));
				cell.setCellStyle(cellStyle);
				cell.setCellValue(value);
			}
			else if (map.get(key) instanceof Float) {
				float value = map.get(key) != null ? Float.parseFloat(String.valueOf(map.get(key))) : 0L;

				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(EXCEL_NUMBER_FORMAT));
				cell.setCellStyle(cellStyle);
				cell.setCellValue(value);
			}
			else if (map.get(key) instanceof Double || map.get(key) instanceof BigDecimal) {
				double value = map.get(key) != null ? Double.parseDouble(String.valueOf(map.get(key))) : 0L;

				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(EXCEL_NUMBER_FORMAT));
				cell.setCellStyle(cellStyle);
				cell.setCellValue(value);
			}
			else {
				String value = map.get(key) != null ? String.valueOf(map.get(key)) : "";
				
				cell.setCellStyle(cellStyle);
				cell.setCellValue(value);				
			}
		}
	}
	

	/**
	 * Value Object to Excel Data
	 * <pre>
	 *
	 * </pre>
	 *
	 * @param sheet
	 * @param object
	 * @param idx
	 * @throws Exception
	 */
	private <U extends Object> void valueObject2Excel(HSSFSheet sheet, U object, CellStyle cellStyle, int idx) throws Exception {
		
		/**
		 * Row 생성
		 */
		Field[] fields = object.getClass().getDeclaredFields();	
		String value = null;
		int cellIdx = 0;
		int fieldLength =  fields.length;
		for(int k=0; k<= fieldLength -1; k++){ 
			/**
			 * UID skip
			 */
			if (!"uuid".equals(fields[k].getName())) {
				HSSFCell cell = getCell( sheet, idx, cellIdx++);
				cell.setCellStyle(cellStyle);

				fields[k].setAccessible(true);
				value = String.valueOf(fields[k].get(object));

				if ("null".equalsIgnoreCase(value)) {
					cell.setCellValue("");									
				}
				else {
					cell.setCellValue(value);				
				}
			}
		}
	}
	
	/**
	 * Excel Header style
	 *<pre>
	 *
	 *</pre>
	 * @param workbook
	 * @return
	 * @throws Exception
	 */
	public HSSFCellStyle getHeaderStyle(HSSFWorkbook workbook) throws Exception {

		HSSFCellStyle cellStyle = workbook.createCellStyle();
		
		/** Set border style **/
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		
		/** set Alignment **/
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		
		/** set color **/
		cellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		cellStyle.setFillBackgroundColor(IndexedColors.BLUE.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		/** set font **/
		Font font = workbook.createFont();
		font.setBold(true);
		font.setColor(IndexedColors.WHITE.getIndex());
		
		cellStyle.setFont(font);
		
		return cellStyle;
	}
	
	public HSSFCellStyle getDataStyle(HSSFWorkbook workbook) throws Exception {
		
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		
		/** Set border style **/
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		
		/** set Alignment **/
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		
		return cellStyle;
	}	

}
