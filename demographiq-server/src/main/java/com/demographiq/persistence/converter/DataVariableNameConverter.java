package com.demographiq.persistence.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for converting data variable IDs to their human-readable names
 * and vice versa.
 */
@Component
public class DataVariableNameConverter {
    private static final Logger logger = LoggerFactory.getLogger(DataVariableNameConverter.class);

    private static final Map<String, String> VARIABLE_ID_TO_NAME;

    static {
        Map<String, String> idToName = new HashMap<>();
        
        // Add variable ID mappings as needed
        idToName.put("POPDENS_CY", "Population Density");
        
        VARIABLE_ID_TO_NAME = Collections.unmodifiableMap(idToName);
    }
    
    /**
     * Converts a variable ID to its corresponding human-readable name.
     *
     * @param variableId The variable identifier (e.g., "POPDENS_CY")
     * @return The human-readable name of the variable
     * @throws IllegalArgumentException if the variable ID is not found in the map
     */
    public String getVariableName(String variableId) {
        if (variableId == null) {
            throw new IllegalArgumentException("Variable ID cannot be null");
        }
        
        if (!VARIABLE_ID_TO_NAME.containsKey(variableId)) {
            String errorMessage = "Unknown variable ID: " + variableId;
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        
        return VARIABLE_ID_TO_NAME.get(variableId);
    }
}