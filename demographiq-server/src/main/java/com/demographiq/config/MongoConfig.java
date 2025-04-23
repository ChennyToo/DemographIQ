package com.demographiq.config;

import com.demographiq.persistence.converter.ExtremeRecordConverter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration class for setting up the MongoDB client.
 * This class defines a singleton MongoClient bean for the application.
 */
@Configuration
public class MongoConfig {
    // Inject MongoDB connection details from application properties
    @Value("${MongoDB_USER}")
    private String mongoDbUsername;

    @Value("${MongoDB_PASSWORD}")
    private String mongoDbPassword;


    /**
     * Defines a singleton MongoClient bean.
     * Spring will manage the lifecycle of this client, including connection pooling.
     *
     * @return The configured MongoClient instance.
     */
    @Bean
    public MongoClient mongoClient() {
        String connectionUri = String.format(
            "mongodb+srv://%s:%s@demographiq.cbhbhvx.mongodb.net/?retryWrites=true&w=majority&appName=Demographiq",
            mongoDbUsername, mongoDbPassword
        );
        return MongoClients.create(connectionUri);
    }


    /**
     * Defines a singleton ExtremeRecordConverter bean.
     * This converter is used for converting MongoDB documents to ExtremeRecord objects.
     *
     * @return The configured ExtremeRecordConverter instance.
     */
    @Bean
    public ExtremeRecordConverter extremeRecordConverter() {
        return new ExtremeRecordConverter();
    }
}
