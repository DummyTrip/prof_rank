package com.diplomska.prof_rank.pages.admin.Institution;

import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.InstitutionHibernate;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by Aleksandar on 25-Sep-16.
 */
public class ShowInstitution {
    @Persist
    @Property
    private Long institutionId;

    @Inject
    private InstitutionHibernate institutionHibernate;

    @Persist
    @Property
    private Institution institution;

    void onActivate(Long institutionId) {
        this.institutionId = institutionId;
    }

    Long passivate() {
        return institutionId;
    }

    void setupRender() throws Exception {
        this.institution = institutionHibernate.getById(institutionId);
    }
}
