package com.wotf.game.classes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Wessel on 14/03/2016.
 */
public class Lobby {

    private String serverIp;
    private List<Session> sessions;

    public String getServerIp() {
        return serverIp;
    }

    public Lobby(String serverIp) {
        this.serverIp = serverIp;
    }

    public Lobby() {
        try {
            this.serverIp = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public List<Session> getSessions() {
        // TODO: Database calls?
        return Collections.unmodifiableList(sessions);
    }

    public Session getSession(String ip) {
        // http://stackoverflow.com/questions/23696317/java-8-find-first-element-by-predicate
        return sessions
                .stream()
                .filter(x -> x.getHost().getIp().equals(ip))
                .findFirst()
                .get();
    }

    public void addSession(Session s) {
        // TODO: Database calls
        sessions.add(s);
    }

    public void removeSession(Session s) {
        // TODO: Database calls
        sessions.remove(s);
    }

    public void joinSession(Session s) {
        // logic for joining sessions
        // TODO: Database calls
    }

}
