package src.appsIntroductionEx;

import src.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PrintAllMinionNames {
    private static final String MINIONS_DB = "minions_db";
    private static final String GET_ALL_MINION_NAMES = "select name from minions";
    private static final String COLUMN_LABEL_NAME = "name";

    public static void main(String[] args) throws SQLException {
        final Connection connection = Utils.getSQLConnection(MINIONS_DB);

        final PreparedStatement selectAllMinionsStatement = connection.prepareStatement(GET_ALL_MINION_NAMES);

        final ResultSet resultSet = selectAllMinionsStatement.executeQuery();

        final ArrayList<String> minions = new ArrayList<>();

        while (resultSet.next()) {
            minions.add(resultSet.getString(COLUMN_LABEL_NAME));
        }

        int startIndex = 0;
        int endIndex = minions.size() - 1;

        while (startIndex <= endIndex - 1) {
            System.out.println(minions.get(startIndex++));

            if (startIndex != endIndex - 1)
                System.out.println(minions.get(endIndex--));
        }
    }
}