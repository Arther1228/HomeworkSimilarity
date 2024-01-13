package com.suncreate;

import org.junit.Test;
import pers.hdq.util.EasyExcelReadUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author yangliangchuang 2024-01-03 15:18
 */
public class EasyExcelReadUtilTest {

    private String path = "C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123";

    private String filePath = "C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123\\GBT37735——2019.xlsx";


    @Test
    public void getAllExcelSheetList(){

        EasyExcelReadUtil easyExcelReadUtil = new EasyExcelReadUtil(path);
        Map<String, List<String>> allExcelSheetListWithFileNames = easyExcelReadUtil.getAllExcelSheetListWithFileNames();
        System.out.println(allExcelSheetListWithFileNames);
    }


    @Test
    public void getAllExcelColumnNames(){
        EasyExcelReadUtil easyExcelReadUtil = new EasyExcelReadUtil(path);
        Map<String, List<String>> allColumnNamesByFileNameAndSheet = easyExcelReadUtil.getAllColumnNamesByFileNameAndSheet();
        System.out.println(allColumnNamesByFileNameAndSheet);
    }

    @Test
    public void getExcelSheetList() {

        Set<String> excelSheetList = EasyExcelReadUtil.getExcelSheetList(filePath);
        System.out.println(excelSheetList);

    }

    @Test
    public void getColumnNameListBySheet() {
        String sheetName = "题录加工";
        Set<String> sheetColumnNameList = EasyExcelReadUtil.getColumnNameListBySheet(filePath, sheetName);
        System.out.println(sheetColumnNameList);

    }


    @Test
    public void getContentByDetail() {

        String sheetName = "段落";
        String columnName = "段落内容";
        Integer rowIndex = -1;
        Integer rowIndex2 = 19;

        StringBuffer contentByDetailIndex = EasyExcelReadUtil.getContentByDetailIndex(filePath, sheetName, rowIndex, columnName);
        StringBuffer contentByDetailIndex2 = EasyExcelReadUtil.getContentByDetailIndex(filePath, sheetName, rowIndex2, columnName);
        System.out.println(contentByDetailIndex);
        System.out.println(contentByDetailIndex2);

    }

}
