package com.diplomska.prof_rank.pages.Scholar;

import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.ReferenceType;
import com.diplomska.prof_rank.pages.Reference.CreateReference;
import com.diplomska.prof_rank.services.ReferenceTypeHibernate;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 21-Oct-16.
 */
@InstructorPage
public class Index {

    @Persist
    @Property
    Integer numScholar;

    @Persist
    @Property
    List<String> scholars;

    @Property
    String scholar;

    @Property
    ReferenceType referenceType;

    @Inject
    ReferenceTypeHibernate referenceTypeHibernate;

    void setupRender() throws Exception {
        referenceType = referenceTypeHibernate.getByColumn("name", "Papers").get(0);

        if (numScholar == null) {
            numScholar = -1;
        }

        if (scholars == null) {
            scholars = new ArrayList<String>();
        }

        readScholar();
    }

    void readScholar() throws Exception{
        String command = "py scholar.py --author \"vangel ajanovski\"";

        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", command);
        builder.redirectErrorStream(true);
        Process p = builder.start();

        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line;
        numScholar = 0;
        scholars = new ArrayList<String>();

        while (true) {
            line = r.readLine();
            if (line == null) { break; }
            line = line.trim();
            if (line.startsWith("Title")) {
                scholars.add(line);
                numScholar++;
            }
        }

    }

    @InjectPage
    CreateReference createReferencePage;

    Object onActionFromCreateReference(String title) {
        Link link = createReferencePage.setPhrase(title);

        return link;
    }
}
