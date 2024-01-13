package pers.hdq.model;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yangliangchuang 2024-01-10 16:30
 */

@Data
@Builder
public class ExcelCompareItem {

    private String sheetName;

    private String cloumnName;

    private String lineNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExcelCompareItem that = (ExcelCompareItem) o;
        return Objects.equals(sheetName, that.sheetName) &&
                Objects.equals(cloumnName, that.cloumnName) &&
                Objects.equals(lineNumber, that.lineNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheetName, cloumnName, lineNumber);
    }

    public static String toString(Map<ExcelCompareItem, List<String>> excelCompareItemMap) {
        StringBuilder stringBuilder = new StringBuilder();
        int index = 1;

        for (Map.Entry<ExcelCompareItem, List<String>> entry : excelCompareItemMap.entrySet()) {
            ExcelCompareItem excelCompareItem = entry.getKey();
            List<String> fileNames = entry.getValue();

            stringBuilder.append("[比较项" + index + "]: ");
            stringBuilder.append("(1)工作簿名称：" + excelCompareItem.getSheetName());
            stringBuilder.append("、");
            stringBuilder.append("(2)列名：" + excelCompareItem.getCloumnName());
            if (StringUtils.isBlank(excelCompareItem.getLineNumber())) {
                stringBuilder.append("、");
                stringBuilder.append("(3)行号：未设置，比较全列文本");
            } else {
                stringBuilder.append("、");
                stringBuilder.append("(3)行号：" + excelCompareItem.getLineNumber());
            }
            stringBuilder.append("、");
            stringBuilder.append("(4)文件名称列表：");

            // 截取文件路径，只保留文件名称和文件后缀名
            List<String> simplifiedFileNames = fileNames.stream()
                    .map(filePath -> Paths.get(filePath).getFileName().toString())
                    .collect(Collectors.toList());

            if (simplifiedFileNames.size() <= 2) {
                stringBuilder.append(String.join(", ", simplifiedFileNames));
            } else {
                stringBuilder.append(simplifiedFileNames.size() + " 个文件");
            }

            stringBuilder.append("\n");
            index++;
        }

        return stringBuilder.toString();
    }
}
