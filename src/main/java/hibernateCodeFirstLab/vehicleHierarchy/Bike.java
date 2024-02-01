package hibernateCodeFirstLab.vehicleHierarchy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "bikes")
public class Bike extends Vehicle {
    private static final String CAR_TYPE = "BIKE";

    public Bike() {
        super(CAR_TYPE);
    }
}
