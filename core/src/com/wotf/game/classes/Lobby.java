package com.wotf.game.classes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lobby provides the necessary functionality to look for lobbies online
 * and to interact with them
 */
public class Lobby {

    private String serverIp;
    private List<Session> sessions;

    /**
     * Constructor in which the server to look lobbies up from is specified
     * @param serverIp IP address of the server to fetch lobbies from
     */
    public Lobby(String serverIp) {
        this();
        this.serverIp = serverIp;
    }

    /**
     * Main constructor of lobby
     * initializes list of sessions
     */
    public Lobby() {
        sessions = new ArrayList<>();
        
        try {
            this.serverIp = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @return String representing the IP address of the server
     */
    public String getServerIp() {
        return serverIp;
    }
    
    /**
     * @return A list of sessions currently found in the database
     */
    public List<Session> getSessions() {
        // TODO: Database calls?
        return Collections.unmodifiableList(sessions);
    }

    /**
     * Fetches a session based on a given IP address
     * @param ip IP address to match on
     * @return Session object that was found based on the given IP address, can be null
     */
    public Session getSession(String ip) {
        return sessions
                .stream()
                .filter(x -> x.getHost().getIp().equals(ip))
                .findFirst()
                .get();
    }
    
    /**
     * Function that is used to register a new session to the database
     * @param s Session to register
     */
    public void addSession(Session s) {
        // TODO: Database calls
        sessions.add(s);
    }

    /**
     * Function that is used to remove a session from the database
     * @param s Session to remove
     */
    public void removeSession(Session s) {
        // TODO: Database calls
        sessions.remove(s);
    }

    /**
     * Attempts to join the given session
     * if successfully joined, the game will transition to the corresponding Session screen
     * @param s Session to join
     */
    public void joinSession(Session s) {
        // logic for joining sessions
        // TODO: Database calls
    }

}
