package pers.hdq.model;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author yangliangchuang 2024-01-10 16:30
 */

@Data
@Builder
public class ExcelCompareEntity {

    private String sheetName;

    private String cloumnName;

    private String lineNumber;


    public static String toString(List<ExcelCompareEntity> excelCompareEntityList) {
        StringBuilder stringBuilder = new StringBuilder();
        int index = 1;
        for (ExcelCompareEntity excelCompareEntity : excelCompareEntityList) {
            stringBuilder.append("[比较项" + index + "]: ");
            stringBuilder.append("(1)工作簿名称：" + excelCompareEntity.getSheetName());
            stringBuilder.append("、");
            stringBuilder.append("(2)列名：" + excelCompareEntity.getCloumnName());
            if (StringUtils.isNotBlank(excelCompareEntity.getLineNumber())) {
                stringBuilder.append("、");
                stringBuilder.append("(3)行号：" + excelCompareEntity.getLineNumber());
            }
            stringBuilder.append("\n");
            index++;
        }
        return stringBuilder.toString();
    }
}
