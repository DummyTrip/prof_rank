package mk.ukim.finki.isis.edubio.pages.admin.SubjectDomain;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.SubjectDomain;
import mk.ukim.finki.isis.edubio.services.SubjectDomainHibernate;
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
public class CreateSubjectDomain {
    @Property
    private SubjectDomain subjectDomain;

    @Inject
    private SubjectDomainHibernate subjectDomainHibernate;

    @InjectPage
    private Index index;

    @InjectComponent
    private Form form;

    void onPrepareForRender() throws Exception {
        // If fresh start, make sure there's a Person object available.
        if (form.isValid()) {
            subjectDomain = new SubjectDomain();
        }
    }

    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        subjectDomain = new SubjectDomain();
    }

    @CommitAfter
    Object onSuccess() {
        subjectDomainHibernate.store(subjectDomain);

        return index;
    }
}
