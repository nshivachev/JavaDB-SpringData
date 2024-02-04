package hibernateCodeFirstLab.relations.drivers;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "truck_relations")
public class TruckRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "fuel_type")
    private String fuelType;

    @Basic
    private String model;

    @Basic
    private BigDecimal price;

    @Basic
    private String type;

    @Column(name = "load_capacity")
    private String loadCapacity;

    @ManyToMany(targetEntity = Driver.class, mappedBy = "truckRelations")
    private Set<Driver> drivers;

    public TruckRelation() {
        drivers = new HashSet<>();
    }

    public TruckRelation(String model) {
        this();

        this.model = model;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLoadCapacity() {
        return loadCapacity;
    }

    public void setLoadCapacity(String loadCapacity) {
        this.loadCapacity = loadCapacity;
    }
}
