package mk.ukim.finki.isis.edubio.services;

import mk.ukim.finki.isis.edubio.entities.InstitutionProfRank;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class InstitutionHibernate {
    @Inject
    Session session;

    public void store(InstitutionProfRank institutionProfRank) {
        if (institutionProfRank == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(institutionProfRank);
    }

    public List<InstitutionProfRank> getAll() {
        return session.createCriteria(InstitutionProfRank.class).list();
    }

    public void update(InstitutionProfRank institutionProfRank) {
        if (institutionProfRank == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.update(institutionProfRank);
    }

    public void delete(InstitutionProfRank institutionProfRank) {
        if (institutionProfRank == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.delete(institutionProfRank);
    }

    public InstitutionProfRank getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return (InstitutionProfRank) session.get(InstitutionProfRank.class, id);
    }

    public List<InstitutionProfRank> getByColumn(String column, String value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(InstitutionProfRank.class);
        List<InstitutionProfRank> entities = criteria.add(eq(column, value)).list();

        return entities;
    }

    public List<String> findAllInstitutionNames() {
        List<String> institutionNames = new ArrayList<String>();

        for (InstitutionProfRank institution : getAll()) {
            institutionNames.add(institution.getName());
        }

        return institutionNames;
    }
}
