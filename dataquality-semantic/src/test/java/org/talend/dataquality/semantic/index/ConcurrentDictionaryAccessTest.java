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
package org.talend.dataquality.semantic.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.TopDocs;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher.SynonymSearchMode;

public class ConcurrentDictionaryAccessTest {

    private static final Logger log = Logger.getLogger(ConcurrentDictionaryAccessTest.class);

    private AtomicBoolean errorOccurred = new AtomicBoolean();

    @Before
    public void setUp() throws Exception {
        errorOccurred.set(false);
    }

    private SynonymIndexSearcher newSynonymIndexSearcher() {
        try {
            final URI ddPath = this.getClass().getResource("/luceneIdx/dictionary").toURI();
            final SynonymIndexSearcher searcher = new SynonymIndexSearcher(ddPath);
            searcher.setTopDocLimit(20);
            searcher.setSearchMode(SynonymSearchMode.MATCH_EXACT);
            return searcher;
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private class SearcherRunnable implements Runnable {

        @Override
        public void run() {
            final SynonymIndexSearcher searcher = newSynonymIndexSearcher();
            doConcurrentAccess(searcher, true);
        }

    };

    @Test
    public void testThreadUnsafeConcurrentAccess() throws Exception {
        try {
            List<Thread> workers = new ArrayList<>();
            for (int i = 0; i < 200; i++) {
                final Runnable r = new SearcherRunnable();
                workers.add(new Thread(r));
            }
            for (Thread worker : workers) {
                worker.start();
            }
            for (Thread worker : workers) {
                worker.join();
            }
            assertEquals("ConcurrentAccess failed", false, errorOccurred.get());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private static final Map<String, String> EXPECTED_CATEGORY = new HashMap<String, String>() {

        private static final long serialVersionUID = 3771932655942133797L;

        {
            put("Paris", "CITY");
            put("Talend", "COMPANY");
            put("CDG", "AIRPORT_CODE");
            put("French", "LANGUAGE");

        }
    };

    private void doConcurrentAccess(SynonymIndexSearcher searcher, boolean isLogEnabled) {
        int datasetID = (int) Math.floor(Math.random() * 4);

        String input = "";
        switch (datasetID) {
        case 0:
            input = "Paris";
            break;
        case 1:
            input = "Talend";
            break;
        case 2:
            input = "CDG";
            break;
        case 3:
            input = "French";
            break;
        default:
            break;
        }

        try {

            final TopDocs docs = searcher.searchDocumentBySynonym(input);
            final Document doc = searcher.getDocument(docs.scoreDocs[0].doc);

            final String cat = doc.getField("word").stringValue();

            if (!EXPECTED_CATEGORY.get(input).equals(cat)) {
                errorOccurred.set(true);
                if (isLogEnabled) {
                    log.error(input + " is expected to be a " + EXPECTED_CATEGORY.get(input) + " but actually " + cat);
                }
            }
            searcher.close();
        } catch (Exception e) {
            errorOccurred.set(true);
            if (isLogEnabled) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
