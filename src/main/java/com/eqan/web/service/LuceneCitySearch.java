package com.eqan.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eqan.web.model.City;

@Service("citySearch")
public class LuceneCitySearch implements CitySearch {
	private static Logger LOG = LoggerFactory.getLogger(LuceneCitySearch.class);
	private static int HITS_PER_PAGE = 5;

	private IndexSearcher searcher;
	private QueryParser qp;

	@Autowired
	public LuceneCitySearch(Directory cityIndex, QueryParser qp) throws IOException {
		IndexReader reader = DirectoryReader.open(cityIndex);
		searcher = new IndexSearcher(reader);
		this.qp = qp;

	}

	public List<City> searchByCanonicalName(String search) {
		List<City> cities = new ArrayList<>();
		try {
			Query q = qp.parse(QueryParser.escape(search));
			TopDocs docs = searcher.search(q, HITS_PER_PAGE);
			ScoreDoc[] hits = docs.scoreDocs;

			
			for (ScoreDoc scoreDoc : hits) {
				int docID = scoreDoc.doc;
				Document d = searcher.doc(docID);
				City city = new City(Long.parseLong(d.get("id")), d.get("name"), d.get("canonical name"),
						d.get("country code"));
				cities.add(city);

			}		
		} catch (ParseException | IOException e) {
			if(LOG.isDebugEnabled()) {
				LOG.debug("Exception: " + e.getClass().getName(), e);
			}
			throw new RuntimeException(e);
		}

		return cities;
	}

	@Override
	public City searchOneByName(String name) {
		throw new UnsupportedOperationException("This operation is not yet supported");
	}

	@Override
	public List<City> searchByName(String name) {
		throw new UnsupportedOperationException("This operation is not yet supported");
	}
}
