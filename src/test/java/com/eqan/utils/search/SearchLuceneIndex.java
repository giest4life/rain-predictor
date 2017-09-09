package com.eqan.utils.search;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchLuceneIndex {
    public static final Logger LOG = LoggerFactory.getLogger(SearchLuceneIndex.class);

    public static IndexSearcher searcher;

    public static void main(String[] args) throws IOException, ParseException {

        StandardAnalyzer analyzer = new StandardAnalyzer();

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("indices/location_index")));

        searcher = new IndexSearcher(reader);

        Query q1 = new TermQuery(new Term("location_name", "sterling"));
        Query q2 = new TermQuery(new Term("admin1_name", "virginia"));

        BooleanQuery bq1 = new BooleanQuery.Builder().add(q1, BooleanClause.Occur.MUST)
                .add(q2, BooleanClause.Occur.MUST).build();
//        queryAndLog(bq1);

        Query q3 = new QueryParser("location_name", analyzer).parse("Sterling");
        Query q4 = new QueryParser("admin1_name", analyzer).parse("Virginia");

        BooleanQuery bq2 = new BooleanQuery.Builder().add(q3, BooleanClause.Occur.MUST)
                .add(q4, BooleanClause.Occur.MUST).build();
//        queryAndLog(bq2);

        Query q5 = new QueryParser("location_name", analyzer)
                .parse("admin1_name: Virginia AND location_name: Sterling");
//        queryAndLog(q5);

        Query q6 = new QueryParser("location_name", analyzer)
                .parse("\"Sterling VA\" OR (Sterling AND admin1_name: Virginia)");
//        queryAndLog(q6);
        Query q7 = new QueryParser("location_name", analyzer).parse("\"New York City\"");
//        queryAndLog(q7);
        
        Query q8 = new QueryParser("long_name", analyzer).parse("\"New York City NY\" OR (\"New York City\" admin1_name: NY)");
//        queryAndLog(q8);

        Query q9 = new QueryParser("long_name", analyzer).parse("London England");
        queryAndLog(q9);
    }

    public static void queryAndLog(Query query) throws IOException {
        LOG.info("Running query {}", query);
        TopDocs docs = searcher.search(query, 10);
        ScoreDoc[] hits = docs.scoreDocs;
        for (ScoreDoc scoreDoc : hits) {
            Document doc = searcher.doc(scoreDoc.doc);
            LOG.info("{}", doc.get("long_name"));
        }
        LOG.info("Finished running and printing query");
    }
}
