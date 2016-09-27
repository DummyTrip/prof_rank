package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;

/**
 * Created by Aleksandar on 27.09.2016.
 */
public class ReferenceInstanceReport {
    private Long id;

    private ReferenceInstance referenceInstance;

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
    @JoinColumn(name = "referenceInstance_id")
    public ReferenceInstance getReferenceInstance() {
        return referenceInstance;
    }

    public void setReferenceInstance(ReferenceInstance referenceInstance) {
        this.referenceInstance = referenceInstance;
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
