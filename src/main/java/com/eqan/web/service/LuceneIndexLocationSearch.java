package com.eqan.web.service;

import com.eqan.utils.search.LuceneDocumentToLocationMapper;
import com.eqan.web.model.Location;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service("luceneIndexLocationSearch")
@Slf4j
public class LuceneIndexLocationSearch implements LocationSearch {
    private static final int MAX_HITS = 10;

    private StandardAnalyzer analyzer = new StandardAnalyzer();
    private IndexReader reader;

    @Autowired
    public LuceneIndexLocationSearch(@Value("${LOCATION_INDEX_PATH}") String locationIndexPath) throws IOException {
        reader = DirectoryReader.open(FSDirectory.open(Paths.get(locationIndexPath)));
    }

    @Override
    public List<Location> search(String queryString) {

        List<Location> locations = new ArrayList<>();

        try {
            queryString = QueryParser.escape(queryString);
            Query q = exactMatchOrAnyWord(queryString);

            if (log.isDebugEnabled()) {
                log.debug("Searching for query string: {}", queryString);
                log.debug("Running query: {}", q.toString());
            }

            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, MAX_HITS);

            ScoreDoc[] hits = docs.scoreDocs;
            for (ScoreDoc scoreDoc : hits) {
                Document doc = searcher.doc(scoreDoc.doc);
                if (log.isTraceEnabled())
                    log.trace("Document returned: {}", doc.get("long_name"));
                locations.add(LuceneDocumentToLocationMapper.mapDoc(doc));
            }
            if (log.isTraceEnabled())
                log.trace("Finished querying");

        } catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        }

        return locations;
    }

    private Query exactMatchOrAnyWord(String queryString) throws ParseException {
        String qs = String.join(" AND ", queryString.split(" ")) + "*";
        return new QueryParser("long_name", analyzer)
                .parse(String.format("\"%s\" OR %s", queryString, qs));
    }

}
