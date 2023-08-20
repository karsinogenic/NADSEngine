package com.nads.nadsengine.Controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.codec.language.Soundex;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
// import org.elasticsearch.index.query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.nads.nadsengine.Models.CleanCustData;
import com.nads.nadsengine.Models.FuzzyInput;
import com.nads.nadsengine.Models.InputApplicant;
import com.nads.nadsengine.Models.MasterDbInput;
import com.nads.nadsengine.Models.NewExtractedResult;
import com.nads.nadsengine.Models.NewRule;
import com.nads.nadsengine.Models.SetupRule;
import com.nads.nadsengine.Models.TblVwNads;
import com.nads.nadsengine.Repositories.BlacklistSetupRepository;
import com.nads.nadsengine.Repositories.CleanCustDataRepository;
import com.nads.nadsengine.Repositories.DBInputRepository;
import com.nads.nadsengine.Repositories.RulesRepository;
import com.nads.nadsengine.Repositories.SetupRuleRepository;
import com.nads.nadsengine.Repositories.VwNadsRepository;
import com.nads.nadsengine.Services.AESEncryptDecrypt;
import com.nads.nadsengine.Services.AscendThread;
import com.nads.nadsengine.Services.BlackListService;
import com.nads.nadsengine.Services.CheckOwnValue;
import com.nads.nadsengine.Services.CheckThreads;
import com.nads.nadsengine.Services.CustomBinarySearch;
import com.nads.nadsengine.Services.FuzzyService;
import com.nads.nadsengine.Services.FuzzyThreadNew;
import com.nads.nadsengine.Services.LOSService;
import com.nads.nadsengine.Services.LogEntry;
import com.nads.nadsengine.Services.LoggingService;
import com.nads.nadsengine.Services.MainSearchService;
import com.nads.nadsengine.Services.MongoFetch;
import com.nads.nadsengine.Services.NewHttpRequest;
import com.nads.nadsengine.Services.SoundexService;
import com.nads.nadsengine.Services.StringBuilderService;
import com.nads.nadsengine.Services.TestThread;
import com.nads.nadsengine.Services.TestThreads;
import com.nads.nadsengine.Services.ToAscendService;

import ch.qos.logback.core.model.Model;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
// import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.val;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

@RestController
@RequestMapping(path = "/testApi")
public class ApiController {
    @Autowired
    private VwNadsRepository vwNadsRepository;
    @Autowired
    private RulesRepository rulesRepository;
    @Autowired
    private SetupRuleRepository setupRuleRepository;

    // private final RateLimiter rateLimiter = RateLimiter.create(10.0); // Contoh:
    // Batas 10 thread per detik

    // @Autowired
    // private MainSearchService mss;

    // @Autowired
    private TestThreads testThreads;

    @Autowired
    private CleanCustDataRepository cleanCustDataRepository;

    @Autowired
    private StringBuilderService sbs;

    @Autowired
    private BlacklistSetupRepository blacklistSetupRepository;

    @Autowired
    private DBInputRepository dbInputRepository;

    @Autowired
    private MongoFetch mongoFetch;

    @PersistenceContext
    private EntityManager em;

    private AESEncryptDecrypt aesEncryptDecrypt;

    private final String baseQuery = "Select * from ";

    private final String endQuery = " Order By ID_NUMBER DESC";

    private final MongoTemplate mongoTemplate;
    private final MongoOperations mongoOperations;

    private FuzzySearch fuzzySearch;

    private FuzzyService fuzzyService;

    @Autowired
    private Validator validator;

    @Autowired
    public ApiController(MongoTemplate mongoTemplate, MongoOperations mongoOperations) {
        this.mongoTemplate = mongoTemplate;
        this.mongoOperations = mongoOperations;
    }

    @Autowired
    private BlackListService blackListService;

    @PostMapping(value = "/fuzzySearch")
    public ResponseEntity<Map> fuzzySearch(@RequestBody(required = true) FuzzyInput input) {
        Map hasil = new HashMap<>();
        List<ExtractedResult> fuzzy = fuzzySearch.extractAll(input.getValue1(), input.getValue2());
        List<NewExtractedResult> fuzzy_new = new ArrayList<>();
        for (ExtractedResult extractedResult : fuzzy) {
            int rasio = fuzzySearch.ratio(input.getValue1(), extractedResult.getString());
            NewExtractedResult newExtractedResult = new NewExtractedResult(extractedResult.getString(),
                    extractedResult.getScore(), rasio, extractedResult.getIndex());
            fuzzy_new.add(newExtractedResult);
        }

        hasil.put("rc", 200);
        hasil.put("rd", "Berhasil");
        hasil.put("hasil", fuzzy_new);

        return new ResponseEntity<Map>(hasil, HttpStatus.OK);
    }

