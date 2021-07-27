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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class ClientEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 12)
    @NotNull
    @Size(max = 12)
    private String counterPartyId;

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private List<TradeEntity> trades;
    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private List<FNBEntity> fnbs;

    public ClientEntity() {
        trades = new ArrayList<>();
        fnbs = new ArrayList<>();
    }

    public ClientEntity(String counterPartyId) {
        this();
        this.counterPartyId = counterPartyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCounterPartyId() {
        return counterPartyId;
    }

    public void setCounterPartyId(String counterPartyId) {
        this.counterPartyId = counterPartyId;
    }

    public List<TradeEntity> getTrades() {
        return trades;
    }

    public void setTrades(List<TradeEntity> trades) {
        this.trades = trades;
    }

    public List<FNBEntity> getFnbs() {
        return fnbs;
    }

    public void setFnbs(List<FNBEntity> fnbs) {
        this.fnbs = fnbs;
    }

}
