package com.nads.nadsengine.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.nads.nadsengine.Models.InputApplicant;
import com.nads.nadsengine.Models.MasterDbInput;
import com.nads.nadsengine.Models.NewRule;
import com.nads.nadsengine.Models.SetupRule;
import com.nads.nadsengine.Repositories.DBInputRepository;
import com.nads.nadsengine.Repositories.RulesRepository;
import com.nads.nadsengine.Repositories.SetupRuleRepository;

// @Component
public class MainSearchService {

    private RulesRepository rulesRepository;
    private SetupRuleRepository setupRuleRepository;
    private MongoFetch mongoFetch;
    private FuzzyService fuzzyService;
    private SoundexService soundexService;

    long startTime = System.currentTimeMillis();

    Map response = new HashMap<>();
    List<String> triggered_code = new ArrayList<>();
    List<List<String>> triggered_id = new ArrayList<>();
    List<Map> triggered_error = new ArrayList<>();
    List<List<String>> triggered_app_num = new ArrayList<>();
    List<String> misc_error = new ArrayList<>();
    Map<String, Object> gabungan = new HashMap<>();
    Map<String, Object> gabungan1 = new HashMap<>();
    Map temp_error = new HashMap<>();
    Map specialList = new HashMap();
    Map mapAscend = new HashMap<>();
    Map mapAscend1 = new HashMap<>();

    ObjectMapper objectMapper = new ObjectMapper();

    public MainSearchService(Map response, List<String> triggered_code,
            List<List<String>> triggered_id, List<List<String>> triggered_app_num,
            List<Map> triggered_error, List<String> misc_error, RulesRepository rulesRepository,
            SetupRuleRepository setupRuleRepository, MongoFetch mongoFetch, Map<String, Object> gabungan,
            Map<String, Object> gabungan1, Map temp_error, FuzzyService fuzzyService, Map specialList,
            SoundexService soundexService, Map mapAscend, Map mapAscend1) {

        this.response = response;
        this.triggered_app_num = triggered_app_num;
        this.triggered_code = triggered_code;
        this.triggered_error = triggered_error;
        this.triggered_id = triggered_id;
        this.misc_error = misc_error;
        this.rulesRepository = rulesRepository;
        this.setupRuleRepository = setupRuleRepository;
        this.mongoFetch = mongoFetch;
        this.gabungan = gabungan;
        this.gabungan1 = gabungan1;
        this.temp_error = temp_error;
        this.fuzzyService = fuzzyService;
        this.specialList = specialList;
        this.soundexService = soundexService;
        this.mapAscend = mapAscend;
        this.mapAscend1 = mapAscend1;
    }

    // public Map putIntoResponse() {
    // if (triggered_id.size() > 0) {
    // // System.out.println("masuk S");
    // response.put("Status", "S");
    // } else {
    // response.put("Status", "C");
    // }

    // response.put("Triggered code", triggered_code);
    // response.put("Triggered error", temp_error);
    // response.put("Triggered id", gabungan);
    // response.put("Triggered app number", gabungan1);
    // response.put("misc error", misc_error);
    // response.put("rc", 200);
    // response.put("rd", "Berhasil");
    // return response;
    // }

    public List<String> findErrorRules(List<SetupRule> lSetupRules) {
        List<String> misc_error_temp = new ArrayList<>();
        for (int i = 0; i < lSetupRules.size(); i++) {
            List<NewRule> newRuleIsi = this.rulesRepository.findByKode(lSetupRules.get(i).getKodeRule());
            // System.out.println("ada ? " + !newRuleIsi.isEmpty());

            if (newRuleIsi.isEmpty()) {
                misc_error_temp.add("Rules " + lSetupRules.get(i).getKodeRule() + " tidak ada di Database");
            }
        }
        return misc_error_temp;
    }

