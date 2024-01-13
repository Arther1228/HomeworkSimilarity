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

    private String filePath = "C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123\\相似度比对结果-智能分词-相似度比对-20240113085224.xlsx";


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
        String rowIndex = "-1";

        String contentByDetailIndex = EasyExcelReadUtil.getContentByDetailIndex(filePath, sheetName, columnName, rowIndex);
        System.out.println(contentByDetailIndex);


        String sheetName2 = "题录加工";
        String columnName2 = "字段值";
        String rowIndex2 = "19";

        String contentByDetailIndex2 = EasyExcelReadUtil.getContentByDetailIndex(filePath, sheetName2, columnName2, rowIndex2);
        System.out.println(contentByDetailIndex2);

    }

}
