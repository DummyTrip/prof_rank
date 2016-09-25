package com.diplomska.prof_rank.pages.Rulebook;

import com.diplomska.prof_rank.entities.Rulebook;
import com.diplomska.prof_rank.services.RulebookHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.diplomska.prof_rank.pages.Rulebook.Index;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class EditRulebook {
    @Persist
    private Long rulebookId;

    @Property
    private Rulebook rulebook;

    @InjectComponent
    private Form form;

    @Inject
    private RulebookHibernate rulebookHibernate;

    @InjectPage
    private Index index;

    void onActivate(Long rulebookId) {
        this.rulebookId = rulebookId;
    }

    Long passivate() {
        return rulebookId;
    }

    void onPrepareForRender() throws Exception {
        if (form.isValid()) {
            rulebook = findRulebook(rulebookId);

            if (rulebook == null) {
                throw new Exception("Rulebook " + rulebookId + " does not exist.");
            }
        }
    }

    void onPrepareForSubmit() {
        rulebook = findRulebook(rulebookId);

        if (rulebook == null) {
            form.recordError("Person has been deleted by another process.");
            // Instantiate an empty rulebook to avoid NPE in the BeanEditForm.
            rulebook = new Rulebook();
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
        rulebookHibernate.update(rulebook);

        return index;
    }

    void onRefresh() {
        // By doing nothing the page will be displayed afresh.
    }

    private Rulebook findRulebook(Long rulebookId) {
        Rulebook rulebook = rulebookHibernate.getById(rulebookId);

        if (rulebook == null) {
            throw new IllegalStateException("No data in database.");
        }

        return rulebook;
    }
}
