package introductionToHibernate;

import introductionToHibernate.entities.Employee;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.Scanner;

public class _11_FindEmployeesByFirstName {
    private static final String DB_NAME = "soft_uni";
    private static final String GET_EMPLOYEES_FORMAT = "select e from Employee e where e.firstName like '%s%%'";
    private static final String EMPLOYEE_DATA_FORMAT = "%s %s - %s - ($%.2f)%n";

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        final String pattern = scanner.nextLine();

        final EntityManager entityManager = Persistence.createEntityManagerFactory(DB_NAME).createEntityManager();

        entityManager.createQuery(String.format(GET_EMPLOYEES_FORMAT, pattern), Employee.class)
                .getResultList()
                .forEach(employee -> System.out.printf(EMPLOYEE_DATA_FORMAT,
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getJobTitle(),
                        employee.getSalary()));

        entityManager.close();
    }
}
