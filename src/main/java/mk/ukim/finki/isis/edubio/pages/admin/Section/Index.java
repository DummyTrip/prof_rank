package mk.ukim.finki.isis.edubio.pages.admin.Section;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.RulebookSection;
import mk.ukim.finki.isis.edubio.entities.Section;
import mk.ukim.finki.isis.edubio.services.RulebookHibernate;
import mk.ukim.finki.isis.edubio.services.SectionHibernate;
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
@AdministratorPage
public class Index {
    @Inject
    private SectionHibernate sectionHibernate;

    @Inject
    private RulebookHibernate rulebookHibernate;

    @Property
    private RulebookSection rulebookSection;

    @Property
    private Section section;

    @Property
    private BeanModel<RulebookSection> rulebookSectionBeanModel;

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

    public List<RulebookSection> getRulebookSections() {
        return sectionHibernate.getAllRulebookSection();
    }

    void setupRender() {
        sectionBeanModel = beanModelSource.createDisplayModel(Section.class, messages);
        sectionBeanModel.add("edit", null);
        sectionBeanModel.add("delete", null);

        rulebookSectionBeanModel = beanModelSource.createDisplayModel(RulebookSection.class, messages);
        rulebookSectionBeanModel.add("rulebookName", pcs.create(RulebookSection.class, "rulebook.name"));
        rulebookSectionBeanModel.add("sectionName", pcs.create(RulebookSection.class, "section.name"));
        rulebookSectionBeanModel.add("show", null);
        rulebookSectionBeanModel.add("delete", null);
    }

    @CommitAfter
    void onActionFromDelete(Long id) {
        RulebookSection entity = sectionHibernate.getRulebookSectionById(id);

        rulebookHibernate.deleteSection(entity.getRulebook(), entity.getSection());
    }

    @CommitAfter
    void onActionFromDeleteSection(Long sectionId) {
        Section entity = sectionHibernate.getById(sectionId);
        sectionHibernate.delete(entity);
    }
}
