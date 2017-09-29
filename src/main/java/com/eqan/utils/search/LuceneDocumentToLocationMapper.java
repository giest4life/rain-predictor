package com.eqan.utils.search;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eqan.web.model.Location;

@Slf4j
public class LuceneDocumentToLocationMapper {

    public static Location mapDoc(Document doc) {
        if (log.isTraceEnabled())
            log.trace("Geoname ID: {} ", doc.get("geoname_id"));
        return Location.builder().geonameId(Long.parseLong(doc.get("geoname_id")))
                .longitude(Double.parseDouble(doc.get("longitude")))
                .latitude(Double.parseDouble(doc.get("latitude")))
                .locationName(doc.get("location_name"))
                .countryCode(doc.get("country_code"))
                .countryName(doc.get("country_name"))
                .admin1Code(doc.get("admin1_code"))
                .admin1Name(doc.get("admin1_name"))
                .build();
    }
}
