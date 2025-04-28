package com.demographiq.persistence.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demographiq.model.ExtremeRecord;
import com.demographiq.model.PastExtremeRecord;


/**
 * Utility class for converting MongoDB documents to ExtremeRecord objects
 * This class is responsible solely for the mapping logic.
 */
public class ExtremeRecordConverter {

    private static final Logger logger = LoggerFactory.getLogger(ExtremeRecordConverter.class);


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
     *
     * @param document The MongoDB document.
     * @return The value as a double, throws an exception if extration fails.
     */
    private double extractValue(Document document) {
        Object valueObj = document.get("value");
        if (valueObj instanceof Number number) {
            return number.doubleValue();
        }
        String errorMessage = "MongoDB document value extraction failed.";
        logger.error(errorMessage + " Document ID: {}", document.getObjectId("_id"));
        throw new IllegalArgumentException(errorMessage);
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
     * @return A list of PastExtremeRecord objects, may be empty.
     */
    private List<PastExtremeRecord> extractPreviousRecords(Document document) {
        List<Document> previousRecordDocs = document.getList("previous_records", Document.class);

        if (previousRecordDocs == null || previousRecordDocs.isEmpty()) {
            return Collections.emptyList();
        }

        List<PastExtremeRecord> previousRecords = new ArrayList<>();

        for (Document prevDoc : previousRecordDocs) {
            try {
                double prevValue = extractValue(prevDoc);
                Integer prevUserId = prevDoc.getInteger("userId");
                LocalDateTime prevRecordedAt = extractRecordedAtFromDate(prevDoc.getDate("record_date"));
                PastExtremeRecord pastRecord = new PastExtremeRecord(prevValue, prevUserId, prevRecordedAt);
                previousRecords.add(pastRecord);
            } catch (IllegalArgumentException e) {
                logger.error("Skipping invalid previous record due to extraction error: {}. Document ID: {}, Previous Record: {}",
                             e.getMessage(), document.getObjectId("_id"), prevDoc.toJson());
            } catch (Exception e) {
                 logger.error("Unexpected error processing previous record. Document ID: {}, Previous Record: {}, Error: {}",
                             document.getObjectId("_id"), prevDoc.toJson(), e.getMessage(), e);
            }
        }
        return previousRecords;
    }

     /**
     * Helper method to convert a Date object to LocalDateTime.
     *
     * @param date The Date object to convert.
     * @return The corresponding LocalDateTime, or null if the input date is null.
     */
    private LocalDateTime extractRecordedAtFromDate(Date date) {
        if (date != null) {
            return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }
        return null;
    }

}