    @GetMapping(value = "/testFuzzySearch")
    public ResponseEntity<Map> testFuzzySearch() {
        Query query = new Query();
        query.limit(9000);
        List<BasicDBObject> list = mongoTemplate.find(query, BasicDBObject.class, "CleanCustData");
        List<String> listnama = new ArrayList<>();

        for (BasicDBObject string : list) {
            if (string.getString("CUST_NAME_PRIMARY") != null)
                listnama.add(string.getString("CUST_NAME_PRIMARY"));
        }

        List<ExtractedResult> fuzzy = fuzzySearch.extractAll("BAGAS", listnama, 80);
        List<NewExtractedResult> fuzzy_new = new ArrayList<>();

        for (ExtractedResult extractedResult : fuzzy) {
            int rasio = fuzzySearch.ratio("NAUFAL", extractedResult.getString());
            NewExtractedResult newExtractedResult = new NewExtractedResult(extractedResult.getString(),
                    extractedResult.getScore(), rasio, extractedResult.getIndex());
            fuzzy_new.add(newExtractedResult);
        }

        Map hasil = new HashMap<>();
        hasil.put("rc", 200);
        hasil.put("rd", "Berhasil");
        hasil.put("hasil", fuzzy_new);
        return new ResponseEntity<Map>(hasil, HttpStatus.OK);
    }

    @GetMapping(value = "/cekBlackList")
    public ResponseEntity<Map> testFetch(@RequestBody(required = false) Map<String, Object> input) {
        Map hasil = new HashMap<>();
        Map validasi = blackListService.BlackListValidation(input);
        ObjectMapper objectMapper = new ObjectMapper();
        if (validasi.get("rc").equals(400)) {
            return new ResponseEntity<Map>(validasi, HttpStatus.BAD_REQUEST);
        }
        hasil = blackListService.BlackListFunction(input, "BlackListData");

        return new ResponseEntity<Map>(hasil, HttpStatus.OK);
    }

    @GetMapping(value = "/fetchData")
    public ResponseEntity<Map> fetchData(@RequestBody(required = false) Map<String, Object> input) {
        Map response = new HashMap<>();
        String collection = "nadsMaster";
        List<DBObject> list = new ArrayList<>();
        if (input == null) {
            list = mongoFetch.fetchLargeData(collection);
        } else {
            // list = mongoFetch.fetchWithParam(input, collection);
        }
        if (list == null) {
            return new ResponseEntity<>(response, null, 204);
        }
        response.put("rc", 200);
        response.put("rd", "Berhasil");
        response.put("data", list);
        response.put("size", list.size());
        return new ResponseEntity<>(response, null, 200);
    }

    @GetMapping(value = "/fetchID")
    public ResponseEntity<Map> fetchID() {
        Map response = new HashMap<>();
        BasicDBObject isi = mongoFetch.fecthWithId();
        response.put("rc", 200);
        response.put("rd", "Berhasil");
        response.put("data", isi);
        // response.put("size", list.size());
        return new ResponseEntity<>(response, null, 200);
    }

    @GetMapping(value = "/testFetchData")
    public ResponseEntity<Map> testFetchData(@RequestBody(required = false) Map<String, Object> input) {
        ResponseEntity response = this.mongoFetch.myEndpoint("CleanCustData");
        return response;
    }

    @GetMapping(value = "/testLog")
    public void testLog() {
        LoggingService logEntry = new LoggingService();
        logEntry.setLogLevel("INFO");
        logEntry.setExternalId("123456789");
        logEntry.setExternalIp("ip yang hit");
        logEntry.setInternalIp("10.14.20.174");
        logEntry.setInternalId("123456789");
        logEntry.setMessageType("REQUEST");
        logEntry.setGroupApplication("engine_casa_inqury");
        logEntry.setServiceApplication("inqury casa");
        logEntry.setProcessApplication("proses hit ke host xxxxxx");
        logEntry.setMessageKey("1234566789");
        logEntry.setMessageCode("01");
        logEntry.setMessageDescription("gagal prosed database XXXXX");
        logEntry.setMessageException("com.sakdhskhdakhd. erorrr...............");
        logEntry.setMessageBody("{JSON format} {rc:01 , rd gagal prosed database XXXXX}");

        String formattedLog = logEntry.formatLog();
        // System.out.println(formattedLog);
        logEntry.info(formattedLog);
    }

    @GetMapping(value = "/testAggregate")
    public ResponseEntity<Map> testAggregate() {
        Map hasil = new HashMap<>();
        MatchOperation matchStage = Aggregation.match(Criteria.where("FLAG").is("S"));
        // LimitOperation limitOperation = Aggregation.limit(100);
        // ProjectionOperation projectStage = Aggregation.project("foo", "bar.baz");

        Aggregation aggregation = Aggregation.newAggregation(matchStage);

        AggregationResults<CleanCustData> output = mongoTemplate.aggregate(aggregation, "CleanCustData",
                CleanCustData.class);
        List<CleanCustData> out = output.getMappedResults();

        hasil.put("hasil", out);

        return new ResponseEntity<Map>(hasil, HttpStatus.OK);
    }

