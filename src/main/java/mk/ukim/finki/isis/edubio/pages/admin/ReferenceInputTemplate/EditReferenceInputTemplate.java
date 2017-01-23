package mk.ukim.finki.isis.edubio.pages.admin.ReferenceInputTemplate;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.ReferenceInputTemplate;
import mk.ukim.finki.isis.edubio.services.ReferenceInputTemplateHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
@AdministratorPage
public class EditReferenceInputTemplate {
    @Persist
    private Long referenceInputTemplateId;

    @Property
    private ReferenceInputTemplate referenceInputTemplate;

    @InjectComponent
    private Form form;

    @Inject
    private ReferenceInputTemplateHibernate referenceInputTemplateHibernate;

    @InjectPage
    private Index index;

    void onActivate(Long referenceInputTemplateId) {
        this.referenceInputTemplateId = referenceInputTemplateId;
    }

    Long passivate() {
        return referenceInputTemplateId;
    }

    void onPrepareForRender() throws Exception {
        if (form.isValid()) {
            referenceInputTemplate = findReferenceInputTemplate(referenceInputTemplateId);

            if (referenceInputTemplate == null) {
                throw new Exception("ReferenceInputTemplate " + referenceInputTemplateId + " does not exist.");
            }
        }
    }

    void onPrepareForSubmit() {
        referenceInputTemplate = findReferenceInputTemplate(referenceInputTemplateId);

        if (referenceInputTemplate == null) {
            form.recordError("Person has been deleted by another process.");
            // Instantiate an empty referenceInputTemplate to avoid NPE in the BeanEditForm.
            referenceInputTemplate = new ReferenceInputTemplate();
        }
    }

    void onValidateFromForm() {
        if (form.getHasErrors()) {
            // server-side error
            return;
        }
    }


    @CommitAfter
    Object onSuccess() {
        referenceInputTemplateHibernate.update(referenceInputTemplate);

        return index;
    }

    void onRefresh() {
        // By doing nothing the page will be displayed afresh.
    }

    private ReferenceInputTemplate findReferenceInputTemplate(Long referenceInputTemplateId) {
        ReferenceInputTemplate referenceInputTemplate = referenceInputTemplateHibernate.getById(referenceInputTemplateId);

        if (referenceInputTemplate == null) {
            throw new IllegalStateException("No data in database.");
        }

        return referenceInputTemplate;
    }
}
