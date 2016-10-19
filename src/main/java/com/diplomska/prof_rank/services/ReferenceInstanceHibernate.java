package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Subqueries;
import org.w3c.dom.Attr;

import java.util.*;

import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.eqProperty;
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
    ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    ReferenceHibernate referenceHibernate;

    @Inject
    AttributeHibernate attributeHibernate;

    @Inject
    PersonHibernate personHibernate;

    @CommitAfter
    public void store(ReferenceInstance referenceInstance) {
        if (referenceInstance == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.save(referenceInstance);
    }

    public List<ReferenceInstance> getAll() {
        return session.createCriteria(ReferenceInstance.class).addOrder(Order.desc("id")).list();
    }

    public List<ReferenceInstance> getAll(Integer offset, Integer limit) {
        return session.createCriteria(ReferenceInstance.class).addOrder(Order.desc("id")).setFirstResult(offset).setMaxResults(limit).list();
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

    public List<String> getAllDisplayNames() {
        List<String> displayNames = new ArrayList<String>();
        List<ReferenceInstance> referenceInstances = getAll();

        for (ReferenceInstance referenceInstance : referenceInstances) {
            displayNames.add(getDisplayName(referenceInstance));
        }

        return displayNames;
    }

    public String getDisplayName(ReferenceInstance referenceInstance) {
        String displayName = "";
        List<AttributeReferenceInstance> attributeReferenceInstances = getSortedAttributeReferenceInstance(referenceInstance);
        List<Attribute> attributes = getAttributeValues(referenceInstance);

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

    public Integer getReferenceInstanceAuthorNum(ReferenceInstance referenceInstance, Person person) {
        if (person == null || referenceInstance == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(ReferenceInstancePerson.class);
        criteria.add(eq("person", person))
                .add(eq("referenceInstance", referenceInstance));

        List<ReferenceInstancePerson> rips = criteria.list();

        if (rips.size() > 0) {
            ReferenceInstancePerson rip = (ReferenceInstancePerson) criteria.list().get(0);

            return rip.getAuthorNum();
        } else {
            return -1;
        }
    }

    public Integer getReferenceInstanceAuthorNum(ReferenceInstance referenceInstance, String author) {
        if (author == null || referenceInstance == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(ReferenceInstancePerson.class);
        criteria.add(eq("author", author))
                .add(eq("referenceInstance", referenceInstance));

        List<ReferenceInstancePerson> rips = criteria.list();

        if (rips.size() > 0) {
            ReferenceInstancePerson rip = (ReferenceInstancePerson) criteria.list().get(0);

            return rip.getAuthorNum();
        } else {
            return -1;
        }
    }

    public boolean isDisplayAttribute(ReferenceInstance referenceInstance, Attribute attribute) {
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
        return session.createCriteria(ReferenceInstance.class)
                .add(eq("reference", reference)).addOrder(Order.desc("id")).list();
    }

    public List<ReferenceInstance> getByReference(Reference reference, Integer offset, Integer limit) {
        return session.createCriteria(ReferenceInstance.class)
                .add(eq("reference", reference)).addOrder(Order.desc("id")).setFirstResult(offset).setMaxResults(limit).list();
    }

    public List<ReferenceInstance> getByReferenceAndPerson(Reference reference, Person person) {
        List<ReferenceInstance> referenceInstances = personHibernate.getReferenceInstances(person);
        List<ReferenceInstance> referenceInstancesOfSpecificReference = new ArrayList<ReferenceInstance>();

        for (ReferenceInstance referenceInstance : referenceInstances) {
            if (referenceInstance.getReference().getId().equals(reference.getId())) {
                referenceInstancesOfSpecificReference.add(referenceInstance);
            }
        }

        return referenceInstancesOfSpecificReference;
    }

    public List<ReferenceInstance> getByReferenceAndPerson(Reference reference, Person person, Integer offset, Integer limit) {
        List<ReferenceInstance> referenceInstances = personHibernate.getReferenceInstances(person);
        List<ReferenceInstance> referenceInstancesOfSpecificReference = new ArrayList<ReferenceInstance>();

        limit = limit + offset > referenceInstances.size() ? referenceInstances.size() : limit + offset;

        for (ReferenceInstance referenceInstance : referenceInstances) {
            if (referenceInstance.getReference().getId().equals(reference.getId())) {
                referenceInstancesOfSpecificReference.add(referenceInstance);
            }
        }

        return referenceInstancesOfSpecificReference.subList(offset, offset + limit);
    }

    public List<ReferenceInstance> getByReferenceAndFilter(Reference reference, Map<String, String> filterMap, Integer offset, Integer limit) {
        Criteria criteria = session.createCriteria(ReferenceInstance.class, "referenceInstance");
        criteria.add(eq("reference", reference));
        criteria.createAlias("referenceInstance.attributeReferenceInstances", "ari");
        criteria.createAlias("ari.attribute", "attribute");

        for (String key : filterMap.keySet()) {
            criteria.add(eq("attribute.name", key));
            criteria.add(eq("ari.value", filterMap.get(key)));
        }

        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return criteria.addOrder(Order.desc("referenceInstance.id")).setFirstResult(offset).setMaxResults(limit).list();
    }

    public List<ReferenceInstance> getByReferenceFilterAndPerson(Reference reference, Map<String, String> filterMap, Person person) {
        List<ReferenceInstance> filteredReferenceInstances = new ArrayList<ReferenceInstance>();

        Criteria criteria = session.createCriteria(ReferenceInstance.class, "refInstance");
        criteria.add(eq("reference", reference));
        criteria.createAlias("refInstance.attributeReferenceInstances", "ari");
        criteria.createAlias("ari.attribute", "attribute");

        for (String key : filterMap.keySet()) {
            criteria.add(eq("attribute.name", key));
            criteria.add(eq("ari.value", filterMap.get(key)));
        }

        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        List<ReferenceInstance> referenceInstances = criteria.addOrder(Order.desc("refInstance.id")).list();
        List<ReferenceInstance> personReferenceInstances = personHibernate.getReferenceInstances(person);

        for (ReferenceInstance referenceInstance : referenceInstances) {
            if (personReferenceInstances.contains(referenceInstance)) {
                filteredReferenceInstances.add(referenceInstance);
            }
        }

        return filteredReferenceInstances;
    }

    public List<Attribute> getAttributes(ReferenceInstance referenceInstance) {
        if (referenceInstance == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Reference reference = referenceInstance.getReference();
        return referenceHibernate.getAttributeValues(reference);
    }

    public void updateAttributeReferenceInstances(ReferenceInstance referenceInstance, Map<String, String> newValues, List<Attribute> attributes) {
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

            boolean display = isDisplayAttribute(referenceInstance, attribute);

            setAttributeValueIndexDisplay(referenceInstance, attribute, value, index, display);
        }
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

    public List<AttributeReferenceInstance> getSortedAttributeReferenceInstance(ReferenceInstance referenceInstance) {
        if (referenceInstance == null ) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return sortAttributeReferenceInstances(referenceInstance);
    }

    public List<AttributeReferenceInstance> sortAttributeReferenceInstances(ReferenceInstance referenceInstance) {
        List<AttributeReferenceInstance> attributeReferenceInstances = referenceInstance.getAttributeReferenceInstances();

        Collections.sort(attributeReferenceInstances, new Comparator<AttributeReferenceInstance>() {
            @Override
            public int compare(AttributeReferenceInstance o1, AttributeReferenceInstance o2) {
                return o1.getIndex().compareTo(o2.getIndex());
            }
        });

        return attributeReferenceInstances;
    }

    public AttributeReferenceInstance getOrCreateAttributeRefereneInstance(ReferenceInstance referenceInstance, Attribute attribute) {
        if (referenceInstance == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        AttributeReferenceInstance attributeReferenceInstance;
        List<AttributeReferenceInstance> aris = session.createCriteria(AttributeReferenceInstance.class)
                .add(eq("referenceInstance", referenceInstance))
                .add(eq("attribute", attribute))
                .list();

        if (aris.size() > 0) {
            attributeReferenceInstance = aris.get(0);
        } else {
            attributeReferenceInstance = new AttributeReferenceInstance();
        }

        return attributeReferenceInstance;
    }

    public void setAttributeValue(ReferenceInstance referenceInstance, Attribute attribute, String value) {
        if (referenceInstance == null || attribute == null || value == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        AttributeReferenceInstance attributeReferenceInstance = getOrCreateAttributeRefereneInstance(referenceInstance, attribute);

        setAttributeValue(attributeReferenceInstance, referenceInstance, value);
    }

    public void setAttributeValue(AttributeReferenceInstance attributeReferenceInstance, ReferenceInstance referenceInstance, String value) {
        if (attributeReferenceInstance == null || referenceInstance == null || value == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Attribute attribute = attributeReferenceInstance.getAttribute();

        attributeReferenceInstance.setReferenceInstance(referenceInstance);
        attributeReferenceInstance.setAttribute(attribute);
        attributeReferenceInstance.setValue(value);

        session.persist(attributeReferenceInstance);
    }

    public void setAttributeIndex(ReferenceInstance referenceInstance, Attribute attribute, Integer index) {
        if (referenceInstance == null || attribute == null || index == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        AttributeReferenceInstance attributeReferenceInstance = getOrCreateAttributeRefereneInstance(referenceInstance, attribute);

        setAttributeIndex(attributeReferenceInstance, referenceInstance, index);
    }

    public void setAttributeIndex(AttributeReferenceInstance attributeReferenceInstance, ReferenceInstance referenceInstance, Integer index) {
        if (attributeReferenceInstance == null || referenceInstance == null || index == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Attribute attribute = attributeReferenceInstance.getAttribute();

        attributeReferenceInstance.setReferenceInstance(referenceInstance);
        attributeReferenceInstance.setAttribute(attribute);
        attributeReferenceInstance.setIndex(index);

        session.persist(attributeReferenceInstance);
    }

    public void setAttributeDisplay(ReferenceInstance referenceInstance, Attribute attribute, boolean display) {
        if (referenceInstance == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        AttributeReferenceInstance attributeReferenceInstance = getOrCreateAttributeRefereneInstance(referenceInstance, attribute);

        setAttributeDisplay(attributeReferenceInstance, referenceInstance, display);
    }

    public void setAttributeDisplay(AttributeReferenceInstance attributeReferenceInstance, ReferenceInstance referenceInstance, boolean display) {
        if (attributeReferenceInstance == null || referenceInstance == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Attribute attribute = attributeReferenceInstance.getAttribute();

        attributeReferenceInstance.setReferenceInstance(referenceInstance);
        attributeReferenceInstance.setAttribute(attribute);
        attributeReferenceInstance.setDisplay(display);

        session.persist(attributeReferenceInstance);
    }

    @CommitAfter
    public void setAttributeValueIndexDisplay(ReferenceInstance referenceInstance, Attribute attribute, String value, Integer index, boolean display) {
        if (referenceInstance == null || attribute == null || value == null || index == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        AttributeReferenceInstance attributeReferenceInstance = getOrCreateAttributeRefereneInstance(referenceInstance, attribute);

        attributeReferenceInstance.setReferenceInstance(referenceInstance);
        attributeReferenceInstance.setAttribute(attribute);
        attributeReferenceInstance.setValue(value);
        attributeReferenceInstance.setIndex(index);
        attributeReferenceInstance.setDisplay(display);

        session.persist(attributeReferenceInstance);
    }

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
