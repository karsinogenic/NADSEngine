package com.nads.nadsengine.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "blacklist_setup")
public class BlacklistSetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String table_name;

    private String and_or;

    private String input_field_name;

    private String operator;

    private String output_field_name;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean is_active;

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getAnd_or() {
        return and_or;
    }

    public void setAnd_or(String and_or) {
        this.and_or = and_or;
    }

    public String getInput_field_name() {
        return input_field_name;
    }

    public void setInput_field_name(String input_field_name) {
        this.input_field_name = input_field_name;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOutput_field_name() {
        return output_field_name;
    }

    public void setOutput_field_name(String output_field_name) {
        this.output_field_name = output_field_name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

    // getter setter

}
