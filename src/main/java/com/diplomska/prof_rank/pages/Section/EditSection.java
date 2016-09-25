package com.diplomska.prof_rank.pages.Section;

import com.diplomska.prof_rank.entities.Section;
import com.diplomska.prof_rank.services.SectionHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.diplomska.prof_rank.pages.Section.Index;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class EditSection {
    @Persist
    private Long sectionId;

    @Property
    private Section section;

    @InjectComponent
    private Form form;

    @Inject
    private SectionHibernate sectionHibernate;

    @InjectPage
    private Index index;

    void onActivate(Long sectionId) {
        this.sectionId = sectionId;
    }

    Long passivate() {
        return sectionId;
    }

    void onPrepareForRender() throws Exception {
        if (form.isValid()) {
            section = findSection(sectionId);

            if (section == null) {
                throw new Exception("Section " + sectionId + " does not exist.");
            }
        }
    }

    void onPrepareForSubmit() {
        section = findSection(sectionId);

        if (section == null) {
            form.recordError("Person has been deleted by another process.");
            // Instantiate an empty section to avoid NPE in the BeanEditForm.
            section = new Section();
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
        sectionHibernate.update(section);

        return index;
    }

    void onRefresh() {
        // By doing nothing the page will be displayed afresh.
    }

    private Section findSection(Long sectionId) {
        Section section = sectionHibernate.getById(sectionId);

        if (section == null) {
            throw new IllegalStateException("No data in database.");
        }

        return section;
    }
}