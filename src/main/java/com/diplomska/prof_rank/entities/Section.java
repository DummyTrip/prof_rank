package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 21-Sep-16.
 */
@Entity
public class Section {
    private Long id;

    @Validate("required")
    private String name;

//    @Column
//    @ElementCollection(targetClass = RulebookSection.class)
//    private List<RulebookSection> rulebookSections = new ArrayList<RulebookSection>();

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

//    @OneToMany(mappedBy = "section")
//    public List<RulebookSection> getRulebookSections() {
//        return rulebookSections;
//    }
//
//    public void setRulebookSections(List<RulebookSection> rulebookSections) {
//        this.rulebookSections = rulebookSections;
//    }
}
