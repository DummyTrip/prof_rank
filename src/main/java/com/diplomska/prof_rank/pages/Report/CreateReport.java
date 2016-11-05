package com.diplomska.prof_rank.pages.Report;

import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.entities.Report;
import com.diplomska.prof_rank.services.PersonHibernate;
import com.diplomska.prof_rank.services.ReportHibernate;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;

/**
 * Created by Aleksandar on 05-Nov-16.
 */
public class CreateReport {
    @Property
    Date startDate;

    @Property
    Date endDate;

    @Inject
    ReportHibernate reportHibernate;

    @Inject
    PersonHibernate personHibernate;

    @InjectPage
    Index index;

    public void setupRender() {

    }

    @CommitAfter
    @OnEvent(component = "save", value = "selected")
    public Object filterReports() {
        storeReport();

        return index;
    }

    private void storeReport() {
        Report report = new Report();
        Person person = personHibernate.getById(Long.valueOf(1));

        report.setPerson(person);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setTitle("Test Report");
        report.setTotalScore(0f);
        for (Reference reference : personHibernate.getReferences(person)) {
            reportHibernate.setReference(report, reference);
        }

        reportHibernate.store(report);
    }
}
