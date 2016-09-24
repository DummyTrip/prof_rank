package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.Role;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class RoleHibernate {
    @Inject
    Session session;

    @CommitAfter
    public void store(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(role);
    }

    public List<Role> getAll() {
        return session.createCriteria(Role.class).list();
    }

    @CommitAfter
    public void delete(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.delete(role);
    }

    public Role getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return (Role) session.get(Role.class, id);
    }

    public List<Role> getByColumn(String column, String value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(Role.class);
        List<Role> entities = criteria.add(eq(column, value)).list();

        return entities;
    }
}