    public Map runThread(int nThread, List<SetupRule> lSetupRules, List<Thread> lThreads,
            List<Thread> lThreads1, List<TestThreads> lTestThreads, List<AscendThread> lAscThreads,
            InputApplicant inputApplicant, TestThreads testThreads,
            MongoTemplate mongoTemplate, MongoOperations mongoOperations, SoundexService soundexService1,
            CheckOwnValue checkOwnValue) {
        Map status = new HashMap<>();
        try {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < nThread; i++) {
                // System.out.println("test " + i);
                // String coba_null = null;
                // System.out.println(coba_null.split(","));
                JSONObject jsonObject = new JSONObject(inputApplicant);
                List<NewRule> newRuleIsi = this.rulesRepository.findByKode(lSetupRules.get(i).getKodeRule());
                // System.out.println(lSetupRules.get(i).getKodeRule());
                Boolean checkInput = checkOwnValue.ownValue1(newRuleIsi, jsonObject);

                // System.out.println(lSetupRules.get(i).getKodeRule() + " cek input: " +
                // checkInput);
                if (checkInput) {
                    if (lSetupRules.get(i).getDatabaseName().toUpperCase().equals("ASCEND")) {
                        AscendThread ascendThread = new AscendThread(fuzzyService, setupRuleRepository, rulesRepository,
                                lSetupRules.get(i).getKodeRule(), inputApplicant, i,
                                lSetupRules.get(i).getNDays() != null ? lSetupRules.get(i).getNDays() : 0,
                                soundexService1);

                        Thread thread1 = new Thread(ascendThread);
                        lThreads1.add(thread1);
                        lAscThreads.add(ascendThread);
                        thread1.start();
                    } else {
                        if (!newRuleIsi.isEmpty()) {
                            testThreads = new TestThreads(fuzzyService, rulesRepository, setupRuleRepository,
                                    lSetupRules.get(i).getKodeRule(),
                                    inputApplicant,
                                    mongoTemplate, mongoOperations, i, mongoFetch,
                                    lSetupRules.get(i).getNDays() != null ? lSetupRules.get(i).getNDays() : 0);
                            // Future<List<BasicDBObject>> future = completionService.submit(testThreads,
                            // testThreads.getResult());
                            // futures.add(future);
                            Thread thread = new Thread(testThreads);
                            lThreads.add(thread);
                            lTestThreads.add(testThreads);
                            thread.start();
                        }
                    }
                }
            }
            long endTime = System.currentTimeMillis();
            // System.out.println(" thread took: " + (endTime - startTime) + "ms");

            long startTimeR = System.currentTimeMillis();
            for (Thread thread : lThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            for (Thread thread1 : lThreads1) {
                try {
                    thread1.join();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            long endTimeR = System.currentTimeMillis();
            // System.out.println("Wait thread took: " + (endTimeR - startTimeR) + "ms");

            status.put("status", "OK");
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            status.put("status", "ERROR");
            status.put("error", "Cek runThread");
            System.out.println(e);
            return status;
        }

    }

    public Map getThreadResult(List<TestThreads> lTestThreads, List<AscendThread> lAscThreads) {
        Map status = new HashMap();
        try {
            for (TestThreads testThreads : lTestThreads) {
                if (!testThreads.errorStr().isEmpty()) {

                    temp_error.put(testThreads.getKode(), testThreads.errorStr());
                    // triggered_error.add(temp_error);
                }
                if (!testThreads.specialResult().isEmpty()) {
                    // System.out.println("special: " + testThreads.specialResult());
                    specialList.putAll(testThreads.specialResult());
                    // triggered_code.add(testThreads.getKode());
                }
                if (!testThreads.getResult().isEmpty()) {
                    int size = 0;
                    try {
                        size = this.setupRuleRepository.findThresholdByKode(testThreads.getKode());

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    List<BasicDBObject> result_list = testThreads.getResult();
                    List<String> temp_list = new ArrayList<>();
                    List<String> temp_list1 = new ArrayList<>();
                    for (BasicDBObject res : result_list) {
                        // System.out.println(res.getString("ID_NUMBER"));
                        temp_list.add(res.getString("_id"));
                        temp_list1.add(res.getString("Application_Number"));
                    }
                    Set<String> set_temp = new HashSet<>(temp_list);
                    Set<String> set_temp1 = new HashSet<>(temp_list1);
                    temp_list = new ArrayList<>(set_temp);
                    temp_list1 = new ArrayList<>(set_temp1);

                    if (temp_list1.size() > size) {
                        triggered_code.add(testThreads.getKode());
                        triggered_id.add(temp_list);
                        triggered_app_num.add(temp_list1);
                    }

                }
            }
            // System.out.println(specialList.toString());
            for (AscendThread ascendThread : lAscThreads) {
                // System.out.println("Kode: " + ascendThread.getKode());
                // System.out.println("Kode: " + ascendThread.getResult().size());
                if (!ascendThread.errorStr().isEmpty()) {
                    // List<String> temp_error = new ArrayList<>();
                    Map temp_error = new HashMap<>();
                    temp_error.put(ascendThread.getKode(), ascendThread.errorStr());
                    triggered_error.add(temp_error);
                }
                if (!ascendThread.isiAscend().isEmpty()) {
                    // List<String> temp_error = new ArrayList<>();
                    JSONArray isi_asc = ascendThread.isiAscend();
                    List<String> temp_id = new ArrayList<>();
                    for (int i = 0; i < isi_asc.length(); i++) {
                        JSONObject obj = isi_asc.getJSONObject(i).getJSONObject("_source");
                        temp_id.add(obj.getString("CUST_NBR"));
                        mapAscend1.put(obj.getString("CUST_NBR"), obj.toMap());
                    }
                    mapAscend.put(ascendThread.getKode(), temp_id);
                }
                // if (!ascendThread.getResult().isEmpty()) {

                // }
                if (!ascendThread.getResult().isEmpty()) {
                    // System.out.println("masok");
                    List<BasicDBObject> result_list = ascendThread.getResult();
                    List<String> temp_list = new ArrayList<>();
                    List<String> temp_list1 = new ArrayList<>();
                    for (BasicDBObject res : result_list) {
                        // System.out.println(res.getString("ID_NUMBER"));
                        temp_list.add(res.getString("CUST_NBR"));
                        temp_list1.add(res.getString("CUST_NBR"));
                    }
                    Set<String> set_temp = new HashSet<>(temp_list);
                    Set<String> set_temp1 = new HashSet<>(temp_list1);
                    temp_list = new ArrayList<>(set_temp);
                    temp_list1 = new ArrayList<>(set_temp1);
                    triggered_id.add(temp_list);
                    triggered_app_num.add(temp_list1);
                    // System.out.println(ascendThread.getKode());
                    triggered_code.add(ascendThread.getKode());
                }
            }

            // System.out.println("triggered code: " + triggered_code.toString());

            // Map<String, Object> gabungan = new HashMap<>();
            // Map<String, Object> gabungan1 = new HashMap<>();
            if (triggered_id.size() == triggered_code.size()) {
                for (int i = 0; i < triggered_id.size(); i++) {

                    gabungan.put(triggered_code.get(i), triggered_id.get(i));
                    gabungan1.put(triggered_code.get(i), triggered_app_num.get(i));
                }
            }
            status.put("status", "OK");
            return status;
        } catch (Exception e) {
            status.put("status", "ERROR");
            status.put("error", "cek getThreadResult");
            return status;
        }
    }

    public Map generateResultBody(JSONObject new_input_json, Map<String, Object> map_input,
            DBInputRepository dbInputRepository, Map<String, Object> to_db_data) {
        Map status = new HashMap<>();
        try {
            LocalDate dob = LocalDate.parse(new_input_json.getString("dob"));
            Integer age = Period.between(dob, LocalDate.now()).getYears();
            new_input_json.put("age", age.toString());
            map_input = new_input_json.toMap();

            for (Map.Entry<String, Object> isi : map_input.entrySet()) {
                String key = isi.getKey();
                Object value = isi.getValue().toString().toUpperCase();
                if (!isi.getValue().getClass().equals("String")) {
                    value = isi.getValue();
                }

                long startTime1 = System.currentTimeMillis();
                List<MasterDbInput> params = dbInputRepository.findAllParam("nadsMaster");
                for (MasterDbInput isix : params) {
                    if (isix.getParamInput().equals(key)) {
                        String new_key = isix.getParamDb();
                        to_db_data.put(new_key, value);
                    }
                }
                long endTime1 = System.currentTimeMillis();
                // System.out.println("Fetching took " + (endTime1 - startTime1) + "
                // milliseconds");

            }

            LocalDateTime localDateTime = LocalDateTime.now();
            LocalDate localDate = LocalDate.now();
            // System.out.println("ldt: " + localDateTime);
            to_db_data.put("Application_Date", LocalDate.now().toString());
            to_db_data.put("App_DateTime", LocalDateTime.now().toString());

            long endTime = System.currentTimeMillis();
            // System.out.println("System took " + (endTime - startTime) + " milliseconds");

            if (triggered_id.size() > 0) {
                // System.out.println("masuk S");
                response.put("Status", "S");
            } else {
                response.put("Status", "C");
            }
            // System.out.println(mapAscend.toString());
            if (!mapAscend.isEmpty()) {
                response.put("Ascend_Data", mapAscend1);
                // response.put("Ascend_Id", mapAscend);
            }
            if (!specialList.isEmpty()) {
                response.put("Special", specialList);
            }
            response.put("Triggered code", triggered_code);
            response.put("Triggered error", temp_error);
            response.put("Triggered id", gabungan);
            response.put("Triggered app number", gabungan1);
            response.put("misc error", misc_error);
            response.put("rc", 200);
            response.put("rd", "Berhasil");

            status.put("status", "OK");
            return status;
        } catch (Exception e) {
            status.put("status", "ERROR");
            status.put("error", "cek getThreadResult");
            return status;
        }

    }

}
