package com.wotf.game.classes;

import com.wotf.gui.view.ISessionSettings;
import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.RemotePublisher;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Session contains the data structure containing the list of teams, players and
 * gameSettings
 */
public class Session extends UnicastRemoteObject implements ISessionSettings {

    private GameSettings gameSettings;
    private Player host;
    private List<Player> players;
    private Registry registry;
    private RemotePublisher publisher;
    private String roomName;
    private int ID;
    private int maxPlayersSession;

    public Session() throws RemoteException {
    }

    ;

    /**
     * Initializes a session using the information of the hosting player
     *
     * @param host Player indicating which player is the host
     */
    public Session(Player host, String roomName, int maxPlayersSession) throws RemoteException {
        this();
        this.gameSettings = new GameSettings();
        this.host = host;
        this.players = new ArrayList<>();
        players.add(host);
        this.maxPlayersSession = maxPlayersSession;
        this.roomName = roomName;
        publisher = new RemotePublisher();
        publisher.registerProperty("sessionsettingsprop");
        publisher.registerProperty("startgameprop");
    }

    public void createNewRegistry() throws RemoteException {
        registry = LocateRegistry.createRegistry(5555);
        registry.rebind("SessionSettings", this);
    }

    public void removeRegistry() throws NoSuchObjectException{
        UnicastRemoteObject.unexportObject(registry, true);
    }
    
    /**
     * Constructor without any graphics Made for the unit testing.
     */
    public Session(Player host, boolean any) throws RemoteException {
        this.gameSettings = new GameSettings(true);
        this.host = host;
        this.players = new ArrayList<>();
    }

    @Override
    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public void setGameSettings(GameSettings settings) throws RemoteException {
        GameSettings oldsettings = this.gameSettings;
        this.gameSettings = settings;
        publisher.inform("sessionsettingsprop", oldsettings, this.gameSettings);
    }

    /**
     * @return The host of this session
     */
    public Player getHost() {
        return host;
    }

    /**
     * @return List of players that joined this session
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Adds a player to the list of players
     *
     * @param p Player to add
     */
    public void addPlayer(Player p) {
        players.add(p);
    }

    /**
     * Removes a player from the list of players Will kick the player from the
     * lobby afterwards
     *
     * @param p Player to remove
     */
    public void removePlayer(Player p) {
        players.remove(p);
        // TODO: logic for kicking players
    }

    public String getRoomName() {
        return roomName;
    }
    
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getMaxPlayersSession() {
        return maxPlayersSession;
    }
    /**
     * Initializes the game screen
     */
    public void startGame() throws RemoteException {
        // TODO: handle code for creating game object
    }

    @Override
    public void subscribeRemoteListener(IRemotePropertyListener listener, String property) throws RemoteException {
        publisher.subscribeRemoteListener(listener, property);
    }

    @Override
    public void unsubscribeRemoteListener(IRemotePropertyListener listener, String property) throws RemoteException {
        publisher.unsubscribeRemoteListener(listener, property);
    }

    @Override
    public String toString() {
        return host.getName() + " " + host.getIp();
    }

}
