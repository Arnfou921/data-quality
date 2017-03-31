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
package org.talend.survivorship.model;

import java.util.Collection;
import java.util.List;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class SubDataSet extends DataSet {

    private List<Integer> dataSetIndex;

    /**
     * DOC zshen SubDataSet constructor comment.
     * 
     * @param columns
     */
    public SubDataSet(DataSet dataSet, List<Integer> conflictDataIndexList) {
        super(dataSet.getColumnList(), dataSet.getRecordList());
        dataSetIndex = conflictDataIndexList;
        this.survivorIndexMap = dataSet.survivorIndexMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.model.DataSet#getAttributesByColumn(java.lang.String)
     */
    @Override
    public Collection<Attribute> getAttributesByColumn(String columnName) {
        for (Column col : this.getColumnList()) {
            if (col.getName().equals(columnName)) {
                return col.getAttributesByFilter(dataSetIndex);
            }
        }
        return null;
    }

    /**
     * Getter for dataSetIndex.
     * 
     * @return the dataSetIndex
     */
    public List<Integer> getDataSetIndex() {
        return this.dataSetIndex;
    }

}
