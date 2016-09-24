package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.Section;
import com.diplomska.prof_rank.entities.RulebookSection;
import com.diplomska.prof_rank.entities.Rulebook;
import com.diplomska.prof_rank.entities.Section;
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
public class RulebookHibernate {
    @Inject
    Session session;

    @CommitAfter
    public void store(Rulebook rulebook) {
        if (rulebook == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(rulebook);
    }

    public List<Rulebook> getAll() {
        return session.createCriteria(Rulebook.class).list();
    }

    @CommitAfter
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
                .add(eq("rulebook_id", rulebook.getId()))
                .add(eq("section_id", section.getId()))
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


    @CommitAfter
    public void setSection(Rulebook rulebook, Section section) {
        if (rulebook == null || section == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        RulebookSection rulebookSection = new RulebookSection();

        rulebookSection.setRulebook(rulebook);
        rulebookSection.setSection(section);

        session.persist(rulebookSection);
    }
}
