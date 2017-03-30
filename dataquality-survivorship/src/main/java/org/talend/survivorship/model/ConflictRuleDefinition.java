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
public class ConflictRuleDefinition extends RuleDefinition {

    private String fillColumn;

    private boolean duplicateSurCheck;

    /**
     * create by zshen constructor of ConflictRuleDefinition .
     * 
     * @param order
     * @param ruleName
     * @param referenceColumn
     * @param function
     * @param operation
     * @param targetColumn
     * @param ignoreBlanks
     */
    public ConflictRuleDefinition(Order order, String ruleName, String referenceColumn, Function function, String operation,
            String targetColumn, boolean ignoreBlanks, String fillColumn, boolean duplicateSurCheck) {
        super(order, ruleName, referenceColumn, function, operation, targetColumn, ignoreBlanks);
        this.fillColumn = fillColumn;
        this.duplicateSurCheck = duplicateSurCheck;
    }

    /**
     * Getter for fillColumn.
     * 
     * @return the fillColumn
     */
    public String getFillColumn() {
        return this.fillColumn;
    }

    /**
     * Getter for duplicateSurCheck.
     * 
     * @return the duplicateSurCheck
     */
    public boolean isDuplicateSurCheck() {
        return this.duplicateSurCheck;
    }

}
