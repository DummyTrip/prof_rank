package mk.ukim.finki.isis.edubio.services;

import mk.ukim.finki.isis.edubio.entities.Attribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class AttributeHibernate {
    @Inject
    Session session;

    public void store(Attribute attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.save(attribute);
    }

    public List<Attribute> getAll() {
        return session.createCriteria(Attribute.class).list();
    }

    public void update(Attribute attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.update(attribute);
    }

    public void delete(Attribute attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.delete(attribute);
    }

    public Attribute getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return (Attribute) session.get(Attribute.class, id);
    }

    public List<Attribute> getByColumn(String column, String value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(Attribute.class);
        List<Attribute> entities = criteria.add(eq(column, value)).list();

        return entities;
    }

    public Attribute getOrCreateAttribute(String attributeName) {
        if (attributeName == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<Attribute> attributes = getByColumn("name", attributeName);
        Attribute attribute;

        if (attributes.size() > 0) {
            attribute = attributes.get(0);
        } else {
            attribute = new Attribute();
            attribute.setName(attributeName);
            attribute.setInputType("text");

            store(attribute);
        }

        return attribute;
    }
}
