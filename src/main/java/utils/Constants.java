package utils;

public enum Constants {
    ;

    static final String USER_KEY = "user";
    static final String USER_VALUE = "root";

    static final String PASSWORD_KEY = "password";
    static final String PASSWORD_VALUE = "12345";

    static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
    static final String PARAMETER_FOR_THE_DATABASE =
            "?allowPublicKeyRetrieval=true&useSSL=false&createDatabaseIfNotExist=true&serverTimezone=UTC";
}