    @GetMapping(value = "/stringBuilder")
    public ResponseEntity<Map> stringBuilderTest() {
        Map hasil = new HashMap<>();

        Query query = new Query();
        query.addCriteria(Criteria.where("FLAG").is("S"));
        query.limit(1);
        // query.maxTime(Duration.ofSeconds(15));

        List<Document> documents = mongoTemplate.find(query, Document.class, "CleanCustData");

        // List<JSONObject> results = new ArrayList<>();
        // for (Document document : documents) {
        // JSONObject jsonObject = new JSONObject(document);
        // results.add(jsonObject);
        // }

        // return result;

        // query.maxTimeMsec(10000);
        // List<CleanCustData> out = this.cleanCustDataRepository.findAllLimit(1);

        hasil.put("hasil", documents);
        // hasil.put("hasil_size", documents.size());

        return new ResponseEntity<Map>(hasil, HttpStatus.OK);
    }

    @GetMapping(value = "/testMongoRepo")
    public ResponseEntity<Map> mongoRepoTest() {
        Map hasil = new HashMap<>();

        // MongoFetch mongoFetch1 = new MongoFetch(mongoTemplate);
        Query query = new Query();
        Criteria criteria = new Criteria();
        List<Criteria> listOr = new ArrayList<>();
        List<Criteria> listAnd = new ArrayList<>();

        listOr.add(Criteria.where("Home_Address1").regex("DANAU MANINJAU"));
        listOr.add(Criteria.where("Company_Address1").regex("DANAU MANINJAU"));
        listAnd.add(Criteria.where("Surname").is("NADIRA CAHYANI"));
        if (!listOr.isEmpty()) {
            criteria.orOperator(listOr);
        }
        if (!listAnd.isEmpty()) {
            criteria.andOperator(listAnd);
        }
        query.addCriteria(criteria);

        // criteria.and("HOME_POSTCODE").exists(true).ne(null);
        // query.limit(1);

        System.out.println(query.toString());
        long startTime = System.currentTimeMillis();
        List<BasicDBObject> coba = mongoTemplate.find(query, BasicDBObject.class, "nadsMaster");
        long endTime = System.currentTimeMillis();
        hasil.put("hasil", coba);
        hasil.put("elapsed_time", endTime - startTime);

        return new ResponseEntity<>(hasil, HttpStatus.OK);

    }

    // @PostMapping(value = "/findRule")
    public ResponseEntity<Map> testFindRule(@RequestParam(required = false, value = "kode") String kode,
            @RequestBody InputApplicant inputApplicant) {

        Map response = new HashMap<>();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<InputApplicant>> violations = validator.validate(inputApplicant);
        if (!violations.isEmpty()) {
            response.put("rc", 400);
            response.put("rd", "Blank / Null");
            response.put("error", violations.iterator().next().getPropertyPath().toString());
            // throw new ConstraintViolationException(violations);
            // return new ResponseEntity<Map<String, Object>>(response,
            // HttpStatus.BAD_REQUEST);
            return new ResponseEntity<Map>(response, null, 400);
        }

        if (kode == null) {
            List<SetupRule> lSetupRules = this.setupRuleRepository.findAll();
            List<Thread> lThreads = new ArrayList<>();
            List<CheckThreads> lCheckThreads = new ArrayList<>();
            List<String> suspect = new ArrayList<>();
            List<String> errorList = new ArrayList<>();
            List<Integer> execTime = new ArrayList<>();
            try {
                for (SetupRule setupRule : lSetupRules) {
                    CheckThreads checkThreads = new CheckThreads(setupRule.getKodeRule(), inputApplicant,
                            rulesRepository,
                            setupRuleRepository,
                            sbs, cleanCustDataRepository);
                    Thread thread = new Thread(checkThreads);

                    lCheckThreads.add(checkThreads);
                    lThreads.add(thread);
                    thread.start();
                }

                for (Thread thread : lThreads) {
                    thread.join();
                }

                for (CheckThreads checkThreads : lCheckThreads) {
                    Map result = checkThreads.result();
                    // System.out.println(result.get("exec_time").toString());
                    if (result.get("flag").equals("1")) {
                        suspect.add(result.get("rule").toString());
                    }
                    if (result.get("flag").equals("2")) {
                        errorList.add(result.get("rd").toString());
                    }
                    execTime.add(Integer.valueOf(result.get("exec_time").toString()));
                }

                Double avg_exec_time = 0.0;
                for (Integer exe_time : execTime) {
                    avg_exec_time += exe_time;
                }
                avg_exec_time = avg_exec_time / execTime.size();
                response.put("rc", 200);
                response.put("rd", "Berhasil");
                response.put("error", errorList);
                response.put("suspect", suspect);
                response.put("avg_exec_time_per_thread", avg_exec_time);
            } catch (Exception e) {
                System.out.println(e.toString());
            }

        } else {
            CheckThreads checkThreads = new CheckThreads(kode, inputApplicant, rulesRepository, setupRuleRepository,
                    sbs, cleanCustDataRepository);
            Thread thread = new Thread(checkThreads);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // while (thread.isAlive()) {
            response = checkThreads.result();
            // }
        }
        return new ResponseEntity<Map>(response, HttpStatus.OK);
    }

