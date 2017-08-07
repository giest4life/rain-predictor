package com.eqan.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PredictionResult {
	private double latitude;
	private double longitude;
	private String timezone;
	JsonNode currently;
	JsonNode hourly;

	public JsonNode getCurrently() {
		return currently;
	}

	public JsonNode getHourly() {
		return hourly;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setCurrently(JsonNode currently) {
		this.currently = currently;
	}

	public void setHourly(JsonNode hourly) {
		this.hourly = hourly;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

}
