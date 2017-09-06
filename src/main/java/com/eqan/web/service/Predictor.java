package com.eqan.web.service;

import com.eqan.web.model.Location;
import com.eqan.web.model.PredictionResult;

public interface Predictor {
	 PredictionResult predict(String city);
	 PredictionResult predict(Location location);
	 PredictionResult predict(double longitude, double latitude);
}
