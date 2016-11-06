package com.diplomska.prof_rank.pages.Report;

import com.diplomska.prof_rank.entities.Report;
import com.diplomska.prof_rank.model.UserInfo;
import com.diplomska.prof_rank.services.PersonHibernate;
import com.diplomska.prof_rank.services.ReportHibernate;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;

import java.util.*;

/**
 * Created by Aleksandar on 31-Oct-16.
 */
public class Index {
    @Property
    List<Report> reports;

    @Property
    Report report;

    @Inject
    ReportHibernate reportHibernate;

    @Inject
    PersonHibernate personHibernate;

    @Property
    String reportName;

    @ActivationRequestParameter(value = "title")
    String reportNameQueryString;

    @Inject
    PageRenderLinkSource pageRenderLinkSource;

    @Persist
    @Property
    Date startDate;

    @Persist
    @Property
    Date endDate;

    @SessionState
    UserInfo userInfo;

    public void setupRender() {
        if (reportNameQueryString != null) {
            reportName = reportNameQueryString;
            reports = reportHibernate.getByColumn("title", reportNameQueryString);
        }

        if (startDate != null || endDate != null) {
            // this is correct
            reports = personHibernate.getReports(userInfo.getPerson(), startDate, endDate);
            // this is not correct. temp code.
//            reports = reportHibernate.getAll();
        }

        if (reports == null) {
            reports = reportHibernate.getAll();
        }
    }

    public List<String> onProvideCompletionsFromSearchName(String partial) {
        List<String> matches = new ArrayList<String>();
        partial = partial.toUpperCase();

        for (Report report: reportHibernate.getAll()) {
            String title = report.getTitle();
            if (title.toUpperCase().startsWith(partial)) {
                matches.add(title);
            }
        }

        return matches;
    }

    public Object onSuccessFromForm() {
        Link link = this.set(reportName);

        return link;
    }

    public Link set(String reportName) {
        this.reportNameQueryString = reportName;

        return pageRenderLinkSource.createPageRenderLink(this.getClass());
    }

    @OnEvent(component = "filter", value = "selected")
    public Object filterReports() {
        return this;
    }

    public void onActionFromResetFilter() {
        startDate = null;
        endDate = null;
        reportNameQueryString = null;
    }
}
