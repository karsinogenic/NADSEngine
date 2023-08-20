package com.nads.nadsengine.Services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nads.nadsengine.Models.NewExtractedResult;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.algorithms.WeightedRatio;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

public class FuzzyThreadService implements Runnable {
    private FuzzySearch fuzzySearch;

    private NewExtractedResult hasil_baru;

    private String value;

    private String matcher1;

    private Integer cutoff1;
    private Integer index;

    public FuzzyThreadService(String value, String matcher1, int cutoff1, int index, FuzzySearch fuzzySearch) {
        this.cutoff1 = cutoff1;
        this.matcher1 = matcher1;
        this.value = value;
        this.index = index;
        this.fuzzySearch = fuzzySearch;
    }

    @Override
    public void run() {
        int rasio = fuzzySearch.ratio(matcher1, value);
        // System.out.println(rasio);
        if (rasio >= cutoff1) {
            hasil_baru = new NewExtractedResult(value, cutoff1, cutoff1, index);
        }

    }

    public NewExtractedResult hasilFuzzy() {
        return hasil_baru;
    }
}
