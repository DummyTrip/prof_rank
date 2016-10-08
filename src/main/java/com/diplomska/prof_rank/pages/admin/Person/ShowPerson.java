package com.diplomska.prof_rank.pages.admin.Person;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.ReferenceInstanceHibernate;
import com.diplomska.prof_rank.services.ReportHibernate;
import com.diplomska.prof_rank.services.PersonHibernate;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 06-Oct-16.
 */
@AdministratorPage
public class ShowPerson {
    @Persist
    @Property
    private Long personId;

    @Inject
    private PersonHibernate personHibernate;

    @Persist
    @Property
    private Person person;

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

//    public Role getRole() {
//        return person.getRole();
//    }

    public List<ReferenceInstance> getReferenceInstances() {
        return personHibernate.getReferenceInstances(person);
    }

    public List<ReferenceInstance> getAddReferenceInstances() {
        return referenceInstanceHibernate.getAll();
    }

    public List<Report> getReports() {
        return personHibernate.getReports(person);
    }

    public List<Report> getAddReports() {
        return reportHibernate.getAll();
    }

    void onActivate(Long personId) {
        this.personId = personId;
    }

    Long passivate() {
        return personId;
    }

    void setupRender() throws Exception {
        this.person = personHibernate.getById(personId);

        if (person == null) {
            throw new Exception("Report " + personId + " does not exist.");
        }

        referenceInstanceBeanModel = beanModelSource.createDisplayModel(ReferenceInstance.class, messages);
        referenceInstanceBeanModel.add("referenceInstanceName", pcs.create(ReferenceInstance.class, "reference.name"));
        referenceInstanceBeanModel.add("show", null);
        referenceInstanceBeanModel.add("delete", null);

        addReferenceInstanceBeanModel = beanModelSource.createDisplayModel(ReferenceInstance.class, messages);
        addReferenceInstanceBeanModel.add("referenceInstanceName", pcs.create(ReferenceInstance.class, "reference.name"));
        addReferenceInstanceBeanModel.add("show", null);
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

//    public boolean isRoleNull() {
//        return person.getRole() == null ? false : true;
////        return false;
//    }

    @CommitAfter
    void onActionFromAddReferenceInstance(Long id) {
        ReferenceInstance entity = referenceInstanceHibernate.getById(id);

        personHibernate.setReferenceInstance(person, entity);
    }

    @CommitAfter
    void onActionFromAddReport(Long id) {
        Report entity = reportHibernate.getById(id);
//        person = personHibernate.getById(personId);

        personHibernate.setReport(person, entity);
    }

    @CommitAfter
    void onActionFromDeleteReferenceInstance(Long id) {
        ReferenceInstance entity = referenceInstanceHibernate.getById(id);

        personHibernate.deleteReferenceInstance(person, entity);
    }

    @CommitAfter
    void onActionFromDeleteReport(Long id) {
        Report entity = reportHibernate.getById(id);

        personHibernate.deleteReport(person, entity);
    }

}
