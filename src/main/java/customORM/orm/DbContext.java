package customORM.orm;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface DbContext<E> {
    boolean persist(E entity) throws SQLException, IllegalAccessException;

    Iterable<E> find(Class<E> table) throws SQLException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException;

    Iterable<E> find(Class<E> table, String where) throws SQLException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException;

    E findFirst(Class<E> table) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    E findFirst(Class<E> table, String where) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

    void doCreate(Class<E> entityClass) throws SQLException;

    void doAlter(Class<E> entityClass) throws SQLException;

    void doDelete(Class<E> table, String column, String criteria) throws SQLException;
}
