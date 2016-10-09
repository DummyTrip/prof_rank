package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.ReferenceInstanceHibernate;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

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

    @ActivationRequestParameter(value = "name")
    private String referenceNameQueryString;

    @Inject
    private PageRenderLinkSource pageRenderLinkSource;

    @Persist
    private List<String> displayNames;

    public List<ReferenceInstance> getReferenceInstances() {
        User user = userHibernate.getById(Long.valueOf(1));
        if (user != null) {
//            return referenceInstanceHibernate.getByReferenceAndUser(reference, user);
            List<ReferenceInstance> referenceInstances = referenceInstanceHibernate.getByReference(reference);
            return referenceInstances;
        } else {
            return new ArrayList<ReferenceInstance>();
        }
//        return referenceInstanceHibernate.getByReference(reference);
    }

    public String getDisplayName() {
        return referenceInstanceHibernate.getDisplayName(referenceInstance);
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

    List<String> onProvideCOmpletionsFromSearchName(String partial) {
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

    public Object onSuccessFromForm() {
        Link link = this.set(referenceName);

        return link;
    }

    void setupRender() throws Exception {
        this.reference = referenceHibernate.getById(referenceId);
        this.referenceName = referenceNameQueryString;
        this.displayNames = referenceInstanceHibernate.getAllDisplayNames();

        refInstances = new ArrayList<ReferenceInstance>();
    }

    @Property
    int pageNumber;

    private static final Integer PageSize = 15;

    @Persist
    @Property
    List<ReferenceInstance> refInstances;

    @OnEvent("nextPage")
    List<ReferenceInstance> moreValues() throws InterruptedException {
        Integer first = Integer.valueOf(pageNumber) * PageSize;
        Thread.sleep(200);

        int size = refInstances.size();
        List<ReferenceInstance> newInstances = referenceInstanceHibernate.getByReference(reference, first, PageSize);
        refInstances.addAll(newInstances);

        return refInstances;
    }

}
