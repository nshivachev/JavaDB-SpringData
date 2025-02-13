package hibernateCodeFirstLab.vehicleHierarchy;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
//@Table(name = "cars")
public class Car extends PassengerVehicle {
    private static final String CAR_TYPE = "CAR";

    public Car() {
        super(CAR_TYPE);
    }

    public Car(String model, String fuelType, int seats) {
        this();

        this.model = model;
        this.fuelType = fuelType;
        this.seats = seats;
    }
}
