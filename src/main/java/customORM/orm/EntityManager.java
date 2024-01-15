package customORM.orm;

import customORM.orm.annotations.Column;
import customORM.orm.annotations.Entity;
import customORM.orm.annotations.Id;
import utils.Connector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static java.lang.String.format;

public class EntityManager<E> implements DbContext<E> {
    private final static String DB_NAME = "soft_uni";
    private final Connection connection;

    private final static String CREATE_TABLE_FORMAT = "create table %s (id int primary key auto_increment, %s)";
    private final static String ALTER_TABLE_FORMAT = "alter table %s add column %s";
    private final static String GET_TABLE_FORMAT = "select * from %s %s";
    private final static String GET_FIRST_TABLE_ROW_FORMAT = "select * from %s %s limit 1";
    private final static String INSERT_QUERY_FORMAT = "insert into %s (%s) values (%s)";
    private final static String UPDATE_QUERY_BY_ID_FORMAT = "update %s set %s where id = %s";
    private final static String DELETE_TABLE_FORMAT = "delete from %s where %s = %s";

    private final static String COLUMN_NAME_TYPE_FORMAT = "%s %s";
    private final static String GET_TABLE_FIELD_NAME_FORMAT = "select `column_name` from `information_schema`.`columns` where `table_schema`='%s' and `column_name`!='id' and `table_name`='%s'";
    private final static String INSERT_VALUE_FORMAT = "'%s'";
    private final static String UPDATE_VALUE_FORMAT = "%s = %s";
    private final static String WHERE_CLAUSE_FORMAT = "where %s";

    private final static String INT = "int";
    private final static String DATE = "date";
    private final static String VARCHAR = "varchar(45)";

    private final static String EMPTY_STRING = "";
    private final static String COMMA = ", ";

    private final static String MISSING_ENTITY_ANNOTATION_EXCEPTION = "Provided class does not have Entity annotation";
    private final static String MISSING_PRIMARY_KEY_EXCEPTION = "Entity does not primary key";
    private final static String UNSUPPORTED_TYPE_EXCEPTION_FORMAT = "Unsupported type: %s";


    public EntityManager() throws SQLException {
        this.connection = Connector.getSQLConnection(DB_NAME);
    }

    @Override
    public boolean persist(E entity) throws SQLException, IllegalAccessException {
        return getId(entity) == null || (long) getId(entity) <= 0
                ? doInsert(entity)
                : doUpdate(entity);
    }

