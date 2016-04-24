/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Dino Spong
 */
public abstract class EntityContext<T>
{
    protected abstract T getEntityFromRecord(ResultSet record) throws SQLException ;
}
