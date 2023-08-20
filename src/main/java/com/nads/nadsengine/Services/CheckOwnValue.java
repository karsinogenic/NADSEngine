package com.nads.nadsengine.Services;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import com.nads.nadsengine.Models.NewRule;

public class CheckOwnValue {

    public Boolean ownValue1(List<NewRule> newRuleIsi, JSONObject input) {
        boolean hasil = true;
        // System.out.println("ownvalue");
        for (NewRule newRule : newRuleIsi) {
            if (newRule.getIsOwnValue().equals(2)) {
                String input_str = input.get(newRule.getParamInput()).toString();
                String own_value = newRule.getValue().toString();
                // System.out.println("D17");
                // System.out.println(input_str + " " + newRule.getOperator() + " " +
                // own_value);
                if (newRule.getOperator().equals(">")) {
                    hasil = Long.parseLong(input_str) > Long.parseLong(own_value);
                }
                if (newRule.getOperator().equals(">=")) {
                    hasil = Long.parseLong(input_str) >= Long.parseLong(own_value);
                }
                if (newRule.getOperator().equals("<")) {
                    hasil = Long.parseLong(input_str) < Long.parseLong(own_value);
                }
                if (newRule.getOperator().equals("<=")) {
                    hasil = Long.parseLong(input_str) <= Long.parseLong(own_value);
                }
                if (newRule.getOperator().equals("=")) {
                    hasil = input_str.equals(own_value);
                }
                if (newRule.getOperator().equals("<>")) {
                    hasil = !input_str.equals(own_value);
                }
                if (newRule.getOperator().equals("IN")) {
                    List<String> list_temp = Arrays.asList(own_value.split(","));
                    hasil = list_temp.contains(input_str);
                }
                if (newRule.getOperator().equals("NOT IN")) {
                    List<String> list_temp = Arrays.asList(own_value.split(","));
                    hasil = !list_temp.contains(input_str);
                }

                // System.out.println(newRule.getKodeRule() + ": " + hasil);
                if (newRule.getAndOr().toUpperCase().equals("AND") && !hasil) {
                    break;
                }
            }
        }
        return hasil;
    }

}
