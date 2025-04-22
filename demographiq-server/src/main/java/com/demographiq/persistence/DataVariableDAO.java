package com.demographiq.persistence;

import java.util.List;
import java.util.Optional;

import com.demographiq.model.ExtremeRecord;

public interface DataVariableDAO {
    
    /**
     * Country code representing worldwide/global data
     */
    String GLOBAL_COUNTRY_CODE = "000";
    
    /**
     * Get the extreme recorded value for a specific variable in a country or globally
     * 
     * @param countryCode The ISO country code or GLOBAL_COUNTRY_CODE for worldwide data
     * @param variableId The ID of the data variable (e.g., "POPDENS_CY")
     * @param isHigh True for highest value, false for lowest value
     * @return The extreme record or empty if no data exists
     */
    Optional<ExtremeRecord> getExtremeValue(String countryCode, String variableId, boolean isHigh);
    
    /**
     * Check if value is more extreme than current record and update if it is
     * 
     * @param record The new record candidate (contains countryCode field)
     * @param isHigh True to check for higher values, false for lower values
     * @return true if record was updated, false if not more extreme
     */
    boolean updateIfMoreExtreme(ExtremeRecord record, boolean isHigh);
    
    /**
     * Get all extreme values for all variables for a specific country or globally
     * 
     * @param countryCode The ISO country code or GLOBAL_COUNTRY_CODE for worldwide data
     * @param isHigh True for highest values, false for lowest values
     * @return List of all extreme records
     */
    List<ExtremeRecord> getAllExtremes(String countryCode, boolean isHigh);
    
    /**
     * Get all countries that have records for a specific variable
     * 
     * @param variableId The ID of the data variable
     * @param isHigh True for highest values, false for lowest values
     * @return List of country codes that have records for this variable
     */
    List<String> getCountriesWithRecordsFor(String variableId, boolean isHigh);
}