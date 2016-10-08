package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.ReferenceInstanceHibernate;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 01-Oct-16.
 */
@InstructorPage
public class Index {
    @Inject
    ReferenceHibernate referenceHibernate;

    @Property
    Reference reference;

    @Property
    String referenceName;

    @ActivationRequestParameter(value = "name")
    private String referenceNameQueryString;

    @Inject
    private PageRenderLinkSource pageRenderLinkSource;

    @Persist
    private List<String> referenecNames;

    public String getReferenceNameQueryString() {
        return referenceNameQueryString;
    }

    public List<Reference> getReferences() {
        if (referenceNameQueryString != null) {
            return referenceHibernate.getByColumn("name", referenceNameQueryString);
        } else {
            return referenceHibernate.getAll();
        }
    }

    List<String> onProvideCOmpletionsFromSearchName(String partial) {
        List<String> matches = new ArrayList<String>();
        partial = partial.toUpperCase();

        for (String name : referenecNames) {
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

    void setupRender() {
        this.referenceName = referenceNameQueryString;
        this.referenecNames = referenceHibernate.getAllNames();
    }

    public Object onSuccessFromForm() {
        Link link = this.set(referenceName);

        return link;
    }
}
