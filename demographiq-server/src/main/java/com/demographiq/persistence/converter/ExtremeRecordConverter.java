package com.demographiq.persistence.converter;

import com.demographiq.model.ExtremeRecord;
import com.demographiq.model.PastExtremeRecord;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Utility class for converting MongoDB documents to ExtremeRecord objects
 * This class is responsible solely for the mapping logic.
 */
public class ExtremeRecordConverter {

    /**
     * Convert a MongoDB document to an ExtremeRecord object
     *
     * @param document The MongoDB document to convert
     * @param isHigh Whether this record represents a high or low value
     * @return The populated ExtremeRecord object
     */
    public ExtremeRecord convertDocumentToExtremeRecord(Document document, boolean isHigh) {
        if (document == null) {
            return null;
        }

        ExtremeRecord record = new ExtremeRecord();

        // Set MongoDB document ID
        if (document.getObjectId("_id") != null) {
             record.setId(document.getObjectId("_id").toString());
        }


        // Set basic fields from document
        record.setCountryCode(document.getString("source_country"));
        record.setCountryName(document.getString("country_name"));
        record.setVariableId(document.getString("metric"));
        record.setVariableName(document.getString("metric_name"));
        record.setUserId(document.getInteger("userId"));

        // Handle numeric value using helper method
        record.setValue(extractValue(document));

        // Set dates using helper method
        record.setRecordedAt(extractRecordedAt(document));

        // Set whether this is a high or low record
        record.setIsHigh(isHigh);

        // Handle location using helper method
        extractLocation(document, record);

        // Handle previous records using helper method
        record.setPreviousRecords(extractPreviousRecords(document));

        return record;
    }

    /**
     * Helper method to extract and convert the 'value' field from the document.
     * Handles both Integer and Double types.
     *
     * @param document The MongoDB document.
     * @return The value as a double, defaults to 0.0 if not found or invalid type.
     */
    private double extractValue(Document document) {
        Object valueObj = document.get("value");
        if (valueObj instanceof Integer) {
            return ((Integer) valueObj).doubleValue();
        } else if (valueObj instanceof Double) {
            return (Double) valueObj;
        }
        // Return a default value or handle as an error if value is mandatory
        return 0.0;
    }

    /**
     * Helper method to extract and determine the 'recordedAt' date from the document.
     * Prioritizes 'record_date', falls back to 'last_updated'.
     *
     * @param document The MongoDB document.
     * @return The recorded date as LocalDateTime, or null if neither date field is found.
     */
    private LocalDateTime extractRecordedAt(Document document) {
        Date recordDate = document.getDate("record_date");
        if (recordDate != null) {
            return LocalDateTime.ofInstant(recordDate.toInstant(), ZoneId.systemDefault());
        } else {
            Date lastUpdated = document.getDate("last_updated");
            if (lastUpdated != null) {
                return LocalDateTime.ofInstant(lastUpdated.toInstant(), ZoneId.systemDefault());
            }
        }
        return null; // Or handle as needed if a date is mandatory
    }

    /**
     * Helper method to extract location coordinates and set them on the ExtremeRecord.
     *
     * @param document The MongoDB document.
     * @param record   The ExtremeRecord object to populate.
     */
    private void extractLocation(Document document, ExtremeRecord record) {
        Document locationDoc = document.get("location", Document.class);
        if (locationDoc != null) {
            List<Double> coordinates = locationDoc.getList("coordinates", Double.class);
            if (coordinates != null && coordinates.size() >= 2) {
                record.setLongitude(coordinates.get(0));
                record.setLatitude(coordinates.get(1));
            }
        }
    }

    /**
     * Helper method to extract and convert the 'previous_records' list.
     *
     * @param document The MongoDB document.
     * @return A list of PastExtremeRecord objects, or null if the list is not found or empty.
     */
    private List<PastExtremeRecord> extractPreviousRecords(Document document) {
        List<Document> previousRecordDocs = document.getList("previous_records", Document.class);
        if (previousRecordDocs != null && !previousRecordDocs.isEmpty()) {
            List<PastExtremeRecord> previousRecords = new ArrayList<>();

            for (Document prevDoc : previousRecordDocs) {
                // Extract value
                double prevValue = 0.0;
                Object prevValueObj = prevDoc.get("value");
                if (prevValueObj instanceof Integer) {
                    prevValue = ((Integer) prevValueObj).doubleValue();
                } else if (prevValueObj instanceof Double) {
                    prevValue = (Double) prevValueObj;
                }

                // Extract userId
                Integer prevUserId = prevDoc.getInteger("userId");

                // Extract date
                LocalDateTime prevRecordedAt = null;
                Date prevRecordDate = prevDoc.getDate("record_date");
                if (prevRecordDate != null) {
                    prevRecordedAt = LocalDateTime.ofInstant(prevRecordDate.toInstant(), ZoneId.systemDefault());
                }

                // Create and add past record
                PastExtremeRecord pastRecord = new PastExtremeRecord(prevValue, prevUserId, prevRecordedAt);
                previousRecords.add(pastRecord);
            }
            return previousRecords;
        }
        return null;
    }

}
