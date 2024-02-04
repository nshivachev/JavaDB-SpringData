package hibernateCodeFirstLab.relations;

import hibernateCodeFirstLab.relations.companies.Company;
import hibernateCodeFirstLab.relations.companies.PlaneRelation;
import hibernateCodeFirstLab.relations.drivers.Driver;
import hibernateCodeFirstLab.relations.drivers.TruckRelation;
import hibernateCodeFirstLab.relations.plateNumber.CarRelation;
import hibernateCodeFirstLab.relations.plateNumber.PlateNumber;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class Main {
    private static final String DB_NAME = "relations";

    public static void main(String[] args) {
        final EntityManager entityManager = Persistence.createEntityManagerFactory(DB_NAME).createEntityManager();

        entityManager.getTransaction().begin();

        final PlateNumber plateNumber = new PlateNumber("AB1234CD");
        final CarRelation car = new CarRelation(plateNumber);
        entityManager.persist(plateNumber);
        entityManager.persist(car);

        final PlaneRelation plane = new PlaneRelation("Boeing");
        final Company company = new Company("Wizz", plane);
        plane.setCompany(company);
        entityManager.persist(company);

        final TruckRelation truck = new TruckRelation("Texim");
        final Driver driver = new Driver("Gosho", truck);
        entityManager.persist(driver);

        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
