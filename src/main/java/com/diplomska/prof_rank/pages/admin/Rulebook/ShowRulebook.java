package com.diplomska.prof_rank.pages.admin.Rulebook;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.entities.Rulebook;
import com.diplomska.prof_rank.entities.Section;
import com.diplomska.prof_rank.services.RulebookHibernate;
import com.diplomska.prof_rank.services.SectionHibernate;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import java.util.List;

/**
 * Created by Aleksandar on 25-Sep-16.
 */
@AdministratorPage
public class ShowRulebook {
    @Persist
    @Property
    private Long rulebookId;

    @Inject
    private RulebookHibernate rulebookHibernate;

    @Persist
    @Property
    private Rulebook rulebook;

    @Property
    private Section section;

    @Property
    private Section addSection;

    @Inject
    private SectionHibernate sectionHibernate;

    @Property
    private BeanModel<Section> sectionBeanModel;

    @Property
    private BeanModel<Section> addSectionBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<Section> getSections() {
        return rulebookHibernate.getSections(rulebook);
    }

    public List<Section> getAddSections() throws Exception {
        return sectionHibernate.getAll();
    }


    void onActivate(Long rulebookId) {
        this.rulebookId = rulebookId;
    }

    Long passivate() {
        return rulebookId;
    }

    void setupRender() throws Exception {
        this.rulebook = rulebookHibernate.getById(rulebookId);

        if (rulebook == null) {
            throw new Exception("Report " + rulebookId + " does not exist.");
        }

        sectionBeanModel = beanModelSource.createDisplayModel(Section.class, messages);
        sectionBeanModel.add("sectionName", pcs.create(Section.class, "name"));
        sectionBeanModel.add("delete", null);

        addSectionBeanModel = beanModelSource.createDisplayModel(Section.class, messages);
        addSectionBeanModel.add("sectionName", pcs.create(Section.class, "name"));
        addSectionBeanModel.add("add", null);
    }

    @CommitAfter
    void onActionFromAdd(Long id) {
        Section entity = sectionHibernate.getById(id);

        rulebookHibernate.setSection(rulebook, entity);
    }

    @CommitAfter
    void onActionFromDelete(Long id) {
        Section entity = sectionHibernate.getById(id);

        rulebookHibernate.deleteSection(rulebook, entity);
    }
}
