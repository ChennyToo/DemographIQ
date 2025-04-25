package com.demographiq.model;

/**
 * An Enrichment Response object is what will be sent back to the client after an enrichment request, this will contain essentially
 * two parts, the data value for the coordinates that THEY have entered, and the current highest/lowest record for that variable.
 * For example, we want the user to know the population density of the coordinate that they have clicked on as well as the highest/lowest population density
 * that has ever been achieved by any user.
 */
public class EnrichmentResponse {
    private String sourceCountry; // e.g., "US"
    private String variableId; // e.g., "POPDENS_CY"
    private double value; // e.g., 1000.0
    private Integer score; // Out of 5000
    private ExtremeRecord currentRecord; // The current extreme record

    public EnrichmentResponse(String sourceCountry, String variableId, double value, ExtremeRecord currentRecord) {
        this.sourceCountry = sourceCountry;
        this.variableId = variableId;
        this.value = value;
        this.currentRecord = currentRecord;
    }

    

    public String toString() {
        return "EnrichmentResponse{" +
                "sourceCountry='" + sourceCountry + '\'' +
                ", variableId='" + variableId + '\'' +
                ", value=" + value +
                ", currentRecord=" + currentRecord +
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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public ExtremeRecord getCurrentRecord() {
        return currentRecord;
    }

    public void setCurrentRecord(ExtremeRecord currentRecord) {
        this.currentRecord = currentRecord;
    }
    
}
