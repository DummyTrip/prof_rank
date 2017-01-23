package mk.ukim.finki.isis.edubio.pages.admin.Section;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.Section;
import mk.ukim.finki.isis.edubio.services.SectionHibernate;
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
public class CreateSection {
    @Property
    private Section section;

    @Inject
    private SectionHibernate sectionHibernate;

    @InjectPage
    private Index index;

    @InjectComponent
    private Form form;

    void onPrepareForRender() throws Exception {
        // If fresh start, make sure there's a Person object available.
        if (form.isValid()) {
            section = new Section();
        }
    }

    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        section = new Section();
    }

    @CommitAfter
    Object onSuccess() {
        sectionHibernate.store(section);

        return index;
    }
}
