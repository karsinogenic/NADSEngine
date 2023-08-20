package com.nads.nadsengine.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.nads.nadsengine.Models.CleanCustData;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;

public class FuzzyThreadNew implements Runnable {

    private Map fuzzyItem;
    private int cutoff;
    private List<CleanCustData> data;
    private CleanCustData cleanCustData;
    private volatile boolean flag = false;

    public FuzzyThreadNew(Map fuzzyItem, List<CleanCustData> data, int cutoff) {
        this.fuzzyItem = fuzzyItem;
        this.data = data;
        this.flag = false;
        this.cutoff = cutoff;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        // System.out.println(data.get(0).toString());
        List<BoundExtractedResult<CleanCustData>> match = FuzzySearch.extractAll(
                fuzzyItem.get("value").toString(),
                data, x -> x.getCustNamePrimary().toString(), cutoff);
        // System.out.println(match.isEmpty());
        if (match.size() > 0) {
            cleanCustData = match.get(0).getReferent();
            // System.out.println(cleanCustData);
            flag = true;
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        // System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
    }

    public List<CleanCustData> findResult() {
        List<CleanCustData> baru = new ArrayList<>();
        if (flag) {
            baru.add(cleanCustData);
        }
        return baru;
    }

}
