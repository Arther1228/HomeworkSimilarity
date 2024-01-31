package pers.hdq.function;

import pers.hdq.model.DocFileEntity;
import pers.hdq.model.PlagiarizeEntity;
import pers.hdq.model.SimilarityOutEntity;
import pers.hdq.picture.PHash;
import pers.hdq.similarity.CosineSimilarity;
import pers.hdq.similarity.Jaccard;
import pers.hdq.util.EasyExcelUtil;
import pers.hdq.util.FileUtils;
import pers.hdq.util.IKUtils;
import pers.hdq.util.WordPicture;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangliangchuang 2024-01-11 13:10
 */
public class CommonFunction {


    /**
     * 将小数格式化为百分数
     **/
    static DecimalFormat numFormat = new DecimalFormat("0.00%");


    /**
     * 遍历文件夹中的文本文件
     *
     * @param root 遍历的跟路径
     * @return List<String> 存储有所有文本文件绝对路径的字符串数组
     */
    public static List<String> recursionWord(String root) throws Exception {
        List<String> allDocAbsolutePathList = new ArrayList<>();
        File file = new File(root);
        if (!file.exists()) {
            throw new Exception("文件夹不存在:" + root);
        }
        File[] subFile = file.listFiles();
        if (subFile != null) {
            for (File value : subFile) {
                String fileName = value.getName();
                /*  判断是文件还是文件夹*/
                if (value.isDirectory()) {
                    /*  文件夹则递归*/
                    List<String> childPathList = recursionWord(value.getAbsolutePath());
                    allDocAbsolutePathList.addAll(childPathList);
                } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".txt")) {
                    /*  绝对路径*/
                    String absolutePath = value.getAbsolutePath();
                    allDocAbsolutePathList.add(absolutePath);
                }
            }
        }
        return allDocAbsolutePathList;
    }


    /**
     * 传入文档绝对路径，返回文档实体（绝对路径、图片路径、分词结果、图片hash结果等）
     *
     * @param path           文档绝对路径
     * @param pictureSimFlag 是否处理图片
     * @param ikFlag         ik智能分词开关
     * @return {@link DocFileEntity}返回文档实体（绝对路径、图片路径、分词结果、图片hash结果等）
     * @author HuDaoquan
     * @date 2022/6/15 13:10
     **/
    public static DocFileEntity getDocEntity(String path, Boolean pictureSimFlag, Boolean ikFlag) {
        File docFile = new File(path);
        String name = docFile.getName();
        DocFileEntity docEntity = DocFileEntity.builder()
                .fileName(name)
                .absolutePath(docFile.getAbsolutePath())
                /*  父路径\\无空格无后缀文件名*/
                // .pictureParentPath(docFile.getParent() + "\\" + name.replaceAll(" ", "").split("\\.")[0])
                .build();
        //将每个文档的文本分词后返回,去除数字和字母，使用IK分词器分词
        docEntity.setWordList(IKUtils.segStr(FileUtils.readFile(path).replaceAll("[0-9a-zA-Z]", ""), ikFlag));
        // 比较图片相似度
        if (pictureSimFlag) {
            // 计算文档中图片的hash指纹
            List<String> oneDocPictureHashList = WordPicture.getWordPicture(docEntity);
            docEntity.setPictureHashList(oneDocPictureHashList);
            System.out.println(docEntity.getFileName() + "的图片数量为:" + oneDocPictureHashList.size());
        }
        return docEntity;
    }


    /**
     * 比较两个文档的相似度，返回相似度实体
     *
     * @param docLeft              文档1
     * @param docRight             文档2
     * @param pictureSimFlag       图片相似度
     * @param threshold            相似度判定阈值
     * @param plagiarizeEntityList 超过相似度阈值名单
     * @return {@link SimilarityOutEntity} 计算得到的相似度实体
     * @author HuDaoquan
     * @date 2022/6/15 13:38
     **/
    public static SimilarityOutEntity comparingTwoDoc(DocFileEntity docLeft, DocFileEntity docRight, Boolean pictureSimFlag,
                                                      Double threshold, List<PlagiarizeEntity> plagiarizeEntityList) {

        /*  余弦相似度*/
        double conSim = CosineSimilarity.sim(docLeft.getWordList(), docRight.getWordList());
        // // 杰卡德相似度
        double jaccardSim = Jaccard.jaccardSimilarity(docLeft.getWordList(), docRight.getWordList());
        double textSim = (conSim + jaccardSim) / 2;
        // 判断结果
        String judgeResult = "";
        /*  存图片相似度*/
        double avgPicSim = 0D;
        /*  存最终加权相似度*/
        double weightedSim;
        if (pictureSimFlag) {
            // 文档1中每张图片与文档2中所有图片相似度的最大值的集合
            List<Double> docLeftAllPictureMaxSim = new ArrayList<>(docLeft.getPictureHashList().size());
            for (String hashLeft : docLeft.getPictureHashList()) {
                List<Double> leftDocPictureSimList = new ArrayList<>(docLeft.getPictureHashList().size());
                for (String hashRight : docRight.getPictureHashList()) {
                    double pictureSim = PHash.getSimilarity(hashLeft, hashRight);
                    leftDocPictureSimList.add(pictureSim);
                    /*  找到某张图相似度超过90%就不再比较后面了，直接比较文档1的下一张图*/
                    if (pictureSim > 0.9) {
                        break;
                    }
                }
                // 求出文档1中某张图片与文档2中所有图片相似度的最大值
                double docLeftOnePictureSimMax =
                        leftDocPictureSimList.stream().max(Comparator.comparing(Double::doubleValue)).orElse(0D);
                docLeftAllPictureMaxSim.add(docLeftOnePictureSimMax);
            }
            // 求出文档1的所有图片相似度均值作为本次的图片相似度
            avgPicSim = docLeftAllPictureMaxSim.stream().collect(Collectors.averagingDouble(Double::doubleValue));
            // 如果任意一个文本图片为空，则总相似度不考虑图片相似度
            if (docLeft.getPictureHashList().isEmpty() && docRight.getPictureHashList().isEmpty()) {
                /*  将文本相似度结果平方，，调整相似度*/
                weightedSim = (Math.pow(textSim, 1.5) + avgPicSim);
            } else {
                /*  将文本相似度结果算1.5次方，，调整相似度*/
                weightedSim = Math.pow(textSim, 1.5) * 0.6 + avgPicSim * 0.4;
            }
        } else {
            // 不计算图片相似度
            textSim = (conSim + jaccardSim) / 2;
            /*  将文本相似度结果平方，，调整相似度*/
            weightedSim = textSim;
        }

        if (weightedSim > threshold || jaccardSim > 0.90 || conSim > 0.90 || avgPicSim > 0.90) {
            judgeResult = "疑似超过相似度阈值";
            //超过相似度阈值名单
            plagiarizeEntityList.add(PlagiarizeEntity.builder().docName(docLeft.getAbsolutePath()).build());
            plagiarizeEntityList.add(PlagiarizeEntity.builder().docName(docRight.getAbsolutePath()).build());
        }

        return SimilarityOutEntity.builder()
                .judgeResult(judgeResult)
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
    public static void sortAndImportExcel(String excelPath, List<SimilarityOutEntity> detailList, List<SimilarityOutEntity> sortMaxResultList, List<PlagiarizeEntity> plagiarizeEntityList) {

        // 排序详细结果
        detailList = detailList.stream().sorted(
                Comparator.comparing(
                        SimilarityOutEntity::getLeftDocName,
                        Comparator.comparingLong(CommonFunction::extractNumber))
                        .thenComparing(
                                SimilarityOutEntity::getRightDocName,
                                Comparator.comparingLong(CommonFunction::extractNumber))
        ).collect(Collectors.toList());

        // 排序简略结果
        sortMaxResultList = sortMaxResultList.stream().sorted(
                Comparator.comparing(
                        SimilarityOutEntity::getLeftDocName,
                        Comparator.comparingLong(CommonFunction::extractNumber))
                        .thenComparing(
                                SimilarityOutEntity::getRightDocName,
                                Comparator.comparingLong(CommonFunction::extractNumber))
        ).collect(Collectors.toList());

        // 去重超过相似度阈值名单
        plagiarizeEntityList = plagiarizeEntityList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(
                                PlagiarizeEntity::getDocName,
                                Comparator.comparingLong(CommonFunction::extractNumber)))), ArrayList::new));


        System.out.println("相似度计算完成,开始导出excel文件,当前时间:" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        EasyExcelUtil.writeExcel(excelPath, detailList, sortMaxResultList, plagiarizeEntityList);
        System.err.println("相似度计算结果已存入：" + excelPath);
    }

    public static long extractNumber(String fileName) {
        // 提取文件名中的数字部分
        String numberStr = fileName.replaceAll("[^0-9]", "");
        return numberStr.isEmpty() ? 0 : Long.parseLong(numberStr);
    }

}
