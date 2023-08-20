package com.nads.nadsengine.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity(name = "TBL_VW_NADS")
@Table
public class TblVwNads {

    @Id
    @Column(name = "ID_NUMBER", nullable = false, updatable = false, length = 25)
    private String idNumber;

    @Column(name = "FIN_ACCOUNT", length = 19)
    private String finAccount;

    // @Column(name = "coba", length = 19)
    // private String coba;

    @Column(name = "CUST_NUMBER", length = 25)
    private String custNumber;

    @Column(name = "CARD_NUMBER", length = 19)
    private String cardNumber;

    @Column(name = "CUST_NAME_PRIMARY", length = 40)
    private String custNamePrimary;

    @Column(name = "CUST_NAME_SUPPLEMENT", length = 40)
    private String custNameSupplement;

    @Column(name = "DATEOFBIRTH", precision = 8, scale = 0)
    private BigDecimal dateofbirth;

    @Column(name = "HOME_ADDR1", length = 40)
    private String homeAddr1;

    @Column(name = "HOME_ADDR2", length = 40)
    private String homeAddr2;

    @Column(name = "HOME_ADDR3", length = 40)
    private String homeAddr3;

    @Column(name = "HOME_ADDR4", length = 40)
    private String homeAddr4;

    @Column(name = "HOME_POSTCODE", length = 6)
    private String homePostcode;

    @Column(name = "HOME_PHONE", length = 15)
    private String homePhone;

    @Column(name = "TELP_COY", length = 15)
    private String telpCoy;

    @Column(name = "MOBILE", length = 15)
    private String mobile;

    @Column(name = "xpacstatus", length = 2)
    private String xpacStatus;

    @Column(name = "CRDACCT_BLK_CODE", length = 2)
    private String crdacctBlkCode;

    @Column(name = "CIF", length = 25)
    private String cif;

    @Column(name = "MOM_NAME", length = 40)
    private String momName;

    @Column(name = "NO_APLIKASI", length = 22)
    private String noAplikasi;

    @Column(name = "FLAG", length = 2)
    private String flag;

    @Column(name = "RECOMENDER_ID", length = 19)
    private String recomenderId;

    @Column(name = "RECOMENDER_NAME", length = 30)
    private String recomenderName;

    @Column(name = "PROGRAM_ID1", length = 6)
    private String programId1;

    @Column(name = "PROGRAM_ID2", length = 6)
    private String programId2;

    @Column(name = "PROGRAM_ID3", length = 6)
    private String programId3;

    @Column(name = "EMAIL", length = 40)
    private String email;

    @Column(name = "CREDIT_LIMIT", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "OUTS_BALANCE", precision = 16, scale = 2)
    private BigDecimal outsBalance;

    @Column(name = "LAST_PAYMENT_AMT", precision = 15, scale = 2)
    private BigDecimal lastPaymentAmt;

    @Column(name = "LAST_PAYMENT_DATE", precision = 8, scale = 0)
    private BigDecimal lastPaymentDate;

    @Column(name = "OPEN_DATE", precision = 8, scale = 0)
    private BigDecimal openDate;

    @Column(name = "CLOSE_DATE", precision = 8, scale = 0)
    private BigDecimal closeDate;

    @Column(name = "ISSUE_DATE", precision = 8, scale = 0)
    private BigDecimal issueDate;

    @Column(name = "B_SCORE", precision = 4, scale = 0)
    private BigDecimal bScore;

    @Column(name = "PRODUCT_CODE", precision = 3, scale = 0)
    private BigDecimal productCode;

    @Column(name = "RELATION_NAME", length = 30)
    private String relationName;

    @Column(name = "RELATION_ADDR1", length = 40)
    private String relationAddr1;

    @Column(name = "RELATION_ADDR2", length = 40)
    private String relationAddr2;

    @Column(name = "RELATION_ADDR3", length = 40)
    private String relationAddr3;

    @Column(name = "RELATION_ADDR4", length = 40)
    private String relationAddr4;

    @Column(name = "RELATION_HP", length = 15)
    private String relationHp;

    @Column(name = "COMPANY", length = 40)
    private String company;

    @Column(name = "ADDRESS_COMPANY1", length = 40)
    private String addressCompany1;

    @Column(name = "ADDRESS_COMPANY2", length = 40)
    private String addressCompany2;

    @Column(name = "ADDRESS_COMPANY3", length = 40)
    private String addressCompany3;

    @Column(name = "ADDRESS_COMPANY4", length = 40)
    private String addressCompany4;

    @Column(name = "COMPANY_POSTCODE", length = 6)
    private String companyPostcode;

