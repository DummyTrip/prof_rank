
package mk.ukim.finki.isis.edubio.pages.admin.Attribute;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.Attribute;
import mk.ukim.finki.isis.edubio.services.AttributeHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
@AdministratorPage
public class CreateAttribute {
    @Property
    private Attribute attribute;

    @Inject
    private AttributeHibernate attributeHibernate;

    @InjectPage
    private Index index;

    @InjectComponent
    private Form form;

    void onPrepareForRender() throws Exception {
        // If fresh start, make sure there's a Person object available.
        if (form.isValid()) {
            attribute = new Attribute();
        }
    }

    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        attribute = new Attribute();
    }

    @CommitAfter
    Object onSuccess() {
        attributeHibernate.store(attribute);

        return index;
    }
}
