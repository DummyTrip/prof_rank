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

    private Float points;

    private RulebookSection rulebookSection;

    private ReferenceInputTemplate referenceInputTemplate;

    @Column
    @ElementCollection(targetClass = AttributeReference.class)
    private List<AttributeReference> attributeReferences = new ArrayList<AttributeReference>();

    @Column
    @ElementCollection(targetClass = ReferenceTypeRulebookSection.class)
    private List<ReferenceTypeRulebookSection> referenceTypeRulebookSections = new ArrayList<ReferenceTypeRulebookSection>();

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

    public Float getPoints() {
        return points;
    }

    public void setPoints(Float points) {
        this.points = points;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    public RulebookSection getRulebookSection() {
        return rulebookSection;
    }

    public void setRulebookSection(RulebookSection rulebookSection) {
        this.rulebookSection = rulebookSection;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    public ReferenceInputTemplate getReferenceInputTemplate() {
        return referenceInputTemplate;
    }

    public void setReferenceInputTemplate(ReferenceInputTemplate referenceInputTemplate) {
        this.referenceInputTemplate = referenceInputTemplate;
    }

    @OneToMany(mappedBy = "referenceType")
    public List<AttributeReference> getAttributeReferences() {
        return attributeReferences;
    }

    public void setAttributeReferences(List<AttributeReference> attributeReferences) {
        this.attributeReferences = attributeReferences;
    }

    @OneToMany(mappedBy = "referenceType")
    public List<ReferenceTypeRulebookSection> getReferenceTypeRulebookSections() {
        return referenceTypeRulebookSections;
    }

    public void setReferenceTypeRulebookSections(List<ReferenceTypeRulebookSection> referenceTypeRulebookSections) {
        this.referenceTypeRulebookSections = referenceTypeRulebookSections;
    }

    @Override
    public String toString() {
        return name;
    }
}
