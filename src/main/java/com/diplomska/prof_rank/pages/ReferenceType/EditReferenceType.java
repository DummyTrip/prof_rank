package com.diplomska.prof_rank.pages.ReferenceType;

import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.entities.Role;
import com.diplomska.prof_rank.entities.ReferenceType;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.RoleHibernate;
import com.diplomska.prof_rank.services.ReferenceTypeHibernate;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;

import com.diplomska.prof_rank.pages.ReferenceType.Index;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class EditReferenceType {
    @Persist
    private Long referenceTypeId;

    @Property
    private ReferenceType referenceType;

    @InjectComponent
    private Form form;

    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

    @InjectPage
    private Index index;

    void onActivate(Long referenceTypeId) {
        this.referenceTypeId = referenceTypeId;
    }

    Long passivate() {
        return referenceTypeId;
    }

    void onPrepareForRender() throws Exception {
        if (form.isValid()) {
            referenceType = findReferenceType(referenceTypeId);

            if (referenceType == null) {
                throw new Exception("ReferenceType " + referenceTypeId + " does not exist.");
            }
        }
    }

    void onPrepareForSubmit() {
        referenceType = findReferenceType(referenceTypeId);

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
        referenceTypeHibernate.update(referenceType);

        return index;
    }

    void onRefresh() {
        // By doing nothing the page will be displayed afresh.
    }

    private ReferenceType findReferenceType(Long referenceTypeId) {
        ReferenceType referenceType = referenceTypeHibernate.getById(referenceTypeId);

        if (referenceType == null) {
            throw new IllegalStateException("No data in database.");
        }

        return referenceType;
    }
}
