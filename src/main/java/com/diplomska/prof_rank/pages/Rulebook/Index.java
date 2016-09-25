package com.diplomska.prof_rank.pages.Rulebook;

import com.diplomska.prof_rank.entities.Rulebook;
import com.diplomska.prof_rank.services.RulebookHibernate;
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
    private RulebookHibernate rulebookHibernate;

    @Property
    private Rulebook rulebook;

    @Property
    private BeanModel<Rulebook> rulebookBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<Rulebook> getRulebooks() {
        return rulebookHibernate.getAll();
    }

    void setupRender() {
        rulebookBeanModel = beanModelSource.createDisplayModel(Rulebook.class, messages);
        rulebookBeanModel.include("name");
        rulebookBeanModel.add("show", null);
        rulebookBeanModel.add("edit", null);
        rulebookBeanModel.add("delete", null);
    }

    @CommitAfter
    void onActionFromDelete(Long rulebookId) {
        rulebook = rulebookHibernate.getById(rulebookId);
        rulebookHibernate.delete(rulebook);
    }
}
