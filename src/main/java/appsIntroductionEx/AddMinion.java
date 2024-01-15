package appsIntroductionEx;

import utils.Connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AddMinion {
    private static final String MINIONS_DB = "minions_db";

    private static final String GET_TOWN_ID_BY_NAME = "select t.id from towns as t where name = ?";
    private static final String INSERT_INTO_TOWNS = "insert into towns(name) values (?)";
    private static final String TOWN_ADDED_FORMAT = "Town %s was added to the database.%n";

    private static final String GET_VILLAIN_ID_BY_NAME = "select v.id from villains as v where name = ?";
    private static final String INSERT_INTO_VILLAINS = "insert into villains(name, evilness_factor) values (?, ?)";
    private static final String EVILNESS_FACTOR = "evil";
    private static final String VILLAIN_ADDED_FORMAT = "Villain %s was added to the database.%n";

    private static final String COLUMN_LABEL_ID = "id";

    private static final String GET_MINION_ID_BY_NAME = "select m.id from minions as m where name = ?";
    private static final String INSERT_INTO_MINIONS = "insert into minions(name, age, town_id) values (?, ?, ?)";
    private static final String MINION_ADDED_FORMAT = "Successfully added %s to be minion of %s.";

    private static final String INSERT_INTO_MINIONS_VILLAINS = "insert into minions_villains(minion_id, villain_id) values (?, ?)";

    public static void main(String[] args) throws SQLException {

        final Scanner scanner = new Scanner(System.in);

        final String[] minionData = scanner.nextLine().split("\\s+");

        final String minionName = minionData[1];
        final int minionAge = Integer.parseInt(minionData[2]);
        final String townName = minionData[3];
        final String villainName = scanner.nextLine().split("\\s+")[1];

        final Connection connection = Connector.getSQLConnection(MINIONS_DB);

        final int townId = getId(connection, List.of(townName), GET_TOWN_ID_BY_NAME, INSERT_INTO_TOWNS, TOWN_ADDED_FORMAT);

        final int villainId = getId(connection, List.of(villainName, EVILNESS_FACTOR), GET_VILLAIN_ID_BY_NAME, INSERT_INTO_VILLAINS, VILLAIN_ADDED_FORMAT);

        addMinionToVillain(connection, minionName, minionAge, villainName, townId, villainId);

        connection.close();
    }

    private static void addMinionToVillain(Connection connection,
                                           String minionName,
                                           int minionAge,
                                           String villainName,
                                           int townId,
                                           int villainId) throws SQLException {

        final PreparedStatement selectStatement = connection.prepareStatement(GET_MINION_ID_BY_NAME);
        selectStatement.setString(1, minionName);

        ResultSet resultSet = selectStatement.executeQuery();

        if (!resultSet.next()) {
            PreparedStatement insertStatement = connection.prepareStatement(INSERT_INTO_MINIONS);
            insertStatement.setString(1, minionName);
            insertStatement.setInt(2, minionAge);
            insertStatement.setInt(3, townId);

            insertStatement.executeUpdate();

            resultSet = selectStatement.executeQuery();
            resultSet.next();

            final int minionId = resultSet.getInt(COLUMN_LABEL_ID);

            insertStatement = connection.prepareStatement(INSERT_INTO_MINIONS_VILLAINS);
            insertStatement.setInt(1, minionId);
            insertStatement.setInt(2, villainId);

            insertStatement.executeUpdate();

            System.out.printf(MINION_ADDED_FORMAT, minionName, villainName);
        }
    }

    private static int getId(Connection connection,
                             List<String> arguments,
                             String getIdStatement,
                             String insertQuery,
                             String entityAddedPrintFormat) throws SQLException {

        final String name = arguments.get(0);

        final PreparedStatement selectStatement = connection.prepareStatement(getIdStatement);
        selectStatement.setString(1, name);

        ResultSet resultSet = selectStatement.executeQuery();

        if (!resultSet.next()) {
            final PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

            for (int i = 0; i < arguments.size(); i++) {
                insertStatement.setString(i + 1, arguments.get(i));
            }

            insertStatement.executeUpdate();

            System.out.printf(entityAddedPrintFormat, name);

            resultSet = selectStatement.executeQuery();
            resultSet.next();
        }

        return resultSet.getInt(COLUMN_LABEL_ID);
    }
}
