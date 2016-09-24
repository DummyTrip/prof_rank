package com.diplomska.prof_rank.pages.Section;

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
    private Section section;

    @Property
    private BeanModel<Section> sectionBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<Section> getSections() {
        return sectionHibernate.getAll();
    }

    void setupRender() {
        sectionBeanModel = beanModelSource.createDisplayModel(Section.class, messages);
        sectionBeanModel.include("name");
        sectionBeanModel.add("edit", null);
        sectionBeanModel.add("delete", null);
    }

    @CommitAfter
    void onActionFromDelete(Long sectionId) {
        section = sectionHibernate.getById(sectionId);
        sectionHibernate.delete(section);
    }
}
