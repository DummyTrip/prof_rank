package com.diplomska.prof_rank.pages.admin.InstitutionProfRank;

import com.diplomska.prof_rank.entities.InstitutionProfRank;
import com.diplomska.prof_rank.services.CountryNames;
import com.diplomska.prof_rank.services.InstitutionHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class EditInstitution {
    @Persist
    private Long institutionId;

    @Property
    private InstitutionProfRank institutionProfRank;

    @InjectComponent
    private Form form;

    @Inject
    private InstitutionHibernate institutionHibernate;

    @InjectPage
    private Index index;

    @Inject
    private CountryNames countryNames;

    List<String> onProvideCOmpletionsFromCountry(String partial) {
        List<String> matches = new ArrayList<String>();
        partial = partial.toUpperCase();

        for (String countryName : countryNames.getSet()) {
            if (countryName.toUpperCase().startsWith(partial)) {
                matches.add(countryName);
            }
        }

        return matches;
    }

    void onActivate(Long institutionId) {
        this.institutionId = institutionId;
    }

    Long passivate() {
        return institutionId;
    }

    void onPrepareForRender() throws Exception {
        if (form.isValid()) {
            institutionProfRank = findInstitution(institutionId);

            if (institutionProfRank == null) {
                throw new Exception("InstitutionProfRank " + institutionId + " does not exist.");
            }
        }
    }

    void onPrepareForSubmit() {
        institutionProfRank = findInstitution(institutionId);

        if (institutionProfRank == null) {
            form.recordError("Person has been deleted by another process.");
            // Instantiate an empty institutionProfRank to avoid NPE in the BeanEditForm.
            institutionProfRank = new InstitutionProfRank();
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
        institutionHibernate.update(institutionProfRank);

        return index;
    }

    void onRefresh() {
        // By doing nothing the page will be displayed afresh.
    }

    private InstitutionProfRank findInstitution(Long institutionId) {
        InstitutionProfRank institutionProfRank = institutionHibernate.getById(institutionId);

        if (institutionProfRank == null) {
            throw new IllegalStateException("No data in database.");
        }

        return institutionProfRank;
    }
}
