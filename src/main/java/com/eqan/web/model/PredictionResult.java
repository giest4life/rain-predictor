package com.eqan.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;
import lombok.Value;

@JsonIgnoreProperties(ignoreUnknown = true)
@Value
public class PredictionResult {
	double latitude;
	double longitude;
	@NonNull String timezone;
	@NonNull JsonNode currently;
	@NonNull JsonNode hourly;

}
