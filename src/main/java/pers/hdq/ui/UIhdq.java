package pers.hdq.ui;

import pers.hdq.util.Contants;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author HuDaoquan
 * @version v1.0
 * @ClassName: UIhdq
 * @Description: 程序可视化界面设计
 * @date 2019年7月1日 下午9:46:52
 */
public class UIhdq extends JPanel {

    private static final long serialVersionUID = 1289965392854758573L;

    /**
     * 选择的路径
     */
    private String path = "";

    /**
     * 结果输出文本域
     */
    private JTextArea docLocationTextArea;

    /**
     * Excel比较tab页 添加按钮
     */
    private JButton addButton;

    private String compareType;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public JTextArea getDocLocationTextArea() {
        return docLocationTextArea;
    }

    public void setDocLocationTextArea(JTextArea docLocationTextArea) {
        this.docLocationTextArea = docLocationTextArea;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public void setAddButton(JButton addButton) {
        this.addButton = addButton;
    }

    public String getCompareType() {
        return compareType;
    }

    public void setCompareType(String compareType) {
        this.compareType = compareType;
    }

    public UIhdq() {

        initFrameStyle();

        // ======== resultPanel ========
        JPanel resultPanel = UiUtil.initResultPanel(this);
        add(resultPanel);

        // ======== compareJPanel ========
        JPanel compareJPanel = UiUtil.initCompareJPanel();
        add(compareJPanel);

        //search button
        UiUtil.initTextPathAndSearchButton(this, compareJPanel);

        // compare tabbedPane
        JTabbedPane tabbedPane = UiTabbedPane.initJTabbedPane(this);
        compareJPanel.add(tabbedPane);

        // ======== startComparePanel ========
        JPanel startComparePanel = UiUtil.initStartComparePanel(this);
        compareJPanel.add(startComparePanel);

        // ===== show border
/*        resultPanel.setBorder(BorderFactory.createLineBorder(Color.red, 2));
        compareJPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        startComparePanel.setBorder(BorderFactory.createLineBorder(Color.green, 2));*/
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


    public static void main(String args[]) {
        try {
            JFrame frame = new JFrame("本地文档相似度比对系统");

            // 初始界面大小
            frame.setBounds(Contants.getFrameSize());
            UIhdq uIhdq = new UIhdq();
            frame.getContentPane().add(uIhdq, BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            redirectConsole(uIhdq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
