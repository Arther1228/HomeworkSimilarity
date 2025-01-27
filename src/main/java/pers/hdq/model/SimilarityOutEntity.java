package pers.hdq.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO
 *
 * @Author: HuDaoquan
 * @Email: 1455523026@qq.com
 * @Date: 2022/6/13 12:27
 * @Version 1.0
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@ContentRowHeight(15)
@HeadRowHeight(30)
public class SimilarityOutEntity {
    /**
     * 判定结果
     **/
    @ColumnWidth(35)
    @ExcelProperty(value = "判定结果", index = 0)
    private String judgeResult;
    /**
     * 文件名1
     **/
    @ColumnWidth(55)
    @ExcelProperty(value = "文件名1", index = 1)
    private String leftDocName;

    private String leftFileName;

    /**
     * 被比较的文件名
     **/
    @ColumnWidth(55)
    @ExcelProperty(value = "文件名2", index = 2)
    private String rightDocName;


    private String rightFileName;
    /**
     * 加权相似度
     **/
    @ColumnWidth(10)
    @ExcelProperty(value = "加权相似度", index = 3)
    private String weightedSim;

    /**
     * 加权相似度数值型
     **/
    @ExcelIgnore
    private Double weightedSimDouble;

}
