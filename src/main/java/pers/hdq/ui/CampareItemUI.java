package pers.hdq.ui;

import pers.hdq.util.EasyExcelReadUtil;
import pers.hdq.util.Contants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author yangliangchuang 2024-01-09 16:20
 */
public class CampareItemUI {

    private static List<DynamicPanel> dynamicPanels = new ArrayList<>();

    private static JPanel dynamicPanelContainer;


    /**
     * 初始化标签页
     */
    public static JTabbedPane initJTabbedPane(UIhdq uIhdq) {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(Contants.getTabbedPaneSize());

        JPanel excelComparisonPanel = CampareItemUI.createExcelComparisonPanel(uIhdq);
        tabbedPane.addTab("Excel文件比较", null, excelComparisonPanel, "设置比较项");
        JComponent wordTxt = makeTextPanel("直接选择比较的文件夹，点击开始比较，即可按照默认的两两文件进行比较相似度");
        tabbedPane.addTab("Word/Txt文件比较", null, wordTxt, "");
        JTextPane jDescriptionPane = initJTextPane();
        tabbedPane.addTab("比较说明", null, jDescriptionPane, "说明内容");

        //jDescriptionPane.setBorder(BorderFactory.createLineBorder(Color.blue, 2));

        return tabbedPane;
    }

    /**
     * 生成一个textPanel
     *
     * @param text
     * @return
     */
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
        txtpnrnrncsvexcelrn.setBounds(Contants.getTxtpnrnrncsvexcelrnSize());

        return txtpnrnrncsvexcelrn;
    }

    /**
     * 创建一个Excel文件比较的内容
     *
     * @return
     */
    public static JPanel createExcelComparisonPanel(UIhdq uIhdq) {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel titleLabel = new JLabel("设置Excel文件比较项，用于查找待比较的文本所在位置：");
        titleLabel.setFont(new Font("仿宋", Font.PLAIN, 16));
        titlePanel.add(titleLabel);

        JButton addButton = new JButton("增加");
        titlePanel.add(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDynamicPanel(uIhdq);
            }
        });

        panel.add(titlePanel, BorderLayout.NORTH);

        dynamicPanelContainer = new JPanel();
        dynamicPanelContainer.setLayout(new BoxLayout(dynamicPanelContainer, BoxLayout.Y_AXIS));
        panel.add(dynamicPanelContainer, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 动态添加选项
     */
    private static void addDynamicPanel(UIhdq uIhdq) {
        DynamicPanel dynamicPanel = new DynamicPanel(uIhdq);
        dynamicPanels.add(dynamicPanel);

        dynamicPanelContainer.add(dynamicPanel);
        dynamicPanelContainer.revalidate();
        dynamicPanelContainer.repaint();
    }

    /**
     * 比较项
     */
    private static class DynamicPanel extends JPanel {

        private JComboBox<String> sheetComboBox;
        private JComboBox<String> columnComboBox;

        private UIhdq uIhdq;

        public DynamicPanel( UIhdq uIhdq) {
            this.uIhdq = uIhdq;

            setLayout(new GridLayout(1, 6));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            add(new JLabel("选择工作簿名称:"));

            sheetComboBox = new JComboBox<>(getSheetNames());
            sheetComboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    updateColumnComboBox(sheetComboBox.getSelectedItem().toString());
                }
            });
            add(sheetComboBox);

            add(new JLabel("指定工作簿中的列:"));
            columnComboBox = new JComboBox<>();
            add(columnComboBox);

            JCheckBox filterCheckBox = new JCheckBox("设置行号");
            JTextField filterTextField = new JTextField();
            filterTextField.setEnabled(false);
            filterCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    filterTextField.setEnabled(filterCheckBox.isSelected());
                }
            });

            add(filterCheckBox);
            add(filterTextField);

            JButton removeButton = new JButton("移除当前行");
            removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removeDynamicPanel(DynamicPanel.this);
                }
            });

            add(removeButton);
        }

        /**
         * 获取选择路径下，所有excel的sheet名称列表
         *
         * @return
         */
        private String[] getSheetNames() {

            Set<String> excelSheetList = EasyExcelReadUtil.getExcelSheetList(uIhdq.getPath());
            String[] sheetArray = excelSheetList.toArray(new String[0]);

            return sheetArray;
        }


        /**
         * 根据所选择的sheet名称，查询所有的列名称（可能同sheet名称，列名不一样的情况存在）
         *
         * @param selectedSheet
         */
        private void updateColumnComboBox(String selectedSheet) {
            Set<String> columnNameListBySheet = EasyExcelReadUtil.getColumnNameListBySheet(uIhdq.getPath(), selectedSheet);

            columnComboBox.removeAllItems();
            for (String column : columnNameListBySheet) {
                columnComboBox.addItem(column);
            }
        }

    }

    /**
     * 移除比较项
     *
     * @param panel
     */
    private static void removeDynamicPanel(DynamicPanel panel) {
        dynamicPanels.remove(panel);
        dynamicPanelContainer.remove(panel);
        dynamicPanelContainer.revalidate();
        dynamicPanelContainer.repaint();
    }

}
