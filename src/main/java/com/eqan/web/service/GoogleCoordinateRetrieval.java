package com.eqan.web.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

@Service("googleCoordinateRetrieval")
public class GoogleCoordinateRetrieval implements CoordinateRetrieval {
	private static final Logger LOG = LoggerFactory.getLogger(GoogleCoordinateRetrieval.class);
	
	private GeoApiContext geoCtxt;
	
	@Autowired
	public GoogleCoordinateRetrieval(@Value("${GOOGLE_API_KEY}") String API_KEY) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("API_KEY is {}", API_KEY);
		}
		geoCtxt = new GeoApiContext.Builder().apiKey(API_KEY).build();
	}
	

	@Override
	public Map<String, Double> getCoordinatesByCity(String city) {
		GeocodingResult[] results;
		Map<String, Double> resultMap = new HashMap<>();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Retrieving coordinates for {}", city);
		}
		try {
			results = GeocodingApi.geocode(geoCtxt, city).await();

			resultMap.put("lat", results[0].geometry.location.lat);
			resultMap.put("lng", results[0].geometry.location.lng);

			if (LOG.isDebugEnabled()) {
				LOG.debug("Finished retrieving coordinates");
				LOG.debug("Returning coordinates for {}", results[0].formattedAddress);
				LOG.debug("Lat: {}, Lng: {}", resultMap.get("lat"), resultMap.get("lng"));
			}

		} catch (ApiException | InterruptedException | IOException e) {
			LOG.error("Exception occurred while retrieving coordinates", e);
			throw new RuntimeException(e);
		}

		return resultMap;
	}
	

}
