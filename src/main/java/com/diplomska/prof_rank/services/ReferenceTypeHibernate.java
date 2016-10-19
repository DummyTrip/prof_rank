package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.Attribute;
import com.diplomska.prof_rank.entities.AttributeReferenceType;
import com.diplomska.prof_rank.entities.ReferenceInputTemplate;
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
public class ReferenceTypeHibernate {
    @Inject
    Session session;

    @CommitAfter
    public void store(ReferenceInputTemplate referenceInputTemplate) {
        if (referenceInputTemplate == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(referenceInputTemplate);
    }

    public List<ReferenceInputTemplate> getAll() {
        return session.createCriteria(ReferenceInputTemplate.class).list();
    }

    @CommitAfter
    public void update(ReferenceInputTemplate referenceInputTemplate) {
        if (referenceInputTemplate == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.update(referenceInputTemplate);
    }

    @CommitAfter
    public void delete(ReferenceInputTemplate referenceInputTemplate) {
        if (referenceInputTemplate == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.delete(referenceInputTemplate);
    }

    public ReferenceInputTemplate getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return (ReferenceInputTemplate) session.get(ReferenceInputTemplate.class, id);
    }

    public List<ReferenceInputTemplate> getByColumn(String column, String value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(ReferenceInputTemplate.class);
        List<ReferenceInputTemplate> entities = criteria.add(eq(column, value)).list();

        return entities;
    }

    public List<Attribute> getAttributes(ReferenceInputTemplate referenceInputTemplate) {
        if (referenceInputTemplate == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<AttributeReferenceType> attributeReferenceTypes = referenceInputTemplate.getAttributeReferenceTypes();
        List<Attribute> attributes = new ArrayList<Attribute>();

        for (AttributeReferenceType attributeReferenceType : attributeReferenceTypes) {
            attributes.add(attributeReferenceType.getAttribute());
        }

        return attributes;
    }

    public void setAttribute(ReferenceInputTemplate referenceInputTemplate, Attribute attribute) {
        if (referenceInputTemplate == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        AttributeReferenceType attributeReferenceType = new AttributeReferenceType();

        attributeReferenceType.setReferenceInputTemplate(referenceInputTemplate);
        attributeReferenceType.setAttribute(attribute);

        session.saveOrUpdate(attributeReferenceType);
    }

    public void deleteAttribute(ReferenceInputTemplate referenceInputTemplate, Attribute attribute) {
        if (referenceInputTemplate == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(AttributeReferenceType.class);
        List<AttributeReferenceType> entities = criteria
                .add(eq("referenceInputTemplate", referenceInputTemplate))
                .add(eq("attribute", attribute))
                .list();

        if (entities.size() < 1) {
            throw new IllegalStateException("No data in database.");
        }

        AttributeReferenceType attributeReferenceType = entities.get(0);
        attributeReferenceType.setReferenceInputTemplate(null);
        referenceInputTemplate.getAttributeReferenceTypes().remove(attributeReferenceType);
    }
}
