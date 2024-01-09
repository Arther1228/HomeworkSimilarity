package pers.hdq.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author HP
 */
@Slf4j
public class ExcelDataListener extends AnalysisEventListener<Map<Integer, String>> {

    /**
     * 列名 map
     */
    private Map<Integer, String> headMap;
    /**
     * 列名 set
     */
    private Set<String> headNameSet;

    public Map<Integer, String> getHeadMap() {
        return headMap;
    }

    public Set<String> getHeadNameSet() {
        return headNameSet;
    }

    /**
     * 按照行号保存每行的内容
     */
    private Map<Integer, Map<Integer, String>> rowContentMap = new HashMap<>();

    public Map<Integer, Map<Integer, String>> getRowContentMap() {
        return rowContentMap;
    }

    private Integer rowIndex = 1;

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
//        log.info("enter invokeHeadMap");
        this.headMap = headMap;
        this.headNameSet = headMap.values().stream().collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    @Override
    public void invoke(Map<Integer, String> rowData, AnalysisContext context) {
//        log.info("enter invoke");

        rowContentMap.put(rowIndex, rowData);
        rowIndex++;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
//        log.info("read excel finished.");
    }
}
