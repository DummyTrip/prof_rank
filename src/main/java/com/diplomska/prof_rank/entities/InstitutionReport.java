package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
@Entity
public class InstitutionReport {
    private Long id;

    private InstitutionProfRank institutionProfRank;

    private Report report;

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
    @JoinColumn(name = "institution_id")
    public InstitutionProfRank getInstitutionProfRank() {
        return institutionProfRank;
    }

    public void setInstitutionProfRank(InstitutionProfRank institutionProfRank) {
        this.institutionProfRank = institutionProfRank;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "report_id")
    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
