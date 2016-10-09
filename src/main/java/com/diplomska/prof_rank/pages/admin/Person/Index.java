package com.diplomska.prof_rank.pages.admin.Person;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.services.ExcelWorkbook;
import com.diplomska.prof_rank.services.PersonHibernate;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import java.util.List;

/**
 * Created by Aleksandar on 06-Oct-16.
 */
@AdministratorPage
public class Index {
    @Inject
    private PersonHibernate personHibernate;

    @Inject
    ExcelWorkbook excelWorkbook;

    @Property
    private Person person;

    @Property
    private BeanModel<Person> personBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<Person> getPersons() {
        return personHibernate.getAll();
    }

    void setupRender() {
        personBeanModel = beanModelSource.createDisplayModel(Person.class, messages);
        personBeanModel.include("userName", "firstName", "middleName", "lastName", "email");
        personBeanModel.add("show", null);
        personBeanModel.add("edit", null);
        personBeanModel.add("delete", null);
    }

    @CommitAfter
    void onActionFromDelete(Long personId) throws Exception{
        person = personHibernate.getById(personId);
        personHibernate.delete(person);
    }
}
