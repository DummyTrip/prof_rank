package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.Attribute;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class AttributeHibernate {
    @Inject
    Session session;

    @CommitAfter
    public void store(Attribute attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(attribute);
    }

    public List<Attribute> getAll() {
        return session.createCriteria(Attribute.class).list();
    }

    @CommitAfter
    public void update(Attribute attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.update(attribute);
    }

    @CommitAfter
    public void delete(Attribute attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.delete(attribute);
    }

    public Attribute getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return (Attribute) session.get(Attribute.class, id);
    }

    public List<Attribute> getByColumn(String column, String value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(Attribute.class);
        List<Attribute> entities = criteria.add(eq(column, value)).list();

        return entities;
    }
}
