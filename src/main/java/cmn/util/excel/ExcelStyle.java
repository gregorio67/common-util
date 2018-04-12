import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelStyle {

	/**
	 * Excel Header style
	 *<pre>
	 *
	 *</pre>
	 * @param workbook excel workbook
	 * @return excel header column style
	 * @throws Exception
	 */
	public static CellStyle getHeaderColumnStyle(Workbook workbook) throws Exception {

		CellStyle cellStyle = workbook.createCellStyle();
		
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
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Excel data style
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param workbook excel workbook
	 * @return excel cell style for data
	 * @throws Exception
	 */
	public static CellStyle getDataStyle(Workbook workbook) throws Exception {
		
		CellStyle cellStyle = workbook.createCellStyle();
		
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
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Excel Header Style
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param workbook excel workbook
	 * @return Excel style
	 * @throws Exception
	 */
	public static CellStyle getHeaderNameStyle(Workbook workbook) throws Exception {
		Font font = workbook.createFont();
		font.setBold(true);
		
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setFont(font);
		
		return cellStyle;
	}
}
