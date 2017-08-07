package com.eqan.utils.search;

import java.io.IOException;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CityIndexFactory {

	private Directory cityIndex;

	@Autowired
	CSVParser records;

	@Bean(name = "cityIndex")
	public Directory getCityIndex() throws IOException {
		if (cityIndex == null) {
			makeIndex();
		}
		return cityIndex;
	}

	private void makeIndex() throws IOException {
		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		cityIndex = new RAMDirectory();
		try (IndexWriter w = new IndexWriter(cityIndex, config); CSVParser r = records) {
			for (CSVRecord csvRecord : records) {
				addDoc(w, csvRecord);
			}
		}
	}

	private void addDoc(IndexWriter w, CSVRecord record) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("name", record.get("Name"), Field.Store.YES));
		doc.add(new TextField("canonical name", record.get("Canonical Name"), Field.Store.YES));
		doc.add(new StringField("id", record.get("Criteria ID"), Field.Store.YES));
		doc.add(new StringField("country code", record.get("Country Code"), Field.Store.YES));

		w.addDocument(doc);
	}

}
