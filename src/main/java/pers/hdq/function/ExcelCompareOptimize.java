package pers.hdq.function;

import pers.hdq.model.*;
import pers.hdq.similarity.CosineSimilarity;
import pers.hdq.similarity.Jaccard;
import pers.hdq.util.EasyExcelUtil;
import pers.hdq.util.FileUtils;
import pers.hdq.util.HutoolExcelUtil;
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
 * （1）excel有了比较项之后，比较方法不一样了，虽然有10个excel，但是每个比较项，可能就一两个；
 * （2）首先获取比较项，找到所在的excel，key：比较项，value:比较的excel文件路径；
 * （3）遍历key，找到对应excel中的内容，保存到对象，两两比较；
 * （4）
 */
public class ExcelCompareOptimize {

    /**
     * 将小数格式化为百分数
     **/
    private static DecimalFormat numFormat = new DecimalFormat("0.00%");


    // 存储所有文档内容
    private static List<ExcelFileEntity> allExcelEntityList = new ArrayList<>();


    public static void readExcelCompareContent(Map<ExcelCompareItem, List<String>> excelCompareItemAndExcelList, Boolean ikFlag, Boolean multithreadingFlag) {

        System.out.println("开始扫描文档,当前时间:" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        //选择线程类型
        ExecutorService threadPool = ThreadPoolUtil.fileThreadPool;
        if (!multithreadingFlag) {
            threadPool = ThreadPoolUtil.singleThreadPool;
        }

        //总计算次数
        int sumCount = 0;
        for (Map.Entry<ExcelCompareItem, List<String>> excelCompareItemEntry : excelCompareItemAndExcelList.entrySet()) {

            ExcelCompareItem excelCompareItem = excelCompareItemEntry.getKey();
            List<String> allExcelAbsolutePath = excelCompareItemEntry.getValue();
            int count = (allExcelAbsolutePath.size() - 1) * allExcelAbsolutePath.size() / 2;

            CountDownLatch cdl = new CountDownLatch(sumCount);
            //遍历处理所有文件
            for (String excelAbsolutePath : allExcelAbsolutePath) {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        allExcelEntityList.add(getExcelEntity(excelAbsolutePath, excelCompareItem, ikFlag));
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

            sumCount += count;
        }

        System.out.println("文档读取完成,开始计算相似度,需计算" + sumCount + "次,当前时间:" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
    }

    /**
     * 传入文档绝对路径，返回文档实体（绝对路径、图片路径、分词结果、图片hash结果等）
     *
     * @param path   文档绝对路径
     * @param ikFlag ik智能分词开关
     * @return {@link DocFileEntity}返回文档实体（绝对路径、图片路径、分词结果、图片hash结果等）
     * @author HuDaoquan
     * @date 2022/6/15 13:10
     **/
    public static ExcelFileEntity getExcelEntity(String path, ExcelCompareItem excelCompareItem, Boolean ikFlag) {
        File docFile = new File(path);
        String name = docFile.getName();

        ExcelFileEntity excelEntity = ExcelFileEntity.builder()
                .excelCompareItem(excelCompareItem)
                .fileName(name)
                .absolutePath(docFile.getAbsolutePath())
                .build();

        //将每个文档的文本分词后返回,去除数字和字母，使用IK分词器分词
        String fileContent = FileUtils.readFile(path);

        excelEntity.setWordList(IKUtils.segStr(fileContent.replaceAll("[0-9a-zA-Z]", ""), ikFlag));
        return excelEntity;
    }

    /**
     * 递归遍历入参path目录下所有文档，并两两比较相似度
     *
     * @param excelCompareItemAndExcelList Excel比对项
     * @param ikFlag                       是否打开智能分词，为false显示最小粒度分词结果
     * @param threshold                    相似度阈值
     * @author HuDaoquan
     * @date 2022/6/15 14:50
     **/
    public static void getExcelFileSimilarity(Map<ExcelCompareItem, List<String>> excelCompareItemAndExcelList, Boolean ikFlag,
                                              Double threshold, Boolean multithreadingFlag) {

        //解析excel 比较项内容
        readExcelCompareContent(excelCompareItemAndExcelList, ikFlag, multithreadingFlag);

        //总计算次数
        int sumCount = (allExcelAbsolutePath.size() - 1) * allExcelAbsolutePath.size() / 2;
        int detailSize = sumCount > 100000 ? 1 : sumCount;

        // sheet1中详细所有数据
        List<ExcelSimilarityOutEntity> detailList = Collections.synchronizedList(new ArrayList<>(detailSize));
        // sheet2中简略结果数据
        List<ExcelSimilarityOutEntity> sortMaxResultList = Collections.synchronizedList(new ArrayList<>(allExcelAbsolutePath.size()));
        // sheet3中超过相似度阈值名单
        List<PlagiarizeEntity> plagiarizeEntityList = Collections.synchronizedList(new ArrayList<>());

        //选择线程类型
        ExecutorService comThreadPool = ThreadPoolUtil.compareThreadPool;
        if (!multithreadingFlag) {
            comThreadPool = ThreadPoolUtil.singleThreadPool;
        }
        CountDownLatch compareCdl = new CountDownLatch(allExcelAbsolutePath.size() - 1);
        // 遍所有文档信息冒泡原理两两比较文档相似度
        for (int i = 0; i < allExcelEntityList.size() - 1; i++) {
            int finalI = i;
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    getFinishExcelCountModel(threshold, sumCount, allExcelEntityList, detailList, sortMaxResultList, plagiarizeEntityList, finalI);
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
            ExcelSimilarityOutEntity excelSimilarityOutEntity = ExcelSimilarityOutEntity.builder().judgeResult("本次比较详细结果将超过" + sumCount + "行,防止excel崩溃,此次详细结果不输出,请参考简略结果").build();
            detailList.add(excelSimilarityOutEntity);
        }

        String excelPath = path + "\\相似度比对结果-".concat("智能分词-" + "相似度比对-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()).concat(".xlsx"));
        // 排序并导出excel
        sortAndImportExcel(excelPath, detailList, sortMaxResultList, plagiarizeEntityList);
    }

    /**
     * 模式1 外层循环调用
     *
     * @param threshold
     * @param sumCount
     * @param allExcelEntityList
     * @param detailList
     * @param sortMaxResultList
     * @param plagiarizeEntityList
     * @param i
     * @return {@link int}
     * @author HuDaoquan
     * @date 2022/6/18 19:44
     **/
    private static void getFinishExcelCountModel(Double threshold, int sumCount, List<ExcelFileEntity> allExcelEntityList, List<ExcelSimilarityOutEntity> detailList, List<ExcelSimilarityOutEntity> sortMaxResultList, List<PlagiarizeEntity> plagiarizeEntityList, int i) {
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

        if (sumCount <= 100000) {
            // 相似度实体加到详细结果中
            detailList.addAll(docLeftAllSimList);
        }
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
        /*  存图片相似度*/
        double avgPicSim = 0D;
        /*  存最终加权相似度*/
        double weightedSim;

        /*  将文本相似度结果平方，，调整相似度*/
        weightedSim = (Math.pow(textSim, 1.5) + avgPicSim);

        if (weightedSim > threshold || jaccardSim > 0.90 || conSim > 0.90 || avgPicSim > 0.90) {
            judgeResult = "疑似超过相似度阈值";
            //超过相似度阈值名单
            plagiarizeEntityList.add(PlagiarizeEntity.builder().docName(docLeft.getAbsolutePath()).build());
            plagiarizeEntityList.add(PlagiarizeEntity.builder().docName(docRight.getAbsolutePath()).build());
        }

        return ExcelSimilarityOutEntity.builder()
                .excelCompareItemStr(docLeft.getExcelCompareItemStr())
                .judgeResult(judgeResult)
                .conSim(numFormat.format(conSim))
                .avgPicSim(numFormat.format(avgPicSim))
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
        detailList = detailList.stream().sorted(Comparator.comparing(ExcelSimilarityOutEntity::getWeightedSimDouble, Comparator.reverseOrder())).collect(Collectors.toList());
        // 排序简略结果
        sortMaxResultList = sortMaxResultList.stream().sorted(Comparator.comparing(ExcelSimilarityOutEntity::getWeightedSimDouble, Comparator.reverseOrder())).collect(Collectors.toList());
        // 去重超过相似度阈值名单
        plagiarizeEntityList = plagiarizeEntityList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PlagiarizeEntity::getDocName))), ArrayList::new));


        System.out.println("相似度计算完成,开始导出excel文件,当前时间:" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        EasyExcelUtil.writeXlsExcel(excelPath, detailList, sortMaxResultList, plagiarizeEntityList);
        System.err.println("相似度计算结果已存入：" + excelPath);
    }

}
