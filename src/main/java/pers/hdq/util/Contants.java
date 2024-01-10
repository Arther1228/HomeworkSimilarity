package pers.hdq.util;

import pers.hdq.ui.SearchMouseAdapter;

import javax.swing.*;
import java.awt.*;

/**
 * @author yangliangchuang 2024-01-10 10:18
 */
public class Contants {


    public static Rectangle getFrameSize() {
        return new Rectangle(500, 100, 1000, 800);
    }

    public static Rectangle getPanel1Pane1Size() {
        return new Rectangle(10, 30, 900, 250);
    }

    public static Rectangle getScrollPane1Size() {
        return new Rectangle(10, 30, 950, 320);
    }

    public static Rectangle getTableShowJPanelSize() {
        return new Rectangle(10, 30, 900, 600);
    }

    public static Rectangle getTabbedPaneSize() {
        return new Rectangle(10, 55, 759, 300);
    }

    public static Rectangle getTxtpnrnrncsvexcelrnSize() {
        return new Rectangle(10, 55, 759, 300);
    }


    public static Rectangle getPanel2Pane1Size() {
        return new Rectangle(790, 54, 150, 300);
    }

    public static JCheckBox getWordBox() {

        JCheckBox wordBox = new JCheckBox("打开智能分词");
        wordBox.setFont(new Font("仿宋", Font.PLAIN, 16));
        wordBox.setToolTipText("取消勾选会将每个词语分成最小颗粒。如：“笔记本电脑”=【笔记本电脑, 笔记本, 笔记, 电脑】，提高精度");
        wordBox.setSelected(true);

        return wordBox;
    }

    public static JCheckBox getPicBox() {

        JCheckBox picBox = new JCheckBox("打开图片相似度比对");
        picBox.setFont(new Font("仿宋", Font.PLAIN, 16));
        picBox.setToolTipText("勾选后会对文档中图片进行比较，但会严重降低比较速度，当图片过多时计算会很慢");

        return picBox;
    }

    public static JPanel getRadioButtonPanel() {
        JPanel radioButtonPanel = new JPanel();

        radioButtonPanel.setLayout(new GridLayout(1, 2));
        JRadioButton rb1 = new JRadioButton("Excel");
        rb1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JRadioButton rb2 = new JRadioButton("Word/Txt");
        rb2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ButtonGroup group = new ButtonGroup();

        group.add(rb1);
        group.add(rb2);

        radioButtonPanel.add(rb1);
        radioButtonPanel.add(rb2);

        return radioButtonPanel;
    }


    public static JComboBox getComboBox() {

        JComboBox comboBox = new JComboBox<String>();
        comboBox.setToolTipText("选择相似度阈值");
        comboBox.addItem("选择相似度下限");
        comboBox.addItem("20%");
        comboBox.addItem("30%");
        comboBox.addItem("40%");
        comboBox.addItem("50%");
        comboBox.addItem("60%");
        comboBox.addItem("70%");
        comboBox.addItem("80%");
        comboBox.addItem("90%");
        comboBox.addItem("95%");

        return comboBox;
    }

    public static JComboBox getQueryModeBox() {

        JComboBox queryModeBox = new JComboBox<String>();
        queryModeBox.setToolTipText("相似度比对模式 \n1、所有文档两两比较；");
        queryModeBox.addItem("选择相似度比对模式");
        queryModeBox.addItem("模式1:两两");

        return queryModeBox;
    }


    public static JComboBox getMultithreadingBox() {

        JComboBox multithreadingBox = new JComboBox<String>();
        multithreadingBox.setToolTipText("开启多线程,速度更快;但更耗CPU资源");
        multithreadingBox.addItem("线程模式");
        multithreadingBox.addItem("1.单线程");
        multithreadingBox.addItem("2.多线程");


        return multithreadingBox;
    }

}
