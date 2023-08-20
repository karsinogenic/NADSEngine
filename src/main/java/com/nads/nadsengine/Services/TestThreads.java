package com.nads.nadsengine.Services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.nads.nadsengine.Models.InputApplicant;
import com.nads.nadsengine.Models.NewExtractedResult;
import com.nads.nadsengine.Models.NewRule;
import com.nads.nadsengine.Models.SetupRule;
import com.nads.nadsengine.Repositories.BlacklistSetupRepository;
import com.nads.nadsengine.Repositories.CleanCustDataRepository;
import com.nads.nadsengine.Repositories.RulesRepository;
import com.nads.nadsengine.Repositories.SetupRuleRepository;
import com.nads.nadsengine.Repositories.VwNadsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import me.xdrop.fuzzywuzzy.FuzzySearch;

public class TestThreads implements Runnable {

    private RulesRepository rulesRepository;
    private SetupRuleRepository setupRuleRepository;

    private MongoFetch mongoFetch;

    // // @PersistenceContext
    // // private EntityManager em;

    private MongoTemplate mongoTemplate;
    private MongoOperations mongoOperations;

    private FuzzyService fuzzyService;

    private String kode;
    private String errorkode;
    private Map maperror = new HashMap<>();
    private InputApplicant inputApplicant;
    private int num;
    private int nDays;
    private boolean flag_skip;

    private List<BasicDBObject> finalDatas = new ArrayList<>();
    private List<BasicDBObject> listDatas = new ArrayList<>();
    private List<String> errorList = new ArrayList<>();
    private Map specialListResult = new HashMap();

    public TestThreads(FuzzyService fuzzyService, RulesRepository rulesRepository,
            SetupRuleRepository setupRuleRepository, String kode,
            InputApplicant inputApplicant, MongoTemplate mongoTemplate,
            MongoOperations mongoOperations, int num, MongoFetch mongoFetch, int nDays) {
        this.kode = kode;
        this.inputApplicant = inputApplicant;
        this.mongoTemplate = mongoTemplate;
        this.mongoOperations = mongoOperations;
        this.num = num;
        this.rulesRepository = rulesRepository;
        this.setupRuleRepository = setupRuleRepository;
        this.fuzzyService = fuzzyService;
        this.mongoFetch = mongoFetch;
        this.nDays = nDays;

    }

    public List<BasicDBObject> getResult() {
        return finalDatas;
    }

    public Map specialResult() {
        return specialListResult;
    }

    public Integer getSize() {
        return listDatas.size();
    }

    public String getKode() {
        return kode;
    }

    public String getErrorKode() {
        return errorkode;
    }

    public List<String> errorStr() {
        return errorList;
    }

    public Map listError() {
        return maperror;
    }

