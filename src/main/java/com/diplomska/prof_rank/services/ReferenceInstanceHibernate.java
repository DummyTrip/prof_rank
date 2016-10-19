package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;

import java.util.*;

import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.in;

/**
 * Created by Aleksandar on 27.09.2016.
 */
public class ReferenceInstanceHibernate {
    @Inject
    Session session;

    @Inject
    RulebookHibernate rulebookHibernate;

    @Inject
    ReferenceInputTemplateHibernate referenceInputTemplateHibernate;

    @Inject
    ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    AttributeHibernate attributeHibernate;

    @Inject
    PersonHibernate personHibernate;

    @CommitAfter
    public void store(Reference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.save(reference);
    }

    public List<Reference> getAll() {
        return session.createCriteria(Reference.class).addOrder(Order.desc("id")).list();
    }

    public List<Reference> getAll(Integer offset, Integer limit) {
        return session.createCriteria(Reference.class).addOrder(Order.desc("id")).setFirstResult(offset).setMaxResults(limit).list();
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

    public List<String> getAllDisplayNames() {
        List<String> displayNames = new ArrayList<String>();
        List<Reference> references = getAll();

        for (Reference reference : references) {
            displayNames.add(getDisplayName(reference));
        }

        return displayNames;
    }

    public String getDisplayName(Reference reference) {
        String displayName = "";
        List<AttributeReferenceInstance> attributeReferenceInstances = getSortedAttributeReferenceInstance(reference);
        List<Attribute> attributes = getAttributeValues(reference);

        for (AttributeReferenceInstance attributeReferenceInstance : attributeReferenceInstances) {
            String attributeName = attributeReferenceInstance.getAttribute().getName();
            if (attributeReferenceInstance.isDisplay()) {
                if (displayName.length() > 0) {
                    displayName += ", ";
                }
                displayName += attributeReferenceInstance.getValue();
            }
        }

        return displayName;
    }

    public Integer getReferenceAuthorNum(Reference reference, Person person) {
        if (person == null || reference == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(ReferenceInstancePerson.class);
        criteria.add(eq("person", person))
                .add(eq("reference", reference));

        List<ReferenceInstancePerson> rips = criteria.list();

        if (rips.size() > 0) {
            ReferenceInstancePerson rip = (ReferenceInstancePerson) criteria.list().get(0);

            return rip.getAuthorNum();
        } else {
            return -1;
        }
    }

    public Integer getReferenceAuthorNum(Reference reference, String author) {
        if (author == null || reference == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(ReferenceInstancePerson.class);
        criteria.add(eq("author", author))
                .add(eq("reference", reference));

        List<ReferenceInstancePerson> rips = criteria.list();

        if (rips.size() > 0) {
            ReferenceInstancePerson rip = (ReferenceInstancePerson) criteria.list().get(0);

            return rip.getAuthorNum();
        } else {
            return -1;
        }
    }

    public boolean isDisplayAttribute(Reference reference, Attribute attribute) {
        String attributeName = attribute.getName();
        if (attributeName.equals("Наслов") ||
                attributeName.equals("Предмет") ||
                attributeName.equals("Име на проектот") ||
                attributeName.startsWith("Период") ||
                attributeName.equals("Год.") ||
                attributeName.startsWith("Позиција") ||
                attributeName.equals("Година"))
        {
            return true;
        } else {
            return false;
        }
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

    public List<Reference> getByReferenceType(ReferenceType referenceType) {
        return session.createCriteria(Reference.class)
                .add(eq("referenceType", referenceType)).addOrder(Order.desc("id")).list();
    }

    public List<Reference> getByReferenceType(ReferenceType referenceType, Integer offset, Integer limit) {
        return session.createCriteria(Reference.class)
                .add(eq("referenceType", referenceType)).addOrder(Order.desc("id")).setFirstResult(offset).setMaxResults(limit).list();
    }

    public List<Reference> getByReferenceTypeAndPerson(ReferenceType referenceType, Person person) {
        List<Reference> references = personHibernate.getReferences(person);
        List<Reference> referencesOfSpecificReferenceType = new ArrayList<Reference>();

        for (Reference reference : references) {
            if (reference.getReferenceType().getId().equals(referenceType.getId())) {
                referencesOfSpecificReferenceType.add(reference);
            }
        }

        return referencesOfSpecificReferenceType;
    }

    public List<Reference> getByReferenceTypeAndPerson(ReferenceType referenceType, Person person, Integer offset, Integer limit) {
        List<Reference> references = personHibernate.getReferences(person);
        List<Reference> referencesOfSpecificReferenceType = new ArrayList<Reference>();

        limit = limit + offset > references.size() ? references.size() : limit + offset;

        for (Reference reference : references) {
            if (reference.getReferenceType().getId().equals(referenceType.getId())) {
                referencesOfSpecificReferenceType.add(reference);
            }
        }

        return referencesOfSpecificReferenceType.subList(offset, offset + limit);
    }

    public List<Reference> getByReferenceTypeAndFilter(ReferenceType referenceType, Map<String, String> filterMap, Integer offset, Integer limit) {
        Criteria criteria = session.createCriteria(Reference.class, "reference");
        criteria.add(eq("referenceType", referenceType));
        criteria.createAlias("reference.attributeReferenceInstances", "ari");
        criteria.createAlias("ari.attribute", "attribute");

        for (String key : filterMap.keySet()) {
            criteria.add(eq("attribute.name", key));
            criteria.add(eq("ari.value", filterMap.get(key)));
        }

        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return criteria.addOrder(Order.desc("reference.id")).setFirstResult(offset).setMaxResults(limit).list();
    }

    public List<Reference> getByReferenceTypeFilterAndPerson(ReferenceType referenceType, Map<String, String> filterMap, Person person) {
        List<Reference> filteredReferences = new ArrayList<Reference>();

        Criteria criteria = session.createCriteria(Reference.class, "refInstance");
        criteria.add(eq("referenceType", referenceType));
        criteria.createAlias("refInstance.attributeReferenceInstances", "ari");
        criteria.createAlias("ari.attribute", "attribute");

        for (String key : filterMap.keySet()) {
            criteria.add(eq("attribute.name", key));
            criteria.add(eq("ari.value", filterMap.get(key)));
        }

        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        List<Reference> references = criteria.addOrder(Order.desc("refInstance.id")).list();
        List<Reference> personReferences = personHibernate.getReferences(person);

        for (Reference reference : references) {
            if (personReferences.contains(reference)) {
                filteredReferences.add(reference);
            }
        }

        return filteredReferences;
    }

    public List<Attribute> getAttributes(Reference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        ReferenceType referenceType = reference.getReferenceType();
        return referenceTypeHibernate.getAttributeValues(referenceType);
    }

    public void updateAttributeReferenceInstances(Reference reference, Map<String, String> newValues, List<Attribute> attributes) {
        for (String attributeId: newValues.keySet()) {
            Attribute attribute = attributeHibernate.getById(Long.valueOf(attributeId));

            String mapValue = newValues.get(String.valueOf(attribute.getId()));
            String value = mapValue == null ? "" : mapValue;

            Integer index = -1;
            for (int i = 0 ; i <attributes.size(); i++) {
                Attribute attribute1 = attributes.get(i);

                if (attribute1.getId().equals(attribute.getId())) {
                    index = i;
                }
            }

            boolean display = isDisplayAttribute(reference, attribute);

            setAttributeValueIndexDisplay(reference, attribute, value, index, display);
        }
    }

    public List<Attribute> getAttributeValues(Reference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<AttributeReferenceInstance> attributeReferenceInstances = reference.getAttributeReferenceInstances();
        List<Attribute> attributes = new ArrayList<Attribute>();

        for (AttributeReferenceInstance attributeReferenceInstance : attributeReferenceInstances) {
            attributes.add(attributeReferenceInstance.getAttribute());
        }

        return attributes;
    }

    public List<AttributeReferenceInstance> getSortedAttributeReferenceInstance(Reference reference) {
        if (reference == null ) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return sortAttributeReferenceInstances(reference);
    }

    public List<AttributeReferenceInstance> sortAttributeReferenceInstances(Reference reference) {
        List<AttributeReferenceInstance> attributeReferenceInstances = reference.getAttributeReferenceInstances();

        Collections.sort(attributeReferenceInstances, new Comparator<AttributeReferenceInstance>() {
            @Override
            public int compare(AttributeReferenceInstance o1, AttributeReferenceInstance o2) {
                return o1.getIndex().compareTo(o2.getIndex());
            }
        });

        return attributeReferenceInstances;
    }

    public AttributeReferenceInstance getOrCreateAttributeRefereneInstance(Reference reference, Attribute attribute) {
        if (reference == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        AttributeReferenceInstance attributeReferenceInstance;
        List<AttributeReferenceInstance> aris = session.createCriteria(AttributeReferenceInstance.class)
                .add(eq("reference", reference))
                .add(eq("attribute", attribute))
                .list();

        if (aris.size() > 0) {
            attributeReferenceInstance = aris.get(0);
        } else {
            attributeReferenceInstance = new AttributeReferenceInstance();
        }

        return attributeReferenceInstance;
    }

    public void setAttributeValue(Reference reference, Attribute attribute, String value) {
        if (reference == null || attribute == null || value == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        AttributeReferenceInstance attributeReferenceInstance = getOrCreateAttributeRefereneInstance(reference, attribute);

        setAttributeValue(attributeReferenceInstance, reference, value);
    }

    public void setAttributeValue(AttributeReferenceInstance attributeReferenceInstance, Reference reference, String value) {
        if (attributeReferenceInstance == null || reference == null || value == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Attribute attribute = attributeReferenceInstance.getAttribute();

        attributeReferenceInstance.setReference(reference);
        attributeReferenceInstance.setAttribute(attribute);
        attributeReferenceInstance.setValue(value);

        session.persist(attributeReferenceInstance);
    }

    public void setAttributeIndex(Reference reference, Attribute attribute, Integer index) {
        if (reference == null || attribute == null || index == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        AttributeReferenceInstance attributeReferenceInstance = getOrCreateAttributeRefereneInstance(reference, attribute);

        setAttributeIndex(attributeReferenceInstance, reference, index);
    }

    public void setAttributeIndex(AttributeReferenceInstance attributeReferenceInstance, Reference reference, Integer index) {
        if (attributeReferenceInstance == null || reference == null || index == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Attribute attribute = attributeReferenceInstance.getAttribute();

        attributeReferenceInstance.setReference(reference);
        attributeReferenceInstance.setAttribute(attribute);
        attributeReferenceInstance.setIndex(index);

        session.persist(attributeReferenceInstance);
    }

    public void setAttributeDisplay(Reference reference, Attribute attribute, boolean display) {
        if (reference == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        AttributeReferenceInstance attributeReferenceInstance = getOrCreateAttributeRefereneInstance(reference, attribute);

        setAttributeDisplay(attributeReferenceInstance, reference, display);
    }

    public void setAttributeDisplay(AttributeReferenceInstance attributeReferenceInstance, Reference reference, boolean display) {
        if (attributeReferenceInstance == null || reference == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Attribute attribute = attributeReferenceInstance.getAttribute();

        attributeReferenceInstance.setReference(reference);
        attributeReferenceInstance.setAttribute(attribute);
        attributeReferenceInstance.setDisplay(display);

        session.persist(attributeReferenceInstance);
    }

    @CommitAfter
    public void setAttributeValueIndexDisplay(Reference reference, Attribute attribute, String value, Integer index, boolean display) {
        if (reference == null || attribute == null || value == null || index == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        AttributeReferenceInstance attributeReferenceInstance = getOrCreateAttributeRefereneInstance(reference, attribute);

        attributeReferenceInstance.setReference(reference);
        attributeReferenceInstance.setAttribute(attribute);
        attributeReferenceInstance.setValue(value);
        attributeReferenceInstance.setIndex(index);
        attributeReferenceInstance.setDisplay(display);

        session.persist(attributeReferenceInstance);
    }

    public void deleteAttribute(Reference reference, Attribute attribute) {
        if (reference == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(AttributeReferenceInstance.class);
        List<AttributeReferenceInstance> entities = criteria
                .add(eq("reference", reference))
                .add(eq("attribute", attribute))
                .list();

        if (entities.size() < 1) {
            throw new IllegalStateException("No data in database.");
        }

        AttributeReferenceInstance attributeReferenceInstance = entities.get(0);
        attributeReferenceInstance.setReference(null);
        reference.getAttributeReferenceInstances().remove(attributeReferenceInstance);
    }
}
