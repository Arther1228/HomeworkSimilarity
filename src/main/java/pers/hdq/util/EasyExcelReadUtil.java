package pers.hdq.util;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author HP
 */
@Slf4j
public class EasyExcelReadUtil {

    private List<String> allExcelFiles;

    private Map<String, Set<String>> fileSheetListMap = new HashMap<>();

    public EasyExcelReadUtil(String folderPath) {
        allExcelFiles = HutoolExcelUtil.getAllExcelFilePaths(folderPath);

        for (String filePath : allExcelFiles) {
            Set<String> sheetNames = getExcelSheetList(filePath);
            fileSheetListMap.put(filePath, sheetNames);
        }
    }

    /**
     * 文件夹中的所有sheet的名称：文件名称
     *
     * @return
     */
    public Map<String, List<String>> getAllExcelSheetListWithFileNames() {
        Map<String, List<String>> sheetNameFileMap = new HashMap<>(10);

        for (String filePath : allExcelFiles) {
            Set<String> sheetNames = fileSheetListMap.get(filePath);
            for (String sheetName : sheetNames) {
                sheetNameFileMap.computeIfAbsent(sheetName, k -> new ArrayList<>()).add(filePath);
            }
        }

        return sheetNameFileMap;
    }

    /**
     * 指定路径下所有Excel文件的列名信息
     *
     * @return Map，键为"文件名-sheet名"，值为列名列表
     */
    public Map<String, List<String>> getAllColumnNamesByFileNameAndSheet() {

        Map<String, List<String>> columnNameMap = new HashMap<>(10);
        for (String filePath : allExcelFiles) {
            Set<String> sheetNames = fileSheetListMap.get(filePath);
            for (String sheetName : sheetNames) {
                Set<String> columnNames = getColumnNameListBySheet(filePath, sheetName);
                String key = filePath + "-" + sheetName;
                columnNameMap.put(key, new ArrayList<>(columnNames));
            }
        }

        return columnNameMap;
    }

    /**
     * 获取所有sheet名 + 列名组成的key，以及对应的存在文件列表
     *
     * @return Map，键为"sheet名-列名"，值为存在文件列表
     */
    public Map<String, List<String>> getAllSheetAndColumnNamesWithFiles() {
        Map<String, List<String>> sheetColumnFileMap = new HashMap<>(10);

        for (String filePath : allExcelFiles) {
            Set<String> sheetNames = fileSheetListMap.get(filePath);
            for (String sheetName : sheetNames) {
                Set<String> columnNames = getColumnNameListBySheet(filePath, sheetName);

                for (String columnName : columnNames) {
                    String key = sheetName + "-" + columnName;
                    sheetColumnFileMap.computeIfAbsent(key, k -> new ArrayList<>()).add(filePath);
                }
            }
        }

        return sheetColumnFileMap;
    }


    /**
     * 查询所有的sheet列表
     *
     * @param filePath
     */
    public static Set<String> getExcelSheetList(String filePath) {
        Set<String> sheetNameSet = new HashSet<>();
        try {
            ExcelReaderBuilder excelReaderBuilder = EasyExcel.read(new File(filePath));
            ExcelReader excelReader = excelReaderBuilder.build();
            List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
            sheetNameSet = sheets.stream().map(ReadSheet::getSheetName).collect(Collectors.toSet());
            excelReader.close();
        } catch (InvalidOperationException e) {
            System.err.println("有一个文件，因为正在打开，无法访问：" + filePath);
        } catch (Exception e) {
            System.err.println("查询所有的sheet列表报错，原因：" + e.getMessage());
        }
        return sheetNameSet;
    }


    /**
     * 查询指定文件和sheet的所有列名
     *
     * @param filePath
     */
    public static Set<String> getColumnNameListBySheet(String filePath, String sheetName) {
        Set<String> headNameSet = new HashSet<>();
        try {
            ExcelDataListener excelDataListener = new ExcelDataListener();
            EasyExcel.read(filePath, excelDataListener).sheet(sheetName).doRead();
            headNameSet = excelDataListener.getHeadNameSet();
        } catch (Exception e) {
            System.err.println("查询指定sheet，列名列表报错，原因：" + e.getMessage());
        }
        return headNameSet;

    }


    /**
     * 查询指定sheet、列名、行号的内容
     *
     * @param filePath
     */
    public static String getContentByDetailIndex(String filePath, String sheetName, String columnName, String lineNumber) {
        StringBuffer content = new StringBuffer();

        try {
            ExcelDataListener excelDataListener = new ExcelDataListener();
            EasyExcel.read(filePath, excelDataListener).sheet(sheetName).doRead();

            //获取列 index
            Map<Integer, String> headMap = excelDataListener.getHeadMap();

            if (headMap == null) {
                System.err.println("当前文件: " + filePath + " 不存在指定列：" + sheetName);
                return content.toString();
            }

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
            if (StringUtils.isNotBlank(lineNumber) && !"-1".equals(lineNumber)) {
                Map<Integer, String> rowContentMap = excelContentMap.get(Integer.valueOf(lineNumber));
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
        } catch (Exception e) {
            System.err.println("读文件: " + filePath + " 内容发生错误，原因：" + e.getMessage());
        }

        return content.toString();
    }

}

