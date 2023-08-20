package com.nads.nadsengine.Services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.json.JSONObject;

import com.nads.nadsengine.Models.CleanCustData;
import com.nads.nadsengine.Models.InputApplicant;
import com.nads.nadsengine.Models.NewRule;
import com.nads.nadsengine.Models.SetupRule;
import com.nads.nadsengine.Repositories.CleanCustDataRepository;
import com.nads.nadsengine.Repositories.RulesRepository;
import com.nads.nadsengine.Repositories.SetupRuleRepository;

import jakarta.el.ELException;

public class CheckThreads implements Runnable {

    private String kode;
    private InputApplicant inputApplicant;
    private RulesRepository rulesRepository;
    private SetupRuleRepository setupRuleRepository;
    private StringBuilderService stringBuilderService;
    private CleanCustDataRepository cleanCustDataRepository;
    private Map result;
    private volatile boolean running = true;

    public CheckThreads(String kode, InputApplicant inputApplicant, RulesRepository rulesRepository,
            SetupRuleRepository setupRuleRepository, StringBuilderService sbs,
            CleanCustDataRepository cleanCustDataRepository) {
        this.kode = kode;
        this.rulesRepository = rulesRepository;
        this.setupRuleRepository = setupRuleRepository;
        this.stringBuilderService = sbs;
        this.cleanCustDataRepository = cleanCustDataRepository;
        this.inputApplicant = inputApplicant;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        Map response = new HashMap<>();
        while (running) {
            List<NewRule> newRules = this.rulesRepository.findByKode(kode);
            // System.out.println(kode);
            Optional<SetupRule> newRules1 = this.setupRuleRepository.findByKode(kode);

            if (newRules.isEmpty() || newRules1.isEmpty()) {
                response.put("rc", 400);
                response.put("rd", "Kode " + kode + " tidak ditemukan");
                response.put("flag", "2");
                // Interupt
                // System.out.println("ga ada kode");

                stop();
                result = response;
                break;
                // throw new Exception("Kode tidak ada");
            }
            // if(newRules.ge)
            List<JSONObject> orRule = new ArrayList<>();
            List<JSONObject> andRule = new ArrayList<>();
            List<Map> fuzzyList = new ArrayList<>();
            JSONObject jsonInput = new JSONObject(inputApplicant);

            for (NewRule newRule : newRules) {
                JSONObject newObject = new JSONObject();

                Object inputIsi;
                try {
                    inputIsi = jsonInput.get(newRule.getParamInput());

                } catch (Exception e) {
                    response.put("rc", 400);
                    response.put("rd", "Param '" + newRule.getParamInput() + "' tidak dimasukkan");
                    stop();
                    break;
                    // TODO: handle exception
                }

                if (!newRule.getOperator().equals("FUZZY")) {
                    newObject = stringBuilderService.objectBuilder(newRule.getParamDb(), inputIsi,
                            newRule.getOperator());
                    if (newRule.getAndOr().equals("or")) {
                        orRule.add(newObject);
                    } else {
                        andRule.add(newObject);
                    }
                } else {
                    Map fuzzyItem = new HashMap<>();
                    fuzzyItem.put("dbparam", newRule.getParamDb());
                    fuzzyItem.put("value", inputIsi);
                    fuzzyList.add(fuzzyItem);
                }
                // System.out.println(newRule.getParamInput().getClass());
                // newObject = stringBuilderService.objectBuilder(newRule.getParamDb(),
                // inputIsi,
                // newRule.getOperator());
                // if (newRule.getAndOr().equals("or")) {
                // orRule.add(newObject);
                // } else {
                // andRule.add(newObject);
                // }
            }

            if (newRules1.get().getNDays() != null) {
                JSONObject newObject = new JSONObject();
                LocalDate ld = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);
                ld = ld.minusMonths(Integer.valueOf(newRules1.get().getNDays()));
                String ld_str = ld.toString().replaceAll("-", "");
                Integer ld_int = Integer.valueOf(ld_str);
                newObject = stringBuilderService.objectBuilder("issueDate", ld_int, ">");
                andRule.add(newObject);
            }

            JSONObject finalAnd = stringBuilderService.andBuilder(andRule);
            // System.out.println("finalAnd :" + finalAnd);
            JSONObject finalOr = new JSONObject();
            finalOr = finalAnd;
            if (!orRule.isEmpty()) {
                if (!andRule.isEmpty()) {
                    orRule.add(finalAnd);
                }
                finalOr = stringBuilderService.orBuilder(orRule);
            }

            String aggregateStr = "{ $match:" + finalOr.toString() + "}";
            aggregateStr = aggregateStr.replace("\n", ",");

            // System.out.println(aggregateStr);
            List<CleanCustData> data = this.cleanCustDataRepository
                    .findAllCustom(aggregateStr, 100000);

            int NumDataPerThread = 100;
            int thread_size = data.size() / NumDataPerThread;
            if (thread_size < 1) {
                thread_size = 1;
            }

            List<Thread> listThreads = new ArrayList<>();
            List<FuzzyThreadNew> listFuzzyThread = new ArrayList<>();

            // System.out.println("isi: " + data.size());

            if (!fuzzyList.isEmpty()) {
                for (Map fuzzyItem : fuzzyList) {
                    for (int i = 0; i < thread_size; i++) {
                        // System.out.println("iter: " + fuzzyItem.toString());
                        FuzzyThreadNew fuzzyThreadNew = new FuzzyThreadNew(fuzzyItem,
                                data.subList(i * NumDataPerThread,
                                        thread_size == 1 ? data.size() : (i + 1) * NumDataPerThread),
                                91);
                        Thread fThread = new Thread(fuzzyThreadNew);
                        listThreads.add(fThread);
                        listFuzzyThread.add(fuzzyThreadNew);
                        fThread.start();
                    }

                    try {
                        for (Thread thread : listThreads) {
                            // System.out.println(thread.getName());
                            thread.join();
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    // System.out.println(listFuzzyThread.size());
                    int iter = 0;
                    for (FuzzyThreadNew fuzzyThreadNew : listFuzzyThread) {
                        // System.out.println(fuzzyThreadNew.findResult().toString());
                        data = fuzzyThreadNew.findResult();
                        if (!data.isEmpty()) {
                            // System.out.println("iter: " + iter);
                            // System.out.println(data.get(0).toString());
                            break;
                        }
                        iter++;
                    }
                }
            }

            response.put("query", finalOr.toString().replace("""
                    \
                    """, ""));

            if (data.isEmpty()) {
                response.put("rc", 200);
                response.put("rd", "Tidak ada data");
                response.put("flag", "0");
                // throw new Exception("Tidak ada data");
                stop();
                result = response;
                break;
            }

            response.put("rc", 200);
            response.put("rd", "Berhasil");
            response.put("flag", "1");
            response.put("rule", kode);
            response.put("data", data.get(0));
            stop();

            // result = response;
        }
        long endtime = System.currentTimeMillis();
        long executetime = endtime - startTime;
        response.put("exec_time", executetime);
        result = response;
    }

    public Map result() {
        return result;
    }
}
