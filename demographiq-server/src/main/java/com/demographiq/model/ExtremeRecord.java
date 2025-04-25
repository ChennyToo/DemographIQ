package com.demographiq.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExtremeRecord {
    private String id;                        // document ID
    private String variableId;                // e.g., "POPDENS_CY" 
    private String variableName;              // e.g., "Population Density"
    private double value;                     // The extreme value
    private double latitude;                  // Location latitude
    private double longitude;                 // Location longitude
    private String countryCode;               // Country code (e.g., "US")
    private String countryName;               // Country name
    private Integer userId;                    // User who discovered this record
    private LocalDateTime recordedAt;         // When this record was set
    private boolean isHigh;                   // True if high value, false if low
    private List<PastExtremeRecord> previousRecords; // Previous records
    
    // Default constructor
    public ExtremeRecord() {
        this.previousRecords = new ArrayList<>();
    }
    
    // Constructor with all fields
    public ExtremeRecord(
            String variableId, 
            String variableName, 
            double value, 
            double latitude, 
            double longitude,
            String countryCode,
            String countryName, 
            Integer userId, 
            boolean isHigh) {
        this.variableId = variableId;
        this.variableName = variableName;
        this.value = value;
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.userId = userId;
        this.recordedAt = LocalDateTime.now();
        this.isHigh = isHigh;
        this.previousRecords = new ArrayList<>();
    }

    public boolean isEmpty() {
        // This will only be true is there are no Document fetched from MongoDB
        return Objects.isNull(this.id) &&
               Objects.isNull(this.variableId) &&
               Objects.isNull(this.variableName) &&
               this.value == 0.0;
    }
    
    
    public List<PastExtremeRecord> getPreviousRecords() {
        return previousRecords;
    }
    
    public void setPreviousRecords(List<PastExtremeRecord> previousRecords) {
        this.previousRecords = previousRecords != null ? previousRecords : new ArrayList<>();
    }
    
    public void addPastRecord(double value, Integer userId, LocalDateTime recordedAt) {
        if (this.previousRecords == null) {
            this.previousRecords = new ArrayList<>();
        }
        this.previousRecords.add(new PastExtremeRecord(value, userId, recordedAt));
    }
    
    public PastExtremeRecord toPastRecord() {
        return new PastExtremeRecord(this.value, this.userId, this.recordedAt);
    }

    @Override
    public String toString() {
        return "ExtremeRecord{" +
                "id='" + id + '\'' +
                ", variableId='" + variableId + '\'' +
                ", variableName='" + variableName + '\'' +
                ", value=" + value +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", countryCode='" + countryCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", userId='" + userId + '\'' +
                ", recordedAt=" + recordedAt +
                ", isHigh=" + isHigh +
                ", previousRecordsCount=" + (previousRecords != null ? previousRecords.size() : 0) +
                ", previousRecords=" + previousRecords +
                '}';
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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

    public void setIsHigh(boolean isHigh) {
        this.isHigh = isHigh;
    }
}