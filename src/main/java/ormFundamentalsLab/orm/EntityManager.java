package ormFundamentalsLab.orm;

import ormFundamentalsLab.orm.annotations.Column;
import ormFundamentalsLab.orm.annotations.Entity;
import ormFundamentalsLab.orm.annotations.Id;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static java.lang.String.join;

public class EntityManager<E> implements DbContext<E> {
    private final Connection connection;

    private final static String GET_TABLE_FORMAT = "select * from %s %s";
    private final static String GET_FIRST_TABLE_ROW_FORMAT = "select * from %s %s limit 1";
    private final static String INSERT_QUERY_FORMAT = "insert into %s (%s) values (%s)";
    private final static String UPDATE_QUERY_BY_ID_FORMAT = "update %s set %s where id = %s";

    private final static String MISSING_ENTITY_ANNOTATION_EXCEPTION = "Provided class does not have Entity annotation";
    private final static String MISSING_PRIMARY_KEY_EXCEPTION = "Entity does not primary key";
    private final static String UNSUPPORTED_TYPE_EXCEPTION_FORMAT = "Unsupported type: %s";

    private final static String WHERE_KEYWORD = "WHERE";
    private final static String COMMA = ",";
    private final static String SINGLE_QUOTE = "'";
    private final static String EMPTY_STRING = "";
    private final static String EMPTY_SPACE = " ";
    private final static String EQUAL_SIGN = "=";


    public EntityManager(Connection connection) {
        this.connection = connection;
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
        return where == null ? EMPTY_STRING : WHERE_KEYWORD + EMPTY_SPACE + where;
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

            fieldValues.add(SINGLE_QUOTE + declaredField.get(entity) + SINGLE_QUOTE);
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
                join(COMMA, getFieldsWithoutIdentity(entity)),
                join(COMMA, getInsertValues(entity)));

        return connection.prepareStatement(query).execute();
    }

    private boolean doUpdate(E entity) throws IllegalAccessException, SQLException {
        final List<String> fieldsAndValues = new ArrayList<>();

        for (int i = 0; i < getFieldsWithoutIdentity(entity).size(); i++) {
            fieldsAndValues.add(getFieldsWithoutIdentity(entity).get(i) + EQUAL_SIGN + getInsertValues(entity).get(i));
        }

        final String query = format(UPDATE_QUERY_BY_ID_FORMAT,
                getTableName(entity.getClass()),
                join(COMMA, fieldsAndValues),
                getId(entity));

        return connection.prepareStatement(query).execute();
    }
}
