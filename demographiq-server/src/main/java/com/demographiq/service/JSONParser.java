package com.demographiq.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.demographiq.model.EnrichmentResponse;
import com.demographiq.model.ExtremeRecord;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JSONParser {
    
    /**
     * Extract the FeatureSet from a GeoEnrichment service response
     * 
     * @param jsonResponse The JSON response string from the GeoEnrichment service
     * @return A list of features (as maps), or an empty list if none found
     * @throws IOException If JSON parsing fails
     */
    public static EnrichmentResponse extractAttributeData(String jsonResponse, String dataVariable) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonResponse);
        JsonNode featureSetNode = rootNode.path("results")
        .path(0)
        .path("value")
        .path("FeatureSet");
        
        if (featureSetNode.size() == 0) {
            System.out.println("No attribute data for this coordinate");
            return null;
        }

        JsonNode attributesNode = featureSetNode.get(0).path("features").get(0).path("attributes");
        double attributeValue = attributesNode.path(dataVariable).asDouble(0.0);
        String sourceCountry = attributesNode.path("sourceCountry").asText("Unknown");
        System.out.println("FeatureSet attributes: " + attributesNode.path(dataVariable).toString());
        EnrichmentResponse validResponse = new EnrichmentResponse(sourceCountry, dataVariable, attributeValue, new ExtremeRecord());
        return validResponse;
    }
    
    // // Example usage
    // @SuppressWarnings("CallToPrintStackTrace")
    // public static void main(String[] args) {
    //     String jsonResponse = "{\"results\":[{\"paramName\":\"GeoEnrichmentResult\",\"dataType\":\"GeoEnrichmentResult\",\"value\":{\"version\":\"2.0\",\"FeatureSet\":[]}}],\"messages\":[{\"type\":\"esriJobMessageTypeError\",\"id\":20010604,\"description\":\"Unable to detect country for study area at [0].\"}]}";
        
    //     try {
    //         extractAttributeData(jsonResponse, "POPDENS_CY");
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
}
