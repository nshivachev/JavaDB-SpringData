package hibernateCodeFirstLab.relations.drivers;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "drivers")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "full_name")
    private String fullName;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "drivers_truck_relations",
            joinColumns = @JoinColumn(name = "driver_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "truck_relation_id", referencedColumnName = "id"))
    private Set<TruckRelation> truckRelations;

    public Driver() {
        truckRelations = new HashSet<>();
    }

    public Driver(String fullName, TruckRelation truckRelation) {
        this();

        this.fullName = fullName;
        truckRelations.add(truckRelation);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
