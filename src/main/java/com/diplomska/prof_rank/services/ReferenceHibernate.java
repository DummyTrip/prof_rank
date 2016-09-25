package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.*;
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

    @Inject
    RulebookHibernate rulebookHibernate;

    @Inject
    ReferenceTypeHibernate referenceTypeHibernate;

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
    public void update(Reference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.update(reference);
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

    public List<Section> getSections(Reference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<ReferenceRulebookSection> referenceRulebookSections= reference.getReferenceRulebookSections();
        List<RulebookSection> rulebookSections = new ArrayList<RulebookSection>();

        for (ReferenceRulebookSection referenceRulebookSection : referenceRulebookSections) {
            rulebookSections.add(referenceRulebookSection.getRulebookSection());
        }

        List<Section> sections = new ArrayList<Section>();

        for (RulebookSection rulebookSection : rulebookSections) {
            sections.add(rulebookSection.getSection());
        }

        return sections;
    }

    @CommitAfter
    public void setSection(Reference reference, RulebookSection rulebookSection) {
        if (reference == null || rulebookSection == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        ReferenceRulebookSection referenceRulebookSection = new ReferenceRulebookSection();

        referenceRulebookSection.setRulebookSection(rulebookSection);
        referenceRulebookSection.setReference(reference);

        session.persist(referenceRulebookSection);
    }

    @CommitAfter
    public void deleteSection(Reference reference, RulebookSection rulebookSection) {
        if (reference == null || rulebookSection == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(ReferenceRulebookSection.class);
        List<ReferenceRulebookSection> entities = criteria
                .add(eq("reference", reference))
                .add(eq("rulebookSection", rulebookSection))
                .list();

        if (entities.size() < 1) {
            throw new IllegalStateException("No data in database.");
        }

        ReferenceRulebookSection referenceRulebookSection = entities.get(0);
        referenceRulebookSection.setReference(null);
        reference.getReferenceRulebookSections().remove(referenceRulebookSection);
    }

    public void setSection(Reference reference, Section section, Rulebook rulebook) {
        if (reference == null || section == null || rulebook == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        RulebookSection rulebookSection = rulebookHibernate.getRulebookSection(rulebook, section);

        setSection(reference, rulebookSection);
    }

    public ReferenceType getReferenceType(Reference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return reference.getReferenceType();
    }

    @CommitAfter
    public void setReferenceType(Reference reference, ReferenceType referenceType) {
        if (reference == null || reference == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        reference.setReferenceType(referenceType);
        session.persist(reference);
    }

    public List<Attribute> getAttributes(Reference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }
        ReferenceType referenceType = reference.getReferenceType();
        List<Attribute> attributes = referenceTypeHibernate.getAttributes(referenceType);

        return attributes;
    }

    public List<Attribute> getAttributeValues(Reference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<AttributeReference> attributeReferences = reference.getAttributeReferences();
        List<Attribute> attributes = new ArrayList<Attribute>();

        for (AttributeReference attributeReference : attributeReferences) {
            attributes.add(attributeReference.getAttribute());
        }

        return attributes;
    }

    @CommitAfter
    public void setAttributeValue(Reference reference, Attribute attribute) {
        if (reference == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        AttributeReference attributeReference = new AttributeReference();

        attributeReference.setReference(reference);
        attributeReference.setAttribute(attribute);

        session.persist(attributeReference);
    }

    @CommitAfter
    public void deleteAttribute(Reference reference, Attribute attribute) {
        if (reference == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(AttributeReference.class);
        List<AttributeReference> entities = criteria
                .add(eq("reference", reference))
                .add(eq("attribute", attribute))
                .list();

        if (entities.size() < 1) {
            throw new IllegalStateException("No data in database.");
        }

        AttributeReference attributeReference = entities.get(0);
        attributeReference.setReference(null);
        reference.getAttributeReferences().remove(attributeReference);
    }
}