    @Column(name = "CARD_STATUS")
    private Boolean cardStatus;

    @Column(name = "ACCOUNT_STATUS")
    private Boolean accountStatus;

    @Column(name = "ACCOUNT_MAINT_DATE", precision = 8, scale = 0)
    private BigDecimal accountMaintDate;

    @Column(name = "CARD_MAINT_DATE", precision = 8, scale = 0)
    private BigDecimal cardMaintDate;

    @Column(name = "CUST_MAINT_DATE", precision = 8, scale = 0)
    private BigDecimal custMaintDate;

    @Column(name = "CRDACCT_BRANCH_NBR", precision = 8, scale = 0)
    private BigDecimal crdacctBranchNbr;

    @Column(name = "CUST_PLC_BIRTH", length = 40)
    private String custPlcBirth;

    @Column(name = "NUMBER_ROW")
    private Integer numberRow;

    @Column(name = "CUST_TAX_ID", length = 100)
    private String custTaxId;

    @Column(name = "TEST", length = 100)
    private String test;

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(final String idNumber) {
        this.idNumber = idNumber;
    }

    public String getFinAccount() {
        return finAccount;
    }

    public void setFinAccount(final String finAccount) {
        this.finAccount = finAccount;
    }

    public String getCustNumber() {
        return custNumber;
    }

