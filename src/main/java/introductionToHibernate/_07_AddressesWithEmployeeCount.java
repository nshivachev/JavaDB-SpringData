package introductionToHibernate;

import introductionToHibernate.entities.Address;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class _07_AddressesWithEmployeeCount {
    private static final String DB_NAME = "soft_uni";
    private static final String GET_ADDRESSES = "select a from Address a order by a.employees.size desc";
    private static final String ADDRESS_DATA_FORMAT = "%s, %s - %d employees%n";

    public static void main(String[] args) {
        final EntityManager entityManager = Persistence.createEntityManagerFactory(DB_NAME).createEntityManager();

        final StringBuilder result = new StringBuilder();

        entityManager.createQuery(GET_ADDRESSES, Address.class)
                .setMaxResults(10)
                .getResultList()
                .forEach(a -> result.append(
                        String.format(ADDRESS_DATA_FORMAT,
                                a.getText(),
                                a.getTown().getName(),
                                a.getEmployees().size())));

        entityManager.close();

        System.out.println(result.toString().trim());
    }
}
