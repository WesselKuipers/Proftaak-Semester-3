/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.database;

import com.wotf.game.classes.Session;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dino Spong
 */
public class SessionContext extends EntityContext<Session> {

    /**
     * Get Session by ID
     * @param id ID of the session
     * @return Session of the found ID
     * @throws SQLException if no session was found
     */
    public Session GetById(int id) throws SQLException {
        String query = "SELECT * FROM session WHERE ID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(id);
        return GetEntityFromRecord(DBCon.executeResultSet(query, parameters));
    }
    /**
     * Get last Session added in the database
     * @return last added Session
     * @throws SQLException 
     */
    public Session GetLastAddedSession() throws SQLException {
        String query = "SELECT MAX(ID) AS ID FROM session";
        ResultSet result = DBCon.executeResultSet(query);
        int id = 0;
        while(result.next()) {
         id = result.getInt("ID");
        }
        return GetById(id);
    }

    /**
     * Get Session by HosID
     * @param id of the host
     * @return Session host by player ID
     * @throws SQLException 
     */
    public Session GetByHostId(int id) throws SQLException {
        String query = "SELECT * FROM session WHERE HostID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(id);
        return GetEntityFromRecord(DBCon.executeResultSet(query, parameters));
    }
    /**
     * Get all Sessions
     * @return list of all sessions
     * @throws SQLException 
     */
    public List<Session> GetAll() throws SQLException {
        String query = "SELECT * FROM session ORDER BY ID";
        ResultSet res = DBCon.executeResultSet(query);
        List<Session> sessions = new ArrayList<>();

        while (res.next()) {
            sessions.add(GetById(res.getInt("ID")));
        }

        return sessions;
    }
    /**
     * Insert session the database
     * @param session to add in the database
     * @return true/false if added was succesfull
     */
    public boolean Insert(Session session) {
        String query = "INSERT INTO event (HostID, RoomName, MaxPlayersSession) VALUES (?, ?, ?)";
        List<Object> parameters = new ArrayList<>();
        parameters.add(session.getPlayers().get(0).getID());
        parameters.add((session.getRoomName() != null) ? session.getRoomName() : "Roomname is not entered");
        parameters.add(session.getMaxPlayersSession());

        return DBCon.executeUpdate(query, parameters) >= 1;
    }
    /**
     * Update session in the database
     * @param session to update in database
     * @return true/false if update was succesfull
     */
    public boolean Update(Session session) {
        String query = "UPDATE session SET RoomName = ?, MaxPlayersSession = ? WHERE ID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(session.getRoomName());
        parameters.add(session.getMaxPlayersSession());
        parameters.add(session.getID());
        
        return DBCon.executeUpdate(query, parameters) >= 1;
    }
    /**
     * Delete session in the database
     * @param session to delete in database
     * @return true/false if update was succesfull
     */
    public boolean Delete(Session session) {
        String query = "DELETE FROM session WHERE ID = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(session.getID());
        
        return DBCon.executeUpdate(query, parameters) >= 1;
    }

    @Override
    protected Session GetEntityFromRecord(ResultSet record) throws SQLException {
        Session session = null;
        while(record.next()){        
        session = new Session(new PlayerContext().GetById(record.getInt("HostID")), record.getString("RoomName"), record.getInt("MaxPlayersSession"));
        session.setID(record.getInt("ID"));
        }
        return session;
    }

}
