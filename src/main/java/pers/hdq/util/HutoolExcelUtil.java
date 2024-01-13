package pers.hdq.util;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class HutoolExcelUtil {

    /**
     * 获取指定路径下的所有Excel文件的绝对路径
     *
     * @param folderPath 指定路径
     * @return Excel文件的绝对路径列表
     */
    public static List<String> getAllExcelFilePaths(String folderPath) {

        List<String> excelFilePaths = FileUtil.loopFiles(folderPath, file -> {
            String fileName = file.getName().toLowerCase();
            return (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) && !fileName.startsWith("~$");
        }).stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());

        return excelFilePaths;
    }

}
