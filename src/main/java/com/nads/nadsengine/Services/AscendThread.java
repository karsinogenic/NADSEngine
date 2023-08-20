package com.nads.nadsengine.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sound.midi.spi.SoundbankReader;

import org.apache.commons.codec.language.Soundex;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.mongodb.core.query.Criteria;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.nads.nadsengine.Models.InputApplicant;
import com.nads.nadsengine.Models.NewExtractedResult;
import com.nads.nadsengine.Models.NewRule;
import com.nads.nadsengine.Models.SetupRule;
import com.nads.nadsengine.Repositories.RulesRepository;
import com.nads.nadsengine.Repositories.SetupRuleRepository;

public class AscendThread implements Runnable {

    private InputApplicant inputApplicant;
    private String kode;
    private Integer num;
    private Integer nDays;
    private RulesRepository rulesRepository;
    private SetupRuleRepository setupRuleRepository;
    private boolean flag_skip;
    private FuzzyService fuzzyService;
    private SoundexService soundexService;

    private List<BasicDBObject> finalDatas = new ArrayList<>();
    private List<BasicDBObject> listDatas = new ArrayList<>();
    private List<String> errorList = new ArrayList<>();
    private Map specialListResult = new HashMap();
    private JSONArray isiAscend = new JSONArray();

    public AscendThread(FuzzyService fuzzyService, SetupRuleRepository setupRuleRepository,
            RulesRepository rulesRepository, String kode,
            InputApplicant inputApplicant, int num, int nDays, SoundexService soundexService) {
        this.kode = kode;
        this.inputApplicant = inputApplicant;
        this.num = num;
        this.nDays = nDays;
        this.setupRuleRepository = setupRuleRepository;
        this.rulesRepository = rulesRepository;
        this.fuzzyService = fuzzyService;
        this.soundexService = soundexService;
    }

    public List<BasicDBObject> getResult() {
        return finalDatas;
    }

    public Integer getSize() {
        return listDatas.size();
    }

    public String getKode() {
        return kode;
    }

    public Map specialResult() {
        return specialListResult;
    }

    public JSONArray isiAscend() {
        return isiAscend;
    }

    // public String getErrorKode() {
    // return errorkode;
    // }

    public List<String> errorStr() {
        return errorList;
    }

    // public Map listError() {
    // return maperror;
    // }

