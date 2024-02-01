package pers.hdq.util;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import pers.hdq.model.DocFileEntity;
import pers.hdq.model.ExcelSimilarityOutEntity;
import pers.hdq.model.PlagiarizeEntity;
import pers.hdq.model.SimilarityOutEntity;

import java.util.*;
import java.util.stream.Collectors;


/**
 * easyExcel 工具类(仅导出)中可自定义样式格式等
 *
 * @Author: HuDaoquan
 * @Email: 1455523026@qq.com
 * @Date: 2021/5/22 14:18
 * @Version 1.0
 */
public class EasyExcelUtil {


    /**
     * 导出 Excel ：一个 sheet，带表头.
     *
     * @param detailList,sortMaxResultList,SimilarityOutList 各个Sheet数据
     * @param filepath                                       导出的文件名
     */
    public static void writeExcel(String filepath, List<SimilarityOutEntity> detailList, List<SimilarityOutEntity> sortMaxResultList,
                                  List<PlagiarizeEntity> plagiarizeEntityList) {
        //调用工具类,导出excel
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 颜色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 10);
        // 字体
        headWriteCellStyle.setWriteFont(headWriteFont);
        headWriteCellStyle.setWrapped(true);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //设置内容靠中对齐
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        ExcelWriter excelWriter = EasyExcel.write(filepath).excelType(ExcelTypeEnum.XLSX).registerWriteHandler(horizontalCellStyleStrategy).build();

        WriteSheet sheetDetail = EasyExcel.writerSheet(0, "详细结果").head(SimilarityOutEntity.class).build();
        excelWriter.write(detailList, sheetDetail);

        WriteSheet sheetMax = EasyExcel.writerSheet(1, "简略结果").head(SimilarityOutEntity.class).build();
        excelWriter.write(sortMaxResultList, sheetMax);

        WriteSheet sheetPlagiarize = EasyExcel.writerSheet(2, "超过相似度阈值名单").head(PlagiarizeEntity.class).build();
        excelWriter.write(plagiarizeEntityList, sheetPlagiarize);

        excelWriter.finish();

    }

    /**
     * 导出 Excel ：一个 sheet，带表头.
     *
     * @param detailList,sortMaxResultList,SimilarityOutList 各个Sheet数据
     * @param filepath                                       导出的文件名
     */
    public static void writeExcelMatrix(String filepath, List<SimilarityOutEntity> detailList, List<DocFileEntity> sortedAllDocEntityList) {

        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 颜色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 11);
        // 取消加粗
        headWriteFont.setBold(false);
        // 设置字体为宋体
        headWriteFont.setFontName("Calibri");
        // 字体
        headWriteCellStyle.setWriteFont(headWriteFont);
        headWriteCellStyle.setWrapped(true);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //设置内容靠中对齐
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

        //数据转换
        Map<String, String> similarityMap = detailList.stream().collect(Collectors.toMap(
                entity -> entity.getLeftFileName() + "-" + entity.getRightFileName(), SimilarityOutEntity::getWeightedSim));

        //用户过滤不存在的key
        Set<String> leftFileNamesSet = detailList.stream()
                .map(SimilarityOutEntity::getLeftFileName)
                .collect(Collectors.toSet());

        // 每一列标题
        List<List<String>> headList = new ArrayList<>();

        // 每一行的数据
        List<List<Object>> dataList = new ArrayList<>();

        for (int i = 0; i < sortedAllDocEntityList.size(); i++) {
            String lineDocFileEntityFileName = sortedAllDocEntityList.get(i).getFileName();

            List<Object> line = new ArrayList<>();
            //过滤最后一行
            if (!leftFileNamesSet.contains(lineDocFileEntityFileName)) continue;
            line.add(lineDocFileEntityFileName);

            for (int j = 0; j < sortedAllDocEntityList.size(); j++) {
                String rowDocFileEntityFileName = sortedAllDocEntityList.get(j).getFileName();

                // 标题只统计一次
                if (i == 0) {
                    if (j == 0) {
                        headList.add(Collections.singletonList("文件名"));
                    } else {
                        headList.add(Collections.singletonList(rowDocFileEntityFileName));
                    }
                }
                // 过滤第一列
                if (lineDocFileEntityFileName.equals(rowDocFileEntityFileName)) continue;
                String weightedSim = similarityMap.get(lineDocFileEntityFileName + "-" + rowDocFileEntityFileName);
                weightedSim = weightedSim == null ? "    /" : weightedSim;
                line.add(weightedSim);
            }
            dataList.add(line);
        }

        EasyExcel.write(filepath)
                .head(headList).sheet("相似度矩阵")
                .registerWriteHandler(horizontalCellStyleStrategy)
                .doWrite(dataList);
    }


    /**
     * 导出 Excel ：一个 sheet，带表头.
     *
     * @param detailList,sortMaxResultList,SimilarityOutList 各个Sheet数据
     * @param filepath                                       导出的文件名
     */
    public static void writeXlsExcel(String filepath, List<ExcelSimilarityOutEntity> detailList, List<ExcelSimilarityOutEntity> sortMaxResultList,
                                     List<PlagiarizeEntity> plagiarizeEntityList) {
        //调用工具类,导出excel
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 颜色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 10);
        // 字体
        headWriteCellStyle.setWriteFont(headWriteFont);
        headWriteCellStyle.setWrapped(true);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //设置内容靠中对齐
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        ExcelWriter excelWriter = EasyExcel.write(filepath).excelType(ExcelTypeEnum.XLSX).registerWriteHandler(horizontalCellStyleStrategy).build();

        WriteSheet sheetDetail = EasyExcel.writerSheet(0, "详细结果").head(ExcelSimilarityOutEntity.class).build();
        excelWriter.write(detailList, sheetDetail);

        WriteSheet sheetMax = EasyExcel.writerSheet(1, "简略结果").head(ExcelSimilarityOutEntity.class).build();
        excelWriter.write(sortMaxResultList, sheetMax);

        WriteSheet sheetPlagiarize = EasyExcel.writerSheet(2, "超过相似度阈值名单").head(PlagiarizeEntity.class).build();
        excelWriter.write(plagiarizeEntityList, sheetPlagiarize);

        excelWriter.finish();

    }


}

