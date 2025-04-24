package com.demographiq.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnrichmentRequest {
    @JsonProperty("latitude")
    private Double latitude;
    
    @JsonProperty("longitude")
    private Double longitude;
    
    @JsonProperty("userId")
    private Integer userId;
    
    @JsonProperty("dataVariable")
    private String dataVariable;

    @JsonProperty("isHigh")
    private boolean isHigh;

    public EnrichmentRequest(
            @JsonProperty("latitude") Double latitude,
            @JsonProperty("longitude") Double longitude,
            @JsonProperty("userId") Integer userId,
            @JsonProperty("dataVariable") String dataVariable,
            @JsonProperty("isHigh") boolean isHigh) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
        this.dataVariable = dataVariable;
        this.isHigh = isHigh;
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
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getDataVariable() {
        return dataVariable;
    }
    
    public void setDataVariable(String dataVariable) {
        this.dataVariable = dataVariable;
    }

    public boolean isHigh() {
        return isHigh;
    }

    public void setHigh(boolean isHigh) {
        this.isHigh = isHigh;
    }
}