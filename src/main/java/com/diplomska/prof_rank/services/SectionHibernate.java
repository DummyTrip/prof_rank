package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.Section;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class SectionHibernate {
    @Inject
    Session session;

    @CommitAfter
    public void store(Section section) {
        if (section == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(section);
    }

    public List<Section> getAll() {
        return session.createCriteria(Section.class).list();
    }

    @CommitAfter
    public void delete(Section section) {
        if (section == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.delete(section);
    }

    public Section getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return (Section) session.get(Section.class, id);
    }

    public List<Section> getByColumn(String column, String value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(Section.class);
        List<Section> entities = criteria.add(eq(column, value)).list();

        return entities;
    }
}
