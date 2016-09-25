package com.diplomska.prof_rank.pages.Institution;

import com.diplomska.prof_rank.entities.Institution;
import com.diplomska.prof_rank.services.CountryNames;
import com.diplomska.prof_rank.services.InstitutionHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.diplomska.prof_rank.pages.Institution.Index;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class CreateInstitution {
    @Property
    private Institution institution;

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
            institution = new Institution();
        }
    }

    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        institution = new Institution();
    }

    @CommitAfter
    Object onSuccess() {
        institutionHibernate.store(institution);

        return index;
    }
}
