package mk.ukim.finki.isis.edubio.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;

@Entity
@Table(schema = "edubio", name = "reference_type_rulebook_section")
public class ReferenceTypeRulebookSection {
    private Long id;

    private ReferenceType referenceType;

    private RulebookSection rulebookSection;

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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rulebook_section_id")
    public RulebookSection getRulebookSection() {
        return rulebookSection;
    }

    public void setRulebookSection(RulebookSection rulebookSection) {
        this.rulebookSection = rulebookSection;
    }
}
