package com.nads.nadsengine.Services;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class StringBuilderService {

    public JSONObject objectBuilder(String col_name, Object value, String operator) {
        JSONObject hasil = new JSONObject();
        JSONObject gabungan = new JSONObject();
        if (operator.equals("=")) {
            gabungan.put("$eq", value);
        }

        if (operator.equals("<>")) {
            gabungan.put("$ne", value);

        }

        if (operator.equals(">")) {
            gabungan.put("$gt", Integer.valueOf(value.toString()));

        }

        if (operator.equals("<")) {
            gabungan.put("$lt", Integer.valueOf(value.toString()));

        }
        // System.out.println(value.getClass());
        hasil.put(col_name, gabungan);

        return hasil;
    }

    public JSONObject andBuilder(List<JSONObject> list_object) {
        JSONObject hasil = new JSONObject();
        JSONArray listArray = new JSONArray(list_object);
        hasil.put("$and", listArray);
        return hasil;
    }

    public JSONObject orBuilder(List<JSONObject> list_object) {
        JSONObject hasil = new JSONObject();
        JSONArray listArray = new JSONArray(list_object);
        hasil.put("$or", listArray);
        return hasil;
    }

}
