package com.nads.nadsengine.Services;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.nads.nadsengine.Models.CleanCustData;

@Service
public class CustDataService {

    private final MongoTemplate mongoTemplate;

    public CustDataService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<CleanCustData> findAllLimit(int limits) {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.limit(limits));
        AggregationResults<CleanCustData> results = mongoTemplate.aggregate(aggregation, "CleanCustData",
                CleanCustData.class);
        return results.getMappedResults();
    }

}
