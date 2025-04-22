package com.demographiq.persistence;

import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class DataVariableMongoDAO {

    public static void main(String[] args) {
        // Replace the placeholder with your MongoDB deployment's connection string
        String uri = "mongodb+srv://username:password@demographiq.cbhbhvx.mongodb.net/?retryWrites=true&w=majority&appName=Demographiq";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
            MongoCollection<Document> collection = database.getCollection("movies");

            // Retrieve all documents in the collection
            try (MongoCursor<Document> cursor = collection.find().iterator()) {
                while (cursor.hasNext()) {
                    System.out.println(cursor.next().toJson());
                }
            }
        }
    }
}