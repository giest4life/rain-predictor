package com.eqan.utils.search;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eqan.utils.dao.LocationRowMapper;
import com.eqan.web.model.Location;

public class CreateLuceneIndex {
    public static final Logger LOG = LoggerFactory.getLogger(CreateLuceneIndex.class);

    public static void main(String[] args) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = FSDirectory.open(Paths.get("indices/location_index"));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);

        ApplicationContext ctxt = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        JdbcTemplate jdbcTemplate = ctxt.getBean("jdbcTemplate", JdbcTemplate.class);

        LOG.info("Getting locations from database");
        List<Location> locations = jdbcTemplate.query("SELECT * from geoname_location", new LocationRowMapper());
        LOG.info("Creating documents of locations for Lucene");
        for (Location location : locations) {

            Document doc = new Document();
            Field geonameId = new StoredField("geoname_id", location.getGeonameId());
            Field longitude = new StoredField("longitude", location.getLongitude());
            Field latitude = new StoredField("latitude", location.getLatitude());
            Field locationName = new TextField("location_name", location.getLocationName(), Field.Store.YES);
            Field countryCode = new TextField("country_code", location.getCountryCode(), Field.Store.YES);
            Field countryName = new TextField("country_name",
                    location.getCountryName() != null ? location.getCountryName() : "", Field.Store.YES);
            Field admin1Code = new TextField("admin1_code", location.getAdmin1Code(), Field.Store.YES);
            Field admin1CodeName = new TextField("admin1_name",
                    location.getAdmin1Name() != null ? location.getAdmin1Name() : "", Field.Store.YES);
            
            StringBuilder contents = new StringBuilder();
            contents.append(location.getLocationName());
            
            if (location.getAdmin1Name() != null) {
                contents.append(" ");
                contents.append(location.getAdmin1Name());
                
            }
            
            if (!location.getAdmin1Code().matches("^\\d+$")) {
                contents.append(" ");
                contents.append(location.getAdmin1Code());
            }
            
            Field longName = new TextField("long_name", contents.toString(), Field.Store.YES);
            
            doc.add(geonameId);
            doc.add(longitude);
            doc.add(latitude);
            doc.add(locationName);
            doc.add(countryCode);
            doc.add(countryName);
            doc.add(admin1Code);
            doc.add(admin1CodeName);
            doc.add(longName);
            w.addDocument(doc);
        }
        w.close();
        LOG.info("Added {} documents to index", locations.size());

    }
}
