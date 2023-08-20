package com.nads.nadsengine.Models;

import jakarta.persistence.*;

@Entity
public class NewRule {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "kode_rule")
    private String KodeRule;

    @Column(name = "param_input")
    private String ParamInput;

    @Column(name = "param_db")
    private String ParamDb;

    @Column(name = "operator")
    private String Operator;

    @Column(name = "and_or")
    private String AndOr;

    @Column(name = "fuzzy_score")
    private Integer fuzzyScore;

    @Column(name = "fuzzy_token_score")
    private Integer fuzzyTokenScore;

    @Column(name = "is_own_value", columnDefinition = "INTEGER DEFAULT 0", nullable = false)
    private Integer isOwnValue;

    @Column(name = "value")
    private String value;

    @Column(name = "is_special_case", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isSpecialCase;

    @Column(name = "special_case_func")
    private String specialCaseFunc;

    @Column(name = "partial_start")
    private Integer partialStart;

    @Column(name = "partial_end")
    private Integer partialEnd;

    // @Column(name="")

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getKodeRule() {
        return KodeRule;
    }

    public void setKodeRule(String kodeRule) {
        KodeRule = kodeRule;
    }

    public String getParamInput() {
        return ParamInput;
    }

    public void setParamInput(String paramInput) {
        ParamInput = paramInput;
    }

    public String getParamDb() {
        return ParamDb;
    }

    public void setParamDb(String paramDb) {
        ParamDb = paramDb;
    }

    public String getOperator() {
        return Operator;
    }

    public void setOperator(String operator) {
        Operator = operator;
    }

    public String getAndOr() {
        return AndOr;
    }

    public void setAndOr(String andOr) {
        AndOr = andOr;
    }

    public Integer getFuzzyScore() {
        return fuzzyScore;
    }

    public void setFuzzyScore(Integer fuzzyScore) {
        this.fuzzyScore = fuzzyScore;
    }

    public Integer getFuzzyTokenScore() {
        return fuzzyTokenScore;
    }

    public void setFuzzyTokenScore(Integer fuzzyTokenScore) {
        this.fuzzyTokenScore = fuzzyTokenScore;
    }

    public Integer getIsOwnValue() {
        return isOwnValue;
    }

    public void setIsOwnValue(Integer isOwnValue) {
        this.isOwnValue = isOwnValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getPartialStart() {
        return partialStart;
    }

    public void setPartialStart(Integer partialStart) {
        this.partialStart = partialStart;
    }

    public Integer getPartialEnd() {
        return partialEnd;
    }

    public void setPartialEnd(Integer partialEnd) {
        this.partialEnd = partialEnd;
    }

    public Boolean getIsSpecialCase() {
        return isSpecialCase;
    }

    public void setIsSpecialCase(Boolean isSpecialCase) {
        this.isSpecialCase = isSpecialCase;
    }

    public String getSpecialCaseFunc() {
        return specialCaseFunc;
    }

    public void setSpecialCaseFunc(String specialCaseFunc) {
        this.specialCaseFunc = specialCaseFunc;
    }

}
