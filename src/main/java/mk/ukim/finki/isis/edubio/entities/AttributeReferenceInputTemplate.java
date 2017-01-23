package mk.ukim.finki.isis.edubio.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;

@Entity
@Table(schema = "edubio", name = "attribute_reference_input_template")
public class AttributeReferenceInputTemplate {
    private Long id;

    private ReferenceInputTemplate referenceInputTemplate;

    private Attribute attribute;

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
    @JoinColumn(name = "reference_input_template_id")
    public ReferenceInputTemplate getReferenceInputTemplate() {
        return referenceInputTemplate;
    }

    public void setReferenceInputTemplate(ReferenceInputTemplate referenceInputTemplate) {
        this.referenceInputTemplate = referenceInputTemplate;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "attribute_id")
    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
}
