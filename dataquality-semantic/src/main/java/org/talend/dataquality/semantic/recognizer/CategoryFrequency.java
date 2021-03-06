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
package org.talend.dataquality.semantic.recognizer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;

/**
 * created by talend on 2015-07-28 Detailled comment.
 */
public class CategoryFrequency implements Comparable<CategoryFrequency>, Serializable {

    private static final long serialVersionUID = -3859689633174391877L;

    String categoryId;

    String categoryName;

    float frequency;

    long count;

    /**
     * CategoryFrequency constructor from a category.
     *
     * @param cat the category
     * 
     * @deprecated
     */
    @Deprecated
    public CategoryFrequency(ISubCategory cat) {
        // do not use cat.getId() for the moment because it represents the mongoId
        this(cat.getName(), cat.getName());
    }

    /**
     * CategoryFrequency constructor from a category.
     *
     * @param catId the category ID
     * @param catName the category name
     */
    public CategoryFrequency(String catId, String catName) {
        this.categoryId = catId;
        this.categoryName = catName;
    }

    /**
     * CategoryFrequency constructor from a category.
     *
     * @param catId the category ID
     * @param catName the category name
     * @param count the occurrence
     */
    public CategoryFrequency(String catId, String catName, long count) {
        this.categoryId = catId;
        this.categoryName = catName;
        this.count = count;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName != null ? categoryName : categoryId;
    }

    public float getFrequency() {
        return frequency;
    }

    public long getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CategoryFrequency))
            return false;

        CategoryFrequency that = (CategoryFrequency) o;

        return !(this.getCategoryId() != null ? !this.getCategoryId().equals(that.getCategoryId())
                : that.getCategoryId() != null);

    }

    @Override
    public int hashCode() {
        return getCategoryId() != null ? getCategoryId().hashCode() : 0;
    }

    @Override
    public int compareTo(CategoryFrequency o) {
        if (this.getCount() > o.getCount()) {
            return 1;
        } else if (this.getCount() < o.getCount()) {
            return -1;
        } else {
            final SemanticCategoryEnum cat1 = SemanticCategoryEnum.getCategoryById(this.getCategoryId());
            final SemanticCategoryEnum cat2 = SemanticCategoryEnum.getCategoryById(o.getCategoryId());

            if (cat1 != null && cat2 != null) {
                return cat2.ordinal() - cat1.ordinal();
            } else if (cat1 == null && cat2 != null) {
                return 1;
            } else if (cat1 != null && cat2 == null) {
                return -1;
            } else {
                return o.getCategoryId().compareTo(this.getCategoryId());
            }
        }
    }

    @Override
    public String toString() {
        return "[Category: " + categoryId + " Count: " + count + " Frequency: " + frequency + "]";
    }

    public static void main(String[] args) {
        CategoryFrequency cf1 = new CategoryFrequency("", "");
        CategoryFrequency cf2 = new CategoryFrequency("CITY", "CITY");
        List<CategoryFrequency> list = new ArrayList<>();
        list.add(cf1);
        list.add(cf2);
        System.out.println(list);

        Collections.sort(list);

        System.out.println(list);

    }
}
