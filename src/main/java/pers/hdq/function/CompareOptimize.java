package pers.hdq.function;

import pers.hdq.model.DocFileEntity;
import pers.hdq.model.PlagiarizeEntity;
import pers.hdq.model.SimilarityOutEntity;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 文档相似度计算
 *
 * @Author: HuDaoquan
 * @Email: 1455523026@qq.com
 * @Date: 2019/6/13 12:27
 * @Version 1.0
 */
public class CompareOptimize {

    /**
     * 递归遍历入参path目录下所有文档，并两两比较相似度
     *
     * @param path           需要相似度比对的文件夹
     * @param ikFlag         是否打开智能分词，为false显示最小粒度分词结果
     * @param pictureSimFlag 是否计算文档中图片相似度，为是会增加准确率，但会极大增加运算时间
     * @param threshold      相似度阈值
     * @param excelPath      excel绝对路径
     *
     * @author HuDaoquan
     * @date 2022/6/15 14:50
     **/
    public static void getSimilarityMode1(String path, Boolean ikFlag, Boolean pictureSimFlag,
                                          Double threshold, String excelPath, Boolean multithreadingFlag) throws Exception {

        System.out.println("开始扫描文档,当前时间:" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        /*  递归遍历目录；获取所有文档绝对路径*/
        List<String> allDocAbsolutePath = CommonFunction.recursionWord(path);
        //总计算次数
        int sumCount = (allDocAbsolutePath.size() - 1) * allDocAbsolutePath.size() / 2;
        // 存储所有文档
        List<DocFileEntity> allDocEntityList = Collections.synchronizedList(new ArrayList<>(allDocAbsolutePath.size()));
        //选择线程类型
        ExecutorService threadPool = ThreadPoolUtil.fileThreadPool;
        if (!multithreadingFlag) {
            threadPool = ThreadPoolUtil.singleThreadPool;
        }

        CountDownLatch cdl = new CountDownLatch(allDocAbsolutePath.size());
        //遍历处理所有文件
        for (String s : allDocAbsolutePath) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    allDocEntityList.add(CommonFunction.getDocEntity(s, pictureSimFlag, ikFlag));
                    //计数器递减
                    cdl.countDown();
                }
            };
            //执行线程
            threadPool.execute(run);
        }

        //线程执行完后再执行主线程
        try {
            cdl.await();
        } catch (InterruptedException e) {
            System.out.println("阻塞子线程中断异常:" + e);
        }
        System.out.println("文档读取完成,开始计算相似度,共计" + allDocAbsolutePath.size() + "个文件,需计算" + sumCount + "次,当前时间:" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));


        int detailSize = sumCount;
        if (sumCount > 100000) {
            detailSize = 1;
        }
        // sheet1中详细所有数据
        List<SimilarityOutEntity> detailList = Collections.synchronizedList(new ArrayList<>(detailSize));
        // sheet2中简略结果数据
        List<SimilarityOutEntity> sortMaxResultList =
                Collections.synchronizedList(new ArrayList<>(allDocAbsolutePath.size()));
        // sheet3中超过相似度阈值名单
        List<PlagiarizeEntity> plagiarizeEntityList = Collections.synchronizedList(new ArrayList<>());
        //选择线程类型
        ExecutorService comThreadPool = ThreadPoolUtil.compareThreadPool;
        if (!multithreadingFlag) {
            comThreadPool = ThreadPoolUtil.singleThreadPool;
        }
        CountDownLatch compareCdl = new CountDownLatch(allDocAbsolutePath.size() - 1);
        // 遍所有文档信息冒泡原理两两比较文档相似度
        for (int i = 0; i < allDocEntityList.size() - 1; i++) {
            int finalI = i;
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    getFinishDocCountModel1(pictureSimFlag, threshold, sumCount, allDocEntityList, detailList, sortMaxResultList, plagiarizeEntityList, finalI);
                    //计数器递减
                    compareCdl.countDown();
                }
            };
            //执行线程
            comThreadPool.execute(run);
        }
        //线程执行完后再执行主线程
        try {
            compareCdl.await();
        } catch (InterruptedException e) {
            System.out.println("阻塞子线程中断异常:" + e);
        }

        if (detailList.isEmpty()) {
            SimilarityOutEntity similarityOutEntity =
                    SimilarityOutEntity.builder().judgeResult("本次比较详细结果将超过" + sumCount + "行,防止excel崩溃,此次详细结果不输出,请参考简略结果").build();
            detailList.add(similarityOutEntity);
        }
        // 排序并导出excel
        CommonFunction.sortAndImportExcel(excelPath, detailList, sortMaxResultList, plagiarizeEntityList);
    }

    /**
     * 模式1 外层循环调用
     *
     * @param pictureSimFlag
     * @param threshold
     * @param sumCount
     * @param allDocEntityList
     * @param detailList
     * @param sortMaxResultList
     * @param plagiarizeEntityList
     * @param i
     *
     * @return {@link int}
     * @author HuDaoquan
     * @date 2022/6/18 19:44
     **/
    private static void getFinishDocCountModel1(Boolean pictureSimFlag, Double threshold, int sumCount,
                                                List<DocFileEntity> allDocEntityList, List<SimilarityOutEntity> detailList, List<SimilarityOutEntity> sortMaxResultList, List<PlagiarizeEntity> plagiarizeEntityList, int i) {
        // 文档1与其后所有文档的相似度
        List<SimilarityOutEntity> docLeftAllSimList = new ArrayList<>();
        // 文档1
        DocFileEntity docLeft = allDocEntityList.get(i);
        for (int j = i + 1; j < allDocEntityList.size(); j++) {
            // 被比较文本
            DocFileEntity docRight = allDocEntityList.get(j);
            // 比较文本相似度
            SimilarityOutEntity cellSimEntity = CommonFunction.comparingTwoDoc(docLeft, docRight, pictureSimFlag, threshold, plagiarizeEntityList);
            docLeftAllSimList.add(cellSimEntity);
        }

        if (sumCount <= 100000) {
            // 相似度实体加到详细结果中
            detailList.addAll(docLeftAllSimList);
        }
        // 找出和文档1最相似的文档，先降序排序
        docLeftAllSimList =
                docLeftAllSimList.stream().sorted(Comparator.comparing(SimilarityOutEntity::getWeightedSimDouble,
                        Comparator.reverseOrder())).collect(Collectors.toList());
        System.out.println(docLeft.getAbsolutePath() + " 与其后的" + docLeftAllSimList.size() + "个文档比较完成,最大相似度:" + docLeftAllSimList.get(0).getWeightedSim());
        /*  求出每个文档的最大值，如果最大值有多个，只保留10个*/
        int m = 0;
        for (SimilarityOutEntity similarityOutEntity : docLeftAllSimList) {
            if (m >= 10) {
                break;
            }
            if (similarityOutEntity.getWeightedSimDouble().equals(docLeftAllSimList.get(0).getWeightedSimDouble())) {
                /*  将相似度实体加入简略结果*/
                sortMaxResultList.add(similarityOutEntity);
                m++;
            }
        }
    }


}
