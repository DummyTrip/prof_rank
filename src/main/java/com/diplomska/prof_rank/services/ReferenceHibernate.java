package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.entities.RulebookSection;
import com.diplomska.prof_rank.entities.Section;
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
public class ReferenceHibernate {
    @Inject
    Session session;

    @CommitAfter
    public void store(Reference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(reference);
    }

    public List<Reference> getAll() {
        return session.createCriteria(Reference.class).list();
    }

    @CommitAfter
    public void delete(Reference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.delete(reference);
    }

    public Reference getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return (Reference) session.get(Reference.class, id);
    }

    public List<Reference> getByColumn(String column, String value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(Reference.class);
        List<Reference> entities = criteria.add(eq(column, value)).list();

        return entities;
    }

//    public List<Section> getReferences(Reference reference) {
//        if (reference == null) {
//            throw new IllegalArgumentException("Cannot filter by null value.");
//        }
//
//        List<RulebookSection> rulebookSections = reference.getRulebookSections();
//        List<Section> references = new ArrayList<Section>();
//
//        for (RulebookSection rulebookSection : rulebookSections) {
//            references.add(rulebookSection.getReference());
//        }
//
//        return references;
//    }
//
//    @CommitAfter
//    public void setReference(Reference reference, Reference reference) {
//        if (reference == null || reference == null) {
//            throw new IllegalArgumentException("Cannot persist null value.");
//        }
//        RulebookSection rulebookSection = new RulebookSection();
//
//        rulebookSection.setReference(reference);
//        rulebookSection.setReference(reference);
//
//        session.persist(rulebookSection);
//    }
}
