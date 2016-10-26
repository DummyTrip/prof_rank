package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
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
    ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    ReferenceHibernate referenceHibernate;

    @Inject
    PersonHibernate personHibernate;

    @Persist
    @Property
    ReferenceType referenceType;

    @Property
    Reference reference;

    @Property
    AttributeReference ari;

    @Persist
    @Property
    Long referenceTypeId;

    @Persist
    @Property
    Long oldreferenceTypeId;

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
    Map<String, String> filterMap;

    @Persist
    @Property
    List<Reference> allReferences;

    public String getDisplayName() {
        return referenceHibernate.getDisplayName(reference);
    }

    public List<Attribute> getAttributes() {
        return referenceTypeHibernate.getAttributeValues(referenceType);
    }

    public List<AttributeReference> getAttributeValues() {
        return referenceHibernate.getSortedAttributeReferences(reference);
    }

    void onActivate(Long referenceTypeId) {
        this.referenceTypeId = referenceTypeId;
    }

    Long passivate() {
        return referenceTypeId;
    }

    // TODO: make the search bar work
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
        Link link = this.setFilters(filterMap);

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
        if (!referenceTypeId.equals(oldreferenceTypeId)) {
            selectedCheckboxes = null;
            toggleDisplay = true;
            oldreferenceTypeId = referenceTypeId;
        }

        this.referenceType = referenceTypeHibernate.getById(referenceTypeId);
        this.referenceName = referenceNameQueryString;
        this.displayNames = referenceHibernate.getAllDisplayNames();

        refInstances = new ArrayList<Reference>();
        firstPageRefInstances = new ArrayList<Reference>();

        filterMap = filterQueryStringToMap(filtersQueryString);

        if (filterMap.keySet().size() > 0) {
            allReferences = sortReferenceInstaces(filterMap);
        } else {
            allReferences = sortReferenceInstaces();
        }

        if (selectedCheckboxes == null) {
            selectedCheckboxes = new HashMap<Long, Integer>();
            for (Attribute attribute : getAttributes()) {
                Integer value = referenceTypeHibernate.isDisplayAttribute(referenceType, attribute) ? 1 : 0;

                selectedCheckboxes.put(attribute.getId(), value);
            }
        }
    }

    private List<Reference> sortReferenceInstaces() {
        List<Reference> references = referenceHibernate.getByReferenceType(referenceType);

        return getSortedReferences(references);
    }

    private List<Reference> sortReferenceInstaces(Map<String, String> filterMap) {
        Person person = personHibernate.getById(Long.valueOf(1));
        List<Reference> references = referenceHibernate.getByReferenceTypeFilterAndPerson(referenceType, filterMap, person);

        if (references == null) {
            return new ArrayList<Reference>();
        }

        return getSortedReferences(references);
    }

    private List<Reference> getSortedReferences(List<Reference> references) {

        Attribute orderAttribute = getOrderAttribute();

        if (orderAttribute == null) {
            return references;
        }

        Map<Reference, String> unsortedMap = getUnsortedMapOfReferenceInstaces(references, orderAttribute);

        Map<Reference, String> sortedMap = sortMapByValue(unsortedMap);
        references = new ArrayList<Reference>();

        for (Reference ri : sortedMap.keySet()) {
            references.add(ri);
        }

        return references;
    }

    private Attribute getOrderAttribute() {
        List<Attribute> attributes = referenceTypeHibernate.getAttributeValues(referenceType);
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

    private Map<Reference, String> getUnsortedMapOfReferenceInstaces(List<Reference> references, Attribute orderAttribute) {
        Map<Reference, String> unsortedMap = new HashMap<Reference, String>();

        for (Reference refInstance : references) {
            for (AttributeReference ari: refInstance.getAttributeReferences()) {
                if (ari.getAttribute().equals(orderAttribute)) {
                    unsortedMap.put(refInstance, ari.getValue());
                    break;
                }
            }
        }

        return unsortedMap;
    }

    private Map<Reference, String> sortMapByValue(Map<Reference, String> unsortedMap) {
        List<Map.Entry<Reference, String>> list =
                new LinkedList<Map.Entry<Reference, String>>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Reference, String>>() {
            public int compare(Map.Entry<Reference, String> o1,
                               Map.Entry<Reference, String> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<Reference, String> sortedMap = new LinkedHashMap<Reference, String>();
        for (Map.Entry<Reference, String> entry : list) {
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
    List<Reference> firstPageRefInstances;

    @Persist
    @Property
    List<Reference> refInstances;

    // ajax call, used fpr paging of References
    @OnEvent("nextPage")
    List<Reference> moreValues() throws InterruptedException {
        Integer allReferencesSize = allReferences.size();
        // make sure first is not larger than the size of all instances
        Integer first = allReferencesSize > pageNumber * PageSize ? pageNumber * PageSize : allReferencesSize;
        // make sure last element index is not larger than the size of all instances
        Integer last = allReferencesSize > first + PageSize ? first + PageSize : allReferencesSize;
        // Delays the ajax call.
        // Sometimes the call returns almost instantly, which is a bad user experience.
        Thread.sleep(200);

        List<Reference> newInstances = allReferences.subList(first, last);

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

    @CommitAfter
    @OnEvent(component = "addDisplay", value = "selected")
    public void addDisplay(Long attributeId) {
        selectedCheckboxes.put(attributeId, 1);
        saveDisplay();

        ajaxResponseRenderer.addRender(leftPanelZone);

    }

    @CommitAfter
    @OnEvent(component = "removeDisplay", value = "selected")
    public void removeDisplay(Long attributeId) {
        selectedCheckboxes.put(attributeId, 0);
        saveDisplay();

        ajaxResponseRenderer.addRender(leftPanelZone);

    }

    void saveDisplay() {
        List<Reference> references = referenceHibernate.getByReferenceType(referenceType);

        for (Reference reference : references) {
            List<AttributeReference> attributeReferences = reference.getAttributeReferences();

            for (AttributeReference ari : attributeReferences) {
                Attribute attribute = ari.getAttribute();
                if (selectedCheckboxes.containsKey(attribute.getId())) {
                    boolean display = selectedCheckboxes.get(attribute.getId()) == 0 ? false : true;

                    referenceHibernate.setAttributeDisplay(ari, reference, display);
                }
            }
        }
    }

    public Object onActionFromRefreshPage(Long referenceTypeId) {
        this.referenceTypeId = referenceTypeId;
        this.filtersQueryString = null;

        return this;
    }

    public Object onActionFromResetFilter() {
        this.filtersQueryString = null;

        return this;
    }

}
