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
    
    private Predictor predictor;

    @Autowired
    public PredictionController(Predictor predictor) {
        this.predictor = predictor;
    }

    @GetMapping("/predict")
    public PredictionResult getPrediction(@RequestParam double longitude, @RequestParam double latitude) {
        if (LOG.isTraceEnabled())
            LOG.trace("Getting weather prediction for longitude {} and latitude {}", longitude, latitude);
        return predictor.predict(longitude, latitude);
    }
}
