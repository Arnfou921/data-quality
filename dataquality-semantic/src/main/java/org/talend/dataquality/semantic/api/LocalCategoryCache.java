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
package org.talend.dataquality.semantic.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.talend.dataquality.semantic.index.ClassPathDirectory;

public class LocalCategoryCache {

    private static final Logger LOGGER = Logger.getLogger(LocalCategoryCache.class);

    private SearcherManager mgr;

    LocalCategoryCache(String contextName) {
        try {
            URI ddPath = CategoryRegistryManager.getInstance(contextName).getDictionaryURI();
            Directory dir = ClassPathDirectory.open(ddPath);
            mgr = new SearcherManager(dir, null);
        } catch (IOException e) {
            LOGGER.error("Failed to read local dictionary cache! ", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to parse index URI! ", e);
        }
    }

    public String getUpperCategory(String categoryName) {

        String res = "";
        try {
            String categoryId = getCategoryId(categoryName);
            mgr.maybeRefresh();
            IndexSearcher searcher = mgr.acquire();
            IndexReader reader = searcher.getIndexReader();
            // TODO if there are more than 1 result...
            TopDocs topCats = searcher.search(new TermQuery(new Term(DictionaryConstants.ID, categoryId)), 1);
            mgr.release(searcher);

            if (topCats.scoreDocs.length > 0) {
                IndexableField upperType = reader.document(topCats.scoreDocs[0].doc).getField(DictionaryConstants.UPPERTYPEID);
                if (upperType != null) {
                    res = getCategoryName(upperType.stringValue());
                }
            }
        } catch (IOException e) {
            LOGGER.trace(e.getMessage(), e);
        }
        return res;
    }

    public Set<String> getLowerCategories(String categoryName) {

        List<String> resCat = new ArrayList<String>();
        Deque<String> catToSee = new ArrayDeque<>();
        Set<String> resCatName = new TreeSet<>();
        try {
            catToSee.add(getCategoryId(categoryName));

            mgr.maybeRefresh();
            IndexSearcher searcher = mgr.acquire();

            IndexReader reader = searcher.getIndexReader();

            String currentCategory;
            while (!catToSee.isEmpty()) {
                currentCategory = catToSee.pop();
                TopDocs topCats = searcher.search(new TermQuery(new Term(DictionaryConstants.UPPERTYPEID, currentCategory)),
                        reader.numDocs());
                for (ScoreDoc scoreDoc : topCats.scoreDocs) {
                    Document doc = reader.document(scoreDoc.doc);
                    String upperType = doc.getField(DictionaryConstants.UPPERTYPEID).stringValue();
                    resCat.add(upperType);
                    catToSee.add(upperType);
                }
            }
            mgr.release(searcher);

            for (String id : resCat) {
                resCatName.add(getCategoryName(id));
            }
        } catch (IOException e) {
            LOGGER.trace(e.getMessage(), e);
        }
        return resCatName;
    }

    private String getCategoryId(String categoryName) throws IOException {
        String id = "";
        mgr.maybeRefresh();
        IndexSearcher searcher = mgr.acquire();
        TopDocs topCats = searcher.search(new TermQuery(new Term(DictionaryConstants.NAME, categoryName)), 1);
        if (topCats.scoreDocs.length != 0) {
            id = searcher.getIndexReader().document(topCats.scoreDocs[0].doc).getField(DictionaryConstants.ID).stringValue();
        }
        mgr.release(searcher);
        return id;
    }

    private String getCategoryName(String categoryId) throws IOException {
        String name = "";
        mgr.maybeRefresh();
        IndexSearcher searcher = mgr.acquire();
        TopDocs topCats = searcher.search(new TermQuery(new Term(DictionaryConstants.ID, categoryId)), 1);
        if (topCats.scoreDocs.length != 0) {
            name = searcher.getIndexReader().document(topCats.scoreDocs[0].doc).getField(DictionaryConstants.NAME).stringValue();
        }
        mgr.release(searcher);
        return name;
    }
}
