package pers.hdq.ui;

import pers.hdq.util.SizeContants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author yangliangchuang 2024-01-06 10:25
 */
public class UiUtil {


    /**
     * 初始化上方的Panel
     */
    public static JPanel initResultPanel(UIhdq uIhdq) {

        JPanel panel1 = new JPanel();

        panel1.setToolTipText("");
        panel1.setLayout(null);

        // ======== scrollPane1 结果框 ========
        JScrollPane scrollPane1 = new JScrollPane();
        // ---- docLocationTextArea ----
        JTextArea docLocationTextArea = new JTextArea();
        Font x = new Font("仿宋", 0, 15);
        docLocationTextArea.setFont(x);
        docLocationTextArea.setToolTipText("相似度比对结果");
        docLocationTextArea.setEditable(false);
        scrollPane1.setViewportView(docLocationTextArea);
        scrollPane1.setBounds(SizeContants.getScrollPane1Size());

        uIhdq.setDocLocationTextArea(docLocationTextArea);
        //scrollPane1.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));

        panel1.add(scrollPane1);

        // ---- label1 ----
        JLabel label1 = new JLabel();
        label1.setFont(new Font("仿宋", Font.BOLD, 14));
        label1.setToolTipText("");
        label1.setText("比对结果：");
        label1.setBounds(10, 0, 87, 25);

        panel1.add(label1);
        panel1.setBounds(SizeContants.getPanel1Pane1Size());

        return panel1;
    }

    /**
     * 初始化下方的TableShowJPanel（包含路径显示、选择按钮、比较项tab、开始比对 jpanel）
     */
    public static JPanel initTableShowJPanel() {

        JPanel tableShowJPanel = new JPanel();
        tableShowJPanel.setToolTipText("");
        tableShowJPanel.setLayout(null);
        tableShowJPanel.setBounds(SizeContants.getTableShowJPanelSize());

        return tableShowJPanel;
    }

    /**
     * 初始化 选择目录和查询按钮
     *
     * @param tableShowJPanel
     */
    public static void initTextPathAndSearchButton(UIhdq uIhdq, JPanel tableShowJPanel) {

        // -- label --
        JLabel label = new JLabel("您选择的比对路径是：");
        label.setFont(new Font("仿宋", Font.PLAIN, 14));
        label.setBounds(10, 8, 145, 29);
        tableShowJPanel.add(label);

        //--textPath--
        JTextField textPath = new JTextField();
        textPath.setFont(new Font("仿宋", Font.PLAIN, 16));
        textPath.setBackground(SystemColor.menu);
        textPath.setEditable(false);
        textPath.setBounds(145, 8, 625, 32);
        textPath.setColumns(10);

        tableShowJPanel.add(textPath);

        // ---- searchButton ----
        JButton searchButton = new JButton();
        searchButton.setText("选择比对路径");
        searchButton.setBounds(798, 14, 145, 33);
        searchButton.setFont(new Font("仿宋", Font.BOLD, 16));
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 自动生成的方法存根
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = jfc.getSelectedFile();
                    if (file != null) {
                        String tempPath = file.getPath();
                        textPath.setText(tempPath);
                        uIhdq.setPath(tempPath);
                    }
                }
            }
        });
        tableShowJPanel.add(searchButton);
    }

    /**
     * 初始化下方右侧的Panel
     */
    public static JPanel initPanel2(UIhdq uIhdq) {
        JPanel panel2 = new JPanel();

        panel2.setBounds(SizeContants.getPanel2Pane1Size());

        panel2.setLayout(new GridLayout(6, 1, 0, 3));
        JCheckBox wordBox = new JCheckBox("打开智能分词");
        wordBox.setFont(new Font("仿宋", Font.PLAIN, 16));
        wordBox.setToolTipText("取消勾选会将每个词语分成最小颗粒。如：“笔记本电脑”=【笔记本电脑, 笔记本, 笔记, 电脑】，提高精度");
        wordBox.setSelected(true);
        panel2.add(wordBox);

        JCheckBox picBox = new JCheckBox("打开图片相似度比对");
        picBox.setFont(new Font("仿宋", Font.PLAIN, 16));
        picBox.setToolTipText("勾选后会对文档中图片进行比较，但会严重降低比较速度，当图片过多时计算会很慢");
        panel2.add(picBox);
        // sortBox = new JCheckBox("打开排序输出");
        // sortBox.setFont(new Font("仿宋", Font.PLAIN, 16));
        // sortBox.setToolTipText("勾选后输出相似度比对结果会按相似度降序排序！会增加运算时间,不建议勾选");
        // panel2.add(sortBox);

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

        JComboBox queryModeBox = new JComboBox<String>();
        queryModeBox.setToolTipText("相似度比对模式 \n1、所有文档两两比较；");
        queryModeBox.addItem("选择相似度比对模式");
        queryModeBox.addItem("模式1:两两");

        JComboBox multithreadingBox = new JComboBox<String>();
        multithreadingBox.setToolTipText("开启多线程,速度更快;但更耗CPU资源");
        multithreadingBox.addItem("线程模式");
        multithreadingBox.addItem("1.单线程");
        multithreadingBox.addItem("2.多线程");

        JButton beginButton = new JButton("开始比对");
        beginButton.setForeground(Color.BLACK);
        beginButton.setFont(new Font("仿宋", Font.BOLD, 20));
        beginButton.addMouseListener(new SearchMouseAdapter(uIhdq, wordBox, picBox, comboBox, queryModeBox, multithreadingBox));

        panel2.add(comboBox);
        panel2.add(queryModeBox);
        panel2.add(multithreadingBox);

        panel2.add(beginButton);
        return panel2;
    }

    /**
     * 控制台重定向
     *
     * @param
     */
    public static void redirectConsole(UIhdq uIhdq) {
        JTextArea docLocationTextArea = uIhdq.getDocLocationTextArea();

        OutputStream textAreaStream = new OutputStream() {
            @Override
            public void write(int b) {
                docLocationTextArea.append(String.valueOf((char) b));
                docLocationTextArea.paintImmediately(docLocationTextArea.getBounds());// 实时输出
            }

            @Override
            public void write(byte b[]) {
                docLocationTextArea.append(new String(b));
                docLocationTextArea.paintImmediately(docLocationTextArea.getBounds());// 实时输出
            }

            @Override
            public void write(byte b[], int off, int len) {
                docLocationTextArea.append(new String(b, off, len));
                docLocationTextArea.paintImmediately(docLocationTextArea.getBounds());// 实时输出
            }
        };
        PrintStream myOut = new PrintStream(textAreaStream);
        System.setOut(myOut);
        System.setErr(myOut);
    }


    /**
     * 计算窗体大小
     *
     * @param panel
     * @return
     */
    public static Dimension computePreferredSize(JPanel panel) {

        // compute preferred size
        Dimension preferredSize = new Dimension();
        for (int i = 0; i < panel.getComponentCount(); i++) {
            Rectangle bounds = panel.getComponent(i).getBounds();
            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
        }
        Insets insets = panel.getInsets();
        preferredSize.width += insets.right;
        preferredSize.height += insets.bottom;

        return preferredSize;
    }
}
