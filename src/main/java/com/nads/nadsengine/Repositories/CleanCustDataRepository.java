package com.nads.nadsengine.Repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nads.nadsengine.Models.CleanCustData;

public interface CleanCustDataRepository extends MongoRepository<CleanCustData, String> {

    @Aggregation(pipeline = { "?0", "?1" })
    List<CleanCustData> findAllLimit(String query, String limit);

    @Aggregation(pipeline = { "?0", "{$limit: ?1}" })
    List<CleanCustData> findAllCustom(String custQuery, int limit);

    @Query("{idNumber:'?0'}")
    List<CleanCustData> findByIdNew(String Id);

}
