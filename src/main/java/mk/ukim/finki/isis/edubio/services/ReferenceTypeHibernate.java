package mk.ukim.finki.isis.edubio.services;

import mk.ukim.finki.isis.edubio.entities.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import java.util.*;

import static org.hibernate.criterion.Restrictions.eq;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class ReferenceTypeHibernate {
    @Inject
    Session session;

    @Inject
    RulebookHibernate rulebookHibernate;

    @Inject
    ReferenceInputTemplateHibernate referenceInputTemplateHibernate;

    @Inject
    PersonHibernate personHibernate;

    public void store(ReferenceType referenceType) {
        if (referenceType == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.save(referenceType);
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
        List<Reference> allReferences = session.createCriteria(Reference.class).list();

        return getSortedReferenceTypes(allReferences, limit);
    }

    public List<ReferenceType> getPopularByPerson(Person person, Integer limit) {
        List<Reference> allReferences = personHibernate.getReferences(person);

        return getSortedReferenceTypes(allReferences, limit);
    }

    public List<ReferenceType> getPopularByPerson(Person person, Integer limit, List<Section> sections) {
        List<Reference> allReferences = personHibernate.getReferences(person);
        List<Reference> references = new ArrayList<Reference>();

        for (Reference reference : allReferences) {
            List<Section> refSections = getSections(reference.getReferenceType());

            for (Section section : sections) {
                for(Section refSection: refSections) {
                    if (refSection.getId().equals(section.getId())) {
                        references.add(reference);
                    }
                }
            }
        }

        return getSortedReferenceTypes(references, limit);
    }

    public List<ReferenceType> getPopularByPerson(Person person, Integer limit, List<Section> sections, String referenceTypeName) {
        List<Reference> allReferences = personHibernate.getReferences(person);
        List<Reference> references = new ArrayList<Reference>();

        for (Reference reference : allReferences) {
            List<Section> refSections = getSections(reference.getReferenceType());

            for (Section section : sections) {
                for(Section refSection: refSections) {
                    if (refSection.getId().equals(section.getId())
                            && reference.getReferenceType().getName().startsWith(referenceTypeName)) {
                        references.add(reference);
                    }
                }
            }
        }

        return getSortedReferenceTypes(references, limit);
    }

    private List<ReferenceType> getSortedReferenceTypes(List<Reference> allReferences, Integer limit) {
        List<ReferenceType> sortedReferenceTypes = new ArrayList<ReferenceType>();

        Map<ReferenceType, Integer> unsortedReferenceMap = new HashMap<ReferenceType, Integer>();

        for (Reference reference : allReferences) {
            ReferenceType referenceType = reference.getReferenceType();

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

    public void update(ReferenceType referenceType) {
        if (referenceType == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.update(referenceType);
    }

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

    public void setSection(ReferenceType referenceType, RulebookSection rulebookSection) {
        if (referenceType == null || rulebookSection == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        ReferenceTypeRulebookSection referenceTypeRulebookSection = new ReferenceTypeRulebookSection();

        referenceTypeRulebookSection.setRulebookSection(rulebookSection);
        referenceTypeRulebookSection.setReferenceType(referenceType);

        session.persist(referenceTypeRulebookSection);
    }

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
            setAttributeReferenceType(referenceType, attribute);
        }
    }

    public void setAttributeReferenceType(ReferenceType referenceType, Attribute attribute) {
        AttributeReferenceType attributeReferenceType = new AttributeReferenceType();

        attributeReferenceType.setAttribute(attribute);
        attributeReferenceType.setReferenceType(referenceType);

        session.persist(attributeReferenceType);

        List<AttributeReferenceType> attributeReferenceTypes = referenceType.getAttributeReferenceTypes();
        attributeReferenceTypes.add(attributeReferenceType);

        referenceType.setAttributeReferenceTypes(attributeReferenceTypes);
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

        List<AttributeReferenceType> attributeReferenceTypes = referenceType.getAttributeReferenceTypes();
        List<Attribute> attributes = new ArrayList<Attribute>();

        for (AttributeReferenceType attributeReferenceType : attributeReferenceTypes) {
            attributes.add(attributeReferenceType.getAttribute());
        }

        return attributes;
    }

    public AttributeReferenceType getOrCreateAttributeReferenceType(ReferenceType referenceType, Attribute attribute) {
        if (referenceType == null || attribute == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        AttributeReferenceType attributeReferenceType;
        List<AttributeReferenceType> attributeReferenceTypes = session.createCriteria(AttributeReferenceType.class)
                .add(eq("referenceType", referenceType))
                .add(eq("attribute", attribute))
                .list();

        if (attributeReferenceTypes.size() > 0) {
            attributeReferenceType = attributeReferenceTypes.get(0);
        } else {
            attributeReferenceType = new AttributeReferenceType();
        }

        return attributeReferenceType;
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

        AttributeReferenceType attributeReferenceType = getOrCreateAttributeReferenceType(referenceType, attribute);

        attributeReferenceType.setReferenceType(referenceType);
        attributeReferenceType.setAttribute(attribute);
        attributeReferenceType.setDisplay(display);

        session.save(attributeReferenceType);
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
