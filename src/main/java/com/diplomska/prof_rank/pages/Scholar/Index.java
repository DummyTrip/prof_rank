package com.diplomska.prof_rank.pages.Scholar;

import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.entities.ReferenceType;
import com.diplomska.prof_rank.pages.Reference.CreateReference;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.ReferenceTypeHibernate;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

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
    List<String> allScholarResults;

    @Persist
    @Property
    List<String> scholarResults;

    @Property
    String scholarResult;

    @Property
    ReferenceType referenceType;

    @Inject
    ReferenceTypeHibernate referenceTypeHibernate;

    void setupRender() throws Exception {
        referenceType = referenceTypeHibernate.getByColumn("name", "Papers").get(0);

        if (numScholar == null) {
            numScholar = -1;
        }

//        filtersQueryString = null;
        if (filtersQueryString != null) {
            scholarResults = filterScholarResults(filterMap);
        }

        if (allScholarResults == null) {
            allScholarResults = new ArrayList<String>();
            scholarResults = new ArrayList<String>();

            readScholar();
        }

        if (searchScholarResult == null) {
            searchScholarResult = "";
        }

        if (scholarResultTitleQueryString != null) {
            scholarResults = filterScholarResultsByTitle(scholarResultTitleQueryString);

            if (scholarResults.size() == 0) {
                scholarResults = allScholarResults;
            }
        }

        if (attributes == null) {
            attributes = new ArrayList<String>();
            attributes.add("Title");
            attributes.add("Year");
        }

        if (filterMap == null) {
            filterMap = new HashMap<String, String>();
//            filterMap.put("Title", "");
//            filterMap.put("Year", "");
        }
    }

    @Inject
    ReferenceHibernate referenceHibernate;

    // todo remove hardcoded commands
    void readScholar() throws Exception{
        String command = "py scholar.py --author \"vangel ajanovski\"";

        // todo make it work for linux
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", command);
        builder.redirectErrorStream(true);
        Process p = builder.start();

        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line;
        numScholar = 0;
        allScholarResults = new ArrayList<String>();
        String paper = "";
        while (true) {
            line = r.readLine();
            if (line == null) { break; }
            line = line.trim();
            String lineToLower = line.toLowerCase();
            if (lineToLower.startsWith("title")) {
                paper += line.substring(6);
            } else if (lineToLower.startsWith("year")) {
                paper = line.substring(5) + ", " + paper;
            }
            if (line.trim().length() == 0) {
                allScholarResults.add(paper);
                numScholar++;
                paper = "";
            }
        }

        List<String> allDisplayNames = referenceHibernate.getAllDisplayNames();

        for (String result: allScholarResults) {
            String title = result;
            String[] splitResult = result.split(", ");
            if (splitResult.length > 1) {
                title = splitResult[1];
            }

            for (String name: allDisplayNames) {
                if (name.startsWith(title)) {
                    // todo see why it doesn't filter papers alredy written to db.
                    allScholarResults.remove(result);
                    break;
                }
            }
        }

        Collections.sort(allScholarResults, Collections.reverseOrder());
        scholarResults = allScholarResults;
    }

    @InjectPage
    CreateReference createReferencePage;

    Object onActionFromCreateReference(String title) {
        ReferenceType referenceType = referenceTypeHibernate.getByColumn("name", "Papers").get(0);
        Link link = createReferencePage.setPhrase(referenceType.getId(), title);

        return link;
    }

    public boolean isResultsNotNull() {
        return scholarResults.size() > 0 ? true : false;
    }

    @Property
    String searchScholarResult;

    List<String> onProvideCompletionsFromSearchName(String partial){
        return filterScholarResultsByTitle(partial);
    }

    List<String> filterScholarResultsByTitle(String title) {
        if (title == null || title.equals("")){
            return new ArrayList<String>();
        }

        List<String> matches = new ArrayList<String>();
        title = title.toUpperCase();

        for (String name : scholarResults) {
            String[] splitName = name.split(", ");
            if (splitName.length > 1) {
                name = splitName[1];
            }

            if (name.toUpperCase().startsWith(title)) {
                matches.add(name);
            }
        }

        return matches;
    }

    public Object onSuccessFromForm() {
        Link link = this.set(searchScholarResult);

        return link;
    }

    @ActivationRequestParameter(value = "title")
    private String scholarResultTitleQueryString;

    @Inject
    PageRenderLinkSource pageRenderLinkSource;

    public Link set(String scholarResultTitle) {
        if (scholarResultTitle == null){
            this.scholarResultTitleQueryString = "";
        } else {
            this.scholarResultTitleQueryString = scholarResultTitle;
        }

        return pageRenderLinkSource.createPageRenderLink(this.getClass());
    }

    // This query is used to send a dynamic list of filters.
    // The filters are sent in this format:
    //      key1:;%value1,:%key2:;%value2...
    // The filters are written to Map<String, String> filterMap in setupRender.
    @ActivationRequestParameter(value = "filters")
    private String filtersQueryString;

    @Persist
    @Property
    List<String> attributes;

    @Persist
    @Property
    String attribute;

    @Persist
    @Property
    Map<String, String> filterMap;

    public Object onSuccessFromFilterForm() {
        Link link = this.setFilters(filterMap);

        return link;
    }

    public Link setFilters(Map<String, String> filterMap) {
        if (filterMap.keySet().size() > 0) {
            filtersQueryString = "";

            for (String key : filterMap.keySet()) {
                if (filterMap.get(key) != null) {
                    filtersQueryString += key + ":;%" + filterMap.get(key) + ",;%";
                }
            }
        }

        return pageRenderLinkSource.createPageRenderLink(this.getClass());
    }

    public Object onActionFromResetFilter() {
        this.filtersQueryString = null;
        this.filterMap = null;
        scholarResults = allScholarResults;

        return this;
    }

    private List<String> filterScholarResults(Map<String, String> filterMap) {
        List<String> matches = new ArrayList<String>();
        String title = filterMap.get("Title") == null ? "-1" : filterMap.get("Title").toUpperCase();
        String year = filterMap.get("Year") == null ? "-1" : filterMap.get("Year").toUpperCase();

        for (String name : allScholarResults) {
            String[] splitName = name.split(", ");
            String scholarTitle = name;
            String scholarYear = "";

            if (splitName.length > 1) {
                scholarTitle = splitName[1];
                scholarYear = splitName[0];
            }

            if (scholarTitle.toUpperCase().startsWith(title) || scholarYear.startsWith(year)) {
                matches.add(name);
            }
        }

        if (matches.size() == 0) {
            return allScholarResults;
        } else {
            return matches;
        }
    }

}
