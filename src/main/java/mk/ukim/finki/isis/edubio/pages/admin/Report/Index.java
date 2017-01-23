package mk.ukim.finki.isis.edubio.pages.admin.Report;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.Report;
import mk.ukim.finki.isis.edubio.services.ReportHibernate;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
@AdministratorPage
public class Index {
    @Inject
    private ReportHibernate reportHibernate;

    @Property
    private Report report;

    @Property
    private BeanModel<Report> reportBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<Report> getReports() {
        return reportHibernate.getAll();
    }

    void setupRender() {
        reportBeanModel = beanModelSource.createDisplayModel(Report.class, messages);
        reportBeanModel.include("title", "totalScore", "startDate", "endDate");
        reportBeanModel.add("show", null);
        reportBeanModel.add("edit", null);
        reportBeanModel.add("delete", null);
    }

    @CommitAfter
    void onActionFromDelete(Long reportId) {
        report = reportHibernate.getById(reportId);
        reportHibernate.delete(report);
    }
}
