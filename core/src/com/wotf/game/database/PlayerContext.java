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
    public Player getById(int id) throws SQLException {
        String query = "SELECT * FROM player WHERE ID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(id);
        return getEntityFromRecord(DBCon.executeResultSet(query, parameters));
    }

    /**
     * Get last Player added in the database
     *
     * @return last added Player
     * @throws SQLException
     */
    public Player getLastAddedPlayer() throws SQLException {
        String query = "SELECT MAX(ID) FROM player";
        return getEntityFromRecord(DBCon.executeResultSet(query));
    }

    /**
     * Get player by IP address
     *
     * @param ip of the player
     * @return player found with that ip address
     * @throws SQLException
     */
    public Player getByIP(String ip) throws SQLException {
        String query = "SELECT * FROM player WHERE IPAddress = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(ip);
        return getEntityFromRecord(DBCon.executeResultSet(query, parameters));
    }

    /**
     * Get all players
     *
     * @return list of all players
     * @throws SQLException
     */
    public List<Player> getAll() throws SQLException {
        String query = "SELECT * FROM player ORDER BY ID";
        ResultSet res = DBCon.executeResultSet(query);
        List<Player> players = new ArrayList<>();

        while (res.next()) {
            players.add(getById(res.getInt("ID")));
        }

        return players;
    }

    /**
     * Insert player the database
     *
     * @param player to add in the database
     * @return true/false if added was succesfull
     */
    public boolean insert(Player player) {
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
    public boolean update(Player player) {
        String query = "UPDATE session SET IngameName = ? WHERE ID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(player.getName());
        parameters.add(player.getId());

        return DBCon.executeUpdate(query, parameters) >= 1;
    }

    /**
     * Delete player in the database
     *
     * @param player to delete in database
     * @return true/false if update was succesfull
     */
    public boolean delete(Player player) {
       String query = "DELETE FROM player WHERE ID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(player.getId());

        return DBCon.executeUpdate(query, parameters) >= 1;
    }

    @Override
    protected Player getEntityFromRecord(ResultSet record) throws SQLException {
        Player player = new Player(record.getString("IPAddress"), record.getString("IngameName"));
        player.setId(record.getInt("ID"));
        return player;
    }

}
