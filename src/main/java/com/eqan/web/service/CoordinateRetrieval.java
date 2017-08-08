package com.eqan.web.service;

import java.util.Map;

public interface CoordinateRetrieval {
	Map<String, Double> getCoordinatesByCity(String city);
}
