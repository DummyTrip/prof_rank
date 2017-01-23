package mk.ukim.finki.isis.edubio.pages.admin.InstitutionProfRank;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.InstitutionProfRank;
import mk.ukim.finki.isis.edubio.services.InstitutionHibernate;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by Aleksandar on 25-Sep-16.
 */
@AdministratorPage
public class ShowInstitution {
    @Persist
    @Property
    private Long institutionId;

    @Inject
    private InstitutionHibernate institutionHibernate;

    @Persist
    @Property
    private InstitutionProfRank institutionProfRank;

    void onActivate(Long institutionId) {
        this.institutionId = institutionId;
    }

    Long passivate() {
        return institutionId;
    }

    void setupRender() throws Exception {
        this.institutionProfRank = institutionHibernate.getById(institutionId);
    }
}
