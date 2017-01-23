package mk.ukim.finki.isis.edubio.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;

@Entity
@Table(schema = "edubio", name = "reference_report")
public class ReferenceReport {
    private Long id;

    private Reference reference;

    private Report report;

    private float points;

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
    @JoinColumn(name = "reference_id")
    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "report_id")
    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }
}
