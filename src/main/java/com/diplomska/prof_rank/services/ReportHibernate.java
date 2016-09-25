package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.*;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class ReportHibernate {
    @Inject
    Session session;

    @Inject
    UserHibernate userHibernate;

    @CommitAfter
    public void store(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(report);
    }

    public List<Report> getAll() {
        return session.createCriteria(Report.class).list();
    }

    @CommitAfter
    public void update(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.update(report);
    }

    @CommitAfter
    public void delete(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.delete(report);
    }

    public Report getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return (Report) session.get(Report.class, id);
    }

    public List<Report> getByColumn(String column, String value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(Report.class);
        List<Report> entities = criteria.add(eq(column, value)).list();

        return entities;
    }

    public User getUser(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return report.getUser();
    }

    public void setUser(Report report, User user) {
        userHibernate.setReport(user, report);
    }

    public List<Institution> getInstitutions(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<InstitutionReport> institutionReports = report.getInstitutionReports();
        List<Institution> institutions = new ArrayList<Institution>();

        for (InstitutionReport institutionReport : institutionReports) {
            institutions.add(institutionReport.getInstitution());
        }

        return institutions;
    }

    @CommitAfter
    public void setInstitution(Report report, Institution institution) {
        if (report == null || institution == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        InstitutionReport institutionReport = new InstitutionReport();

        institutionReport.setReport(report);
        institutionReport.setInstitution(institution);

        session.saveOrUpdate(institutionReport);
    }


    @CommitAfter
    public void deleteInstitution(Report report, Institution institution) {
        if (report == null || institution == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(InstitutionReport.class);
        List<InstitutionReport> entities = criteria
                .add(eq("report", report))
                .add(eq("institution", institution))
                .list();

        if (entities.size() < 1) {
            throw new IllegalStateException("No data in database.");
        }

        InstitutionReport institutionReport = entities.get(0);
        institutionReport.setReport(null);
        report.getInstitutionReports().remove(institutionReport);
    }

    public List<SubjectDomain> getSubjectDomains(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<ReportSubjectDomain> subjectDomainReports = report.getReportSubjectDomains();
        List<SubjectDomain> subjectDomains = new ArrayList<SubjectDomain>();

        for (ReportSubjectDomain subjectDomainReport : subjectDomainReports) {
            subjectDomains.add(subjectDomainReport.getSubjectDomain());
        }

        return subjectDomains;
    }

    @CommitAfter
    public void setSubjectDomain(Report report, SubjectDomain subjectDomain) {
        if (report == null || subjectDomain == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        ReportSubjectDomain subjectDomainReport = new ReportSubjectDomain();

        subjectDomainReport.setReport(report);
        subjectDomainReport.setSubjectDomain(subjectDomain);

        session.saveOrUpdate(subjectDomainReport);
    }

    @CommitAfter
    public void deleteSubjectDomain(Report report, SubjectDomain subjectDomain) {
        if (report == null || subjectDomain == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(ReportSubjectDomain.class);
        List<ReportSubjectDomain> entities = criteria
                .add(eq("report", report))
                .add(eq("subjectDomain", subjectDomain))
                .list();

        if (entities.size() < 1) {
            throw new IllegalStateException("No data in database.");
        }

        ReportSubjectDomain reportSubjectDomain = entities.get(0);
        reportSubjectDomain.setReport(null);
        report.getReportSubjectDomains().remove(reportSubjectDomain);
    }

    public List<Reference> getReferences(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<ReferenceReport> referenceReports = report.getReferenceReports();
        List<Reference> references = new ArrayList<Reference>();

        for (ReferenceReport referenceReport : referenceReports) {
            references.add(referenceReport.getReference());
        }

        return references;
    }

    @CommitAfter
    public void setReference(Report report, Reference reference) {
        if (report == null || reference == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        ReferenceReport referenceReport = new ReferenceReport();

        referenceReport.setReport(report);
        referenceReport.setReference(reference);

        session.saveOrUpdate(referenceReport);
    }

    @CommitAfter
    public void deleteReference(Report report, Reference reference) {
        if (report == null || reference == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(ReferenceReport.class);
        List<ReferenceReport> entities = criteria
                .add(eq("report", report))
                .add(eq("reference", reference))
                .list();

        if (entities.size() < 1) {
            throw new IllegalStateException("No data in database.");
        }

        ReferenceReport referenceReport = entities.get(0);
        referenceReport.setReport(null);
        report.getReferenceReports().remove(referenceReport);
    }
}
