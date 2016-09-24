package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
@Entity
public class Reference {
    private Long id;

    private String name;

    private RulebookSection rulebookSection;

    private ReferenceType referenceType;

    @Column
    @ElementCollection(targetClass = AttributeReference.class)
    private List<AttributeReference> attributeReferences = new ArrayList<AttributeReference>();

//    @Column
//    @ElementCollection(targetClass = ReferenceUser.class)
//    private List<ReferenceUser> referenceUsers = new ArrayList<ReferenceUser>();

    @Column
    @ElementCollection(targetClass = ReferenceRulebookSection.class)
    private List<ReferenceRulebookSection> referenceRulebookSections = new ArrayList<ReferenceRulebookSection>();

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

    @ManyToOne(cascade = CascadeType.ALL)
    public RulebookSection getRulebookSection() {
        return rulebookSection;
    }

    public void setRulebookSection(RulebookSection rulebookSection) {
        this.rulebookSection = rulebookSection;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    @OneToMany(mappedBy = "reference")
    public List<AttributeReference> getAttributeReferences() {
        return attributeReferences;
    }

    public void setAttributeReferences(List<AttributeReference> attributeReferences) {
        this.attributeReferences = attributeReferences;
    }

    @OneToMany(mappedBy = "reference")
    public List<ReferenceRulebookSection> getReferenceRulebookSections() {
        return referenceRulebookSections;
    }

    public void setReferenceRulebookSections(List<ReferenceRulebookSection> referenceRulebookSections) {
        this.referenceRulebookSections = referenceRulebookSections;
    }

//    @OneToMany(mappedBy = "reference")
//    public List<ReferenceUser> getReferenceUsers() {
//        return referenceUsers;
//    }
//
//    public void setReferenceUsers(List<ReferenceUser> referenceUsers) {
//        this.referenceUsers = referenceUsers;
//    }
}
