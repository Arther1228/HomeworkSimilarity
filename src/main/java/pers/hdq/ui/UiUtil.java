package pers.hdq.ui;

import pers.hdq.util.Contants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author yangliangchuang 2024-01-06 10:25
 */
public class UiUtil {


    /**
     * 初始化上方的Panel
     */
    public static JPanel initResultPanel(UIhdq uIhdq) {
        JPanel resultPanel = new JPanel();

        resultPanel.setToolTipText("");
        resultPanel.setLayout(null);

        // ======== scrollPane1 结果框 ========
        JScrollPane scrollPane1 = new JScrollPane();
        // ---- docLocationTextArea ----
        JTextArea docLocationTextArea = new JTextArea();
        Font x = new Font("仿宋", 0, 15);
        docLocationTextArea.setFont(x);
        docLocationTextArea.setToolTipText("相似度比对结果");
        docLocationTextArea.setEditable(false);
        scrollPane1.setViewportView(docLocationTextArea);
        scrollPane1.setBounds(Contants.getScrollPane1Size());

        uIhdq.setDocLocationTextArea(docLocationTextArea);
        //scrollPane1.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));

        resultPanel.add(scrollPane1);

        // ---- label1 ----
        JLabel label1 = new JLabel();
        label1.setFont(new Font("仿宋", Font.BOLD, 14));
        label1.setToolTipText("");
        label1.setText("比对结果：");
        label1.setBounds(10, 0, 87, 25);

        resultPanel.add(label1);
        resultPanel.setBounds(Contants.getResultPanelPane1Size());

        return resultPanel;
    }

    /**
     * 初始化下方的compareJPanel（包含路径显示、选择按钮、比较项tab、开始比对 jpanel）
     */
    public static JPanel initCompareJPanel() {

        JPanel compareJPanel = new JPanel();
        compareJPanel.setToolTipText("");
        compareJPanel.setLayout(null);
        compareJPanel.setBounds(Contants.getComparePanelSize());

        return compareJPanel;
    }

    /**
     * 初始化 选择目录和查询按钮
     *
     * @param compareJPanel
     */
    public static void initTextPathAndSearchButton(UIhdq uIhdq, JPanel compareJPanel) {

        // -- label --
        JLabel label = new JLabel("您选择的比对路径是：");
        label.setFont(new Font("仿宋", Font.PLAIN, 14));
        label.setBounds(10, 8, 145, 29);
        compareJPanel.add(label);

        //--textPath--
        JTextField textPath = new JTextField();
        textPath.setFont(new Font("仿宋", Font.PLAIN, 16));
        textPath.setBackground(SystemColor.menu);
        textPath.setEditable(false);
        textPath.setBounds(145, 8, 625, 32);
        textPath.setColumns(10);

        compareJPanel.add(textPath);

        //方便测试
        textPath.setText("C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123");
        uIhdq.setPath("C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123");
        UiTabbedPane.initExcelContent("C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123");

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
                        UiTabbedPane.initExcelContent(tempPath);
                    }
                }
            }
        });
        compareJPanel.add(searchButton);
    }

    /**
     * 初始化下方右侧的Panel
     */
    public static JPanel initStartComparePanel(UIhdq uIhdq) {
        JPanel startComparePanel = new JPanel();

        startComparePanel.setBounds(Contants.getPanel2Pane1Size());
        // 将布局改为 GridLayout(5, 1, 10, 3)，将 Excel 和 Word/Txt 放在同一行
        startComparePanel.setLayout(new GridLayout(5, 1, 10, 3));

        JCheckBox wordBox = Contants.getWordBox();
        startComparePanel.add(wordBox);

        JCheckBox picBox = Contants.getPicBox();
//        startComparePanel.add(picBox);

        JPanel radioButtonPanel = Contants.getRadioButtonPanel(uIhdq);
        startComparePanel.add(radioButtonPanel);

        JComboBox comboBox = Contants.getComboBox();
        JComboBox queryModeBox = Contants.getQueryModeBox();
        JComboBox multithreadingBox = Contants.getMultithreadingBox();

        JButton beginButton = new JButton("开始比对");
        beginButton.setForeground(Color.BLACK);
        beginButton.setFont(new Font("仿宋", Font.BOLD, 20));
        beginButton.addMouseListener(new CompareMouseAdapter(uIhdq, wordBox, picBox, comboBox, queryModeBox, multithreadingBox));

        startComparePanel.add(comboBox);
//        startComparePanel.add(queryModeBox);
        startComparePanel.add(multithreadingBox);

        startComparePanel.add(beginButton);
        return startComparePanel;
    }

}
