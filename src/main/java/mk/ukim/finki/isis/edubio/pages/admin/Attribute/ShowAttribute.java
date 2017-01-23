package mk.ukim.finki.isis.edubio.pages.admin.Attribute;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.services.AttributeHibernate;
import mk.ukim.finki.isis.edubio.entities.Attribute;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by Aleksandar on 25-Sep-16.
 */
@AdministratorPage
public class ShowAttribute {
    @Persist
    @Property
    private Long attributeId;

    @Inject
    private AttributeHibernate attributeHibernate;

    @Persist
    @Property
    private Attribute attribute;

    void onActivate(Long attributeId) {
        this.attributeId = attributeId;
    }

    Long passivate() {
        return attributeId;
    }

    void setupRender() throws Exception {
        this.attribute = attributeHibernate.getById(attributeId);
    }
}
