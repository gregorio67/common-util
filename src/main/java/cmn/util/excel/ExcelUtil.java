import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import stis.framework.exception.BizException;
import stis.framework.vo.TestVo;

public class ExcelUtil {
	
	/** Logger **/
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);
	
	private static final String EXCEL_NUMBER_FORMAT = "#,##0.00";
	
	private static final int EXCEL_MEMORY_KEEP_SIZE = 300;
	
	private static final String EXCEL_DEFAULT_SHEET_NAME = "Sheet";
	
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Read excel file
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param excelFileName excel file name
	 * @return List<Map<String, Object>>
	 * @throws Exception
	 */
	public static List<Map<String, Object>> readExcel(String excelFileName) throws Exception {
		return readExcel(excelFileName, null, 0, null);
	}

	public static List<Map<String, Object>> readExcel(String excelFileName, int index) throws Exception {
		return readExcel(excelFileName, null, index, null);
	}

	/**              
	 * 
	 *<pre>
	 * 1.Description: Read encrypted excel file
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param excelFileName excel file name
	 * @param password excel file password
	 * @return List<Map<String, Object>>
	 * @throws Exception
	 */
	public static List<Map<String, Object>> readExcel(String excelFileName, String password) throws Exception {
		return readExcel(excelFileName, password, 0, null);
	}

	public static List<Map<String, Object>> readExcel(String excelFileName, String password, int index) throws Exception {
		return readExcel(excelFileName, password, index, null);
	}
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Read excel file without header data in excel file
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param excelFileName excel file name
	 * @param cellNames cell name for creating map
	 * @return List<Map<String, Object>>
	 * @throws Exception
	 */
	public static List<Map<String, Object>> readExcel(String excelFileName, List<String> cellNames) throws Exception {
		return readExcel(excelFileName, null, 0, cellNames);
	}
	
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Read excel data from excel file
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param excelFileName excel file name
	 * @param password password to protect excel file
	 * @param index sheet index
	 * @param cellNames excel header 
	 * @return All date from read excel file List<Map<String, Object>>
	 * @throws Exception
	 */
	public static List<Map<String, Object>> readExcel(String excelFileName, String password, int index, List<String> cellNames) throws Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		Workbook workbook = null;
		
		try {
			workbook = getWorkbook(excelFileName, password);
			int numSheet = workbook.getNumberOfSheets();
			
			Sheet sheet = workbook.getSheetAt(index);
			Iterator<Row> rowItr = sheet.iterator();
			while(rowItr.hasNext()) {
				Row curRow = rowItr.next();
				if (NullUtil.isNull(cellNames)) {
					cellNames = getHeaderCell(curRow);
					continue;
				}
				int maxCell = curRow.getLastCellNum();
				
				/** Check excel max cell is equal to header cell **/ 
				if (cellNames.size() != maxCell) {
					LOGGER.error("The input cell name is not match exel max cell ");
					throw new BizException("The input cell name is not match exel max cell ");
				}
				
				resultList.add(getRowData(curRow, cellNames));
			}				
			
		}
		catch (Exception ex) {
			LOGGER.error("{} excel file processing error :: {} \n {}", excelFileName, ex.getMessage());			
			throw new BizException("{} excel file processing error", excelFileName);
		}
		finally {
			if (workbook != null) workbook.close();
		}
		
		return resultList;
	}
	
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Read header from excel
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param row Excel Header Row
	 * @return List<String> excel header data
	 * @throws Exception
	 */
	public static List<String> getHeaderCell(Row row) throws Exception {
		List<String> headerCell = new LinkedList<String>();
		Iterator<Cell> cellItr = row.iterator();
		while(cellItr.hasNext()) {
			Cell cell = cellItr.next();
			headerCell.add(cell.getStringCellValue());
		}
		
		return headerCell;
	}
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Create Excel HSSFWorkbook, If OfficeXmlFileException is occurred, create XSSFWorkbook
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param excelFileName excel file name
	 * @return Excel Workbook
	 * @throws Exception
	 */
	public static Workbook getWorkbook(String excelFileName)  throws Exception {
		return getWorkbook(excelFileName, null);
	}
	
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Get Excel workbook from excel file name
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param excelFileName excel file name
	 * @param password password if excel file is protected with password
	 * @return Excel workbook
	 * @throws Exception
	 */
	public static Workbook getWorkbook(String excelFileName, String password)  throws Exception {
		FileInputStream excelFile = null;
		Workbook workbook = null;
		try {
			excelFile = new FileInputStream(new File(excelFileName));
			
			/** This supports XSSFWorkbook and HSSFWorkbook **/
			if (NullUtil.isNull(password)) {
				workbook = WorkbookFactory.create(excelFile);
			}
			/** Excel file is locked with password **/
			else {
				workbook = WorkbookFactory.create(excelFile, password);				
			}
		}
		catch(Exception ex) {
			LOGGER.error("Excel File read Error {} \n {}", excelFileName, ex.getMessage());
			throw new BizException("Excel File read Error");
		}
		finally {
			if (excelFile != null) 	excelFile.close();
		}

		return workbook;
	}
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Create new excel workbook
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param excelType excel file type xls, xlxs
	 * @return excel workbook
	 * @throws Exception
	 */
	public static Workbook createWorkbook(ExcelType excelType) throws Exception {
		if (excelType == ExcelType.HSSF) {
			HSSFWorkbook workbook = new HSSFWorkbook();
			return workbook;
		}
		else if (excelType == ExcelType.SXSSF){
			
			SXSSFWorkbook  workbook = new SXSSFWorkbook(EXCEL_MEMORY_KEEP_SIZE);
			
			/**  Excel temp file is gzipped **/
			workbook.setCompressTempFiles(true);
			return workbook;
		}
		else {
			XSSFWorkbook workbook = new XSSFWorkbook();
			return workbook;
		}
	}
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Return excel sheet
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param workbook Excel workbook
	 * @param idx sheet index
	 * @return Sheet
	 * @throws Exception
	 */
	public static Sheet getSheet(Workbook workbook, int idx) throws Exception {
		Sheet sheet = workbook.getSheetAt(idx);
		return sheet;
	}
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Create excel new sheet
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param workbook excel workbook
	 * @return excel sheet
	 * @throws Exception
	 */
	public static Sheet createSheet(Workbook workbook) throws Exception {
		return createSheet(workbook, EXCEL_DEFAULT_SHEET_NAME);
	}
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Create excel new sheet with sheet name
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param workbook excel workbook
	 * @param sheetName excel sheet name
	 * @return excel sheet
	 * @throws Exception
	 */
	public static Sheet createSheet(Workbook workbook, String sheetName) throws Exception {
		return workbook.createSheet(sheetName);
	}

	/**
	 * 
	 *<pre>
	 * 1.Description: Read row data and return Map
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param row Excel row
	 * @param cells header name
	 * @return Map key is cells and value is excel data
	 * @throws Exception
	 */
	public static Map<String, Object> getRowData(Row row, List<String> cells) throws Exception {
		Map<String, Object> rowMap = new LinkedHashMap<String, Object>();
		Iterator<Cell> cellItr = row.cellIterator();
		
		int idx = 0;

		while(cellItr.hasNext()) {
			Cell curCell = cellItr.next();
			if (curCell.getCellTypeEnum() == CellType.NUMERIC) {
				rowMap.put(cells.get(idx++), curCell.getNumericCellValue());	
			}
			else if (curCell.getCellTypeEnum() == CellType.BOOLEAN) {
				rowMap.put(cells.get(idx++), curCell.getBooleanCellValue());						
			}
			else {
				rowMap.put(cells.get(idx++), curCell.getStringCellValue() != null ? curCell.getStringCellValue() : "");											
			}
		}
		return rowMap;
	}
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Create excel row with row data
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param sheet excel sheet
	 * @param map excel row data for creating excel row
	 * @param cellStyle  excel cell style for row
	 * @param idx excel row index
	 * @throws Exception
	 */
	public static <U extends Map<String, Object>> void map2Excel(Sheet sheet, U map, CellStyle cellStyle,  int idx) throws Exception {
		
		Iterator<String> keys = map.keySet().iterator();
		String key = null;
		int cellIdx = 0;
	
		Row row = sheet.createRow(idx);
		
		while(keys.hasNext()) {
			key = keys.next();
			/** Create new excel cell **/
			Cell cell = row.createCell(cellIdx++);
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
	 * 
	 *<pre>
	 * 1.Description: Create excel row with value object
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param sheet excel sheet
	 * @param clazz value object
	 * @param cellStyle excel cell style
	 * @param idx row index
	 * @throws Exception
	 */
	public static <U> void object2Excel(Sheet sheet, U clazz, CellStyle cellStyle,  int idx) throws Exception {
		
		int cellIdx = 0;
		
		/** Create new excel row **/
		Row row = sheet.createRow(idx);
		
		/** Get all fields from value object **/
		Field[] fields = FieldUtils.getAllFields(clazz.getClass());
		
		for(Field field : fields) {
			
			/** Get field value from value object **/
			Object object = FieldUtils.readField(clazz, field.getName(), true);
			
			/** Create new excel cell **/
			Cell cell = row.createCell(cellIdx++);
			sheet.autoSizeColumn(cellIdx);
			
			if (object instanceof Integer) {
				int value = object != null ? Integer.parseInt(String.valueOf(object)) : 0;

				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(EXCEL_NUMBER_FORMAT));
				cell.setCellStyle(cellStyle);
				cell.setCellValue(value);
			}
			else if (object instanceof Long) {
				long value = object != null ? Long.parseLong(String.valueOf(object)) : 0L;

				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(EXCEL_NUMBER_FORMAT));
				cell.setCellStyle(cellStyle);
				cell.setCellValue(value);
			}
			else if (object instanceof Float) {
				float value = object != null ? Float.parseFloat(String.valueOf(object)) : 0L;

				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(EXCEL_NUMBER_FORMAT));
				cell.setCellStyle(cellStyle);
				cell.setCellValue(value);
			}
			else if (object instanceof Double || object instanceof BigDecimal) {
				double value = object != null ? Double.parseDouble(String.valueOf(object)) : 0L;

				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(EXCEL_NUMBER_FORMAT));
				cell.setCellStyle(cellStyle);
				cell.setCellValue(value);
			}
			else {
				String value =object != null ? String.valueOf(object) : "";
				
				cell.setCellStyle(cellStyle);
				cell.setCellValue(value);				
			}
			
		}
	}
	
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Create excel row with value object
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param sheet excel sheet
	 * @param clazz value object
	 * @param cellStyle excel cell style
	 * @param idx row index
	 * @param fieldNames value object filed names
	 * @throws Exception
	 */
	public static <U> void object2Excel(Sheet sheet, U clazz, CellStyle cellStyle,  int idx, List<String> fieldNames) throws Exception {
		
		int cellIdx = 0;
		
		/** Create new excel row **/
		Row row = sheet.createRow(idx);
		
		for(String fieldName : fieldNames) {
			
			/** Get value from filed **/
			Object object = FieldUtils.readField(clazz, fieldName, true);
			
			/** Create new excel cell **/
			Cell cell = row.getCell(cellIdx++);
			sheet.autoSizeColumn(cellIdx);
			
			if (object instanceof Integer) {
				int value = object != null ? Integer.parseInt(String.valueOf(object)) : 0;

				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(EXCEL_NUMBER_FORMAT));
				cell.setCellStyle(cellStyle);
				cell.setCellValue(value);
			}
			else if (object instanceof Long) {
				long value = object != null ? Long.parseLong(String.valueOf(object)) : 0L;

				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(EXCEL_NUMBER_FORMAT));
				cell.setCellStyle(cellStyle);
				cell.setCellValue(value);
			}
			else if (object instanceof Float) {
				float value = object != null ? Float.parseFloat(String.valueOf(object)) : 0L;

				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(EXCEL_NUMBER_FORMAT));
				cell.setCellStyle(cellStyle);
				cell.setCellValue(value);
			}
			else if (object instanceof Double || object instanceof BigDecimal) {
				double value = object != null ? Double.parseDouble(String.valueOf(object)) : 0L;

				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(EXCEL_NUMBER_FORMAT));
				cell.setCellStyle(cellStyle);
				cell.setCellValue(value);
			}
			else {
				String value =object != null ? String.valueOf(object) : "";
				
				cell.setCellStyle(cellStyle);
				cell.setCellValue(value);				
			}
			
		}
	}

	enum ExcelType {
	    HSSF, XSSF, SXSSF
	}
	
	
	public static void main(String[] args) throws Exception {
//		List<Map<String, Object>> result = readExcel("D:/temp/sehati_instance_data.xls", "lgcns2018!");
//		LOGGER.debug("Result1 :: {}", result);
//
//		result = readExcel("D:/temp/sehati_instance_data.xlsx");
//		
//		LOGGER.debug("Result2 :: {}", result);
		
		final TestVo vo = new TestVo();
		vo.setAge(10);
		vo.setName("kkimdoy");
		vo.setPassword("aaaaa");

		FieldUtils.writeField(vo, "age", 20, true);

		List<String> cell = new ArrayList<String>();
		cell.add("name");
		cell.add("age");
		cell.add("password");
		Field[] fields = FieldUtils.getAllFields(vo.getClass());
		
		for (Field field : fields) {
			String name =field.getName();
			Object object = FieldUtils.readField(vo, name, true);
			
			LOGGER.debug("{} :: {}", name, object);
		}
		
		Field field = ReflectionUtils.findField(vo.getClass(), "age");
		ReflectionUtil.makeAccessible(field);
		ReflectionUtils.setField(field, vo, 30);
		
		ReflectionUtils.doWithFields(vo.getClass(), new FieldCallback() {

			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				ReflectionUtil.makeAccessible(field);
				Object value = ReflectionUtils.getField(field, vo);
				LOGGER.debug("{} :: {}", field.getName(), value);
			}
			
		});
		
	}

}
