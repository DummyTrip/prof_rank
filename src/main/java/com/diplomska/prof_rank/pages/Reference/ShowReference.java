package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.AttributeHibernate;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.ReferenceInstanceHibernate;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.internal.util.IntegerRange;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import java.util.*;

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

    @Persist
    @Property
    Long oldReferenceId;

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

    @Persist
    @Property
    List<ReferenceInstance> allReferenceInstances;

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
        return referenceInstanceHibernate.getSortedAttributeReferenceInstance(referenceInstance);
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
        if (!referenceId.equals(oldReferenceId)) {
            selectedCheckboxes = null;
            toggleDisplay = true;
            oldReferenceId = referenceId;
        }

        this.reference = referenceHibernate.getById(referenceId);
        this.referenceName = referenceNameQueryString;
        this.displayNames = referenceInstanceHibernate.getAllDisplayNames();
        testMap = new HashMap<String, String>();

        refInstances = new ArrayList<ReferenceInstance>();
        firstPageRefInstances = new ArrayList<ReferenceInstance>();

        filterMap = filterQueryStringToMap(filtersQueryString);

        if (filterMap.keySet().size() > 0) {
            allReferenceInstances = sortReferenceInstaces(filterMap);
        } else {
            allReferenceInstances = sortReferenceInstaces();
        }

        if (selectedCheckboxes == null) {
            selectedCheckboxes = new HashMap<Long, Integer>();
            for (Attribute attribute : getAttributes()) {
                Integer value = referenceHibernate.isDisplayAttribute(reference, attribute) ? 1 : 0;

                selectedCheckboxes.put(attribute.getId(), value);
            }
        }
    }

    private List<ReferenceInstance> sortReferenceInstaces() {
        List<ReferenceInstance> referenceInstances = referenceInstanceHibernate.getByReference(reference);

        return getSortedReferenceInstances(referenceInstances);
    }

    private List<ReferenceInstance> sortReferenceInstaces(Map<String, String> filterMap) {
        List<ReferenceInstance> referenceInstances = referenceInstanceHibernate.getByReferenceAndFilter(reference, filterMap, 0, Integer.MAX_VALUE);

        return getSortedReferenceInstances(referenceInstances);
    }

    private List<ReferenceInstance> getSortedReferenceInstances(List<ReferenceInstance> referenceInstances) {

        Attribute orderAttribute = getOrderAttribute();

        if (orderAttribute == null) {
            return referenceInstances;
        }

        Map<ReferenceInstance, String> unsortedMap = getUnsortedMapOfReferenceInstaces(referenceInstances, orderAttribute);

        Map<ReferenceInstance, String> sortedMap = sortMapByValue(unsortedMap);
        referenceInstances = new ArrayList<ReferenceInstance>();

        for (ReferenceInstance ri : sortedMap.keySet()) {
            referenceInstances.add(ri);
        }

        return referenceInstances;
    }

    private Attribute getOrderAttribute() {
        List<Attribute> attributes = referenceHibernate.getAttributeValues(reference);
        Attribute orderAttribute = null;

        for (Attribute attribute : attributes) {
            String attributeName = attribute.getName();
            if (attributeName.startsWith("Период") ||
                    attributeName.equals("Год.") ||
                    attributeName.equals("Година")) {
                orderAttribute = attribute;
                break;
            }
        }

        return orderAttribute;
    }

    private Map<ReferenceInstance, String> getUnsortedMapOfReferenceInstaces(List<ReferenceInstance> referenceInstances, Attribute orderAttribute) {
        Map<ReferenceInstance, String> unsortedMap = new HashMap<ReferenceInstance, String>();

        for (ReferenceInstance refInstance : referenceInstances) {
            for (AttributeReferenceInstance ari: refInstance.getAttributeReferenceInstances()) {
                if (ari.getAttribute().equals(orderAttribute)) {
                    unsortedMap.put(refInstance, ari.getValue());
                    break;
                }
            }
        }

        return unsortedMap;
    }

    private Map<ReferenceInstance, String> sortMapByValue(Map<ReferenceInstance, String> unsortedMap) {
        List<Map.Entry<ReferenceInstance, String>> list =
                new LinkedList<Map.Entry<ReferenceInstance, String>>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<ReferenceInstance, String>>() {
            public int compare(Map.Entry<ReferenceInstance, String> o1,
                               Map.Entry<ReferenceInstance, String> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<ReferenceInstance, String> sortedMap = new LinkedHashMap<ReferenceInstance, String>();
        for (Map.Entry<ReferenceInstance, String> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
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
        Integer allReferenceInstancesSize = allReferenceInstances.size();
        // make sure first is not larger than the size of all instances
        Integer first = allReferenceInstancesSize > pageNumber * PageSize ? pageNumber * PageSize : allReferenceInstancesSize;
        // make sure last element index is not larger than the size of all instances
        Integer last = allReferenceInstancesSize > first + PageSize ? first + PageSize : allReferenceInstancesSize;
        // Delays the ajax call.
        // Sometimes the call returns almost instantly, which is a bad user experience.
        Thread.sleep(200);

        List<ReferenceInstance> newInstances = allReferenceInstances.subList(first, last);

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

    @Persist
    @Property
    boolean toggleDisplay;

    @Inject
    AjaxResponseRenderer ajaxResponseRenderer;

    @InjectComponent
    Zone leftPanelZone;

    @Persist
    @Property
    Map<Long, Integer> selectedCheckboxes;

    public boolean isSelectedCheckbox() {
        return selectedCheckboxes.get(attribute.getId()) == 0 ? false : true;
    }

    void onActionFromToggleDisplay() {
        toggleDisplay = !toggleDisplay;

        ajaxResponseRenderer.addRender(leftPanelZone);
    }

    public Integer getAttVal() {
        return selectedCheckboxes.get(attribute.getId());
    }

    public Long getAttId() {
        return attribute.getId();
    }

    @Inject
    AttributeHibernate attributeHibernate;

    @OnEvent(component = "addDisplay", value = "selected")
    public void addDisplay(Long attributeId) {
        selectedCheckboxes.put(attributeId, 1);

        ajaxResponseRenderer.addRender(leftPanelZone);

    }

    @OnEvent(component = "removeDisplay", value = "selected")
    public void removeDisplay(Long attributeId) {
        selectedCheckboxes.put(attributeId, 0);

        ajaxResponseRenderer.addRender(leftPanelZone);

    }

    @CommitAfter
    @OnEvent(component = "saveDisplay", value = "selected")
    void saveDisplay() {
        Reference reference = referenceHibernate.getById(referenceId);
        List<ReferenceInstance> referenceInstances = referenceInstanceHibernate.getByReference(reference);

        for (ReferenceInstance referenceInstance : referenceInstances) {
            List<AttributeReferenceInstance> attributeReferenceInstances = referenceInstance.getAttributeReferenceInstances();

            for (AttributeReferenceInstance ari : attributeReferenceInstances) {
                Attribute attribute = ari.getAttribute();
                if (selectedCheckboxes.containsKey(attribute.getId())) {
                    boolean display = selectedCheckboxes.get(attribute.getId()) == 0 ? false : true;

                    referenceInstanceHibernate.setAttributeDisplay(ari, referenceInstance, attribute, display);
                }
            }
        }
    }

}
