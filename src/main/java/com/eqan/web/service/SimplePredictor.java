package com.eqan.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.eqan.web.model.Location;
import com.eqan.web.model.PredictionResult;

@Service("simplePredictor")
public class SimplePredictor implements Predictor {
    private static String BASE_URL = "https://api.darksky.net/forecast/{API_KEY}/{lat},{lng}?exclude={EXCLUDE_DATAPOINTS}";
    private static String EXCLUDE_DATAPOINTS = "minutely,daily,alerts,flags";
    private static final Logger LOG = LoggerFactory.getLogger(SimplePredictor.class);

    private String apiKey;
    private RestTemplate restTemplate = new RestTemplate();
    
    @Autowired
    public SimplePredictor(@Value("${DARKSKY_API_KEY}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public PredictionResult predict(double longitude, double latitude) {
        if (LOG.isTraceEnabled())
            LOG.trace("Getting weather prediction for longitude {} and latitude {}", longitude, latitude);
        ResponseEntity<PredictionResult> response = restTemplate.getForEntity(BASE_URL, PredictionResult.class, apiKey,
                latitude, longitude, EXCLUDE_DATAPOINTS);
        return response.getBody();
    }

    @Override
    public PredictionResult predict(Location location) {
        return predict(location.getLongitude(), location.getLatitude());
    }

    @Override
    public PredictionResult predict(String city) {
        throw new UnsupportedOperationException();
    }
}
