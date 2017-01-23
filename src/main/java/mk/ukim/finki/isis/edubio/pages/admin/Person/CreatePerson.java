package mk.ukim.finki.isis.edubio.pages.admin.Person;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.Role;
import mk.ukim.finki.isis.edubio.services.RoleHibernate;
import mk.ukim.finki.isis.edubio.services.PersonHibernate;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

/**
 * Created by Aleksandar on 06-Oct-16.
 */
@AdministratorPage
public class CreatePerson {
    @Property
    private Person person;

    @Inject
    private PersonHibernate personHibernate;

    @Inject
    private RoleHibernate roleHibernate;

    @InjectPage
    private Index index;

    @InjectComponent
    private Form form;

    @Property
    private Role role;

    private List<Role> roles;

    public List<Role> getRoles() {
        return roleHibernate.getAll();
    }

    void onPrepareForRender() throws Exception {
        // If fresh start, make sure there's a Person object available.
        if (form.isValid()) {
            person = new Person();
        }
    }

    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        person = new Person();
    }

    @CommitAfter
    Object onSuccess() {
        personHibernate.store(person);

        if (role != null){
            personHibernate.setRole(person, role);
        }

        return index;
    }

}