    public void setCustNumber(final String custNumber) {
        this.custNumber = custNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCustNamePrimary() {
        return custNamePrimary;
    }

    public void setCustNamePrimary(final String custNamePrimary) {
        this.custNamePrimary = custNamePrimary;
    }

    public String getCustNameSupplement() {
        return custNameSupplement;
    }

    public void setCustNameSupplement(final String custNameSupplement) {
        this.custNameSupplement = custNameSupplement;
    }

    public BigDecimal getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(final BigDecimal dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getHomeAddr1() {
        return homeAddr1;
    }

    public void setHomeAddr1(final String homeAddr1) {
        this.homeAddr1 = homeAddr1;
    }

    public String getHomeAddr2() {
        return homeAddr2;
    }

    public void setHomeAddr2(final String homeAddr2) {
        this.homeAddr2 = homeAddr2;
    }

    public String getHomeAddr3() {
        return homeAddr3;
    }

    public void setHomeAddr3(final String homeAddr3) {
        this.homeAddr3 = homeAddr3;
    }

    public String getHomeAddr4() {
        return homeAddr4;
    }

    public void setHomeAddr4(final String homeAddr4) {
        this.homeAddr4 = homeAddr4;
    }

    public String getHomePostcode() {
        return homePostcode;
    }

    public void setHomePostcode(final String homePostcode) {
        this.homePostcode = homePostcode;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(final String homePhone) {
        this.homePhone = homePhone;
    }

    public String getTelpCoy() {
        return telpCoy;
    }

    public void setTelpCoy(final String telpCoy) {
        this.telpCoy = telpCoy;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }

    public String getXpacStatus() {
        return xpacStatus;
    }

    public void setXpacStatus(final String xpacStatus) {
        this.xpacStatus = xpacStatus;
    }

    public String getCrdacctBlkCode() {
        return crdacctBlkCode;
    }

    public void setCrdacctBlkCode(final String crdacctBlkCode) {
        this.crdacctBlkCode = crdacctBlkCode;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(final String cif) {
        this.cif = cif;
    }

    public String getMomName() {
        return momName;
    }

    public void setMomName(final String momName) {
        this.momName = momName;
    }

    public String getNoAplikasi() {
        return noAplikasi;
    }

    public void setNoAplikasi(final String noAplikasi) {
        this.noAplikasi = noAplikasi;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(final String flag) {
        this.flag = flag;
    }

    public String getRecomenderId() {
        return recomenderId;
    }

    public void setRecomenderId(final String recomenderId) {
        this.recomenderId = recomenderId;
    }

    public String getRecomenderName() {
        return recomenderName;
    }

    public void setRecomenderName(final String recomenderName) {
        this.recomenderName = recomenderName;
    }

    public String getProgramId1() {
        return programId1;
    }

    public void setProgramId1(final String programId1) {
        this.programId1 = programId1;
    }

    public String getProgramId2() {
        return programId2;
    }

    public void setProgramId2(final String programId2) {
        this.programId2 = programId2;
    }

    public String getProgramId3() {
        return programId3;
    }

    public void setProgramId3(final String programId3) {
        this.programId3 = programId3;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(final BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getOutsBalance() {
        return outsBalance;
    }

    public void setOutsBalance(final BigDecimal outsBalance) {
        this.outsBalance = outsBalance;
    }

    public BigDecimal getLastPaymentAmt() {
        return lastPaymentAmt;
    }

    public void setLastPaymentAmt(final BigDecimal lastPaymentAmt) {
        this.lastPaymentAmt = lastPaymentAmt;
    }

    public BigDecimal getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(final BigDecimal lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public BigDecimal getOpenDate() {
        return openDate;
    }

    public void setOpenDate(final BigDecimal openDate) {
        this.openDate = openDate;
    }

    public BigDecimal getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(final BigDecimal closeDate) {
        this.closeDate = closeDate;
    }

    public BigDecimal getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(final BigDecimal issueDate) {
        this.issueDate = issueDate;
    }

    public BigDecimal getBScore() {
        return bScore;
    }

    public void setBScore(final BigDecimal bScore) {
        this.bScore = bScore;
    }

    public BigDecimal getProductCode() {
        return productCode;
    }

    public void setProductCode(final BigDecimal productCode) {
        this.productCode = productCode;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(final String relationName) {
        this.relationName = relationName;
    }

    public String getRelationAddr1() {
        return relationAddr1;
    }

    public void setRelationAddr1(final String relationAddr1) {
        this.relationAddr1 = relationAddr1;
    }

    public String getRelationAddr2() {
        return relationAddr2;
    }

    public void setRelationAddr2(final String relationAddr2) {
        this.relationAddr2 = relationAddr2;
    }

    public String getRelationAddr3() {
        return relationAddr3;
    }

    public void setRelationAddr3(final String relationAddr3) {
        this.relationAddr3 = relationAddr3;
    }

    public String getRelationAddr4() {
        return relationAddr4;
    }

    public void setRelationAddr4(final String relationAddr4) {
        this.relationAddr4 = relationAddr4;
    }

    public String getRelationHp() {
        return relationHp;
    }

    public void setRelationHp(final String relationHp) {
        this.relationHp = relationHp;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(final String company) {
        this.company = company;
    }

    public String getAddressCompany1() {
        return addressCompany1;
    }

    public void setAddressCompany1(final String addressCompany1) {
        this.addressCompany1 = addressCompany1;
    }

    public String getAddressCompany2() {
        return addressCompany2;
    }

    public void setAddressCompany2(final String addressCompany2) {
        this.addressCompany2 = addressCompany2;
    }

    public String getAddressCompany3() {
        return addressCompany3;
    }

    public void setAddressCompany3(final String addressCompany3) {
        this.addressCompany3 = addressCompany3;
    }

    public String getAddressCompany4() {
        return addressCompany4;
    }

    public void setAddressCompany4(final String addressCompany4) {
        this.addressCompany4 = addressCompany4;
    }

    public String getCompanyPostcode() {
        return companyPostcode;
    }

    public void setCompanyPostcode(final String companyPostcode) {
        this.companyPostcode = companyPostcode;
    }

    public Boolean getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(final Boolean cardStatus) {
        this.cardStatus = cardStatus;
    }

    public Boolean getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(final Boolean accountStatus) {
        this.accountStatus = accountStatus;
    }

    public BigDecimal getAccountMaintDate() {
        return accountMaintDate;
    }

    public void setAccountMaintDate(final BigDecimal accountMaintDate) {
        this.accountMaintDate = accountMaintDate;
    }

    public BigDecimal getCardMaintDate() {
        return cardMaintDate;
    }

    public void setCardMaintDate(final BigDecimal cardMaintDate) {
        this.cardMaintDate = cardMaintDate;
    }

    public BigDecimal getCustMaintDate() {
        return custMaintDate;
    }

    public void setCustMaintDate(final BigDecimal custMaintDate) {
        this.custMaintDate = custMaintDate;
    }

    public BigDecimal getCrdacctBranchNbr() {
        return crdacctBranchNbr;
    }

    public void setCrdacctBranchNbr(final BigDecimal crdacctBranchNbr) {
        this.crdacctBranchNbr = crdacctBranchNbr;
    }

    public String getCustPlcBirth() {
        return custPlcBirth;
    }

    public void setCustPlcBirth(final String custPlcBirth) {
        this.custPlcBirth = custPlcBirth;
    }

    public Integer getNumberRow() {
        return numberRow;
    }

    public void setNumberRow(final Integer numberRow) {
        this.numberRow = numberRow;
    }

    public String getCustTaxId() {
        return custTaxId;
    }

    public void setCustTaxId(final String custTaxId) {
        this.custTaxId = custTaxId;
    }

}
