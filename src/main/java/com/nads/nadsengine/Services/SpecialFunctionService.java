package com.nads.nadsengine.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.nads.nadsengine.Models.InputApplicant;
import com.nads.nadsengine.Models.NewRule;

public class SpecialFunctionService {

    public Boolean specialCaseResult(NewRule rule, List<BasicDBObject> isi, InputApplicant input) {
        boolean hasil = true;
        boolean hasil_final = true;
        if (rule.getSpecialCaseFunc().equals("growthIncome") && rule.getAndOr().toUpperCase().equals("AND")) {
            Long treshold1 = Long.parseLong(rule.getValue());
            Long treshold = (long) Math.ceil(treshold1);

            for (BasicDBObject basicDBObject : isi) {
                Long current_salary = input.getSalary();
                Long old_salary = Long.parseLong(basicDBObject.getString("Salary"));
                Long increase1 = Long.valueOf((current_salary - old_salary) * 100 / (old_salary > 0 ? old_salary : 1));
                Long increase = (long) Math.ceil(increase1);
                // System.out.println(current_salary + " : " + old_salary);

                if (rule.getOperator().equals(">")) {
                    hasil = increase > treshold;
                }
                if (rule.getOperator().equals(">=")) {
                    hasil = increase >= treshold;
                }
                if (rule.getOperator().equals("<")) {
                    hasil = increase < treshold;
                }
                if (rule.getOperator().equals("<=")) {
                    hasil = increase <= treshold;
                }
                if (rule.getOperator().equals("=")) {
                    hasil = increase == treshold;
                }
                if (rule.getOperator().equals("<>")) {
                    hasil = increase != treshold;
                }

                if (hasil == false) {
                    hasil_final = false;
                    break;
                }
            }
        }
        return hasil_final;
    }

    public Map specialCaseResultMap(NewRule rule, List<BasicDBObject> isi, InputApplicant input) {
        Map hasil_final = new HashMap<>();
        Map hasil_list = new HashMap<>();
        if (rule.getSpecialCaseFunc().equals("growthIncome")) {
            Long treshold = Long.parseLong(rule.getValue());
            for (BasicDBObject basicDBObject : isi) {
                Long current_salary = input.getSalary();
                Long old_salary = Long.parseLong(basicDBObject.getString("Salary"));
                Long increase1 = Long.valueOf((current_salary - old_salary) * 100 / (old_salary > 0 ? old_salary : 1));
                Long increase = (long) Math.ceil(increase1);
                Map hasil = new HashMap();
                hasil.put(basicDBObject.getString("_id"), increase.toString());
                hasil_list.putAll(hasil);
            }

            hasil_final.put("growthIncome", hasil_list);
            // System.out.println(hasil_list.size());
        }
        // System.out.println("hasil_final: " + hasil_final);
        return hasil_final;
    }

}
