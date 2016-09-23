package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
@Entity
public class ReportSubjectDomain {
    private Long id;

    private Report report;

    private SubjectDomain subjectDomain;

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
    @JoinColumn(name = "report_id")
    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subjectDomain_id")
    public SubjectDomain getSubjectDomain() {
        return subjectDomain;
    }

    public void setSubjectDomain(SubjectDomain subjectDomain) {
        this.subjectDomain = subjectDomain;
    }
}