    @Override
    public void run() {
        // System.out.println("Thread: " + kode + " started");
        flag_skip = false;
        // errorList.clear();
        long startTime0 = System.currentTimeMillis();
        List<NewRule> list = this.rulesRepository.findByKode(kode);
        Optional<SetupRule> rule_desc = this.setupRuleRepository.findByKode(kode);
        JSONObject jsonObject = new JSONObject(inputApplicant);
        Criteria criteria = new Criteria();
        List<Criteria> orCriteria = new ArrayList<>();
        List<Criteria> andCriteria = new ArrayList<>();

        List<NewRule> fuzzy_list = new ArrayList<>();
        List<NewRule> not_fuzzy_list = new ArrayList<>();
        List<NewRule> soundex_list = new ArrayList<>();
        List<NewRule> not_soundex_list = new ArrayList<>();
        List<NewRule> special_list = new ArrayList<>();

        SoundexService soundexService = new SoundexService();

        // System.out.println(jsonObject.toString());

        for (NewRule newRule : list) {
            // System.out.println(!newRule.getIsOwnValue());
            try {
                Object value_in = jsonObject.get(newRule.getParamInput());
                // System.out.println(value_in.toString());
                // System.out.println(value_in.toString().isBlank() &&
                // !newRule.getIsOwnValue());
                // System.out.println("isi blank= ");
                if ((value_in.toString().isBlank() && newRule.getAndOr().toLowerCase().equals("and"))
                        || !newRule.getIsOwnValue().equals(0)) {
                    // if(kode.equals("F43")){
                    // }
                    // System.out.println("skip_flag");
                    flag_skip = true;
                } else {

                    // System.out.println("not_skip");
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        // skip process if false
        if (!flag_skip) {
            // exclude fuzzy and etc from dedup
            for (NewRule newRule : list) {
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
                } else if (newRule.getIsOwnValue().equals(2)) {
                    // skip
                } else {
                    Boolean is_exist = mongoTemplate.collectionExists(rule_desc.get().getDatabaseName());
                    // System.out.println("boolean exist: " + is_exist);
                    // Criteria criteria1 = new Criteria();
                    // criteria1.and(newRule.getParamDb()).ne(false);
                    // Query query = new Query(criteria1);
                    // query.limit(1);
                    // Boolean col_exist = mongoTemplate.exists(query,
                    // rule_desc.get().getDatabaseName());
                    // Boolean col_exist = true;
                    if (!is_exist) {
                        errorList.add(kode + " Collection not exist");

                    } else {
                        // if (col_exist) {
                        // mongoFetch.buildCriteria(rule_desc.get().getDatabaseName(),
                        // newRule.getParamDb(),
                        // jsonObject.get(newRule.getParamInput()), criteria,
                        // newRule.getOperator(), newRule.getAndOr());
                        Object value_in = new Object();
                        try {
                            // System.out.println("key :" + newRule.getParamInput());
                            value_in = jsonObject.get(newRule.getParamInput());

                        } catch (Exception e) {
                            if (newRule.getIsOwnValue().equals(1)) {
                                value_in = newRule.getValue();
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
                                if (errorList.size() > 0) {
                                    maperror.put(kode, errorList);
                                }
                            }
                        }

                        Boolean is_partial = false;
                        if (newRule.getPartialEnd() != null && newRule.getPartialStart() != null) {
                            is_partial = true;
                        }

                        Criteria tempCriteria = mongoFetch.buildNewCriteria(rule_desc.get().getDatabaseName(),
                                newRule.getParamDb(),
                                value_in, newRule.getOperator(), is_partial);

                        // System.out.println(tempCriteria.);
                        if (newRule.getAndOr().toUpperCase().equals("AND")) {
                            andCriteria.add(tempCriteria);
                            // if (kode.equals("F31")) {
                            // System.out.println("and criteria");
                            // System.out.println(andCriteria.size());
                            // }
                        } else {
                            orCriteria.add(tempCriteria);
                        }

                        andCriteria.add(Criteria.where(newRule.getParamDb()).exists(true).ne(""));
                        // andCriteria.add(Criteria.where(newRule.getParamDb()).ne(""));
                    }
                    // andCriteria.add(Criteria.where("is_active").is(true));

                }
            }
            // System.out.println("size fuzzy :" + fuzzy_list.size());
            Criteria temp_criteria1 = new Criteria();
            andCriteria.add(temp_criteria1.and("Application_Number").ne(jsonObject.get("id")));

            Criteria temp_or_crit = new Criteria();
            if (!orCriteria.isEmpty()) {
                temp_or_crit.orOperator(orCriteria);
                // System.out.println("or size: " + orCriteria.size());
                // criteria.andOperator(temp_or_crit);
            }
            if (!andCriteria.isEmpty()) {
                // if (kode.equals("F31")) {

                // System.out.println("and size: " + andCriteria.size());
                // }
                if (!orCriteria.isEmpty()) {
                    // System.out.println("or size: " + orCriteria.size());
                    andCriteria.add(temp_or_crit);
                    // criteria.andOperator(temp_or_crit);
                } else {
                    criteria.orOperator(temp_or_crit);
                }
                criteria.andOperator(andCriteria);

            }

            // Query query1 = new Query(criteria);
            // System.out.println(query1);

            if (nDays != 0) {
                LocalDate temp_date = LocalDate.now();
                // temp_date.getMonthValue()
                // LocalDate date_new = LocalDate.of(temp_date.getYear(),
                // temp_date.getMonthValue(), 1);
                temp_date = temp_date.minusDays(nDays);
                criteria.and("Application_Date").gt(temp_date.toString());
            }

            List<Query> query_list = new ArrayList<>();
            // Query query = new Query();
            if (fuzzy_list.size() > 0) {
                for (NewRule newRule : fuzzy_list) {

                    Query query = new Query(criteria);

                    Field field_fuzzy = query.fields();
                    field_fuzzy.include("Application_Number");
                    field_fuzzy.include("_id");
                    for (NewRule newRule1 : fuzzy_list) {
                        field_fuzzy.include(newRule1.getParamDb());
                    }
                    // query.limit(5000);
                    // query.cursorBatchSize(100);

                    // query.with(Sort.by(Sort.Direction.ASC, newRule.getParamDb()));

                    query_list.add(query);
                }
            } else if (not_fuzzy_list.size() > 0) {
                for (NewRule newRule : not_fuzzy_list) {

                    Query query = new Query(criteria);
                    // System.out.println(query.toString());
                    Field field_fuzzy = query.fields();
                    field_fuzzy.include("Application_Number");
                    field_fuzzy.include("_id");
                    for (NewRule newRule1 : not_fuzzy_list) {
                        field_fuzzy.include(newRule1.getParamDb());
                    }
                    // query.limit(5000);
                    // query.cursorBatchSize(100);

                    // query.with(Sort.by(Sort.Direction.ASC, newRule.getParamDb()));

                    query_list.add(query);
                }
            } else if (soundex_list.size() > 0) {
                for (NewRule newRule : soundex_list) {

                    Query query = new Query(criteria);
                    // System.out.println(query.toString());
                    Field field_soundex = query.fields();
                    field_soundex.include("Application_Number");
                    field_soundex.include("_id");
                    for (NewRule newRule1 : soundex_list) {
                        field_soundex.include(newRule1.getParamDb());
                    }
                    // query.limit(5000);
                    // query.cursorBatchSize(100);
                    // System.out.println(query.toString());

                    // query.with(Sort.by(Sort.Direction.ASC, newRule.getParamDb()));

                    query_list.add(query);
                }
            } else if (not_soundex_list.size() > 0) {
                for (NewRule newRule : not_soundex_list) {

                    Query query = new Query(criteria);
                    // System.out.println(query.toString());
                    Field field_soundex = query.fields();
                    field_soundex.include("Application_Number");
                    field_soundex.include("_id");
                    for (NewRule newRule1 : not_soundex_list) {
                        field_soundex.include(newRule1.getParamDb());
                    }
                    // query.limit(5000);
                    // query.cursorBatchSize(100);
                    // System.out.println(query.toString());

                    // query.with(Sort.by(Sort.Direction.ASC, newRule.getParamDb()));

                    query_list.add(query);
                }
            } else {
                Query query = new Query(criteria);
                query_list.add(query);

                // System.out.println(query.toString());
            }

            fuzzyService = new FuzzyService();
            List<NewExtractedResult> reduced = new ArrayList<>();

            List<List<String>> listnama = new ArrayList<>();

            try {

                listDatas = this.mongoTemplate.find(
                        query_list.get(0).with(Sort.by(Sort.Direction.DESC, "App_DateTime")), BasicDBObject.class,
                        rule_desc.get().getDatabaseName());
                if (kode.equals("PF59")) {

                    System.out.println(kode + " query : " + query_list.get(0).toString());
                }

                long endTime = System.currentTimeMillis();

            } catch (Exception e) {
                // System.out.println("Exception thread: " + kode);
                System.out.println(e);
                errorList.add("Bad Query for Kode: " + kode);
                // System.out.println("error list: " + errorList == null);
                if (!errorList.isEmpty()) {
                    maperror.put(kode, errorList);
                }
            }

            // reduced dedup data when fuzzy and etc is available

            List<BasicDBObject> listcleantemp = new ArrayList<>();
            listcleantemp = listDatas;
            // System.out.println("before clean ");

            // long startTime1 = System.currentTimeMillis();
            if (fuzzy_list.size() > 0) {
                for (int i = 0; i < query_list.size(); i++) {
                    // System.out.println(query_list.get(i).toString());
                    String column_name = fuzzy_list.get(i).getParamDb();
                    for (int j = 0; j < listcleantemp.size(); j++) {
                        if (listcleantemp.get(j).get(column_name) == null) {
                            // System.out.println("null");
                            listcleantemp.get(j).put(column_name, "-");
                        }

                    }
                    // System.out.println("size: " + listcleantemp.size());
                }
                // long endTime1 = System.currentTimeMillis();
                // System.out.println("Total execution time 1: " + (endTime1 - startTime1) +
                // "ms");

                // long startTime2 = System.currentTimeMillis();
                for (int i = 0; i < query_list.size(); i++) {
                    String column_name = fuzzy_list.get(i).getParamDb();
                    List<String> listnama_temp = new ArrayList<>();
                    for (BasicDBObject basicDBObject : listcleantemp) {
                        // System.out.println("coba :" + basicDBObject.getString(column_name));
                        listnama_temp.add(basicDBObject.getString(column_name));
                    }
                    listnama.add(listnama_temp);
                }
                // long endTime2 = System.currentTimeMillis();
                // System.out.println("Total execution time 2: " + (endTime2 - startTime2) +
                // "ms");

                // long startTime3 = System.currentTimeMillis();
                for (int i = 0; i < query_list.size(); i++) {
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
                for (int i = 0; i < query_list.size(); i++) {
                    // System.out.println(query_list.get(i).toString());
                    String column_name = not_fuzzy_list.get(i).getParamDb();
                    // System.out.println("nama kolom: " + column_name);
                    // System.out.println(listcleantemp.get(0).toString());
                    for (int j = 0; j < listcleantemp.size(); j++) {
                        // System.out.println(listcleantemp.get(j).get(column_name).toString());
                        if (listcleantemp.get(j).get(column_name) == null) {
                            // System.out.println("null");
                            listcleantemp.get(j).put(column_name, "-");
                        }

                    }
                    // System.out.println("size: " + listcleantemp.size());
                }
                // long endTime1 = System.currentTimeMillis();
                // System.out.println("Total execution time 1: " + (endTime1 - startTime1) +
                // "ms");

                // long startTime2 = System.currentTimeMillis();
                for (int i = 0; i < query_list.size(); i++) {
                    String column_name = not_fuzzy_list.get(i).getParamDb();
                    List<String> listnama_temp = new ArrayList<>();
                    for (BasicDBObject basicDBObject : listcleantemp) {
                        // System.out.println("coba :" + basicDBObject.getString(column_name));
                        listnama_temp.add(basicDBObject.getString(column_name));
                    }
                    listnama.add(listnama_temp);
                }
                // long endTime2 = System.currentTimeMillis();
                // System.out.println("Total execution time 2: " + (endTime2 - startTime2) +
                // "ms");

                // long startTime3 = System.currentTimeMillis();
                for (int i = 0; i < query_list.size(); i++) {
                    reduced = fuzzyService.fuzzyFinalReverse(listnama.get(i),
                            jsonObject.get(not_fuzzy_list.get(i).getParamInput()).toString(),
                            not_fuzzy_list.get(i).getFuzzyTokenScore(), not_fuzzy_list.get(i).getFuzzyScore());
                    // System.out.println("reduce not: " + reduced.get(i).getString());
                    if (!reduced.isEmpty()) {
                        break;
                    }
                }
            }
            // long endTime3 = System.currentTimeMillis();
            // System.out.println("Total execution time 3: " + (endTime3 - startTime3) +
            // "ms");
            if (soundex_list.size() > 0) {
                // System.out.println("masuk soundex");
                for (int i = 0; i < query_list.size(); i++) {
                    List<NewExtractedResult> temp_reduced = soundexService.list_soundex(listcleantemp,
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
                for (int i = 0; i < query_list.size(); i++) {
                    List<NewExtractedResult> temp_reduced = soundexService.list_notsoundex(listcleantemp,
                            jsonObject.get(not_soundex_list.get(i).getParamInput()).toString(),
                            not_soundex_list.get(i).getParamDb());
                    // System.out.println(temp_reduced.toString());
                    reduced.addAll(temp_reduced);
                    if (temp_reduced.isEmpty() && not_soundex_list.get(i).getAndOr().toUpperCase().equals("AND")) {
                        reduced.clear();

                    }
                }
            }

            // find data from list data using reduced index
            if (fuzzy_list.size() > 0) {
                for (NewExtractedResult string : reduced) {
                    if (!string.getString().equals("-")) {
                        finalDatas.add(listcleantemp.get(string.getIndex()));
                    }
                }

            } else if (not_fuzzy_list.size() > 0) {
                for (NewExtractedResult string : reduced) {
                    if (!string.getString().equals("-")) {
                        finalDatas.add(listcleantemp.get(string.getIndex()));
                    }
                }
            } else if (soundex_list.size() > 0) {
                for (NewExtractedResult string : reduced) {
                    // System.out.println(string.getString());
                    // if (!string.getString().equals("-")) {
                    finalDatas.add(listcleantemp.get(string.getIndex()));
                    // }
                }
            } else if (not_soundex_list.size() > 0) {
                for (NewExtractedResult string : reduced) {
                    finalDatas.add(listcleantemp.get(string.getIndex()));
                }

            } else if (special_list.size() > 0) {
                for (NewRule isiRule : special_list) {
                    SpecialFunctionService sfs = new SpecialFunctionService();
                    Boolean hasil = sfs.specialCaseResult(isiRule, listcleantemp, inputApplicant);
                    if (hasil && listcleantemp.size() > 0) {
                        specialListResult = sfs.specialCaseResultMap(isiRule, listcleantemp, inputApplicant);
                        finalDatas = listcleantemp;
                    }
                }
            } else {

                finalDatas = listcleantemp;
            }

            long endTime0 = System.currentTimeMillis();
            System.out.println("Main Thread " + kode + " execution time: " + (endTime0 - startTime0) + "ms");

        }

    }

}
