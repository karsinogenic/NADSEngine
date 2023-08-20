package com.nads.nadsengine.Services;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.nads.nadsengine.Models.BlacklistSetup;

@Service
public class MongoFetch {

    private MongoTemplate mongoTemplate;
    private MongoCollection mongoCollection;

    @Autowired
    public MongoFetch(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<DBObject> fetchLargeData(String collection) {
        Criteria criteria = new Criteria(); // Add your query criteria here if needed
        List<DBObject> result1 = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Query query = new Query(criteria);
            query.limit(10000).skip(i); // Limit the number of records to fetch in one go (adjust as needed)
            Field field_soundex = query.fields();
            field_soundex.include("Application_Number");
            field_soundex.include("Full_Company_Address");
            List<DBObject> result = mongoTemplate.find(query, DBObject.class, collection);
            result1.addAll(result);
        }

        return result1;
    }

    public List<DBObject> fetchAll(String collectionName) {
        ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.ASC, "NAMA"));
        // query.addCriteria(null)
        query.maxTime(Duration.ofMillis(5000));
        Future<List<DBObject>> future_list = executor.submit(() -> {
            List<DBObject> list = mongoTemplate.find(query, DBObject.class, collectionName);
            return list;
        });

        try {
            return future_list.get(10000000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // Handle the interruption or timeout as needed
            System.out.println("takes too long");
            future_list.cancel(true); // Cancel the query execution
            // Log or handle the exception
        } finally {
            executor.shutdownNow(); // Shutdown the executor service
        }

        return null;

    }

    public List<Object> fetchAllDistinct(String collectionName, String fieldName) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group(fieldName),
                Aggregation.project(fieldName).andExclude(fieldName));

        AggregationOptions options = AggregationOptions.builder().maxTime(Duration.ofMillis(5000)).build();

        AggregationResults<Object> results = mongoTemplate.aggregate(
                aggregation.withOptions(options), collectionName, Object.class);

        return results.getMappedResults();
    }

    public List<BasicDBObject> fetchWithParam(Map<String, Object> input, String collectionName) {
        List<String> keyList = new ArrayList<>();
        List<Object> valueList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            keyList.add(entry.getKey());
            valueList.add(entry.getValue());
        }

        if (keyList.size() != valueList.size()) {
            return null;
        }

        Criteria criteria = new Criteria();
        for (int i = 0; i < keyList.size(); i++) {
            criteria.and(keyList.get(i)).is(valueList.get(i));
        }

        Query query = new Query(criteria);

        // System.out.println(query.toString());

        List<BasicDBObject> list = mongoTemplate.find(query, BasicDBObject.class, collectionName);
        return list;
    }

    public BasicDBObject fecthWithId() {
        int dateInMilliseconds = 1685073098; // Replace with your date in milliseconds
        // Date date = new Date((long) dateInMilliseconds * 1000); // Convert timestamp
        // to milliseconds

        ObjectId objectId1 = new ObjectId(dateInMilliseconds, 0);
        // System.out.println(objectId1.toString());
        // System.out.println(objectId1.toHexString());
        // System.out.println(objectId1.toByteArray());

        Criteria criteria = new Criteria();
        criteria.and("_id").is(objectId1);
        Query query = new Query(criteria);
        BasicDBObject document = mongoTemplate.findOne(query, BasicDBObject.class, "CleanCustData");
        // System.out.println(document.toString());
        // Convert date in milliseconds to ObjectId
        // long dateInMilliseconds = 1686911104000L; // Replace with your date in
        // milliseconds
        // ObjectId objectId2 = new ObjectId(dateInMilliseconds);
        return document;
    }

    public Map fetchBlacklistCustom(List<BlacklistSetup> list, Map<String, Object> input,
            String collectionName) {
        // System.out.println("masuk blacklist custom");
        Map map_hasil = new HashMap<>();
        List<BasicDBObject> list_hasil = new ArrayList<>();
        String input_field_name = "";
        try {
            List<Criteria> and_Criterias = new ArrayList<>();
            // List<Criteria> or_Criterias = new ArrayList<>();
            for (BlacklistSetup blacklistSetup : list) {
                input_field_name = blacklistSetup.getInput_field_name();
                if (blacklistSetup.getAnd_or().toLowerCase().equals("and")) {
                    // System.out.println("masuk ");
                    Criteria criteria = this.buildNewCriteria(collectionName, blacklistSetup.getOutput_field_name(),
                            input.get(input_field_name), blacklistSetup.getOperator(), false);

                    if (input_field_name.toLowerCase().equals("dob")
                            && input.get(input_field_name).toString().contains("/")) {
                        String dob = input.get(blacklistSetup.getInput_field_name()).toString();
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                        if (dob.contains("-")) {
                            Date date_dob = inputFormat.parse(dob);
                            dob = outputFormat.format(date_dob);
                        }
                        criteria = this.buildNewCriteria("nadsBlacklist", blacklistSetup.getOutput_field_name(),
                                dob, blacklistSetup.getOperator(), false);
                    }

                    and_Criterias.add(criteria);

                    // criteria.and(blacklistSetup.getOutput_field_name())
                    // .is(input.get(blacklistSetup.getInput_field_name()));
                    and_Criterias.add(Criteria.where(blacklistSetup.getOutput_field_name()).exists(true).ne(""));
                    and_Criterias.add(Criteria.where("NAMA_TABLE").is(blacklistSetup.getTable_name()));
                }
            }

            Criteria final_Criteria = new Criteria();
            final_Criteria.andOperator(and_Criterias);
            Query query = new Query(final_Criteria);
            // System.out.println(query.toString());
            list_hasil = mongoTemplate.find(query, BasicDBObject.class, collectionName);
            // System.out.println("size: " + list_hasil.size());
            // if (list_hasil.size() > 0) {
            // break;
            // }
            map_hasil.put("rc", 200);
            map_hasil.put("rd", "Berhasil");
            map_hasil.put("data", list_hasil);
            return map_hasil;
        } catch (Exception e) {
            map_hasil.put("rc", 400);
            map_hasil.put("rd", "Error pada field " + input_field_name);
            map_hasil.put("data", null);
            return map_hasil;
            // TODO: handle exception
        }
    }

    public ResponseEntity<List<Document>> myEndpoint(String collectionName) {
        try {
            List<Document> result = new ArrayList<>();

            FindIterable<Document> iterable = mongoTemplate.getCollection(collectionName).find();

            // Set a timeout duration in milliseconds
            long timeout = 5000;
            long startTime = System.currentTimeMillis();

            MongoCursor<Document> cursor = iterable.iterator();

            // Iterate over the cursor until timeout or completion
            while (cursor.hasNext()) {
                if (System.currentTimeMillis() - startTime > timeout) {
                    // Timeout exceeded, close the cursor
                    cursor.close();
                    throw new TimeoutException("Data fetch timeout");
                }

                Document document = cursor.next();
                result.add(document);
            }

            return ResponseEntity.ok(result);
        } catch (TimeoutException ex) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }

    }

    public String getFieldType(String collectionName, String fieldName) {
        Criteria criteria = new Criteria();
        criteria.and(fieldName).exists(true).ne(null);
        Query query = new Query(criteria);
        query.limit(1);
        query.maxTime(Duration.ofMillis(20));
        long startTime0 = System.currentTimeMillis();
        BasicDBObject document = new BasicDBObject();
        try {
            document = mongoTemplate.findOne(query, BasicDBObject.class, collectionName);

        } catch (Exception e) {
            return null;
        }
        long endTime0 = System.currentTimeMillis();
        if (endTime0 - startTime0 > 50) {

            // System.out.println("fetching " + fieldName + " took " + (endTime0 -
            // startTime0) + " milliseconds");
        }
        if (document != null) {
            Object fieldValue = document.get(fieldName);
            // System.out.println(fieldValue.toString());
            if (fieldValue != null) {
                return fieldValue.getClass().getSimpleName();
            }
        }
        return null;
    }

    public Criteria buildCriteria(String db, String col_name, Object value, Criteria criteria, String operator,
            String and_or) {
        // long startTime0 = System.currentTimeMillis();

        // if (col_name.equals("DATEOFBIRTH")) {
        // value = String.valueOf(value) + ".0";
        // }
        // long startTime1 = System.currentTimeMillis();

        String type = getFieldType(db, col_name);
        if (value == null && !type.equals("String")) {

        }
        // System.out.println("tipe: " + type);
        // long endTime1 = System.currentTimeMillis();
        // System.out.println("method1 took " + (endTime1 - startTime1) + "
        // milliseconds");
        // System.out.println(type);
        if (operator.equals("=")) {
            if (and_or.toUpperCase().equals("AND")) {
                if (type.equals("String")) {
                    criteria.and(col_name).is(value);
                } else if (type.equals("Double")) {
                    criteria.and(col_name).is(Double.valueOf(value.toString()));
                } else {
                    // TODO: handle exception
                    criteria.and(col_name).is(Integer.valueOf(value.toString()));
                }
            } else {
                if (type.equals("String")) {
                    criteria.orOperator(Criteria.where(col_name).is(value));
                } else if (type.equals("Double")) {
                    criteria.orOperator(Criteria.where(col_name).is(Double.valueOf(value.toString())));
                    // criteria.and(col_name).is(Double.valueOf(value.toString()));
                } else {
                    // TODO: handle exception
                    criteria.orOperator(Criteria.where(col_name).is(Integer.valueOf(value.toString())));
                    // criteria.and(col_name).is(Integer.valueOf(value.toString()));
                }

            }
        } else if (operator.equals("<>")) {
            if (and_or.toUpperCase().equals("AND")) {
                if (type.equals("String")) {
                    criteria.and(col_name).ne(value);
                } else if (type.equals("Double")) {
                    criteria.and(col_name).ne(Double.valueOf(value.toString()));
                } else {
                    // TODO: handle exception
                    criteria.and(col_name).ne(Integer.valueOf(value.toString()));
                }
            } else {
                criteria.orOperator(Criteria.where(col_name).ne(value));
            }
        }

        else if (operator.equals(">")) {
            if (and_or.toUpperCase().equals("AND")) {
                criteria.and(col_name).gt(value);
            } else {
                criteria.orOperator(Criteria.where(col_name).gt(value));
            }
        }

        else if (operator.equals("<")) {
            if (and_or.toUpperCase().equals("AND")) {
                criteria.and(col_name).lt(value);
            } else {
                criteria.orOperator(Criteria.where(col_name).lt(value));
            }
        }

        else if (operator.equals("LIKE")) {
            if (and_or.toUpperCase().equals("AND")) {
                criteria.and(col_name).regex(value.toString());
            } else {
                criteria.orOperator(Criteria.where(col_name).regex(value.toString()));
            }
        }

        else if (operator.equals("IN")) {
            if (value.toString().contains(",")) {
                String[] new_val = value.toString().split(",");
                value = new_val;
            }
            if (and_or.toUpperCase().equals("AND")) {
                criteria.and(col_name).in(value);
            } else {
                criteria.orOperator(Criteria.where(col_name).in(value));
            }
        }

        // if (operator.equals("FUZZY")) {
        // if (and_or.equals("AND")) {
        // criteria.and(col_name).regex(value.toString().split(" ")[0]);
        // } else {
        // criteria.orOperator(Criteria.where(col_name).regex(value.toString().split("
        // ")[0]));
        // }
        // }
        // long endTime0 = System.currentTimeMillis();
        // System.out.println("Method took " + (endTime0 - startTime0) + "
        // milliseconds");
        return criteria;
    }

    public Criteria buildNewCriteria(String db, String col_name, Object value, String operator, Boolean is_partial) {
        Criteria criteria = new Criteria();
        // System.out.println("masuk builder: " + col_name + ":" + value + ":" +
        // operator + ":" + is_partial);
        long starttime = System.currentTimeMillis();
        String type = getFieldType(db, col_name);
        long endtime = System.currentTimeMillis();
        // System.out.println("tipe: " + type);
        // if (endtime - starttime > 100) {
        // System.out.println("field time: " + (endtime - starttime));
        // }
        // System.out.println(type);
        // System.out.println("db: " + db);
        // System.out.println("col: " + col_name);

        // if (!(value.toString().trim().length() > 0)) {
        // // System.out.println("val: " + value);
        // if (operator.equals("=")) {
        // criteria = Criteria.where(col_name).exists(false);
        // }
        // if (operator.equals("<>")) {
        // criteria = Criteria.where(col_name).exists(true);
        // }

        // return criteria;

        // }

        if (operator.equals("=") && type != null) {
            if (type.equals("String")) {
                criteria = Criteria.where(col_name).is(value);
            } else if (type.equals("Double")) {
                criteria = Criteria.where(col_name).is(Double.valueOf(value.toString()));
            } else {
                criteria = Criteria.where(col_name).is(Long.valueOf(value.toString()));
            }
        } else if (operator.equals("<>") && type != null) {
            if (type.equals("String")) {
                criteria = Criteria.where(col_name).ne(value);
            } else if (type.equals("Double")) {
                criteria = Criteria.where(col_name).ne(Double.valueOf(value.toString()));
            } else {
                criteria = Criteria.where(col_name).ne(Long.valueOf(value.toString()));
            }

        }

        else if (operator.equals(">") && type != null) {
            criteria = Criteria.where(col_name).gt(value);

        }

        else if (operator.equals("<") && type != null) {
            criteria = Criteria.where(col_name).lt(value);
        }

        else if (operator.equals("LIKE") && type != null) {
            criteria = Criteria.where(col_name).regex(value.toString());
            if (is_partial) {
                criteria = Criteria.where(col_name).regex("^" + value.toString() + "");
            }
        } else if (operator.equals("NOT LIKE") && type != null) {
            criteria = Criteria.where(col_name).not().regex(value.toString());
            if (is_partial) {
                // System.out.println("parsial");
                criteria = Criteria.where(col_name).not().regex("^" + value.toString() + "");
            }
        } else if (operator.equals("NOT NULL") && type != null) {
            criteria = Criteria.where(col_name).exists(true).ne("");
        }

        else if (operator.equals("IN") && type != null) {
            String[] value_split = value.toString().split(",");
            if (type.equals("String")) {
                List<String> list_in = Arrays.asList(value_split);
                criteria = Criteria.where(col_name).in(list_in);
            }

            else if (type.equals("Double")) {
                List<Double> list_in = new ArrayList<>();
                for (String string : value_split) {
                    list_in.add(Double.valueOf(string));
                }
                criteria = Criteria.where(col_name).in(list_in);

            } else {
                List<Integer> list_in = new ArrayList<>();
                for (String string : value_split) {
                    list_in.add(Integer.valueOf(string));
                }
                criteria = Criteria.where(col_name).in(list_in);

            }

        } else if (operator.equals("NOT IN") && type != null) {
            String[] value_split = value.toString().split(",");
            if (type.equals("String")) {
                List<String> list_in = Arrays.asList(value_split);
                criteria = Criteria.where(col_name).nin(list_in);
            }

            else if (type.equals("Double")) {
                List<Double> list_in = new ArrayList<>();
                for (String string : value_split) {
                    list_in.add(Double.valueOf(string));
                }
                criteria = Criteria.where(col_name).nin(list_in);

            } else {
                List<Integer> list_in = new ArrayList<>();
                for (String string : value_split) {
                    list_in.add(Integer.valueOf(string));
                }
                criteria = Criteria.where(col_name).nin(list_in);

            }

        }

        // if (operator.equals("FUZZY")) {
        // if (and_or.equals("AND")) {
        // criteria = Criteria.and(col_name).regex(value.toString().split(" ")[0]);
        // } else {
        // criteria =
        // Criteria.orOperator(Criteria.where(col_name).regex(value.toString().split("
        // ")[0]));
        // }
        // }
        // long endTime0 = System.currentTimeMillis();
        // System.out.println("Method took " + (endTime0 - startTime0) + "
        // milliseconds");
        // System.out.println(criteria.toString());
        return criteria;
    }

    public int saveToCollection(String collection, Map isi) {
        try {
            // JSONObject jsonObject = new JSONObject(isi);
            // System.out.println(jsonObject);
            mongoTemplate.save(isi, collection);
            return 200;
        } catch (Exception e) {
            return 400;
        }
    }

    public Map searchAndAdd(String collection, Map isi, String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        BasicDBObject data = mongoTemplate.findOne(query, BasicDBObject.class, collection);
        Map hasil = new TreeMap<>();
        hasil.putAll(data.toMap());
        hasil.putAll(isi);
        return hasil;
    }
}
