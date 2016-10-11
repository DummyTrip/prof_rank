package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.ReferenceInstanceHibernate;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aleksandar on 03-Oct-16.
 */
@InstructorPage
@Import(library={"context:js/main.js"})
public class ShowReference {

    @Inject
    ReferenceHibernate referenceHibernate;

    @Inject
    ReferenceInstanceHibernate referenceInstanceHibernate;

    @Inject
    UserHibernate userHibernate;

    @Persist
    @Property
    Reference reference;

    @Property
    ReferenceInstance referenceInstance;

    @Property
    AttributeReferenceInstance ari;

    @Persist
    @Property
    Long referenceId;

    @Property
    String referenceName;

    @Property
    Attribute attribute;

    @ActivationRequestParameter(value = "name")
    private String referenceNameQueryString;

    // This query is used to send a dynamic list of filters.
    // The filters are sent in this format:
    //      key1:;%value1,:%key2:;%value2...
    // The filters are written to Map<String, String> filterMap in setupRender.
    @ActivationRequestParameter(value = "filters")
    private String filtersQueryString;

    @Inject
    private PageRenderLinkSource pageRenderLinkSource;

    @Persist
    private List<String> displayNames;

    @Persist
    @Property
    private Map<String, String> testMap;

    @Persist
    @Property
    Map<String, String> filterMap;

    public List<ReferenceInstance> getReferenceInstances() {
        User user = userHibernate.getById(Long.valueOf(1));
        if (user != null) {
//            return referenceInstanceHibernate.getByReferenceAndUser(reference, user);
            List<ReferenceInstance> referenceInstances;

            if (filterMap.keySet().size() > 0) {
                referenceInstances = referenceInstanceHibernate.getByReferenceAndFilter(reference, filterMap, 0, 1000);
            } else {
                referenceInstances = referenceInstanceHibernate.getByReference(reference);
            }

            return referenceInstances;
        } else {
            return new ArrayList<ReferenceInstance>();
        }
    }

    public String getDisplayName() {
        return referenceInstanceHibernate.getDisplayName(referenceInstance);
    }

    public List<Attribute> getAttributes() {
        return referenceHibernate.getAttributeValues(reference);
    }

    public List<AttributeReferenceInstance> getAttributeValues() {
        return referenceInstance.getAttributeReferenceInstances();
    }

    void onActivate(Long referenceId) {
        this.referenceId = referenceId;
    }

    Long passivate() {
        return referenceId;
    }

    List<String> onProvideCompletionsFromSearchName(String partial) {
        List<String> matches = new ArrayList<String>();
        partial = partial.toUpperCase();

        for (String name : displayNames) {
            if (name.toUpperCase().startsWith(partial)) {
                matches.add(name);
            }
        }

        return matches;
    }

    public Link set(String referenceName) {
        this.referenceNameQueryString = referenceName;

        return pageRenderLinkSource.createPageRenderLink(this.getClass());
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

    public Object onSuccessFromFilterForm() {
        Link link = this.setFilters(testMap);

        return link;
    }

    public Object onSuccessFromForm() {
        Link link = this.set(referenceName);

        return link;
    }

    public boolean isTextInput() {
        return attribute.getInputType().equals("text") ? true :false;
    }

    void setupRender() throws Exception {
        this.reference = referenceHibernate.getById(referenceId);
        this.referenceName = referenceNameQueryString;
        this.displayNames = referenceInstanceHibernate.getAllDisplayNames();
        testMap = new HashMap<String, String>();

        refInstances = new ArrayList<ReferenceInstance>();
        firstPageRefInstances = new ArrayList<ReferenceInstance>();

        filterMap = filterQueryStringToMap(filtersQueryString);
    }

    // Write the contents of the query string to a map.
    private Map<String, String> filterQueryStringToMap(String filtersQueryString) {
        Map<String, String> queryMap = new HashMap<String, String>();

        if (filtersQueryString != null && filtersQueryString.contains(",;%")) {
            String[] filters = filtersQueryString.split(",;%");
            for (String f : filters) {
                queryMap.put(f.split(":;%")[0], f.split(":;%")[1]);
            }

            return queryMap;
        } else {
            return new HashMap<String, String>();
        }
    }

    @Property
    int pageNumber;

    private static final Integer PageSize = 20;

    @Persist
    @Property
    List<ReferenceInstance> firstPageRefInstances;

    @Persist
    @Property
    List<ReferenceInstance> refInstances;

    // ajax call, used fpr paging of ReferenceInstances
    @OnEvent("nextPage")
    List<ReferenceInstance> moreValues() throws InterruptedException {
        Integer first = pageNumber * PageSize;
        // Delays the ajax call.
        // Sometimes the call returns almost instantly, which is a bad user experience.
        Thread.sleep(200);

        List<ReferenceInstance> newInstances;

        // filter map contains filters from query string
        if (filterMap.keySet().size() > 0) {
            newInstances = referenceInstanceHibernate.getByReferenceAndFilter(reference, filterMap, first, PageSize);
        } else {
            newInstances = referenceInstanceHibernate.getByReference(reference, first, PageSize);
        }

        // This if/else is a fix.
        // When items in page 0 are fewer than PageSize, the items get duplicated.
        // This fix makes sure the items from page 0 don't duplicate
        // by separating the items of page 0 from the rest.
        if (pageNumber == 0) {
            firstPageRefInstances.addAll(newInstances);
            return firstPageRefInstances;
        } else {
            refInstances.addAll(newInstances);

            return refInstances;
        }
    }

}
