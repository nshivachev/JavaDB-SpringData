package src.appsIntroductionEx;

import src.Utils;

import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class GetVillainsNames {
    private static final String GET_VILLAINS_NAMES =
            "select v.name, count(distinct mv.minion_id) minions_count " +
                    "from villains v " +
                    "         join minions_villains mv on v.id = mv.villain_id " +
                    "group by v.name " +
                    "having minions_count > ? " +
                    "order by minions_count desc;";
    private static final String COLUMN_LABEL_NAME = "name";
    private static final String COLUMN_LABEL_MINIONS_COUNT = "minions_count";
    private static final String PRINT_FORMAT = "%s %s%n";

    public static void main(String[] args) throws SQLException {
        final Connection connection = Utils.getSQLConnection("minions_db");

        final PreparedStatement query = connection.prepareStatement(GET_VILLAINS_NAMES);

        query.setInt(1, 15);

        final ResultSet result = query.executeQuery();

        while (result.next()) {
            final String villainName = result.getString(COLUMN_LABEL_NAME);
            final int minionsCount = result.getInt(COLUMN_LABEL_MINIONS_COUNT);

            System.out.printf(PRINT_FORMAT,  villainName, minionsCount);
        }

        connection.close();
    }
}
