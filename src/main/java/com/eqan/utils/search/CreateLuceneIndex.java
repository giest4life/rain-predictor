package com.eqan.utils.search;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;


@Slf4j
public final class CreateLuceneIndex {

    private static final Properties PROPS = new Properties();

    public static void main(String[] args) throws IOException {

        try (final InputStream stream =
                     CreateLuceneIndex.class.getClassLoader().getResourceAsStream("app.properties")) {
            PROPS.load(stream);
        }

        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = FSDirectory.open(Paths.get(PROPS.getProperty("LOCATION_INDEX_PATH")));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);

        log.info("Reading locations from {}", Constants.LOCATION_FILE);

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new FileReader(Constants.LOCATION_FILE));
        for (CSVRecord record : records) {
            Document doc = new Document();
            Field geonameid = new StoredField("geonameid", record.get(0));
            Field locationName = new TextField("location_name", record.get(1), Field.Store.YES);
            Field admin1Code = new TextField("admin1_code", record.get(2), Field.Store.YES);
            Field admin1Name = new TextField("admin1_name", record.get(3), Field.Store.YES);
            Field countryCode = new TextField("country_code", record.get(4), Field.Store.YES);
            Field countryName = new TextField("country_name", record.get(5), Field.Store.YES);
            Field latitude = new StoredField("latitude", record.get(6));
            Field longitude = new StoredField("longitude", record.get(7));

            StringBuilder longNameBuilder = new StringBuilder();
            longNameBuilder.append(record.get(1));

            if (!record.get(2).matches("^\\d+$")) {
                longNameBuilder.append(", ");
                longNameBuilder.append(record.get(2));
            }

            if (record.get(3) != null) {
                longNameBuilder.append(", ");
                longNameBuilder.append(record.get(3));
            }

            longNameBuilder.append(", ");
            longNameBuilder.append(record.get(5));
            longNameBuilder.append(", ");
            longNameBuilder.append(record.get(4));

            Field longName = new TextField("long_name", longNameBuilder.toString(), Field.Store.YES);

            doc.add(geonameid);
            doc.add(locationName);
            doc.add(admin1Code);
            doc.add(admin1Name);
            doc.add(countryCode);
            doc.add(countryName);
            doc.add(latitude);
            doc.add(longitude);
            doc.add(longName);
            w.addDocument(doc);
        }

        w.close();
        log.info("Finished adding all documents to index");
    }
}
