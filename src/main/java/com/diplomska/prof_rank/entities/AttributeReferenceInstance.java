package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;

/**
 * Created by Aleksandar on 27.09.2016.
 */
@Entity
public class AttributeReferenceInstance {
    private Long id;

    private String value;

    private Attribute attribute;

    private ReferenceInstance referenceInstance;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NonVisual
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "attribute_id")
    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "referenceInstance_id")
    public ReferenceInstance getReferenceInstance() {
        return referenceInstance;
    }

    public void setReferenceInstance(ReferenceInstance referenceInstance) {
        this.referenceInstance = referenceInstance;
    }

}
