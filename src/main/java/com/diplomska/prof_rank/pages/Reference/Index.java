package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.*;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aleksandar on 01-Oct-16.
 */
@InstructorPage
public class Index {
    @Inject
    ReferenceTypeHibernate referenceTypeHibernate;

    @Property
    ReferenceType referenceType;

    @Property
    String referenceTypeName;

    @ActivationRequestParameter(value = "name")
    private String referenceTypeNameQueryString;

    @Inject
    private PageRenderLinkSource pageRenderLinkSource;

    @Persist
    private List<String> referenceTypeNames;

    @Inject
    PersonHibernate personHibernate;

    public String getreferenceTypeNameQueryString() {
        return referenceTypeNameQueryString;
    }

    public List<ReferenceType> getReferences() {
        if (referenceTypeNameQueryString != null) {
            return referenceTypeHibernate.getByColumn("name", referenceTypeNameQueryString);
        } else {
            return referenceTypeHibernate.getPopular(Integer.MAX_VALUE);
        }
    }

    List<String> onProvideCompletionsFromSearchName(String partial) {
        List<String> matches = new ArrayList<String>();
        partial = partial.toUpperCase();

        for (String name : referenceTypeNames) {
            if (name.toUpperCase().startsWith(partial)) {
                matches.add(name);
            }
        }

        return matches;
    }

    public Link set(String referenceTypeName) {
        this.referenceTypeNameQueryString = referenceTypeName;

        return pageRenderLinkSource.createPageRenderLink(this.getClass());
    }

    void setupRender() {
        if (referenceTypeNameQueryString != null) {
            referenceTypeName = referenceTypeNameQueryString;
        } else {
            referenceTypeName = "";
        }
        referenceTypeNames = referenceTypeHibernate.getAllNames();

        refs = new ArrayList<ReferenceType>();
        firstPageRefs = new ArrayList<ReferenceType>();

        if (sections == null) {
            sections = sectionHibernate.getAll();
        }

        if (selectedCheckboxes == null) {
            selectedCheckboxes = new HashMap<Long, Integer>();
            selectedSections = new ArrayList<Section>();

            for (Section section : sections) {
                selectedCheckboxes.put(section.getId(), 1);
                selectedSections.add(section);
            }
        }
    }

    public Object onSuccessFromForm() {
        Link link = this.set(referenceTypeName);

        return link;
    }

    @Property
    int pageNumber;

    private static final Integer PageSize = 20;

    @Persist
    @Property
    List<ReferenceType> refs;

    @Persist
    @Property
    List<ReferenceType> firstPageRefs;

    // ajax call, used fpr paging of References
    @OnEvent("nextPage")
    List<ReferenceType> moreValues() throws InterruptedException {
        Integer first = Integer.valueOf(pageNumber) * PageSize;
        Thread.sleep(200);

        List<ReferenceType> newInstances;

        newInstances = referenceTypeHibernate.getPopularByPerson(personHibernate.getById(Long.valueOf(1)),
                Integer.MAX_VALUE, selectedSections, referenceTypeName);

        // This fix makes sure the items from page 0 don't duplicate
        // by separating the items of page 0 from the rest.
        if (pageNumber == 0) {
            firstPageRefs.addAll(newInstances);
            return firstPageRefs;
        } else {
            refs.addAll(newInstances);

            return refs;
        }
    }

    @InjectComponent
    Zone sectionFilterZone;

    @Inject
    SectionHibernate sectionHibernate;

    @Inject
    RulebookHibernate rulebookHibernate;

    @Persist
    @Property
    List<Section> sections;

    @Persist
    @Property
    List<Section> selectedSections;

    @Property
    Section section;

    @Persist
    @Property
    Map<Long, Integer> selectedCheckboxes;

    @Inject
    AjaxResponseRenderer ajaxResponseRenderer;

    public boolean isSelectedCheckbox() {
        return selectedCheckboxes.get(section.getId()) == 0 ? false : true;
    }

    public boolean isSectionNum() {
        return sections.size() > 0 ? true : false;
    }

    @OnEvent(component = "addSection", value = "selected")
    public void addSection(Long sectionId) {
        selectedCheckboxes.put(sectionId, 1);

        selectedSections = new ArrayList<Section>();
        for (Long id : selectedCheckboxes.keySet()) {
            if (selectedCheckboxes.get(id).equals(1)) {
                selectedSections.add(sectionHibernate.getById(id));
            }
        }
    }

    @OnEvent(component = "removeSection", value = "selected")
    public void removeSection(Long sectionId) {
        selectedCheckboxes.put(sectionId, 0);

        selectedSections = new ArrayList<Section>();
        for (Long id : selectedCheckboxes.keySet()) {
            if (selectedCheckboxes.get(id).equals(1)) {
                selectedSections.add(sectionHibernate.getById(id));
            }
        }
    }

    private void updateSections() {
        refs = new ArrayList<ReferenceType>();

        for (Long sectionId : selectedCheckboxes.keySet()) {
            Section section = sectionHibernate.getById(sectionId);
            refs.addAll(sectionHibernate.getReferenceTypes(section));
        }
    }
}
