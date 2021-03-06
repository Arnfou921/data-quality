// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking.semantic;

import java.io.Serializable;
import java.util.List;

import org.talend.dataquality.datamasking.functions.Function;

/**
 * API of data masking action using semantic domain information.
 */
public class ValueDataMasker implements Serializable {

    private static final long serialVersionUID = 7071792900542293289L;

    private Function<String> function;

    Function<String> getFunction() {
        return function;
    }

    /**
     * ValueDataMasker constructor.
     * 
     * @param semanticCategory the semantic domain information
     * @param dataType the data type information
     */
    public ValueDataMasker(String semanticCategory, String dataType) {
        this(semanticCategory, dataType, null);
    }

    /**
     * ValueDataMasker constructor.
     * 
     * @param semanticCategory the semantic domain information
     * @param dataType the data type information
     * @param params extra parameters such as date time pattern list
     */
    public ValueDataMasker(String semanticCategory, String dataType, List<String> params) {
        function = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory(semanticCategory, dataType, params);
    }

    /**
     * mask the input value.
     * 
     * @param input
     * @return the masked value
     */
    public String maskValue(String input) {
        return function.generateMaskedRow(input);
    }

}
