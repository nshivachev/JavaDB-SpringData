package introductionToHibernate;

import introductionToHibernate.entities.Employee;
import introductionToHibernate.entities.Project;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.Scanner;
import java.util.stream.Collectors;

public class _08_GetEmployeeWithProject {
    private static final String DB_NAME = "soft_uni";
    private static final String GET_EMPLOYEES = "select e from Employee e where id = :id";
    private static final String EMPLOYEE_DATA_FORMAT = "%s %s - %s\n\t";
    private static final String ID_PARAMETER = "id";
    private static final String DELIMITER = "\n\t";

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        final int employeeId = Integer.parseInt(scanner.nextLine());

        final EntityManager entityManager = Persistence.createEntityManagerFactory(DB_NAME).createEntityManager();

        final StringBuilder result = new StringBuilder();

        final Employee employee = entityManager.createQuery(GET_EMPLOYEES, Employee.class)
                .setParameter(ID_PARAMETER, employeeId)
                .getSingleResult();

        result.append(
                        String.format(EMPLOYEE_DATA_FORMAT,
                                employee.getFirstName(),
                                employee.getLastName(),
                                employee.getJobTitle()))
                .append(
                        employee.getProjects().stream()
                                .map(Project::getName)
                                .sorted()
                                .collect(Collectors.joining(DELIMITER)));

        entityManager.close();

        System.out.println(result);
    }
}
