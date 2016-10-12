package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 12-Oct-16.
 */
@InstructorPage
public class AddReference {
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

    @Inject
    UserHibernate userHibernate;

    public String getReferenceNameQueryString() {
        return referenceNameQueryString;
    }

    public List<Reference> getReferences() {
        if (referenceNameQueryString != null) {
            return referenceHibernate.getByColumn("name", referenceNameQueryString);
        } else {
            return referenceHibernate.getPopular(Integer.MAX_VALUE);
        }
    }

    List<String> onProvideCompletionsFromSearchName(String partial) {
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

        refs = new ArrayList<Reference>();
    }

    public Object onSuccessFromForm() {
        Link link = this.set(referenceName);

        return link;
    }

    @Property
    int pageNumber;

    private static final Integer PageSize = 20;

    @Persist
    @Property
    List<Reference> refs;

    // ajax call, used fpr paging of References
    @OnEvent("nextPage")
    List<Reference> moreValues() throws InterruptedException {
        Integer first = Integer.valueOf(pageNumber) * PageSize;
        Thread.sleep(200);

        int size = refs.size();
        List<Reference> newInstances;
        if (referenceNameQueryString != null) {
            newInstances = referenceHibernate.getByColumn("name", referenceNameQueryString);
        } else {
            newInstances = referenceHibernate.getAll(first, PageSize);
        }
        refs.addAll(newInstances);

        return refs;
    }
}
