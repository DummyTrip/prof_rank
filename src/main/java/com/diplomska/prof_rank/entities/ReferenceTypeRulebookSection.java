package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
@Entity
public class ReferenceTypeRulebookSection {
    private Long id;

    private ReferenceType referenceType;

    private RulebookSection rulebookSection;

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
    @JoinColumn(name = "referenceType_id")
    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rulebookSection_id")
    public RulebookSection getRulebookSection() {
        return rulebookSection;
    }

    public void setRulebookSection(RulebookSection rulebookSection) {
        this.rulebookSection = rulebookSection;
    }
}
