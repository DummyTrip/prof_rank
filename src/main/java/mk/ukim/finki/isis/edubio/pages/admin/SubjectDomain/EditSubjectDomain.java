package mk.ukim.finki.isis.edubio.pages.admin.SubjectDomain;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.SubjectDomain;
import mk.ukim.finki.isis.edubio.services.ReferenceTypeHibernate;
import mk.ukim.finki.isis.edubio.services.RoleHibernate;
import mk.ukim.finki.isis.edubio.services.SubjectDomainHibernate;
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
    private ReferenceTypeHibernate referenceTypeHibernate;

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
