package pers.hdq.ui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;

/**
 * 比较项
 */
public class CompareItemPanel extends JPanel {

    // sheet
    private JComboBox<String> sheetComboBox;

    // column
    private JComboBox<String> columnComboBox;

    // row
    private JTextField filterTextField;

    public CompareItemPanel() {

        setLayout(new GridLayout(1, 6));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        add(new JLabel("选择工作簿名称:"));
        sheetComboBox = new JComboBox<>(getSheetNames());
        sheetComboBox.setFont(new Font("仿宋", Font.PLAIN, 16));
        sheetComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateColumnComboBox(sheetComboBox.getSelectedItem().toString());
            }
        });
        add(sheetComboBox);

        add(new JLabel("指定工作簿中的列:"));
        columnComboBox = new JComboBox<>();
        columnComboBox.setFont(new Font("仿宋", Font.PLAIN, 16));
        add(columnComboBox);

        if (sheetComboBox.getItemCount() > 0) {
            sheetComboBox.setSelectedIndex(0);
            updateColumnComboBox(sheetComboBox.getSelectedItem().toString());
        }

        JCheckBox filterCheckBox = new JCheckBox("设置行号");
        filterTextField = new JTextField();
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
                UiTabbedPane.removeDynamicPanel(CompareItemPanel.this);
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
        Map<String, List<String>> allSheetWithFileNames = UiTabbedPane.getAllExcelSheetListWithFileNames();

        Set<String> excelSheetList = allSheetWithFileNames.keySet();
        String[] sheetArray = excelSheetList.toArray(new String[0]);

        return sheetArray;
    }


    /**
     * 根据所选择的sheet名称，查询所有的列名称（可能同sheet名称，列名不一样的情况存在）
     *
     * @param selectedSheet
     */
    private void updateColumnComboBox(String selectedSheet) {

        Map<String, List<String>> allSheetWithFileNames = UiTabbedPane.getAllExcelSheetListWithFileNames();
        Map<String, List<String>> allFileNameWithColumns = UiTabbedPane.getAllFileNameWithColumns();

        Set<String> columnNameListBySheet = new HashSet<>();
        List<String> fileNameList = allSheetWithFileNames.get(selectedSheet);

        for (String fileName : fileNameList) {
            String key = fileName + "-" + selectedSheet;
            List<String> columnNameListBySheet1 = allFileNameWithColumns.get(key);
            columnNameListBySheet.addAll(columnNameListBySheet1);
        }
        columnComboBox.removeAllItems();
        for (String column : columnNameListBySheet) {
            columnComboBox.addItem(column);
        }
    }

}
