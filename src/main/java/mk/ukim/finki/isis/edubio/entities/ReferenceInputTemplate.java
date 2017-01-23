package mk.ukim.finki.isis.edubio.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "edubio", name = "reference_input_templates")
public class ReferenceInputTemplate {
    private Long id;

    private String name;

    @Column(name = "attribute_reference_input_template_ids")
    @ElementCollection(targetClass = AttributeReferenceInputTemplate.class)
    private List<AttributeReferenceInputTemplate> attributeReferenceInputTemplates = new ArrayList<AttributeReferenceInputTemplate>();

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

    @OneToMany(mappedBy = "referenceInputTemplate")
    public List<AttributeReferenceInputTemplate> getAttributeReferenceInputTemplates() {
        return attributeReferenceInputTemplates;
    }

    public void setAttributeReferenceInputTemplates(List<AttributeReferenceInputTemplate> attributeReferenceInputTemplates) {
        this.attributeReferenceInputTemplates = attributeReferenceInputTemplates;
    }

    @Override
    public String toString() {
        return name;
    }
}
