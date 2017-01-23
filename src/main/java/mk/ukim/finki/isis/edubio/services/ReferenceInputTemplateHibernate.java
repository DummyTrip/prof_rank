package mk.ukim.finki.isis.edubio.services;

import mk.ukim.finki.isis.edubio.entities.Attribute;
import mk.ukim.finki.isis.edubio.entities.AttributeReferenceInputTemplate;
import mk.ukim.finki.isis.edubio.entities.ReferenceInputTemplate;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class ReferenceInputTemplateHibernate {
    @Inject
    Session session;

    public void store(ReferenceInputTemplate referenceInputTemplate) {
        if (referenceInputTemplate == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(referenceInputTemplate);
    }

    public List<ReferenceInputTemplate> getAll() {
        return session.createCriteria(ReferenceInputTemplate.class).list();
    }

    public void update(ReferenceInputTemplate referenceInputTemplate) {
        if (referenceInputTemplate == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.update(referenceInputTemplate);
    }

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

        List<AttributeReferenceInputTemplate> attributeReferenceInputTemplates = referenceInputTemplate.getAttributeReferenceInputTemplates();
        List<Attribute> attributes = new ArrayList<Attribute>();

        for (AttributeReferenceInputTemplate attributeReferenceInputTemplate : attributeReferenceInputTemplates) {
            attributes.add(attributeReferenceInputTemplate.getAttribute());
        }

        return attributes;
    }

    public void setAttribute(ReferenceInputTemplate referenceInputTemplate, Attribute attribute) {
        if (referenceInputTemplate == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        AttributeReferenceInputTemplate attributeReferenceInputTemplate = new AttributeReferenceInputTemplate();

        attributeReferenceInputTemplate.setReferenceInputTemplate(referenceInputTemplate);
        attributeReferenceInputTemplate.setAttribute(attribute);

        session.saveOrUpdate(attributeReferenceInputTemplate);
    }

    public void deleteAttribute(ReferenceInputTemplate referenceInputTemplate, Attribute attribute) {
        if (referenceInputTemplate == null || attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(AttributeReferenceInputTemplate.class);
        List<AttributeReferenceInputTemplate> entities = criteria
                .add(eq("referenceInputTemplate", referenceInputTemplate))
                .add(eq("attribute", attribute))
                .list();

        if (entities.size() < 1) {
            throw new IllegalStateException("No data in database.");
        }

        AttributeReferenceInputTemplate attributeReferenceInputTemplate = entities.get(0);
        attributeReferenceInputTemplate.setReferenceInputTemplate(null);
        referenceInputTemplate.getAttributeReferenceInputTemplates().remove(attributeReferenceInputTemplate);
    }
}
