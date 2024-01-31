package introductionToHibernate;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;

public class _12_EmployeesMaximumSalaries {
    private static final String DB_NAME = "soft_uni";
    private static final String GET_DEPARTMENTS = "select e.department.name, max(e.salary) from Employee e group by e.department.id having max(e.salary) not between 30000 and 70000";
    private static final String DEPARTMENT_DATA_FORMAT = "%s %.2f%n";

    public static void main(String[] args) {
        final EntityManager entityManager = Persistence.createEntityManagerFactory(DB_NAME).createEntityManager();

        final List<Object[]> resultList = entityManager.createQuery(GET_DEPARTMENTS).getResultList();

        entityManager.close();

        resultList.forEach(e -> System.out.printf(DEPARTMENT_DATA_FORMAT, e[0], e[1]));
    }
}
