package com.eqan.utils.search;

import java.io.IOException;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

public class CoordinateRetrieval {
	private static String API_KEY = "AIzaSyBp_fPCTvyeCA748Mz9tjeL5F2yROI58jM";
	private static GeoApiContext geoCtxt = new GeoApiContext.Builder().apiKey(API_KEY).build();
	private static GeocodingResult[] results;

	public static double[] getCoordinates(String city) throws ApiException, InterruptedException, IOException {
		results = GeocodingApi.geocode(geoCtxt, city).await();
		double[] coordinates = new double[2];
		coordinates[0] = results[0].geometry.location.lat;
		coordinates[1] = results[0].geometry.location.lng;
		return coordinates;
	}
}
