package src.appsIntroductionLab.dataRetrievalApplication;

import src.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Diablo {
    public static void main(String[] args) throws SQLException {
        final Scanner scanner = new Scanner(System.in);

        final Connection connection = Utils.getSQLConnection("diablo");

        System.out.print("Enter username: ");
        final String username = scanner.nextLine();

        final PreparedStatement query = connection.prepareStatement(
                "SELECT user_name, first_name, last_name, COUNT(*) games_count " +
                        "FROM users u " +
                        "JOIN users_games ug " +
                        "ON u.id = ug.user_id " +
                        "WHERE u.user_name = ? " +
                        "GROUP BY u.id;"
        );
        query.setString(1, username);

        final ResultSet result = query.executeQuery();

        if (result.next()) {
            final String dbUsername = result.getString("user_name");
            final String dbFirstname = result.getString("first_name");
            final String dbLastname = result.getString("last_name");
            final int dbGamesCount = result.getInt("games_count");

            System.out.printf("User: %s%n%s %s has played %d games", dbUsername, dbFirstname, dbLastname, dbGamesCount);
        } else {
            System.out.println("No such user exists");
        }
    }
}
