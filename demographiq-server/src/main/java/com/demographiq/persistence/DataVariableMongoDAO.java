package com.demographiq.persistence;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.demographiq.model.ExtremeRecord;
import com.demographiq.model.PastExtremeRecord;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

@Component
public class DataVariableMongoDAO implements DataVariableDAO {

    private static final Logger logger = LoggerFactory.getLogger(DataVariableMongoDAO.class);

    @Value("${MongoDB_USER}")
    private String mongoDbUsername;
    
    @Value("${MongoDB_PASSWORD}")
    private String mongoDbPassword;

    public static void main(String[] args) {
        String uri = "mongodb+srv://user:pass@demographiq.cbhbhvx.mongodb.net/?retryWrites=true&w=majority&appName=Demographiq";
       try (MongoClient mongoClient = MongoClients.create(uri)) {
            // Select the database and collection
            MongoDatabase database = mongoClient.getDatabase("enrichment_data");
            MongoCollection<Document> collection = database.getCollection("record_highs");
            
            // Find the current record
            Document currentRecord = collection.find(
                Filters.and(
                    Filters.eq("source_country", "US"),
                    Filters.eq("metric", "POPDENS_CY")
                )
            ).first();
            
            if (currentRecord != null) {
                // Create a document for the previous record (current record becoming previous)
                Document previousRecord = new Document()
                    .append("value", currentRecord.getInteger("value"))
                    .append("record_date", currentRecord.getDate("record_date"))
                    .append("userId", currentRecord.getInteger("userId"));
                
                // Create update operations
                Bson updates = Updates.combine(
                    // Set new record values
                    Updates.set("value", 30000),
                    Updates.set("userId", 30),
                    Updates.set("record_date", new Date()),
                    Updates.set("last_updated", new Date()),
                    
                    // Add current record to beginning of previous_records array
                    Updates.push("previous_records", previousRecord)
                );
                
                // Update the document
                UpdateResult result = collection.updateOne(
                    Filters.and(
                        Filters.eq("source_country", "US"),
                        Filters.eq("metric", "POPDENS_CY")
                    ),
                    updates
                );
                
                if (result.getModifiedCount() > 0) {
                    System.out.println("Record updated successfully!");
                } else {
                    System.out.println("No documents were updated");
                }
            } else {
                System.out.println("No record found for US population density");
                
                // Optional: Insert a new record if none exists
                // This code would run if there's no existing record
                Document newRecord = new Document()
                    .append("source_country", "US")
                    .append("country_name", "United States")
                    .append("metric", "POPDENS_CY")
                    .append("metric_name", "Population Density")
                    .append("value", 40000)
                    .append("userId", 30)
                    .append("record_date", new Date())
                    .append("previous_records", new java.util.ArrayList<>())
                    .append("location", new Document()
                        .append("type", "Point")
                        .append("coordinates", java.util.Arrays.asList(-122.4194, 37.7749)))
                    .append("last_updated", new Date());
                
                collection.insertOne(newRecord);
                System.out.println("New record inserted!");
            }
        } catch (Exception e) {
            System.err.println("Error updating record: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private MongoDatabase getMongoDatabase() {
        logger.info(mongoDbPassword);
        logger.info(mongoDbUsername);
        String connectionUri = String.format(
            "mongodb+srv://%s:%s@demographiq.cbhbhvx.mongodb.net/?retryWrites=true&w=majority&appName=Demographiq",
            mongoDbUsername, mongoDbPassword
        );
        MongoClient mongoClient = MongoClients.create(connectionUri);
        MongoDatabase database = mongoClient.getDatabase("enrichment_data");
        return database;
    }



/**
 * Retrieves the extreme value (highest or lowest) for a specific country and metric
 * 
 * @param sourceCountry ISO country code (e.g., "US", "CN", "RU")
 * @param variableId Metric identifier (e.g., "POPDENS_CY")
 * @param isHigh If true, retrieve from record_highs collection, else from record_lows
 * @return Optional containing the extreme record if found, empty otherwise
 */
@Override
public Optional<ExtremeRecord> getExtremeValue(String sourceCountry, String variableId, boolean isHigh) {
    MongoDatabase database = getMongoDatabase();
    
    // Determine which collection to query based on whether we want record highs or lows
    String collectionName = isHigh ? "record_highs" : "record_lows";
    MongoCollection<Document> collection = database.getCollection(collectionName);
    
    // Find record by source country and metric
    Document record = collection.find(
        Filters.and(
            Filters.eq("source_country", sourceCountry),
            Filters.eq("metric", variableId)
        )
    ).first();
    
    // Convert document to ExtremeRecord if found
    if (record != null) {
        ExtremeRecord extremeRecord = convertDocumentToExtremeRecord(record, isHigh);
        logger.info("Found extreme record: {}", extremeRecord);
        return Optional.of(extremeRecord);
    }
    logger.info("No record found for country: {}, variable: {}", sourceCountry, variableId);
    // Return empty Optional if no record found
    return Optional.empty();
}

/**
 * Convert a MongoDB document to an ExtremeRecord object
 * 
 * @param document The MongoDB document to convert
 * @param isHigh Whether this record represents a high or low value
 * @return The populated ExtremeRecord object
 */
private ExtremeRecord convertDocumentToExtremeRecord(Document document, boolean isHigh) {
    ExtremeRecord record = new ExtremeRecord();
    
    // Set MongoDB document ID
    record.setId(document.getObjectId("_id").toString());
    
    // Set basic fields from document
    record.setCountryCode(document.getString("source_country"));
    record.setCountryName(document.getString("country_name"));
    record.setVariableId(document.getString("metric"));
    record.setVariableName(document.getString("metric_name"));
    
    // Handle numeric value
    Object valueObj = document.get("value");
    if (valueObj instanceof Integer) {
        record.setValue(((Integer) valueObj).doubleValue());
    } else if (valueObj instanceof Double) {
        record.setValue((Double) valueObj);
    }
    
    // Set user ID - converting from Integer to String
    Integer userIdInt = document.getInteger("userId");
    record.setUserId(userIdInt != null ? userIdInt.toString() : null);
    
    // Set dates
    Date recordDate = document.getDate("record_date");
    if (recordDate != null) {
        record.setRecordedAt(LocalDateTime.ofInstant(recordDate.toInstant(), 
                                                  java.time.ZoneId.systemDefault()));
    } else {
        // Fallback to last_updated if record_date is not available
        Date lastUpdated = document.getDate("last_updated");
        if (lastUpdated != null) {
            record.setRecordedAt(LocalDateTime.ofInstant(lastUpdated.toInstant(), 
                                                      java.time.ZoneId.systemDefault()));
        }
    }
    
    // Set whether this is a high or low record
    record.setIsHigh(isHigh);
    
    // Handle location if available
    Document locationDoc = document.get("location", Document.class);
    if (locationDoc != null) {
        List<Double> coordinates = locationDoc.getList("coordinates", Double.class);
        if (coordinates != null && coordinates.size() >= 2) {
            record.setLongitude(coordinates.get(0));
            record.setLatitude(coordinates.get(1));
        }
    }
    
    // Handle previous records if available
    List<Document> previousRecordDocs = document.getList("previous_records", Document.class);
    if (previousRecordDocs != null && !previousRecordDocs.isEmpty()) {
        List<PastExtremeRecord> previousRecords = new ArrayList<>();
        
        for (Document prevDoc : previousRecordDocs) {
            // Extract value
            Object prevValueObj = prevDoc.get("value");
            double prevValue = 0.0;
            if (prevValueObj instanceof Integer) {
                prevValue = ((Integer) prevValueObj).doubleValue();
            } else if (prevValueObj instanceof Double) {
                prevValue = (Double) prevValueObj;
            }
            
            // Extract userId
            Integer prevUserIdInt = prevDoc.getInteger("userId");
            String prevUserId = prevUserIdInt != null ? prevUserIdInt.toString() : null;
            
            // Extract date
            Date prevRecordDate = prevDoc.getDate("record_date");
            LocalDateTime prevRecordedAt = null;
            if (prevRecordDate != null) {
                prevRecordedAt = LocalDateTime.ofInstant(prevRecordDate.toInstant(), 
                                                      java.time.ZoneId.systemDefault());
            }
            
            // Create and add past record
            PastExtremeRecord pastRecord = new PastExtremeRecord(prevValue, prevUserId, prevRecordedAt);
            previousRecords.add(pastRecord);
        }
        
        record.setPreviousRecords(previousRecords);
    }
    
    return record;
}

    @Override
    public boolean updateIfMoreExtreme(ExtremeRecord record, boolean isHigh) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateIfMoreExtreme'");
    }

    @Override
    public List<ExtremeRecord> getAllExtremes(String countryName, boolean isHigh) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllExtremes'");
    }

    @Override
    public List<String> getCountriesWithRecordsFor(String variableId, boolean isHigh) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCountriesWithRecordsFor'");
    }

    public MongoDatabase testGetMongoDatabase() {
        return getMongoDatabase();
    }
}