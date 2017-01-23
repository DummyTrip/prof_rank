package mk.ukim.finki.isis.edubio.services;

import mk.ukim.finki.isis.edubio.entities.SubjectDomain;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class SubjectDomainHibernate {
    @Inject
    Session session;

    public void store(SubjectDomain subjectDomain) {
        if (subjectDomain == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(subjectDomain);
    }

    public List<SubjectDomain> getAll() {
        return session.createCriteria(SubjectDomain.class).list();
    }

    public void update(SubjectDomain subjectDomain) {
        if (subjectDomain == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.update(subjectDomain);
    }

    public void delete(SubjectDomain subjectDomain) {
        if (subjectDomain == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.delete(subjectDomain);
    }

    public SubjectDomain getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return (SubjectDomain) session.get(SubjectDomain.class, id);
    }

    public List<SubjectDomain> getByColumn(String column, String value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(SubjectDomain.class);
        List<SubjectDomain> entities = criteria.add(eq(column, value)).list();

        return entities;
    }

    public List<String> findAllSubjectDomainNames() {
        List<String> subjectDomainNames = new ArrayList<String>();

        for (SubjectDomain subjectDomain : getAll()) {
            subjectDomainNames.add(subjectDomain.getName());
        }

        return subjectDomainNames;
    }
}
