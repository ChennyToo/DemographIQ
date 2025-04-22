// package com.demographiq.service;

// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.net.HttpURLConnection;
// import java.net.URL;
// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;
// import java.nio.file.Paths;

// import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;

// import io.github.cdimascio.dotenv.Dotenv;

// public class ArcGISSDKTest {
//     public static void main(String[] args) {
//         // boolean canPerformApiCall = RedisApiThrottler.registerApiCall("admin");
//         boolean canPerformApiCall = true;
//         String apiKey = getApiKey();
//         setupArcGisRuntime(apiKey);
//         try {
//             if (canPerformApiCall) {
//                 System.out.println("API call allowed, proceeding with test...");
//                 testApiKey(apiKey);
//             } else {
//                 System.out.println("API call limit exceeded. Cannot perform API call.");
//             }
//         } catch (Exception e) {
//             System.err.println("Error testing ArcGIS SDK: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     private static void setupArcGisRuntime(String apiKey) {
//         String repoRoot = System.getProperty("user.dir");
//         String arcgisPath = Paths.get(repoRoot, "demographiq-server", ".arcgis", "200.6.0").toString();
//         ArcGISRuntimeEnvironment.setInstallDirectory(arcgisPath);
//         // Initialize ArcGIS Runtime with your API key
//         ArcGISRuntimeEnvironment.setApiKey(apiKey);
//         System.out.println("ArcGIS Runtime initialized successfully");
//     }

//     public static String getApiKey() {
//         Dotenv dotenv = Dotenv.load();
//         String apiKey = dotenv.get("ARCGis_KEY");
//         System.out.println("Loaded API Key from .env: " + apiKey);
//         return apiKey;
//     }


//     private static void testApiKey(String apiKey) throws Exception {
//     double latitude = 37.7749; // Example latitude (San Francisco)
//     double longitude = -122.4194; // Example longitude (San Francisco)

//     // Create the JSON string
//     String studyAreasJson = "[{\"geometry\":{\"x\":" + longitude + ",\"y\":" + latitude + "}}]";

//     // URL encode the JSON parameter
//     String encodedStudyAreas = URLEncoder.encode(studyAreasJson, StandardCharsets.UTF_8.toString());
//     String encodedVariable = URLEncoder.encode("[\"" + "POPDENS_CY" + "\"]", "UTF-8");

//     String urlString = "https://geoenrich.arcgis.com/arcgis/rest/services/World/geoenrichmentserver/GeoEnrichment/enrich"
//         + "?studyAreas=" + encodedStudyAreas
//         + "&analysisVariables=" + encodedVariable
//         + "&f=json"
//         + "&token=" + apiKey;  

//     URL url = new URL(urlString);
//     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//     conn.setRequestMethod("GET");
    
//     int responseCode = conn.getResponseCode();
//     System.out.println("API Key Validation Response Code: " + responseCode);
    
//     try (BufferedReader in = new BufferedReader(new InputStreamReader(
//             responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream()))) {
//         String inputLine;
//         StringBuilder response = new StringBuilder();
        
//         while ((inputLine = in.readLine()) != null) {
//             response.append(inputLine);
//         }
        
//         System.out.println("Response:");
//         JSONParser.extractAttributeData(response.toString());
        
//         if (response.toString().contains("error")) {
//             System.out.println("API response failure");
//         } else {
//             System.out.println("API response successful");
//         }
//     }
// }
// }