    @Override
    public void run() {
        flag_skip = false;
        long startTime0 = System.currentTimeMillis();

        List<NewRule> rule_list = this.rulesRepository.findByKode(kode);
        Optional<SetupRule> rule_desc = this.setupRuleRepository.findByKode(kode);
        JSONObject jsonObject = new JSONObject(inputApplicant);

        List<TermQueryBuilder> listQuery = new ArrayList<>();
        // List<TermQueryBuilder> orQuery = new ArrayList<>();
        ElasticQueryBuilder eqb = new ElasticQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        List<NewRule> fuzzy_list = new ArrayList<>();
        List<NewRule> not_fuzzy_list = new ArrayList<>();
        List<NewRule> special_list = new ArrayList<>();
        List<NewRule> soundex_list = new ArrayList<>();
        List<NewRule> not_soundex_list = new ArrayList<>();

        for (NewRule newRule : rule_list) {
            if (!newRule.getParamInput().equals("ownvalue")) {
                Object value_in = jsonObject.get(newRule.getParamInput());
                if (value_in.toString().isBlank()) {
                    // System.out.println("skip_flag");
                    flag_skip = true;
                }
            }
        }

        if (!flag_skip) {
            for (NewRule newRule : rule_list) {
                if (newRule.getOperator().toLowerCase().equals("fuzzy")) {
                    fuzzy_list.add(newRule);
                } else if (newRule.getOperator().toLowerCase().equals("not fuzzy")) {
                    not_fuzzy_list.add(newRule);
                } else if (newRule.getOperator().toLowerCase().equals("sound like")) {
                    soundex_list.add(newRule);
                } else if (newRule.getOperator().toLowerCase().equals("not sound like")) {
                    not_soundex_list.add(newRule);
                } else if (newRule.getIsSpecialCase()) {
                    special_list.add(newRule);
                } else {
                    Object value_in = new Object();

                    try {
                        // System.out.println("key :" + newRule.getParamInput());
                        value_in = jsonObject.get(newRule.getParamInput());

                    } catch (Exception e) {
                        if (newRule.getIsOwnValue().equals(1)) {
                            value_in = newRule.getValue();
                            jsonObject.put("ownvalue", value_in);
                        }
                        // TODO: handle exception
                    }
                    if (newRule.getPartialStart() != null && newRule.getPartialEnd() != null) {
                        // System.out.println("masok");
                        String str_val_in = value_in.toString();
                        int start = newRule.getPartialStart() - 1;
                        int end = newRule.getPartialEnd();
                        try {
                            value_in = value_in.toString().substring(start,
                                    end);
                        } catch (Exception e) {
                            errorList.add(kode + " Partial Error for Input '" + str_val_in + "' ");
                        }
                    }

                    Boolean is_partial = false;
                    if (newRule.getPartialEnd() != null && newRule.getPartialStart() != null) {
                        is_partial = true;
                    }

                    QueryBuilder new_qb = eqb.QueryBuilder(newRule, jsonObject);
                    // builder
                    if (newRule.getAndOr().equalsIgnoreCase("or")) {
                        boolQueryBuilder.should(new_qb);
                    } else {
                        if (newRule.getOperator().toLowerCase().contains("not") || newRule.getOperator().equals("<>")) {
                            boolQueryBuilder.mustNot(new_qb);
                        } else {
                            boolQueryBuilder.must(new_qb);
                        }
                    }

                    // if(){
                    // }

                    // Criteria tempCriteria =
                    // mongoFetch.buildNewCriteria(rule_desc.get().getDatabaseName(),
                    // newRule.getParamDb(),
                    // value_in, newRule.getOperator(), is_partial);
                    // // System.out.println(tempCriteria.);
                    // if (newRule.getAndOr().toUpperCase().equals("AND")) {
                    // andCriteria.add(tempCriteria);
                    // } else {
                    // orCriteria.add(tempCriteria);
                    // }

                }
            }

            String new_bool = boolQueryBuilder.toString();
            // new_bool = new_bool.replaceAll("\\n", "");
            // new_bool = new_bool.replaceAll("\\s", "");
            String jsonString1 = ("""
                    {"query":""" + new_bool + "}");
            // System.out.println(jsonString1);
            JSONObject newJsonObject = new JSONObject(new_bool);

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("query", newJsonObject);
            String jsonString = jsonObject1.toString().replaceAll("\\r|\\n", "");
            jsonString = jsonString.replace("\\", "");
            System.out.println(jsonString);
            // text = text.replaceAll("\\n", "");
            // text = text.replaceAll("\\s", "");

            // Removing line breaks
            NewHttpRequest newHttpRequest = new NewHttpRequest();
            String coba = "";
            try {
                coba = newHttpRequest.postRequestBasicAuth("http://10.14.21.31:9200/dedup-asc*/_search", jsonString,
                        "elastic", "elkbankmega2022@");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            JSONObject newJson = new JSONObject(coba);
            // System.out.println(newJson);
            JSONObject jsonHits = new JSONObject(newJson.getJSONObject("hits").toString());
            JSONArray jsonItem = new JSONArray(jsonHits.getJSONArray("hits").toString());
            // System.out.println("\n" + jsonItem.toString());
            ObjectMapper om = new ObjectMapper();
            List<BasicDBObject> final_item = new ArrayList<>();
            isiAscend = jsonItem;
            // System.out.println("Size item: " + jsonItem.length());
            for (int i = 0; i < jsonItem.length(); i++) {
                JSONObject temp_object = new JSONObject(jsonItem.getJSONObject(i).getJSONObject("_source").toString());
                BasicDBObject basicDBObject = new BasicDBObject(temp_object.toMap());
                final_item.add(basicDBObject);
            }
            // System.out.println("Size item final: " + final_item.size());

            List<NewExtractedResult> reduced = new ArrayList<>();
            List<List<String>> listnama = new ArrayList<>();
            // System.out.println("size: " + fuzzy_list.size());

            if (fuzzy_list.size() > 0) {
                for (int i = 0; i < fuzzy_list.size(); i++) {
                    // System.out.println(fuzzy_list.get(i).getParamDb());
                    String column_name = fuzzy_list.get(i).getParamDb();
                    for (int j = 0; j < final_item.size(); j++) {
                        if (final_item.get(j).get(column_name) == null) {
                            // System.out.println("null");
                            final_item.get(j).put(column_name, "-");
                        }

                    }
                    // System.out.println("size: " + final_item.size());
                }
                // long endTime1 = System.currentTimeMillis();
                // System.out.println("Total execution time 1: " + (endTime1 - startTime1) +
                // "ms");

                // long startTime2 = System.currentTimeMillis();
                for (int i = 0; i < fuzzy_list.size(); i++) {
                    String column_name = fuzzy_list.get(i).getParamDb();
                    List<String> listnama_temp = new ArrayList<>();
                    for (BasicDBObject basicDBObject : final_item) {
                        // System.out.println("coba :" + basicDBObject.getString(column_name));
                        listnama_temp.add(basicDBObject.getString(column_name));
                    }
                    listnama.add(listnama_temp);
                }
                // long endTime2 = System.currentTimeMillis();
                // System.out.println("Total execution time 2: " + (endTime2 - startTime2) +
                // "ms");

                // long startTime3 = System.currentTimeMillis();
                for (int i = 0; i < fuzzy_list.size(); i++) {
                    List<NewExtractedResult> temp_reduced = fuzzyService.fuzzyFinal(listnama.get(i),
                            jsonObject.get(fuzzy_list.get(i).getParamInput()).toString(),
                            fuzzy_list.get(i).getFuzzyTokenScore(), fuzzy_list.get(i).getFuzzyScore());
                    // System.out.println(temp_reduced.toString());
                    reduced.addAll(temp_reduced);
                    if (temp_reduced.isEmpty() && fuzzy_list.get(i).getAndOr().toUpperCase().equals("AND")) {
                        reduced.clear();

                    }
                }
            }
            if (not_fuzzy_list.size() > 0) {
                for (int i = 0; i < not_fuzzy_list.size(); i++) {
                    // System.out.println(not_fuzzy_list.get(i).toString());
                    String column_name = not_fuzzy_list.get(i).getParamDb();
                    for (int j = 0; j < final_item.size(); j++) {
                        if (final_item.get(j).get(column_name) == null) {
                            // System.out.println("null");
                            final_item.get(j).put(column_name, "-");
                        }

                    }
                    // System.out.println("size: " + final_item.size());
                }
                // long endTime1 = System.currentTimeMillis();
                // System.out.println("Total execution time 1: " + (endTime1 - startTime1) +
                // "ms");

                // long startTime2 = System.currentTimeMillis();
                for (int i = 0; i < not_fuzzy_list.size(); i++) {
                    String column_name = not_fuzzy_list.get(i).getParamDb();
                    List<String> listnama_temp = new ArrayList<>();
                    for (BasicDBObject basicDBObject : final_item) {
                        // System.out.println("coba :" + basicDBObject.getString(column_name));
                        listnama_temp.add(basicDBObject.getString(column_name));
                    }
                    listnama.add(listnama_temp);
                }
                // long endTime2 = System.currentTimeMillis();
                // System.out.println("Total execution time 2: " + (endTime2 - startTime2) +
                // "ms");

                // long startTime3 = System.currentTimeMillis();
                for (int i = 0; i < not_fuzzy_list.size(); i++) {
                    List<NewExtractedResult> temp_reduced = fuzzyService.fuzzyFinal(listnama.get(i),
                            jsonObject.get(not_fuzzy_list.get(i).getParamInput()).toString(),
                            not_fuzzy_list.get(i).getFuzzyTokenScore(), not_fuzzy_list.get(i).getFuzzyScore());
                    // System.out.println(temp_reduced.toString());
                    reduced.addAll(temp_reduced);
                    if (temp_reduced.isEmpty() && not_fuzzy_list.get(i).getAndOr().toUpperCase().equals("AND")) {
                        reduced.clear();

                    }
                }
            }

            if (soundex_list.size() > 0) {
                // System.out.println("masuk soundex");
                for (int i = 0; i < soundex_list.size(); i++) {
                    List<NewExtractedResult> temp_reduced = soundexService.list_soundex(final_item,
                            jsonObject.get(soundex_list.get(i).getParamInput()).toString(),
                            soundex_list.get(i).getParamDb());
                    // System.out.println(temp_reduced.toString());
                    reduced.addAll(temp_reduced);
                    if (temp_reduced.isEmpty() && soundex_list.get(i).getAndOr().toUpperCase().equals("AND")) {
                        reduced.clear();

                    }
                }
            }
            if (not_soundex_list.size() > 0) {
                // System.out.println("masuk soundex");
                for (int i = 0; i < not_soundex_list.size(); i++) {
                    List<NewExtractedResult> temp_reduced = soundexService.list_notsoundex(final_item,
                            jsonObject.get(not_soundex_list.get(i).getParamInput()).toString(),
                            not_soundex_list.get(i).getParamDb());
                    // System.out.println(temp_reduced.toString());
                    reduced.addAll(temp_reduced);
                    if (temp_reduced.isEmpty() && not_soundex_list.get(i).getAndOr().toUpperCase().equals("AND")) {
                        reduced.clear();

                    }
                }
            }

            if (fuzzy_list.size() > 0) {
                for (NewExtractedResult string : reduced) {
                    // System.out.println("reduced: "+string.getString());
                    if (!string.getString().equals("-")) {
                        finalDatas.add(final_item.get(string.getIndex()));
                    }
                }

            } else if (not_fuzzy_list.size() > 0) {
                for (NewExtractedResult string : reduced) {
                    // System.out.println(string.getString());
                    if (!string.getString().equals("-")) {
                        finalDatas.add(final_item.get(string.getIndex()));
                    }
                }
            } else if (soundex_list.size() > 0) {
                for (NewExtractedResult string : reduced) {
                    // System.out.println(string.getString());
                    // if (!string.getString().equals("-")) {
                    finalDatas.add(final_item.get(string.getIndex()));
                    // }
                }
            } else if (not_soundex_list.size() > 0) {
                for (NewExtractedResult string : reduced) {
                    // System.out.println(string.getString());
                    // if (!string.getString().equals("-")) {
                    finalDatas.add(final_item.get(string.getIndex()));
                    // }
                }

            } else if (special_list.size() > 0) {
                for (NewRule isiRule : special_list) {
                    SpecialFunctionService sfs = new SpecialFunctionService();
                    Boolean hasil = sfs.specialCaseResult(isiRule, final_item, inputApplicant);
                    if (hasil) {
                        specialListResult = sfs.specialCaseResultMap(isiRule, final_item, inputApplicant);
                    }

                }
                finalDatas = final_item;

            } else {
                // System.out.println("masuk");
                finalDatas = final_item;
            }
            // System.out.println("final item" + kode + " : " + final_item.size());
            // System.out.println("final data" + kode + " : " + finalDatas.size());
            // System.out.println("final data2: " + final_item.size());

            // finalDatas = reduced;

            // for (Map map : final_item) {
            // System.out.println(map.toString());
            // }
            // finalDatas = om.readValue(newJson,List.class);
            // return newJson.toMap();
            // System.out.println("\n" + jsonHits.toString());
            long endTime0 = System.currentTimeMillis();
            System.out.println("Main Thread " + kode + " execution time: " + (endTime0 - startTime0) + "ms");

        }

    }

}
