package com.demographiq.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demographiq.model.EnrichmentRequest;
import com.demographiq.model.EnrichmentResponse;
import com.demographiq.model.ExtremeRecord;
import com.demographiq.persistence.converter.CountryNameConverter;
import com.demographiq.persistence.converter.DataVariableNameConverter;
import com.demographiq.persistence.converter.ExtremeRecordConverter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;


@Component
public class DataVariableMongoDAO {

    private static final Logger logger = LoggerFactory.getLogger(DataVariableMongoDAO.class);

    private final ExtremeRecordConverter recordConverter;
    private final CountryNameConverter countryConverter;
    private final DataVariableNameConverter dataVariableConverter;

    private final MongoDatabase database;

    //Dependency injection of MongoClient and ExtremeRecordConverter from MongoConfig.java
    @Autowired
    public DataVariableMongoDAO(MongoClient mongoClient, ExtremeRecordConverter recordConverter, CountryNameConverter countryConverter, DataVariableNameConverter dataVariableConverter) {
        this.recordConverter = recordConverter;
        this.countryConverter = countryConverter;
        this.dataVariableConverter = dataVariableConverter;
        this.database = mongoClient.getDatabase("enrichment_data");
    }

    /**
     * Retrieves the extreme value (highest or lowest) for a specific country and metric
     *
     * @param sourceCountry Country identier (e.g., "US", "CN", "RU")
     * @param variableId Metric identifier (e.g., "POPDENS_CY")
     * @param isHigh If true, retrieve from record_highs collection, else from record_lows
     * @return Optional containing the extreme record if found, empty otherwise
     */
    public Optional<ExtremeRecord> getExtremeValue(String sourceCountry, String variableId, boolean isHigh) {
        Document record = fetchMongoDocument(sourceCountry, variableId, isHigh);
        if (record != null) {
            ExtremeRecord extremeRecord = recordConverter.convertDocumentToExtremeRecord(record, isHigh);
            logger.info("Found extreme record for country: {}, variable: {}", sourceCountry, variableId);
            return Optional.of(extremeRecord);
        }

        logger.info("No record found for country: {}, variable: {}", sourceCountry, variableId);
        return Optional.empty();
    }

    //This will only be called if the user puts the marker on the country they are suppose to play in
    public boolean updateIfMoreExtreme(EnrichmentResponse response, EnrichmentRequest request, boolean isHigh) {
        String variableId = response.getVariableId();
        String gamemodeCountry = request.getSourceCountry();
        double newValue = response.getValue();
        int userId = request.getUserId();
        boolean anyRecordUpdated = false;
        
        // Get country record, this may be WORLD if that is the gamemode that is being played
        Document countryRecordDoc = fetchMongoDocument(gamemodeCountry, variableId, isHigh);
        
        // Convert documents to ExtremeRecord objects
        ExtremeRecord countryExtremeRecord = recordConverter.convertDocumentToExtremeRecord(countryRecordDoc, isHigh);
        
        // Set up coordinates from the request
        double[] coordinates = new double[] {request.getLatitude(), request.getLongitude()};
        
        // Check if country record should be updated
        if (shouldUpdateRecord(response, countryExtremeRecord, isHigh)) {
            logger.info("Updating country record for {}, variable: {}", gamemodeCountry, variableId);
            // Use your existing updateRecord method
            boolean updated = updateRecord(
                gamemodeCountry,
                countryConverter.getCountryName(gamemodeCountry),
                variableId,
                dataVariableConverter.getVariableName(variableId),
                newValue,
                userId,
                isHigh,
                coordinates
            );
            
            if (updated) {
                anyRecordUpdated = true;
                logger.info("Successfully updated country record for: {}", gamemodeCountry);
            }
        }
        
        return anyRecordUpdated;
    }

    public List<ExtremeRecord> getAllExtremes(String countryName, boolean isHigh) {
        throw new UnsupportedOperationException("Unimplemented method 'getAllExtremes'");
    }

    public List<String> getCountriesWithRecordsFor(String variableId, boolean isHigh) {
        throw new UnsupportedOperationException("Unimplemented method 'getCountriesWithRecordsFor'");
    }

    /**
     * Helper method to build the aggregation pipeline for retrieving an extreme value
     * with the previous_records array sliced.
     *
     * @param matchFilter The Bson match filter to use in the pipeline.
     * @return The list of Bson stages for the aggregation pipeline.
     */
    private List<Bson> buildExtremeValueProjectionPipeline(Bson matchFilter) {
        // Define the projection to include only necessary fields and slice the previous_records array
        // Use $slice explicitly with the field path "$previous_records" and the limit -2
        Document projectStage = new Document("$project",
                new Document("_id", 1) // Include _id
                        .append("source_country", 1)
                        .append("country_name", 1)
                        .append("metric", 1)
                        .append("metric_name", 1)
                        .append("value", 1)
                        .append("userId", 1)
                        .append("record_date", 1)
                        .append("last_updated", 1)
                        .append("location", 1)
                        .append("previous_records", new Document("$slice", Arrays.asList("$previous_records", -2)))
        );

        // Create the aggregation pipeline
        // The pipeline consists of a $match stage followed by a $project stage
        return Arrays.asList(
                new Document("$match", matchFilter),
                projectStage
        );
    }

