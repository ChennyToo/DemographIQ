package com.demographiq.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.demographiq.model.EnrichmentRequest;
import com.demographiq.model.EnrichmentResponse;
import com.demographiq.model.ExtremeRecord;
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
    public EnrichmentResponse enrichLocation(EnrichmentRequest request) {
        double latitude = request.getLatitude();
        double longitude = request.getLongitude();
        int userId = request.getUserId();
        String dataVariable = request.getDataVariable();
        boolean isHigh = request.isHigh();

        //Will throw exception if anything is invalid
        validateApiCall(latitude, longitude, userId);
        
        try {
            EnrichmentResponse response = callArcGisApi(latitude, longitude, dataVariable);
            //We send in the request sourceCountry aka the gamemodeCountry, this ensures that we are grabbing from "WORLD" is we are playin on global game mode
            Optional<ExtremeRecord> record = dataVariableMongoDAO.getExtremeValue(request.getSourceCountry(), dataVariable, isHigh);
            //Current record will be an existing record from MongoDB or we make an empty ExrtremeRecord instance to signify that there is no record yet stored in MongoDB
            response.setCurrentRecord(record.orElse(new ExtremeRecord()));
            if (isCorrectCountry(response, request)) {
                dataVariableMongoDAO.updateIfMoreExtreme(response, request, isHigh);
                response.setScore(getScore(response, request));
                return response;
            } 

            else {
                response.setScore(0);
                return response;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error calling ArcGIS API: " + e.getMessage(), e);
        }
    }
    
    private EnrichmentResponse callArcGisApi(double latitude, double longitude, String dataVariable) throws Exception {
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
            logger.info("Response from ArcGIS API: " + responseStr);
            
            // Check if the response contains an error
            if (responseStr.contains("error")) {
                throw new RuntimeException("ArcGIS API returned an error: " + responseStr);
            }
            
            // Extract and process the data using JsonParser
            // Response is incomplete because it does not contain the ExtremeRecord yet from MongoDB
            EnrichmentResponse incompleteResponse = JSONParser.extractAttributeData(responseStr, dataVariable);
            return incompleteResponse;
            // dataVariableMongoDAO.getExtremeValue("US", "POPDENS_CY", true);
            // return "hi";
        }
    }

    private URL getApiUrl(double latitude, double longitude, String dataVariable) throws Exception {
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

    private void validateApiCall(double latitude, double longitude, int userId) {
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

    private boolean isCorrectCountry( EnrichmentResponse response, EnrichmentRequest request) {
        //Given that the user is playing on global game mode, this is fine
        if ("WORLD".equals(request.getSourceCountry())) {
            logger.info("User is playing on global game mode.");
            return true;
        }

        //Given the user is playing on Japan game mode, and they guess somewhere in United States, issues arise
        //We must not allow for the database to be updated with a US record, when the user is playing on Japan game mode
        else if (response.getSourceCountry().equals(request.getSourceCountry())) {
            logger.info("Request and response countries match as " + request.getSourceCountry());
            return true;
        } 
        
        else {
            logger.info("Request source country of " + request.getSourceCountry() + " does not match response source country of " + response.getSourceCountry());
            return false;
        }
    }

    private Integer getScore(EnrichmentResponse response, EnrichmentRequest request) {
        if (response.getCurrentRecord().isEmpty()) {
            return 5000; // No previous record, so the user is the first to set this record
        }

        boolean isHigh = request.isHigh();
        double clientValue = response.getValue();
        double databaseValue = response.getCurrentRecord().getValue();
        double calculatedScore;
    
        if (isHigh) {
            // Calculate score for 'high' scenario
            calculatedScore = (clientValue / databaseValue) * 5000;
        } else {
            // Calculate score for 'low' scenario
            calculatedScore = (databaseValue / clientValue) * 5000;
        }
    
        // Round up to the nearest integer
        int roundedScore = (int) Math.ceil(calculatedScore);
    
        // Ensure the score never exceeds 5000
        int finalScore = Math.min(roundedScore, 5000);
    
        return finalScore;
    }
}