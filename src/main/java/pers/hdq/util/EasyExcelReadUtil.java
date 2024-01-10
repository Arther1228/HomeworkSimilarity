package pers.hdq.util;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author HP
 */
@Slf4j
public class EasyExcelReadUtil {


    /**
     * 文件夹中的所有sheet的名称：文件名称
     *
     * @param folderPath
     * @return
     */
    public static Map<String, List<String>> getAllExcelSheetListWithFileNames(String folderPath) {
        Map<String, List<String>> sheetNameFileMap = new HashMap<>();

        try {
            Files.walk(Paths.get(folderPath))
                    .filter(path -> path.toString().toLowerCase().endsWith(".xls") || path.toString().toLowerCase().endsWith(".xlsx"))
                    .forEach(filePath -> {
                        try {
                            Set<String> sheetNames = getExcelSheetList(filePath.toString());
                            for (String sheetName : sheetNames) {
                                // Update the map to include the file name for the sheet
                                sheetNameFileMap.computeIfAbsent(sheetName, k -> new ArrayList<>()).add(filePath.toString());
                            }
                        } catch (Exception e) {
                            log.error("Error reading Excel file: {}", filePath, e);
                        }
                    });
        } catch (IOException e) {
            log.error("Error traversing folder: {}", folderPath, e);
        }

        return sheetNameFileMap;
    }


    /**
     * 查询所有的sheet列表
     *
     * @param path
     */
    public static Set<String> getExcelSheetList(String path) {

        ExcelReaderBuilder excelReaderBuilder = EasyExcel.read(new File(path));
        ExcelReader excelReader = excelReaderBuilder.build();
        List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
        Set<String> sheetNameSet = sheets.stream().map(ReadSheet::getSheetName).collect(Collectors.toSet());

        return sheetNameSet;
    }


    /**
     * 查询指定sheet，列名列表
     *
     * @param path
     */
    public static Set<String> getColumnNameListBySheet(String path, String sheetName) {

        ExcelDataListener excelDataListener = new ExcelDataListener();
        EasyExcel.read(path, excelDataListener).sheet(sheetName).doRead();
        Set<String> headNameSet = excelDataListener.getHeadNameSet();

        return headNameSet;

    }

    /**
     * 查询指定sheet、列名、行号的内容
     *
     * @param path
     */
    public static StringBuffer getContentByDetailIndex(String path, String sheetName, Integer rowIndex, String columnName) {
        StringBuffer content = new StringBuffer();

        ExcelDataListener excelDataListener = new ExcelDataListener();
        EasyExcel.read(path, excelDataListener).sheet(sheetName).doRead();

        //获取列 index
        Map<Integer, String> headMap = excelDataListener.getHeadMap();
        int columnIndex = -1;
        // 遍历
        for (Map.Entry<Integer, String> entry : headMap.entrySet()) {
            if (entry.getValue().equals(columnName)) {
                columnIndex = entry.getKey();
                break;
            }
        }

        //是否过滤行
        Map<Integer, Map<Integer, String>> excelContentMap = excelDataListener.getRowContentMap();
        if (rowIndex != -1) {
            Map<Integer, String> rowContentMap = excelContentMap.get(rowIndex);
            String columnContent = rowContentMap.get(columnIndex);
            if (StringUtils.isNotBlank(columnContent)) {
                content.append(columnContent + " ");
            }
        } else {
            for (Map<Integer, String> rowConent : excelContentMap.values()) {
                String columnContent = rowConent.get(columnIndex);
                if (StringUtils.isNotBlank(columnContent)) {
                    content.append(columnContent + " ");
                }
            }
        }

        return content;
    }

}

