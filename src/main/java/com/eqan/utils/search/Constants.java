package com.eqan.utils.search;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String[] LOCATION_FILE_HEADERS = {
            "geonameid",
            "location_name",
            "admin1_code",
            "admin1_name",
            "country_code",
            "country_name",
            "latitude",
            "longitude",
    };
    public static final Map<String, Integer> GEONAME_FILE_HEADERS = new HashMap<>();
    public static final String DATA_DIR = "data";
    public static final String LOCATION_FILE_NAME = "locations.csv";
    public static final String LOCATION_FILE = Paths.get(DATA_DIR, LOCATION_FILE_NAME).toString();

    static {
        GEONAME_FILE_HEADERS.put("geonameid", 0);
        GEONAME_FILE_HEADERS.put("asciiname", 2);
        GEONAME_FILE_HEADERS.put("latitude", 4);
        GEONAME_FILE_HEADERS.put("longitude", 5);
        GEONAME_FILE_HEADERS.put("country code", 8);
        GEONAME_FILE_HEADERS.put("admin1 code", 10);
    }
}
