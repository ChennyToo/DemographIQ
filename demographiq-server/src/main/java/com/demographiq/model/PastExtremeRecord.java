package com.demographiq.model;

import java.time.LocalDateTime;

/**
 * Simple class to represent a past extreme record value
 */
public class PastExtremeRecord {
    private double value;           // The past extreme value
    private Integer userId;          // User who recorded this value
    private LocalDateTime recordedAt; // When this record was set
    
    // Default constructor
    public PastExtremeRecord() {
    }
    
    public PastExtremeRecord(double value, Integer userId, LocalDateTime recordedAt) {
        this.value = value;
        this.userId = userId;
        this.recordedAt = recordedAt;
    }
    
    // Getters and setters
    public double getValue() {
        return value;
    }
    
    public void setValue(double value) {
        this.value = value;
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
    
    @Override
    public String toString() {
        return "PastExtremeRecord{" +
                "value=" + value +
                ", userId='" + userId + '\'' +
                ", recordedAt=" + recordedAt +
                '}';
    }
}