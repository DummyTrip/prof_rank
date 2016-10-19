package com.diplomska.prof_rank.pages.admin.ReferenceType;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.entities.ReferenceInputTemplate;
import com.diplomska.prof_rank.entities.ReferenceType;
import com.diplomska.prof_rank.services.ReferenceInputTemplateHibernate;
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
public class EditReferenceType {
    @Persist
    private Long referenceTypeId;

    @Property
    private ReferenceType referenceType;

    @InjectComponent
    private Form form;

    @Inject
    private ReferenceHibernate referenceHibernate;

    @Inject
    private ReferenceInputTemplateHibernate referenceInputTemplateHibernate;

    @InjectPage
    private Index index;

    @Property
    private SelectModel referenceInputTemplateSelectModel;

    @Inject
    SelectModelFactory selectModelFactory;

    void setupRender() {
        List<ReferenceInputTemplate> referenceInputTemplates = referenceInputTemplateHibernate.getAll();

        referenceInputTemplateSelectModel = selectModelFactory.create(referenceInputTemplates, "name");
    }

//    @Property
//    private ReferenceInputTemplate referenceType;
//    private List<ReferenceInputTemplate> referenceTypes;
//
//    public List<ReferenceInputTemplate> getReferenceInputTemplates() {
//        return referenceInputTemplateHibernate.getAll();
//    }

    void onActivate(Long referenceTypeId) {
        this.referenceTypeId = referenceTypeId;
    }

    Long passivate() {
        return referenceTypeId;
    }

    void onPrepareForRender() throws Exception {
        if (form.isValid()) {
            referenceType = findReference(referenceTypeId);

            if (referenceType == null) {
                throw new Exception("ReferenceType " + referenceTypeId + " does not exist.");
            }
        }
    }

    void onPrepareForSubmit() {
        referenceType = findReference(referenceTypeId);

        if (referenceType == null) {
            form.recordError("Person has been deleted by another process.");
            // Instantiate an empty referenceType to avoid NPE in the BeanEditForm.
            referenceType = new ReferenceType();
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
//        referenceType.setUloga(uloga);
        referenceHibernate.update(referenceType);

        return index;
    }

    void onRefresh() {
        // By doing nothing the page will be displayed afresh.
    }

    private ReferenceType findReference(Long referenceTypeId) {
        ReferenceType referenceType = referenceHibernate.getById(referenceTypeId);

        if (referenceType == null) {
            throw new IllegalStateException("No data in database.");
        }

        return referenceType;
    }
}
