package com.diplomska.prof_rank.pages.SubjectDomain;

import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.entities.Role;
import com.diplomska.prof_rank.entities.SubjectDomain;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.RoleHibernate;
import com.diplomska.prof_rank.services.SubjectDomainHibernate;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;

import com.diplomska.prof_rank.pages.SubjectDomain.Index;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class EditSubjectDomain {
    @Persist
    private Long subjectDomainId;

    @Property
    private SubjectDomain subjectDomain;

    @InjectComponent
    private Form form;

    @Inject
    private SubjectDomainHibernate subjectDomainHibernate;

    @Inject
    private RoleHibernate roleHibernate;

    @Inject
    private ReferenceHibernate referenceHibernate;

    @InjectPage
    private Index index;

    void onActivate(Long subjectDomainId) {
        this.subjectDomainId = subjectDomainId;
    }

    Long passivate() {
        return subjectDomainId;
    }

    void onPrepareForRender() throws Exception {
        if (form.isValid()) {
            subjectDomain = findSubjectDomain(subjectDomainId);

            if (subjectDomain == null) {
                throw new Exception("SubjectDomain " + subjectDomainId + " does not exist.");
            }
        }
    }

    void onPrepareForSubmit() {
        subjectDomain = findSubjectDomain(subjectDomainId);

        if (subjectDomain == null) {
            form.recordError("Person has been deleted by another process.");
            // Instantiate an empty subjectDomain to avoid NPE in the BeanEditForm.
            subjectDomain = new SubjectDomain();
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
        subjectDomainHibernate.update(subjectDomain);

        return index;
    }

    void onRefresh() {
        // By doing nothing the page will be displayed afresh.
    }

    private SubjectDomain findSubjectDomain(Long subjectDomainId) {
        SubjectDomain subjectDomain = subjectDomainHibernate.getById(subjectDomainId);

        if (subjectDomain == null) {
            throw new IllegalStateException("No data in database.");
        }

        return subjectDomain;
    }
}
