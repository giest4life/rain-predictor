package com.eqan.web.service;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.eqan.web.model.PredictionResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("simplePredictor")
public class SimplePredictor implements Predictor {
	private static String BASE_URL = "https://api.darksky.net/forecast/b7a7e9efac3718aace6b59265fb8d0b9/{lat},{lng}";
	private static String QUERY_PARAM = "exclude";
	private static String PARAM_VALUE = "minutely,daily,alerts,flags";
	private static final Logger LOG = LoggerFactory.getLogger(SimplePredictor.class);

	@Autowired
	private CoordinateRetrieval coordinateService;

	@Override
	public PredictionResult predict(String city) {
		PredictionResult result = null;
		try {

			Map<String, Double> coordinates = coordinateService.getCoordinatesByCity(city);

			String requestURL = UriComponentsBuilder.fromUriString(BASE_URL).queryParam(QUERY_PARAM, PARAM_VALUE)
					.buildAndExpand(coordinates).toUriString();
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.getForEntity(requestURL, String.class);
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
