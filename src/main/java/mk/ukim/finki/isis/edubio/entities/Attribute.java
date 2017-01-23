package mk.ukim.finki.isis.edubio.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "edubio", name = "attributes")
public class Attribute {
    private Long id;

    private String name;

    private String inputType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonVisual
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "input_type")
    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    @Override
    public String toString() {
        return name;
    }
}
