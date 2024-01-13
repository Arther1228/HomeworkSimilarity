package com.suncreate;

import org.junit.Before;
import org.junit.Test;
import pers.hdq.function.CommonFunction;
import pers.hdq.function.CompareOptimize;
import pers.hdq.function.ExcelCompareOptimize;
import pers.hdq.model.DocFileEntity;
import pers.hdq.model.ExcelCompareItem;

import java.util.*;


/**
 * @author yangliangchuang 2024-01-03 11:24
 */
public class CompareOptimizeTest {

    /*  需要相似度比对的路径*/
    String path = "C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123";

    String filePath = "C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123\\123.txt";

    Map<ExcelCompareItem, List<String>> excelCompareItemAndExcelList = new HashMap<>();

    @Before
    public void init() {

        ExcelCompareItem item1 = ExcelCompareItem.builder().sheetName("段落").columnName("段落内容").build();
        ExcelCompareItem item2 = ExcelCompareItem.builder().sheetName("题录加工").columnName("字段值").lineNumber("3").build();
        ExcelCompareItem item3 = ExcelCompareItem.builder().sheetName("题录加工").columnName("字段值").lineNumber("9").build();

        List<String> fileList1 = Arrays.asList("C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123\\GBT37735——2019 - 1.xlsx", "C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123\\GBT37735——2019 - 2.xlsx");
        List<String> fileList2 = Arrays.asList("C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123\\GBT37735——2019.xlsx", "C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123\\GBT37735——2019 - 1.xlsx", "C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123\\GBT37735——2019 - 2.xlsx");

        excelCompareItemAndExcelList.put(item1, fileList1);
        excelCompareItemAndExcelList.put(item2, fileList1);
        excelCompareItemAndExcelList.put(item3, fileList2);

    }

    @Test
    public void getDocEntity() {

        DocFileEntity docEntity = CommonFunction.getDocEntity(filePath, false, true);
        System.out.println(docEntity);

    }

    @Test
    public void getSimilarityMode1() throws Exception {

        /*  获取开始时间*/
        long startTime = System.currentTimeMillis();
        CompareOptimize.getSimilarityMode1(path, true, false, 0.9, true);
        /*  获取结束时间*/
        long endTime = System.currentTimeMillis();
        /*  输出程序运行时间*/
        System.out.println("所有文档相似度计算完成，共耗时：" + (endTime - startTime) / 1000 + "s");
    }


    @Test
    public void testToString() {

        String toString = ExcelCompareItem.toString(excelCompareItemAndExcelList);
        System.out.println(toString);

    }

    @Test
    public void getExcelFileSimilarity() {
        /*  获取开始时间*/
        long startTime = System.currentTimeMillis();

        ExcelCompareOptimize excelCompareOptimize = new ExcelCompareOptimize(true, true,0.9);
        excelCompareOptimize.getExcelFileSimilarity(path, excelCompareItemAndExcelList);

        /*  获取结束时间*/
        long endTime = System.currentTimeMillis();
        /*  输出程序运行时间*/
        System.out.println("所有文档相似度计算完成，共耗时：" + (endTime - startTime) / 1000 + "s");
    }
}
