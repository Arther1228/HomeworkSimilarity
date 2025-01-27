package pers.hdq.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.*;

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
public class ExcelSimilarityOutEntity {

    /**
     * Excel详细比较项
     **/
    @ColumnWidth(35)
    @ExcelProperty(value = "Excel详细比较项", index = 0)
    private String excelCompareItemStr;

    /**
     * 判定结果
     **/
    @ColumnWidth(35)
    @ExcelProperty(value = "判定结果", index = 1)
    private String judgeResult;
    /**
     * 文件名1
     **/
    @ColumnWidth(55)
    @ExcelProperty(value = "文件名1", index = 2)
    private String leftDocName;
    /**
     * 被比较的文件名
     **/
    @ColumnWidth(55)
    @ExcelProperty(value = "文件名2", index = 3)
    private String rightDocName;

    /**
     * 加权相似度
     **/
    @ColumnWidth(10)
    @ExcelProperty(value = "加权相似度", index = 4)
    private String weightedSim;

    /**
     * 加权相似度数值型
     **/
    @ExcelIgnore
    private Double weightedSimDouble;

    // /**
    //  * 已比较完成的次数，多线程此值获取不便
    //  **/
    // @ExcelIgnore
    // private Integer finishDocCount;
}
