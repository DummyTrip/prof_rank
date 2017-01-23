package mk.ukim.finki.isis.edubio.services;

import mk.ukim.finki.isis.edubio.entities.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

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
    PersonHibernate personHibernate;

    public void store(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(report);
    }

    public List<Report> getAll() {
        return session.createCriteria(Report.class).addOrder(Order.desc("startDate")).list();
    }

    public void update(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.update(report);
    }

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

    public Person getPerson(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return report.getPerson();
    }

    public void setPerson(Report report, Person person) {
        personHibernate.setReport(person, report);
    }

    public List<InstitutionProfRank> getInstitutions(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<InstitutionReport> institutionReports = session.createCriteria(InstitutionReport.class, "institutionReport")
                .add(eq("report", report))
                .list();

        List<InstitutionProfRank> institutionProfRanks = new ArrayList<InstitutionProfRank>();

        for (InstitutionReport institutionReport : institutionReports) {
            institutionProfRanks.add(institutionReport.getInstitutionProfRank());
        }

        return institutionProfRanks;
    }

    public void setInstitution(Report report, InstitutionProfRank institutionProfRank) {
        if (report == null || institutionProfRank == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        InstitutionReport institutionReport = new InstitutionReport();

        institutionReport.setReport(report);
        institutionReport.setInstitutionProfRank(institutionProfRank);

        session.saveOrUpdate(institutionReport);
    }


    public void deleteInstitution(Report report, InstitutionProfRank institutionProfRank) {
        if (report == null || institutionProfRank == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(InstitutionReport.class);
        List<InstitutionReport> entities = criteria
                .add(eq("report", report))
                .add(eq("institutionProfRank", institutionProfRank))
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

        List<ReportSubjectDomain> subjectDomainReports = session.createCriteria(ReportSubjectDomain.class)
                .add(eq("report", report))
                .list();;
        List<SubjectDomain> subjectDomains = new ArrayList<SubjectDomain>();

        for (ReportSubjectDomain subjectDomainReport : subjectDomainReports) {
            subjectDomains.add(subjectDomainReport.getSubjectDomain());
        }

        return subjectDomains;
    }

    public void setSubjectDomain(Report report, SubjectDomain subjectDomain) {
        if (report == null || subjectDomain == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        ReportSubjectDomain subjectDomainReport = new ReportSubjectDomain();

        subjectDomainReport.setReport(report);
        subjectDomainReport.setSubjectDomain(subjectDomain);

        session.saveOrUpdate(subjectDomainReport);
    }

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

        List<ReferenceReport> referenceReports = session.createCriteria(ReferenceReport.class)
                .add(eq("report", report))
                .list();
        List<Reference> references = new ArrayList<Reference>();

        for (ReferenceReport referenceReport : referenceReports) {
            references.add(referenceReport.getReference());
        }

        return references;
    }

    public void setReference(Report report, Reference reference) {
        if (report == null || reference == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        ReferenceReport referenceReport = new ReferenceReport();

        referenceReport.setReport(report);
        referenceReport.setReference(reference);
        if (reference.getReferenceType().getPoints() != null) {
            referenceReport.setPoints(reference.getReferenceType().getPoints());
        }

        session.saveOrUpdate(referenceReport);
    }

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

    public boolean containsRefrerence(Report report, Reference reference) {
        if (report == null || reference == null) {
            throw new IllegalArgumentException("Cannot fliter by null value.");
        }

        return session.createCriteria(ReferenceReport.class)
                .add(eq("report", report))
                .add(eq("reference", reference))
                .list().size() > 0;
    }
}
