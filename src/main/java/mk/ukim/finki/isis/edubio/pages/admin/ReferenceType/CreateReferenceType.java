package mk.ukim.finki.isis.edubio.pages.admin.ReferenceType;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.ReferenceInputTemplate;
import mk.ukim.finki.isis.edubio.entities.ReferenceType;
import mk.ukim.finki.isis.edubio.services.ReferenceInputTemplateHibernate;
import mk.ukim.finki.isis.edubio.services.ReferenceTypeHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
@AdministratorPage
public class CreateReferenceType {
    @Property
    private ReferenceType referenceType;

    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    private ReferenceInputTemplateHibernate referenceInputTemplateHibernate;

    @InjectPage
    private Index index;

    @InjectComponent
    private Form form;

    @Property
    private ReferenceInputTemplate referenceInputTemplate;

    private List<ReferenceInputTemplate> referenceInputTemplates;

    public List<ReferenceInputTemplate> getReferenceInputTemplates() {
        return referenceInputTemplateHibernate.getAll();
    }

    void onPrepareForRender() throws Exception {
        // If fresh start, make sure there's a Person object available.
        if (form.isValid()) {
            referenceType = new ReferenceType();
        }
    }

    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        referenceType = new ReferenceType();
    }

    @CommitAfter
    Object onSuccess() {
        referenceTypeHibernate.store(referenceType);

        if (referenceInputTemplate != null){
            referenceTypeHibernate.setReferenceInputTemplate(referenceType, referenceInputTemplate);
        }

        return index;
    }
}
