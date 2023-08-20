package com.nads.nadsengine.Services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.nads.nadsengine.Models.NewExtractedResult;

import me.xdrop.fuzzywuzzy.Extractor;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.algorithms.WeightedRatio;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

public class FuzzyService {

    @Autowired
    private FuzzySearch fuzzySearch;

    public List<NewExtractedResult> finalResult(List<ExtractedResult> list, String matcher, int cutoff) {
        List<NewExtractedResult> hasil = new ArrayList<>();
        int break_flag = 0;
        for (ExtractedResult extractedResult : list) {
            if (break_flag > 2) {
                break;
            }
            int rasio = fuzzySearch.ratio(matcher, extractedResult.getString());
            NewExtractedResult newExtractedResult = new NewExtractedResult(extractedResult.getString(),
                    extractedResult.getScore(), rasio, extractedResult.getIndex());
            // fuzzy_reduced.add(newExtractedResult);
            if (rasio >= cutoff) {
                hasil.add(newExtractedResult);
                break_flag++;
            }
        }

        return hasil;
    }

    public List<NewExtractedResult> finalResultNew(List<String> list, String matcher, int cutoff) {
        List<NewExtractedResult> hasil = new ArrayList<>();
        List<FuzzyThreadService> fList = new ArrayList<>();
        List<Thread> tList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            FuzzyThreadService fuzzyThreadService = new FuzzyThreadService(list.get(i), matcher, cutoff, i,
                    fuzzySearch);
            Thread thread_fuzzy = new Thread(fuzzyThreadService);
            fList.add(fuzzyThreadService);
            tList.add(thread_fuzzy);
            thread_fuzzy.start();
        }
        // for (Thread thread : tList) {
        // try {
        // thread.join();
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }
        for (FuzzyThreadService fuzzy : fList) {
            hasil.add(fuzzy.hasilFuzzy());
        }

        return hasil;
    }

    public List<NewExtractedResult> finalResultReverse(List<ExtractedResult> list, String matcher, int cutoff) {
        List<NewExtractedResult> hasil = new ArrayList<>();
        for (ExtractedResult extractedResult : list) {
            int rasio = fuzzySearch.ratio(matcher, extractedResult.getString());
            // System.out.println(rasio);
            NewExtractedResult newExtractedResult = new NewExtractedResult(extractedResult.getString(),
                    extractedResult.getScore(), rasio, extractedResult.getIndex());
            // fuzzy_reduced.add(newExtractedResult);
            if (rasio <= cutoff) {
                hasil.add(newExtractedResult);
            }
        }

        return hasil;
    }

    public List<ExtractedResult> reducedResult(List<String> list, String matcher, int cutoff) {
        // System.out.println(list.get(0));
        List<ExtractedResult> fuzzy_reduced = fuzzySearch.extractAll(matcher, list, cutoff);
        return fuzzy_reduced;
    }

    public List<ExtractedResult> reducedResultReverse(List<String> list, String matcher, int cutoff) {
        // System.out.println(list.get(0));
        List<ExtractedResult> fuzzy_reduced = this.extractAllReverse(matcher, list, cutoff);
        return fuzzy_reduced;
    }

    public static List<ExtractedResult> extractAllReverse(String query, Collection<String> choices, int cutoff) {
        NewExtractor extractor = new NewExtractor(cutoff);
        // System.out.println(cutoff);
        return extractor.extractWithoutOrderReverse(query, choices, new WeightedRatio());
    }

    public static List<ExtractedResult> extractAll(String query, Collection<String> choices, int cutoff) {
        NewExtractor extractor = new NewExtractor(cutoff);
        // System.out.println(cutoff);
        return extractor.extractWithoutOrderReverse(query, choices, new WeightedRatio());
    }

    public List<NewExtractedResult> fuzzyFinalReverse(List<String> list, String matcher, int cutoff, int cutoff_final) {
        List<ExtractedResult> fuzzy_reduced = this.reducedResult(list, matcher,
                cutoff);
        // System.out.println("reduced size: " + fuzzy_reduced.size());
        List<NewExtractedResult> hasil = this.finalResultReverse(fuzzy_reduced, matcher, cutoff_final);
        return hasil;
    }

    public List<NewExtractedResult> fuzzyFinal(List<String> list, String matcher, int cutoff, int cutoff_final) {
        // System.out.println(list.size());
        long startTime = System.currentTimeMillis();
        List<ExtractedResult> fuzzy_reduced = this.reducedResult(list, matcher,
                cutoff);
        long endTime = System.currentTimeMillis();
        // System.out.println("reduce fuzzy took " + (endTime - startTime) +
        // "milliseconds");
        // System.out.println("reduced: " + fuzzy_reduced.size());
        long startTime1 = System.currentTimeMillis();
        List<NewExtractedResult> hasil = this.finalResult(fuzzy_reduced, matcher, cutoff_final);
        long endTime1 = System.currentTimeMillis();
        // System.out.println("final fuzzy took " + (endTime1 - startTime1) +
        // "milliseconds");
        return hasil;
    }

    public List<NewExtractedResult> fuzzyFinalBasic(List<String> list, String matcher, int cutoff, int cutoff_final) {
        // System.out.println(list.size());
        // long startTime = System.currentTimeMillis();
        List<NewExtractedResult> fuzzy_reduced = this.finalResultNew(list, matcher, cutoff);
        // long endTime = System.currentTimeMillis();
        // System.out.println("That took [1] " + (endTime - startTime) + "
        // milliseconds");
        // System.out.println("reduced: " + fuzzy_reduced.size());
        // long startTime1 = System.currentTimeMillis();
        // List<NewExtractedResult> hasil = this.finalResult(fuzzy_reduced, matcher,
        // cutoff_final);
        // long endTime1 = System.currentTimeMillis();
        // System.out.println("That took [2] " + (endTime1 - startTime1) + "
        // milliseconds");
        return fuzzy_reduced;
    }

    public static List<Integer> findCommonIntegers(List<Integer> list1, List<Integer> list2) {
        List<Integer> commonIntegers = new ArrayList<>(list1);
        commonIntegers.retainAll(list2);
        if (list1.isEmpty()) {
            return list2;
        }
        if (list2.isEmpty()) {
            return list1;
        }
        return commonIntegers;
    }

}
