package com.eqan.web.model;

public class Location {
    
    private long geonameId;
    private String locationName;
    private double longitude;
    private double latitude;
    private String countryCode;
    private String countryName;
    private String admin1Code;
    private String admin1Name;
    private String admin2Code;
    
    
    public long getGeonameId() {
        return geonameId;
    }
    public void setGeonameId(long geonameId) {
        this.geonameId = geonameId;
    }
    public String getLocationName() {
        return locationName;
    }
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    public String getCountryName() {
        return countryName;
    }
    public void setCountryName(String countryname) {
        this.countryName = countryname;
    }
    public String getAdmin1Code() {
        return admin1Code;
    }
    public void setAdmin1Code(String admin1Code) {
        this.admin1Code = admin1Code;
    }
    public String getAdmin2Code() {
        return admin2Code;
    }
    public void setAdmin2Code(String admin2Code) {
        this.admin2Code = admin2Code;
    }
    public String getAdmin1Name() {
        return admin1Name;
    }
    public void setAdmin1Name(String admin1Name) {
        this.admin1Name = admin1Name;
    }
    
    
    
}
