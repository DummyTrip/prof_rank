package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.PersistentObjectException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import java.util.*;

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

    @Inject
    UserHibernate userHibernate;

    @Inject
    PersonHibernate personHibernate;

    @CommitAfter
    public void store(Reference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(reference);
    }

    public List<Reference> getAll() {
        return session.createCriteria(Reference.class).addOrder(Order.desc("id")).list();
    }

    public List<Reference> getAll(Integer offset, Integer limit) {
        return session.createCriteria(Reference.class).addOrder(Order.desc("id")).setFirstResult(offset).setMaxResults(limit).list();
    }

    public List<String> getAllNames() {
        List<Reference> references = getAll();
        List<String> referenceNames = new ArrayList<String>();

        for (Reference reference : references) {
            referenceNames.add(reference.getName());
        }

        return referenceNames;
    }

    public List<Reference> getPopular(Integer limit) {
        List<ReferenceInstance> allReferenceInstances = session.createCriteria(ReferenceInstance.class).list();

        return getSortedReferences(allReferenceInstances, limit);
    }

    public List<Reference> getPopularByUser(User user, Integer limit) {
        List<ReferenceInstance> allReferenceInstances = userHibernate.getReferenceInstances(user);

        return getSortedReferences(allReferenceInstances, limit);
    }

    public List<Reference> getPopularByPerson(Person person, Integer limit) {
        List<ReferenceInstance> allReferenceInstances = personHibernate.getReferenceInstances(person);

        return getSortedReferences(allReferenceInstances, limit);
    }

    private List<Reference> getSortedReferences(List<ReferenceInstance> allReferenceInstances, Integer limit) {
        List<Reference> sortedReferences = new ArrayList<Reference>();

        Map<Reference, Integer> unsortedReferenceMap = new HashMap<Reference, Integer>();

        for (ReferenceInstance referenceInstance : allReferenceInstances) {
            Reference reference = referenceInstance.getReference();

            if (!unsortedReferenceMap.containsKey(reference)) {
                unsortedReferenceMap.put(reference, 0);
            }

            unsortedReferenceMap.put(reference, unsortedReferenceMap.get(reference) + 1);
        }

        Map<Reference, Integer> sortedReferencesMap = sortMapByValue(unsortedReferenceMap);

        int i = 0;
        for (Reference reference : sortedReferencesMap.keySet()) {
            if (i > limit) {
                break;
            }
            sortedReferences.add(reference);
            i+=1;
        }

        return sortedReferences;
    }

    private Map<Reference, Integer> sortMapByValue(Map<Reference, Integer> unsortedMap) {
        List<Map.Entry<Reference, Integer>> list =
                new LinkedList<Map.Entry<Reference, Integer>>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Reference, Integer>>() {
            public int compare(Map.Entry<Reference, Integer> o1,
                               Map.Entry<Reference, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<Reference, Integer> sortedMap = new LinkedHashMap<Reference, Integer>();
        for (Map.Entry<Reference, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
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

    public void setReferenceType(Reference reference, ReferenceType referenceType) {
        if (reference == null || reference == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        reference.setReferenceType(referenceType);
        session.saveOrUpdate(reference);

        List<Attribute> attributes = referenceTypeHibernate.getAttributes(referenceType);

        for (Attribute attribute : attributes) {
            setAttributeReference(reference, attribute);
        }
    }

    public void setAttributeReference(Reference reference, Attribute attribute) {
        AttributeReference attributeReference = new AttributeReference();

        attributeReference.setAttribute(attribute);
        attributeReference.setReference(reference);

        session.persist(attributeReference);

        List<AttributeReference> attributeReferences = reference.getAttributeReferences();
        attributeReferences.add(attributeReference);

        reference.setAttributeReferences(attributeReferences);
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

    public AttributeReference getOrCreateAttributeReference(Reference reference, Attribute attribute) {
        if (reference == null || attribute == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        AttributeReference attributeReference;
        List<AttributeReference> attributeReferences = session.createCriteria(AttributeReference.class)
                .add(eq("reference", reference))
                .add(eq("attribute", attribute))
                .list();

        if (attributeReferences.size() > 0) {
            attributeReference = attributeReferences.get(0);
        } else {
            attributeReference = new AttributeReference();
        }

        return attributeReference;
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

    public void setAttributeDisplay(Reference reference, Attribute attribute, boolean display) {
        if (reference == null || attribute == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        AttributeReference attributeReference = getOrCreateAttributeReference(reference, attribute);

        attributeReference.setReference(reference);
        attributeReference.setAttribute(attribute);
        attributeReference.setDisplay(display);

        session.persist(attributeReference);
    }

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
