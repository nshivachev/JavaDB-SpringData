package hibernateCodeFirstLab.relations.companies;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(targetEntity = PlaneRelation.class, mappedBy = "company",
            fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<PlaneRelation> planes;

    public Company() {
        planes = new HashSet<>();
    }

    public Company(String name, PlaneRelation plane) {
        this();

        this.name = name;
        planes.add(plane);
    }
}
