package pers.hdq.function;

import pers.hdq.model.*;
import pers.hdq.similarity.CosineSimilarity;
import pers.hdq.similarity.Jaccard;
import pers.hdq.util.EasyExcelReadUtil;
import pers.hdq.util.EasyExcelUtil;
import pers.hdq.util.IKUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author yangliangchuang 2024-01-10 17:24
 */
public class ExcelCompareOptimize {

    /**
     * 将小数格式化为百分数
     **/
    private static DecimalFormat numFormat = new DecimalFormat("0.00%");

    private ExecutorService fileReadThreadPool;

    private ExecutorService comThreadPool;

    //分词
    private Boolean ikFlag;

    //阈值
    private Double threshold;

    //指定比对项：内容


    public ExcelCompareOptimize(Boolean ikFlag, Boolean multithreadingFlag, Double threshold) {

        this.ikFlag = ikFlag;
        this.threshold = threshold;

        //选择线程类型
        fileReadThreadPool = ThreadPoolUtil.fileThreadPool;
        if (!multithreadingFlag) {
            fileReadThreadPool = ThreadPoolUtil.singleThreadPool;
        }

        //选择线程类型
        comThreadPool = ThreadPoolUtil.compareThreadPool;
        if (!multithreadingFlag) {
            comThreadPool = ThreadPoolUtil.singleThreadPool;
        }
    }

