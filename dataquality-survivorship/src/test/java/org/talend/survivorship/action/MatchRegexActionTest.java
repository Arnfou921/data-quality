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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.survivorship.model.DataSet;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class MatchRegexActionTest {

    /**
     * DOC zshen Comment method "setUpBeforeClass".
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * DOC zshen Comment method "tearDownAfterClass".
     * 
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * DOC zshen Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * DOC zshen Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.action.MatchRegexAction#checkCanHandle(org.talend.survivorship.model.DataSet, java.lang.Object, java.lang.String, java.lang.String, int, boolean)}
     * .
     */
    @Test
    public void testCheckCanHandleSucc() {
        DataSet dataset = null;
        Object inputData = "Silly"; //$NON-NLS-1$
        int rowNum = 0;
        String column = null;
        String ruleName = null;
        String expression = "^[A-Z]\\\\w*$"; //$NON-NLS-1$
        boolean ignoreBlanks = false;
        ActionParameter actionParaneter = new ActionParameter(dataset, inputData, rowNum, column, ruleName, expression,
                ignoreBlanks);

        MatchRegexAction matchRegexAction = new MatchRegexAction();
        boolean checkCanHandle = matchRegexAction.checkCanHandle(actionParaneter);
        Assert.assertEquals("The result of checkCanHandle shuold be true", true, checkCanHandle); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.action.MatchRegexAction#checkCanHandle(org.talend.survivorship.model.DataSet, java.lang.Object, java.lang.String, java.lang.String, int, boolean)}
     * .
     */
    @Test
    public void testCheckCanHandleFail() {
        DataSet dataset = null;
        Object inputData = "silly"; //$NON-NLS-1$
        int rowNum = 0;
        String column = null;
        String ruleName = null;
        String expression = "^[A-Z]\\\\w*$"; //$NON-NLS-1$
        boolean ignoreBlanks = false;
        ActionParameter actionParaneter = new ActionParameter(dataset, inputData, rowNum, column, ruleName, expression,
                ignoreBlanks);

        MatchRegexAction matchRegexAction = new MatchRegexAction();
        boolean checkCanHandle = matchRegexAction.checkCanHandle(actionParaneter);
        Assert.assertEquals("The result of checkCanHandle shuold be false", false, checkCanHandle); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.action.MatchRegexAction#checkCanHandle(org.talend.survivorship.model.DataSet, java.lang.Object, java.lang.String, java.lang.String, int, boolean)}
     * .
     */
    @Test
    public void testCheckCanHandleInputNull() {
        DataSet dataset = null;
        Object inputData = null;
        int rowNum = 0;
        String column = null;
        String ruleName = null;
        String expression = "^[A-Z]\\\\w*$"; //$NON-NLS-1$
        boolean ignoreBlanks = false;
        ActionParameter actionParaneter = new ActionParameter(dataset, inputData, rowNum, column, ruleName, expression,
                ignoreBlanks);

        MatchRegexAction matchRegexAction = new MatchRegexAction();
        boolean checkCanHandle = matchRegexAction.checkCanHandle(actionParaneter);
        Assert.assertEquals("The result of checkCanHandle shuold be false", false, checkCanHandle); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.action.MatchRegexAction#checkCanHandle(org.talend.survivorship.model.DataSet, java.lang.Object, java.lang.String, java.lang.String, int, boolean)}
     * .
     */
    @Test
    public void testCheckCanHandleExpressionNull() {
        DataSet dataset = null;
        Object inputData = "Silly";
        int rowNum = 0;
        String column = null;
        String ruleName = null;
        String expression = null;
        boolean ignoreBlanks = false;
        ActionParameter actionParaneter = new ActionParameter(dataset, inputData, rowNum, column, ruleName, expression,
                ignoreBlanks);

        MatchRegexAction matchRegexAction = new MatchRegexAction();
        boolean checkCanHandle = matchRegexAction.checkCanHandle(actionParaneter);
        Assert.assertEquals("The result of checkCanHandle shuold be false", false, checkCanHandle); //$NON-NLS-1$
    }

}
