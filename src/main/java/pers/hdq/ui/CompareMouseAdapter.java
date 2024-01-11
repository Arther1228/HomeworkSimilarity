package pers.hdq.ui;

import pers.hdq.function.CompareOptimize;
import pers.hdq.util.ThresholdUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author yangliangchuang 2024-01-10 11:06
 */
public class CompareMouseAdapter extends MouseAdapter {

    private int index = 1;

    private UIhdq uIhdq;

    private JCheckBox wordBox;

    private JCheckBox picBox;

    private JComboBox comboBox;

    private JComboBox queryModeBox;

    private JComboBox multithreadingBox;

    public CompareMouseAdapter(UIhdq uIhdq, JCheckBox wordBox, JCheckBox picBox, JComboBox comboBox, JComboBox queryModeBox, JComboBox multithreadingBox) {
        this.uIhdq = uIhdq;
        this.wordBox = wordBox;
        this.picBox = picBox;
        this.comboBox = comboBox;
        this.queryModeBox = queryModeBox;
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

        //Excel比较项
        List<CompareItemPanel> compareItemPanels = UiTabbedPane.getCompareItemPanels();

        docLocationTextArea.setText("开始处理：\n");
        docLocationTextArea.paintImmediately(docLocationTextArea.getBounds());
        if (index % 2 == 0) {
            docLocationTextArea.setForeground(Color.BLACK);
        } else {
            docLocationTextArea.setForeground(Color.magenta);
        }
        index++;

        //获取相似度阈值
        String threshold = (String) comboBox.getSelectedItem();
        Double simThre = ThresholdUtil.getSimThre(threshold);

        long startTime = System.currentTimeMillis();
        //是否开启多线程
        boolean multithreadingFlag = "2.多线程".equals(multithreadingBox.getSelectedItem());
        String excelPath = path + "\\相似度比对结果".concat("智能分词-" + "图片相似度比对-" + queryModeBox.getSelectedItem()).concat(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())).concat(".xlsx");
        try {
            switch ((String) queryModeBox.getSelectedItem()) {
                case "模式2今年与往年":
                    CompareOptimize.getSimilarityMode2(path, wordBox.isSelected(), picBox.isSelected(), simThre, excelPath, multithreadingFlag);
                    break;
                default:
                    CompareOptimize.getSimilarityMode1(path, wordBox.isSelected(), picBox.isSelected(), simThre, excelPath, multithreadingFlag);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("所有文档相似度计算完成，共耗时：" + (endTime - startTime) / 1000 + "s");
        } catch (Exception ex) {
            System.out.println("计算出错,请检查后重试:" + ex);
        }

    }
}
