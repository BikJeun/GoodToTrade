/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiy;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.FinancingTransactionEnum;
import util.enumeration.ReportingSideEnum;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class TradeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date date;
    @Column(nullable = false, unique = true)
    @NotNull
    private String tradeID;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ReportingSideEnum reportingSide;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String regulation;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String jurisdiction;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private FinancingTransactionEnum securitiesFinancingTransactionType;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private ClientEntity client;
    @OneToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(nullable = true)
    private FNBEntity fnb;

    public TradeEntity() {
    }

    public TradeEntity(Date date, String tradeID, ReportingSideEnum reportingSide, String regulation, String jurisdiction, FinancingTransactionEnum securitiesFinancingTransactionType) {
        this.date = date;
        this.tradeID = tradeID;
        this.reportingSide = reportingSide;
        this.regulation = regulation;
        this.jurisdiction = jurisdiction;
        this.securitiesFinancingTransactionType = securitiesFinancingTransactionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTradeID() {
        return tradeID;
    }

    public void setTradeID(String tradeID) {
        this.tradeID = tradeID;
    }

    public ReportingSideEnum getReportingSide() {
        return reportingSide;
    }

    public void setReportingSide(ReportingSideEnum reportingSide) {
        this.reportingSide = reportingSide;
    }

    public String getRegulation() {
        return regulation;
    }

    public void setRegulation(String regulation) {
        this.regulation = regulation;
    }

    public String getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(String jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public FinancingTransactionEnum getSecuritiesFinancingTransactionType() {
        return securitiesFinancingTransactionType;
    }

    public void setSecuritiesFinancingTransactionType(FinancingTransactionEnum securitiesFinancingTransactionType) {
        this.securitiesFinancingTransactionType = securitiesFinancingTransactionType;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }

    public FNBEntity getFnb() {
        return fnb;
    }

    public void setFnb(FNBEntity fnb) {
        this.fnb = fnb;
    }

}
