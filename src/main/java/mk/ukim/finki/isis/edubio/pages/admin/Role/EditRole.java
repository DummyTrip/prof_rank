package mk.ukim.finki.isis.edubio.pages.admin.Role;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.Role;
import mk.ukim.finki.isis.edubio.services.RoleHibernate;
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
public class EditRole {
    @Persist
    private Long roleId;

    @Property
    private Role role;

    @InjectComponent
    private Form form;

    @Inject
    private RoleHibernate roleHibernate;

    @InjectPage
    private Index index;

    void onActivate(Long roleId) {
        this.roleId = roleId;
    }

    Long passivate() {
        return roleId;
    }

    void onPrepareForRender() throws Exception {
        if (form.isValid()) {
            role = findRole(roleId);

            if (role == null) {
                throw new Exception("Role " + roleId + " does not exist.");
            }
        }
    }

    void onPrepareForSubmit() {
        role = findRole(roleId);

        if (role == null) {
            form.recordError("Person has been deleted by another process.");
            // Instantiate an empty role to avoid NPE in the BeanEditForm.
            role = new Role();
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
        roleHibernate.update(role);

        return index;
    }

    void onRefresh() {
        // By doing nothing the page will be displayed afresh.
    }

    private Role findRole(Long roleId) {
        Role role = roleHibernate.getById(roleId);

        if (role == null) {
            throw new IllegalStateException("No data in database.");
        }

        return role;
    }
}

