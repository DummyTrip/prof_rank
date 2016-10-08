package com.diplomska.prof_rank.pages.admin.Reference;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.AttributeHibernate;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 25-Sep-16.
 */
@AdministratorPage
public class ShowReference {
    @Persist
    @Property
    private Long referenceId;

    @Inject
    private ReferenceHibernate referenceHibernate;

    @Inject
    private AttributeHibernate attributeHibernate;

    @Persist
    @Property
    private Reference reference;

    @Property
    private Attribute addAttribute;

    @Property
    private Attribute attribute;

    @Property
    private BeanModel<Attribute> attributeBeanModel;

    @Property
    private BeanModel<Attribute> addAttributeBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    @Property
    private RulebookSection rulebookSection;

    @Property
    private BeanModel<RulebookSection> rulebookSectionBeanModel;

    public List<Attribute> getAddAttributes() {
        return attributeHibernate.getAll();
    }

    public List<Attribute> getAttributes() {
        List<Attribute> attributeReferences = new ArrayList<Attribute>();
        attributeReferences.addAll(referenceHibernate.getAttributeValues(reference));

        return attributeReferences;
    }

    public List<RulebookSection> getRulebookSections() {
        List<ReferenceRulebookSection> referenceRulebookSections = new ArrayList<ReferenceRulebookSection>();
        List<RulebookSection> rulebookSections = new ArrayList<RulebookSection>();
        referenceRulebookSections.addAll(reference.getReferenceRulebookSections());

        for( ReferenceRulebookSection rrs : referenceRulebookSections) {
            rulebookSections.add(rrs.getRulebookSection());
        }

        return rulebookSections;
    }

    public ReferenceType getReferenceType() {
        return reference.getReferenceType();
    }

    void onActivate(Long referenceId) {
        this.referenceId = referenceId;
    }

    Long passivate() {
        return referenceId;
    }

    void setupRender() throws Exception {
        this.reference = referenceHibernate.getById(referenceId);

        if (reference == null) {
            throw new Exception("Reference " + referenceId + " does not exist.");
        }

        attributeBeanModel = beanModelSource.createDisplayModel(Attribute.class, messages);
        attributeBeanModel.add("attributeName", pcs.create(Attribute.class, "name"));
        attributeBeanModel.add("attributeInputType", pcs.create(Attribute.class, "inputType"));
        attributeBeanModel.add("delete", null);

        rulebookSectionBeanModel = beanModelSource.createDisplayModel(RulebookSection.class, messages);
        rulebookSectionBeanModel.add("rulebookName", pcs.create(RulebookSection.class, "rulebook.name"));
        rulebookSectionBeanModel.add("sectionName", pcs.create(RulebookSection.class, "section.name"));

//        addAttributeBeanModel = beanModelSource.createDisplayModel(Attribute.class, messages);
//        addAttributeBeanModel.include("name", "inputType");
//        addAttributeBeanModel.add("add", null);
    }

    public boolean isRefTypeNull() {
        return reference.getReferenceType() == null ? false : true;
    }

//
//    @CommitAfter
//    void onActionFromAdd(Long attributeId) {
//        attribute = attributeHibernate.getById(attributeId);
//
//        referenceHibernate.setAttributeValue(reference, attribute);
//    }

    @CommitAfter
    void onActionFromDelete(Long attributeId) {
        Attribute attr = attributeHibernate.getById(attributeId);

        referenceHibernate.deleteAttribute(reference, attr);
    }
}
