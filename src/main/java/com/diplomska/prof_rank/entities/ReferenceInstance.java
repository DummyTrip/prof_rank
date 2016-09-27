package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 27.09.2016.
 */
@Entity
public class ReferenceInstance {
    private Long id;

    private Reference reference;

    @Column
    @ElementCollection(targetClass = AttributeReferenceInstance.class)
    private List<AttributeReferenceInstance> attributeReferenceInstances = new ArrayList<AttributeReferenceInstance>();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NonVisual
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference= reference;
    }

    @OneToMany(mappedBy = "referenceInstance")
    public List<AttributeReferenceInstance> getAttributeReferenceInstances() {
        return attributeReferenceInstances;
    }

    public void setAttributeReferenceInstances(List<AttributeReferenceInstance> attributeReferenceInstances) {
        this.attributeReferenceInstances = attributeReferenceInstances;
    }

    @Override
    public String toString() {
        return getReference().getName();
    }
}
