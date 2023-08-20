package com.nads.nadsengine.Models;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "CleanCustData")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CleanCustData {

    @Id
    @Field(name = "ID_NUMBER")
    private String idNumber;

    @Field(name = "FIN_ACCOUNT")
    private String finAccount;

    // @Field(name = "coba")
    // private String coba;

    @Field(name = "CUST_NUMBER")
    private String custNumber;

    @Field(name = "CARD_NUMBER")
    private String cardNumber;

    @Field(name = "CUST_NAME_PRIMARY")
    private String custNamePrimary;

    @Field(name = "CUST_NAME_SUPPLEMENT")
    private String custNameSupplement;

    @Field(name = "DATEOFBIRTH")
    private BigDecimal dateofbirth;

    @Field(name = "HOME_ADDR1")
    private String homeAddr1;

    @Field(name = "HOME_ADDR2")
    private String homeAddr2;

    @Field(name = "HOME_ADDR3")
    private String homeAddr3;

    @Field(name = "HOME_ADDR4")
    private String homeAddr4;

    @Field(name = "HOME_POSTCODE")
    private String homePostcode;

    @Field(name = "HOME_PHONE")
    private String homePhone;

    @Field(name = "TELP_COY")
    private String telpCoy;

    @Field(name = "MOBILE")
    private String mobile;

    @Field(name = "xpacstatus")
    private String xpacStatus;

    @Field(name = "CRDACCT_BLK_CODE")
    private String crdacctBlkCode;

    @Field(name = "CIF")
    private String cif;

    @Field(name = "MOM_NAME")
    private String momName;

    @Field(name = "NO_APLIKASI")
    private String noAplikasi;

    @Field(name = "FLAG")
    private String flag;

    @Field(name = "RECOMENDER_ID")
    private String recomenderId;

    @Field(name = "RECOMENDER_NAME")
    private String recomenderName;

    @Field(name = "PROGRAM_ID1")
    private String programId1;

    @Field(name = "PROGRAM_ID2")
    private String programId2;

    @Field(name = "PROGRAM_ID3")
    private String programId3;

    @Field(name = "EMAIL")
    private String email;

    @Field(name = "CREDIT_LIMIT")
    private BigDecimal creditLimit;

    @Field(name = "OUTS_BALANCE")
    private BigDecimal outsBalance;

    @Field(name = "LAST_PAYMENT_AMT")
    private BigDecimal lastPaymentAmt;

    @Field(name = "LAST_PAYMENT_DATE")
    private BigDecimal lastPaymentDate;

    @Field(name = "OPEN_DATE")
    private BigDecimal openDate;

    @Field(name = "CLOSE_DATE")
    private BigDecimal closeDate;

    @Field(name = "ISSUE_DATE")
    private BigDecimal issueDate;

    @Field(name = "B_SCORE")
    private BigDecimal bScore;

    @Field(name = "PRODUCT_CODE")
    private BigDecimal productCode;

    @Field(name = "RELATION_NAME")
    private String relationName;

    @Field(name = "RELATION_ADDR1")
    private String relationAddr1;

    @Field(name = "RELATION_ADDR2")
    private String relationAddr2;

    @Field(name = "RELATION_ADDR3")
    private String relationAddr3;

    @Field(name = "RELATION_ADDR4")
    private String relationAddr4;

    @Field(name = "RELATION_HP")
    private String relationHp;

    @Field(name = "COMPANY")
    private String company;

    @Field(name = "ADDRESS_COMPANY1")
    private String addressCompany1;

    @Field(name = "ADDRESS_COMPANY2")
    private String addressCompany2;

    @Field(name = "ADDRESS_COMPANY3")
    private String addressCompany3;

    @Field(name = "ADDRESS_COMPANY4")
    private String addressCompany4;

    @Field(name = "COMPANY_POSTCODE")
    private String companyPostcode;

    @Field(name = "CARD_STATUS")
    private String cardStatus;

    @Field(name = "ACCOUNT_STATUS")
    private String accountStatus;

    @Field(name = "ACCOUNT_MAINT_DATE")
    private BigDecimal accountMaintDate;

    @Field(name = "CARD_MAINT_DATE")
    private BigDecimal cardMaintDate;

    @Field(name = "CUST_MAINT_DATE")
    private BigDecimal custMaintDate;

    @Field(name = "CRDACCT_BRANCH_NBR")
    private BigDecimal crdacctBranchNbr;

    @Field(name = "CUST_PLC_BIRTH")
    private String custPlcBirth;

    @Field(name = "NUMBER_ROW")
    private Integer numberRow;

    @Field(name = "CUST_TAX_ID")
    private String custTaxId;

}
