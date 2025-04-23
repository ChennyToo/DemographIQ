package com.demographiq.persistence;

import com.demographiq.model.ExtremeRecord;
import com.demographiq.persistence.converter.ExtremeRecordConverter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Component
public class DataVariableMongoDAO implements DataVariableDAO {

    private static final Logger logger = LoggerFactory.getLogger(DataVariableMongoDAO.class);

    @Value("${MongoDB_USER}")
    private String mongoDbUsername;

    @Value("${MongoDB_PASSWORD}")
    private String mongoDbPassword;

    private final ExtremeRecordConverter converter = new ExtremeRecordConverter();

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
                Document previousRecord = new Document()
                    .append("value", currentRecord.get("value")) 
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
        String connectionUri = String.format(
            "mongodb+srv://%s:%s@demographiq.cbhbhvx.mongodb.net/?retryWrites=true&w=majority&appName=Demographiq",
            mongoDbUsername, mongoDbPassword
        );
        // In a real application, you'd typically manage the MongoClient lifecycle
        // more robustly, perhaps as a singleton or managed by a framework.
        MongoClient mongoClient = MongoClients.create(connectionUri);
        MongoDatabase database = mongoClient.getDatabase("enrichment_data");
        return database; // Remember to close the MongoClient when the application shuts down
    }


/**
 * Retrieves the extreme value (highest or lowest) for a specific country and metric
 *
 * @param sourceCountry Country identier (e.g., "US", "CN", "RU")
 * @param variableId Metric identifier (e.g., "POPDENS_CY")
 * @param isHigh If true, retrieve from record_highs collection, else from record_lows
 * @return Optional containing the extreme record if found, empty otherwise
 */
@Override
public Optional<ExtremeRecord> getExtremeValue(String sourceCountry, String variableId, boolean isHigh) {
    // Get the MongoDB database connection
    MongoDatabase database = getMongoDatabase();

    // Determine which collection to query based on whether we want record highs or lows
    String collectionName = isHigh ? "record_highs" : "record_lows";
    MongoCollection<Document> collection = database.getCollection(collectionName);

    // Define the match filter to find the specific document by country and variable ID
    Bson matchFilter = Filters.and(
            Filters.eq("source_country", sourceCountry),
            Filters.eq("metric", variableId)
    );

    // --- MANUAL CONSTRUCTION OF $PROJECT STAGE WITH $SLICE ---
    // Define the projection to include only necessary fields and slice the previous_records array
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
                    // Use $slice explicitly with the field path "$previous_records" and the limit -2
                    .append("previous_records", new Document("$slice", Arrays.asList("$previous_records", -2)))
    );
    // --- END MANUAL CONSTRUCTION ---


    // Create the aggregation pipeline
    // The pipeline consists of a $match stage followed by a $project stage
    List<Bson> pipeline = Arrays.asList(
            new Document("$match", matchFilter),
            projectStage // Use the manually constructed projectStage
    );

    // Execute the aggregation pipeline and get the first result
    Document record = collection.aggregate(pipeline).first();

    // Convert document to ExtremeRecord if found using the dedicated converter
    if (record != null) {
        ExtremeRecord extremeRecord = converter.convertDocumentToExtremeRecord(record, isHigh);
        logger.info("Found extreme record for country: {}, variable: {}", sourceCountry, variableId);
        logger.info(extremeRecord.toString());
        return Optional.of(extremeRecord);
    }

    // Log if no record was found
    logger.info("No record found for country: {}, variable: {}", sourceCountry, variableId);

    // Return empty Optional if no record found
    return Optional.empty();
}

    @Override
    public boolean updateIfMoreExtreme(ExtremeRecord record, boolean isHigh) {
        throw new UnsupportedOperationException("Unimplemented method 'updateIfMoreExtreme'");
    }

    @Override
    public List<ExtremeRecord> getAllExtremes(String countryName, boolean isHigh) {
        throw new UnsupportedOperationException("Unimplemented method 'getAllExtremes'");
    }

    @Override
    public List<String> getCountriesWithRecordsFor(String variableId, boolean isHigh) {
        throw new UnsupportedOperationException("Unimplemented method 'getCountriesWithRecordsFor'");
    }

}
