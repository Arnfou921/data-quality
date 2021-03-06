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
package org.talend.dataquality.statistics.datetime;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Customized date time pattern manager.
 * 
 * @author mzhao
 *
 */
public final class CustomDateTimePatternManagerOld {

    private static final Locale DEFAULT_LOCALE = Locale.US;

    public static boolean isDate(String value, List<String> customPatterns) {
        return isDate(value, customPatterns, DEFAULT_LOCALE);
    }

    public static boolean isDate(String value, List<String> customPatterns, Locale locale) {
        // use custom patterns first
        if (isMatchCustomPatterns(value, customPatterns, locale)) {
            return true;
        }
        // validate using system pattern manager
        return SystemDateTimePatternManager.isDate(value);
    }

    public static boolean isTime(String value, List<String> customPatterns) {
        return isTime(value, customPatterns, DEFAULT_LOCALE);
    }

    public static boolean isTime(String value, List<String> customPatterns, Locale locale) {
        // use custom patterns first
        if (isMatchCustomPatterns(value, customPatterns, locale)) {
            return true;
        }
        // validate using system pattern manager
        return SystemDateTimePatternManager.isTime(value);
    }

    private static boolean isMatchCustomPatterns(String value, List<String> customPatterns, Locale locale) {
        for (String pattern : customPatterns) {
            if (isMatchCustomPattern(value, pattern, locale)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isMatchCustomPattern(String value, String customPattern, Locale locale) {
        if (customPattern == null) {
            return false;
        }
        try {// firstly, try with user-defined locale
            DateTimeFormatter.ofPattern(customPattern, locale).parse(value);
            return true;
        } catch (DateTimeParseException | IllegalArgumentException e) {
            if (!DEFAULT_LOCALE.equals(locale)) {
                try {// try with LOCALE_US if user defined locale is not US
                    DateTimeFormatter.ofPattern(customPattern, Locale.US).parse(value);
                    return true;
                } catch (DateTimeParseException | IllegalArgumentException e1) {
                    // return false
                }
            }
        }
        return false;
    }

    // for junit only
    static Set<String> replaceByDateTimePattern(String value, String customPattern) {
        return replaceByDateTimePattern(value, customPattern, DEFAULT_LOCALE);
    }

    static Set<String> replaceByDateTimePattern(String value, String customPattern, Locale locale) {
        return replaceByDateTimePattern(value, Collections.singletonList(customPattern), locale);
    }

    public static Set<String> replaceByDateTimePattern(String value, List<String> customPatterns) {
        return replaceByDateTimePattern(value, customPatterns, DEFAULT_LOCALE);
    }

    public static Set<String> replaceByDateTimePattern(String value, List<String> customPatterns, Locale locale) {
        Set<String> resultPatternSet = new HashSet<String>();
        for (String customPattern : customPatterns) {
            if (isMatchCustomPattern(value, customPattern, locale)) {
                resultPatternSet.add(customPattern);
            }
        }
        // otherwise, replace with system date pattern manager.
        resultPatternSet.addAll(systemPatternReplace(value));
        return resultPatternSet;
    }

    private static Set<String> systemPatternReplace(String value) {
        Set<String> resultPatternSet = new HashSet<String>();
        resultPatternSet.addAll(SystemDateTimePatternManager.datePatternReplace(value));
        if (resultPatternSet.isEmpty()) {
            resultPatternSet.addAll(SystemDateTimePatternManager.timePatternReplace(value));
        }
        return resultPatternSet;
    }
}