    private Document fetchMongoDocument(String sourceCountry, String variableId, boolean isHigh) {
        String collectionName = isHigh ? "record_highs" : "record_lows";
        MongoCollection<Document> collection = database.getCollection(collectionName);

        // Use helper methods to build the query components
        Bson matchFilter = Filters.and(
            Filters.eq("source_country", sourceCountry),
            Filters.eq("metric", variableId)
        );
        List<Bson> pipeline = buildExtremeValueProjectionPipeline(matchFilter);

        // Execute the aggregation pipeline
        Document mongoDoc = collection.aggregate(pipeline).first();
        return mongoDoc;
    }

    private boolean updateRecord(String sourceCountry, String countryName, String variableId, 
                            String variableName, double newValue, int userId, boolean isHigh,
                            double[] coordinates) {
        try {
            String collectionName = isHigh ? "record_highs" : "record_lows";
            MongoCollection<Document> collection = database.getCollection(collectionName);
            Date currentTime = new Date();
            
            // Check if record exists
            Document existingRecord = fetchMongoDocument(sourceCountry, variableId, isHigh);
            
            if (existingRecord != null) {
                // Create a document from the current values to add to previous_records
                Document previousRecord = new Document()
                    .append("value", existingRecord.get("value"))
                    .append("record_date", existingRecord.getDate("record_date"))
                    .append("userId", existingRecord.getInteger("userId"));
                
                // Build update operations
                List<Bson> updates = new ArrayList<>();
                updates.add(Updates.set("value", newValue));
                updates.add(Updates.set("userId", userId));
                updates.add(Updates.set("record_date", currentTime));
                updates.add(Updates.set("last_updated", currentTime));
                updates.add(Updates.push("previous_records", previousRecord));
                if (coordinates != null && coordinates.length == 2) {
                    Document locationDoc = new Document()
                        .append("type", "Point")
                        .append("coordinates", Arrays.asList(coordinates[0], coordinates[1]));
                    updates.add(Updates.set("location", locationDoc));
                }
                
                // Perform the update
                UpdateResult result = collection.updateOne(
                    Filters.and(
                        Filters.eq("source_country", sourceCountry),
                        Filters.eq("metric", variableId)
                    ),
                    Updates.combine(updates)
                );
                
                logger.info("Updated extreme record for country: {}, variable: {}. Modified count: {}", 
                        sourceCountry, variableId, result.getModifiedCount());
                
                return result.getModifiedCount() > 0;
            } else {
                // Create a new record
                Document locationDoc = null;
                if (coordinates != null && coordinates.length == 2) {
                    locationDoc = new Document()
                        .append("type", "Point")
                        .append("coordinates", Arrays.asList(coordinates[0], coordinates[1]));
                }
                
                Document newRecord = new Document()
                    .append("source_country", sourceCountry)
                    .append("country_name", countryName)
                    .append("metric", variableId)
                    .append("metric_name", variableName)
                    .append("value", newValue)
                    .append("userId", userId)
                    .append("record_date", currentTime)
                    .append("previous_records", new ArrayList<>())
                    .append("last_updated", currentTime);
                
                if (locationDoc != null) {
                    newRecord.append("location", locationDoc);
                    logger.info(locationDoc.toString());
                }
                
                collection.insertOne(newRecord);
                
                logger.info("Created new extreme record for country: {}, variable: {}", 
                        sourceCountry, variableId);
                
                return true;
            }
        } catch (Exception e) {
            logger.error("Error updating extreme record: {}", e.getMessage(), e);
            return false;
        }
}

    /**
     * Determines if the current extreme record should be updated based on new data.
     * For high records: update if new value > current value
     * For low records: update if new value < current value
     *
     * @param response The new data coming from the enrichment API
     * @param currentRecord The existing extreme record from the database
     * @param isHigh Whether we're checking for a high or low record
     * @return true if the record should be updated, false otherwise
     */
    private boolean shouldUpdateRecord(EnrichmentResponse response, ExtremeRecord currentRecord, boolean isHigh) {
        // If no current record exists, we should definitely update
        if (currentRecord == null) {
            return true;
        }
        
        double newValue = response.getValue();
        double currentValue = currentRecord.getValue();
        
        if (isHigh) {
            // For high records, update if the new value is greater
            return newValue > currentValue;
        } else {
            // For low records, update if the new value is less
            return newValue < currentValue;
        }
    }
}
