package src.appsIntroductionLab.dataRetrievalApplication;

import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Diablo {
    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "12345");

        Connection connection = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/diablo", properties);

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        PreparedStatement query = connection.prepareStatement(
                "SELECT user_name, first_name, last_name, COUNT(*) games_count " +
                        "FROM users u " +
                        "JOIN users_games ug " +
                        "ON u.id = ug.user_id " +
                        "WHERE u.user_name = ? " +
                        "GROUP BY u.id;"
        );
        query.setString(1, username);

        ResultSet result = query.executeQuery();

        if (result.next()) {
            String dbUsername = result.getString("user_name");
            String dbFirstname = result.getString("first_name");
            String dbLastname = result.getString("last_name");
            int dbGamesCount = result.getInt("games_count");

            System.out.printf("User: %s%n%s %s has played %d games", dbUsername, dbFirstname, dbLastname, dbGamesCount);
        } else {
            System.out.println("No such user exists");
        }
    }
}
