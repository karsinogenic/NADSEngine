package com.nads.nadsengine.Services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.nads.nadsengine.Models.CleanCustData;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class FuzzyThread implements Runnable {

    private List<CleanCustData> data;
    private Map fuzzyItem;
    private Map hasil;
    private Integer fuzzy_ratio;
    private volatile boolean running = true;

    public FuzzyThread(List<CleanCustData> data, Map fuzzyItem, Integer fuzzy_ratio) {
        this.data = data;
        this.fuzzyItem = fuzzyItem;
        this.fuzzy_ratio = fuzzy_ratio;
    }

    public void run() {
        while (running) {
            for (CleanCustData cleanCustData : data) {
                JSONObject cleanData = new JSONObject(cleanCustData);
                FuzzySearch fuzzySearch = new FuzzySearch();
                int rasio = fuzzySearch.ratio(fuzzyItem.get("value").toString(),
                        cleanData.getString(fuzzyItem.get("dbparam").toString()));
                // System.out.println(fuzzyItem.get("value").toString() + " : "
                // + cleanData.getString(fuzzyItem.get("dbparam").toString()));
                // System.out.println("rasio : " + rasio);
                if (rasio > 90) {
                    hasil.put("data", cleanData);
                    hasil.put("rasio", rasio);
                    stop();
                    break;
                }
            }
            // hasil.clear();
        }
    }

    public void stop() {
        running = false;
    }

    public Map result() {
        return hasil;
    }

    public Map searchFuzzy(List<CleanCustData> data, Map fuzzyItem) {
        Map hasil = new HashMap<>();
        for (CleanCustData cleanCustData : data) {
            JSONObject cleanData = new JSONObject(cleanCustData);
            FuzzySearch fuzzySearch = new FuzzySearch();
            int rasio = fuzzySearch.ratio(fuzzyItem.get("value").toString(),
                    cleanData.getString(fuzzyItem.get("dbparam").toString()));
            System.out.println(fuzzyItem.get("value").toString() + " : "
                    + cleanData.getString(fuzzyItem.get("dbparam").toString()));
            System.out.println("rasio : " + rasio);
            hasil.put("data", cleanData);
            hasil.put("rasio", rasio);
            if (rasio > 90) {
                return hasil;
            }
        }
        hasil.clear();
        return hasil;
    }

}
