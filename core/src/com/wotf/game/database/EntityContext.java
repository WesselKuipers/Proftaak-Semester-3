package com.wotf.game.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Abstract class used for classes that derive from a database table
 * @param <T> Type of object derviced from database
 */
public abstract class EntityContext<T>
{
    /**
     * Attempts to derive an object from a DB record
     * @param record record to derive object from
     * @return returns object of type <T>
     * @throws SQLException Thrown when the record is invalid
     */
    protected abstract T getEntityFromRecord(ResultSet record) throws SQLException;
}
