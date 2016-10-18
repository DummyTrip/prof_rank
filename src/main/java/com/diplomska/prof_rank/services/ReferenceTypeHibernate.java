package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.Attribute;
import com.diplomska.prof_rank.entities.AttributeReferenceType;
import com.diplomska.prof_rank.entities.ReferenceType;
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
    public void store(ReferenceType referenceType) {
        if (referenceType == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(referenceType);
    }

    public List<ReferenceType> getAll() {
        return session.createCriteria(ReferenceType.class).list();
    }

    @CommitAfter
    public void update(ReferenceType referenceType) {
        if (referenceType == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.update(referenceType);
    }

    @CommitAfter
    public void delete(ReferenceType referenceType) {
        if (referenceType == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.delete(referenceType);
    }

    public ReferenceType getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return (ReferenceType) session.get(ReferenceType.class, id);
    }

    public List<ReferenceType> getByColumn(String column, String value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(ReferenceType.class);
        List<ReferenceType> entities = criteria.add(eq(column, value)).list();

        return entities;
    }

    public List<Attribute> getAttributes(ReferenceType referenceType) {
        if (referenceType == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<AttributeReferenceType> attributeReferenceTypes = referenceType.getAttributeReferenceTypes();
        List<Attribute> attributes = new ArrayList<Attribute>();

        for (AttributeReferenceType attributeReferenceType : attributeReferenceTypes) {
            attributes.add(attributeReferenceType.getAttribute());
        }

        return attributes;
    }

    public void setAttribute(ReferenceType referenceType, Attribute attribute) {
        if (referenceType == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        AttributeReferenceType attributeReferenceType = new AttributeReferenceType();

        attributeReferenceType.setReferenceType(referenceType);
        attributeReferenceType.setAttribute(attribute);

        session.saveOrUpdate(attributeReferenceType);
    }

    public void deleteAttribute(ReferenceType referenceType, Attribute attribute) {
        if (referenceType == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(AttributeReferenceType.class);
        List<AttributeReferenceType> entities = criteria
                .add(eq("referenceType", referenceType))
                .add(eq("attribute", attribute))
                .list();

        if (entities.size() < 1) {
            throw new IllegalStateException("No data in database.");
        }

        AttributeReferenceType attributeReferenceType = entities.get(0);
        attributeReferenceType.setReferenceType(null);
        referenceType.getAttributeReferenceTypes().remove(attributeReferenceType);
    }
}
