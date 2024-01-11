package appsIntroductionEx;

import src.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class IncreaseMinionsAge {
    private static final String MINIONS_DB = "minions_db";
    private static final String GET_MINION_BY_ID = "select name from minions where id = ?";
    private static final String UPDATE_MINIONS_NAME_AGE_BY_ID = "update minions set name = lower(name), age = age + 1 where id = ?";
    private static final String GET_ALL_MINIONS_NAME_AGE = "select name, age from minions";
    private static final String COLUMN_LABEL_NAME = "name";
    private static final String COLUMN_LABEL_AGE = "age";
    private static final String MINION_NAME_AGE_PRINT_FORMAT = "%s %d%n";

    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        final int[] minionIds = Arrays
                .stream(scanner.nextLine().split("\\s+"))
                .mapToInt(Integer::parseInt)
                .toArray();

        final Connection connection = Utils.getSQLConnection(MINIONS_DB);

        final PreparedStatement updateStatement = connection.prepareStatement(UPDATE_MINIONS_NAME_AGE_BY_ID);

        PreparedStatement selectStatement = connection.prepareStatement(GET_MINION_BY_ID);

        for (int i = 0; i < minionIds.length; i++) {
            int minionId = minionIds[i];

            selectStatement.setInt(1, minionId);

            final ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                updateStatement.setInt(1, minionId);

                updateStatement.executeUpdate();
            }
        }

        final Map<String, Integer> minionsNameToAgeMap = new LinkedHashMap<>();

        selectStatement = connection.prepareStatement(GET_ALL_MINIONS_NAME_AGE);

        final ResultSet resultSet = selectStatement.executeQuery();

        while (resultSet.next()) {
            final String minionName = resultSet.getString(COLUMN_LABEL_NAME);
            final int minionAge = resultSet.getInt(COLUMN_LABEL_AGE);

            minionsNameToAgeMap.put(minionName, minionAge);
        }

        minionsNameToAgeMap.forEach((key, value) -> System.out.printf(MINION_NAME_AGE_PRINT_FORMAT, key, value));
    }
}
