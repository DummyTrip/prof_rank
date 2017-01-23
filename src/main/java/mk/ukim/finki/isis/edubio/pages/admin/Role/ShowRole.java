package mk.ukim.finki.isis.edubio.pages.admin.Role;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.Role;
import mk.ukim.finki.isis.edubio.services.RoleHibernate;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by Aleksandar on 25-Sep-16.
 */
@AdministratorPage
public class ShowRole {
    @Persist
    @Property
    private Long roleId;

    @Inject
    private RoleHibernate roleHibernate;

    @Persist
    @Property
    private Role role;

    void onActivate(Long roleId) {
        this.roleId = roleId;
    }

    Long passivate() {
        return roleId;
    }

    void setupRender() throws Exception {
        this.role = roleHibernate.getById(roleId);
    }
}
