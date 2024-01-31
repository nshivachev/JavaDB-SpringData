package introductionToHibernate;

import introductionToHibernate.entities.Employee;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

public class _03_ContainsEmployee {
    private static final String DB_NAME = "soft_uni";
    private static final String GET_EMPLOYEES = "select e from Employee e";
    private static final String EMPLOYEE_NAME_FORMAT = "%s %s";
    private static final String YES = "Yes";
    private static final String NO = "No";

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        final String employeeName = scanner.nextLine();

        final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(DB_NAME);

        final EntityManager entityManager = entityManagerFactory.createEntityManager();

        final List<Employee> resultList = entityManager.createQuery(GET_EMPLOYEES, Employee.class).getResultList();

        entityManager.close();

        final Predicate<Employee> employeePredicate = e -> String.format(EMPLOYEE_NAME_FORMAT, e.getFirstName(), e.getLastName()).equals(employeeName);

        final String result = resultList.stream().anyMatch(employeePredicate) ? YES : NO;

        System.out.println(result);
    }
}
