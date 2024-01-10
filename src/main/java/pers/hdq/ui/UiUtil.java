package pers.hdq.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author yangliangchuang 2024-01-06 10:25
 */
public class UiUtil {

    /**
     * 初始化标签页
     */
    public static JTabbedPane initJTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(10, 55, 759, 300);

        JPanel excelComparisonPanel = CampareItemUI.createExcelComparisonPanel();
        tabbedPane.addTab("Excel文件比较", null, excelComparisonPanel, "设置比较项");
        JComponent wordTxt = makeTextPanel("直接选择比较的文件，点击开始比较，即可按照默认的两两文件进行比较相似度");
        tabbedPane.addTab("Word/Txt文件比较", null, wordTxt, "");
        JTextPane jDescriptionPane = UiUtil.initJTextPane();
        tabbedPane.addTab("比较说明", null, jDescriptionPane, "说明内容");

        //        jDescriptionPane.setBorder(BorderFactory.createLineBorder(Color.blue, 2));

        return tabbedPane;
    }

    public static JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        filler.setFont(new Font("仿宋", Font.PLAIN, 16));
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);

        return panel;
    }

    /**
     * 初始化使用说明
     */
    public static JTextPane initJTextPane() {

        JTextPane txtpnrnrncsvexcelrn = new JTextPane();
        txtpnrnrncsvexcelrn.setBackground(SystemColor.controlHighlight);
        txtpnrnrncsvexcelrn.setForeground(Color.BLACK);
        txtpnrnrncsvexcelrn.setFont(new Font("仿宋", Font.PLAIN, 16));
        txtpnrnrncsvexcelrn.setEditable(false);
        txtpnrnrncsvexcelrn.setText("使用说明：\r\n  1.相似度比对前请将待相似度比对文档放入文件夹中，然后点击“选择比对路径”按钮选择该文件夹，点击“开始相似度比对”按钮，开始比对；" +
                "\n  2.相似度比对结果存储于所选文件夹中以“相似度比对结果”开头的Excel表格中；" +
                "\n  3.“简略结果”表列出每个文件及其最相似文件，详细结果表列出全部结果；超过相似度阈值名单会列出相似度超过在选定阈值的文件名。" +
                "\n  4.Word/txt文件：目前只支持整个文件两两比较，不支持选择文本段落。" +
                "\n  5.Excel文件：可以设置“比较项”，即Excel文件的工作簿（sheet）、列、行。每次添加一个“比较项”，将两两比较所选文件夹中的Excel文件" +
                "中该“比较项”的文本。如果“比较项”中只有一个Excel文件中有，则不会进行比较。");
        txtpnrnrncsvexcelrn.setToolTipText("使用说明");
        txtpnrnrncsvexcelrn.setBounds(10, 55, 759, 300);

        return txtpnrnrncsvexcelrn;
    }

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
