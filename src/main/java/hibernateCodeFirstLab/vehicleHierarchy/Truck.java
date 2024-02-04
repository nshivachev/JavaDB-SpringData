package hibernateCodeFirstLab.vehicleHierarchy;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
//@Table(name = "trucks")
public class Truck extends TransportationVehicle {
    private static final String TRUCK_TYPE = "TRUCK";

    public Truck() {
        super(TRUCK_TYPE);
    }

    public Truck(String model, String fuelType, int loadCapacity) {
        this();

        this.model = model;
        this.fuelType = fuelType;
        this.loadCapacity = loadCapacity;
    }
}
