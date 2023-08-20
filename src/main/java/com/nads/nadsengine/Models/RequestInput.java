package com.nads.nadsengine.Models;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
public class RequestInput {

    @Id
    private String id;

    private String msc;

    private String nik;

    private String name;

    private String gender;

    private String dob;

    private String alamat1;

    private String alamat2;

    private String alamat3;

    private String kodepos;

    private String telprumah;

    private String hp;

    private String company;

    private String alamatkantor1;

    private String alamatkantor2;

    private String alamatkantor3;

    private String alamatkantor4;

    private String kotakantor;

    private String kodeposkantor;

    private String mom;

    private String salary;

    private String email;

    private String agent;

    private String emergencyname;

    private String emergencyhp;

    private String emergencytelp;

    private String triggered_code;

    private String triggered_id;

    private Boolean is_sent;

    private LocalDateTime request_date;

    private LocalDateTime sent_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsc() {
        return msc;
    }

    public void setMsc(String msc) {
        this.msc = msc;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAlamat1() {
        return alamat1;
    }

    public void setAlamat1(String alamat1) {
        this.alamat1 = alamat1;
    }

    public String getAlamat2() {
        return alamat2;
    }

    public void setAlamat2(String alamat2) {
        this.alamat2 = alamat2;
    }

    public String getAlamat3() {
        return alamat3;
    }

    public void setAlamat3(String alamat3) {
        this.alamat3 = alamat3;
    }

    public String getKodepos() {
        return kodepos;
    }

    public void setKodepos(String kodepos) {
        this.kodepos = kodepos;
    }

    public String getTelprumah() {
        return telprumah;
    }

    public void setTelprumah(String telprumah) {
        this.telprumah = telprumah;
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAlamatkantor1() {
        return alamatkantor1;
    }

    public void setAlamatkantor1(String alamatkantor1) {
        this.alamatkantor1 = alamatkantor1;
    }

    public String getAlamatkantor2() {
        return alamatkantor2;
    }

    public void setAlamatkantor2(String alamatkantor2) {
        this.alamatkantor2 = alamatkantor2;
    }

    public String getAlamatkantor3() {
        return alamatkantor3;
    }

    public void setAlamatkantor3(String alamatkantor3) {
        this.alamatkantor3 = alamatkantor3;
    }

    public String getAlamatkantor4() {
        return alamatkantor4;
    }

    public void setAlamatkantor4(String alamatkantor4) {
        this.alamatkantor4 = alamatkantor4;
    }

    public String getKotakantor() {
        return kotakantor;
    }

    public void setKotakantor(String kotakantor) {
        this.kotakantor = kotakantor;
    }

    public String getKodeposkantor() {
        return kodeposkantor;
    }

    public void setKodeposkantor(String kodeposkantor) {
        this.kodeposkantor = kodeposkantor;
    }

    public String getMom() {
        return mom;
    }

    public void setMom(String mom) {
        this.mom = mom;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getEmergencyname() {
        return emergencyname;
    }

    public void setEmergencyname(String emergencyname) {
        this.emergencyname = emergencyname;
    }

    public String getEmergencyhp() {
        return emergencyhp;
    }

    public void setEmergencyhp(String emergencyhp) {
        this.emergencyhp = emergencyhp;
    }

    public String getEmergencytelp() {
        return emergencytelp;
    }

    public void setEmergencytelp(String emergencytelp) {
        this.emergencytelp = emergencytelp;
    }

    public String getTriggered_code() {
        return triggered_code;
    }

    public void setTriggered_code(String triggered_code) {
        this.triggered_code = triggered_code;
    }

    public String getTriggered_id() {
        return triggered_id;
    }

    public void setTriggered_id(String triggered_id) {
        this.triggered_id = triggered_id;
    }

    public Boolean getIs_sent() {
        return is_sent;
    }

    public void setIs_sent(Boolean is_sent) {
        this.is_sent = is_sent;
    }

    public LocalDateTime getRequest_date() {
        return request_date;
    }

    public void setRequest_date(LocalDateTime request_date) {
        this.request_date = request_date;
    }

    public LocalDateTime getSent_date() {
        return sent_date;
    }

    public void setSent_date(LocalDateTime sent_date) {
        this.sent_date = sent_date;
    }

}
