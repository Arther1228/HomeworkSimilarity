package pers.hdq.ui;

import pers.hdq.function.CompareOptimize;
import pers.hdq.function.ExcelCompareOptimize;
import pers.hdq.model.ExcelCompareItem;
import pers.hdq.util.CompareFileType;
import pers.hdq.util.ThresholdUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

/**
 * @author yangliangchuang 2024-01-10 11:06
 */
public class CompareMouseAdapter extends MouseAdapter {

    private int index = 1;

    private UIhdq uIhdq;

    private JCheckBox wordBox;

    private JComboBox comboBox;

    private JComboBox multithreadingBox;

    public CompareMouseAdapter(UIhdq uIhdq, JCheckBox wordBox, JComboBox comboBox, JComboBox multithreadingBox) {
        this.uIhdq = uIhdq;
        this.wordBox = wordBox;
        this.comboBox = comboBox;
        this.multithreadingBox = multithreadingBox;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        String path = uIhdq.getPath();
        JTextArea docLocationTextArea = uIhdq.getDocLocationTextArea();

        if (path == null || path.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请先选择比对路径", "提示", JOptionPane.WARNING_MESSAGE);
            return; // 不执行后续的操作
        }

        docLocationTextArea.setText("开始处理：\n");
        docLocationTextArea.paintImmediately(docLocationTextArea.getBounds());
        docLocationTextArea.setForeground(Color.BLACK);

        if (index % 2 == 0) {
            docLocationTextArea.setForeground(Color.BLACK);
        } else {
            docLocationTextArea.setForeground(Color.magenta);
        }
        index++;

        try {
            long startTime = System.currentTimeMillis();
            //获取相似度阈值
            Double simThre = ThresholdUtil.getSimThre((String) comboBox.getSelectedItem());
            //是否开启多线程
            boolean multithreadingFlag = "2.多线程".equals(multithreadingBox.getSelectedItem());
            //比较文件类型
            String compareType = uIhdq.getCompareType();
            docLocationTextArea.append("当前选择的比较文件类型： " + compareType + "\n");

            if (CompareFileType.EXCEL.getName().equals(compareType)) {
                //Excel比较项
                Map<ExcelCompareItem, List<String>> excelCompareItemAndExcelList = UiTabbedPane.getExcelCompareItemAndExcelList();
                //打印比较项
                docLocationTextArea.append(ExcelCompareItem.toString(excelCompareItemAndExcelList));

                //比较
                ExcelCompareOptimize excelCompareOptimize = new ExcelCompareOptimize(wordBox.isSelected(), multithreadingFlag, simThre);
                excelCompareOptimize.getExcelFileSimilarity(path, excelCompareItemAndExcelList);

            } else if (CompareFileType.WORD_TXT.getName().equals(compareType)) {
                CompareOptimize.getSimilarityMode1(path, wordBox.isSelected(), false, simThre, multithreadingFlag);
            }

            long endTime = System.currentTimeMillis();
            System.out.println("所有文档相似度计算完成，共耗时：" + (endTime - startTime) / 1000 + "s");
        } catch (Exception ex) {
            System.out.println("计算出错,请检查后重试:" + ex);
        }

    }
}
