package com.nads.nadsengine.Controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nads.nadsengine.Models.InputApplicant;
import com.nads.nadsengine.Models.MasterDbInput;
import com.nads.nadsengine.Models.SetupRule;
import com.nads.nadsengine.Repositories.DBInputRepository;
import com.nads.nadsengine.Repositories.RulesRepository;
import com.nads.nadsengine.Repositories.SetupRuleRepository;
import com.nads.nadsengine.Services.AscendThread;
import com.nads.nadsengine.Services.BlackListService;
import com.nads.nadsengine.Services.CheckOwnValue;
import com.nads.nadsengine.Services.FuzzyService;
import com.nads.nadsengine.Services.LOSService;
import com.nads.nadsengine.Services.LoggingService;
import com.nads.nadsengine.Services.MainSearchService;
import com.nads.nadsengine.Services.MongoFetch;
import com.nads.nadsengine.Services.SoundexService;
import com.nads.nadsengine.Services.TestThreads;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@RestController
@RequestMapping("/api")
public class NewApiController {

    @Autowired
    private Validator validator;

    @Autowired
    private RulesRepository rulesRepository;

    @Autowired
    private SetupRuleRepository setupRuleRepository;

    private TestThreads testThreads;

    private final MongoTemplate mongoTemplate;

    private final MongoOperations mongoOperations;

    @Autowired
    private MongoFetch mongoFetch;

    @Autowired
    private BlackListService blackListService;

    @Autowired
    private DBInputRepository dbInputRepository;

    public NewApiController(MongoTemplate mongoTemplate, MongoOperations mongoOperations) {
        this.mongoTemplate = mongoTemplate;
        this.mongoOperations = mongoOperations;
    }

    public Map cekBlackList(@RequestBody(required = false) Map<String, Object> input) {
        Map validasi = blackListService.BlackListValidation(input);
        ObjectMapper objectMapper = new ObjectMapper();
        Map hasil = new HashMap<>();
        Integer bool_hasil = 1;
        if (validasi.get("rc").equals(444)) {
            return validasi;
        }
        hasil = blackListService.BlackListFunction(input, "nadsBlacklist");
        return hasil;
        // return new ResponseEntity<Map>(hasil, HttpStatus.OK);
    }

