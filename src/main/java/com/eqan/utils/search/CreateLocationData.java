package com.eqan.utils.search;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.eqan.utils.search.Constants.*;

@Slf4j
public class CreateLocationData {

    private static final Map<String, String> countryCodeMap = new HashMap<>();
    private static final Map<String, String> admin1CodeMap = new HashMap<>();
    private static final Properties PROPS = new Properties();


    public static void main(String[] args) throws IOException {
        log.info("Running application to create lucene index");

        try (final InputStream stream = CreateLocationData.class.getClassLoader().getResourceAsStream("app.properties")) {
            PROPS.load(stream);
        }

        fillCountryCodeMap();
        fillAdmin1CodeMap();
        writeLocationCSV();

        log.info("Finished parsing CSV");
    }

    private static void writeLocationCSV() throws IOException {

        String citiesPath = Paths.get(DATA_DIR, PROPS.getProperty("GEONAME_CITIES_FILE")).toString();

        log.info("Writing new CSV to file using source file {}", citiesPath);

        int rowNum = 0;
        try (CSVPrinter printer = new CSVPrinter(
                new FileWriter(LOCATION_FILE), CSVFormat.DEFAULT)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withDelimiter('\t')
                    .withQuote(null)
                    .parse(new FileReader(citiesPath));

            log.info("Writing headers");
            printer.printRecord(LOCATION_FILE_HEADERS);

            log.info("Writing the rest");

            for (CSVRecord record : records) {
                printer.printRecord(createRow(record));
                rowNum++;
            }

        }
        log.info("{} rows written", rowNum);
    }

    private static String[] createRow(CSVRecord srcRecord) {
        String[] targetRow = new String[LOCATION_FILE_HEADERS.length];
        targetRow[0] = srcRecord.get(0);
        targetRow[1] = srcRecord.get(GEONAME_FILE_HEADERS.get("asciiname"));
        targetRow[2] = srcRecord.get(GEONAME_FILE_HEADERS.get("admin1 code"));
        String countryCode = srcRecord.get(GEONAME_FILE_HEADERS.get("country code"));
        targetRow[3] = admin1CodeMap.get(countryCode + "." + targetRow[2]);
        targetRow[4] = countryCode;
        targetRow[5] = countryCodeMap.get(countryCode);
        targetRow[6] = srcRecord.get(GEONAME_FILE_HEADERS.get("latitude"));
        targetRow[7] = srcRecord.get(GEONAME_FILE_HEADERS.get("longitude"));
        return targetRow;
    }

    private static void fillCountryCodeMap() throws IOException {
        String path = Paths.get(DATA_DIR, PROPS.getProperty("COUNTRY_CODES_FILE")).toString();

        log.info("Filling the country code map using {}", path);

        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .parse(new FileReader(path));

        for (CSVRecord record : records) {
            countryCodeMap.put(record.get(1), record.get(0));
        }

        log.info("Filled country code map");
    }

    private static void fillAdmin1CodeMap() throws IOException {
        String path = Paths.get(DATA_DIR, PROPS.getProperty("ADMIN1_CODES_FILE")).toString();
        log.info("Filling admin1 code map using {}", path);

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withDelimiter('\t')
                .parse(new FileReader(path));

        for (CSVRecord record : records) {
            admin1CodeMap.put(record.get(0), record.get(2));
        }

        log.info("Filled admin1 code map");
    }

}