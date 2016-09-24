package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.SubjectDomain;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class SubjectDomainHibernate {
    @Inject
    Session session;

    @CommitAfter
    public void store(SubjectDomain subjectDomain) {
        if (subjectDomain == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(subjectDomain);
    }

    public List<SubjectDomain> getAll() {
        return session.createCriteria(SubjectDomain.class).list();
    }

    @CommitAfter
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
}
