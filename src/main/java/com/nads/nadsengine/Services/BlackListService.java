package com.nads.nadsengine.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.nads.nadsengine.Models.BlacklistSetup;
import com.nads.nadsengine.Repositories.BlacklistSetupRepository;

@Service
public class BlackListService {

    private MongoTemplate mongoTemplate;
    private MongoFetch mongoFetch;

    @Autowired
    public BlackListService(MongoTemplate mongoTemplate, MongoFetch mongoFetch) {
        this.mongoTemplate = mongoTemplate;
        this.mongoFetch = mongoFetch;
    }

    @Autowired
    private BlacklistSetupRepository blacklistSetupRepository;

    public Map BlackListValidation(Map<String, Object> input) {
        Map hasil = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Object> out = mongoFetch.fetchAllDistinct("nadsBlacklist", "NAMA_TABLE");
        // System.out.println("out_size: " + out.size());
        // System.out.println("i);
        List<String> tblDB = this.blacklistSetupRepository.findDistinct();
        List<String> tblMongo = new ArrayList<>();
        List<String> isiInput = this.blacklistSetupRepository.findDistinctInput();

        List<String> keyList = new ArrayList<>();
        List<Object> valueList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            keyList.add(entry.getKey());
            valueList.add(entry.getValue());
        }

        // for (String string : keyList) {
        // if (!isiInput.contains(string)) {
        // hasil.put("rc", 400);
        // hasil.put("rd", "Tidak ada input dengan parameter " + string);
        // return hasil;
        // }
        // }

        for (String string : isiInput) {
            if (!keyList.contains(string)) {
                hasil.put("rc", 444);
                hasil.put("rd", "Tidak ada input dengan parameter " + string);
                return hasil;
            }
        }

        for (Object object : out) {
            // System.out.println("object: " + object);
            Map map = objectMapper.convertValue(object, Map.class);
            try {
                tblMongo.add(map.get("_id").toString());
            } catch (Exception e) {
                hasil.put("rc", 444);
                hasil.put("rd", "Value tidak boleh null/kosong");
                hasil.put("data", map);
                // return hasil;
            }
        }

        for (String string : tblDB) {
            if (!tblMongo.contains(string)) {
                hasil.put("rc", 444);
                hasil.put("rd", "Tabel " + string + " tidak ada di MongoDB");
                return hasil;
            }
        }
        hasil.put("rc", 200);
        hasil.put("rd", "Berhasil");
        hasil.put("data", tblDB);
        return hasil;
    }

    public Map BlackListFunction(Map<String, Object> input,
            String collectionName) {
        // System.out.println("masuk blacklist function");
        Map hasil = new HashMap<>();
        List<String> distincList = this.blacklistSetupRepository.findDistinct();
        for (String string : distincList) {
            List<BlacklistSetup> blacklistSetups = this.blacklistSetupRepository.findByTable_name(string);
            Map listMap = mongoFetch.fetchBlacklistCustom(blacklistSetups, input, collectionName);
            if (listMap.get("data") == null) {
                return listMap;
            }
            // System.out.println(listMap);
            // List<BasicDBObject> convertList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                jsonArray = new JSONArray(listMap.get("data").toString());
            } catch (Exception e) {
                // e.printStackTrace();
                hasil = listMap;
                return hasil;
            }
            // System.out.println(jsonArray);
            // JSONObject temp_map = new JSONObject(convertList.get(0).toString());
            if (jsonArray.length() > 0) {
                hasil.put("rc", 400);
                hasil.put("rd", "Data ditemukan di " + jsonArray.getJSONObject(0).getString("NAMA_TABLE"));
                hasil.put("data", jsonArray.toList());
                return hasil;
            }
        }
        hasil.put("rc", 200);
        hasil.put("rd", "Aman");
        // hasil.put("data", convertList);
        return hasil;
    }

}
