package com.diplomska.prof_rank.pages.Report;

import com.diplomska.prof_rank.entities.Report;
import com.diplomska.prof_rank.services.ReportHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.diplomska.prof_rank.pages.Report.Index;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class EditReport {
    @Persist
    private Long reportId;

    @Property
    private Report report;

    @InjectComponent
    private Form form;

    @Inject
    private ReportHibernate reportHibernate;

    @InjectPage
    private Index index;

    void onActivate(Long reportId) {
        this.reportId = reportId;
    }

    Long passivate() {
        return reportId;
    }

    void onPrepareForRender() throws Exception {
        if (form.isValid()) {
            report = findReport(reportId);

            if (report == null) {
                throw new Exception("Report " + reportId + " does not exist.");
            }
        }
    }

    void onPrepareForSubmit() {
        report = findReport(reportId);

        if (report == null) {
            form.recordError("Person has been deleted by another process.");
            // Instantiate an empty report to avoid NPE in the BeanEditForm.
            report = new Report();
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
//        report.setUloga(uloga);
        reportHibernate.update(report);

        return index;
    }

    void onRefresh() {
        // By doing nothing the page will be displayed afresh.
    }

    private Report findReport(Long reportId) {
        Report report = reportHibernate.getById(reportId);

        if (report == null) {
            throw new IllegalStateException("No data in database.");
        }

        return report;
    }
}
