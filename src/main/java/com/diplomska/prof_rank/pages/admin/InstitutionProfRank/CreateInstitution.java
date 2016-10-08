package com.diplomska.prof_rank.pages.admin.InstitutionProfRank;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.entities.InstitutionProfRank;
import com.diplomska.prof_rank.services.CountryNames;
import com.diplomska.prof_rank.services.InstitutionHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
@AdministratorPage
public class CreateInstitution {
    @Property
    private InstitutionProfRank institutionProfRank;

    @Inject
    private InstitutionHibernate institutionHibernate;

    @InjectPage
    private Index index;

    @InjectComponent
    private Form form;

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

    void onPrepareForRender() throws Exception {
        // If fresh start, make sure there's a Person object available.
        if (form.isValid()) {
            institutionProfRank = new InstitutionProfRank();
        }
    }

    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        institutionProfRank = new InstitutionProfRank();
    }

    @CommitAfter
    Object onSuccess() {
        institutionHibernate.store(institutionProfRank);

        return index;
    }
}
