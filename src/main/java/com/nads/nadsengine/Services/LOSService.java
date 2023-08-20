package com.nads.nadsengine.Services;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class LOSService {

    public JSONObject toLOS(String refId, String status) {
        NewHttpRequest httpRequest = new NewHttpRequest();
        Map jsonMap = new HashMap<>();
        jsonMap.put("reffId", refId);
        jsonMap.put("status", status);
        JSONObject jsonObject = new JSONObject(jsonMap);
        // System.out.println(jsonObject.toString());
        // System.out.println(jsonMap.toString());
        String hasil = "";
        String coba = """
                {
                 "reffId":"MOB_1675989239671",
                 "status":"Clean"
                 }
                """;

        try {
            hasil = httpRequest.postRequestBasicAuth("http://10.15.43.28:8300/api/nads", jsonObject.toString(), null,
                    null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new JSONObject(hasil);
    }

}

// 10.15.43.28:8300/api/nads

// body:
// {
// "reffId":"MOB_1675989239671",
// "status":"Clean"
// }