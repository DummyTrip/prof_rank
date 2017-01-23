package mk.ukim.finki.isis.edubio.entities;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(schema = "edubio", name = "attribute_reference")
public class AttributeReference {
    private Long id;

    private String value;

    private Integer index;

    private boolean display = false;

    private Attribute attribute;

    private Reference reference;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonVisual
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Type(type = "text")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "attribute_id")
    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reference_id")
    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }
}
