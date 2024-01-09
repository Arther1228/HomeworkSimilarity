package pers.hdq.ui;

import pers.hdq.function.CompareOptimize;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author HuDaoquan
 * @version v1.0
 * @ClassName: UIhdq
 * @Description: 程序可视化界面设计
 * @date 2019年7月1日 下午9:46:52
 */
public class UIhdq extends JPanel {
    private static final long serialVersionUID = 1289965392854758573L;
    private String path = "";
    private static JTextArea docLocationTextArea;
    private JCheckBox wordBox;
    private JCheckBox picBox;
    // private JCheckBox sortBox;
    private static JTextField textPath;

    private Double simThre;
    private JComboBox<String> comboBox;
    // 相似度比对模式 1、所有文档两两比较；2、今年与往年比较（要求所选路径中必须有一个”今年“文件夹、一个“往年”文件夹）
    private JComboBox<String> queryModeBox;
    private JComboBox<String> multithreadingBox;

    public UIhdq() {
        initComponents();
    }

    private void initComponents() {

        initFrameStyle();

        // ======== panel1 ========
        JPanel panel1 = new JPanel();
        initResultPanel(panel1);

        // ======== tableShowJPanel ========
        JPanel tableShowJPanel = new JPanel();
        initTableShowJPanel(tableShowJPanel);

        //search button
        initTextPathAndSearchButton(tableShowJPanel);

        // textPane
        JTabbedPane tabbedPane = UiUtil.initJTabbedPane();
        tableShowJPanel.add(tabbedPane);

        // ======== panel2 ========
        JPanel panel2 = new JPanel();
        initPanel2(panel2);
        tableShowJPanel.add(panel2);

        //===== show border
//        panel1.setBorder(BorderFactory.createLineBorder(Color.red, 2));
//        tableShowJPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
//        panel2.setBorder(BorderFactory.createLineBorder(Color.green, 2));

    }


