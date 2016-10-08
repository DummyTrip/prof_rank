package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.*;
import mk.ukim.finki.isis.model.entities.Instructor;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.criterion.Restrictions.*;
import static org.hibernate.criterion.Restrictions.eq;

/**
 * Created by Aleksandar on 06-Oct-16.
 */
public class PersonHibernate {
    @Inject
    Session session;

    @CommitAfter
    public void store(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.persist(person);
    }

    public List<Person> getAll() {
        return session.createCriteria(Person.class).list();
    }

    @CommitAfter
    public void update(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        session.update(person);
    }

    @CommitAfter
    public void delete(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Cannot remove null value.");
        }

        session.delete(person);
    }

    public Person getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return (Person) session.get(Person.class, id);
    }

    public Person getPersonByUsername(String username) {
        return (Person) session.createCriteria(Person.class)
                .add(eq("userName", username)).uniqueResult();
    }

    public List<Person> getByColumn(String column, String value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(Person.class);
        List<Person> entities = criteria.add(eq(column, value)).list();

        return entities;
    }

    public List<Instructor> getInstructorsByPersonId (Long personId) {
        return session.createCriteria(Instructor.class)
                .add(eq("person.personId", personId)).list();
    }

    public List<ReferenceInstance> getReferenceInstances(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(ReferenceInstancePerson.class);

        List<ReferenceInstancePerson> referenceInstancePersons = criteria.add(eq("person", person)).list();;
        List<ReferenceInstance> referenceInstances = new ArrayList<ReferenceInstance>();

        for (ReferenceInstancePerson referenceInstancePerson : referenceInstancePersons) {
            referenceInstances.add(referenceInstancePerson.getReferenceInstance());
        }

        return referenceInstances;
    }

    @CommitAfter
    public void setReferenceInstance(Person person, ReferenceInstance referenceInstance) {
        if (person == null || referenceInstance == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        ReferenceInstancePerson referenceInstancePerson = new ReferenceInstancePerson();

        referenceInstancePerson.setPerson(person);
        referenceInstancePerson.setReferenceInstance(referenceInstance);

        session.saveOrUpdate(referenceInstancePerson);
    }

    @CommitAfter
    public void deleteReferenceInstance(Person person, ReferenceInstance referenceInstance) {
        if (person == null || referenceInstance == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(ReferenceInstancePerson.class);
        List<ReferenceInstancePerson> entities = criteria
                .add(eq("person", person))
                .add(eq("referenceInstance", referenceInstance))
                .list();

        if (entities.size() < 1) {
            throw new IllegalStateException("No data in database.");
        }

        ReferenceInstancePerson referenceInstancePerson = entities.get(0);
        referenceInstancePerson.setPerson(null);
    }

    public List<Report> getReports(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(Report.class);
        List<Report> reports = criteria.add(eq("person", person)).list();

        return reports;
    }

    @CommitAfter
    public void setReports(Person person, List<Report> reports) {
        if (person == null || reports == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        for (Report report : reports) {
            report.setPerson(person);
        }
    }


    @CommitAfter
    public void setReport(Person person, Report report) {
        if (person == null || report == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        report.setPerson(person);
    }

    @CommitAfter
    public void deleteReport(Person person, Report report) {
        if (person == null || report == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        report.setPerson(null);
    }

    public List<Person> getPersonByRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        return session.createCriteria(PersonRole.class)
                .add(eq("role", role)).list();
    }

    public Role getRole(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<PersonRole> personRoles = session.createCriteria(PersonRole.class)
                .add(eq("person", person)).list();

        // Sometimes a person has no role.
        if (personRoles.size() > 0) {
            return personRoles.get(0).getRole();
        } else {
            return null;
        }
    }

    public Role getRoleByPersonId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Person person = getById(id);

        return getRole(person);
    }

    public void setRole(Person person, Role role) {
        if (person == null || role == null) {
            throw new IllegalArgumentException("Cannot persist by null value.");
        }

        List<PersonRole> personRoles = session.createCriteria(PersonRole.class)
                .add(eq("person", person)).list();
        PersonRole personRole;

        if (personRoles.size() > 0) {
            personRole = personRoles.get(0);
            personRole.setRole(role);
        } else {
            personRole = new PersonRole();
            personRole.setPerson(person);
            personRole.setRole(role);
            session.saveOrUpdate(personRole);
        }
    }

    public void deleteRole(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Cannot persist by null value.");
        }

        List<PersonRole> personRoles = session.createCriteria(PersonRole.class)
                .add(eq("person", person)).list();
        PersonRole personRole;

        if (personRoles.size() > 0) {
            personRole = personRoles.get(0);
            personRole.setRole(null);
            personRole.setPerson(null);
            session.delete(personRole);
        }
    }
}