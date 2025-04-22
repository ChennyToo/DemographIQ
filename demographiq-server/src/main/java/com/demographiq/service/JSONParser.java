package com.demographiq.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

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
    public static double extractAttributeData(String jsonResponse, String dataVariable) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonResponse);
        JsonNode featureSetNode = rootNode.path("results")
        .path(0)
        .path("value")
        .path("FeatureSet");
        
        if (featureSetNode.size() == 0) {
            System.out.println("No attribute data for this coordinate");
            return 0.0;
        }

        JsonNode attributesNode = featureSetNode.get(0).path("features").get(0).path("attributes");
        double attributeValue = attributesNode.path(dataVariable).asDouble(0.0);
        System.out.println("FeatureSet attributes: " + attributesNode.path(dataVariable).toString());
        return attributeValue;
    }
    
    // Example usage
    public static void main(String[] args) {
        String jsonResponse = "{\"results\":[{\"paramName\":\"GeoEnrichmentResult\",\"dataType\":\"GeoEnrichmentResult\",\"value\":{\"version\":\"2.0\",\"FeatureSet\":[]}}],\"messages\":[{\"type\":\"esriJobMessageTypeError\",\"id\":20010604,\"description\":\"Unable to detect country for study area at [0].\"}]}";
        
        try {
            extractAttributeData(jsonResponse, "POPDENS_CY");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
