package api.utilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

    FileInputStream fis;
    FileOutputStream fos;
    XSSFWorkbook workbook;
    XSSFSheet sheet;
    XSSFRow row;
    XSSFCell cell;
    String path;

    public ExcelUtil(String path, String sheetName) {
        this.path = path;
        try {
            fis = new FileInputStream(path);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get number of rows
    public int getRowCount() {
        return sheet.getLastRowNum() + 1;
    }

    // Get number of columns in a specific row
    public int getCellCount(int rowNum) {
        row = sheet.getRow(rowNum);
        if (row != null)
            return row.getLastCellNum();
        else
            return 0;
    }

    // Get cell data
    public String getCellData(int rowNum, int colNum) {
        try {
            row = sheet.getRow(rowNum);
            cell = row.getCell(colNum);
            DataFormatter formatter = new DataFormatter();
            return formatter.formatCellValue(cell);
        } catch (Exception e) {
            return "";
        }
    }

    // Set cell data
    public void setCellData(int rowNum, int colNum, String data) {
        try {
            row = sheet.getRow(rowNum);
            if (row == null)
                row = sheet.createRow(rowNum);

            cell = row.getCell(colNum);
            if (cell == null)
                cell = row.createCell(colNum);

            cell.setCellValue(data);

            fos = new FileOutputStream(path);
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fill cell with green color
    public void fillGreenColour(int rowNum, int colNum) {
        fillColour(rowNum, colNum, IndexedColors.BRIGHT_GREEN);
    }

    // Fill cell with red color
    public void fillRedColour(int rowNum, int colNum) {
        fillColour(rowNum, colNum, IndexedColors.RED);
    }

    // Generic method to fill color
    private void fillColour(int rowNum, int colNum, IndexedColors color) {
        try {
            row = sheet.getRow(rowNum);
            if (row == null)
                row = sheet.createRow(rowNum);

            cell = row.getCell(colNum);
            if (cell == null)
                cell = row.createCell(colNum);

            CellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(color.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell.setCellStyle(style);

            fos = new FileOutputStream(path);
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Close workbook after operations
    public void closeWorkbook() {
        try {
            if (workbook != null)
                workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
