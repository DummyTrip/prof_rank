package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
@Entity
public class Institution {
    private Long id;

    private String name;

    private String city;

    private String country;
//
//    @Column
//    @ElementCollection(targetClass = InstitutionReport.class)
//    private List<InstitutionReport> institutionReports = new ArrayList<InstitutionReport>();

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

//    public List<InstitutionReport> getInstitutionReports() {
//        return institutionReports;
//    }
//
//    public void setInstitutionReports(List<InstitutionReport> institutionReports) {
//        this.institutionReports = institutionReports;
//    }
}
