package com.suncreate;

import org.junit.Test;
import pers.hdq.function.CommonFunction;
import pers.hdq.function.CompareOptimize;
import pers.hdq.model.DocFileEntity;


/**
 * @author yangliangchuang 2024-01-03 11:24
 */
public class CompareOptimizeTest {

    @Test
    public void getDocEntity() {

        String path = "C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123\\123.txt";
        DocFileEntity docEntity = CommonFunction.getDocEntity(path, false, true);

        System.out.println(docEntity);
    }

    @Test
    public void getSimilarityMode1() throws Exception {
        /*  需要相似度比对的路径*/
        String path = "C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123";
        /*  获取开始时间*/
        long startTime = System.currentTimeMillis();
        CompareOptimize.getSimilarityMode1(path, true, false, 0.9, true);
        /*  获取结束时间*/
        long endTime = System.currentTimeMillis();
        /*  输出程序运行时间*/
        System.out.println("所有文档相似度计算完成，共耗时：" + (endTime - startTime) / 1000 + "s");
    }
}
