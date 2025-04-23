package com.demographiq.model;

public class EnrichmentResponse {
    private String sourceCountry; // e.g., "US"
    private String variableId; // e.g., "POPDENS_CY"
    private double value; // e.g., 1000.0

    public EnrichmentResponse(String sourceCountry, String variableId, double value) {
        this.sourceCountry = sourceCountry;
        this.variableId = variableId;
        this.value = value;
    }

    public String toString() {
        return "EnrichmentResponse{" +
                "sourceCountry='" + sourceCountry + '\'' +
                ", variableId='" + variableId + '\'' +
                ", value=" + value +
                '}';
    }

    public String getSourceCountry() {
        return sourceCountry;
    }

    public void setSourceCountry(String sourceCountry) {
        this.sourceCountry = sourceCountry;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    
}
