package com.nads.nadsengine.Services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.json.JSONObject;

import com.nads.nadsengine.Models.InputApplicant;
import com.nads.nadsengine.Models.NewRule;
import com.nads.nadsengine.Models.SetupRule;

public class ElasticQueryBuilder {

    public org.elasticsearch.index.query.QueryBuilder QueryBuilder(NewRule rule, JSONObject input) {
        String new_input = input.getString(rule.getParamInput());
        if (rule.getParamInput().equals("dob")) {
            new_input = new_input.replaceAll("-", "");
        }
        TermQueryBuilder termQuery = QueryBuilders.termQuery(rule.getParamDb(), new_input);
        WildcardQueryBuilder wildcardQuery = QueryBuilders.wildcardQuery(rule.getParamDb(),
                input.getString(rule.getParamInput()));

        // TermQueryBuilder teramQuery2 = QueryBuilders.termQuery("field2", "value2");

        // if(andOr.toUpperCase().equals("AND")){
        String[] equal_op = { "=", "<>", "IN" };
        List<String> eq_op_list = new ArrayList<String>(Arrays.asList(equal_op));
        if (eq_op_list.contains(rule.getOperator().toUpperCase())) {
            if (rule.getOperator().toUpperCase().equals("IN")) {
                List<String> split_str = new ArrayList<String>(
                        Arrays.asList(input.getString(rule.getParamInput()).split(",")));
                termQuery = QueryBuilders.termQuery(rule.getParamDb(), split_str);
            }
            return termQuery;
        } else {
            return wildcardQuery;
        }

        // if (rule.getOperator().equals("<>")) {
        // boolQueryBuilder.mustNot(termQuery1);
        // }
        // }

        // if(andOr.toUpperCase().equals("OR")){
        // if(operator.equals("=")){
        // boolQueryBuilder.should(termQuery1);
        // }

        // if(operator.equals("<>")){
        // boolQueryBuilder.shouldNot(termQuery1);
        // }
        // }

        // return boolQueryBuilder;
    }

}
