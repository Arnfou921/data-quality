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
package org.talend.survivorship.action;

import java.util.HashMap;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class SurvivedValuePassAction extends AbstractSurvivoredAction {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.AbstractSurvivoredAction#checkCanHandle(org.talend.survivorship.action.ActionParameter)
     */
    @Override
    public boolean checkCanHandle(ActionParameter actionParameter) {
        HashMap<String, Integer> survivorIndexMap = actionParameter.getDataset().getSurvivorIndexMap();
        String refColumn = actionParameter.getColumn();
        Integer survivedValueIndex = survivorIndexMap.get(refColumn);
        Object refSurvivedValue = actionParameter.getDataset().getRecordList().get(survivedValueIndex).getAttribute(refColumn)
                .getValue();
        return actionParameter.getInputData().equals(refSurvivedValue);
    }

}
