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
public class User {
    private Long id;

    @Validate("required")
    private String firstName;

    @Validate("required")
    private String fatherName;

    @Validate("required")
    private String lastName;

    @Validate("required")
    private String email;

    @Column(nullable = true)
    private Role role;

    @Column
    @ElementCollection(targetClass = ReferenceInstanceUser.class)
    private List<ReferenceInstanceUser> referenceInstanceUsers = new ArrayList<ReferenceInstanceUser>();

    @Column()
    @ElementCollection(targetClass = Report.class)
    private List<Report> reports = new ArrayList<Report>();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NonVisual
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @OneToMany(mappedBy = "user")
    public List<ReferenceInstanceUser> getReferenceInstanceUsers() {
        return referenceInstanceUsers;
    }

    public void setReferenceInstanceUsers(List<ReferenceInstanceUser> referenceInstanceUsers) {
        this.referenceInstanceUsers = referenceInstanceUsers;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }
}
