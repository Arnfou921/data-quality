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

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.URI;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.talend.dataquality.semantic.index.ESIndex;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class CategoryRecognizerBuilder implements Serializable {

    private static final long serialVersionUID = -4113525921010790756L;

    private static CategoryRecognizerBuilder INSTANCE;

    private Mode mode;

    private String host;

    private int port;

    private URI ddPath;

    private URI kwPath;

    private String clusterName;

    private LuceneIndex dataDictIndex;

    private LuceneIndex keywordIndex;

    public static CategoryRecognizerBuilder newBuilder() {
        if (INSTANCE == null) {
            INSTANCE = new CategoryRecognizerBuilder();
        }
        return INSTANCE;
    }

    public CategoryRecognizerBuilder host(String host) {
        this.host = host;
        return this;
    }

    public CategoryRecognizerBuilder port(int port) {
        this.port = port;
        return this;
    }

    public CategoryRecognizerBuilder ddPath(URI ddPath) {
        this.ddPath = ddPath;
        return this;
    }

    public CategoryRecognizerBuilder kwPath(URI kwPath) {
        this.kwPath = kwPath;
        return this;
    }

    public CategoryRecognizerBuilder es() {
        this.mode = Mode.ELASTIC_SEARCH;
        return this;
    }

    public CategoryRecognizerBuilder lucene() {
        this.mode = Mode.LUCENE;
        return this;
    }

    public CategoryRecognizer build() throws IOException {

        switch (mode) {
        case LUCENE:
            LuceneIndex dict = getDataDictIndex();
            LuceneIndex keyword = getKeywordIndex();
            return new DefaultCategoryRecognizer(dict, keyword);
        case ELASTIC_SEARCH:
            Settings settings = Settings.builder().put("cluster.name", clusterName).build();
            TransportClient client = TransportClient.builder().settings(settings).build()
                    .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(host, port)));
            ESIndex esDict = new ESIndex(client, ESIndex.ES_DATADICT_INDEX, ESIndex.ES_DATADICT_TYPE);
            ESIndex esKeyword = new ESIndex(client, ESIndex.ES_KEYWORD_INDEX, ESIndex.ES_KEYWORD_TYPE);
            return new DefaultCategoryRecognizer(esDict, esKeyword);
        default:
            throw new IllegalArgumentException("no mode specified.");
        }

    }

    private LuceneIndex getDataDictIndex() {
        if (dataDictIndex == null) {
            dataDictIndex = new LuceneIndex(ddPath, SynonymIndexSearcher.SynonymSearchMode.MATCH_SEMANTIC_DICTIONARY);
        }
        return dataDictIndex;
    }

    private LuceneIndex getKeywordIndex() {
        if (keywordIndex == null) {
            keywordIndex = new LuceneIndex(kwPath, SynonymIndexSearcher.SynonymSearchMode.MATCH_SEMANTIC_KEYWORD);
        }
        return keywordIndex;
    }

    public CategoryRecognizerBuilder cluster(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    /**
     * created by talend on 2015-07-28 Detailled comment.
     *
     */
    public enum Mode {
        LUCENE,
        ELASTIC_SEARCH
    }

    public Mode getMode() {
        return mode;
    }

    public URI getDDPath() {
        return ddPath;
    }

    public URI getKWPath() {
        return kwPath;
    }

}
