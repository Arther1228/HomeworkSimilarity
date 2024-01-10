package pers.hdq.ui;

import pers.hdq.util.Contants;

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
        scrollPane1.setBounds(Contants.getScrollPane1Size());

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
        panel1.setBounds(Contants.getPanel1Pane1Size());

        return panel1;
    }

    /**
     * 初始化下方的TableShowJPanel（包含路径显示、选择按钮、比较项tab、开始比对 jpanel）
     */
    public static JPanel initTableShowJPanel() {

        JPanel tableShowJPanel = new JPanel();
        tableShowJPanel.setToolTipText("");
        tableShowJPanel.setLayout(null);
        tableShowJPanel.setBounds(Contants.getTableShowJPanelSize());

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

        panel2.setBounds(Contants.getPanel2Pane1Size());
        // 将布局改为 GridLayout(6, 1, 0, 3)，将 Excel 和 Word/Txt 放在同一行
        panel2.setLayout(new GridLayout(5, 1, 10, 3));

        JCheckBox wordBox = Contants.getWordBox();
        panel2.add(wordBox);

        JCheckBox picBox = Contants.getPicBox();
//        panel2.add(picBox);

        JPanel radioButtonPanel = Contants.getRadioButtonPanel();
        panel2.add(radioButtonPanel);

        JComboBox comboBox = Contants.getComboBox();
        JComboBox queryModeBox = Contants.getQueryModeBox();
        JComboBox multithreadingBox = Contants.getMultithreadingBox();

        JButton beginButton = new JButton("开始比对");
        beginButton.setForeground(Color.BLACK);
        beginButton.setFont(new Font("仿宋", Font.BOLD, 20));
        beginButton.addMouseListener(new SearchMouseAdapter(uIhdq, wordBox, picBox, comboBox, queryModeBox, multithreadingBox));

        panel2.add(comboBox);
//        panel2.add(queryModeBox);
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
