package com.diplomska.prof_rank.pages.admin.Attribute;

import com.diplomska.prof_rank.entities.Attribute;
import com.diplomska.prof_rank.services.AttributeHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class CreateAttribute {
    @Property
    private Attribute attribute;

    @Inject
    private AttributeHibernate attributeHibernate;

    @InjectPage
    private Index index;

    @InjectComponent
    private Form form;

    void onPrepareForRender() throws Exception {
        // If fresh start, make sure there's a Person object available.
        if (form.isValid()) {
            attribute = new Attribute();
        }
    }

    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        attribute = new Attribute();
    }

    @CommitAfter
    Object onSuccess() {
        attributeHibernate.store(attribute);

        return index;
    }
}
