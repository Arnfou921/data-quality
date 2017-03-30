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
package org.talend.dataquality.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test for class {@link DuplicateCharEraser}.
 * 
 * @author qiongli
 * @version 2017.03.30
 */
public class DuplicateCharEraserTest {

    @Test
    public void testremoveDuplicate_CR() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser("\r"); //$NON-NLS-1$
        String input = "a\rbccccdeaa\r\r\ry"; //$NON-NLS-1$
        assertEquals("a\rbccccdeaa\ry", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testremoveDuplicate_LF() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser("\n"); //$NON-NLS-1$
        String input = "a\nbccccdeaa\n\n\ny"; //$NON-NLS-1$
        assertEquals("a\nbccccdeaa\ny", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testremoveDuplicate_CRLF() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser("\r\n"); //$NON-NLS-1$
        String input = "a\r\nbccccdeaa\r\n\r\n\r\ny"; //$NON-NLS-1$
        assertEquals("a\r\nbccccdeaa\r\ny", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testremoveDuplicate_TAB() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser("\t"); //$NON-NLS-1$
        String input = "a\tbccccdeaa\t\t\t\t\t\ty"; //$NON-NLS-1$
        assertEquals("a\tbccccdeaa\ty", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testremoveDuplicate_LETTER() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser("c"); //$NON-NLS-1$
        String input = "atbccccdeaaCCtcy"; //$NON-NLS-1$
        assertEquals("atbcdeaaCCtcy", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
        duplicateCharEraser = new DuplicateCharEraser("a"); //$NON-NLS-1$
        input = "aaatbccccdeaaCCtcy"; //$NON-NLS-1$
        assertEquals("atbccccdeaCCtcy", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
        duplicateCharEraser = new DuplicateCharEraser("ac"); //$NON-NLS-1$
        input = "acacacactbccccdeaCCtaccy"; //$NON-NLS-1$
        assertEquals("actbccccdeaCCtaccy", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$

        input = "abcdef"; //$NON-NLS-1$
        assertEquals("abcdef", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    /**
     * 
     * test specail cahrs("|","(","[") in regex.
     */
    public void testremoveDuplicate_specailchars() {
        DuplicateCharEraser stringConverter = new DuplicateCharEraser("|"); //$NON-NLS-1$
        String input = "atb||||x"; //$NON-NLS-1$
        assertEquals("atb|x", stringConverter.removeRepeatedChar(input)); //$NON-NLS-1$

        stringConverter = new DuplicateCharEraser("\\("); //$NON-NLS-1$
        input = "atb((((x"; //$NON-NLS-1$
        assertEquals("atb(x", stringConverter.removeRepeatedChar(input)); //$NON-NLS-1$

        stringConverter = new DuplicateCharEraser("\\+"); //$NON-NLS-1$
        input = "ab++c"; //$NON-NLS-1$
        assertEquals("ab+c", stringConverter.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testremoveDuplicate_NULL1() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser("c"); //$NON-NLS-1$
        String input = null;
        assertEquals(null, duplicateCharEraser.removeRepeatedChar(input));
        input = ""; //$NON-NLS-1$
        assertEquals("", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testremoveDuplicate_NULL2() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser();
        String input = "aaabc"; //$NON-NLS-1$
        assertEquals(input, duplicateCharEraser.removeRepeatedChar(input));
        duplicateCharEraser = new DuplicateCharEraser(""); //$NON-NLS-1$
        assertEquals(input, duplicateCharEraser.removeRepeatedChar(input));
        duplicateCharEraser = new DuplicateCharEraser(null);
        assertEquals(input, duplicateCharEraser.removeRepeatedChar(input));
    }

    @Test
    public void testremoveWhiteSpace() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser();
        String input = "a   b\t\t\tc\n\n\nd\r\re\f\ff"; //$NON-NLS-1$
        String cleanStr = duplicateCharEraser.removeRepeatedChar(input);
        assertEquals("a b\tc\nd\re\ff", cleanStr); //$NON-NLS-1$

        // \r\n will not be removed
        input = "aaab\r\n\r\n\r\nx"; //$NON-NLS-1$
        cleanStr = duplicateCharEraser.removeRepeatedChar(input);
        assertEquals("aaab\r\n\r\n\r\nx", cleanStr); //$NON-NLS-1$

        input = "a\u0085\u0085\u0085b\u00A0\u00A0c\u1680\u1680d\u180E\u180Ee\u2000\u2000f\u2001\u2001g\u2002\u2002h\u2003\u2003i\u2004\u2004"; //$NON-NLS-1$
        cleanStr = duplicateCharEraser.removeRepeatedChar(input);
        assertEquals("a\u0085b\u00A0c\u1680d\u180Ee\u2000f\u2001g\u2002h\u2003i\u2004", cleanStr); //$NON-NLS-1$

        input = "a\u2005\u2005\u2005b\u2006\u2006c\u2007\u2007d\u2008\u2008e\u2009\u2009f\u200A\u200Ag\u2028\u2028h\u2029\u2029i\u202F\u202Fj\u205F\u205Fk\u3000\u3000l"; //$NON-NLS-1$
        cleanStr = duplicateCharEraser.removeRepeatedChar(input);
        assertEquals("a\u2005b\u2006c\u2007d\u2008e\u2009f\u200Ag\u2028h\u2029i\u202Fj\u205Fk\u3000l", cleanStr); //$NON-NLS-1$
    }

    @Test
    public void testremoveWhiteSpaceNull() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser();
        String input = ""; //$NON-NLS-1$
        String cleanStr = duplicateCharEraser.removeRepeatedChar(input);
        assertEquals("", cleanStr); //$NON-NLS-1$
        input = null;
        cleanStr = duplicateCharEraser.removeRepeatedChar(input);
        assertNull(cleanStr);
    }

    @Test
    public void testremoveWhiteSpacWithoutSpace() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser();
        String input = "abccdef"; //$NON-NLS-1$
        String cleanStr = duplicateCharEraser.removeRepeatedChar(input);
        assertEquals("abccdef", cleanStr); //$NON-NLS-1$
    }

    @Test
    public void testremoveDuplicate_IllegalArgumentException() {
        Exception exp = null;
        try {
            new DuplicateCharEraser("+"); //$NON-NLS-1$
        } catch (Exception e) {
            exp = e;
        }
        assertTrue(exp != null && exp instanceof IllegalArgumentException);
    }

}
