/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.database;

import com.wotf.game.classes.Player;
import com.wotf.game.classes.Session;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DinoS
 */
public class SessionPlayerContext {
    /**
     * Get all player from session
     * @param session to get players of
     * @return List of players in a session
     * @throws SQLException 
     */
    public ArrayList<Player> getPlayersFromSession(Session session) throws SQLException {
        PlayerContext _context = new PlayerContext();

        String query = "SELECT * FROM session_participant WHERE SessionID = ? ORDER BY PlayerID";
        ArrayList<Object> parameters = new ArrayList<>();
        parameters.add(session.getID());

        ResultSet res = DBCon.executeResultSet(query, parameters);
        ArrayList<Player> players = new ArrayList<>();

        while (res.next()) {
            players.add(_context.getById(res.getInt("PlayerID")));
        }

        return players;
    }
    /**
     * Insert new session participant to database
     * @param player to insert
     * @param session to insert
     * @return true if insert / false if not
     */
    public boolean insert(Player player, Session session) {
        String query = "INSERT INTO session_participant (PlayerID, SessionID) VALUES (?, ?)";
        List<Object> parameters = new ArrayList<>();
        parameters.add(player.getID());
        parameters.add(session.getID());

        return DBCon.executeUpdate(query, parameters) >= 1;
    }
    /**
     * Delete player from session
     * @param player to remove
     * @param session to remove player of
     * @return true if delete / false if not
     */
    public boolean deletePlayerFromSession(Player player, Session session) {
        String query = "DELETE FROM session_participant WHERE SessionID = ? AND PlayerID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(session.getID());
        parameters.add(player.getID());

        return DBCon.executeUpdate(query, parameters) >= 1;
    }

    /**
     * Delete Session and player from database
     * @param session to remove with players
     * @return true if delete / false if not
     */
    public boolean deleteSessionAndPlayers(Session session) {
        String query = "DELETE FROM session_participant WHERE SessionID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(session.getID());

        return DBCon.executeUpdate(query, parameters) >= 1;
    }
}
