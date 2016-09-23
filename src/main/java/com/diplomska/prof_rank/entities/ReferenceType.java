package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
@Entity
public class ReferenceType {
    private Long id;

    private String name;

    @Column
    @ElementCollection(targetClass = AttributeReferenceType.class)
    private List<AttributeReferenceType> attributeReferenceTypes = new ArrayList<AttributeReferenceType>();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NonVisual
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "referenceType")
    public List<AttributeReferenceType> getAttributeReferenceTypes() {
        return attributeReferenceTypes;
    }

    public void setAttributeReferenceTypes(List<AttributeReferenceType> attributeReferenceTypes) {
        this.attributeReferenceTypes = attributeReferenceTypes;
    }
}
