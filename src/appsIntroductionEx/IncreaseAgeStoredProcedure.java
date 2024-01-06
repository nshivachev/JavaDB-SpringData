package src.appsIntroductionEx;

import src.Utils;

import java.sql.*;
import java.util.Scanner;

public class IncreaseAgeStoredProcedure {
    private static final String MINIONS_DB = "minions_db";
    private static final String UPDATE_AGE_PROCEDURE = "call usp_get_older(?)";
    private static final String GET_MINION_NAME_AGE_BY_ID = "select name, age from minions where id = ?";
    private static final String COLUMN_LABEL_NAME = "name";
    private static final String COLUMN_LABEL_AGE = "age";
    private static final String MINION_NAME_AGE_PRINT_FORMAT = "%s %d";

    public static void main(String[] args) throws SQLException {
        final Scanner scanner = new Scanner(System.in);

        final int minionId = Integer.parseInt(scanner.nextLine());

        final Connection connection = Utils.getSQLConnection(MINIONS_DB);

        final CallableStatement callProcedureStatement = connection.prepareCall(UPDATE_AGE_PROCEDURE);
        callProcedureStatement.setInt(1, minionId);

        callProcedureStatement.executeQuery();

        final PreparedStatement selectStatement = connection.prepareStatement(GET_MINION_NAME_AGE_BY_ID);
        selectStatement.setInt(1, minionId);

        final ResultSet resultSet = selectStatement.executeQuery();

        if (resultSet.next()) {
            final String minionName = resultSet.getString(COLUMN_LABEL_NAME);
            final int minionAge = resultSet.getInt(COLUMN_LABEL_AGE);

            System.out.printf(MINION_NAME_AGE_PRINT_FORMAT, minionName, minionAge);
        }
    }
}