    @Override
    public Iterable<E> find(Class<E> table) throws SQLException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return find(table, null);
    }

    @Override
    public Iterable<E> find(Class<E> table, String where) throws SQLException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        final String query = format(GET_TABLE_FORMAT, getTableName(table), getWhereClause(where));

        final ResultSet resultSet = connection.createStatement().executeQuery(query);

        return createEntities(table, resultSet);
    }

    @Override
    public E findFirst(Class<E> table) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return findFirst(table, null);
    }

    @Override
    public E findFirst(Class<E> table, String where) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final String query = format(GET_FIRST_TABLE_ROW_FORMAT, getTableName(table), getWhereClause(where));

        final ResultSet resultSet = connection.createStatement().executeQuery(query);

        return createEntity(table, resultSet);
    }

    @Override
    public void doCreate(Class<E> entityClass) throws SQLException {
        final String query = String.format(CREATE_TABLE_FORMAT, getTableName(entityClass), getAllFieldsAndDataTypes(entityClass));

        connection.prepareStatement(query).execute();
    }

    @Override
    public void doAlter(Class<E> entityClass) throws SQLException {
        final String query = String.format(ALTER_TABLE_FORMAT, getTableName(entityClass), getNewFields(entityClass));

        connection.prepareStatement(query).executeUpdate();
    }

    @Override
    public void doDelete(Class<E> entityClass, String column, String criteria) throws SQLException {
        final String query = String.format(DELETE_TABLE_FORMAT, getTableName(entityClass), column, criteria);

        connection.prepareStatement(query).execute();
    }

    private Set<String> getAllFieldsFromTable(Class<?> entityClass) throws SQLException {
        final Set<String> allFields = new HashSet<>();

        final String query = String.format(GET_TABLE_FIELD_NAME_FORMAT, DB_NAME, getTableName(entityClass));

        final PreparedStatement preparedStatement = connection.prepareStatement(query);

        final ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            allFields.add(resultSet.getString(1));
        }

        return allFields;
    }

    private String getNewFields(Class<?> entityClass) throws SQLException {
        final List<String> result = new ArrayList<>();
        final Set<String> existingTableFields = getAllFieldsFromTable(entityClass);

        Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(Id.class))
                .forEach(field -> {
                    if (!existingTableFields.contains(field.getAnnotation(Column.class).name())) {
                        result.add(String.format(
                                COLUMN_NAME_TYPE_FORMAT,
                                field.getAnnotation(Column.class).name(),
                                getFieldType(field)));
                    }
                });

        return String.join(COMMA, result);
    }

    private String getFieldType(Field field) {
        final Class<?> fieldType = field.getType();
        final String columnType;

        if (fieldType == int.class || fieldType == Integer.class) {
            columnType = INT;
        } else if (fieldType == LocalDate.class) {
            columnType = DATE;
        } else if (fieldType == String.class) {
            columnType = VARCHAR;
        } else {
            throw new ORMException(format(UNSUPPORTED_TYPE_EXCEPTION_FORMAT, fieldType));
        }

        return columnType;
    }

    private String getAllFieldsAndDataTypes(Class<E> entityClass) {
        final Set<String> result = new LinkedHashSet<>();

        Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .forEach(field -> result.add(String.format(
                        COLUMN_NAME_TYPE_FORMAT,
                        field.getAnnotation(Column.class).name(),
                        getFieldType(field))));

        return String.join(COMMA, result);
    }

    private void fillField(E entity, Field field, ResultSet resultSet) throws SQLException, IllegalAccessException {
        field.setAccessible(true);

        final Class<?> clazz = field.getType();

        final String columnName = field.isAnnotationPresent(Column.class)
                ? field.getAnnotation(Column.class).name()
                : field.getName();

        if (clazz == long.class || clazz == Long.class) {
            field.setLong(entity, resultSet.getLong(columnName));
        } else if (clazz == int.class || clazz == Integer.class) {
            field.setInt(entity, resultSet.getInt(columnName));
        } else if (clazz == LocalDate.class) {
            field.set(entity, LocalDate.parse(resultSet.getString(columnName)));
        } else if (clazz == String.class) {
            field.set(entity, resultSet.getString(columnName));
        } else {
            throw new ORMException(format(UNSUPPORTED_TYPE_EXCEPTION_FORMAT, field.getType()));
        }
    }

    private List<E> createEntities(Class<E> table, ResultSet resultSet) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        final List<E> entities = new ArrayList<>();

        E entity = createEntity(table, resultSet);

        while (entity != null) {
            entities.add(entity);
            entity = createEntity(table, resultSet);
        }

        return entities;
    }

    private E createEntity(Class<E> entityType, ResultSet resultSet) throws IllegalAccessException, SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        if (!resultSet.next()) return null;

        final E entity = entityType.getDeclaredConstructor().newInstance();

        final Field[] declaredFields = entity.getClass().getDeclaredFields();

        for (Field field : declaredFields) {
            fillField(entity, field, resultSet);
        }

        return entity;
    }

    private static String getWhereClause(String where) {
        return where == null ? EMPTY_STRING : String.format(WHERE_CLAUSE_FORMAT, where);
    }

    private String getTableName(Class<?> clazz) {
        final Entity annotation = clazz.getAnnotation(Entity.class);

        if (annotation == null) throw new ORMException(MISSING_ENTITY_ANNOTATION_EXCEPTION);

        return annotation.name();
    }

    private List<String> getFieldsWithoutIdentity(E entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> f.getAnnotation(Column.class) != null)
                .map(f -> f.getAnnotation(Column.class).name())
                .toList();
    }

    private List<String> getInsertValues(E entity) throws IllegalAccessException {
        final Field[] declaredFields = entity.getClass().getDeclaredFields();
        final List<String> fieldValues = new ArrayList<>();

        for (Field declaredField : declaredFields) {
            if (declaredField.getAnnotation(Column.class) == null) continue;

            declaredField.setAccessible(true);

            fieldValues.add(String.format(INSERT_VALUE_FORMAT, declaredField.get(entity)));
        }

        return fieldValues;
    }

    private Object getId(E entity) throws IllegalAccessException {
        final Field primaryKey = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException(MISSING_PRIMARY_KEY_EXCEPTION));

        primaryKey.setAccessible(true);

        return primaryKey.get(entity);
    }

    private boolean doInsert(E entity) throws IllegalAccessException, SQLException {
        final String query = format(INSERT_QUERY_FORMAT,
                getTableName(entity.getClass()),
                String.join(COMMA, getFieldsWithoutIdentity(entity)),
                String.join(COMMA, getInsertValues(entity)));

        return connection.prepareStatement(query).execute();
    }

    private boolean doUpdate(E entity) throws IllegalAccessException, SQLException {
        final List<String> fieldsAndValues = new ArrayList<>();

        for (int i = 0; i < getFieldsWithoutIdentity(entity).size(); i++) {
            fieldsAndValues.add(String.format(UPDATE_VALUE_FORMAT,
                    getFieldsWithoutIdentity(entity).get(i),
                    getInsertValues(entity).get(i)));
        }

        final String query = String.format(UPDATE_QUERY_BY_ID_FORMAT,
                getTableName(entity.getClass()),
                String.join(COMMA, fieldsAndValues),
                getId(entity));

        return connection.prepareStatement(query).execute();
    }
}
