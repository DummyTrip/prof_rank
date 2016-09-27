package com.diplomska.prof_rank.pages.User;

import com.diplomska.prof_rank.entities.ReferenceInstance;
import com.diplomska.prof_rank.entities.Report;
import com.diplomska.prof_rank.entities.Role;
import com.diplomska.prof_rank.entities.User;
import com.diplomska.prof_rank.services.ReferenceInstanceHibernate;
import com.diplomska.prof_rank.services.ReportHibernate;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import java.util.List;

/**
 * Created by Aleksandar on 25-Sep-16.
 */
public class ShowUser {
    @Persist
    @Property
    private Long userId;

    @Inject
    private UserHibernate userHibernate;

    @Persist
    @Property
    private User user;

    @Inject
    private ReferenceInstanceHibernate referenceInstanceHibernate;

    @Inject
    private ReportHibernate reportHibernate;

    @Property
    private ReferenceInstance referenceInstance;

    @Property
    private ReferenceInstance addReferenceInstance;

    @Property
    private Report report;

    @Property
    private Report addReport;

    @Property
    private BeanModel<ReferenceInstance> referenceInstanceBeanModel;

    @Property
    private BeanModel<ReferenceInstance> addReferenceInstanceBeanModel;

    @Property
    private BeanModel<Report> reportBeanModel;

    @Property
    private BeanModel<Report> addReportBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public Role getRole() {
        return user.getRole();
    }

    public List<ReferenceInstance> getReferenceInstances() {
        return userHibernate.getReferenceInstances(user);
    }

    public List<ReferenceInstance> getAddReferenceInstances() {
        return referenceInstanceHibernate.getAll();
    }

    public List<Report> getReports() {
        return userHibernate.getReports(user);
    }

    public List<Report> getAddReports() {
        return reportHibernate.getAll();
    }

    void onActivate(Long userId) {
        this.userId = userId;
    }

    Long passivate() {
        return userId;
    }

    void setupRender() throws Exception {
        this.user = userHibernate.getById(userId);

        if (user == null) {
            throw new Exception("Report " + userId + " does not exist.");
        }

        referenceInstanceBeanModel = beanModelSource.createDisplayModel(ReferenceInstance.class, messages);
        referenceInstanceBeanModel.add("referenceInstanceName", pcs.create(ReferenceInstance.class, "reference.name"));
        referenceInstanceBeanModel.add("delete", null);

        addReferenceInstanceBeanModel = beanModelSource.createDisplayModel(ReferenceInstance.class, messages);
        addReferenceInstanceBeanModel.add("referenceInstanceName", pcs.create(ReferenceInstance.class, "reference.name"));
        addReferenceInstanceBeanModel.add("add", null);

        reportBeanModel = beanModelSource.createDisplayModel(Report.class, messages);
        reportBeanModel.add("reportTitle", pcs.create(Report.class, "title"));
        reportBeanModel.add("reportTotalScore", pcs.create(Report.class, "totalScore"));
        reportBeanModel.add("reportStartDate", pcs.create(Report.class, "startDate"));
        reportBeanModel.add("reportEndDate", pcs.create(Report.class, "endDate"));
        reportBeanModel.add("delete", null);

        addReportBeanModel = beanModelSource.createDisplayModel(Report.class, messages);
        addReportBeanModel.add("reportTitle", pcs.create(Report.class, "title"));
        addReportBeanModel.add("reportTotalScore", pcs.create(Report.class, "totalScore"));
        addReportBeanModel.add("reportStartDate", pcs.create(Report.class, "startDate"));
        addReportBeanModel.add("reportEndDate", pcs.create(Report.class, "endDate"));
        addReportBeanModel.add("add", null);
    }

    public boolean isRoleNull() {
        return user.getRole() == null ? false : true;
//        return false;
    }

    @CommitAfter
    void onActionFromAddReferenceInstance(Long id) {
        ReferenceInstance entity = referenceInstanceHibernate.getById(id);

        userHibernate.setReferenceInstance(user, entity);
    }

    @CommitAfter
    void onActionFromAddReport(Long id) {
        Report entity = reportHibernate.getById(id);
//        user = userHibernate.getById(userId);

        userHibernate.setReport(user, entity);
    }

    @CommitAfter
    void onActionFromDeleteReferenceInstance(Long id) {
        ReferenceInstance entity = referenceInstanceHibernate.getById(id);

        userHibernate.deleteReferenceInstance(user, entity);
    }

    @CommitAfter
    void onActionFromDeleteReport(Long id) {
        Report entity = reportHibernate.getById(id);

        userHibernate.deleteReport(user, entity);
    }


}
