package pers.hdq.ui;

import pers.hdq.util.Contants;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

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


    public UIhdq() {

        initFrameStyle();

        // ======== panel1 ========
        JPanel panel1 = UiUtil.initResultPanel(this);
        add(panel1);

        // ======== tableShowJPanel ========
        JPanel tableShowJPanel = UiUtil.initTableShowJPanel();
        add(tableShowJPanel);

        //search button
        UiUtil.initTextPathAndSearchButton(this, tableShowJPanel);

        // campare tabbedPane
        JTabbedPane tabbedPane = CampareItemUI.initJTabbedPane();
        tableShowJPanel.add(tabbedPane);

        // ======== panel2 ========
        JPanel panel2 = UiUtil.initPanel2(this);
        tableShowJPanel.add(panel2);

        // ===== show border
/*        panel1.setBorder(BorderFactory.createLineBorder(Color.red, 2));
        tableShowJPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        panel2.setBorder(BorderFactory.createLineBorder(Color.green, 2));*/
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


    public static void main(String args[]) {
        try {
            JFrame frame = new JFrame("本地文档相似度比对系统");

            // 初始界面大小
            frame.setBounds(Contants.getFrameSize());
            UIhdq uIhdq = new UIhdq();
            frame.getContentPane().add(uIhdq, BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            UiUtil.redirectConsole(uIhdq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
