package com.demographiq.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.demographiq.model.EnrichmentRequest;
import com.demographiq.persistence.DataVariableMongoDAO;


@Service
public class ArcGISService {
    private static final Logger logger = LoggerFactory.getLogger(ArcGISService.class);
    private final RedisApiThrottler redisApiThrottler;
    private final DataVariableMongoDAO dataVariableMongoDAO;

    @Value("${ARCGis_KEY}")
    private String apiKey;

    @Autowired
    public ArcGISService(RedisApiThrottler redisApiThrottler, DataVariableMongoDAO dataVariableMongoDAO) {
        this.redisApiThrottler = redisApiThrottler;
        this.dataVariableMongoDAO = dataVariableMongoDAO;
    }

    /**
     * Enriches a location with demographic data based on its coordinates
     * 
     * @param latitude The latitude of the location (-90 to +90 degrees)
     * @param longitude The longitude of the location (-180 to +180 degrees)
     * @return Enriched demographic data for the location
     * @throws IllegalArgumentException if latitude or longitude are outside valid ranges
     * @throws RuntimeException if API calls are throttled or if there's an API error
     */
    public String enrichLocation(EnrichmentRequest request) {
        double latitude = request.getLatitude();
        double longitude = request.getLongitude();
        String userId = request.getUserId();
        String dataVariable = request.getDataVariable();

        //Will throw exception if anything is invalid
        validateApiCall(latitude, longitude, userId);
        
        try {
            return callArcGisApi(latitude, longitude, dataVariable);
        } catch (Exception e) {
            throw new RuntimeException("Error calling ArcGIS API: " + e.getMessage(), e);
        }
    }
    
    private String callArcGisApi(double latitude, double longitude, String dataVariable) throws Exception {
        URL url = getApiUrl(latitude, longitude, dataVariable);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        int responseCode = conn.getResponseCode();
        
        try (BufferedReader in = new BufferedReader(new InputStreamReader(responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            
            String responseStr = response.toString();
            
            // Check if the response contains an error
            if (responseStr.contains("error")) {
                throw new RuntimeException("ArcGIS API returned an error: " + responseStr);
            }
            
            // Extract and process the data using JsonParser
            return JSONParser.extractAttributeData(responseStr, dataVariable) + "";
            // logger.info("checkpoint 1");
            // dataVariableMongoDAO.getExtremeValue("US", "POPDENS_CY", true);
            // return "hi";
        }
    }

    public URL getApiUrl(double latitude, double longitude, String dataVariable) throws Exception {
        // Create the JSON string for study areas
        String studyAreasJson = "[{\"geometry\":{\"x\":" + longitude + ",\"y\":" + latitude + "}}]";
        
        // URL encode the JSON parameter
        String encodedStudyAreas = URLEncoder.encode(studyAreasJson, StandardCharsets.UTF_8.toString());
        String encodedVariable = URLEncoder.encode("[\"" + dataVariable + "\"]", "UTF-8");
        
        String urlString = "https://geoenrich.arcgis.com/arcgis/rest/services/World/geoenrichmentserver/GeoEnrichment/enrich"
            + "?studyAreas=" + encodedStudyAreas
            + "&analysisVariables=" + encodedVariable
            + "&f=json"
            + "&token=" + apiKey;
            
        return URI.create(urlString).toURL();
    }

    public void validateApiCall(double latitude, double longitude, String userId) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and +90 degrees");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and +180 degrees");
        }
        boolean hasCallsLeft = redisApiThrottler.registerApiCall(userId);
        if (!hasCallsLeft) {
            throw new RuntimeException("API call limit exceeded. Please try again later.");
        }
    }
}