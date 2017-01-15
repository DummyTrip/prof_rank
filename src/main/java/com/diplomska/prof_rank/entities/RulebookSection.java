package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;

import javax.persistence.*;

@Entity
@Table(schema = "edubio", name = "rulebook_section")
public class RulebookSection {
    private Long id;

    @Validate("required")
    private Rulebook rulebook;

    @Validate("required")
    private Section section;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonVisual
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rulebook_id")
    public Rulebook getRulebook() {
        return rulebook;
    }

    public void setRulebook(Rulebook rulebook) {
        this.rulebook = rulebook;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "section_id")
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }
}
