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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;

/**
 * this class is used for Converting duration from one unit to another.<br/>
 * year<br/>
 * month<br/>
 * week<br/>
 * day<br/>
 * hour<br/>
 * minute<br/>
 * second<br/>
 * millisecond<br/>
 * input default value is day<br/>
 * output default value is hour<br/>
 * Created by msjian on 2017-03-28.
 */
public class DurationConverter {

    /**
     * 1 day = 24 hours.
     */
    private static final long num_24 = 24L;

    /**
     * 1 minite = 60 seconds
     */
    private static final long num_60 = 60L;

    /**
     * 1 second = 1000 milliseconds.
     */
    private static final long num_1000 = 1000L;

    /**
     * 1 year = 365 days.
     */
    private static final long num_365 = 365L;

    /**
     * 1 month = 30 days.
     */
    private static final long num_30 = 30L;

    /**
     * 1 week = 7 days.
     */
    private static final long num_7 = 7L;

    /**
     * 1 year = 52 weeks.
     */
    private static final long num_52 = 52L;

    /**
     * 1 year = 12 months.
     */
    private static final long num_12 = 12L;

    public static final ChronoUnit DEFAULT_FROM_UNIT = ChronoUnit.DAYS;

    public static final ChronoUnit DEFAULT_TO_UNIT = ChronoUnit.HOURS;

    private ChronoUnit fromUnit;

    private ChronoUnit toUnit;

    /**
     * Default constructor, the default from unit is ChronoUnit.DAYS, the default to unit is
     * ChronoUnit.HOURS.
     */
    public DurationConverter() {
        this(DEFAULT_FROM_UNIT, DEFAULT_TO_UNIT);
    }

    /**
     * ConverterDuration Constructor.
     *
     * @param from - the from ChronoUnit, default value is ChronoUnit.MILE.
     * @param to - the to ChronoUnit, default value is ChronoUnit.second.
     */
    public DurationConverter(ChronoUnit from, ChronoUnit to) {
        this.fromUnit = from == null ? DEFAULT_FROM_UNIT : from;
        this.toUnit = to == null ? DEFAULT_TO_UNIT : to;
    }

    /**
     * convert the value from fromUnit type to toUnit type.
     * 
     * @param value
     * @return long
     */
    public long convert(long value) {
        if (Long.MAX_VALUE == value || Long.MIN_VALUE == value) {
            return value;
        }
        if (this.fromUnit.equals(this.toUnit)) {
            return value;
        }

        // get the days first, then use it as base to convert to the target value.
        long days = 0L;
        switch (this.fromUnit) {
        case MILLIS:
            days = value / num_24 / num_60 / num_60 / num_1000;
            break;
        case SECONDS:
            days = value / num_24 / num_60 / num_60;
            break;
        case MINUTES:
            days = value / num_24 / num_60;
            break;
        case HOURS:
            days = value / num_24;
            break;
        case DAYS:
            days = value;
            break;
        case YEARS:
            days = value * num_365;
            break;
        case MONTHS:
            days = value * num_30;
            break;
        case WEEKS:
            days = value * num_7;
            break;
        default:
            break;
        }

        switch (this.toUnit) {
        case MILLIS:
            return getExactDays(value, days) * num_24 * num_60 * num_60 * num_1000;
        case SECONDS:
            return getExactDays(value, days) * num_24 * num_60 * num_60;
        case MINUTES:
            return getExactDays(value, days) * num_24 * num_60;
        case HOURS:
            return getExactDays(value, days) * num_24;
        case DAYS:
            return getExactDays(value, days);
        case YEARS:
            return new BigDecimal(days).divide(new BigDecimal(num_365), RoundingMode.HALF_UP).longValue();
        case MONTHS:
            return new BigDecimal(days).divide(new BigDecimal(num_30), RoundingMode.HALF_UP).longValue();
        case WEEKS:
            return new BigDecimal(days).divide(new BigDecimal(num_7), RoundingMode.UP).longValue();
        default:
            break;
        }
        return value;
    }

    /**
     * get the days more exactly with what we want.
     * 
     * @param value
     * @param days
     * @return
     */
    protected long getExactDays(long value, long days) {
        if (this.fromUnit == ChronoUnit.MONTHS) {
            long year = value / num_12;
            long month = value % num_12;
            return year * num_365 + month * num_30;
        } else if (this.fromUnit == ChronoUnit.WEEKS) {
            long year = value / num_52;
            long week = value % num_52;
            return year * num_365 + week * num_7;
        }
        return days;
    }
}