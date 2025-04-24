package com.demographiq.config;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

/**
 * Spring Configuration for setting up the ArcGIS API client and Runtime Environment.
 */
@Configuration
public class ArcGISConfig {

    @Value("${ARCGis_KEY}")
    private String apiKey;

    /**
     * Defines a singleton bean that initializes the ArcGIS Runtime Environment.
     * The 'initialize' method will be called by Spring after the bean is created.
     *
     * @param apiKey The ArcGIS API key injected from properties.
     * @return An instance of the ArcGISRuntimeInitializer.
     */
    @Bean(initMethod = "initialize") // Use initMethod to call the setup logic
    public ArcGISRuntimeInitializer arcGISRuntimeInitializer(@Value("${ARCGis_KEY}") String apiKey) {
        // Pass the injected values to the initializer
        return new ArcGISRuntimeInitializer(apiKey);
    }

    /**
     * Helper class to encapsulate the ArcGIS Runtime initialization logic.
     * Defined as a static inner class within the configuration.
     */
    public static class ArcGISRuntimeInitializer {

        private static final Logger logger = LoggerFactory.getLogger(ArcGISRuntimeInitializer.class);

        private final String apiKey;

        public ArcGISRuntimeInitializer(String apiKey) {
            this.apiKey = apiKey;
        }

        /**
         * Initializes the ArcGIS Runtime Environment.
         * This method is called by Spring after the bean is constructed.
         */
        public void initialize() {
            String repoRoot = System.getProperty("user.dir");
            String arcgisPath;
            if (repoRoot.endsWith("demographiq-server")) {
                arcgisPath = Paths.get(repoRoot, ".arcgis", "200.6.0").toString();
            } else {
                arcgisPath = Paths.get(repoRoot, "demographiq-server", ".arcgis", "200.6.0").toString();
            }
            try {
                ArcGISRuntimeEnvironment.setInstallDirectory(arcgisPath);
                ArcGISRuntimeEnvironment.setApiKey(apiKey);
                logger.info("ArcGIS Runtime Environment initialized successfully.");

            } catch (Exception e) {
                logger.error("Error initializing ArcGIS Runtime Environment", e);
            }
        }

    }

}
