package hibernateCodeFirstLab.vehicleHierarchy;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class Main {
    private static final String DB_NAME = "relations";

    public static void main(String[] args) {
        final EntityManager entityManager = Persistence.createEntityManagerFactory(DB_NAME).createEntityManager();

        entityManager.getTransaction().begin();

        Vehicle car = new Car("Ford Focus", "Petrol", 5);
        Vehicle truck = new Truck("Volovo Truck", "Diesel", 3);
        Vehicle plane = new Plane("Boeing", "kerosene", 100);
        Vehicle bike = new Bike();

        entityManager.persist(car);
        entityManager.persist(truck);
        entityManager.persist(plane);
        entityManager.persist(bike);

        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
