package com.diplomska.prof_rank.pages.admin.Attribute;

import com.diplomska.prof_rank.entities.Attribute;
import com.diplomska.prof_rank.services.AttributeHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class EditAttribute {
    @Persist
    private Long attributeId;

    @Property
    private Attribute attribute;

    @InjectComponent
    private Form form;

    @Inject
    private AttributeHibernate attributeHibernate;

    @InjectPage
    private Index index;

    void onActivate(Long attributeId) {
        this.attributeId = attributeId;
    }

    Long passivate() {
        return attributeId;
    }

    void onPrepareForRender() throws Exception {
        if (form.isValid()) {
            attribute = findAttribute(attributeId);

            if (attribute == null) {
                throw new Exception("Attribute " + attributeId + " does not exist.");
            }
        }
    }

    void onPrepareForSubmit() {
        attribute = findAttribute(attributeId);

        if (attribute == null) {
            form.recordError("Person has been deleted by another process.");
            // Instantiate an empty attribute to avoid NPE in the BeanEditForm.
            attribute = new Attribute();
        }
    }

    void onValidateFromForm() {
        if (form.getHasErrors()) {
            // server-side error
            return;
        }
    }


    @CommitAfter
    Object onSuccess() {
//        attribute.setUloga(uloga);
        attributeHibernate.update(attribute);

        return index;
    }

    void onRefresh() {
        // By doing nothing the page will be displayed afresh.
    }

    private Attribute findAttribute(Long attributeId) {
        Attribute attribute = attributeHibernate.getById(attributeId);

        if (attribute == null) {
            throw new IllegalStateException("No data in database.");
        }

        return attribute;
    }
}
