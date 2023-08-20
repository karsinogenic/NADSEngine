package com.nads.nadsengine.Models;

import java.util.HashMap;
import java.util.Map;

import com.nads.nadsengine.Repositories.DBInputRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputApplicant {

        @NotNull
        private String id;

        @NotNull
        private String msc;

        @NotNull
        private String nik;

        @NotNull
        private String name;

        @NotNull
        private String gender;

        @NotNull
        private String dob;

        @NotNull
        private String alamat1;

        @NotNull
        private String alamat2;

        @NotNull
        private String alamat3;

        private String alamat4;

        @NotNull
        private String apptype;

        private String alamatfull;

        @NotNull
        private String kodepos;

        @NotNull
        private String telprumah;

        @NotNull
        private String hp;

        @NotNull
        private String company;

        @NotNull
        private String alamatkantor1;

        @NotNull
        private String alamatkantor2;

        @NotNull
        private String alamatkantor3;

        @NotNull
        private String alamatkantor4;

        private String alamatkantorfull;

        private String telpkantor;

        @NotNull
        private String kotakantor;

        @NotNull
        private String kodeposkantor;

        @NotNull
        private String mom;

        @NotNull
        private Long salary;

        @NotNull
        private String email;

        @NotNull
        private String agent;

        @NotNull
        private String pob;

        @NotNull
        private String jabatan;

        private String npwp;

        @NotNull
        private String emergencyname;

        @NotNull
        private String emergencyhp;

        @NotNull
        private String emergencytelp;

        private String reproses;

        private String age;

        @NotNull
        private String bidangusaha;

        public InputApplicant toUpperCase(InputApplicant inputApplicant) {
                // inputApplicant.setMsc(inputApplicant.getMsc().toUpperCase());
                // inputApplicant.setNik(inputApplicant.getNik().toUpperCase());
                inputApplicant.setName(inputApplicant.getName().toUpperCase());
                inputApplicant.setGender(inputApplicant.getGender().toUpperCase());
                inputApplicant.setDob(inputApplicant.getDob().toUpperCase());
                inputApplicant.setAlamat1(inputApplicant.getAlamat1().toUpperCase());
                inputApplicant.setAlamat2(inputApplicant.getAlamat2().toUpperCase());
                inputApplicant.setAlamat3(inputApplicant.getAlamat3().toUpperCase());
                // inputApplicant.setAlamat4(inputApplicant.getAlamat4().toUpperCase());
                inputApplicant
                                .setAlamatfull(
                                                (inputApplicant.getAlamat1().toUpperCase() + " "
                                                                + inputApplicant.getAlamat2().toUpperCase()
                                                                + " " + inputApplicant.getAlamat3().toUpperCase() + " "
                                                                + inputApplicant.getKodepos())
                                                                .replaceAll("\\s{2,}", " "));
                // inputApplicant.setKodepos(kodepos.toUpperCase());
                // inputApplicant.setTelprumah(telprumah.toUpperCase());
                // inputApplicant.setHp(hp.toUpperCase());
                inputApplicant.setCompany(inputApplicant.getCompany().toUpperCase());
                inputApplicant.setAlamatkantor1(inputApplicant.getAlamatkantor1().toUpperCase());
                inputApplicant.setAlamatkantor2(inputApplicant.getAlamatkantor2().toUpperCase());
                inputApplicant.setAlamatkantor3(inputApplicant.getAlamatkantor3().toUpperCase());
                inputApplicant.setAlamatkantor4(inputApplicant.getAlamatkantor4().toUpperCase());
                inputApplicant.setAlamatkantorfull((inputApplicant.getAlamatkantor1().toUpperCase() + " "
                                + inputApplicant.getAlamatkantor2().toUpperCase() + " "
                                + inputApplicant.getAlamatkantor3().toUpperCase() + " "
                                + inputApplicant.getAlamatkantor4().toUpperCase() + " " + inputApplicant.getKodepos())
                                .replaceAll("\\s{2,}", " "));
                inputApplicant.setKotakantor(inputApplicant.getKotakantor().toUpperCase());
                // inputApplicant.setKodeposkantor(kodeposkantor.toUpperCase());
                inputApplicant.setEmail(inputApplicant.getEmail().toUpperCase());
                inputApplicant.setMom(inputApplicant.getMom().toUpperCase());
                inputApplicant.setAgent(inputApplicant.getAgent().toUpperCase());
                inputApplicant.setEmergencyname(inputApplicant.getEmergencyname().toUpperCase());
                // inputApplicant.setEmergencyhp(emergencyhp.toUpperCase());
                // inputApplicant.setEmergencytelp(emergencytelp.toUpperCase());
                inputApplicant.setApptype(inputApplicant.getApptype().toUpperCase());
                /////

                inputApplicant.setPob(inputApplicant.getPob().toUpperCase());
                inputApplicant.setJabatan(inputApplicant.getJabatan().toUpperCase());
                inputApplicant.setBidangusaha(inputApplicant.getBidangusaha().toUpperCase());

                return inputApplicant;
        }

}
