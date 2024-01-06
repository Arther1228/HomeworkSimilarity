package pers.hdq.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author yangliangchuang 2024-01-06 10:25
 */
public class UiUtil {

    /**
     *  初始化使用说明
     */
    public static JTextPane createJTextPane(){

        JTextPane txtpnrnrncsvexcelrn = new JTextPane();
        txtpnrnrncsvexcelrn.setBackground(SystemColor.controlHighlight);
        txtpnrnrncsvexcelrn.setForeground(Color.BLACK);
        txtpnrnrncsvexcelrn.setFont(new Font("仿宋", Font.BOLD, 16));
        txtpnrnrncsvexcelrn.setEditable(false);
        txtpnrnrncsvexcelrn.setText("使用说明：\r\n  1.相似度比对前请将待相似度比对文档放入文件夹中，然后点击“选择比对路径”按钮选择该文件夹，点击“开始相似度比对”按钮，开始比对；" +
                "\n  2.相似度比对模式1:将对所选路径下所有文档两两比较;  " +
                "\n  3.相似度比对结果存储于所选文件夹中以“相似度比对结果”开头的Excel表格中；" +
                "\n  4.“简略结果”表列出每个文件及其最相似文件，详细结果表列出全部结果；超过相似度阈值名单会列出相似度超过在选定阈值的文件名。");
        txtpnrnrncsvexcelrn.setToolTipText("使用说明");
        txtpnrnrncsvexcelrn.setBounds(10, 55, 759, 300);

        return txtpnrnrncsvexcelrn;
    }


    public static void showTextPane(JTextPane txtpnrnrncsvexcelrn, boolean flag){

        txtpnrnrncsvexcelrn.setVisible(flag);
    }

    public static Dimension computePreferredSize(JPanel panel){

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
