package com.demographiq.service;

import java.nio.file.Paths;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import io.github.cdimascio.dotenv.Dotenv;

public class ArcGISSDKTest {
    public static void main(String[] args) {
        try {
            Dotenv dotenv = Dotenv.load();
            String apiKey = dotenv.get("ARCGis_KEY");
            System.out.println("Loaded API Key from .env: " + apiKey);
            String repoRoot = System.getProperty("user.dir");
            String arcgisPath = Paths.get(repoRoot, "demographiq-server", ".arcgis", "200.6.0").toString();
            ArcGISRuntimeEnvironment.setInstallDirectory(arcgisPath);
            // Initialize ArcGIS Runtime with your API key
            ArcGISRuntimeEnvironment.setApiKey("YOUR_API_KEY");
            System.out.println("ArcGIS Runtime initialized successfully");
            
            // Create a map (without displaying it)
            ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);
            System.out.println("ArcGIS Map created successfully with basemap: " + map.getBasemap().getName());
            
            // Try a basic geometry operation
            Point point = new Point(-118.805, 34.027, SpatialReferences.getWgs84());
            System.out.println("Created point at: " + point.getX() + ", " + point.getY());
            
            System.out.println("ArcGIS SDK is properly installed and functioning!");
        } catch (Exception e) {
            System.err.println("Error testing ArcGIS SDK: " + e.getMessage());
            e.printStackTrace();
        }
    }
}