package com.demographiq.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ArcGISService {
    @Value("${arcgis.client.id}")
    private String clientId;
    
    @Value("${arcgis.client.secret}")
    private String clientSecret;
    
    private static final String ARCGIS_TOKEN_URL = "https://www.arcgis.com/sharing/rest/oauth2/token";
    private static final String ARCGIS_ENRICHMENT_URL = "https://geoenrich.arcgis.com/arcgis/rest/services/World/geoenrichmentserver/GeoEnrichment/enrich";
    
    private String accessToken;
    
    @PostConstruct
    public void init() {
        this.accessToken = getAccessToken();
    }
    
    public String enrichAddress(String address) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = ARCGIS_ENRICHMENT_URL + "?studyAreas=[{\"address\":{\"text\":\"" + 
                        address + "\"}}]&f=json&token=" + accessToken;
            
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);
            
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Failed to enrich address\"}";
        }
    }
    
    private String getAccessToken() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = ARCGIS_TOKEN_URL + "?client_id=" + clientId + 
                         "&client_secret=" + clientSecret + 
                         "&grant_type=client_credentials";
            
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);
            
            String responseBody = EntityUtils.toString(response.getEntity());
            // Simple JSON parsing - consider using Jackson for more robust parsing
            return responseBody.split("\"access_token\":\"")[1].split("\"")[0];
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get ArcGIS access token");
        }
    }
}