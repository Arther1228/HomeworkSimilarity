package pers.hdq.model;

import lombok.*;

import java.util.List;

/**
 * 文本类
 *
 * @Author: HuDaoquan
 * @Email: 1455523026@qq.com
 * @Date: 2022/6/14 14:25
 * @Version 1.0
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ExcelFileEntity {

    private ExcelCompareItem excelCompareItem;
    /**
     * 绝对路径
     **/
    private String absolutePath;
    /**
     * 文件名
     **/
    private String fileName;

    /**
     * 分词结果
     **/
    private List<String> wordList;

}
