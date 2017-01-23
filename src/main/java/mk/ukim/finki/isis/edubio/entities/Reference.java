package mk.ukim.finki.isis.edubio.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "edubio", name = "references")
public class Reference {
    private Long id;

    private ReferenceType referenceType;

    @Column(name = "attribute_reference_ids")
    @ElementCollection(targetClass = AttributeReference.class)
    private List<AttributeReference> attributeReferences = new ArrayList<AttributeReference>();

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
    @JoinColumn(name = "reference_type_id")
    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    @OneToMany(mappedBy = "reference")
    public List<AttributeReference> getAttributeReferences() {
        return attributeReferences;
    }

    public void setAttributeReferences(List<AttributeReference> attributeReferences) {
        this.attributeReferences = attributeReferences;
    }

    @Override
    public String toString() {
        return getReferenceType().getName();
    }
}
