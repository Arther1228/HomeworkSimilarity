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

        Map<String, List<String>> allExcelSheetListWithFileNames = EasyExcelReadUtil.getAllExcelSheetListWithFileNames(path);
        System.out.println(allExcelSheetListWithFileNames);
    }


    @Test
    public void getAllExcelColumnNames(){

        Map<String, List<String>> allExcelSheetListWithFileNames = EasyExcelReadUtil.getAllExcelColumnNames(path);
        System.out.println(allExcelSheetListWithFileNames);
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

        StringBuffer contentByDetailIndex = EasyExcelReadUtil.getContentByDetailIndex(filePath, sheetName, rowIndex, columnName);
        System.out.println(contentByDetailIndex);

    }

    @Test
    public void getContentByDetailIndex() {

        String sheetName = "题录加工";
        String columnName = "字段值";
        Integer rowIndex = 19;

        StringBuffer contentByDetailIndex = EasyExcelReadUtil.getContentByDetailIndex(filePath, sheetName, rowIndex, columnName);
        System.out.println(contentByDetailIndex);
    }
}
