package com.diplomska.prof_rank.components;

import com.diplomska.prof_rank.services.PersonHibernate;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Aleksandar on 17-Oct-16.
 */
public class AuthorMapField extends MapField {
    @Inject
    PersonHibernate personHibernate;

    List<String> onProvideCompletionsFromAuthor(String partial) {
        List<String> matches = new ArrayList<String>();
        partial = partial.toUpperCase();

        for (String personIdentifier : personHibernate.listAllPersonIdentifiers()) {
            if (personIdentifier.toUpperCase().contains(partial)) {
                matches.add(personIdentifier);
            }
        }

        return matches;
    }
}
