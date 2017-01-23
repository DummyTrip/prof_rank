package mk.ukim.finki.isis.edubio.pages.admin.Section;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.ReferenceType;
import mk.ukim.finki.isis.edubio.entities.Rulebook;
import mk.ukim.finki.isis.edubio.entities.RulebookSection;
import mk.ukim.finki.isis.edubio.services.ReferenceTypeHibernate;
import mk.ukim.finki.isis.edubio.services.RulebookHibernate;
import mk.ukim.finki.isis.edubio.services.SectionHibernate;
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
public class ShowSection {
    @Persist
    @Property
    private Long rulebookSectionId;

    @Inject
    private SectionHibernate sectionHibernate;

    @Inject
    private RulebookHibernate rulebookHibernate;

    @Persist
    @Property
    private RulebookSection rulebookSection;

    @Property
    private ReferenceType referenceType;

    @Property
    private ReferenceType addReferenceType;

    @Property
    private Rulebook addRulebook;

    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

    @Property
    private BeanModel<ReferenceType> referenceTypeBeanModel;

    @Property
    private BeanModel<ReferenceType> addReferenceTypeBeanModel;

    @Property
    private BeanModel<Rulebook> addRulebookBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<ReferenceType> getReferenceTypes() {
        return sectionHibernate.getReferenceTypes(rulebookSection.getSection());
    }

    public List<ReferenceType> getAddReferenceTypes() {
        return referenceTypeHibernate.getAll();
    }

    public List<Rulebook> getAddRulebooks() {
        return rulebookHibernate.getAll();
    }

    public Rulebook getRulebook() {
        return rulebookSection.getRulebook();
    }

    void onActivate(Long rulebookSectionId) {
        this.rulebookSectionId = rulebookSectionId;
    }

    Long passivate() {
        return rulebookSectionId;
    }

    void setupRender() throws Exception {
        this.rulebookSection = sectionHibernate.getRulebookSectionById(rulebookSectionId);

        if (rulebookSection == null) {
            throw new Exception("Report " + rulebookSectionId + " does not exist.");
        }

        referenceTypeBeanModel = beanModelSource.createDisplayModel(ReferenceType.class, messages);
        referenceTypeBeanModel.add("delete", null);

        addReferenceTypeBeanModel = beanModelSource.createDisplayModel(ReferenceType.class, messages);
        addReferenceTypeBeanModel.add("add", null);

        addRulebookBeanModel = beanModelSource.createDisplayModel(Rulebook.class, messages);
        addRulebookBeanModel.add("add", null);
    }

    public boolean isRulebookNull() {
        return rulebookSection.getRulebook() == null ? false : true;
//        return false;
    }

    @CommitAfter
    void onActionFromAddReferenceType(Long id) {
        ReferenceType entity = referenceTypeHibernate.getById(id);
        rulebookSection = sectionHibernate.getRulebookSectionById(rulebookSectionId);

        referenceTypeHibernate.setSection(entity, rulebookSection);
    }

    @CommitAfter
    void onActionFromAddRulebook(Long id) {
        Rulebook entity = rulebookHibernate.getById(id);
        rulebookSection = sectionHibernate.getRulebookSectionById(rulebookSectionId);

        rulebookHibernate.setSection(entity, rulebookSection.getSection(), rulebookSection);
    }

    @CommitAfter
    void onActionFromDelete(Long id) {
        ReferenceType entity = referenceTypeHibernate.getById(id);

        referenceTypeHibernate.deleteSection(entity, rulebookSection);
    }
}
