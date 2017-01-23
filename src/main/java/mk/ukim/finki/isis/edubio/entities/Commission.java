package mk.ukim.finki.isis.edubio.entities;

import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;

@Entity
@Table(schema = "edubio", name = "commission")
public class Commission {
    private Long id;

    private Person person;

    private Person commissioner;

    private Integer commissionerNum;

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
    @JoinColumn(name = "commissioner_id")
    public Person getCommissioner() {
        return commissioner;
    }

    public void setCommissioner(Person commissioner) {
        this.commissioner = commissioner;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id")
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Column(name="commissioner_num")
    public Integer getCommissionerNum() {
        return commissionerNum;
    }

    public void setCommissionerNum(Integer commissionerNum) {
        this.commissionerNum = commissionerNum;
    }
}
