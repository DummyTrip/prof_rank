package mk.ukim.finki.isis.edubio.entities;

import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;

import javax.persistence.*;

@Entity
@Table(schema = "edubio", name = "person_role")
public class PersonRole {
    private Long id;

    @Validate("required")
    private Person person;

    @Validate("required")
    private Role role;

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
    @JoinColumn(name = "person_id")
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
