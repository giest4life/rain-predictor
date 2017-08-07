package com.eqan.web.service;

import com.eqan.web.model.PredictionResult;

public interface Predictor {
	 PredictionResult predict(String city);
}
