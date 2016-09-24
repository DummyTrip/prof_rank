package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.Institution;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class InstitutionHibernate {
    @Inject
    Session session;

    @CommitAfter
    public void store(Institution institution) {
        if (institution == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(institution);
    }

    public List<Institution> getAll() {
        return session.createCriteria(Institution.class).list();
    }

    @CommitAfter
    public void delete(Institution institution) {
        if (institution == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.delete(institution);
    }

    public Institution getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return (Institution) session.get(Institution.class, id);
    }

    public List<Institution> getByColumn(String column, String value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(Institution.class);
        List<Institution> entities = criteria.add(eq(column, value)).list();

        return entities;
    }
}
