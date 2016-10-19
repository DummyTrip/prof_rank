package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.*;
import mk.ukim.finki.isis.model.entities.Instructor;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.criterion.Restrictions.*;
import static org.hibernate.criterion.Restrictions.eq;

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

    public List<Person> getByBibtexAuthorName(String author) {
        String lastName = author.split(", ")[0];
        String firstName = author.split(", ")[1].split(" ")[0];

        Criteria criteria = session.createCriteria(Person.class);
        List<Person> entities = criteria
                .add(eq("lastName", lastName))
                .add(like("firstName", firstName))
                .list();

        return entities;
    }

    public List<Person> getByColumn(String column, String value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(Person.class);
        List<Person> entities = criteria.add(eq(column, value)).list();

        return entities;
    }

    public List<String> listAllPersonIdentifiers() {
        List<String> identifiers = new ArrayList<String>();

        for (Person person : getAll()) {
            StringBuilder sb = buildPersonIdentifier(person);
            identifiers.add(sb.toString());
        }

        return identifiers;
    }

    private StringBuilder buildPersonIdentifier(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append(person.getFirstName());
        sb.append(" ");
        sb.append(person.getLastName());
        sb.append(" (");
        sb.append(person.getEmail());
        sb.append(")");
        return sb;
    }

    public List<Instructor> getInstructorsByPersonId (Long personId) {
        return session.createCriteria(Instructor.class)
                .add(eq("person.personId", personId)).list();
    }

    public List<Reference> getReferences(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        Criteria criteria = session.createCriteria(ReferencePerson.class);

        List<ReferencePerson> referencePersons = criteria.add(eq("person", person)).list();
        List<Reference> references = new ArrayList<Reference>();

        for (ReferencePerson referencePerson : referencePersons) {
            references.add(referencePerson.getReference());
        }

        return references;
    }

    public void setReference(Person person, Reference reference, Integer authorNum) {
        if (person == null || reference == null || authorNum == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }
        ReferencePerson referencePerson = new ReferencePerson();

        referencePerson.setPerson(person);
        referencePerson.setAuthorNum(authorNum);
        referencePerson.setReference(reference);

        session.saveOrUpdate(referencePerson);
    }

    public void setReference(Reference reference, String personIdentifier, Integer authorNum) {
        if (reference == null || personIdentifier == null || authorNum == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        ReferencePerson referencePerson = new ReferencePerson();
        Person person;

        String[] identifiers = personIdentifier.split("\\(");
        if (identifiers.length > 1) {
            List<Person> persons = getPersonFromIdentifier(personIdentifier);

            person = persons.get(0);
            referencePerson.setPerson(person);
        } else {
            referencePerson.setAuthor(personIdentifier);
        }

        referencePerson.setAuthorNum(authorNum);
        referencePerson.setReference(reference);

        session.saveOrUpdate(referencePerson);
    }

    private List<Person> getPersonFromIdentifier(String personIdentifier) {
        if (personIdentifier == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        String[] identifiers = personIdentifier.split("\\(");
        String email = identifiers[1];
        email = email.substring(0, email.length() - 1);

        return getByColumn("email", email);
    }

    public List<String> getReferenceAuthors(Reference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("Cannot filter by null value.");
        }

        List<String> authors = new ArrayList<String>();

        Criteria criteria = session.createCriteria(ReferencePerson.class);
        criteria.add(eq("reference", reference));

        List<ReferencePerson> rips = criteria.list();

        for (ReferencePerson rip : rips) {
            Person person = rip.getPerson();
            String identifier;

            if (person == null) {
                identifier = rip.getAuthor();
            } else {
                identifier = buildPersonIdentifier(person).toString();
            }

            authors.add(identifier);
        }

        return authors;
    }

    public void setReferenceMissingPerson(Reference reference, String author) {
        if (author == null || reference == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        ReferencePerson referencePerson = new ReferencePerson();

        referencePerson.setAuthor(author);
        referencePerson.setReference(reference);

        session.saveOrUpdate(referencePerson);
    }

    @CommitAfter
    public void deleteReference(Person person, Reference reference) {
        if (person == null || reference == null) {
            throw new IllegalArgumentException("Cannot persist null value.");
        }

        Criteria criteria = session.createCriteria(ReferencePerson.class);
        List<ReferencePerson> entities = criteria
                .add(eq("person", person))
                .add(eq("reference", reference))
                .list();

        if (entities.size() < 1) {
            throw new IllegalStateException("No data in database.");
        }

        ReferencePerson referencePerson = entities.get(0);
        referencePerson.setPerson(null);
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
