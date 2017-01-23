package mk.ukim.finki.isis.edubio.services;

import org.hsqldb.lib.StringUtil;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Aleksandar on 25-Sep-16.
 */
public class CountryNames {
    private Set<String> countryNames = new TreeSet<String>();

    public CountryNames() {
        Locale[] availableLocales = Locale.getAvailableLocales();

        for (Locale locale : availableLocales) {
            if (!StringUtil.isEmpty(locale.getDisplayCountry())) {
                countryNames.add(locale.getDisplayCountry());
            }
        }
    }

    public  Set<String> getSet() {
        return Collections.unmodifiableSet(countryNames);
    }
}
