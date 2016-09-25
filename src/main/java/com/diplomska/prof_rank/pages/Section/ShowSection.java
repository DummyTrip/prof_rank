package com.diplomska.prof_rank.pages.Section;

import com.diplomska.prof_rank.entities.Rulebook;
import com.diplomska.prof_rank.entities.RulebookSection;
import com.diplomska.prof_rank.entities.Section;
import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.services.RulebookHibernate;
import com.diplomska.prof_rank.services.SectionHibernate;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import org.apache.commons.codec.language.bm.Rule;
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
    private Reference reference;

    @Property
    private Reference addReference;

    @Property
    private Rulebook addRulebook;

    @Inject
    private ReferenceHibernate referenceHibernate;

    @Property
    private BeanModel<Reference> referenceBeanModel;

    @Property
    private BeanModel<Reference> addReferenceBeanModel;

    @Property
    private BeanModel<Rulebook> addRulebookBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<Reference> getReferences() {
        return sectionHibernate.getReferences(rulebookSection.getSection());
    }

    public List<Reference> getAddReferences() {
        return referenceHibernate.getAll();
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

        referenceBeanModel = beanModelSource.createDisplayModel(Reference.class, messages);
        referenceBeanModel.add("referenceName", pcs.create(Reference.class, "name"));
        referenceBeanModel.add("delete", null);

        addReferenceBeanModel = beanModelSource.createDisplayModel(Reference.class, messages);
        addReferenceBeanModel.add("referenceName", pcs.create(Reference.class, "name"));
        addReferenceBeanModel.add("add", null);

        addRulebookBeanModel = beanModelSource.createDisplayModel(Rulebook.class, messages);
        addRulebookBeanModel.add("rulebookName", pcs.create(Rulebook.class, "name"));
        addRulebookBeanModel.add("add", null);
    }

    public boolean isRulebookNull() {
        return rulebookSection.getRulebook() == null ? false : true;
//        return false;
    }

    @CommitAfter
    void onActionFromAddReference(Long id) {
        Reference entity = referenceHibernate.getById(id);
        rulebookSection = sectionHibernate.getRulebookSectionById(rulebookSectionId);

        referenceHibernate.setSection(entity, rulebookSection);
    }

    @CommitAfter
    void onActionFromAddRulebook(Long id) {
        Rulebook entity = rulebookHibernate.getById(id);
        rulebookSection = sectionHibernate.getRulebookSectionById(rulebookSectionId);

        rulebookHibernate.setSection(entity, rulebookSection.getSection(), rulebookSection);
    }

    @CommitAfter
    void onActionFromDelete(Long id) {
        Reference entity = referenceHibernate.getById(id);

        referenceHibernate.deleteSection(entity, rulebookSection);
    }
}
