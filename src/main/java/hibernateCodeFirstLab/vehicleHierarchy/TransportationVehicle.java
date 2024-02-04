package hibernateCodeFirstLab.vehicleHierarchy;

import javax.persistence.Basic;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class TransportationVehicle extends Vehicle {
    @Basic
    protected double loadCapacity;

    public TransportationVehicle(String type) {
        super(type);
    }

    public double getLoadCapacity() {
        return loadCapacity;
    }

    public void setLoadCapacity(double loadCapacity) {
        this.loadCapacity = loadCapacity;
    }
}
