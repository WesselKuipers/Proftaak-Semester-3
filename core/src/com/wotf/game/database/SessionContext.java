/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.database;

import com.wotf.game.classes.Session;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dino Spong
 */
public class SessionContext extends EntityContext<Session> {

    /**
     * Get Session by ID
     *
     * @param id ID of the session
     * @return Session of the found ID
     * @throws SQLException if no session was found
     */
    public Session getById(int id) throws SQLException {
        String query = "SELECT * FROM session WHERE ID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(id);
        return getEntityFromRecord(DBCon.executeResultSet(query, parameters));
    }

    /**
     * Get last Session added in the database
     *
     * @return last added Session
     * @throws SQLException
     */
    public Session getLastAddedSession() throws SQLException {
        String query = "SELECT MAX(ID) AS ID FROM session";
        ResultSet result = DBCon.executeResultSet(query);
        int id = 0;
        while (result.next()) {
            id = result.getInt("ID");
        }
        return getById(id);
    }

    /**
     * Get Session by HosID
     *
     * @param id of the host
     * @return Session host by player ID
     * @throws SQLException
     */
    public Session getByHostId(int id) throws SQLException {
        String query = "SELECT * FROM session WHERE HostID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(id);
        return getEntityFromRecord(DBCon.executeResultSet(query, parameters));
    }

    /**
     * Get all Sessions
     *
     * @return list of all sessions
     * @throws SQLException
     */
    public List<Session> getAll() throws SQLException {
        String query = "SELECT * FROM session ORDER BY ID";
        ResultSet res = DBCon.executeResultSet(query);
        List<Session> sessions = new ArrayList<>();
        
        while (res.next()) {
            sessions.add(getById(res.getInt("ID")));
        }
        return sessions;
    }

    /**
     * Insert session the database
     *
     * @param session to add in the database
     * @return true/false if added was succesfull
     */
    public boolean insert(Session session) {
        String query = "INSERT INTO session (HostID, RoomName, MaxPlayersSession) VALUES (?, ?, ?)";
        List<Object> parameters = new ArrayList<>();
        parameters.add(session.getHost().getID());
        parameters.add((session.getRoomName() != null) ? session.getRoomName() : "Roomname is not entered");
        parameters.add(session.getGameSettings().getMaxPlayersSession());

        return DBCon.executeUpdate(query, parameters) >= 1;
    }

    /**
     * Update session in the database
     *
     * @param session to update in database
     * @return true/false if update was succesfull
     */
    public boolean update(Session session) {
        String query = "UPDATE session SET RoomName = ?, MaxPlayersSession = ? WHERE ID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(session.getRoomName());
        parameters.add(session.getGameSettings().getMaxPlayersSession());
        parameters.add(session.getId());

        return DBCon.executeUpdate(query, parameters) >= 1;
    }

    /**
     * Delete session in the database
     *
     * @param session to delete in database
     * @return true/false if update was succesfull
     */
    public boolean delete(Session session) {
        String query = "DELETE FROM session WHERE HostID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(session.getHost().getID());
        return DBCon.executeUpdate(query, parameters) >= 1;
    }

    @Override
    protected Session getEntityFromRecord(ResultSet record) throws SQLException {
        Session session = null;
        while (record.next()) {
            try {
                session = new Session(new PlayerContext().getById(record.getInt("HostID")), record.getString("RoomName"));
                session.getGameSettings().setMaxPlayersSession(record.getInt("MaxPlayersSession"));
            } catch (RemoteException ex) {
                Logger.getLogger(SessionContext.class.getName()).log(Level.SEVERE, null, ex);
            }
            session.setId(record.getInt("ID"));
        }
        return session;
    }

}
