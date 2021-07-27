/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import util.enumeration.FNBEnum;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class FNBEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private FNBEnum reportingCounterParty;

    @OneToMany(mappedBy = "fnb", fetch = FetchType.EAGER)
    private List<DocumentEntity> docs;
    @OneToOne(mappedBy = "fnb")
    private TradeEntity trade;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private ClientEntity client;

    public FNBEntity() {
        docs = new ArrayList<>();

    }

    public FNBEntity(FNBEnum reportingCounterParty) {
        this();
        this.reportingCounterParty = reportingCounterParty;
    }

    public FNBEnum getReportingCounterParty() {
        return reportingCounterParty;
    }

    public void setReportingCounterParty(FNBEnum reportingCounterParty) {
        this.reportingCounterParty = reportingCounterParty;
    }

    public List<DocumentEntity> getDocs() {
        return docs;
    }

    public void setDocs(List<DocumentEntity> docs) {
        this.docs = docs;
    }

    public TradeEntity getTrade() {
        return trade;
    }

    public void setTrade(TradeEntity trade) {
        this.trade = trade;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
