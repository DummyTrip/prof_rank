package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
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
    ReferenceInputTemplateHibernate referenceInputTemplateHibernate;

    @Inject
    PersonHibernate personHibernate;

    @CommitAfter
    public void store(ReferenceType referenceType) {
        if (referenceType == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(referenceType);
    }

    public List<ReferenceType> getAll() {
        return session.createCriteria(ReferenceType.class).addOrder(Order.desc("id")).list();
    }

    public List<ReferenceType> getAll(Integer offset, Integer limit) {
        return session.createCriteria(ReferenceType.class).addOrder(Order.desc("id")).setFirstResult(offset).setMaxResults(limit).list();
    }

    public List<ReferenceType> getBySections(Integer offset, Integer limit, List<Section> sections) {
        List<ReferenceType> allReferenceTypes = session.createCriteria(ReferenceType.class).addOrder(Order.desc("id")).list();

        List<ReferenceType> referenceTypes = new ArrayList<ReferenceType>();

        for (ReferenceType referenceType : allReferenceTypes) {
            List<Section> refSections = getSections(referenceType);

            for (Section section : sections) {
                for(Section refSection: refSections) {
                    if (refSection.getId().equals(section.getId())) {
                        referenceTypes.add(referenceType);
                        break;
                    }
                }
            }

            if (referenceTypes.size() == (offset + limit)) {
                break;
            }
        }
        // TODO: refactor this code
        if (offset > referenceTypes.size()) {
            return new ArrayList<ReferenceType>();
        }

        limit = referenceTypes.size() > (limit + offset) ? (limit + offset) : referenceTypes.size();
        return referenceTypes.subList(offset, limit);
    }

    public List<String> getAllNames() {
        List<ReferenceType> referenceTypes = getAll();
        List<String> referenceTypeNames = new ArrayList<String>();

        for (ReferenceType referenceType : referenceTypes) {
            referenceTypeNames.add(referenceType.getName());
        }

        return referenceTypeNames;
    }

    public List<ReferenceType> getPopular(Integer limit) {
        List<ReferenceInstance> allReferenceInstances = session.createCriteria(ReferenceInstance.class).list();

        return getSortedReferenceTypes(allReferenceInstances, limit);
    }

    public List<ReferenceType> getPopularByPerson(Person person, Integer limit) {
        List<ReferenceInstance> allReferenceInstances = personHibernate.getReferenceInstances(person);

        return getSortedReferenceTypes(allReferenceInstances, limit);
    }

    public List<ReferenceType> getPopularByPerson(Person person, Integer limit, List<Section> sections) {
        List<ReferenceInstance> allReferenceInstances = personHibernate.getReferenceInstances(person);
        List<ReferenceInstance> references = new ArrayList<ReferenceInstance>();

        for (ReferenceInstance referenceInstance : allReferenceInstances ) {
            List<Section> refSections = getSections(referenceInstance.getReferenceType());

            for (Section section : sections) {
                for(Section refSection: refSections) {
                    if (refSection.getId().equals(section.getId())) {
                        references.add(referenceInstance);
                    }
                }
            }
        }

        return getSortedReferenceTypes(references, limit);
    }

    private List<ReferenceType> getSortedReferenceTypes(List<ReferenceInstance> allReferenceInstances, Integer limit) {
        List<ReferenceType> sortedReferenceTypes = new ArrayList<ReferenceType>();

        Map<ReferenceType, Integer> unsortedReferenceMap = new HashMap<ReferenceType, Integer>();

        for (ReferenceInstance referenceInstance : allReferenceInstances) {
            ReferenceType referenceType = referenceInstance.getReferenceType();

            if (!unsortedReferenceMap.containsKey(referenceType)) {
                unsortedReferenceMap.put(referenceType, 0);
            }

            unsortedReferenceMap.put(referenceType, unsortedReferenceMap.get(referenceType) + 1);
        }

        Map<ReferenceType, Integer> sortedReferencesMap = sortMapByValue(unsortedReferenceMap);

        int i = 0;
        for (ReferenceType referenceType : sortedReferencesMap.keySet()) {
            if (i > limit) {
                break;
            }
            sortedReferenceTypes.add(referenceType);
            i+=1;
        }

        return sortedReferenceTypes;
    }

    private Map<ReferenceType, Integer> sortMapByValue(Map<ReferenceType, Integer> unsortedMap) {
        List<Map.Entry<ReferenceType, Integer>> list =
                new LinkedList<Map.Entry<ReferenceType, Integer>>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<ReferenceType, Integer>>() {
            public int compare(Map.Entry<ReferenceType, Integer> o1,
                               Map.Entry<ReferenceType, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<ReferenceType, Integer> sortedMap = new LinkedHashMap<ReferenceType, Integer>();
        for (Map.Entry<ReferenceType, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
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

    public List<Section> getSections(ReferenceType referenceType) {
        if (referenceType == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<ReferenceTypeRulebookSection> referenceTypeRulebookSections = referenceType.getReferenceTypeRulebookSections();
        List<RulebookSection> rulebookSections = new ArrayList<RulebookSection>();

        for (ReferenceTypeRulebookSection referenceTypeRulebookSection : referenceTypeRulebookSections) {
            rulebookSections.add(referenceTypeRulebookSection.getRulebookSection());
        }

        List<Section> sections = new ArrayList<Section>();

        for (RulebookSection rulebookSection : rulebookSections) {
            sections.add(rulebookSection.getSection());
        }

        return sections;
    }

    @CommitAfter
    public void setSection(ReferenceType referenceType, RulebookSection rulebookSection) {
        if (referenceType == null || rulebookSection == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        ReferenceTypeRulebookSection referenceTypeRulebookSection = new ReferenceTypeRulebookSection();

        referenceTypeRulebookSection.setRulebookSection(rulebookSection);
        referenceTypeRulebookSection.setReferenceType(referenceType);

        session.persist(referenceTypeRulebookSection);
    }

    @CommitAfter
    public void deleteSection(ReferenceType referenceType, RulebookSection rulebookSection) {
        if (referenceType == null || rulebookSection == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(ReferenceTypeRulebookSection.class);
        List<ReferenceTypeRulebookSection> entities = criteria
                .add(eq("referenceType", referenceType))
                .add(eq("rulebookSection", rulebookSection))
                .list();

        if (entities.size() < 1) {
            throw new IllegalStateException("No data in database.");
        }

        ReferenceTypeRulebookSection referenceTypeRulebookSection = entities.get(0);
        referenceTypeRulebookSection.setReferenceType(null);
        referenceType.getReferenceTypeRulebookSections().remove(referenceTypeRulebookSection);
    }

    public void setSection(ReferenceType referenceType, Section section, Rulebook rulebook) {
        if (referenceType == null || section == null || rulebook == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        RulebookSection rulebookSection = rulebookHibernate.getRulebookSection(rulebook, section);

        setSection(referenceType, rulebookSection);
    }

    public ReferenceInputTemplate getReferenceInputTemplate(ReferenceType referenceType) {
        if (referenceType == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return referenceType.getReferenceInputTemplate();
    }

    public void setReferenceInputTemplate(ReferenceType referenceType, ReferenceInputTemplate referenceInputTemplate) {
        if (referenceType == null || referenceInputTemplate == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        referenceType.setReferenceInputTemplate(referenceInputTemplate);
        session.saveOrUpdate(referenceType);

        List<Attribute> attributes = referenceInputTemplateHibernate.getAttributes(referenceInputTemplate);

        for (Attribute attribute : attributes) {
            setAttributeReference(referenceType, attribute);
        }
    }

    public void setAttributeReference(ReferenceType referenceType, Attribute attribute) {
        AttributeReference attributeReference = new AttributeReference();

        attributeReference.setAttribute(attribute);
        attributeReference.setReferenceType(referenceType);

        session.persist(attributeReference);

        List<AttributeReference> attributeReferences = referenceType.getAttributeReferences();
        attributeReferences.add(attributeReference);

        referenceType.setAttributeReferences(attributeReferences);
    }

    public List<Attribute> getAttributes(ReferenceType referenceType) {
        if (referenceType == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }
        ReferenceInputTemplate referenceInputTemplate = referenceType.getReferenceInputTemplate();
        List<Attribute> attributes = referenceInputTemplateHibernate.getAttributes(referenceInputTemplate);

        return attributes;
    }

    public List<Attribute> getAttributeValues(ReferenceType referenceType) {
        if (referenceType == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<AttributeReference> attributeReferences = referenceType.getAttributeReferences();
        List<Attribute> attributes = new ArrayList<Attribute>();

        for (AttributeReference attributeReference : attributeReferences) {
            attributes.add(attributeReference.getAttribute());
        }

        return attributes;
    }

    public AttributeReference getOrCreateAttributeReference(ReferenceType referenceType, Attribute attribute) {
        if (referenceType == null || attribute == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        AttributeReference attributeReference;
        List<AttributeReference> attributeReferences = session.createCriteria(AttributeReference.class)
                .add(eq("referenceType", referenceType))
                .add(eq("attribute", attribute))
                .list();

        if (attributeReferences.size() > 0) {
            attributeReference = attributeReferences.get(0);
        } else {
            attributeReference = new AttributeReference();
        }

        return attributeReference;
    }

    public boolean isDisplayAttribute(ReferenceType referenceType, Attribute attribute) {
        // TODO: change this method. Don't hardcode the display attributes.
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

    public void setAttributeDisplay(ReferenceType referenceType, Attribute attribute, boolean display) {
        if (referenceType == null || attribute == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        AttributeReference attributeReference = getOrCreateAttributeReference(referenceType, attribute);

        attributeReference.setReferenceType(referenceType);
        attributeReference.setAttribute(attribute);
        attributeReference.setDisplay(display);

        session.persist(attributeReference);
    }

    public void deleteAttribute(ReferenceType referenceType, Attribute attribute) {
        if (referenceType == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(AttributeReference.class);
        List<AttributeReference> entities = criteria
                .add(eq("referenceType", referenceType))
                .add(eq("attribute", attribute))
                .list();

        if (entities.size() < 1) {
            throw new IllegalStateException("No data in database.");
        }

        AttributeReference attributeReference = entities.get(0);
        attributeReference.setReferenceType(null);
        referenceType.getAttributeReferences().remove(attributeReference);
    }
}
