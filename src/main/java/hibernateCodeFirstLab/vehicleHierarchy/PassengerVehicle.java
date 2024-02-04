package hibernateCodeFirstLab.vehicleHierarchy;

import javax.persistence.Basic;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class PassengerVehicle extends Vehicle {
    @Basic
    protected int seats;

    public PassengerVehicle(String type) {
        super(type);
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }
}
