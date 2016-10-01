package com.diplomska.prof_rank.pages.admin.Report;

import com.diplomska.prof_rank.entities.Report;
import com.diplomska.prof_rank.services.RoleHibernate;
import com.diplomska.prof_rank.services.ReportHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class CreateReport {
    @Property
    private Report report;

    @Inject
    private ReportHibernate reportHibernate;

    @Inject
    private RoleHibernate roleHibernate;

    @InjectPage
    private Index index;

    @InjectComponent
    private Form form;

    void onPrepareForRender() throws Exception {
        // If fresh start, make sure there's a Person object available.
        if (form.isValid()) {
            report = new Report();
        }
    }

    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        report = new Report();
    }

    @CommitAfter
    Object onSuccess() {
        reportHibernate.store(report);

        return index;
    }
}
