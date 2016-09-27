package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 27.09.2016.
 */
public class ReferenceInstance {
    private Long id;

    private Reference reference;

    @Column
    @ElementCollection(targetClass = AttributeReference.class)
    private List<AttributeReference> attributeReferences = new ArrayList<AttributeReference>();

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
    public List<AttributeReference> getAttributeReferences() {
        return attributeReferences;
    }

    public void setAttributeReferences(List<AttributeReference> attributeReferences) {
        this.attributeReferences = attributeReferences;
    }

    @Override
    public String toString() {
        return getReference().getName();
    }
}
