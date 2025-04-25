package com.demographiq.persistence.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.demographiq.persistence.DataVariableMongoDAO;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for converting country codes to full country names
 * and vice versa.
 */
@Component
public class CountryNameConverter {
    private static final Logger logger = LoggerFactory.getLogger(CountryNameConverter.class);

    private static final Map<String, String> COUNTRY_CODE_TO_NAME;

    static {
        Map<String, String> codeToName = new HashMap<>();
        
        codeToName.put("US", "United States");
        codeToName.put("WORLD", "Global");
        COUNTRY_CODE_TO_NAME = Collections.unmodifiableMap(codeToName);
    }
    
/**
 * Converts a country code to its corresponding full name.
 *
 * @param countryCode The source country code (e.g., "US", "CA")
 * @return The full country name, or the original code if not found
 * @throws IllegalArgumentException if the country code is not found in the map
 */
public String getCountryName(String countryCode) {
    if (countryCode == null) {
        throw new IllegalArgumentException("Country code cannot be null");
    }
    
    String upperCode = countryCode.trim().toUpperCase();
    if (!COUNTRY_CODE_TO_NAME.containsKey(upperCode)) {
        String errorMessage = "Unknown country code: " + upperCode;
        // Add logging (you'll need to add a logger field to the class)
        logger.error(errorMessage);
        throw new IllegalArgumentException(errorMessage);
    }
    
    return COUNTRY_CODE_TO_NAME.get(upperCode);
}
}