    // @PostMapping(value = "/findRuleNew")
    public ResponseEntity<Map> findRuleNew(@RequestParam(required = false, value = "kode") String kode,
            @RequestBody InputApplicant inputApplicant) {

        Map response = new HashMap<>();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<InputApplicant>> violations = validator.validate(inputApplicant);
        if (!violations.isEmpty()) {
            response.put("rc", 400);
            response.put("rd", "Blank / Null");
            response.put("error", violations.iterator().next().getPropertyPath().toString());
            // throw new ConstraintViolationException(violations);
            // return new ResponseEntity<Map<String, Object>>(response,
            // HttpStatus.BAD_REQUEST);
            return new ResponseEntity<Map>(response, null, 400);
        }

        if (kode == null) {

        } else {
            List<NewRule> rule = this.rulesRepository.findByKode(kode);
            Criteria criteria = new Criteria();
            JSONObject jsonObject = new JSONObject(inputApplicant);
            for (NewRule newRule : rule) {
                if (newRule.getParamInput().equals("dob")) {
                    criteria.and(newRule.getParamDb()).is(jsonObject.get(newRule.getParamInput()) + ".0");
                } else {
                    criteria.and(newRule.getParamDb()).is(jsonObject.get(newRule.getParamInput()));
                }
            }
            Query query = new Query(criteria);
            System.out.println(query.toString());
            List<BasicDBObject> list = mongoTemplate.find(query, BasicDBObject.class, "CleanCustData");
            response.put("rc", 200);
            response.put("rd", "Berhasil");
            response.put("data", list);
            // }
        }
        return new ResponseEntity<Map>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/binarySearch")
    public ResponseEntity<Map> binarySearch(@RequestParam String name, @RequestParam Integer limit) {
        Map response = new HashMap<>();
        String collection = "CleanCustData";
        String column_name = "CUST_NAME_PRIMARY";
        String[] name_list = name.split(" ");
        System.out.println("testt " + name_list[0]);
        List<BasicDBObject> list = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.and("FLAG").is("P");
        // criteria.and("DATEOFBIRTH").is("19801025.0");
        // criteria.and(column_name).regex(name_list[0]);
        // criteria.where(column_name).regex(name_list[0]);
        Query query = new Query(criteria);
        query.with(Sort.by(Sort.Direction.ASC, column_name));
        query.limit(limit);
        System.out.println(query.toString());
        list = mongoTemplate.find(query, BasicDBObject.class, collection);

        CustomBinarySearch customBinarySearch = new CustomBinarySearch();

        List<Integer> list2 = customBinarySearch.binarySearchName(list, name, 5, column_name,
                10);
        System.out.println(list2.toString());

        List<BasicDBObject> lastList = new ArrayList<>();
        List<String> listnama = new ArrayList<>();
        if (list2.get(0) < list2.get(1)) {
            // System.out.println("empty");
            for (int i = list2.get(0); i < list2.get(1); i++) {
                lastList.add(list.get(i));
                listnama.add(list.get(i).getString(column_name));
            }
        } else {
            // System.out.println("not empty");
            for (BasicDBObject basicDBObject : list) {
                if (basicDBObject.getString(column_name) != null)
                    listnama.add(basicDBObject.getString(column_name));
            }
        }
        FuzzySearch fuzzySearch = new FuzzySearch();
        List<ExtractedResult> fuzzy = new ArrayList<>();
        for (String string : name_list) {
            // System.out.println("string: " + string);
            List<ExtractedResult> fuzzy_temp = fuzzySearch.extractAll(string, listnama, 80);
            if (!fuzzy_temp.isEmpty()) {
                fuzzy.addAll(fuzzy_temp);
            }
        }
        List<NewExtractedResult> fuzzy_reduced = new ArrayList<>();
        List<NewExtractedResult> fuzzy_new = new ArrayList<>();

        for (ExtractedResult extractedResult : fuzzy) {
            int rasio = fuzzySearch.ratio(name, extractedResult.getString());
            NewExtractedResult newExtractedResult = new NewExtractedResult(extractedResult.getString(),
                    extractedResult.getScore(), rasio, extractedResult.getIndex());
            fuzzy_reduced.add(newExtractedResult);
            if (rasio >= 80) {
                fuzzy_new.add(newExtractedResult);
            }
        }

        response.put("rc", 200);
        response.put("rd", "Berhasil");
        response.put("data", fuzzy_new);
        response.put("reduced data", fuzzy_reduced);
        if (!lastList.isEmpty()) {
            response.put("actual data", lastList.get(fuzzy.get(0).getIndex()));
        }
        response.put("actual size data", list.size());
        response = new TreeMap<>(response);
        return new ResponseEntity<Map>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/testSiklus")
    public ResponseEntity<Map> testSiklus(@RequestParam(required = false, value = "kode") String kode,
            @RequestParam(required = false, value = "thread") int nThread,
            @RequestBody InputApplicant inputApplicant) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        Map response = new HashMap<>();
        List<String> triggered_code = new ArrayList<>();

        // for (int j = 0; j < nThread; j++) {

        Optional<SetupRule> setupRuleIsi = this.setupRuleRepository.findByKode(kode);
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                // System.out.println("test");

                List<Thread> lThreads = new ArrayList<>();
                List<TestThreads> lTestThreads = new ArrayList<>();

                ExecutorService executorService = Executors.newFixedThreadPool(nThread);
                CompletionService<List<BasicDBObject>> completionService = new ExecutorCompletionService<>(
                        executorService);

                List<Future<List<BasicDBObject>>> futures = new ArrayList<>();

                for (int i = 0; i < nThread; i++) {
                    // System.out.println("test " + i);

                    testThreads = new TestThreads(fuzzyService, rulesRepository, setupRuleRepository, kode,
                            inputApplicant,
                            mongoTemplate, mongoOperations, i, mongoFetch, setupRuleIsi.get().getNDays());
                    // Future<List<BasicDBObject>> future = completionService.submit(testThreads,
                    // testThreads.getResult());
                    // futures.add(future);
                    Thread thread = new Thread(testThreads);
                    lThreads.add(thread);
                    lTestThreads.add(testThreads);
                    thread.start();
                }

                // while (!futures.isEmpty()) {
                // try {
                // Future<List<BasicDBObject>> completedFuture = completionService.take();
                // List<BasicDBObject> result = completedFuture.get(); // Retrieve the result
                // // System.out.println("Result: " + result);
                // futures.remove(completedFuture);
                // } catch (Exception e) {
                // // Handle exceptions
                // e.printStackTrace();
                // }
                // }

                // // Shutdown the ExecutorService
                // executorService.shutdown();

                for (Thread thread : lThreads) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                for (TestThreads testThreads : lTestThreads) {
                    if (!testThreads.getResult().isEmpty()) {
                        triggered_code.add(testThreads.getKode());
                    }
                }

                System.out.println("triggered code: " + triggered_code.toString());
                // TODO Auto-generated method stub
            }

        };

