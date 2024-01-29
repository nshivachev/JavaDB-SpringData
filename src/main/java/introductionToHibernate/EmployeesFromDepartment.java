package introductionToHibernate;

import introductionToHibernate.entities.Employee;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.Comparator;
import java.util.List;

public class EmployeesFromDepartment {
    private static final String DB_NAME = "soft_uni";
    private static final String GET_EMPLOYEES = "select e from Employee e";
    private static final String RESEARCH_AND_DEVELOPMENT = "Research and Development";
    private static final String EMPLOYEE_DATA_FORMAT = "%s %s from %s - $%.2f%n";

    public static void main(String[] args) {
        final EntityManager entityManager = Persistence.createEntityManagerFactory(DB_NAME).createEntityManager();

        entityManager.getTransaction().begin();

        final List<Employee> resultList = entityManager.createQuery(GET_EMPLOYEES, Employee.class).getResultList();

        entityManager.getTransaction().commit();
        entityManager.close();

        final StringBuilder result = new StringBuilder();

        resultList.stream()
                .filter(e -> e.getDepartment().getName().equals(RESEARCH_AND_DEVELOPMENT))
                .sorted(Comparator.comparing(Employee::getId))
                .sorted(Comparator.comparing(Employee::getSalary))
                .forEach(e -> result.append(String.format(
                        EMPLOYEE_DATA_FORMAT,
                        e.getFirstName(),
                        e.getLastName(),
                        e.getDepartment().getName(),
                        e.getSalary())));

        System.out.print(result.toString().trim());
    }
}