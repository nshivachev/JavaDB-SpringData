package appsIntroductionEx;

import utils.Connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class RemoveVillain {
    private static final String MINIONS_DB = "minions_db";

    private static final String GET_VILLAIN_NAME_BY_ID = "select name from villains where id = ?";
    private static final String GET_MINION_VILLAIN_COUNT_BY_VILLAIN_ID = "select count(minion_id) as count from minions_villains where villain_id = ?";

    private static final String DELETE_MINIONS_VILLAINS_BY_VILLAIN_ID = "delete from minions_villains where villain_id = ?";
    private static final String DELETE_VILLAIN_BY_ID = "delete from villains where id = ?";

    private static final String COLUMN_LABEL_NAME = "name";
    private static final String COLUMN_LABEL_COUNT = "count";

    private static final String NO_VILLAIN_FOUND_MESSAGE = "No such villain was found";
    private static final String DELETE_VILLAIN_PRINT_FORMAT = "%s was deleted%n";
    private static final String DELETE_MINIONS_PRINT_FORMAT = "%d minions released";

    public static void main(String[] args) throws SQLException {
        final Scanner scanner = new Scanner(System.in);

        final int villainId = Integer.parseInt(scanner.nextLine());

        final Connection connection = Connector.getSQLConnection(MINIONS_DB);

        PreparedStatement selectStatement = connection.prepareStatement(GET_VILLAIN_NAME_BY_ID);
        selectStatement.setInt(1, villainId);

        ResultSet resultSet = selectStatement.executeQuery();

        if (!resultSet.next()) {
            System.out.println(NO_VILLAIN_FOUND_MESSAGE);

            connection.close();

            return;
        }

        final String deletedVillainName = resultSet.getString(COLUMN_LABEL_NAME);

        selectStatement = connection.prepareStatement(GET_MINION_VILLAIN_COUNT_BY_VILLAIN_ID);
        selectStatement.setInt(1, villainId);

        resultSet = selectStatement.executeQuery();
        resultSet.next();

        final int deletedMinionsCount = resultSet.getInt(COLUMN_LABEL_COUNT);

        connection.setAutoCommit(false);

        try {
            PreparedStatement deleteStatement = connection.prepareStatement(DELETE_MINIONS_VILLAINS_BY_VILLAIN_ID);
            deleteStatement.setInt(1, villainId);

            deleteStatement.executeUpdate();

            deleteStatement = connection.prepareStatement(DELETE_VILLAIN_BY_ID);
            deleteStatement.setInt(1, villainId);

            deleteStatement.executeUpdate();

            connection.commit();

            System.out.printf(DELETE_VILLAIN_PRINT_FORMAT, deletedVillainName);
            System.out.printf(DELETE_MINIONS_PRINT_FORMAT, deletedMinionsCount);

        } catch (SQLException e) {
            e.printStackTrace();

            connection.rollback();
        }

        connection.close();
    }
}
