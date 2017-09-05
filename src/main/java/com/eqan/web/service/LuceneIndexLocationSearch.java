package com.eqan.web.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eqan.utils.search.LuceneDocumentToLocationMapper;
import com.eqan.web.model.Location;

@Service("luceneIndexLocationSearch")
public class LuceneIndexLocationSearch implements LocationSearch {
    public static final Logger LOG = LoggerFactory.getLogger(LuceneIndexLocationSearch.class);
    public static final int MAX_HITS = 10;

    StandardAnalyzer analyzer = new StandardAnalyzer();
    IndexReader reader;

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
            
            Query phraseQuery = createPhraseQuery(queryString);

            TopDocs docs = searcher.search(phraseQuery, MAX_HITS);
            if (docs.scoreDocs.length == 0) {
                if (LOG.isTraceEnabled())
                    LOG.trace("Trying word query");
                Query wordQuery = createWordQuery(queryString);
                docs = searcher.search(wordQuery, MAX_HITS);
            }
            ScoreDoc[] hits = docs.scoreDocs;
            for (ScoreDoc scoreDoc : hits) {
                Document doc = searcher.doc(scoreDoc.doc);
                if (LOG.isTraceEnabled())
                    LOG.trace("Document returned: {}", doc.get("geoname_id"));
                locations.add(LuceneDocumentToLocationMapper.mapDoc(doc));
            }
            if (LOG.isTraceEnabled())
                LOG.trace("Finished querying");

        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return locations;
    }
    private Query createPhraseQuery(String queryString) throws ParseException {
        return new QueryParser("long_name", analyzer).parse("\"" + queryString + "\"");
    }
    private Query createWordQuery(String queryString) throws ParseException {
        return new QueryParser("long_name", analyzer).parse(queryString);
    }

}
