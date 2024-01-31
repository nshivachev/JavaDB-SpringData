package introductionToHibernate;

import introductionToHibernate.entities.Town;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class _02_ChangeCasing {
    private static final String DB_NAME = "soft_uni";
    private static final String GET_TOWNS = "select t from Town t";

    public static void main(String[] args) {
        final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(DB_NAME);
        final EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        final List<Town> resultList = entityManager.createQuery(GET_TOWNS, Town.class).getResultList();

        for (Town town : resultList) {
            final String townName = town.getName();

            if (townName.length() <= 5) {
                town.setName(townName.toUpperCase());

                entityManager.persist(town);
            }
        }

        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
