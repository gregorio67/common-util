import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stis.framework.util.ExcelStyle;
import stis.framework.util.ExcelUtil;
import stis.framework.util.NullUtil;

public class ExcelResultHandler<T> implements ResultHandler<Map<String, Object>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelResultHandler.class);
	
	private Workbook workbook =  null;
	private Sheet sheet = null;
	private int rowNum = 0;
	
	public ExcelResultHandler(Workbook workbook) {
		this.workbook = new XSSFWorkbook();
		try {
			createSheetTitle(this.workbook, null, null);			
		}
		catch(Exception ex) {
			
		}
	}
	public ExcelResultHandler(Workbook workbook, List<String> headerColumns) {
		this.workbook = workbook;
		try {
			createSheetTitle(this.workbook, null, headerColumns);			
		}
		catch(Exception ex) {
			
		}
	}
	
	public ExcelResultHandler(Workbook workbook, String headerName, List<String> headerColumns) {
		this.workbook = workbook;
		try {
			createSheetTitle(this.workbook, headerName, headerColumns);			
		}
		catch(Exception ex) {
			
		}
	}
	
	@Override
	public void handleResult(ResultContext<? extends Map<String, Object>> resultContext) {
		Map<String, Object> result = resultContext.getResultObject();
		try {
			ExcelUtil.map2Excel(sheet, result, ExcelStyle.getDataStyle(workbook), rowNum);			
		}
		catch (Exception ex) {
			
		}
		rowNum++;
	}

	/**
	 * 
	 *<pre>
	 * 1.Description: Create excel header and header column
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param workbook excel workbook
	 * @param headerName excel header name
	 * @param headerColumns excel header column
	 * @throws Exception
	 */
	private void createSheetTitle(Workbook workbook, String headerName, List<String> headerColumns) throws Exception {

		sheet = workbook.createSheet();
		
		/** Create Excel Header Name **/
		if (!NullUtil.isNull(headerName)) {
			Row row = sheet.createRow(rowNum);
			Cell cell = row.getCell(0);
			cell.setCellValue(headerName);
			cell.setCellStyle(ExcelStyle.getHeaderNameStyle(this.workbook));

			/** Cell Merge **/
			sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, headerColumns.size() - 1));			
			rowNum++;
		}
		/** Create excel header columns **/
		if (headerColumns != null) {
			Row row = sheet.createRow(rowNum);
			int idx = 0;
			for (String headerColumn : headerColumns) {
				Cell cell = row.getCell(idx++);
				cell.setCellStyle(ExcelStyle.getHeaderColumnStyle(this.workbook));
				cell.setCellValue(headerColumn);
			}
			rowNum++;			
		}
	}
}
