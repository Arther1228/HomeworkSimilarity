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
public class CompareOptimize2 {


    /**
     * 相似度比对方式2：今年的文档两两比较，今年的与往年的比较；往年的互相之间不需要比较
     *
     * @param path           待相似度比对文件夹
     * @param ikFlag         ik智能分词开关
     * @param pictureSimFlag 图片相似度开关
     * @param threshold      重复度判定阈值
     * @param excelPath      导出的excel绝对路径
     * @author HuDaoquan
     * @date 2022/6/15 13:15
     **/
    public static void getSimilarityMode2(String path, Boolean ikFlag, Boolean pictureSimFlag,
                                          Double threshold, String excelPath, Boolean multithreadingFlag) throws Exception {
        System.out.println("开始扫描文档,当前时间:" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        /*  递归遍历目录；获取所有今年文档绝对路径*/
        List<String> thisYearDocAbsolutePath = CommonFunction.recursionWord(path + "\\今年");
        // 往年文档路径
        List<String> historyYearDocAbsolutePath = CommonFunction.recursionWord(path + "\\往年");
        //总计算次数
        int sumCount =
                (thisYearDocAbsolutePath.size() - 1) * thisYearDocAbsolutePath.size() / 2 + thisYearDocAbsolutePath.size() * historyYearDocAbsolutePath.size();

        // 存储今年文档
        List<DocFileEntity> thisYearDocEntityList =
                Collections.synchronizedList(new ArrayList<>(thisYearDocAbsolutePath.size()));
        // 存储往年文档
        List<DocFileEntity> historyYearDocEntityList =
                Collections.synchronizedList(new ArrayList<>(historyYearDocAbsolutePath.size()));
        //选择线程类型
        ExecutorService threadPool = ThreadPoolUtil.fileThreadPool;
        ExecutorService comThreadPool = ThreadPoolUtil.fileThreadPool;
        if (!multithreadingFlag) {
            threadPool = ThreadPoolUtil.singleThreadPool;
        }
        // 线程计数器
        CountDownLatch thisYearCdl = new CountDownLatch(thisYearDocAbsolutePath.size());
        //遍历处理所有今年文档
        for (String s : thisYearDocAbsolutePath) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    //获取今年文档实体
                    thisYearDocEntityList.add(CommonFunction.getDocEntity(s, pictureSimFlag, ikFlag));
                    //计数器递减
                    thisYearCdl.countDown();
                }
            };
            //执行线程
            threadPool.execute(run);
        }

        CountDownLatch historyCdl = new CountDownLatch(historyYearDocAbsolutePath.size());
        // 遍历处理所有往年文档
        for (String s : historyYearDocAbsolutePath) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    //获取往年文档实体
                    historyYearDocEntityList.add(CommonFunction.getDocEntity(s, pictureSimFlag, ikFlag));
                    //计数器递减
                    historyCdl.countDown();
                }
            };
            //执行线程
            threadPool.execute(run);
        }


        //线程执行完后再执行主线程
        try {
            thisYearCdl.await();
            historyCdl.await();
        } catch (InterruptedException e) {
            System.out.println("阻塞子线程中断异常:" + e);
        }
        System.out.println("今年文档数量:" + thisYearDocEntityList.size());
        System.out.println("往年文档数量:" + historyYearDocEntityList.size());

        System.out.println("开始计算相似度,需计算" + sumCount + "次,当前时间:" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        // 详情名单初始长度
        int detailSize = sumCount;
        if (sumCount > 100000) {
            detailSize = 1;
        }
        // sheet1中详细所有数据
        List<SimilarityOutEntity> detailList = Collections.synchronizedList(new ArrayList<>(detailSize));
        // sheet2中简略结果数据
        List<SimilarityOutEntity> sortMaxResultList = Collections.synchronizedList(new ArrayList<>(
                thisYearDocEntityList.size()));
        // sheet3中超过相似度阈值名单
        List<PlagiarizeEntity> plagiarizeEntityList = Collections.synchronizedList(new ArrayList<>());

        CountDownLatch compareCdl = new CountDownLatch(thisYearDocEntityList.size());

        // 冒泡排序原理遍历比较文件，遍所有文档信息冒泡原理两两比较文档相似度
        for (int i = 0; i < thisYearDocEntityList.size(); i++) {
            int finalI = i;
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    getFinishDocCountMode2(pictureSimFlag, threshold, sumCount, thisYearDocEntityList, historyYearDocEntityList, detailList, sortMaxResultList, plagiarizeEntityList, finalI);
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
        //关闭线程池
        ThreadPoolUtil.compareThreadPool.shutdown();
        if (detailList.isEmpty()) {
            SimilarityOutEntity similarityOutEntity =
                    SimilarityOutEntity.builder().judgeResult("本次比较详细结果将超过" + sumCount + "行,防止excel崩溃,此次详细结果不输出,请参考简略结果").build();
            detailList.add(similarityOutEntity);
        }
        CommonFunction.sortAndImportExcel(excelPath, detailList, sortMaxResultList, plagiarizeEntityList);
    }

    private static void getFinishDocCountMode2(Boolean pictureSimFlag, Double threshold, int sumCount,
                                               List<DocFileEntity> thisYearDocEntityList, List<DocFileEntity> historyYearDocEntityList, List<SimilarityOutEntity> detailList, List<SimilarityOutEntity> sortMaxResultList, List<PlagiarizeEntity> plagiarizeEntityList, int i) {
        // 文档1与其他被比较的所有文档的相似度
        List<SimilarityOutEntity> docLeftAllSimList = new ArrayList<>();
        // 文档1
        DocFileEntity docLeft = thisYearDocEntityList.get(i);
        //今年的文档
        for (int j = i + 1; j < thisYearDocEntityList.size(); j++) {
            //被比较文档
            DocFileEntity docRight = thisYearDocEntityList.get(j);
            // 比较两个文档相似度，返回相似度实体
            SimilarityOutEntity cellSimEntity = CommonFunction.comparingTwoDoc(docLeft, docRight, pictureSimFlag, threshold, plagiarizeEntityList);
            docLeftAllSimList.add(cellSimEntity);
        }
        //往年文档
        for (int j = 0; j < historyYearDocEntityList.size(); j++) {
            //被比较文档
            DocFileEntity docRight = historyYearDocEntityList.get(j);
            // 比较两个文档相似度，返回相似度实体
            SimilarityOutEntity cellSimEntity = CommonFunction.comparingTwoDoc(docLeft, docRight, pictureSimFlag, threshold, plagiarizeEntityList);
            docLeftAllSimList.add(cellSimEntity);
        }

        if (sumCount <= 100000) {
            detailList.addAll(docLeftAllSimList);
        }
        // 找出和文档1最相似的文档，先降序排序
        docLeftAllSimList = docLeftAllSimList.stream().sorted(Comparator.comparing(SimilarityOutEntity::getWeightedSimDouble, Comparator.reverseOrder())).collect(Collectors.toList());
        System.out.println(docLeft.getAbsolutePath() + "与其后的" + docLeftAllSimList.size() + "个文档比较完成,最大相似度:" + docLeftAllSimList.get(0).getWeightedSim());
        /*  求出每个文档的最大值，如果最大值有多个，只保留10个*/
        int m = 0;
        for (SimilarityOutEntity similarityOutEntity : docLeftAllSimList) {
            if (m >= 10) {
                break;
            }
            if (similarityOutEntity.getWeightedSimDouble().equals(docLeftAllSimList.get(0).getWeightedSimDouble())) {
                /*  加入后期排序*/
                sortMaxResultList.add(similarityOutEntity);
                m++;
            }
        }
    }


}
