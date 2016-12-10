package com.diplomska.prof_rank.pages;


import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.model.UserInfo;
import com.diplomska.prof_rank.services.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Start page of application prof_rank.
 */
@InstructorPage
public class Index
{
    @Inject
    ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    ReferenceHibernate referenceHibernate;

    @Inject
    PersonHibernate personHibernate;

    @Property
    ReferenceType referenceType;

    @SessionState
    private UserInfo userInfo;

    public Float getPoints() {
        Float points = 0f;

        List<Reference> references = referenceHibernate.getAll();

        // calculate points
        for (Reference reference : references) {
            Float referencePoints = reference.getReferenceType().getPoints();
            if (referencePoints != null) {
                points += reference.getReferenceType().getPoints();
            }
        }

        return points;
    }

    public List<ReferenceType> getPopularReferenceTypes() {
        return referenceTypeHibernate.getPopular(9);
    }

    public List<ReferenceType> getPopularReferenceTypesForPerson() {
        if (userInfo != null && userInfo.getPerson() != null) {
            return referenceTypeHibernate.getPopularByPerson(userInfo.getPerson(), 9);
        } else {
            return new ArrayList<ReferenceType>();
        }
    }

    public Section getSection() {
        return referenceTypeHibernate.getSections(referenceType).get(0);
    }

    public Integer getNumberOfReferences() {
        List<Reference> referencesOfCurrentReferenceType = referenceHibernate.getByReferenceType(referenceType);

        return referencesOfCurrentReferenceType.size();
    }

    public Integer getNumberOfReferencesForPerson() {
        Person person = personHibernate.getById(userInfo.getPersonId());

        List<Reference> referencesOfCurrentReferenceType = referenceHibernate.getByReferenceTypeAndPerson(referenceType, person);

        return referencesOfCurrentReferenceType.size();
    }

    public boolean isPopularReferenceTypesNull() {
        return getPopularReferenceTypes().size() > 0;
    }

    public boolean isPopularReferenceTypesForPersonNull() {
        return getPopularReferenceTypesForPerson().size() > 0;
    }
}
