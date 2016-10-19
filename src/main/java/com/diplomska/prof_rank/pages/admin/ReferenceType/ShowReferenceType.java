package com.diplomska.prof_rank.pages.admin.ReferenceType;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.AttributeHibernate;
import com.diplomska.prof_rank.services.ReferenceTypeHibernate;
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
public class ShowReferenceType {
    @Persist
    @Property
    private Long referenceTypeId;

    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    private AttributeHibernate attributeHibernate;

    @Persist
    @Property
    private ReferenceType referenceType;

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
        attributeReferences.addAll(referenceTypeHibernate.getAttributeValues(referenceType));

        return attributeReferences;
    }

    public List<RulebookSection> getRulebookSections() {
        List<ReferenceTypeRulebookSection> referenceTypeRulebookSections = new ArrayList<ReferenceTypeRulebookSection>();
        List<RulebookSection> rulebookSections = new ArrayList<RulebookSection>();
        referenceTypeRulebookSections.addAll(referenceType.getReferenceTypeRulebookSections());

        for( ReferenceTypeRulebookSection rtrs : referenceTypeRulebookSections) {
            rulebookSections.add(rtrs.getRulebookSection());
        }

        return rulebookSections;
    }

    public ReferenceInputTemplate getReferenceInputTemplate() {
        return referenceType.getReferenceInputTemplate();
    }

    void onActivate(Long referenceTypeId) {
        this.referenceTypeId = referenceTypeId;
    }

    Long passivate() {
        return referenceTypeId;
    }

    void setupRender() throws Exception {
        this.referenceType = referenceTypeHibernate.getById(referenceTypeId);

        if (referenceType == null) {
            throw new Exception("ReferenceType " + referenceTypeId + " does not exist.");
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
        return referenceType.getReferenceInputTemplate() == null ? false : true;
    }

//
//    @CommitAfter
//    void onActionFromAdd(Long attributeId) {
//        attribute = attributeHibernate.getById(attributeId);
//
//        referenceTypeHibernate.setAttributeValue(referenceType, attribute);
//    }

    @CommitAfter
    void onActionFromDelete(Long attributeId) {
        Attribute attr = attributeHibernate.getById(attributeId);

        referenceTypeHibernate.deleteAttribute(referenceType, attr);
    }
}
