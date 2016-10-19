package com.diplomska.prof_rank.entities;

import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;

/**
 * Created by Aleksandar on 06-Oct-16.
 */
@Entity
public class ReferencePerson {
    private Long id;

    private Reference reference;

    private Person person;

    private String author;

    private Integer authorNum;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NonVisual
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reference_id")
    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id")
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getAuthorNum() {
        return authorNum;
    }

    public void setAuthorNum(Integer authorNum) {
        this.authorNum = authorNum;
    }
}
