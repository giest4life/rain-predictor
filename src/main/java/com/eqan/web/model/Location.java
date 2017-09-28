package com.eqan.web.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Location {
    
    long geonameId;
    @NonNull String locationName;
    double longitude;
    double latitude;
    @NonNull String countryCode;
    @NonNull String countryName;
    @NonNull String admin1Code;
    @NonNull String admin1Name;
}
