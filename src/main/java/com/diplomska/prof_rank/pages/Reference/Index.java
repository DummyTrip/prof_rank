package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.ReferenceInstanceHibernate;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

/**
 * Created by Aleksandar on 01-Oct-16.
 */
public class Index {
    @Inject
    ReferenceHibernate referenceHibernate;

    @Property
    Reference reference;
//
//    @Persist
//    @Property
//    Long referenceId;

    public List<Reference> getReferences() {
        return referenceHibernate.getAll();
    }
//
//    void onActivate(Long referenceId) {
//        this.referenceId = referenceId;
//    }
//
//    Long passivate() {
//        return referenceId;
//    }
//
//    void setupRender() throws Exception {
//        this.reference = referenceHibernate.getById(referenceId);
//    }
}
