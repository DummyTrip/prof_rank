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
 * Created by Aleksandar on 27.09.2016.
 */
public class ReferenceInstanceHibernate {
    @Inject
    Session session;

    @Inject
    RulebookHibernate rulebookHibernate;

    @Inject
    ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    ReferenceHibernate referenceHibernate;

    @Inject
    UserHibernate userHibernate;

    @CommitAfter
    public void store(ReferenceInstance referenceInstance) {
        if (referenceInstance == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.save(referenceInstance);
    }

    public List<ReferenceInstance> getAll() {
        return session.createCriteria(ReferenceInstance.class).list();
    }

    @CommitAfter
    public void update(ReferenceInstance referenceInstance) {
        if (referenceInstance == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.update(referenceInstance);
    }

    @CommitAfter
    public void delete(ReferenceInstance referenceInstance) {
        if (referenceInstance == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.delete(referenceInstance);
    }

    public ReferenceInstance getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return (ReferenceInstance) session.get(ReferenceInstance.class, id);
    }

    public List<ReferenceInstance> getByColumn(String column, String value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(ReferenceInstance.class);
        List<ReferenceInstance> entities = criteria.add(eq(column, value)).list();

        return entities;
    }

    public List<ReferenceInstance> getByReference(Reference reference) {
        List<ReferenceInstance> referenceInstances = getAll();
        List<ReferenceInstance> referenceInstancesOfSpecificReference = new ArrayList<ReferenceInstance>();

        for (ReferenceInstance referenceInstance : referenceInstances) {
            if (referenceInstance.getReference().getId().equals(reference.getId())) {
                referenceInstancesOfSpecificReference.add(referenceInstance);
            }
        }

        return referenceInstancesOfSpecificReference;
    }

    public List<ReferenceInstance> getByReferenceAndUser(Reference reference, User user) {
        List<ReferenceInstance> referenceInstances = userHibernate.getReferenceInstances(user);
        List<ReferenceInstance> referenceInstancesOfSpecificReference = new ArrayList<ReferenceInstance>();

        for (ReferenceInstance referenceInstance : referenceInstances) {
            if (referenceInstance.getReference().getId().equals(reference.getId())) {
                referenceInstancesOfSpecificReference.add(referenceInstance);
            }
        }

        return referenceInstancesOfSpecificReference;
    }

    public List<Attribute> getAttributes(ReferenceInstance referenceInstance) {
        if (referenceInstance == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Reference reference = referenceInstance.getReference();
        return referenceHibernate.getAttributes(reference);
    }

    public List<Attribute> getAttributeValues(ReferenceInstance referenceInstance) {
        if (referenceInstance == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<AttributeReferenceInstance> attributeReferenceInstances = referenceInstance.getAttributeReferenceInstances();
        List<Attribute> attributes = new ArrayList<Attribute>();

        for (AttributeReferenceInstance attributeReferenceInstance : attributeReferenceInstances) {
            attributes.add(attributeReferenceInstance.getAttribute());
        }

        return attributes;
    }

    @CommitAfter
    public void setAttributeValue(ReferenceInstance referenceInstance, Attribute attribute, String value) {
        if (referenceInstance == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        AttributeReferenceInstance attributeReferenceInstance = new AttributeReferenceInstance();

        attributeReferenceInstance.setReferenceInstance(referenceInstance);
        attributeReferenceInstance.setAttribute(attribute);
        attributeReferenceInstance.setValue(value);

        session.persist(attributeReferenceInstance);
    }

    @CommitAfter
    public void deleteAttribute(ReferenceInstance referenceInstance, Attribute attribute) {
        if (referenceInstance == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(AttributeReferenceInstance.class);
        List<AttributeReferenceInstance> entities = criteria
                .add(eq("referenceInstance", referenceInstance))
                .add(eq("attribute", attribute))
                .list();

        if (entities.size() < 1) {
            throw new IllegalStateException("No data in database.");
        }

        AttributeReferenceInstance attributeReferenceInstance = entities.get(0);
        attributeReferenceInstance.setReferenceInstance(null);
        referenceInstance.getAttributeReferenceInstances().remove(attributeReferenceInstance);
    }

}
