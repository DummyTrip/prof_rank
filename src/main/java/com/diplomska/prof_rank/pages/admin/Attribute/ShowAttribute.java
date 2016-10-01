package com.diplomska.prof_rank.pages.admin.Attribute;

import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.AttributeHibernate;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by Aleksandar on 25-Sep-16.
 */
public class ShowAttribute {
    @Persist
    @Property
    private Long attributeId;

    @Inject
    private AttributeHibernate attributeHibernate;

    @Persist
    @Property
    private Attribute attribute;

    void onActivate(Long attributeId) {
        this.attributeId = attributeId;
    }

    Long passivate() {
        return attributeId;
    }

    void setupRender() throws Exception {
        this.attribute = attributeHibernate.getById(attributeId);
    }
}
