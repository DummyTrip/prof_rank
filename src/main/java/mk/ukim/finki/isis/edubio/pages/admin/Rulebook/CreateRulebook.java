package mk.ukim.finki.isis.edubio.pages.admin.Rulebook;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.Rulebook;
import mk.ukim.finki.isis.edubio.services.RulebookHibernate;
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
public class CreateRulebook {
    @Property
    private Rulebook rulebook;

    @Inject
    private RulebookHibernate rulebookHibernate;

    @InjectPage
    private Index index;

    @InjectComponent
    private Form form;

    void onPrepareForRender() throws Exception {
        // If fresh start, make sure there's a Person object available.
        if (form.isValid()) {
            rulebook = new Rulebook();
        }
    }

    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        rulebook = new Rulebook();
    }

    @CommitAfter
    Object onSuccess() {
        rulebookHibernate.store(rulebook);

        return index;
    }
}
