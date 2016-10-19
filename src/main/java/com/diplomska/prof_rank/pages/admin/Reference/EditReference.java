package com.diplomska.prof_rank.pages.admin.Reference;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.entities.ReferenceInputTemplate;
import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.services.ReferenceTypeHibernate;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
@AdministratorPage
public class EditReference {
    @Persist
    private Long referenceId;

    @Property
    private Reference reference;

    @InjectComponent
    private Form form;

    @Inject
    private ReferenceHibernate referenceHibernate;

    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

    @InjectPage
    private Index index;

    @Property
    private SelectModel referenceInputTemplateSelectModel;

    @Inject
    SelectModelFactory selectModelFactory;

    void setupRender() {
        List<ReferenceInputTemplate> referenceInputTemplates = referenceTypeHibernate.getAll();

        referenceInputTemplateSelectModel = selectModelFactory.create(referenceInputTemplates, "name");
    }

//    @Property
//    private ReferenceInputTemplate referenceType;
//    private List<ReferenceInputTemplate> referenceTypes;
//
//    public List<ReferenceInputTemplate> getReferenceInputTemplates() {
//        return referenceTypeHibernate.getAll();
//    }

    void onActivate(Long referenceId) {
        this.referenceId = referenceId;
    }

    Long passivate() {
        return referenceId;
    }

    void onPrepareForRender() throws Exception {
        if (form.isValid()) {
            reference = findReference(referenceId);

            if (reference == null) {
                throw new Exception("Reference " + referenceId + " does not exist.");
            }
        }
    }

    void onPrepareForSubmit() {
        reference = findReference(referenceId);

        if (reference == null) {
            form.recordError("Person has been deleted by another process.");
            // Instantiate an empty reference to avoid NPE in the BeanEditForm.
            reference = new Reference();
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
//        reference.setUloga(uloga);
        referenceHibernate.update(reference);

        return index;
    }

    void onRefresh() {
        // By doing nothing the page will be displayed afresh.
    }

    private Reference findReference(Long referenceId) {
        Reference reference = referenceHibernate.getById(referenceId);

        if (reference == null) {
            throw new IllegalStateException("No data in database.");
        }

        return reference;
    }
}
