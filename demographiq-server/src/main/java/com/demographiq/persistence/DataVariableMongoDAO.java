package com.demographiq.persistence;

import java.util.List;
import java.util.Optional;

import com.demographiq.model.ExtremeRecord;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class DataVariableMongoDAO implements DataVariableDAO {

    public static void main(String[] args) {
        String uri = "mongodb+srv://username:password@demographiq.cbhbhvx.mongodb.net/?retryWrites=true&w=majority&appName=Demographiq";
        MongoClient mongoClient = MongoClients.create(uri);

    }

    @Override
    public Optional<ExtremeRecord> getExtremeValue(String countryName, String variableId, boolean isHigh) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getExtremeValue'");
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
}