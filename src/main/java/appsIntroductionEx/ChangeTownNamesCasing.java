package appsIntroductionEx;

import utils.Connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChangeTownNamesCasing {
    private static final String MINIONS_DB = "minions_db";
    private static final String GET_TOWNS_COUNT_BY_COUNTRY_NAME = "select count(*) as count from towns where country = ?";
    private static final String UPDATE_TOWN_NAMES_BY_COUNTRY_NAME = "update towns set name = upper(name) where country = ?";
    private static final String NO_AFFECTED_TOWN_PRINT = "No town names were affected.";
    private static final String UPDATED_NUMBER_OF_TOWNS_PRINT_FORMAT = "%s town names were affected.%n";
    private static final String GET_TOWNS_NAME_BY_COUNTRY_NAME = "select name from towns where country = ?";
    private static final String COLUMN_LABEL_NAME = "name";

    public static void main(String[] args) throws SQLException {
        final Scanner scanner = new Scanner(System.in);

        final String countryName = scanner.nextLine();

        final Connection connection = Connector.getSQLConnection(MINIONS_DB);

        PreparedStatement selectStatement = connection.prepareStatement(GET_TOWNS_COUNT_BY_COUNTRY_NAME);
        selectStatement.setString(1, countryName);

        ResultSet resultSet = selectStatement.executeQuery();

        final int townsCount = resultSet.next() ? resultSet.getInt("count") : 0;

        if (townsCount == 0) {
            System.out.println(NO_AFFECTED_TOWN_PRINT);

            connection.close();

            return;
        }

        final PreparedStatement updateStatement = connection.prepareStatement(UPDATE_TOWN_NAMES_BY_COUNTRY_NAME);
        updateStatement.setString(1, countryName);

        updateStatement.executeUpdate();

        System.out.printf(UPDATED_NUMBER_OF_TOWNS_PRINT_FORMAT, townsCount);

        final List<String> towns = new ArrayList<>();

        selectStatement = connection.prepareStatement(GET_TOWNS_NAME_BY_COUNTRY_NAME);
        selectStatement.setString(1, countryName);

        resultSet = selectStatement.executeQuery();

        while (resultSet.next()) {
            towns.add(resultSet.getString(COLUMN_LABEL_NAME));
        }

        System.out.print(towns);

        connection.close();
    }
}