    /**
     * 初始化页面样式
     */
    private void initFrameStyle() {

        Border border = new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(
                new javax.swing.border.EmptyBorder(0, 0, 0, 0), "",
                javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BOTTOM,
                new java.awt.Font("仿宋", java.awt.Font.BOLD, 15), java.awt.Color.red), getBorder());
        setBorder(border);
        addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent e) {
                if ("border".equals(e.getPropertyName())) {
                    throw new RuntimeException();
                }
            }
        });

        setLayout(new GridLayout(2, 1, 0, 5));
    }

    /**
     * 初始化上方的Panel
     */
    private void initResultPanel(JPanel panel1) {

        panel1.setToolTipText("");
        panel1.setLayout(null);

        // ======== scrollPane1 结果框 ========
        JScrollPane scrollPane1 = new JScrollPane();
        // ---- docLocationTextArea ----
        docLocationTextArea = new JTextArea();
        Font x = new Font("仿宋", 0, 15);
        docLocationTextArea.setFont(x);
        docLocationTextArea.setToolTipText("相似度比对结果");
        docLocationTextArea.setEditable(false);
        scrollPane1.setViewportView(docLocationTextArea);
        scrollPane1.setBounds(10, 30, 950, 320);

//        scrollPane1.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));

        panel1.add(scrollPane1);

        // ---- label1 ----
        JLabel label1 = new JLabel();
        label1.setFont(new Font("仿宋", Font.BOLD, 14));
        label1.setToolTipText("");
        label1.setText("比对结果：");
        label1.setBounds(10, 0, 87, 25);

        panel1.add(label1);

//        Dimension preferredSize = UiUtil.computePreferredSize(panel1);
//        panel1.setMinimumSize(preferredSize);
//        panel1.setPreferredSize(preferredSize);

        panel1.setBounds(10, 30, 900, 250);

        add(panel1);
    }

    /**
     * 初始化下方的TableShowJPanel
     *
     * @param tableShowJPanel
     */
    private void initTableShowJPanel(JPanel tableShowJPanel) {
        tableShowJPanel.setToolTipText("");
        tableShowJPanel.setLayout(null);

//        Dimension preferredSize = UiUtil.computePreferredSize(tableShowJPanel);
//        tableShowJPanel.setMinimumSize(preferredSize);
//        tableShowJPanel.setPreferredSize(preferredSize);

        tableShowJPanel.setBounds(10, 30, 900, 600);

        add(tableShowJPanel);
    }

    /**
     * 初始化 选择目录和查询按钮
     *
     * @param tableShowJPanel
     */
    private void initTextPathAndSearchButton(JPanel tableShowJPanel) {

        // -- label --
        JLabel label = new JLabel("您选择的比对路径是：");
        label.setFont(new Font("仿宋", Font.PLAIN, 14));
        label.setBounds(10, 8, 145, 29);
        tableShowJPanel.add(label);

        //--textPath--
        textPath = new JTextField();
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
                        path = file.getPath();
                        textPath.setText(path);
                    }
                }
            }
        });
        tableShowJPanel.add(searchButton);
    }


    /**
     * 初始化下方右侧的Panel
     *
     * @param panel2
     */
    private void initPanel2(JPanel panel2) {

        panel2.setBounds(808, 54, 132, 300);

        {
            panel2.setLayout(new GridLayout(6, 1, 0, 3));
            wordBox = new JCheckBox("打开智能分词");
            wordBox.setFont(new Font("仿宋", Font.PLAIN, 16));
            wordBox.setToolTipText("取消勾选会将每个词语分成最小颗粒。如：“笔记本电脑”=【笔记本电脑, 笔记本, 笔记, 电脑】，提高精度");
            wordBox.setSelected(true);
            panel2.add(wordBox);
            picBox = new JCheckBox("打开图片相似度比对");
            picBox.setFont(new Font("仿宋", Font.PLAIN, 16));
            picBox.setToolTipText("勾选后会对文档中图片进行比较，但会严重降低比较速度，当图片过多时计算会很慢");
            panel2.add(picBox);
            // sortBox = new JCheckBox("打开排序输出");
            // sortBox.setFont(new Font("仿宋", Font.PLAIN, 16));
            // sortBox.setToolTipText("勾选后输出相似度比对结果会按相似度降序排序！会增加运算时间,不建议勾选");
            // panel2.add(sortBox);
        }
        comboBox = new JComboBox<String>();
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

        queryModeBox = new JComboBox<String>();
        queryModeBox.setToolTipText("相似度比对模式 \n1、所有文档两两比较；");
//        queryModeBox.setToolTipText("相似度比对模式 \n1、所有文档两两比较；\n2、今年与往年比较（所选路径中必须有一个”今年“文件夹、一个“往年”文件夹）");
        queryModeBox.addItem("选择相似度比对模式");
        queryModeBox.addItem("模式1两两");
//        queryModeBox.addItem("模式2今年与往年");

        multithreadingBox = new JComboBox<String>();
        multithreadingBox.setToolTipText("开启多线程,速度更快;但更耗CPU资源");
        multithreadingBox.addItem("线程模式");
        multithreadingBox.addItem("1.单线程");
        multithreadingBox.addItem("2.多线程");

        JButton beginButton = new JButton("开始比对");
        beginButton.setForeground(Color.BLACK);
        beginButton.setFont(new Font("仿宋", Font.BOLD, 20));
        beginButton.addMouseListener(new MouseAdapter() {
            int index = 1;

            @Override
            public void mouseClicked(MouseEvent e) {
                docLocationTextArea.setText("开始处理：\n");
                docLocationTextArea.paintImmediately(docLocationTextArea.getBounds());
                if (index % 2 == 0) {
                    docLocationTextArea.setForeground(Color.BLACK); // 黑色
                } else {
                    docLocationTextArea.setForeground(Color.magenta); // 紫色
                }
                index++;

                //获取相似度阈值
                String threshold = (String) comboBox.getSelectedItem();
                switch (threshold) {
                    case "20%":
                        simThre = 0.2;
                        break;
                    case "30%":
                        simThre = 0.3;
                        break;
                    case "40%":
                        simThre = 0.4;
                        break;
                    case "50%":
                        simThre = 0.5;
                        break;
                    case "60%":
                        simThre = 0.6;
                        break;
                    case "70%":
                        simThre = 0.7;
                        break;
                    case "80%":
                        simThre = 0.8;
                        break;
                    case "90%":
                        simThre = 0.9;
                        break;
                    case "95%":
                        simThre = 0.95;
                        break;
                    default:
                        simThre = 0.90;
                }
                long startTime = System.currentTimeMillis(); // 获取开始时间
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
                    long endTime = System.currentTimeMillis(); // 获取结束时间
                    System.out.println("所有文档相似度计算完成，共耗时：" + (endTime - startTime) / 1000 + "s"); // 输出程序运行时间
                } catch (Exception ex) {
                    System.out.println("计算出错,请检查后重试:" + ex);
                }


            }
        });
        panel2.add(comboBox);
        panel2.add(queryModeBox);
        panel2.add(multithreadingBox);
        panel2.add(beginButton);
    }

    /**
     * 控制台重定向
     *
     * @param
     */
    public static void redirectConsole() {
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

    public static void main(String args[]) {
        redirectConsole();
        try {
            JFrame frame = new JFrame("本地文档相似度比对系统");
            // 初始界面大小
            frame.setBounds(500, 100, 1000, 800);
            frame.getContentPane().add(new UIhdq(), BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
