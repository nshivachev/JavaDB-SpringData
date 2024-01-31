package introductionToHibernate;

import introductionToHibernate.entities.Address;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.Scanner;

public class _06_AddingANewAddressAndUpdatingEmployee {
    private static final String DB_NAME = "soft_uni";
    private static final String UPDATE_EMPLOYEE = "update Employee e set e.address = :newAddress where e.lastName = :lastName";
    private static final String ADDRESS_TEXT = "Vitoshka 15";
    private static final String NEW_ADDRESS_PARAMETER = "newAddress";
    private static final String LAST_NAME_PARAMETER = "lastName";

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        final String employeeLastName = scanner.nextLine();

        final EntityManager entityManager = Persistence.createEntityManagerFactory(DB_NAME).createEntityManager();

        entityManager.getTransaction().begin();

        final Address address = new Address();
        address.setText(ADDRESS_TEXT);

        entityManager.persist(address);

        final int count = entityManager.createQuery(UPDATE_EMPLOYEE)
                .setParameter(NEW_ADDRESS_PARAMETER, address)
                .setParameter(LAST_NAME_PARAMETER, employeeLastName)
                .executeUpdate();

        if (count > 0) {
            entityManager.getTransaction().commit();
        } else {
            entityManager.getTransaction().rollback();
        }

        entityManager.close();

        System.out.println(count);
    }
}
