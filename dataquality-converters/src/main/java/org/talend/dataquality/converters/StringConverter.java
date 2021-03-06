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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * this class is used for Converting a String.<br/>
 * created by msjian on 2017.2.16
 * 
 */
public class StringConverter {

    public String[] WHITESPACE_CHARS = new String[] { "\t" // CHARACTER TABULATION //$NON-NLS-1$
            , "\n" // LINE FEED (LF) //$NON-NLS-1$
            , '\u000B' + "" // LINE TABULATION //$NON-NLS-1$
            , "\f" // FORM FEED (FF) //$NON-NLS-1$
            , "\r" // CARRIAGE RETURN (CR) //$NON-NLS-1$
            , " " // SPACE //$NON-NLS-1$
            , '\u0085' + "" // NEXT LINE (NEL) //$NON-NLS-1$
            , '\u00A0' + "" // NO-BREAK SPACE //$NON-NLS-1$
            , '\u1680' + "" // OGHAM SPACE MARK //$NON-NLS-1$
            , '\u180E' + "" // MONGOLIAN VOWEL SEPARATOR //$NON-NLS-1$
            , '\u2000' + "" // EN QUAD //$NON-NLS-1$
            , '\u2001' + "" // EM QUAD //$NON-NLS-1$
            , '\u2002' + "" // EN SPACE //$NON-NLS-1$
            , '\u2003' + "" // EM SPACE //$NON-NLS-1$
            , '\u2004' + "" // THREE-PER-EM SPACE //$NON-NLS-1$
            , '\u2005' + "" // FOUR-PER-EM SPACE //$NON-NLS-1$
            , '\u2006' + "" // SIX-PER-EM SPACE //$NON-NLS-1$
            , '\u2007' + "" // FIGURE SPACE //$NON-NLS-1$
            , '\u2008' + "" // PUNCTUATION SPACE //$NON-NLS-1$
            , '\u2009' + "" // THIN SPACE //$NON-NLS-1$
            , '\u200A' + "" // HAIR SPACE //$NON-NLS-1$
            , '\u2028' + "" // LINE SEPARATOR //$NON-NLS-1$
            , '\u2029' + "" // PARAGRAPH SEPARATOR //$NON-NLS-1$
            , '\u202F' + "" // NARROW NO-BREAK SPACE //$NON-NLS-1$
            , '\u205F' + "" // MEDIUM MATHEMATICAL SPACE //$NON-NLS-1$
            , '\u3000' + "" // IDEOGRAPHIC SPACE //$NON-NLS-1$
    };

    private String repeatChar = null;

    private Pattern removeRepeatCharPattern = null;

    private Pattern removeWhiteSpacesPattern = null;

    /**
     * 
     * This constructor is used to some cases but except removing a specify repeated String {@link #removeRepeatedChar(String)}.
     */
    public StringConverter() {
        this(null);
    }

    /**
     * 
     * This constructor is used to remove a specify repeated String {@link #removeRepeatedChar(String)} .
     * 
     * @param repeatStr it is a repeat String
     */
    public StringConverter(String repeatStr) {
        this.repeatChar = repeatStr;
        if (!StringUtils.isEmpty(repeatStr)) {
            removeRepeatCharPattern = Pattern.compile("(" + repeatStr + ")+"); //$NON-NLS-1$//$NON-NLS-2$
        }
        removeWhiteSpacesPattern = Pattern.compile("([\\s\\u0085\\p{Z}])\\1+"); //$NON-NLS-1$
    }

    /**
     * Remove trailing and leading characters which may be empty string, space string,\t,\n,\r,\f...any space, break related
     * characters.
     * 
     * @param inputStr - the input text.
     * @return String
     */
    public String removeTrailingAndLeadingWhitespaces(String inputStr) {
        if (StringUtils.isEmpty(inputStr)) {
            return inputStr;
        }

        String result = inputStr;
        while (StringUtils.startsWithAny(result, WHITESPACE_CHARS)) {
            result = StringUtils.removeStart(result, result.substring(0, 1));
        }

        while (StringUtils.endsWithAny(result, WHITESPACE_CHARS)) {
            result = StringUtils.removeEnd(result, result.substring(result.length() - 1, result.length()));
        }
        return result;
    }

    /**
     * Remove trailing and leading characters and the remove Character is whitespace only. <br/>
     * <br/>
     * Note: this is not equals inputStr.trim() for example: <br/>
     * when the inputStr is ("\t" + "abc "), this method will return ("\t" + "abc"),<br/>
     * but for trim method will return "abc"
     * 
     * @param inputStr - the input text.
     * @return String
     */
    public String removeTrailingAndLeading(String inputStr) {
        return removeTrailingAndLeading(inputStr, " "); //$NON-NLS-1$
    }

    /**
     * Remove trailing and leading characters.
     * 
     * @param inputStr - the input text.
     * @param removeCharacter - the remove character.
     * @return String.
     */
    public String removeTrailingAndLeading(String inputStr, String removeCharacter) {
        if (StringUtils.isEmpty(inputStr) || StringUtils.isEmpty(removeCharacter)) {
            return inputStr;
        }

        String result = inputStr;

        while (result.startsWith(removeCharacter)) {
            result = StringUtils.removeStart(result, removeCharacter);
        }
        while (result.endsWith(removeCharacter)) {
            result = StringUtils.removeEnd(result, removeCharacter);
        }
        return result;
    }

    /**
     * 
     * Remove consecutive repeated characters by a specified char.
     * 
     * @param inputStr the source String
     * @return the string with the source string removed if found
     */
    public String removeRepeatedChar(String inputStr) {
        if (StringUtils.isEmpty(inputStr) || StringUtils.isEmpty(repeatChar) || removeRepeatCharPattern == null) {
            return inputStr;
        }
        Matcher matcher = removeRepeatCharPattern.matcher(inputStr);
        return matcher.replaceAll(repeatChar);
    }

    /**
     * 
     * Remove all repeated white spaces which include all strings in {@link #WHITESPACE_CHARS} like as " ","\n","\r","\t".
     * *
     * 
     * <pre>
     * removeRepeatedWhitespaces(null) = null
     * removeRepeatedWhitespaces("")   = ""
     * removeRepeatedWhitespaces("a  back\t\t\td") = "a back\td"
     * </pre>
     * 
     * @param inputStr inputStr the source String
     * @return the string removed all whiteSpaces
     */
    public String removeRepeatedWhitespaces(String inputStr) {
        if (StringUtils.isEmpty(inputStr) || removeWhiteSpacesPattern == null) {
            return inputStr;
        }
        Matcher matcher = removeWhiteSpacesPattern.matcher(inputStr);
        return matcher.replaceAll("$1"); //$NON-NLS-1$
    }

}
