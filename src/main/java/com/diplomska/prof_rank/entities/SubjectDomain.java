package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
@Entity
public class SubjectDomain {
    private Long id;

    private String name;

    private String identifier;

    @Column
    @ElementCollection(targetClass = InstitutionReport.class)
    private List<InstitutionReport> institutionReports = new ArrayList<InstitutionReport>();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NonVisual
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<InstitutionReport> getInstitutionReports() {
        return institutionReports;
    }

    public void setInstitutionReports(List<InstitutionReport> institutionReports) {
        this.institutionReports = institutionReports;
    }
}
