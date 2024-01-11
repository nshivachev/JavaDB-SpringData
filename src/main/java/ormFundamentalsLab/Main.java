package ormFundamentalsLab;

import ormFundamentalsLab.entities.User;
import ormFundamentalsLab.orm.EntityManager;
import src.Utils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main {
    private final static String DB_NAME = "soft_uni";
    private final static String PRINT_USER_FORMAT = "id: %d, username: %s, age: %d, registration date: %s%n";
    private final static String ALL_USERS_PRINT = "All users: ";
    private final static String FILTERED_USERS_PRINT = "User older than 18 years and registered after 2020 year: ";

    public static void main(String[] args) throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        final Connection connection = Utils.getSQLConnection(DB_NAME);

        final EntityManager<User> userManager = new EntityManager<>(connection);

        User firstUser = new User("First", 29, LocalDate.now());
        //Insert user into the DB
        userManager.persist(firstUser);
        //Get the created user from the DB
        firstUser = userManager.findFirst(User.class);
        printUser(firstUser);
        firstUser.setAge(32);
        //Update the user into the DB
        userManager.persist(firstUser);
        firstUser = userManager.findFirst(User.class);
        printUser(firstUser);

        User secondUser = new User("Second", 35, LocalDate.now());
        secondUser.setRegistrationDate(LocalDate.of(1999, 01, 31));
        userManager.persist(secondUser);
        secondUser = userManager.findFirst(User.class, "user_name = 'Second' and age = 35");
        printUser(secondUser);

        User thirdUser = new User("Third", 18, LocalDate.now());
        userManager.persist(thirdUser);
        thirdUser = userManager.findFirst(User.class, "user_name = 'Third' and age >= 18");
        printUser(thirdUser);

        System.out.println();
        System.out.println(ALL_USERS_PRINT);

        final List<User> allUsers = (List<User>) userManager.find(User.class);
        allUsers.forEach(Main::printUser);

        System.out.println();
        System.out.println(FILTERED_USERS_PRINT);

        final List<User> filteredUsers = (List<User>) userManager.find(User.class, "age >= 18 and year(registration_date) >= 2020");
        filteredUsers.forEach(Main::printUser);
    }

    private static void printUser(User userFound) {
        System.out.printf(PRINT_USER_FORMAT,
                userFound.getId(),
                userFound.getUsername(),
                userFound.getAge(),
                userFound.getRegistrationDate());
    }
}
