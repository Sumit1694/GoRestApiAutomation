package api.utilities;

import java.io.IOException;

import org.testng.annotations.DataProvider;

public class DataProviders {

    @DataProvider(name = "Data")
    public String[][] getAllData() throws IOException {
        String path = System.getProperty("user.dir") + "//testData//UserData.xlsx";
        ExcelUtil xl = new ExcelUtil(path, "Sheet1");

        int rownum = xl.getRowCount();
        int colcount = xl.getCellCount(1);

        String apidata[][] = new String[rownum - 1][colcount];

        for (int i = 1; i < rownum; i++) { 
            for (int j = 0; j < colcount; j++) {
                apidata[i - 1][j] = xl.getCellData(i, j);
            }
        }
        xl.closeWorkbook();
        return apidata;
    }

    @DataProvider(name = "UserNames")
    public Object[] getUserNames() throws IOException {
        String path = System.getProperty("user.dir") + "//testData//UserData.xlsx";
        ExcelUtil xl = new ExcelUtil(path, "Sheet1");

        int rownum = xl.getRowCount();

        Object[] apidata = new Object[rownum - 1];

        for (int i = 1; i < rownum; i++) { // skip header row
            apidata[i - 1] = xl.getCellData(i, 1); // username assumed at column index 1
        }

        xl.closeWorkbook();
        return apidata;
    }
}
