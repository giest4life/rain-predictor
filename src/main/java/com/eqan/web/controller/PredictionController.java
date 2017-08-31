package com.eqan.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eqan.web.model.PredictionResult;
import com.eqan.web.service.Predictor;

@RestController
public class PredictionController {
	private static final Logger LOG = LoggerFactory.getLogger(PredictionController.class);
	@Autowired
	Predictor predictor;
	
	@GetMapping("/predict")
	public PredictionResult getPrediction(@RequestParam String city) {
		if (city.trim().isEmpty()) {
			throw new IllegalArgumentException("The city parameter must not be null or empty");
		}
		if(LOG.isTraceEnabled()) {
			LOG.trace("Received request for /predict for city " + city);
		}
		return predictor.predict(city);
	}
}
