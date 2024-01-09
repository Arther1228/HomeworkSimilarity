package pers.hdq.ui;

import pers.hdq.util.EasyExcelReadUtil;

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

    private static String testPath = "C:\\Users\\HP\\Desktop\\作业查重系统\\package\\123\\GBT37735——2019.xlsx";

    private static List<DynamicPanel> dynamicPanels = new ArrayList<>();
    private static JPanel dynamicPanelContainer;


    public static JPanel createExcelComparisonPanel() {
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
                addDynamicPanel();
            }
        });

        panel.add(titlePanel, BorderLayout.NORTH);

        dynamicPanelContainer = new JPanel();
        dynamicPanelContainer.setLayout(new BoxLayout(dynamicPanelContainer, BoxLayout.Y_AXIS));
        panel.add(dynamicPanelContainer, BorderLayout.CENTER);

        return panel;
    }

    private static void addDynamicPanel() {
        DynamicPanel dynamicPanel = new DynamicPanel();
        dynamicPanels.add(dynamicPanel);

        dynamicPanelContainer.add(dynamicPanel);
        dynamicPanelContainer.revalidate();
        dynamicPanelContainer.repaint();
    }

    private static class DynamicPanel extends JPanel {

        private JComboBox<String> sheetComboBox;
        private JComboBox<String> columnComboBox;

        public DynamicPanel() {

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

        private String[] getSheetNames() {


            Set<String> excelSheetList = EasyExcelReadUtil.getExcelSheetList(testPath);
            String[] sheetArray = excelSheetList.toArray(new String[0]);

            return sheetArray;
        }


        private void updateColumnComboBox(String selectedSheet) {
            Set<String> columnNameListBySheet = EasyExcelReadUtil.getColumnNameListBySheet(testPath, selectedSheet);

            columnComboBox.removeAllItems();
            for (String column : columnNameListBySheet) {
                columnComboBox.addItem(column);
            }
        }

    }

    private static void removeDynamicPanel(DynamicPanel panel) {
        dynamicPanels.remove(panel);
        dynamicPanelContainer.remove(panel);
        dynamicPanelContainer.revalidate();
        dynamicPanelContainer.repaint();
    }

}
