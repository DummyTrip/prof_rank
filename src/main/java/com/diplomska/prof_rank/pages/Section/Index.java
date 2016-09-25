package com.diplomska.prof_rank.pages.Section;

import com.diplomska.prof_rank.entities.RulebookSection;
import com.diplomska.prof_rank.entities.Section;
import com.diplomska.prof_rank.services.SectionHibernate;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class Index {
    @Inject
    private SectionHibernate sectionHibernate;

    @Property
    private RulebookSection rulebookSection;

    @Property
    private BeanModel<RulebookSection> sectionBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<RulebookSection> getRulebookSections() {
        return sectionHibernate.getAllRulebookSection();
    }

    void setupRender() {
        sectionBeanModel = beanModelSource.createDisplayModel(RulebookSection.class, messages);
//        sectionBeanModel.add("rulebookName", pcs.create(RulebookSection.class, "rulebook.name"));
        sectionBeanModel.add("sectionName", pcs.create(RulebookSection.class, "section.name"));
        sectionBeanModel.add("show", null);
        sectionBeanModel.add("edit", null);
        sectionBeanModel.add("delete", null);
    }

    @CommitAfter
    void onActionFromDelete(Long sectionId) {
        Section entity = sectionHibernate.getById(sectionId);
        sectionHibernate.delete(entity);
    }
}
