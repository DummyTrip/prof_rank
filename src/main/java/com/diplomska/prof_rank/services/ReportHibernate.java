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

        session.persist(institutionReport);
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

        session.persist(subjectDomainReport);
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

        session.persist(referenceReport);
    }
}
