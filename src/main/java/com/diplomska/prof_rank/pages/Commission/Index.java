package com.diplomska.prof_rank.pages.Commission;

import com.diplomska.prof_rank.entities.Attribute;
import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.services.PersonHibernate;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;

/**
 * Created by Aleksandar on 28-Oct-16.
 */
public class Index {
    @Persist
    @Property
    boolean missingCommissioners;

    @Persist
    @Property
    private Map<String, String> testMap;

    @Persist
    @Property
    List<String> commissioners;

    @Property
    String commissioner;

    @Persist
    @Property
    String person;

    @Inject
    PersonHibernate personHibernate;

    @InjectPage
    com.diplomska.prof_rank.pages.Index index;

    @Persist
    @Property
    boolean showCommission;

    public void setupRender() {
        if (testMap == null) {
            testMap = new HashMap<String, String>();
        }

        if (person == null) {
            person = "person";
            testMap.put(person, "");
        }
    }

    private void resetPersistedVariables() {
        testMap = null;
        missingCommissioners = false;
        commissioners = null;
        person = null;
        showCommission = false;
    }

    public boolean isTestMapPopulated() {
        return testMap.size() > 0 ? true : false;
    }

    @OnEvent(component = "deleteCommissioner", value = "selected")
    public void deleteCommissioner(String commissioner) {
        for (Iterator<String> iterator = commissioners.iterator(); iterator.hasNext(); ) {
            if (commissioner.equals(iterator.next())) {
                iterator.remove();
            }
        }

        testMap.remove(commissioner);
    }

    @OnEvent(component = "addCommissioner", value = "selected")
    void createNewCommissioner(String value) {
        addCommissioner("");
    }

    void addCommissioner(String value) {
        String lastCommissioner;
        if (commissioners.size() > 0){
            lastCommissioner = commissioners.get(commissioners.size() - 1);
        } else{
            lastCommissioner = "commissioner 0";
        }
        Integer newCommissionerNumber = Integer.valueOf(lastCommissioner.split(" ")[1]) + 1;
        String commissionerName = "commissioner " + newCommissionerNumber;
        addCommissionerToForm(commissionerName, value);
    }


    private void addCommissionerToForm(String key, String value) {
        if (!testMap.containsKey(key)) {
            testMap.put(key, value);
            commissioners.add(key);
        } else {
            testMap.put(key, value);
        }
    }


    @CommitAfter
    @OnEvent(component = "save", value = "selected")
    Object saveCommission() {
        Map<String, String> tempMap = new HashMap<String, String>(testMap);
        if (tempMap.get(person) == null) {
            return missingCommissioner();
        }

        List<String> commissionNames = new ArrayList<String>();
        Person academic;
        List<Person> persons = personHibernate.getPersonFromIdentifier(tempMap.get(person));
        if (persons.size() > 0) {
            academic = persons.get(0);
            tempMap.remove(person);
        } else {
            return missingCommissioner();
        }

        for (String commissioner : commissioners) {
            commissionNames.add(tempMap.get(commissioner));
            tempMap.remove(commissioner);
        }

        for (String commissionerName : commissionNames) {
            if (commissionerName == null) {
                return missingCommissioner();
            }

            persons = personHibernate.getPersonFromIdentifier(commissionerName);
            if (persons.size() > 0) {
                Person commissioner = persons.get(0);
                personHibernate.setCommissioner(academic, commissioner, commissionNames.indexOf(commissionerName));
            } else {
                return missingCommissioner();
            }
        }

        resetPersistedVariables();

        return index;
    }

    @OnEvent(component = "editCommission", value = "selected")
    Object editCommission() {
        List<Person> commission;
        if (testMap.get(person) == null) {
            return missingCommissioner();
        }

        List<Person> persons = personHibernate.getPersonFromIdentifier(testMap.get(person));
        if (persons.size() > 0) {
            commission = personHibernate.getCommission(persons.get(0));
        } else {
            return missingCommissioner();
        }

        commissioners = new ArrayList<String>();
        if (commission.size() == 0){
            String commissionerName = "commissioner 1";
            commissioners.add(commissionerName );
            testMap.put(commissionerName , "");
        }

        for (Person person : commission) {
            addCommissioner(personHibernate.buildPersonIdentifier(person).toString());
        }

        showCommission = true;

        return this;
    }

    Object missingCommissioner() {
        missingCommissioners = true;
        return this;
    }

    Object onActionFromCancel() {
        resetPersistedVariables();

        return index;
    }
    
}
