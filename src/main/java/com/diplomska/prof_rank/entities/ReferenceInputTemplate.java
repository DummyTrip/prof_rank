package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
@Entity
public class ReferenceInputTemplate {
    private Long id;

    private String name;

    @Column
    @ElementCollection(targetClass = AttributeReferenceInputTemplate.class)
    private List<AttributeReferenceInputTemplate> attributeReferenceInputTemplates = new ArrayList<AttributeReferenceInputTemplate>();

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

    @OneToMany(mappedBy = "referenceInputTemplate")
    public List<AttributeReferenceInputTemplate> getAttributeReferenceInputTemplates() {
        return attributeReferenceInputTemplates;
    }

    public void setAttributeReferenceInputTemplates(List<AttributeReferenceInputTemplate> attributeReferenceInputTemplates) {
        this.attributeReferenceInputTemplates = attributeReferenceInputTemplates;
    }

    @Override
    public String toString() {
        return name;
    }
}