        Thread newThread = new Thread(runnable);
        newThread.start();
        // newThread.join();

        response.put("rc", 200);
        response.put("rd", "Berhasil");
        // response.put("data", testThreads.getResult());
        // response.put("actual data size", testThreads.getSize());
        // response.put("triggered code", triggered_code);
        long endTime = System.currentTimeMillis();
        System.out.println("System took " + (endTime - startTime) + " milliseconds");
        return new ResponseEntity<Map>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/testFindRule")
    public ResponseEntity<Map> testFindRule(@RequestParam(required = false, value = "kode") String kode,
            @RequestParam(required = false, value = "thread") int nThread,
            @RequestBody Map<String, Object> map_input) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        Map response = new HashMap<>();
        List<String> triggered_code = new ArrayList<>();
        List<String> misc_error = new ArrayList<>();
        List<List<String>> triggered_id = new ArrayList<>();
        List<Map> triggered_error = new ArrayList<>();
        List<List<String>> triggered_app_num = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();

        InputApplicant inputApplicant = objectMapper.convertValue(map_input, InputApplicant.class);
        System.out.println("salary: " + inputApplicant.getSalary().getClass());
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

        // for (int j = 0; j < nThread; j++) {

        // Runnable runnable = new Runnable() {

        // @Override
        // public void run() {
        // System.out.println("test");

        Optional<SetupRule> setupRuleIsi = this.setupRuleRepository.findByKode(kode);
        List<NewRule> newRuleIsi = this.rulesRepository.findByKode(kode);

        List<Thread> lThreads = new ArrayList<>();
        List<TestThreads> lTestThreads = new ArrayList<>();

        List<Thread> lThreads1 = new ArrayList<>();
        List<AscendThread> lAscThreads = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(nThread);
        CompletionService<List<BasicDBObject>> completionService = new ExecutorCompletionService<>(
                executorService);

        List<Future<List<BasicDBObject>>> futures = new ArrayList<>();
        FuzzyService fuzzyService1 = new FuzzyService();
        SoundexService soundexService1 = new SoundexService();

        for (int i = 0; i < nThread; i++) {
            if (setupRuleIsi.get().getDatabaseName().toUpperCase().equals("ASCEND")) {
                AscendThread ascendThread = new AscendThread(fuzzyService1, setupRuleRepository, rulesRepository,
                        setupRuleIsi.get().getKodeRule(), inputApplicant, i,
                        setupRuleIsi.get().getNDays() != null ? setupRuleIsi.get().getNDays() : 0, soundexService1);

                Thread thread1 = new Thread(ascendThread);
                lThreads1.add(thread1);
                lAscThreads.add(ascendThread);
                thread1.start();
            } else {
                // System.out.println("test " + i);
                if (!newRuleIsi.isEmpty() && setupRuleIsi.isPresent()) {
                    testThreads = new TestThreads(fuzzyService, rulesRepository, setupRuleRepository, kode,
                            inputApplicant,
                            mongoTemplate, mongoOperations, i, mongoFetch,
                            setupRuleIsi.get().getNDays() != null ? setupRuleIsi.get().getNDays() : 0);
                    // Future<List<BasicDBObject>> future = completionService.submit(testThreads,
                    // testThreads.getResult());
                    // futures.add(future);
                    Thread thread = new Thread(testThreads);
                    lThreads.add(thread);
                    lTestThreads.add(testThreads);
                    thread.start();
                } else {
                    misc_error.add("Tidak ada " + kode + " di Database");
                }
            }
        }

