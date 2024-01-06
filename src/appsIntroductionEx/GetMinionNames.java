package src.appsIntroductionEx;

import src.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class GetMinionNames {
    private static final String MINIONS_DB = "minions_db";
    private static final String GET_VILLAIN_NAME_MINIONS_NAME_AGE_BY_VILLAIN_ID =
            "select v.name as villain_name, m.name as minion_name, m.age as minion_age " +
                    "from minions m " +
                    "         join minions_villains mv on m.id = mv.minion_id " +
                    "         join villains v on v.id = mv.villain_id " +
                    "where mv.villain_id = ?;";
    private static final String COLUMN_LABEL_VILLAIN_NAME = "villain_name";
    private static final String COLUMN_LABEL_MINION_NAME = "minion_name";
    private static final String COLUMN_LABEL_MINIONS_AGE = "minion_age";
    private static final String MINION_LIST_FORMAT = "%d. %s %d%n";
    private static final String VILLAIN_NAME_FORMAT = "Villain: %s%n";
    private static final String NO_VILLAIN_FORMAT = "No villain with ID %s exists in the database.";

    public static void main(String[] args) throws SQLException {
        final Scanner scanner = new Scanner(System.in);

        final Connection connection = Utils.getSQLConnection(MINIONS_DB);

        final PreparedStatement statement = connection.prepareStatement(GET_VILLAIN_NAME_MINIONS_NAME_AGE_BY_VILLAIN_ID);

        final int villainId = Integer.parseInt(scanner.nextLine());

        statement.setInt(1, villainId);

        final ResultSet resultSet = statement.executeQuery();

        String villainName = null;

        final StringBuilder minionsData = new StringBuilder();

        int minionIndex = 1;

        while (resultSet.next()) {
            villainName = resultSet.getString(COLUMN_LABEL_VILLAIN_NAME);
            final String minionName = resultSet.getString(COLUMN_LABEL_MINION_NAME);
            final int minionAge = resultSet.getInt(COLUMN_LABEL_MINIONS_AGE);

            minionsData.append(String.format(MINION_LIST_FORMAT, minionIndex++, minionName, minionAge));
        }

        connection.close();

        minionsData.insert(0, String.format(VILLAIN_NAME_FORMAT, villainName));

        final String result = villainName == null
                ? String.format(NO_VILLAIN_FORMAT, villainId)
                : minionsData.toString().trim();

        System.out.print(result);
    }
}