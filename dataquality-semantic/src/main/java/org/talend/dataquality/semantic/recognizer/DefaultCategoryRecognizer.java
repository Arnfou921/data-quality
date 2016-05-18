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
package org.talend.dataquality.semantic.recognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.ISubCategoryClassifier;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.custom.UDCategorySerDeser;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedCategory;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.classifier.impl.DataDictFieldClassifier;
import org.talend.dataquality.semantic.index.Index;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
class DefaultCategoryRecognizer implements CategoryRecognizer {

    private final List<CategoryFrequency> catList = new ArrayList<>();

    private final Map<String, CategoryFrequency> categoryToFrequency = new HashMap<>();

    private final ISubCategoryClassifier dataDictFieldClassifier;

    private UserDefinedClassifier userDefineClassifier;

    private long emptyCount = 0;

    private long total = 0;

    public DefaultCategoryRecognizer(Index dictionary, Index keyword) throws IOException {
        dataDictFieldClassifier = new DataDictFieldClassifier(dictionary, keyword);

        userDefineClassifier = new UDCategorySerDeser().readJsonFile();
    }

    /**
     * @deprecated use {@link #getSubCategoriesSet(String)} instead.
     * @param data
     * @return
     */
    @Deprecated
    public Set<ISubCategory> getSubCategories(String data) {
        MainCategory mainCategory = MainCategory.getMainCategory(data);
        Set<ISubCategory> subCategorySet = new HashSet<>();

        switch (mainCategory) {
        case Alpha:
        case AlphaNumeric:
            subCategorySet.addAll(dataDictFieldClassifier.classifyIntoCategories(data));
        case Numeric:
            if (userDefineClassifier != null) {
                subCategorySet.addAll(userDefineClassifier.classifyIntoCategories(data, mainCategory));
            }
            break;

        case NULL:
        case BLANK:
        case UNKNOWN:
            break;
        }
        return subCategorySet;

    }

    public Set<String> getSubCategorySet(String data) {

        MainCategory mainCategory = MainCategory.getMainCategory(data);
        Set<String> subCategorySet = new HashSet<>();

        switch (mainCategory) {
        case Alpha:
        case AlphaNumeric:
            subCategorySet.addAll(dataDictFieldClassifier.classify(data));
        case Numeric:
            if (userDefineClassifier != null) {
                subCategorySet.addAll(userDefineClassifier.classify(data, mainCategory));
            }
            break;

        case NULL:
        case BLANK:
            emptyCount++;
        case UNKNOWN:
            break;
        }
        return subCategorySet;
    }

    @Override
    public void prepare() {
        // dictionary.initIndex();
        // keyword.initIndex();
    }

    @Override
    public void reset() {
        catList.clear();
        categoryToFrequency.clear();
        total = 0;
        emptyCount = 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.semantic.recognizer.CategoryRecognizer#process(java.lang.String)
     */
    @Override
    public String[] process(String data) {
        Set<String> categories = getSubCategorySet(data);
        if (categories.size() > 0) {
            for (String cat : categories) {
                incrementCategory(cat, SemanticCategoryEnum.valueOf(cat).getDisplayName());
            }
        } else {
            incrementCategory(StringUtils.EMPTY);
        }
        total++;
        return categories.toArray(new String[categories.size()]);
    }

    /**
     * @deprecated use {@link #process(String)} instead.
     * 
     * @see org.talend.dataquality.semantic.recognizer.CategoryRecognizer#process(java.lang.String)
     */
    @Deprecated
    @Override
    public ISubCategory[] processCategories(String data) {
        Set<ISubCategory> categories = getSubCategories(data);
        if (categories.size() > 0) {
            for (ISubCategory cat : categories) {
                incrementCategory(cat.getId(), cat.getName());
            }
        } else {
            incrementCategory(StringUtils.EMPTY);
        }
        total++;
        return categories.toArray(new ISubCategory[categories.size()]);
    }

    private void incrementCategory(String catId) {
        CategoryFrequency c = categoryToFrequency.get(catId);
        if (c == null) {
            c = new CategoryFrequency(new UserDefinedCategory(catId, catId));
            categoryToFrequency.put(catId, c);
            catList.add(c);
        }
        c.count++;

    }

    private void incrementCategory(String catId, String catName) {
        CategoryFrequency c = categoryToFrequency.get(catId);
        if (c == null) {
            c = new CategoryFrequency(new UserDefinedCategory(catId, catName));
            categoryToFrequency.put(catId, c);
            catList.add(c);
        }
        c.count++;

    }

    @Override
    public Collection<CategoryFrequency> getResult() {
        for (CategoryFrequency category : categoryToFrequency.values()) {
            category.frequency = Math.round(category.count * 10000 / total) / 100F;
        }

        Collections.sort(catList, new Comparator<CategoryFrequency>() {

            @Override
            public int compare(CategoryFrequency o1, CategoryFrequency o2) {
                // The EMPTY category must always be ranked after the others
                if ("".equals(o1.category.getId())) {
                    return 1;
                } else if ("".equals(o2.category.getId())) {
                    return -1;
                }
                return (int) (o2.count - o1.count);
            }
        });
        return catList;
    }
}
