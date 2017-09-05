package com.eqan.utils.search;

import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eqan.web.model.Location;

public class LuceneDocumentToLocationMapper {
    private static final Logger LOG = LoggerFactory.getLogger(LuceneDocumentToLocationMapper.class);
    public static Location mapDoc(Document doc) {
        Location l = new Location();
        if (LOG.isTraceEnabled())
            LOG.trace("Geoname ID: {} ",doc.get("geoname_id"));
        l.setGeonameId(Long.parseLong(doc.get("geoname_id")));
        l.setLongitude(Double.parseDouble(doc.get("longitude")));
        l.setLatitude(Double.parseDouble(doc.get("latitude")));
        l.setLocationName(doc.get("location_name"));
        l.setCountryCode(doc.get("country_code"));
        l.setCountryName(doc.get("country_name"));
        l.setAdmin1Code(doc.get("admin1_code"));
        l.setAdmin1Name(doc.get("admin1_name"));
        return l;
    }
}
