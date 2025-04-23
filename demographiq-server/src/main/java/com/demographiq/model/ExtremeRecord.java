package com.demographiq.model;

import java.time.LocalDateTime;

public class ExtremeRecord {
    private String id;                // document ID
    private String variableId;        // e.g., "POPDENS_CY"
    private String variableName;      // e.g., "Population Density"
    private double value;             // The extreme value
    private double latitude;          // Location latitude
    private double longitude;         // Location longitude
    private String countryName;       // Country name
    private String userId;          // User who discovered this record
    private LocalDateTime recordedAt; // When this record was set
    private boolean isHigh;           // True if this is a high value (find max population density), false if low (find min population density)
    
    public ExtremeRecord(
            String variableId, 
            String variableName, 
            double value, 
            double latitude, 
            double longitude, 
            String countryName, 
            String userId, 
            boolean isHigh) {
        this.variableId = variableId;
        this.variableName = variableName;
        this.value = value;
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryName = countryName;
        this.userId = userId;
        this.recordedAt = LocalDateTime.now();
        this.isHigh = isHigh;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getVariableId() {
        return variableId;
    }
    
    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }
    
    public String getVariableName() {
        return variableName;
    }
    
    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setValue(double value) {
        this.value = value;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public String getcountryName() {
        return countryName;
    }
    
    public void setcountryName(String countryName) {
        this.countryName = countryName;
    }
    
    public String getUsername() {
        return userId;
    }
    
    public void setUsername(String userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
    
    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public boolean isHigh() {
        return isHigh;
    }

    public void setHigh(boolean isHigh) {
        this.isHigh = isHigh;
    }
}