package mk.ukim.finki.isis.edubio.components;

import mk.ukim.finki.isis.edubio.services.PersonHibernate;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.List;

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
