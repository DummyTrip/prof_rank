package mk.ukim.finki.isis.edubio.services;

import mk.ukim.finki.isis.edubio.entities.Section;
import mk.ukim.finki.isis.edubio.entities.RulebookSection;
import mk.ukim.finki.isis.edubio.entities.Rulebook;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;


/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class RulebookHibernate {
    @Inject
    Session session;

    public void store(Rulebook rulebook) {
        if (rulebook == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(rulebook);
    }

    public List<Rulebook> getAll() {
        return session.createCriteria(Rulebook.class).list();
    }

    public void update(Rulebook rulebook) {
        if (rulebook == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.update(rulebook);
    }

    public void delete(Rulebook rulebook) {
        if (rulebook == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.delete(rulebook);
    }

    public Rulebook getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return (Rulebook) session.get(Rulebook.class, id);
    }

    public List<Rulebook> getByColumn(String column, String value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(Rulebook.class);
        List<Rulebook> entities = criteria.add(eq(column, value)).list();

        return entities;
    }

    public RulebookSection getRulebookSection(Rulebook rulebook, Section section) {
        if (rulebook == null || section == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(RulebookSection.class);
        List<RulebookSection> rulebookSections = criteria
                .add(eq("rulebook", rulebook))
                .add(eq("section", section))
                .list();

        if (rulebookSections.size() == 0) {
            throw new IllegalStateException("No data in database.");
        }

        return rulebookSections.get(0);
    }

    public List<Section> getSections(Rulebook rulebook) {
        if (rulebook == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<RulebookSection> rulebookSections = rulebook.getRulebookSections();
        List<Section> sections = new ArrayList<Section>();

        for (RulebookSection rulebookSection : rulebookSections) {
            sections.add(rulebookSection.getSection());
        }

        return sections;
    }


    public void setSection(Rulebook rulebook, Section section) {
        if (rulebook == null || section == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        RulebookSection rulebookSection = new RulebookSection();

        rulebookSection.setRulebook(rulebook);
        rulebookSection.setSection(section);

        session.saveOrUpdate(rulebookSection);
    }

    public void setSection(Rulebook rulebook, Section section, RulebookSection rulebookSection) {
        if (rulebook == null || section == null || rulebookSection == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        rulebookSection.setRulebook(rulebook);
        rulebookSection.setSection(section);

        session.update(rulebookSection);
    }

    public void deleteSection(Rulebook rulebook, Section section) {
        if (rulebook == null || section == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(RulebookSection.class);
        List<RulebookSection> entities = criteria
                .add(eq("rulebook", rulebook))
                .add(eq("section", section))
                .list();

        if (entities.size() < 1) {
            throw new IllegalStateException("No data in database.");
        }

        RulebookSection rulebookSection = entities.get(0);
        rulebookSection.setRulebook(null);
        rulebook.getRulebookSections().remove(rulebookSection);
    }
}
