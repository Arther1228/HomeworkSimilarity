package com.suncreate;

import org.junit.Test;
import pers.hdq.util.HutoolExcelUtil;

import java.util.List;

public class HutoolExcelUtilTest {


    @Test
    public void main() {
        String folderPath = "C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123";
        List<String> excelFiles = HutoolExcelUtil.getAllExcelFilePaths(folderPath);

        // Display the list of Excel files
        for (String path : excelFiles) {
            System.out.println(path);
        }
    }
}
