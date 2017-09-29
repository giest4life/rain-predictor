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
    public static final int MAX_HITS = 10;

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
            IndexSearcher searcher = new IndexSearcher(reader);
            queryString = QueryParser.escape(queryString);
            if (log.isTraceEnabled())
                log.trace("Searching for query string: {}", queryString);
            Query q = exactPhrase(queryString);

            TopDocs docs = searcher.search(q, MAX_HITS);
            if (docs.scoreDocs.length == 0) {
                if (log.isDebugEnabled())
                    log.trace("Trying starts with query");
                q = startsWith(queryString);
                docs = searcher.search(q, MAX_HITS);
            }
            if (docs.scoreDocs.length == 0) {
                if (log.isTraceEnabled())
                    log.trace("Trying word query");
                q = anyWord(queryString);
                docs = searcher.search(q, MAX_HITS);
            }
            ScoreDoc[] hits = docs.scoreDocs;
            for (ScoreDoc scoreDoc : hits) {
                Document doc = searcher.doc(scoreDoc.doc);
                if (log.isTraceEnabled())
                    log.trace("Document returned: {}", doc.get("long_name"));
                locations.add(LuceneDocumentToLocationMapper.mapDoc(doc));
            }
            if (log.isTraceEnabled())
                log.trace("Finished querying");

        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return locations;
    }

    private Query exactPhrase(String queryString) throws ParseException {
        return new QueryParser("long_name", analyzer).parse(String.format("\"%s\"", queryString));
    }

    private Query startsWith(String queryString) throws ParseException {
        return new QueryParser("long_name", analyzer).parse(queryString + "*");
    }

    private Query anyWord(String queryString) throws ParseException {
        return new QueryParser("long_name", analyzer).parse(queryString);
    }

}