        // while (!futures.isEmpty()) {
        // try {
        // Future<List<BasicDBObject>> completedFuture = completionService.take();
        // List<BasicDBObject> result = completedFuture.get(); // Retrieve the result
        // // System.out.println("Result: " + result);
        // futures.remove(completedFuture);
        // } catch (Exception e) {
        // // Handle exceptions
        // e.printStackTrace();
        // }
        // }

        // // Shutdown the ExecutorService
        // executorService.shutdown();

        for (Thread thread1 : lThreads1) {
            try {
                thread1.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        for (Thread thread : lThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        for (TestThreads testThreads : lTestThreads) {
            if (!testThreads.errorStr().isEmpty()) {
                // List<String> temp_error = new ArrayList<>();
                Map temp_error = new HashMap<>();
                temp_error.put(testThreads.getKode(), testThreads.errorStr());
                triggered_error.add(temp_error);
            }
            if (!testThreads.getResult().isEmpty()) {
                triggered_code.add(testThreads.getKode());

            }
            if (!testThreads.getResult().isEmpty()) {
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
                triggered_id.add(temp_list);
                triggered_app_num.add(temp_list1);

            }
        }
        for (AscendThread ascendThread : lAscThreads) {
            System.out.println(ascendThread.getResult());
            if (!ascendThread.errorStr().isEmpty()) {
                // List<String> temp_error = new ArrayList<>();
                Map temp_error = new HashMap<>();
                temp_error.put(ascendThread.getKode(), ascendThread.errorStr());
                triggered_error.add(temp_error);
            }
            if (!ascendThread.getResult().isEmpty()) {
                triggered_code.add(ascendThread.getKode());

            }
            if (!ascendThread.getResult().isEmpty()) {
                List<BasicDBObject> result_list = ascendThread.getResult();
                List<String> temp_list = new ArrayList<>();
                List<String> temp_list1 = new ArrayList<>();
                // for (BasicDBObject res : result_list) {
                // // System.out.println(res.getString("ID_NUMBER"));
                // temp_list.add(res.getString("_id"));
                // temp_list1.add(res.getString("Application_Number"));
                // }
                Set<String> set_temp = new HashSet<>(temp_list);
                Set<String> set_temp1 = new HashSet<>(temp_list1);
                temp_list = new ArrayList<>(set_temp);
                temp_list1 = new ArrayList<>(set_temp1);
                triggered_id.add(temp_list);
                triggered_app_num.add(temp_list1);

            }
        }

        // System.out.println("triggered code: " + triggered_code.toString());
        // System.out.println("triggered error: " + triggered_error.toString());
        // System.out.println("triggered id: " + triggered_id.toString());
        // TODO Auto-generated method stub
        // }

        // };

        // Thread newThread = new Thread(runnable);
        // newThread.start();
        // newThread.join();

        // response.put("data", ascendThread.getResult());
        // response.put("actual data size", ascendThread.getSize());
        response.put("Triggered code", triggered_code);
        response.put("Triggered error", triggered_error);
        response.put("Triggered id", triggered_id);
        // response.put("Triggered id", triggered_app_num);
        response.put("misc error", misc_error);
        response.put("rc", 200);
        response.put("rd", "Berhasil");
        long endTime = System.currentTimeMillis();
        System.out.println("System took " + (endTime - startTime) + " milliseconds");
        return new ResponseEntity<Map>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/testFindAllRule")
    public ResponseEntity<Map> testFindAllRule(@RequestBody Map<String, Object> map_input) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        LoggingService loggingService = new LoggingService();
        Map response = new HashMap<>();
        List<String> triggered_code = new ArrayList<>();
        List<List<String>> triggered_id = new ArrayList<>();
        List<Map> triggered_error = new ArrayList<>();
        Map specialList = new HashMap();
        List<List<String>> triggered_app_num = new ArrayList<>();
        List<String> misc_error = new ArrayList<>();
        Map temp_error = new HashMap<>();
        Map mapAscend = new HashMap<>();
        Map mapAscend1 = new HashMap<>();
        Map<String, Object> gabungan = new HashMap<>();
        Map<String, Object> gabungan1 = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        FuzzyService fuzzyService1 = new FuzzyService();
        SoundexService soundexService1 = new SoundexService();

        MainSearchService mss = new MainSearchService(response, triggered_code, triggered_id, triggered_app_num,
                triggered_error, misc_error, rulesRepository, setupRuleRepository, mongoFetch, gabungan, gabungan1,
                temp_error, fuzzyService1, specialList, soundexService1, mapAscend, mapAscend1);

        if (!map_input.get("salary").equals("")) {
            map_input.put("salary", Long.valueOf(map_input.get("salary").toString()));
        }

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

        // System.out.println(specialList.toString());

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
            // mongoTemplate.insert(to_db_data, "nadsMaster");
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

    @PostMapping(value = "/cobaLos")
    public ResponseEntity<Map> cobaLOS(@RequestParam String refId, @RequestParam String status) {
        Map response = new HashMap<>();
        LOSService losService = new LOSService();
        JSONObject los = losService.toLOS(refId, status);
        response.putAll(los.toMap());
        return new ResponseEntity<Map>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/testFindAllRuleThread")
    public ResponseEntity<Map> testFindAllRuleThread(@RequestBody Map<String, Object> map_input)
            throws InterruptedException {
        long startTime = System.currentTimeMillis();

        List<SetupRule> lSetupRules = this.setupRuleRepository.findAllActive(true);
        Map response = new HashMap<>();
        Thread nadsThread = new Thread();
        List<String> triggered_code = new ArrayList<>();
        List<List<String>> triggered_id = new ArrayList<>();
        List<Map> triggered_error = new ArrayList<>();
        Map specialList = new HashMap<>();
        List<List<String>> triggered_app_num = new ArrayList<>();
        List<String> misc_error = new ArrayList<>();
        Map temp_error = new HashMap<>();
        Map mapAscend = new HashMap<>();
        Map mapAscend1 = new HashMap<>();
        Map<String, Object> gabungan = new HashMap<>();
        Map<String, Object> gabungan1 = new HashMap<>();
        FuzzyService fuzzyService = new FuzzyService();
        SoundexService soundexService1 = new SoundexService();
        CheckOwnValue checkOwnValue = new CheckOwnValue();

        ObjectMapper objectMapper = new ObjectMapper();
        RateLimiter rateLimiter = RateLimiter.create(50.0);
        if (rateLimiter.tryAcquire()) {
            Runnable nadsRunnable = new Runnable() {
                Map response = new HashMap<>();

                @Override
                public void run() {
                    InputApplicant inputApplicant = objectMapper.convertValue(map_input, InputApplicant.class);
                    Set<ConstraintViolation<InputApplicant>> violations = validator.validate(inputApplicant);
                    MainSearchService mss = new MainSearchService(response, triggered_code, triggered_id,
                            triggered_app_num,
                            triggered_error, misc_error, rulesRepository, setupRuleRepository, mongoFetch, gabungan,
                            gabungan1,
                            temp_error, fuzzyService, specialList, soundexService1, mapAscend, mapAscend1);

                    if (!violations.isEmpty()) {
                        List<String> error_str = new ArrayList<>();
                        for (ConstraintViolation<InputApplicant> violation : violations) {
                            error_str.add(violation.getPropertyPath() + ": " + violation.getMessage());
                        }
                        response.put("rc: ", 400);
                        response.put("rd: ", "error input");
                        response.put("error: ", error_str);
                        // throw new ValidationException("Validation failed");
                        // return new ResponseEntity<Map>(response, null, 400);
                    }

                    inputApplicant = inputApplicant.toUpperCase(inputApplicant);

                    List<String> misc_error_temp = mss.findErrorRules(lSetupRules);
                    misc_error.addAll(misc_error_temp);

                    int nThread = lSetupRules.size();
                    // System.out.println("size: " + nThread);

                    List<Thread> lThreads = new ArrayList<>();
                    List<TestThreads> lTestThreads = new ArrayList<>();

                    List<Thread> lThreads1 = new ArrayList<>();
                    List<AscendThread> lAscThreads = new ArrayList<>();

                    CheckOwnValue checkOwnValue = new CheckOwnValue();
                    Map runThread = mss.runThread(nThread, lSetupRules, lThreads, lThreads1, lTestThreads, lAscThreads,
                            inputApplicant, testThreads, mongoTemplate, mongoOperations, soundexService1,
                            checkOwnValue);
                    // System.out.println("Status: " + runThread.get("status"));
                    if (!runThread.get("status").equals("OK")) {
                        // return new ResponseEntity<Map>(runThread, null, 400);
                    }

                    Map getThreadResult = mss.getThreadResult(lTestThreads, lAscThreads);
                    if (!getThreadResult.get("status").equals("OK")) {
                        // return new ResponseEntity<Map>(getThreadResult, null, 400);
                    }

                    Map<String, Object> to_db_data = new HashMap<>();
                    JSONObject new_input_json = new JSONObject(inputApplicant);
                    Map generateResultBody = mss.generateResultBody(new_input_json, map_input, dbInputRepository,
                            to_db_data);
                    if (!generateResultBody.get("status").equals("OK")) {
                        // return new ResponseEntity<Map>(generateResultBody, null, 400);
                    }

                    JSONObject temp_json = new JSONObject(response);

                    to_db_data.put("Json_Data", temp_json.toString());
                    to_db_data.put("Fraud_Alert", response.get("Status"));
                    to_db_data.put("Triggered_Rules", triggered_code.toString().replace("[", "").replace("]", ""));
                    to_db_data.put("Triggered_Id", gabungan.toString());
                    to_db_data.put("Triggered_AppNum", gabungan1.toString());
                    to_db_data.put("Special", specialList.toString());

                    if (!inputApplicant.getReproses().isEmpty()) {
                        to_db_data.put("Reproses", true);
                    } else {
                        to_db_data.put("Reproses", false);
                    }

                    // System.out.println("status: " + to_db_data.get("Status"));
                    to_db_data = new TreeMap<>(to_db_data);
                    if (to_db_data.get("Fraud_Alert").equals("S")) {
                        // System.out.println("masuk: " + to_db_data.get("Status").toString());
                        mongoTemplate.insert(to_db_data, "nadsTriggered");
                    } else {
                        // mongoTemplate.insert(to_db_data, "nadsMaster");
                    }

                    response = new TreeMap<>(response);

                }
            };
            nadsThread = new Thread(nadsRunnable);
            nadsThread.start();
        } else {
            System.out.println("rate limit exceed");
        }

        return new ResponseEntity<Map>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/testAsync")
    public ResponseEntity<Map> testAsync() {
        Map response = new HashMap<>();

        List<TestThread> listTT = new ArrayList<>();
        List<Thread> listT = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            TestThread testThread = new TestThread(i, this.setupRuleRepository);
            Thread thread = new Thread(testThread);
            listTT.add(testThread);
            listT.add(thread);
            thread.start();
        }
        // Thread thread = new Thread(runnable);

        return new ResponseEntity<Map>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/soundlikeCek")
    public String cobaFetchDB(@RequestParam("value1") String value1, @RequestParam("value2") String value2) {
        Soundex soundex = new Soundex();
        String hasil = value1 + " = " + soundex.soundex(value1) + "\n" + value2 + " = " + soundex.soundex(value2);
        return hasil;
    }

    // private RestHI

    @GetMapping(value = "/elasticBuilder")
    public Map elasticBuilder() throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder boolQueryBuilder1 = QueryBuilders.boolQuery();
        BoolQueryBuilder boolQueryBuilderOr = QueryBuilders.boolQuery();
        WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("CUST_LOCAL_NAME", "AGUNG*");
        boolQueryBuilder.must(wildcardQueryBuilder);
        // boolQueryBuilderOr.should(QueryBuilders.termQuery("CUST_LOCAL_NAME",
        // "RPAASC015*"));
        // boolQueryBuilder.must(wildcardQueryBuilder);
        wildcardQueryBuilder = QueryBuilders.wildcardQuery("CARD_NBR", "5242610051078825*");
        boolQueryBuilder.mustNot(wildcardQueryBuilder);
        // wildcardQueryBuilder = QueryBuilders.wildcardQuery("cust-nbr", "CM34107*");
        // boolQueryBuilder.must(boolQueryBuilderOr);
        // boolQueryBuilder.must(QueryBuilders.termQuery("cust-nbr",
        // "3322135303770003"),
        // boolQueryBuilderOr.should(QueryBuilders.termQuery("cust-branch", "00134")));
        String new_bool = boolQueryBuilder.toString();
        new_bool = new_bool.replaceAll("\\n", "");
        new_bool = new_bool.replaceAll("\\s", "");
        String jsonString1 = ("""
                {"query":""" + new_bool + "}");
        System.out.println(jsonString1);
        JSONObject newJsonObject = new JSONObject(new_bool);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("query", newJsonObject);
        String jsonString = jsonObject.toString().replaceAll("\\r|\\n", "");
        jsonString = jsonString.replace("\\", "");
        System.out.println(jsonString);
        // text = text.replaceAll("\\n", "");
        // text = text.replaceAll("\\s", "");

        // Removing line breaks
        NewHttpRequest newHttpRequest = new NewHttpRequest();
        String coba = newHttpRequest.postRequestBasicAuth("http://10.14.21.31:9200/dedup-asc*/_search", jsonString,
                "elastic", "elkbankmega2022@");
        JSONObject newJson = new JSONObject(coba);
        return newJson.toMap();
    }

    @PostMapping("/decision")
    public ResponseEntity<Map> decisionNads(@RequestParam String id, @RequestBody Map json) {
        Map response = new HashMap<>();
        Map hasil = mongoFetch.searchAndAdd("nadsTriggered", json, id);
        int post = mongoFetch.saveToCollection("nadsMaster", hasil);
        if (post == 200) {
            response.put("rc", post);
            response.put("rd", "Berhasil");
        } else {
            response.put("rc", post);
            response.put("rd", "Gagal");
        }
        return new ResponseEntity<Map>(response, null, post);
    }
}
