package introductionToHibernate;

import introductionToHibernate.entities.Employee;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.List;

public class _04_EmployeesWithSalaryOver50000 {
    private static final String DB_NAME = "soft_uni";
    private static final String GET_EMPLOYEES = "select e from Employee e";

    public static void main(String[] args) {
        final EntityManager entityManager = Persistence.createEntityManagerFactory(DB_NAME).createEntityManager();

        entityManager.getTransaction().begin();

        final List<Employee> resultList = entityManager.createQuery(GET_EMPLOYEES, Employee.class).getResultList();

        entityManager.getTransaction().commit();
        entityManager.close();

        resultList.forEach(e -> {
            if (e.getSalary().compareTo(BigDecimal.valueOf(50000)) > 0)
                System.out.println(e.getFirstName());
        });
    }
}
