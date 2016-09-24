package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.*;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

import static com.github.sommeri.less4j.core.compiler.scopes.InScopeSnapshotRunner.id;
import static org.hibernate.criterion.Restrictions.*;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
public class UserHibernate {
    @Inject
    Session session;

    @CommitAfter
    public void store(User user) {
        session.persist(user);
    }

    @CommitAfter
    public void store(User user, Role role) {
        user.setRole(role);
        session.persist(user);
    }

    public List<User> getAll() {
        return session.createCriteria(User.class).list();
    }

    public User getById(Long id) {
        return (User) session.get(User.class, id);
    }

    public List<User> getByColumn(String column, String value) {
        Criteria criteria = session.createCriteria(User.class);
        List<User> entities = criteria.add(eq(column, value)).list();

        return entities;
    }

    public Role getRole(User user) {
        return user.getRole();
    }

    @CommitAfter
    public void setRole(User user, Role role) {
        user.setRole(role);
    }

    public List<Reference> getReferences(User user) {
        List<ReferenceUser> referenceUsers = user.getReferenceUsers();
        List<Reference> references = new ArrayList<Reference>();

        for (ReferenceUser referenceUser : referenceUsers) {
            references.add(referenceUser.getReference());
        }

        return references;
    }

    @CommitAfter
    public void setReference(User user, Reference reference) {
        ReferenceUser referenceUser = new ReferenceUser();

        referenceUser.setUser(user);
        referenceUser.setReference(reference);

        session.persist(referenceUser);
    }

    public List<Report> getReports(User user) {
        return user.getReports();
    }

    @CommitAfter
    public void setReport(User user, List<Report> reports) {
        for (Report report : reports) {
            report.setUser(user);
        }
        user.setReports(reports);

        session.persist(user);
    }

}
