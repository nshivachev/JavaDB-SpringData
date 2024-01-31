package introductionToHibernate;

import introductionToHibernate.entities.Address;
import introductionToHibernate.entities.Town;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Scanner;

public class _13_RemoveTowns {
    private static final String DB_NAME = "soft_uni";
    private static final String ENTER_TOWN_NAME_TEXT = "Enter town name: ";
    private static final String GET_TOWNS_BY_NAME = "select t from Town t where t.name = :town_name";
    private static final String GET_ADDRESSES_BY_TOWN_ID = "select a from Address a where a.town.id = :town_id";
    private static final String TOWN_NAME_PARAMETER = "town_name";
    private static final String TOWN_ID_PARAMETER = "town_id";
    private static final String DELETED_ADDRESS_FORMAT = "%d address in %s deleted";
    private static final String DELETED_ADDRESSES_FORMAT = "%d addresses in %s deleted";

    public static void main(String[] args) {
        System.out.print(ENTER_TOWN_NAME_TEXT);

        final Scanner scanner = new Scanner(System.in);

        final String townName = scanner.nextLine();

        final EntityManager entityManager = Persistence.createEntityManagerFactory(DB_NAME).createEntityManager();

        final Town town = entityManager.createQuery(GET_TOWNS_BY_NAME, Town.class)
                .setParameter(TOWN_NAME_PARAMETER, townName)
                .getSingleResult();

        final List<Address> addresses = entityManager.createQuery(GET_ADDRESSES_BY_TOWN_ID, Address.class)
                .setParameter(TOWN_ID_PARAMETER, town.getId())
                .getResultList();

        entityManager.getTransaction().begin();
        addresses.forEach(entityManager::remove);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        entityManager.remove(town);
        entityManager.getTransaction().commit();

        entityManager.close();

        final int deletedAddressesCount = addresses.size();

        if (deletedAddressesCount > 1) {
            System.out.printf(DELETED_ADDRESSES_FORMAT, addresses.size(), townName);
            return;
        }

        System.out.printf(DELETED_ADDRESS_FORMAT, addresses.size(), townName);
    }
}
