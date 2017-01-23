package mk.ukim.finki.isis.edubio.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "edubio", name = "reference_types")
public class ReferenceType {
    private Long id;

    private String name;

    private Float points;

    private RulebookSection rulebookSection;

    private ReferenceInputTemplate referenceInputTemplate;

    @Column(name = "attribute_reference_type_ids")
    @ElementCollection(targetClass = AttributeReferenceType.class)
    private List<AttributeReferenceType> attributeReferenceTypes = new ArrayList<AttributeReferenceType>();

    @Column(name = "reference_type_rulebook_section_ids")
    @ElementCollection(targetClass = ReferenceTypeRulebookSection.class)
    private List<ReferenceTypeRulebookSection> referenceTypeRulebookSections = new ArrayList<ReferenceTypeRulebookSection>();

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

    public Float getPoints() {
        return points;
    }

    public void setPoints(Float points) {
        this.points = points;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rulebook_section_id")
    public RulebookSection getRulebookSection() {
        return rulebookSection;
    }

    public void setRulebookSection(RulebookSection rulebookSection) {
        this.rulebookSection = rulebookSection;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reference_input_template_id")
    public ReferenceInputTemplate getReferenceInputTemplate() {
        return referenceInputTemplate;
    }

    public void setReferenceInputTemplate(ReferenceInputTemplate referenceInputTemplate) {
        this.referenceInputTemplate = referenceInputTemplate;
    }

    @OneToMany(mappedBy = "referenceType")
    public List<AttributeReferenceType> getAttributeReferenceTypes() {
        return attributeReferenceTypes;
    }

    public void setAttributeReferenceTypes(List<AttributeReferenceType> attributeReferenceTypes) {
        this.attributeReferenceTypes = attributeReferenceTypes;
    }

    @OneToMany(mappedBy = "referenceType")
    public List<ReferenceTypeRulebookSection> getReferenceTypeRulebookSections() {
        return referenceTypeRulebookSections;
    }

    public void setReferenceTypeRulebookSections(List<ReferenceTypeRulebookSection> referenceTypeRulebookSections) {
        this.referenceTypeRulebookSections = referenceTypeRulebookSections;
    }

    @Override
    public String toString() {
        return name;
    }
}
