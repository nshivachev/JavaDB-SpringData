package introductionToHibernate;

import introductionToHibernate.entities.Project;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.Comparator;

public class FindLatest10Projects {
    private static final String DB_NAME = "soft_uni";
    private static final String GET_PROJECTS = "select p from Project p order by p.startDate desc";
    private static final String PROJECT_DATA_FORMAT = "Project name: %s%n\tProject Description: %s%n\tProject Start Date: %s:00.0%n\tProject End Date: %s%n";
    private static final String T_SYMBOL = "T";
    private static final String WHITE_SPACE = " ";

    public static void main(String[] args) {
        final EntityManager entityManager = Persistence.createEntityManagerFactory(DB_NAME).createEntityManager();

        entityManager
                .createQuery(GET_PROJECTS, Project.class)
                .setMaxResults(10)
                .getResultList().stream()
                .sorted(Comparator.comparing(Project::getName))
                .forEach(p -> System.out.printf(PROJECT_DATA_FORMAT,
                                p.getName(),
                                p.getDescription(),
                                String.valueOf(p.getStartDate()).replace(T_SYMBOL, WHITE_SPACE),
                                p.getEndDate()));

        entityManager.close();
    }
}