    /**
     * 当前比较项，需要比对的列表
     *
     * @param excelCompareItem
     * @param allExcelAbsolutePath
     * @return
     */
    public List<ExcelFileEntity> readExcelCompareContent(ExcelCompareItem excelCompareItem, List<String> allExcelAbsolutePath) {
        System.out.println("根据当前指标项： " + excelCompareItem + ",开始扫描文档,当前时间:" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        List<ExcelFileEntity> allExcelEntityList = Collections.synchronizedList(new ArrayList<>());

        CountDownLatch cdl = new CountDownLatch(allExcelAbsolutePath.size());
        //遍历处理所有文件
        for (String excelAbsolutePath : allExcelAbsolutePath) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    allExcelEntityList.add(getExcelEntity(excelCompareItem, excelAbsolutePath));
                    //计数器递减
                    cdl.countDown();
                }
            };
            //执行线程
            fileReadThreadPool.execute(run);
        }

        //线程执行完后再执行主线程
        try {
            cdl.await();
        } catch (InterruptedException e) {
            System.out.println("阻塞子线程中断异常:" + e);
        }
        System.out.println("当前指标项：" + excelCompareItem + ",读取完成,当前时间:" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        return allExcelEntityList;
    }

    /**
     * 传入文档绝对路径，返回文档实体（绝对路径、图片路径、分词结果、图片hash结果等）
     *
     * @param filePath 文档绝对路径
     * @return {@link DocFileEntity}返回文档实体（绝对路径、图片路径、分词结果、图片hash结果等）
     * @author HuDaoquan
     * @date 2022/6/15 13:10
     **/
    public ExcelFileEntity getExcelEntity(ExcelCompareItem excelCompareItem, String filePath) {
        File docFile = new File(filePath);
        String name = docFile.getName();

        ExcelFileEntity excelEntity = ExcelFileEntity.builder()
                .excelCompareItem(excelCompareItem)
                .fileName(name)
                .absolutePath(docFile.getAbsolutePath())
                .build();

        //将每个文档的文本分词后返回,去除数字和字母，使用IK分词器分词
        String fileContent = EasyExcelReadUtil.getContentByDetailIndex(filePath, excelCompareItem.getSheetName(), excelCompareItem.getColumnName(), excelCompareItem.getLineNumber());
        excelEntity.setWordList(IKUtils.segStr(fileContent.replaceAll("[0-9a-zA-Z]", ""), ikFlag));
        return excelEntity;
    }

    /**
     * 递归遍历入参path目录下所有文档，并两两比较相似度
     *
     * @param excelCompareItemAndExcelList Excel比对项
     * @author HuDaoquan
     * @date 2022/6/15 14:50
     **/
    public void getExcelFileSimilarity(String path, Map<ExcelCompareItem, List<String>> excelCompareItemAndExcelList) {

        // sheet1中详细所有数据
        List<ExcelSimilarityOutEntity> detailList = Collections.synchronizedList(new ArrayList<>());
        // sheet2中简略结果数据
        List<ExcelSimilarityOutEntity> sortMaxResultList = Collections.synchronizedList(new ArrayList<>());
        // sheet3中超过相似度阈值名单
        List<PlagiarizeEntity> plagiarizeEntityList = Collections.synchronizedList(new ArrayList<>());

        //总计算次数
        int sumCount = 0;
        for (Map.Entry<ExcelCompareItem, List<String>> excelCompareItemEntry : excelCompareItemAndExcelList.entrySet()) {
            ExcelCompareItem excelCompareItem = excelCompareItemEntry.getKey();
            List<String> allExcelAbsolutePath = excelCompareItemEntry.getValue();
            System.out.println("=======================开始对指标项：【" + excelCompareItem + "】进行比较：=======================");

            //总计算次数
            sumCount += (allExcelAbsolutePath.size() - 1) * allExcelAbsolutePath.size() / 2;

            //解析excel 比较项内容
            List<ExcelFileEntity> allExcelEntityList = readExcelCompareContent(excelCompareItem, allExcelAbsolutePath);

            CountDownLatch compareCdl = new CountDownLatch(allExcelAbsolutePath.size() - 1);
            // 遍所有文档信息冒泡原理两两比较文档相似度
            for (int i = 0; i < allExcelEntityList.size() - 1; i++) {
                int finalI = i;
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        getFinishExcelCountModel(allExcelEntityList, detailList, sortMaxResultList, plagiarizeEntityList, finalI);
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

            System.out.println("=======================完成对指标项： 【" + excelCompareItem + "】 比较。=======================");
        }

        if (detailList.size() >= 100000) {
            ExcelSimilarityOutEntity excelSimilarityOutEntity = ExcelSimilarityOutEntity.builder().judgeResult("本次比较详细结果将超过" + sumCount + "行,防止excel崩溃,此次详细结果不输出,请参考简略结果").build();
            detailList.clear();
            detailList.add(excelSimilarityOutEntity);
        }

        String excelPath = path + "\\相似度比对结果-".concat("智能分词-" + "相似度比对-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()).concat(".xlsx"));
        // 排序并导出excel
        sortAndImportExcel(excelPath, detailList, sortMaxResultList, plagiarizeEntityList);
    }

    /**
     * 模式1 外层循环调用
     *
     * @param allExcelEntityList
     * @param detailList
     * @param sortMaxResultList
     * @param plagiarizeEntityList
     * @param i
     * @return {@link int}
     * @author HuDaoquan
     * @date 2022/6/18 19:44
     **/
    private void getFinishExcelCountModel(List<ExcelFileEntity> allExcelEntityList, List<ExcelSimilarityOutEntity> detailList, List<ExcelSimilarityOutEntity> sortMaxResultList, List<PlagiarizeEntity> plagiarizeEntityList, int i) {
        // 文档1与其后所有文档的相似度
        List<ExcelSimilarityOutEntity> docLeftAllSimList = new ArrayList<>();
        // 文档1
        ExcelFileEntity docLeft = allExcelEntityList.get(i);
        for (int j = i + 1; j < allExcelEntityList.size(); j++) {
            // 被比较文本
            ExcelFileEntity docRight = allExcelEntityList.get(j);
            // 比较文本相似度
            ExcelSimilarityOutEntity cellSimEntity = comparingTwoExcel(docLeft, docRight, threshold, plagiarizeEntityList);
            docLeftAllSimList.add(cellSimEntity);
        }

        // 相似度实体加到详细结果中
        detailList.addAll(docLeftAllSimList);

        // 找出和文档1最相似的文档，先降序排序
        docLeftAllSimList = docLeftAllSimList.stream().sorted(Comparator.comparing(ExcelSimilarityOutEntity::getWeightedSimDouble,
                Comparator.reverseOrder())).collect(Collectors.toList());
        System.out.println(docLeft.getAbsolutePath() + " 与其后的" + docLeftAllSimList.size() + "个文档比较完成,最大相似度:" + docLeftAllSimList.get(0).getWeightedSim());
        /*  求出每个文档的最大值，如果最大值有多个，只保留10个*/
        int m = 0;
        for (ExcelSimilarityOutEntity ExcelSimilarityOutEntity : docLeftAllSimList) {
            if (m >= 10) {
                break;
            }
            if (ExcelSimilarityOutEntity.getWeightedSimDouble().equals(docLeftAllSimList.get(0).getWeightedSimDouble())) {
                /*  将相似度实体加入简略结果*/
                sortMaxResultList.add(ExcelSimilarityOutEntity);
                m++;
            }
        }
    }

    /**
     * 比较两个文档的相似度，返回相似度实体
     *
     * @param docLeft              文档1
     * @param docRight             文档2
     * @param threshold            相似度判定阈值
     * @param plagiarizeEntityList 超过相似度阈值名单
     * @return {@link ExcelSimilarityOutEntity} 计算得到的相似度实体
     * @author HuDaoquan
     * @date 2022/6/15 13:38
     **/
    public static ExcelSimilarityOutEntity comparingTwoExcel(ExcelFileEntity docLeft, ExcelFileEntity docRight, Double threshold, List<PlagiarizeEntity> plagiarizeEntityList) {

        /*  余弦相似度*/
        double conSim = CosineSimilarity.sim(docLeft.getWordList(), docRight.getWordList());
        // // 杰卡德相似度
        double jaccardSim = Jaccard.jaccardSimilarity(docLeft.getWordList(), docRight.getWordList());
        // 不计算图片相似度
        double textSim = (conSim + jaccardSim) / 2;
        // 判断结果
        String judgeResult = "";
        /*  存最终加权相似度*/
        double weightedSim;

        /*  将文本相似度结果平方，，调整相似度*/
        weightedSim = textSim;

        if (weightedSim > threshold || jaccardSim > 0.90 || conSim > 0.90) {
            judgeResult = "疑似超过相似度阈值";
            //超过相似度阈值名单
            plagiarizeEntityList.add(PlagiarizeEntity.builder().docName(docLeft.getAbsolutePath()).build());
            plagiarizeEntityList.add(PlagiarizeEntity.builder().docName(docRight.getAbsolutePath()).build());
        }

        return ExcelSimilarityOutEntity.builder()
                .excelCompareItemStr(docLeft.getExcelCompareItem().toString())
                .judgeResult(judgeResult)
                .conSim(numFormat.format(conSim))
                .jaccardSim(numFormat.format(jaccardSim))
                .leftDocName(docLeft.getAbsolutePath())
                .weightedSim(numFormat.format(weightedSim))
                .rightDocName(docRight.getAbsolutePath())
                .weightedSimDouble(weightedSim)
                .build();

    }


    /**
     * 将几个sheet表数据排序去重并输出excel
     *
     * @param excelPath            excel绝对路径
     * @param detailList           详细名单
     * @param sortMaxResultList    简略名单
     * @param plagiarizeEntityList 超过相似度阈值名单
     * @author HuDaoquan
     * @date 2022/6/15 14:14
     **/
    public static void sortAndImportExcel(String excelPath, List<ExcelSimilarityOutEntity> detailList, List<ExcelSimilarityOutEntity> sortMaxResultList, List<PlagiarizeEntity> plagiarizeEntityList) {

        // 排序详细结果
        detailList = detailList.stream()
                .sorted(Comparator.comparing(ExcelSimilarityOutEntity::getExcelCompareItemStr)
                        .thenComparing(ExcelSimilarityOutEntity::getWeightedSimDouble, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        // 排序简略结果
        sortMaxResultList = sortMaxResultList.stream()
                .sorted(Comparator.comparing(ExcelSimilarityOutEntity::getExcelCompareItemStr)
                        .thenComparing(ExcelSimilarityOutEntity::getWeightedSimDouble, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        // 去重超过相似度阈值名单
        plagiarizeEntityList = plagiarizeEntityList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PlagiarizeEntity::getDocName))), ArrayList::new));


        System.out.println("相似度计算完成,开始导出excel文件,当前时间:" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        EasyExcelUtil.writeXlsExcel(excelPath, detailList, sortMaxResultList, plagiarizeEntityList);
        System.err.println("相似度计算结果已存入：" + excelPath);
    }

}
