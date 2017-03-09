package org.talend.dataquality.semantic.split;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.talend.dataquality.semantic.api.DictionaryUtils;

public class SplitDiscoveryValidationTest {

    private static final Logger LOGGER = Logger.getLogger(SplitDiscoveryValidationTest.class);

    public static final String F_CATID = "catid";

    public static final String F_ID = "docid";//$NON-NLS-1$

    public static final String F_WORD = "word";//$NON-NLS-1$

    public static final String F_SYN = "syn";//$NON-NLS-1$

    public static final String F_SYNTERM = "synterm";//$NON-NLS-1$

    public static final String F_RAW = "raw";

    private static final int MAX_TOKEN_COUNT_FOR_KEYWORD_MATCH = 20;

    private static final int MAX_CHAR_COUNT_FOR_DICTIONARY_MATCH = 100;

    private int maxEdits = 2; // Default value

    /**
     * @param input
     * @return
     * @throws IOException
     */
    protected Query createQueryForSemanticDictionaryMatch(String input) throws IOException {
        // for dictionary search, ignore searching for input containing too many tokens
        if (input.length() > MAX_CHAR_COUNT_FOR_DICTIONARY_MATCH) {
            return new TermQuery(new Term(F_SYNTERM, StringUtils.EMPTY));
        }

        return getTermQuery(F_SYNTERM, StringUtils.join(getTokensFromAnalyzer(input), ' '), false);
    }

    private Query getTermQuery(String field, String text, boolean fuzzy) {
        Term term = new Term(field, text);
        return fuzzy ? new FuzzyQuery(term, maxEdits) : new TermQuery(term);
    }

    /**
     *
     * @param input
     * @return a list of lower-case tokens which strips accents & punctuation
     * @throws IOException
     */
    public static List<String> getTokensFromAnalyzer(String input) {
        StandardTokenizer tokenStream = new StandardTokenizer(new StringReader(input));
        TokenStream result = new StandardFilter(tokenStream);
        result = new LowerCaseFilter(result);
        result = new ASCIIFoldingFilter(result);
        CharTermAttribute charTermAttribute = result.addAttribute(CharTermAttribute.class);
        List<String> termList = new ArrayList<String>();
        try {
            tokenStream.reset();
            while (result.incrementToken()) {
                String term = charTermAttribute.toString();
                termList.add(term);
            }
            result.close();
        } catch (IOException e) {
            LOGGER.debug(e);
        }
        if (termList.size() == 1) { // require exact match when the input has only one token
            termList.clear();
            termList.add(StringUtils.stripAccents(input.toLowerCase()));
        }
        return termList;
    }

    public static void main(String[] args) throws IOException {

        SplitDiscoveryValidationTest appli = new SplitDiscoveryValidationTest();

        RAMDirectory ramDir = new RAMDirectory();

        Document doc = DictionaryUtils.generateDocument(F_ID, F_CATID, "FR_DEPARTMENT",
                new HashSet<String>(Arrays.asList(new String[] { "Île-de-France" })));

        IndexWriter writer = new IndexWriter(ramDir, new IndexWriterConfig(Version.LATEST, new StandardAnalyzer()));
        writer.addDocument(doc);
        writer.commit();
        writer.close();

        DirectoryReader reader = DirectoryReader.open(ramDir);
        IndexSearcher searcher = new IndexSearcher(reader);

        String[] inputs = new String[] { //
                "france", // non match
                "île-de-france", // ignore case
                "Ile-de-France", // ignore accents
                "Ile de France", // ignore special chars
                "Île-de-France" // exact match
        };

        for (String input : inputs) {
            Query query = appli.createQueryForSemanticDictionaryMatch(input);
            TopDocs result = searcher.search(query, 10);
            System.out.println("Input: " + input + "\n found " + result.totalHits + " result(s).");

            for (int i = 0; i < result.totalHits; i++) {
                Document findDoc = reader.document(result.scoreDocs[0].doc);
                System.out.println(" => " + findDoc.getField(F_RAW).stringValue());
            }
            System.out.println();
        }
    }

}
