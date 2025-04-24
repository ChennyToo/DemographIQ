package com.demographiq.persistence;

import java.util.List;
import java.util.Optional;

import com.demographiq.model.EnrichmentResponse;
import com.demographiq.model.ExtremeRecord;

public interface DataVariableDAO {
    
    /**
     * Get the extreme recorded value for a specific variable in a country or globally
     * 
     * @param countryName The country name
     * @param variableId The ID of the data variable (e.g., "POPDENS_CY")
     * @param isHigh True for highest value, false for lowest value
     * @return The extreme record or empty if no data exists
     */
    Optional<ExtremeRecord> getExtremeValue(String countryName, String variableId, boolean isHigh);
    

    boolean updateIfMoreExtreme(EnrichmentResponse response);
    
    /**
     * Get all extreme values for all variables for a specific country or globally
     * 
     * @param countryName The country name
     * @param isHigh True for highest values, false for lowest values
     * @return List of all extreme records
     */
    List<ExtremeRecord> getAllExtremes(String countryName, boolean isHigh);
    
    /**
     * Get all countries that have records for a specific variable
     * 
     * @param variableId The ID of the data variable
     * @param isHigh True for highest values, false for lowest values
     * @return List of country codes that have records for this variable
     */
    List<String> getCountriesWithRecordsFor(String variableId, boolean isHigh);
}