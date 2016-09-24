package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
@Entity
public class Report {
    private Long id;

    private String title;

    private Float totalScore;

    private Date startDate;

    private Date endDate;

    @Column
    @ElementCollection(targetClass = InstitutionReport.class)
    private List<InstitutionReport> institutionReports = new ArrayList<InstitutionReport>();

    @Column
    @ElementCollection(targetClass = ReportSubjectDomain.class)
    private List<ReportSubjectDomain> reportSubjectDomains = new ArrayList<ReportSubjectDomain>();

    @Column
    @ElementCollection(targetClass = ReferenceReport.class)
    private List<ReferenceReport> referenceReports = new ArrayList<ReferenceReport>();

    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NonVisual
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Float totalScore) {
        this.totalScore = totalScore;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @OneToMany(mappedBy = "report")
    public List<InstitutionReport> getInstitutionReports() {
        return institutionReports;
    }

    public void setInstitutionReports(List<InstitutionReport> institutionReports) {
        this.institutionReports = institutionReports;
    }

    @OneToMany(mappedBy = "report")
    public List<ReportSubjectDomain> getReportSubjectDomains() {
        return reportSubjectDomains;
    }

    public void setReportSubjectDomains(List<ReportSubjectDomain> reportSubjectDomains) {
        this.reportSubjectDomains = reportSubjectDomains;
    }

    @OneToMany(mappedBy = "report")
    public List<ReferenceReport> getReferenceReports() {
        return referenceReports;
    }

    public void setReferenceReports(List<ReferenceReport> referenceReports) {
        this.referenceReports = referenceReports;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
