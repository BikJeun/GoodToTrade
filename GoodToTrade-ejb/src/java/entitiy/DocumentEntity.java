/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiy;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import util.enumeration.DocEnum;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class DocumentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private DocEnum type;
    @Column(nullable = false)
    @NotNull
    private boolean status;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(nullable = true)
    private FNBEntity fnb;

    public DocumentEntity() {
    }

    public DocumentEntity(DocEnum type, boolean status) {
        this.type = type;
        this.status = status;
    }

    public DocEnum getType() {
        return type;
    }

    public void setType(DocEnum type) {
        this.type = type;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public FNBEntity getFnb() {
        return fnb;
    }

    public void setFnb(FNBEntity fnb) {
        this.fnb = fnb;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
