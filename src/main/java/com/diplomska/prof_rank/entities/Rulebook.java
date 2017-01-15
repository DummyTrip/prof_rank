package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "edubio", name = "rulebooks")
public class Rulebook {
    private Long id;

    @Validate("required")
    private String name;

    @Column(name = "rulebook_section_ids")
    @ElementCollection(targetClass = RulebookSection.class)
    private List<RulebookSection> rulebookSections = new ArrayList<RulebookSection>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "rulebook")
    public List<RulebookSection> getRulebookSections() {
        return rulebookSections;
    }

    public void setRulebookSections(List<RulebookSection> rulebookSections) {
        this.rulebookSections = rulebookSections;
    }
}
