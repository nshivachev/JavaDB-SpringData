package customORM;

import customORM.entities.User;
import customORM.orm.EntityManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main {
    private final static String PRINT_USER_FORMAT = "id: %d, username: %s, age: %d, registration date: %s%n";
    private final static String ALL_USERS_PRINT = "All users: ";
    private final static String FILTERED_USERS_PRINT = "User older than 18 years and registered after 2020 year: ";

    public static void main(String[] args) throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {

        final EntityManager<User> userManager = new EntityManager<>();


        userManager.doCreate(User.class);

//        userManager.doAlter(User.class);

        //Insert new users into the DB
        userManager.persist(new User("First", 29, LocalDate.now()));
        userManager.persist(new User("Second", 35, LocalDate.of(1999, 01, 31)));
        userManager.persist(new User("Third", 18, LocalDate.now()));

        final User firstUser = userManager.findFirst(User.class);
        printUser(firstUser);
        firstUser.setAge(32);
        //Update user into the DB by id
        userManager.persist(firstUser);
        printUser(firstUser);

        final User secondUser = userManager.findFirst(User.class, "user_name = 'Second' and age = 35");
        printUser(secondUser);

        final User thirdUser = userManager.findFirst(User.class, "user_name = 'Third' and age >= 18");
        printUser(thirdUser);

        System.out.println();
        System.out.println(ALL_USERS_PRINT);

        final List<User> allUsers = (List<User>) userManager.find(User.class);
        allUsers.forEach(Main::printUser);

        System.out.println();
        System.out.println(FILTERED_USERS_PRINT);

        final List<User> filteredUsers = (List<User>) userManager.find(User.class, "age >= 18 and year(registration_date) >= 2020");
        filteredUsers.forEach(Main::printUser);

//        userManager.doDelete(User.class, "age", "18");
    }

    private static void printUser(User userFound) {
        System.out.printf(PRINT_USER_FORMAT,
                userFound.getId(),
                userFound.getUsername(),
                userFound.getAge(),
                userFound.getRegistrationDate());
    }
}
