// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.survivorship.action.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.util.StringUtils;
import org.talend.survivorship.model.InputConvertResult;
import org.talend.survivorship.model.Record;
import org.talend.survivorship.model.SubDataSet;
import org.talend.survivorship.model.SurvivedResult;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class CRCRHandler extends AbstractChainResponsibilityHandler {

    Map<Integer, String> conflictRowNum = new HashMap<>();

    /**
     * Create by zshen CRCRHandler constructor.
     * 
     * @param handlerParameter relation parameter
     */
    public CRCRHandler(HandlerParameter handlerParameter) {
        super(handlerParameter);
    }

    // TODO this method is not need consisder reomve it
    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#doHandle(java.lang.Object, int,
     * java.lang.String, java.lang.String)
     */
    @Override
    protected void doHandle(Object inputData, int rowNum, String ruleName) {
        this.conflictRowNum.put(rowNum, getTargetColumnName(inputData));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#doHandle(java.lang.Object, int,
     * java.lang.String, java.lang.String)
     */

    protected void doHandle(Integer rowNum, String columnName) {
        this.conflictRowNum.put(rowNum, columnName);
    }

    /**
     * create by zshen return target column name
     * 
     * @return
     */
    private String getTargetColumnName(Object inputData) {
        if (isNeedFillColumn((Object[]) inputData, handlerParameter.getTarColumn().getName())) {
            return handlerParameter.getFillColumn();
        }
        return handlerParameter.getTarColumn().getName();
    }

    @Override
    protected void initConflictRowNum(Map<Integer, String> preConflictRowNum) {
        this.conflictRowNum.putAll(preConflictRowNum);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#handleRequest(java.lang.Object, int,
     * java.lang.String)
     */
    @Override
    public void handleRequest() {
        // do process all of data and init next node
        // 1.create new dataSet by new method of dataset class
        // 2.loop all of data which make sure on the previous node
        // 3.generate new conflict data for next node
        // 4.next handleRequest
        // TODO

        // ConflictDataIndexList is old data
        List<Integer> conflictDataIndexList = this.getHandlerParameter().getConflictDataIndexList();
        if (conflictDataIndexList == null) {
            return;
        }
        // ConflictDataIndexList be clear
        this.getHandlerParameter().UpdateDataSet();
        for (Integer index : conflictDataIndexList) {
            InputConvertResult inputData = getInputData(index);
            if (this.canHandler(inputData.getInputData(), getHandlerParameter().getExpression(), index)) {
                doHandle(index, inputData.isIsfilled() ? this.getHandlerParameter().getFillColumn()
                        : this.getHandlerParameter().getTarColumn().getName());
                if (this.getSuccessor() != null) {
                    // init ConflictDataIndexList for next one
                    this.getHandlerParameter().addConfDataIndex(index);
                }
            }
        }

        if (this.getSuccessor() == null) {
            return;
        }
        List<Integer> newConflictDataIndexList = this.getHandlerParameter().getConflictDataIndexList();
        if (newConflictDataIndexList.size() <= 0) {
            this.getHandlerParameter().getConflictDataIndexList().addAll(conflictDataIndexList);
        } else if (newConflictDataIndexList.size() == 1) {
            return;
        }
        this.getSuccessor().handleRequest();
    }

    /**
     * DOC zshen Comment method "doHandle".
     * 
     * @param inputData
     * @param i
     */
    private void doHandle(Object[] inputData, int i) {
        // TODO Auto-generated method stub

    }

    /**
     * create by zshen get inputData by rowNum and the name of fill column
     * 
     * @return
     */
    private InputConvertResult getInputData(Integer rowNum) {
        InputConvertResult inputResult = new InputConvertResult();
        Map<String, Integer> columnIndexMap = handlerParameter.getColumnIndexMap();
        Object[] dataArray = new Object[columnIndexMap.size()];
        for (String colName : columnIndexMap.keySet()) {
            Record record = handlerParameter.getDataset().getRecordList().get(rowNum);
            Object value = record.getAttribute(colName).getValue();

            if (isNeedFillColumn(value, colName)) {
                value = record.getAttribute(handlerParameter.getFillColumn()).getValue();
                inputResult.setIsfilled(true);
            }
            dataArray[columnIndexMap.get(colName)] = value;
        }
        inputResult.setInputData(dataArray);
        return inputResult;
    }

    /**
     * DOC zshen Comment method "isNeedFillColumn".
     * 
     * @param value
     * @param ignoreBlank
     * @return
     */
    private boolean isNeedFillColumn(Object[] values, String columnName) {
        Integer colIndex = handlerParameter.getColumnIndexMap().get(handlerParameter.getTarColumn().getName());
        Object inputData = values[colIndex];
        return isNeedFillColumn(inputData, columnName);
    }

    /**
     * DOC zshen Comment method "isNeedFillColumn".
     * 
     * @param value
     * @param ignoreBlank
     * @return
     */
    private boolean isNeedFillColumn(Object value, String columnName) {
        if (handlerParameter.getFillColumn() == null || !columnName.equals(handlerParameter.getTarColumn().getName())) {
            return false;
        }
        boolean ignoreBlank = handlerParameter.isIgnoreBlank();
        return inputDataIsEmpty(value, ignoreBlank) && fillColumnIsValid();
    }

    private boolean fillColumnIsValid() {
        return handlerParameter.getFillColumn() != null && !handlerParameter.getFillColumn().isEmpty();
    }

    /**
     * DOC zshen Comment method "inputDataIsEmpty".
     * 
     * @param value
     * @param ignoreBlank
     * @return
     */
    private boolean inputDataIsEmpty(Object value, boolean ignoreBlank) {
        return value == null || StringUtils.EMPTY.equals(ignoreBlank ? value.toString().trim() : value.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#handleRequest(org.talend.survivorship.model.Record
     * , int)
     */
    @Override
    public void handleRequest(Record inputData, int rowNum) {
        // Map<String, Integer> columnIndexMap = handlerParameter.getColumnIndexMap();
        // Object[] dataArray = new Object[inputData.getAttributes().size()];
        // for (String colName : columnIndexMap.keySet()) {
        // dataArray[columnIndexMap.get(colName)] = inputData.getAttribute(colName).getValue();
        // }
        // handleRequest(dataArray, rowNum);
        handleRequest(null, rowNum);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#isContinue(java.lang.Object,
     * java.lang.String, int)
     */
    @Override
    protected boolean isContinue(Object inputData, int rowNum) {
        // Skip conflict has been resolved case
        // this.conflictRowNum.size() > 0 && !this.conflictRowNum.contains(rowNum)&&
        if (!this.getHandlerParameter().isConflictRow(rowNum)) {
            return false;
        }
        if (this.canHandler(inputData, getHandlerParameter().getExpression(), rowNum)) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#getSuccessor()
     */
    @Override
    public CRCRHandler getSuccessor() {
        return (CRCRHandler) super.getSuccessor();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#linkSuccessor(org.talend.survivorship.action.
     * handler.AbstractChainResponsibilityHandler)
     */
    @Override
    public AbstractChainResponsibilityHandler linkSuccessor(AbstractChainResponsibilityHandler successor) {
        // CRCRHandler link CRCRHandler only else will link fail and return itself
        if (successor instanceof CRCRHandler) {
            return super.linkSuccessor(successor);
        } else {
            return this;
        }
    }

    /**
     * 
     * Create by zshen get row number of survivored value
     * 
     * @return
     */
    public SurvivedResult getSurvivoredRowNum() {
        if (this.conflictRowNum.size() == 1 || allNumWithSameData()) {
            Iterator<Integer> iterator = this.conflictRowNum.keySet().iterator();
            Integer index = iterator.next();
            return new SurvivedResult(index, conflictRowNum.get(index));
        } else if (this.getSuccessor() != null) {
            return this.getSuccessor().getSurvivoredRowNum();
        } else {
            return null;
        }
    }

    /**
     * 
     * create by zshen fill column case return empty directly.
     * So that all of column should be tarColumn
     * 
     * @return
     */
    public Object getLongestResult(Object result) {
        Object finalResult = result;
        SubDataSet dataSet = (SubDataSet) this.getHandlerParameter().getDataset();
        List<Integer> dataSetIndex = dataSet.getDataSetIndex();
        for (Integer rowNum : dataSetIndex) {
            Object tarInputData = this.getHandlerParameter().getTarInputData(rowNum,
                    this.getHandlerParameter().getTarColumn().getName());
            if (tarInputData == null) {
                continue;
            }
            if (finalResult.toString().length() < tarInputData.toString().length()) {
                finalResult = tarInputData;
            }

        }
        return finalResult;

    }

    public Object getNonDupResult(Object result) {
        if (this.getHandlerParameter().getFillColumn() != null) {
            return StringUtils.EMPTY;
        }
        return getLongestResult(result);
    }

    /**
     * create by zshen check whether all of conflict row num for same value
     * 
     * @return true when all of data are same else return false
     */
    private boolean allNumWithSameData() {
        Set<Object> setContainer = new HashSet<>();
        Iterator<Integer> iterator = this.conflictRowNum.keySet().iterator();
        while (iterator.hasNext()) {
            Integer index = iterator.next();
            setContainer.add(this.getHandlerParameter().getTarInputData(index, this.conflictRowNum.get(index)));
        }
        return setContainer.size() == 1;
    }

}
