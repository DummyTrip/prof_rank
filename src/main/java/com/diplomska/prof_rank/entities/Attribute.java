package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
@Entity
public class Attribute {
    private Long id;

    private String name;

    private String inputType;

//    @Column
//    @ElementCollection(targetClass = AttributeReferenceType.class)
//    private List<AttributeReferenceType> attributeReferenceTypes = new ArrayList<AttributeReferenceType>();

//    @Column
//    @ElementCollection(targetClass = AttributeReference.class)
//    private List<AttributeReference> attributeReferences = new ArrayList<AttributeReference>();

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

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

//    @OneToMany(mappedBy = "attribute")
//    public List<AttributeReferenceType> getAttributeReferenceTypes() {
//        return attributeReferenceTypes;
//    }
//
//    public void setAttributeReferenceTypes(List<AttributeReferenceType> attributeReferenceTypes) {
//        this.attributeReferenceTypes = attributeReferenceTypes;
//    }

//    @OneToMany(mappedBy = "attribute")
//    public List<AttributeReference> getAttributeReferences() {
//        return attributeReferences;
//    }
//
//    public void setAttributeReferences(List<AttributeReference> attributeReferences) {
//        this.attributeReferences = attributeReferences;
//    }
}
