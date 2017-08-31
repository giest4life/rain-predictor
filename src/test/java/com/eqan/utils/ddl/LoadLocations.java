package com.eqan.utils.ddl;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eqan.web.model.Location;

public class LoadLocations {
    public static final Logger LOG = LoggerFactory.getLogger(LoadLocations.class);
    public static final String LOCATION_DATA_FILE = "/Users/eqan/Downloads/cities1000.txt";
    public static final String[] HEADERS = { "geonameid", "location_name", "asciiname", "alternatenames", "latitude",
            "longitude", "feature_class", "feature_code", "country_code", "cc2", "admin1_code", "admin2_code",
            "admin3_code", "admin4_code", "population", "elevation", "dem", "timezone", "modification_date" };
    public static final String COUNTRY_CODE_MAPPINGS_FILE = "/Users/eqan/Downloads/country_code_mappings.csv";
    public static final String INSERT_STATEMENT = "INSERT INTO geoname_location(geonameid, location_name, latitude, longitude, country_code, country_name, admin1_code, admin2_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public static Map<String, String> COUNTRY_CODE_MAPPING = new HashMap<>();
    
    static Object[] csvRecordToBatchRecord(CSVRecord r) {

        return new Object[] { Long.parseLong(r.get(HEADERS[0])), r.get(HEADERS[1]),
                Double.parseDouble(r.get(HEADERS[4])), Double.parseDouble(r.get(HEADERS[5])), r.get(HEADERS[8]), COUNTRY_CODE_MAPPING.get(r.get(HEADERS[8])),
                r.get(HEADERS[10]), r.get(HEADERS[11]) };
    }
    
    static Location csvRecordToLocation(CSVRecord r) {
        Location location = new Location();
        location.setGeonameId(Long.parseLong(r.get(HEADERS[0])));
        location.setLocationName(r.get(HEADERS[1]));
        location.setLatitude(Double.parseDouble(r.get(HEADERS[4])));
        location.setLongitude(Double.parseDouble(r.get(HEADERS[5])));
        location.setCountryCode(r.get(HEADERS[8]));
        location.setCountryname(COUNTRY_CODE_MAPPING.get(location.getCountryCode()));
        location.setAdmin1Code(r.get(HEADERS[10]));
        location.setAdmin2Code(r.get(HEADERS[11]));
        return location;
    }

    static void dropTable(JdbcTemplate jdbcTemplate) {
        LOG.info("Truncating table");
        String sql = "TRUNCATE geoname_location";
        jdbcTemplate.execute(sql);
        LOG.info("Dropped all records");
    }
    
    static void countryCodesToCountryNames() throws IOException {
        LOG.info("Mapping country codes to country names");
        BufferedReader br = Files.newBufferedReader(Paths.get(COUNTRY_CODE_MAPPINGS_FILE));
        CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(br);
        
        for (CSVRecord record : parser) {
            COUNTRY_CODE_MAPPING.put(record.get("alpha-2"), record.get("name"));
        }
        LOG.info("Finished mapping country codes to country names");
    }

    public static void main(String[] args) throws IOException {
        
        countryCodesToCountryNames();
        
        BufferedReader br = Files.newBufferedReader(Paths.get(LOCATION_DATA_FILE));
        CSVParser parser = CSVFormat.RFC4180.withDelimiter('\t').withHeader(HEADERS).withQuote(null).parse(br);

        ApplicationContext ctxt = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

        JdbcTemplate jdbcTemplate = ctxt.getBean("jdbcTemplate", JdbcTemplate.class);

        List<Object[]> batchLocations = new ArrayList<>();
        LOG.info("Reading locations from file and adding them to locations list");
        for (CSVRecord record : parser) {
            batchLocations.add(csvRecordToBatchRecord(record));
        }
        dropTable(jdbcTemplate);
        LOG.info("Batch inserting into database");
        
        int[] results = jdbcTemplate.batchUpdate(INSERT_STATEMENT, batchLocations);
        LOG.info("Finished batch update {} records", results.length);

    }
}
