package pers.hdq.ui;

import pers.hdq.model.ExcelCompareItem;
import pers.hdq.util.Contants;
import pers.hdq.util.EasyExcelReadUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * @author yangliangchuang 2024-01-09 16:20
 */
public class UiTabbedPane {


    private static JPanel dynamicPanelContainer;

    /**
     * 比较项列表
     */
    private static List<CompareItemPanel> compareItemPanels = new ArrayList<>();

    /**
     * sheet:文件列表
     */
    private static Map<String, List<String>> allExcelSheetListWithFileNames = new HashMap<>();

    /**
     * 文件全名：全有列
     */
    private static Map<String, List<String>> allColumnNamesByFileNameAndSheet = new HashMap<>();


    /**
     * 键为"sheet名-列名"，值为存在文件列表
     */
    private static Map<String, List<String>> allSheetAndColumnNamesWithFiles = new HashMap<>();

    public static Map<String, List<String>> getAllExcelSheetListWithFileNames() {
        return allExcelSheetListWithFileNames;
    }

    public static Map<String, List<String>> getAllFileNameWithColumns() {
        return allColumnNamesByFileNameAndSheet;
    }

    public static Map<String, List<String>> getAllSheetAndColumnNamesWithFiles() {
        return allSheetAndColumnNamesWithFiles;
    }

    /**
     * 初始化标签页
     */
    public static JTabbedPane initJTabbedPane(UIhdq uIhdq) {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(Contants.getTabbedPaneSize());

        JPanel excelComparisonPanel = createExcelComparisonPanel(uIhdq);
        tabbedPane.addTab("Excel文件比较项设置", null, excelComparisonPanel, "设置比较项");
        JTextPane jDescriptionPane = initJTextPane();
        tabbedPane.addTab("比较说明", null, jDescriptionPane, "说明内容");

        //jDescriptionPane.setBorder(BorderFactory.createLineBorder(Color.blue, 2));

        return tabbedPane;
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
                "\n  4.Word/Txt文件：目前只支持整个文件两两比较，不支持选择文本段落。" +
                "\n  5.Excel文件：可以设置“比较项”，即Excel文件的工作簿（sheet）、列、行。每次添加一个“比较项”，将两两比较所选文件夹中的Excel文件中该“比较项”的文本。" +
                "\n  6.Excel文件比较注意：（1）如果“比较项”只有一个Excel文件中有，则不会进行比较。"+
                "\n  7.Excel文件比较注意：（2）如果在同一个Excel中存在同名工作簿、同名列，比较结果可能不准确，请尽量避免。");
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
        uIhdq.setAddButton(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (uIhdq.getPath() == null || uIhdq.getPath().isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "请先选择比对路径", "提示", JOptionPane.WARNING_MESSAGE);
                } else {
                    addDynamicPanel();
                }
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
    private static void addDynamicPanel() {
        CompareItemPanel compareItemPanel = new CompareItemPanel();
        compareItemPanels.add(compareItemPanel);

        dynamicPanelContainer.add(compareItemPanel);
        dynamicPanelContainer.revalidate();
        dynamicPanelContainer.repaint();
    }


    /**
     * 移除比较项
     *
     * @param panel
     */
    public static void removeDynamicPanel(CompareItemPanel panel) {
        compareItemPanels.remove(panel);

        dynamicPanelContainer.remove(panel);
        dynamicPanelContainer.revalidate();
        dynamicPanelContainer.repaint();
    }

    /**
     * 移除所有比较项
     */
    public static void removeAllDynamicPanels() {
        compareItemPanels.clear();

        dynamicPanelContainer.removeAll();
        dynamicPanelContainer.revalidate();
        dynamicPanelContainer.repaint();
    }


    /**
     * 初始化Excel内容
     *
     * @param path
     */
    public static boolean initExcelContent(String path) {

        EasyExcelReadUtil easyExcelReadUtil = new EasyExcelReadUtil(path);

        allExcelSheetListWithFileNames = easyExcelReadUtil.getAllExcelSheetListWithFileNames();
        allColumnNamesByFileNameAndSheet = easyExcelReadUtil.getAllColumnNamesByFileNameAndSheet();
        allSheetAndColumnNamesWithFiles = easyExcelReadUtil.getAllSheetAndColumnNamesWithFiles();

        return true;
    }

    /**
     * 获取比较项内容
     *
     * @return
     */
    public static Set<ExcelCompareItem> getExcelCompareItemList() {
        Set<ExcelCompareItem> excelCompareItemList = new HashSet<>();

        for (CompareItemPanel compareItemPanel : compareItemPanels) {
            String sheetName = compareItemPanel.getSheetComboBox().getSelectedItem().toString();
            String columnName = compareItemPanel.getColumnComboBox().getSelectedItem().toString();
            String lineNumber = compareItemPanel.getFilterTextField().getText();

            ExcelCompareItem excelCompareItem = ExcelCompareItem.builder()
                    .sheetName(sheetName).columnName(columnName).lineNumber(lineNumber).build();

            excelCompareItemList.add(excelCompareItem);
        }

        return excelCompareItemList;

    }


    /**
     * 根据比较项的 sheet + column 找到对应的excel路径
     *
     * @return
     */
    public static Map<ExcelCompareItem, List<String>> getExcelCompareItemAndExcelList() {
        Map<ExcelCompareItem, List<String>> excelCompareItemStringMap = new HashMap<>();

        for (CompareItemPanel compareItemPanel : compareItemPanels) {
            String sheetName = compareItemPanel.getSheetComboBox().getSelectedItem().toString();
            String columnName = compareItemPanel.getColumnComboBox().getSelectedItem().toString();
            String lineNumber = compareItemPanel.getFilterTextField().getText();

            String key = sheetName + "-" + columnName;
            List<String> filePathList = allSheetAndColumnNamesWithFiles.get(key);

            ExcelCompareItem excelCompareItem = ExcelCompareItem.builder()
                    .sheetName(sheetName).columnName(columnName).lineNumber(lineNumber).build();

            excelCompareItemStringMap.put(excelCompareItem, filePathList);
        }
        return excelCompareItemStringMap;
    }

}
