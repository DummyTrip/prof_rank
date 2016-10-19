package com.diplomska.prof_rank.pages.admin.Person;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.entities.ReferenceType;
import com.diplomska.prof_rank.entities.Role;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.RoleHibernate;
import com.diplomska.prof_rank.services.PersonHibernate;
import mk.ukim.finki.isis.model.entities.Person;
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
 * Created by Aleksandar on 06-Oct-16.
 */
@AdministratorPage
public class EditPerson {
    @Persist
    private Long personId;

    @Property
    private Person person;

    @InjectComponent
    private Form form;

    @Inject
    private PersonHibernate personHibernate;

    @Inject
    private RoleHibernate roleHibernate;

    @Inject
    private ReferenceHibernate referenceHibernate;

    @InjectPage
    private com.diplomska.prof_rank.pages.admin.Person.Index index;

    @Property
    private SelectModel roleSelectModel;

    @Property
    private SelectModel referenceSelectModel;

    @Inject
    SelectModelFactory selectModelFactory;

    void setupRender() {
        List<Role> roles = roleHibernate.getAll();
        List<ReferenceType> referenceTypes = referenceHibernate.getAll();

        roleSelectModel = selectModelFactory.create(roles, "name");
        referenceSelectModel = selectModelFactory.create(referenceTypes, "name");
    }

    @Property
    private Role role;
    private List<Role> roles;

    public List<Role> getRoles() {
        return roleHibernate.getAll();
    }

    void onActivate(Long personId) {
        this.personId = personId;
    }

    Long passivate() {
        return personId;
    }

    void onPrepareForRender() throws Exception {
        if (form.isValid()) {
            person = findPerson(personId);

            if (person == null) {
                throw new Exception("Person " + personId + " does not exist.");
            }

            role = personHibernate.getRole(person);
            if (role == null) {
                role = new Role();
            }
        }
    }

    void onPrepareForSubmit() {
        person = findPerson(personId);

        if (person == null) {
            form.recordError("Person has been deleted by another process.");
            // Instantiate an empty person to avoid NPE in the BeanEditForm.
            person = new Person();
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
        if (role == null) {
            personHibernate.deleteRole(person);
        } else {
            personHibernate.setRole(person, role);
        }

        personHibernate.update(person);

        return index;
    }

    void onRefresh() {
        // By doing nothing the page will be displayed afresh.
    }

    private Person findPerson(Long personId) {
        Person person = personHibernate.getById(personId);

        if (person == null) {
            throw new IllegalStateException("No data in database.");
        }

        return person;
    }

}
