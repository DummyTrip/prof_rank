package mk.ukim.finki.isis.edubio.pages.admin.Attribute;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.Attribute;
import mk.ukim.finki.isis.edubio.services.AttributeHibernate;
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
    private AttributeHibernate attributeHibernate;

    @Property
    private Attribute attribute;

    @Property
    private BeanModel<Attribute> attributeBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<Attribute> getAttributes() {
        return attributeHibernate.getAll();
    }

    void setupRender() {
        attributeBeanModel = beanModelSource.createDisplayModel(Attribute.class, messages);
        attributeBeanModel.include("name", "inputType");
        attributeBeanModel.add("show", null);
        attributeBeanModel.add("edit", null);
        attributeBeanModel.add("delete", null);
    }

    @CommitAfter
    void onActionFromDelete(Long attributeId) {
        attribute = attributeHibernate.getById(attributeId);
        attributeHibernate.delete(attribute);
    }
}
