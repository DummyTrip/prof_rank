package mk.ukim.finki.isis.edubio.pages.admin.Reference;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.AttributeReference;
import mk.ukim.finki.isis.edubio.entities.Reference;
import mk.ukim.finki.isis.edubio.services.ReferenceHibernate;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import java.util.List;

/**
 * Created by Aleksandar on 28-Sep-16.
 */
@AdministratorPage
public class ShowReference {
    @Property
    @Persist
    private Long referenceId;

    @Inject
    ReferenceHibernate referenceHibernate;

    @Property
    Reference reference;

    @Property
    private AttributeReference attributeReference;

    @Property
    private BeanModel<AttributeReference> attributeReferenceBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<AttributeReference> getAttributeReferences() {
        return reference.getAttributeReferences();
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
            throw new Exception("ReferenceType " + referenceId + " does not exist.");
        }

        attributeReferenceBeanModel = beanModelSource.createDisplayModel(AttributeReference.class, messages);
        attributeReferenceBeanModel.include();
        attributeReferenceBeanModel.add("attributeReferenceName", pcs.create(AttributeReference.class, "attribute.name"));
        attributeReferenceBeanModel.add("attributeReferenceInputType", pcs.create(AttributeReference.class, "attribute.inputType"));
        attributeReferenceBeanModel.add("attributeReferenceVal", pcs.create(AttributeReference.class, "value"));
        attributeReferenceBeanModel.add("delete", null);
    }



    }
