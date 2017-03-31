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

/**
 * DOC zshen class global comment. Detailled comment
 */
public class InputConvertResult {

    private Object[] inputData;

    private boolean isfilled;

    /**
     * Getter for inputData.
     * 
     * @return the inputData
     */
    public Object[] getInputData() {
        return this.inputData;
    }

    /**
     * Getter for isfilled.
     * 
     * @return the isfilled
     */
    public boolean isIsfilled() {
        return this.isfilled;
    }

    /**
     * Sets the inputData.
     * 
     * @param inputData the inputData to set
     */
    public void setInputData(Object[] inputData) {
        this.inputData = inputData;
    }

    /**
     * Sets the isfilled.
     * 
     * @param isfilled the isfilled to set
     */
    public void setIsfilled(boolean isfilled) {
        this.isfilled = isfilled;
    }

}