    public Map mapToDB(Map<String, Object> map_input, DBInputRepository dbInputRepository) {
        Map<String, Object> hasil = new HashMap<>();
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
                    hasil.put(new_key, value);
                }
            }
            long endTime1 = System.currentTimeMillis();
            // System.out.println("Fetching took " + (endTime1 - startTime1) + "
            // milliseconds");

        }
        return hasil;
    }

    @PostMapping(value = "/findAllRule")
    public ResponseEntity<Map> testFindAllRule(@RequestBody Map<String, Object> map_input) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        LoggingService loggingService = new LoggingService();

        List<String> triggered_code = new ArrayList<>();
        List<List<String>> triggered_id = new ArrayList<>();
        List<Map> triggered_error = new ArrayList<>();
        List<List<String>> triggered_app_num = new ArrayList<>();
        List<String> misc_error = new ArrayList<>();

        Map specialList = new HashMap();
        Map response = new HashMap<>();
        Map temp_error = new HashMap<>();
        Map mapAscend = new HashMap<>();
        Map mapAscend1 = new HashMap<>();
        Map<String, Object> gabungan = new HashMap<>();
        Map<String, Object> gabungan1 = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        FuzzyService fuzzyService1 = new FuzzyService();
        SoundexService soundexService1 = new SoundexService();
        LOSService losService = new LOSService();

        Map findBlackList = this.cekBlackList(map_input);
        JSONObject jsonObjectBlackList = new JSONObject(findBlackList);

        InputApplicant inputApplicant = objectMapper.convertValue(map_input, InputApplicant.class);
        Set<ConstraintViolation<InputApplicant>> violations = validator.validate(inputApplicant);
        if (!violations.isEmpty()) {
            List<String> error_str = new ArrayList<>();
            for (ConstraintViolation<InputApplicant> violation : violations) {
                error_str.add(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            response.put("rc: ", 400);
            response.put("rd: ", "error input");
            response.put("error: ", error_str);
            // throw new ValidationException("Validation failed");
            return new ResponseEntity<Map>(response, null, 400);
        }

        inputApplicant = inputApplicant.toUpperCase(inputApplicant);

        // System.out.println(findBlackList.toString());
        if (findBlackList.get("rc").equals(444)) {
            return new ResponseEntity<Map>(findBlackList, null, 400);
        } else if (jsonObjectBlackList.get("rc").equals(400)) {
            JSONObject temp_input = new JSONObject(inputApplicant);
            JSONArray arrFindBlackList = new JSONArray(jsonObjectBlackList.getJSONArray("data"));
            List<String> listIdError = new ArrayList<>();
            for (int i = 0; i < arrFindBlackList.length(); i++) {
                listIdError
                        .add(arrFindBlackList.getJSONObject(i).getJSONObject("_id").getString("$oid").toString());
            }
            Map<String, Object> temp_insert = this.mapToDB(temp_input.toMap(), dbInputRepository);
            // System.out.println(temp_insert.toString());
            // temp_insert.putAll(map_input);
            // temp_insert.put("data", arrFindBlackList.get("data").toString());
            temp_insert.put("Fraud_Alert", "B");
            temp_insert.put("Triggered_Id", listIdError.toString().replace("[", "").replace("]", ""));
            temp_insert.put("Application_Date", LocalDate.now().toString());
            temp_insert.put("App_DateTime", LocalDateTime.now().toString());
            mongoTemplate.insert(temp_insert, "nadsMaster");
            JSONObject toLos = losService.toLOS(inputApplicant.getId(), "B");
            System.out.println("los: " + toLos.toString());
            if (toLos.isEmpty()) {
                response.put("rc: ", 400);
                response.put("rd: ", "Gagal ke LOS");
                return new ResponseEntity<Map>(response, null, 400);
            }
            return new ResponseEntity<Map>(findBlackList, null, 400);
        } else {
        }

        MainSearchService mss = new MainSearchService(response, triggered_code, triggered_id, triggered_app_num,
                triggered_error, misc_error, rulesRepository, setupRuleRepository, mongoFetch, gabungan, gabungan1,
                temp_error, fuzzyService1, specialList, soundexService1, mapAscend, mapAscend1);

        if (!map_input.get("salary").equals("")) {
            map_input.put("salary", Long.valueOf(map_input.get("salary").toString()));
        }

        List<SetupRule> lSetupRules = this.setupRuleRepository.findAllActive(true);

        List<String> misc_error_temp = mss.findErrorRules(lSetupRules);
        misc_error.addAll(misc_error_temp);

        int nThread = lSetupRules.size();
        // System.out.println("size: " + nThread);

        List<Thread> lThreads = new ArrayList<>();
        List<TestThreads> lTestThreads = new ArrayList<>();

        List<Thread> lThreads1 = new ArrayList<>();
        List<AscendThread> lAscThreads = new ArrayList<>();

        CheckOwnValue checkOwnValue = new CheckOwnValue();

        long startTimeT = System.currentTimeMillis();

        Map runThread = mss.runThread(nThread, lSetupRules, lThreads, lThreads1, lTestThreads, lAscThreads,
                inputApplicant, testThreads, mongoTemplate, mongoOperations, soundexService1, checkOwnValue);
        // System.out.println("Status: " + runThread.get("status"));
        if (!runThread.get("status").equals("OK")) {
            return new ResponseEntity<Map>(runThread, null, 400);
        }

        long endTimeT = System.currentTimeMillis();
        System.out.println("All thread took: " + (endTimeT - startTimeT) + "ms");

        long startTimeR = System.currentTimeMillis();
        Map getThreadResult = mss.getThreadResult(lTestThreads, lAscThreads);
        if (!getThreadResult.get("status").equals("OK")) {
            return new ResponseEntity<Map>(getThreadResult, null, 400);
        }
        long endTimeR = System.currentTimeMillis();
        System.out.println("All result took: " + (endTimeR - startTimeR) + "ms");

        long startTimeP = System.currentTimeMillis();
        Map<String, Object> to_db_data = new HashMap<>();
        JSONObject new_input_json = new JSONObject(inputApplicant);
        Map generateResultBody = mss.generateResultBody(new_input_json, map_input, dbInputRepository, to_db_data);
        if (!generateResultBody.get("status").equals("OK")) {
            return new ResponseEntity<Map>(generateResultBody, null, 400);
        }

        JSONObject temp_json = new JSONObject(response);
        JSONObject ascend_temp = new JSONObject(mapAscend);
        JSONObject ascend_temp1 = new JSONObject(mapAscend1);
        JSONObject triggered_id_json = new JSONObject(gabungan);
        JSONObject triggered_appnum_json = new JSONObject(gabungan1);
        JSONObject special_json = new JSONObject(specialList);

        // System.out.println("specialList: " + specialList.toString());

        // to_db_data.put("Json_Data", temp_json.toString());
        to_db_data.put("Fraud_Alert", response.get("Status"));
        to_db_data.put("Triggered_Rules", triggered_code.toString().replace("[", "").replace("]", ""));
        to_db_data.put("Triggered_Id", triggered_id_json.toString());
        to_db_data.put("Triggered_AppNum", triggered_appnum_json.toString());
        if (!specialList.isEmpty()) {
            to_db_data.put("Special", special_json.toString());
        }
        if (!mapAscend.isEmpty()) {
            // to_db_data.put("Ascend_Id", ascend_temp.toString());
            to_db_data.put("Ascend_Data", ascend_temp1.toString());
        }

        if (!inputApplicant.getReproses().isEmpty()) {
            to_db_data.put("Reproses", true);
        } else {
            to_db_data.put("Reproses", false);
        }

        // System.out.println("status: " + to_db_data.get("Status"));
        to_db_data = new TreeMap<>(to_db_data);

        // save to ascend mongo
        // ToAscendService toAscendService = new ToAscendService();
        // toAscendService.saveToAscendMongo(to_db_data, triggered_code,
        // setupRuleRepository, mongoTemplate);

        if (to_db_data.get("Fraud_Alert").equals("S")) {
            // System.out.println("masuk: " + to_db_data.get("Status").toString());
            mongoTemplate.insert(to_db_data, "nadsTriggered");
        } else {
            mongoTemplate.insert(to_db_data, "nadsMaster");
            JSONObject toLos = losService.toLOS(inputApplicant.getId(), "B");
            System.out.println("los: " + toLos.toString());
            if (toLos.isEmpty()) {
                response.put("rc: ", 400);
                response.put("rd: ", "Gagal ke LOS");
                loggingService.warning(response.toString());
                // return new ResponseEntity<Map>(response, null, 400);
            }
        }

        response = new TreeMap<>(response);
        long endTimeP = System.currentTimeMillis();
        System.out.println("post db took: " + (endTimeP - startTimeP) + "ms");
        String f_request = ("\nrequest: " + new JSONObject(inputApplicant).toString());
        String f_response = ("\nresponse: " + new JSONObject(response).toString());
        loggingService.info(f_request);
        loggingService.info(f_response);
        return new ResponseEntity<Map>(response, HttpStatus.OK);
    }

}
