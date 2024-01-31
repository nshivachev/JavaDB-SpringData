package introductionToHibernate;

import introductionToHibernate.entities.Employee;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.math.BigDecimal;

public class _10_IncreaseSalaries {
    private static final String DB_NAME = "soft_uni";
    private static final String GET_EMPLOYEES = "select e from Employee e where e.department.name in ('Engineering', 'Tool Design', 'Marketing', 'Information Services')";
    private static final String EMPLOYEE_DATA_FORMAT = "%s %s ($%.2f)%n";

    public static void main(String[] args) {
        final EntityManager entityManager = Persistence.createEntityManagerFactory(DB_NAME).createEntityManager();

        entityManager.getTransaction().begin();

        entityManager
                .createQuery(GET_EMPLOYEES, Employee.class)
                .getResultList()
                .forEach(employee -> {
                    employee.setSalary(employee.getSalary().multiply(BigDecimal.valueOf(1.12)));
                    entityManager.persist(employee);
                    System.out.printf(EMPLOYEE_DATA_FORMAT,
                            employee.getFirstName(),
                            employee.getLastName(),
                            employee.getSalary());
                });

        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
