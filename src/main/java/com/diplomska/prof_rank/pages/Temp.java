package com.diplomska.prof_rank.pages;

import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.ReferenceTypeHibernate;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;
import org.w3c.dom.Attr;

import java.util.List;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
public class Temp {
    @Property
    private Reference tmp;

    @Inject
    ReferenceHibernate referenceHibernate;

    public List<Reference> getTemp() {
        return referenceHibernate.getAll();
    }

    @Property
    private Attribute attribute;

    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

    public List<Attribute> getAttributes() {
        ReferenceType referenceType = referenceHibernate.getReferenceType(tmp);
        return referenceTypeHibernate.getAttributes(referenceType);
    }

    public boolean isTextInput() {
        return attribute.getInputType().equals("text") ? true :false;
    }

    @Property
    private String testVal;
}
