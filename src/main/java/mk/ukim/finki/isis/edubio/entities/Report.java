package mk.ukim.finki.isis.edubio.entities;

import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(schema = "edubio", name = "reports")
public class Report {
    private Long id;

    private String title;

    private Float totalScore;

    private Date startDate;

    private Date endDate;

    @Column(name = "institution_reports_ids")
    @ElementCollection(targetClass = InstitutionReport.class)
    private List<InstitutionReport> institutionReports = new ArrayList<InstitutionReport>();

    @Column(name = "report_subject_domain_ids")
    @ElementCollection(targetClass = ReportSubjectDomain.class)
    private List<ReportSubjectDomain> reportSubjectDomains = new ArrayList<ReportSubjectDomain>();

    @Column(name = "reference_report_ids")
    @ElementCollection(targetClass = ReferenceReport.class)
    private List<ReferenceReport> referenceReports = new ArrayList<ReferenceReport>();

    private Person person;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "total_score")
    public Float getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Float totalScore) {
        this.totalScore = totalScore;
    }

    @Column(name = "start_date")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "end_date")
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
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
