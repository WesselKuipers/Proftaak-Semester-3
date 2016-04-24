/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.database;

import com.wotf.game.classes.Player;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dino Spong
 */
public class PlayerContext extends EntityContext<Player> {

    /**
     * Get player by ID
     *
     * @param id of the player
     * @return player with ID found in the database
     * @throws SQLException
     */
    public Player GetById(int id) throws SQLException {
        String query = "SELECT * FROM player WHERE ID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(id);
        return GetEntityFromRecord(DBCon.executeResultSet(query, parameters));
    }

    /**
     * Get last Player added in the database
     *
     * @return last added Player
     * @throws SQLException
     */
    public Player GetLastAddedPlayer() throws SQLException {
        String query = "SELECT MAX(ID) AS ID FROM player";
        ResultSet result = DBCon.executeResultSet(query);
        int id = 0;
        while (result.next()) {
            id = result.getInt("ID");
        }
        return GetById(id);
    }

    /**
     * Get player by IP address
     *
     * @param ip of the player
     * @return player found with that ip address
     * @throws SQLException
     */
    public Player GetByIP(String ip) throws SQLException {
        String query = "SELECT * FROM player WHERE IPAddress = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(ip);
        return GetEntityFromRecord(DBCon.executeResultSet(query, parameters));
    }

    /**
     * Get all players
     *
     * @return list of all players
     * @throws SQLException
     */
    public List<Player> GetAll() throws SQLException {
        String query = "SELECT * FROM player ORDER BY ID";
        ResultSet res = DBCon.executeResultSet(query);
        List<Player> players = new ArrayList<>();

        while (res.next()) {
            players.add(GetById(res.getInt("ID")));
        }

        return players;
    }

    /**
     * Insert player the database
     *
     * @param player to add in the database
     * @return true/false if added was succesfull
     */
    public boolean Insert(Player player) {
        String query = "INSERT INTO player (IngameName, IPAddress) VALUES (?, ?)";
        List<Object> parameters = new ArrayList<>();
        parameters.add(player.getName());
        parameters.add(player.getIp());

        return DBCon.executeUpdate(query, parameters) >= 1;
    }

    /**
     * Update player in the database
     *
     * @param player to update in database
     * @return true/false if update was succesfull
     */
    public boolean Update(Player player) {
        String query = "UPDATE session SET IngameName = ? WHERE ID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(player.getName());
        parameters.add(player.getID());

        return DBCon.executeUpdate(query, parameters) >= 1;
    }

    /**
     * Delete player in the database
     *
     * @param player to delete in database
     * @return true/false if update was succesfull
     */
    public boolean Delete(Player player) {
        String query = "DELETE FROM player WHERE ID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(player.getID());

        return DBCon.executeUpdate(query, parameters) >= 1;
    }

    @Override
    protected Player GetEntityFromRecord(ResultSet record) throws SQLException {
        Player player = null;
        while (record.next()) {
            player = new Player(record.getString("IPAddress"), record.getString("IngameName"));
            player.setID(record.getInt("ID"));
        }
        return player;
    }

}
