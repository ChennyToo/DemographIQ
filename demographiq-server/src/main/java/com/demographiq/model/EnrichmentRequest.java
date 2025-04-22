package com.demographiq.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnrichmentRequest {
    @JsonProperty("latitude")
    private Double latitude;
    
    @JsonProperty("longitude")
    private Double longitude;
    
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("dataVariable")
    private String dataVariable;
    
    public EnrichmentRequest(
            @JsonProperty("latitude") Double latitude,
            @JsonProperty("longitude") Double longitude,
            @JsonProperty("userId") String userId,
            @JsonProperty("dataVariable") String dataVariable) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
        this.dataVariable = dataVariable;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getDataVariable() {
        return dataVariable;
    }
    
    public void setDataVariable(String dataVariable) {
        this.dataVariable = dataVariable;
    }
}