package com.eqan.web.service;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.eqan.web.model.PredictionResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("simplePredictor")
public class SimplePredictor implements Predictor {
	private static String BASE_URL = "https://api.darksky.net/forecast/{API_KEY}/{lat},{lng}?exclude={EXCLUDE_DATAPOINTS}";
	private static String EXCLUDE_DATAPOINTS = "minutely,daily,alerts,flags";
	private static final Logger LOG = LoggerFactory.getLogger(SimplePredictor.class);

	@Autowired
	private CoordinateRetrieval coordinateService;
	
	@Value("${DARKSKY_API_KEY}")
	private String API_KEY;

	@Override
	public PredictionResult predict(String city) {
		PredictionResult result = null;
		try {

			Map<String, Double> coordinates = coordinateService.getCoordinatesByCity(city);

			RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL, String.class, API_KEY,
                    coordinates.get("lat"), coordinates.get("lng"), EXCLUDE_DATAPOINTS);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(response.getBody());
			result = mapper.treeToValue(jsonNode, PredictionResult.class);

		} catch (IOException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Exception occurred", e);
			}
			throw new RuntimeException(e);
		}
		return result;
	}

	@PostConstruct
	public void postConstruct() {
		LOG.info("SimplePredictor initialized");
	}
}
